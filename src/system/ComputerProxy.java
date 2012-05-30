package system;

import api.*;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;

public class ComputerProxy extends UnicastRemoteObject implements Runnable, Computer {

    // TODO One should be able to config these
    public int WANT_TO_STEAL_SIZE = 2;
    public int STEAL_ALLOWED_SIZE = 3;
    private int TASK_LIST_MAX_SIZE = STEAL_ALLOWED_SIZE + 5;

    private Computer computer;
    protected Space space;
    private Task cached;
    private ArrayList<Task> tasks;
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
        this.tasks = new ArrayList<Task>();    // FIX THESE TO BE THREADSAFE
        this.running = true;
        this.otherComputers = new ArrayList<Computer>();

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
        return tasks.size() <= WANT_TO_STEAL_SIZE;
    }

    @Override
    public synchronized Task stealTask() throws RemoteException {
        return tasks.remove(0);
    }

    @Override
    public synchronized void addTask(Task task) throws RemoteException {
        this.tasks.add(task);
    }

    public synchronized boolean canSteal() {
        return tasks.size() >= STEAL_ALLOWED_SIZE;
    }

    @Override
    public List<Task> getTaskQ() throws RemoteException {
        return (List<Task>) tasks.clone();
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
        // TODO add the process of handling prefetched tasks as well
        giveTaskBack2Space(task);
        for (Task t : tasks) {
            giveTaskBack2Space(t);
        }
        deregisterComputer();
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

    private void lookForCachedResult(Result result) {
        if (result instanceof ContinuationResult) {
            // One should not be able to access the ContinuationTask in this way. Its error-prone.
            // TODO find a better way to retrieve tasks marked as cached
            ContinuationTask continuationTask = (ContinuationTask) result.getTaskReturnValue();
            ArrayList<Task> tasks = continuationTask.getTasks();
            Task task;
            if ((task = tasks.get(0)).getCached() && this.cached == null) {
                this.cached = task;
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
            Result result = null;
            // if computer has a cached task execute that one. If not get one from space
            try {
                boolean hasCached;
                try {
                    hasCached = hasCached();     // TODO we may not need this to be asynchronous.
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
                    Task task;
                    if (tasks.size() <= 0) {
                        task = space.takeTask();
                    }
                    else {
                        task = tasks.remove(0);
                    }

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
            } catch (RemoteException ignore){}

            lookForCachedResult(result);
            queueTasks(result);
            putResultToSpace(result);
        } while(running);
    }

    private void queueTasks(Result result) {
        if (result instanceof ContinuationResult) {
            ContinuationResult cr = (ContinuationResult) result;
            for (Task task : cr.getTaskReturnValue().getTasks()) {
                if (tasks.size() > TASK_LIST_MAX_SIZE) break;
                if (task.getCached()) continue;
                task.setCached(true);                             // mark as cached so Space does not Q them
                tasks.add(task);
            }
        }
    }


    @Override
    public Space getSpace() throws RemoteException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void setSpace(Space space) throws RemoteException {
        //To change body of implemented methods use File | Settings | File Templates.
    }


}
