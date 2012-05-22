package system;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.ExportException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;

import api.*;

public class SpaceImpl extends UnicastRemoteObject implements Space, Runnable {

	private static final long serialVersionUID = 1L;
	private BlockingQueue<Result<?>> resultQue;
	private BlockingQueue<Task<?>> taskQue;
	private BlockingQueue<Task<?>> simpleTaskQue;
    private HashMap<Object,ContinuationTask> mapContin;
    private ArrayList<Computer> computers;
    private Shared<?> shared;
//    private Runtime runtime;

    public SpaceImpl() throws RemoteException {
        super();
        computers = new ArrayList<Computer>();
        taskQue = new PriorityBlockingQueue<Task<?>>(11, new TaskComparator());
        simpleTaskQue = new LinkedBlockingQueue<Task<?>>();
        resultQue = new LinkedBlockingQueue<Result<?>>();
        mapContin = new HashMap<Object, ContinuationTask>();

        
//		TODO: Make the shutdown hooks work to shutdown gracefully
//        runtime = Runtime.getRuntime();
//        runtime.addShutdownHook(new ShutdownProcedure(this));
        this.run();
    }

    @Override
    public void put(Task<?> task) throws RuntimeException, RemoteException, InterruptedException {
        if (task.isSimple()) {	
			simpleTaskQue.put(task);
			return;
		}
    	taskQue.put(task);
    }

    @Override
    public Result<?> take() throws RemoteException, InterruptedException {
        return resultQue.take();
    }

    @Override
    public Task<?> takeTask() throws RemoteException, InterruptedException {
        return taskQue.take();
    }
  
    @Override
    public Task<?> takeSimpleTask() throws InterruptedException{
    	return simpleTaskQue.take();
    }
    
    
    @Override
    public synchronized void stop() throws RemoteException {
    	
    	System.out.println("Stop() invoked on the space..");
    	
        for (Computer computer : computers) {
            computer.stop();
        }
        System.exit(0);
    }

    @Override
    public synchronized void register(Computer computer) throws RemoteException {
        Computer proxy = new ComputerProxy(computer, this);
        if(this.shared != null) proxy.setShared(this.shared);
        computers.add(proxy);
        System.out.println("A computer has registered it self");
    }
    
    private synchronized void registerSpaceComputer(Computer computer) throws RemoteException{
    	if(this.shared != null) computer.setShared(this.shared);
    	computers.add(computer);
    	System.out.println("The space computer is successfully registered!");
    	
    }

    @Override
    public synchronized void deregister(Computer computer) throws RemoteException {
        computers.remove(computer); // a cproxy here as well
    }

    @Override
    public synchronized void putResult(Result<?> result) throws RemoteException, InterruptedException {
    	if (result == null) return;
        if (result instanceof ContinuationResult) {
            ContinuationTask continuationTask = (ContinuationTask) result.getTaskReturnValue();
            registerContin(continuationTask);
            return;
        }

        String id = result.getTaskIdentifier();
        if (mapContin.containsKey(id)) {
            ContinuationTask contin = mapContin.get(id);
            contin.ready(result);
            mapContin.remove(id);
            if (contin.isReady()) {
            	if (contin.isSimple()) {
					simpleTaskQue.put(contin);
				}else{
					taskQue.put(contin);
				}
                
            }
        }
        // if not it is probably the end result (as I see it now)
        // in the case of fib. It should just be 1 number left
        else {
            resultQue.put(result); // Don't know if this is the correct way to do it... as for now I have it like that
        }
    }

    @Override
    public synchronized void registerContin(ContinuationTask continuation) throws RemoteException {

        for (Task<?> task : continuation.getTasks()) {
            try {
                // Ignore cached tasks.
            	if (!task.getCached()) {
                    /* The caching fails here.... why? */
            		taskQue.put(task);
				}

            	if (task.isSimple()){
            		simpleTaskQue.put(task);
            	}
                
                mapContin.put(task.getTaskIdentifier(), continuation);
                //SSystem.out.println("    " + task.getTaskIdentifier());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    
    

    private synchronized boolean checkAndSetSharedThreadSafe(Shared shared) throws RemoteException {
        if (shared.isNewerThan(this.shared)) {
            this.shared = shared;
            return true;
        }
        return false;
    }

	@Override
	public  void setShared(Shared shared) throws RemoteException {
        System.out.println("Trying to update shared");
		if (checkAndSetSharedThreadSafe(shared)) {
            System.out.println("Updating shared");
			this.shared = shared;
			for (Computer computer : computers) {
				computer.setShared(shared);
			}
		}
	}

	@Override
	public void run() {
		try {
			//So far this doesn't solve normal task. Only tasks that are marked as simple
			//TODO: Add support for normal tasks as well!
			//Currently all continuation tasks are set to simple.. So all the contination tasks will be executed in space!
			
			Computer comp = new ComputerImpl(this);
			SpaceComputer spaceComputer = new SpaceComputer(comp,this);
			this.registerSpaceComputer(spaceComputer);
			
		} catch (RemoteException ignore) { } //Cannot happen!
	}

	public static void main(String[] args) {
	    int port = 8888;
	    if (System.getSecurityManager() == null) {
	        System.setSecurityManager(new java.rmi.RMISecurityManager());
	    }
	    
	    try {
	        Registry reg = LocateRegistry.createRegistry(port);

	        Space space = new SpaceImpl();
	        reg.rebind(SERVICE_NAME, space);
	
	    }catch (ExportException e) {
	        System.err.println(e.getMessage());
	        System.exit(0);
	    } 
	    
	    catch (RemoteException e) {
	        e.printStackTrace();
	    }
	    catch (Exception e) {
	    	e.printStackTrace();
	    }
	}
	
	
	
}
