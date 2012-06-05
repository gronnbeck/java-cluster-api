package system;

import api.*;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;

public class ComputerProxy extends UnicastRemoteObject implements Runnable, Computer {

	private static final long serialVersionUID = -8341261141794701967L;
	// TODO One should be able to config these
    public int HIGH_WATERMARK;
    public int LOW_WATERMARK;
//    private int TASK_LIST_MAX_SIZE = HIGH_WATERMARK + 5;

    private Computer computer;
    protected Space space;
    private Task<?> cached;
    private BlockingQueue<Task<?>> tasks;
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
        this.tasks = new PriorityBlockingQueue<Task<?>>(11, TaskComparator.getSingleton());
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
        System.out.println("Computer disconnected");
        otherComputers.remove(cp);

    }

    @Override
    public synchronized boolean want2Steal() {
        return tasks.size() <= LOW_WATERMARK;
    }

    public synchronized boolean canSteal() {
        return tasks.size() > HIGH_WATERMARK;
    }

    @Override
    public synchronized Task<?> stealTask() throws RemoteException, InterruptedException {
        return tasks.poll();
    }

    @Override
    public synchronized void addTask(Task<?> task) throws RemoteException {
        this.tasks.add(task);
    }


    @Override
    public int getTaskQSize() throws RemoteException {
        return tasks.size();
    }

    @Override
    public List<Computer> getComputers() throws RemoteException {
    	return new ArrayList<Computer>(otherComputers);
    }

    @Override
    public Result<?> execute(Task<?> task) throws RemoteException {
        return computer.execute(task);
    }

    @Override
    public void stop() throws RemoteException {
        computer.stop();
    }

	@Override
	public Shared<?> getShared(String jobId) throws RemoteException {
		return computer.getShared(jobId);
	}

	@Override
	public void setShared(Shared<?> shared) throws RemoteException {
		computer.setShared(shared);
		
	}

    @Override
    public boolean hasCached() throws RemoteException {
        return cached!=null;
    }

    @Override
    public Result<?> executeCachedTask() throws RemoteException {
        return computer.executeCachedTask();
    }

    private void giveTaskBack2Space(Task<?> task) {
        try {
            task.setCached(false);
            space.put(task);
        } catch (RemoteException ignore) {
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    private synchronized void handleFaultyComputer(Task<?> task)  {
        giveTaskBack2Space(task);
        for (Task<?> t : tasks) {
            giveTaskBack2Space(t);
        }
        deregisterComputer();
    }

    private void deregisterComputer() {
        try {
            space.deregister(this);
        } catch (RemoteException ignore) { }
    }

    private void putResultToSpace(Result<?> result) {
        try {
            space.putResult(result);
        }
        catch (RemoteException ignore) {}
        catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void lookForCachedTasks(Result<?> result) {
        if (result instanceof ContinuationResult) {
            ContinuationTask continuationTask = (ContinuationTask) result.getTaskReturnValue();
            List<Task<?>> cachedTask = continuationTask.getCachedTasks();
            Task<?> task;
            if ((task = cachedTask.get(0)).getCached() && this.cached == null) {
                this.cached = task;
            }
            else if (this.cached != null) {
                System.out.println("[ComputerProxy - lookForCachedTasks] This should not happen");
            }
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
            Result<?> result = null;
            // if computer has a cached task execute that one. If not get one from space
            try {
                boolean hasCached;
                try {
                    hasCached = hasCached();     // TODO we may not need this to be asynchronous. [DONE]
                } catch (RemoteException e) {
                    System.out.println("A computer crashed on checking if it had a cached task");
                    if (cached != null) {
                        handleFaultyComputer(cached);
                    }
                    cached = null;
                    running = false;
                    return;
                }
                if (hasCached && cached != null) {
                        try {
                            result = computer.executeCachedTask();
                            if (result == null) continue;   // the cached task has already been executed. This should never happen since
                                                            // Computer should be single threaded (Intended design)
                        } catch (RemoteException e) {
                            System.out.println("A computer has crashed. Putting the cached task back to space");
                            handleFaultyComputer(cached);
                            running = false;
                            return;
                        }
                        cached = null;
                    }
                else {
                    Task<?> task = tasks.take();
                    try {
                        result = execute(task);
                    } catch (RemoteException e) {
                        System.out.println("A computer has crashed. Putting the currently running task back to space");
                        if (cached != null) {
                            giveTaskBack2Space(cached);
                        }
                        handleFaultyComputer(task);
                        running = false;
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
        } while(running);
    }

    private void queueTasks(Result<?> result) {
        if (result instanceof ContinuationResult) {
            ContinuationResult cr = (ContinuationResult) result;
            for (Task<?> task : cr.getTaskReturnValue().getTasks()) {
                // if (tasks.size() > TASK_LIST_MAX_SIZE) break;
                if (task.getCached()) continue;
                task.setCached(true);                             // mark as cached so Space does not Q them
                tasks.add(task);
            }
        }
    }


    @Override
    public void propagateTaskEvent(TaskEvent<?> taskEvent) throws RemoteException {
        System.out.println("This method is never called, why would your propagate an event to a task?");
        //computer.propagateTaskEvent(taskEvent);
    }
}
