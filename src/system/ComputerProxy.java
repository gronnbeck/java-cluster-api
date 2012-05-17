package system;

import api.Computer;
import api.ContinuationTask;
import api.Result;
import api.Shared;
import api.Space;
import api.Task;
//import com.sun.tools.doclets.internal.toolkit.util.SourceToHTMLConverter;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;

public class ComputerProxy extends UnicastRemoteObject implements Runnable, Computer, Space {

    protected Computer computer;
    protected Space space;
    protected HashMap<String, Task> mapTask;
    /**
     * Creates a Proxy for handling Computers
     * @param computer The computer you wish to proxy
     * @param space On what space the tasks and results are published
     * @throws java.rmi.RemoteException
     */
    public ComputerProxy(Computer computer, Space space) throws RemoteException {
        this.space = space;
        this.computer = computer;
        this.mapTask = new HashMap<String, Task>();
        this.computer.setSpace(this);


        Thread t = new Thread(this);
        t.start();
    }

    protected synchronized void addTask(Task task) {
    	mapTask.put(task.getTaskIdentifier(), task);
    }

	@Override
    public void execute(Task task) throws RemoteException {
       computer.execute(task);
    }

    @Override
    public void stop() throws RemoteException {
        computer.stop();
    }
    
	@Override
	public Object getShared() throws RemoteException {
		return computer.getShared();
	}

	@Override
	public void setShared(Shared shared) throws RemoteException {
        // This is ugly:P isNewer does stop the on and on updating of shared, luckily..
		computer.setShared(shared);
        space.setShared(shared);
		
	}

    private synchronized void removeTaskFromMapTask(Result result) {
        mapTask.remove(result.getTaskIdentifier());
    }

    private synchronized void addCachedTasks(ContinuationTask ct) {
        for (Task t : ct.getTasks()) {
            if (t.getCached()) {
                addTask(t);
            }
        }
    }
    @Override
    public void putResult(Result result) throws RemoteException, InterruptedException {
        removeTaskFromMapTask(result);

        // If a task is cached. The ComputerProxy must be aware of it.
        if (result instanceof ContinuationResult) {
            ContinuationTask ct = (ContinuationTask) result.getTaskReturnValue();
            addCachedTasks(ct);
        }

        space.putResult(result);

    }

    @Override
    public Space getSpace() {
        return space;
    }

    private synchronized  void addTasksToSpace() throws InterruptedException, RemoteException {
        for (Task  t : mapTask.values()) {
            t.setComputer(null);
            space.put(t);
        }
        System.out.println("Returned tasks (" + mapTask.size() + ") to space");
        mapTask = new HashMap<String, Task>();

    }


    /**
     * Start the ComputerProxy process
     * It waits on a client to publish a task. When a task is published
     * it tires to assign the task to its corresponding Computer.
     * If the computer returns a result it sends the result back to the space.
     * However, if the Computer raises a RemoteException this proxy puts the
     * task back into the space, and deregisters it self.
     */
    public void run() {
        System.out.println("ComputerProxy running");
        do {
            try {
                Task task = space.takeTask();
                addTask(task);
                try {
                	execute(task);
                } catch (RemoteException e) {
                    try {
                    	//Computer crashed, put all the tasks that were currently running back into space!
                    	addTasksToSpace();
                        space.put(task);
                        space.deregister(this);
                        System.out.println("A Computer crashed. Tasks that were running are now back in the space.");
                        return;          // exit thread . The proxy is no longer needed

                    } catch (RemoteException ignore) { }
                }
            }
            catch (RemoteException ignore) { }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
        } while(true);
    }
	
    @Override
    public void put(Task task) throws RuntimeException, RemoteException, InterruptedException {
        throw new IllegalAccessError("Not implemented");
    }

    @Override
    public Result take() throws RemoteException, InterruptedException {
    	throw new IllegalAccessError("Not implemented");
    }

    @Override
    public void register(Computer computer) throws RemoteException {
    	throw new IllegalAccessError("Not implemented");
    }

    @Override
    public void deregister(Computer computer) throws RemoteException {
    	throw new IllegalAccessError("Not implemented");
    }

    @Override
    public Task takeTask() throws RemoteException, InterruptedException {
    	throw new IllegalAccessError("Not implemented");
    }

    @Override
    public void registerContin(ContinuationTask continuation) throws RemoteException {
    	throw new IllegalAccessError("Not implemented");
    }

	@Override
	public void setSpace(Space space) {
		throw new IllegalAccessError("Not implemented");
		
	}

	@Override
	public Task takeSimpleTask() {
		throw new IllegalAccessError("This shouldnt be accessed!");
	}


}
