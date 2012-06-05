package system;

import api.*;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.PriorityBlockingQueue;

public class ComputerProxy extends UnicastRemoteObject implements Runnable, Computer {

    // TODO One should be able to config these
    public int HIGH_WATERMARK;
    public int LOW_WATERMARK;

    private Computer computer;
    protected Space space;
    private Task cached;
    private BlockingQueue<Task> tasks;
    private boolean running;
    private ArrayList<Computer> otherComputers;


    /**
     * Creates a Proxy for handling Computers
     * @param computer The computer you wish to computer
     * @param space On what space the tasks and results are published
     */
    public ComputerProxy(Computer computer, Space space) throws RemoteException {
        super();
        this.computer = computer;
        this.space = space;
        this.cached = null;
        this.tasks = new PriorityBlockingQueue<Task>(11, TaskComparator.getSingleton());
        this.running = true;
        this.otherComputers = new ArrayList<Computer>();
        this.LOW_WATERMARK = 0;
        this.HIGH_WATERMARK = 10;

        start();
    }

    public void start() {
        Thread cpThread = new Thread(this);
        cpThread.start();
    }

    @Override
    public synchronized void registerComputer(Computer cp) throws RemoteException {
        //System.out.println("Computer "+ Integer.toHexString(System.identityHashCode(cp)) +" registered to " + Integer.toHexString(System.identityHashCode(this)));
        if (cp == this) return;
        otherComputers.add(cp);
    }

    @Override
    public synchronized void deregisterComputer(Computer cp) throws RemoteException {
        otherComputers.remove(cp);

    }

    @Override
    public  boolean want2Steal() {
        return tasks.size() <= LOW_WATERMARK;
    }

    public  boolean canSteal() {
        return tasks.size() > HIGH_WATERMARK;
    }

    @Override
    public Task stealTask() throws RemoteException, InterruptedException {
        System.out.println("task has been stolen");
        return tasks.poll();
    }

    @Override
    public void addTask(Task task) throws RemoteException {
        try {
            this.tasks.put(task);
        } catch (InterruptedException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }


    @Override
    public int getTaskQSize() throws RemoteException {
        return tasks.size();
    }

    @Override
    public List<Computer> getComputers() throws RemoteException {
        return (List<Computer>) otherComputers.clone();
    }

    @Override
    public Result execute(Task task) throws RemoteException {
        return computer.execute(task);
    }

    @Override
    public void stop() throws RemoteException {
        computer.stop();
    }

	@Override
	public Shared getShared(String jobId) throws RemoteException {
		return computer.getShared(jobId);
	}

	@Override
	public void setShared(Shared shared) throws RemoteException {
		computer.setShared(shared);
		
	}

    @Override
    public boolean hasCached() throws RemoteException {
        return cached!=null;
    }

    @Override
    public Result executeCachedTask() throws RemoteException {
        return computer.executeCachedTask();
    }

    private void giveTaskBack2Space(Task task) {
        try {
            task.setCached(false);
            space.put(task);
        } catch (RemoteException ignore) {
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    private void handleFaultyComputer(Task task)  {
        deregisterComputer();
        if (task != null) giveTaskBack2Space(task);
        for (Task t : tasks) {
            giveTaskBack2Space(t);
        }
    }

    private void deregisterComputer() {
        try {
            space.deregister(this);
        } catch (RemoteException ignore) { }
    }

    private void putResultToSpace(Result result) {
        try {
            space.putResult(result);
        }
        catch (RemoteException ignore) {}
        catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    /**
     * Start the ComputerProxy process
     * It waits on a client to publish a task. When a task is published
     * it tires to assign the task to its corresponding Computer.
     * If the computer returns a result it sends the result back to the space.
     * However, if the Computer raises a RemoteException this computer puts the
     * task back into the space, and deregisters it self.
     */
    public void run() {
        // TODO Clean!
        // I think this method has become too complex. Is there a way we can simply it?
        System.out.println("ComputerProxy running");
        // Start work stealing
        WorkStealer workStealer = new WorkStealer(this);
        Thread wsThread = new Thread(workStealer);
        wsThread.start();


        do {
            Result result = null;
            // if computer has a cached task execute that one. If not get one from space
            try {
                if (_hasCached()) {
                    try {
                        System.out.println("exec cached");
                        result = computer.executeCachedTask();
                        if (result == null) continue;   // the cached task has already been executed. This should never happen since
                                                        // Computer should be single threaded (Intended design)
                    } catch (RemoteException e) {
                        System.out.println("TRYING");
                        workStealer.stop();
                        System.out.println("TRYING2");
                        handleFaultyComputer(cached);
                        System.out.println("A computer has crashed. Putting the cached task back to space");
                        return;
                    }
                    cached = null;
                    }
                else {
                    Task task = tasks.take();
                    /* try {
                        if (task == null) task = space.takeTask();
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }  */
                    try {
                        System.out.println("exec task");
                        result = execute(task);
                    } catch (RemoteException e) {
                        System.out.println("TRYING");
                        workStealer.stop();
                        System.out.println("TRYING2");
                        if (cached != null) {
                            giveTaskBack2Space(cached);
                        }
                        handleFaultyComputer(task);
                        System.out.println("A computer has crashed. Putting the currently running task back to space");
                        return;          // exit thread . The computer is no longer needed
                    }
                }
            }
            catch (InterruptedException e) {
                e.printStackTrace();            // don't know how we shall handle this one, yet...
            }

            lookForCachedTasks(result);
            queueTasks(result);
            putResultToSpace(result);

        } while(true);
    }

    private boolean _hasCached() {
        boolean hasCached = false;
        try {
            hasCached = hasCached();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return hasCached;
    }

    private void lookForCachedTasks(Result result) {
        if (result instanceof ContinuationResult) {
            ContinuationTask continuationTask = (ContinuationTask) result.getTaskReturnValue();
            List<Task> cachedTask = continuationTask.getCachedTasks();
            Task task;
            if ((task = cachedTask.get(0)).getCached() && this.cached == null) {
                this.cached = task;
            }
            else if (this.cached != null) {
                System.out.println("[ComputerProxy - lookForCachedTasks] This should not happen");
            }
        }
    }


    private void queueTasks(Result result) {
        if (result instanceof ContinuationResult) {
            ContinuationResult cr = (ContinuationResult) result;
            for (Task task : cr.getTaskReturnValue().getTasks()) {
                // if (tasks.size() > TASK_LIST_MAX_SIZE) break;
                if (task.getCached()) continue;
                task.setCached(true);                             // mark as cached so Space does not Q them
                try {
                    tasks.put(task);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    @Override
    public void propagateTaskEvent(TaskEvent taskEvent) throws RemoteException {
        System.out.println("This method is never called, why would your propagate an event to a task?");
        //computer.propagateTaskEvent(taskEvent);
    }
}
