package system;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

import api.*;

public class SpaceImpl extends UnicastRemoteObject implements Space, Runnable {

	private static final long serialVersionUID = 1L;
	private BlockingQueue<Result<?>> resultQue;
	private BlockingQueue<Task<?>> taskQue;
	private BlockingQueue<Task<?>> simpleTaskQue;
    private ConcurrentHashMap<Object,ContinuationTask> mapContin;
    private ArrayList<Computer> computers;
    private HashMap<String, BlockingQueue<Result>> resultQs;
    private Shared<?> shared;
    private ConcurrentHashMap<String, Shared<?>> sharedMap;

    public SpaceImpl() throws RemoteException {
        super();
        resultQue = new LinkedBlockingQueue<Result<?>>();
        taskQue = new LinkedBlockingQueue<Task<?>>();
        simpleTaskQue = new LinkedBlockingQueue<Task<?>>();
        mapContin = new ConcurrentHashMap<Object, ContinuationTask>();
        computers = new ArrayList<Computer>();
        resultQs = new HashMap<String, BlockingQueue<Result>>();
        sharedMap = new ConcurrentHashMap<String, Shared<?>>();
        
        Thread computerThread = new Thread(this);
        computerThread.start();
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
    public Result publishTask(Task task) throws RemoteException, InterruptedException {
        resultQs.put(task.getTaskIdentifier(), new LinkedBlockingQueue<Result>());
        put(task);
        return resultQs.get(task.getTaskIdentifier()).take();
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
        for (Computer computer : computers) {
            computer.stop();
        }
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
    	System.out.println("The space computer is successfully registred!");
    	
    }

    @Override
    public synchronized void deregister(Computer computer) throws RemoteException {
        computers.remove(computer); // a cproxy here as well
    }

    @Override
    public synchronized void putResult(Result result) throws RemoteException, InterruptedException {
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
            BlockingQueue<Result> resultFetcher = resultQs.get(result.getTaskIdentifier());
            resultFetcher.put(result);
        }
    }

    @Override
    public synchronized void registerContin(ContinuationTask continuation) throws RemoteException {

        for (Task task : continuation.getTasks()) {
            try {
                // Ignore cached tasks.
            	if (!task.getCached()) {
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
    
    

    public static void main(String[] args) {
        String host = args[0];
        int port = Integer.parseInt(args[1]);
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new java.rmi.RMISecurityManager());
        }
        try {
            Space space = new SpaceImpl();

            // Setup registery for Computers to find a Space
            Registry reg = LocateRegistry.createRegistry(port);
            reg.rebind(SERVICE_NAME, space);
            System.out.println("Space running!");

            // Connect to SpaceProvider
            Registry spaceProviderRegistry = LocateRegistry.getRegistry(host, 8887);
            SpaceProvider spaceProvider = (SpaceProvider) spaceProviderRegistry.lookup(SpaceProvider.SERVICE_NAME);
            spaceProvider.registerSpace(space);
            System.out.println("Space connected to SpaceProvider at " + host);




        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (NotBoundException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    private synchronized boolean checkAndSetSharedThreadSafe(Shared shared) throws RemoteException {
        Shared<?> thisShared = sharedMap.get(shared.getJobId());
        if (shared.isNewerThan(thisShared)) {
            sharedMap.put(shared.getJobId(), shared);
            return true;
        }
        return false;
    }

	@Override
	public  void setShared(Shared shared) throws RemoteException {
        System.out.println("Trying to update shared");
		if (checkAndSetSharedThreadSafe(shared)) {
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
				
			} catch (RemoteException ignore) {
				//Cannot happen!
			}
			

		
	}
}
