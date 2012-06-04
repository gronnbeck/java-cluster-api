package system;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;

import api.*;
import checkpointing.Persistor;
import checkpointing.Recoverable;
import checkpointing.State;

public class SpaceImpl extends UnicastRemoteObject implements Space, Runnable, Recoverable {

	private static final long serialVersionUID = 1L;
    private String id;
	private BlockingQueue<Task<?>> taskQue;
	private BlockingQueue<Task<?>> simpleTaskQue;
    private ConcurrentHashMap<Object,ContinuationTask> waitingTasks;
    private HashMap<String, BlockingQueue<Result>> resultQs;
    private ConcurrentHashMap<String, Shared<?>> sharedMap;

    private BlockingQueue<Computer> computers;
    private ConcurrentHashMap<String, Space> spaces;

    private ConcurrentHashMap<String, BlockingQueue<TaskEvent>> taskEvents;

    // For checkpointing
    private boolean changed;

    public SpaceImpl() throws RemoteException {
        super();
        id = UUID.randomUUID().toString();
        taskQue = new LinkedBlockingQueue<Task<?>>();
        simpleTaskQue = new LinkedBlockingQueue<Task<?>>();
        waitingTasks = new ConcurrentHashMap<Object, ContinuationTask>();
        resultQs = new HashMap<String, BlockingQueue<Result>>();
        sharedMap = new ConcurrentHashMap<String, Shared<?>>();

        taskEvents = new ConcurrentHashMap<String, BlockingQueue<TaskEvent>>();

        // Using an arraylist might give performance problems
        computers = new LinkedBlockingQueue<Computer>();

        spaces = new ConcurrentHashMap<String, Space>();

        // test recover
        changed = false;
        //recover();
        // Start the State Writer..
        //TimeCheckpoint timeCheckpoint = new TimeCheckpoint(this, 1, "/tmp/spaceimpl.data");
        //timeCheckpoint.start();

        Thread computerThread = new Thread(this);
        computerThread.start();
    }

    @Override
    public void put(Task<?> task) throws RuntimeException, RemoteException, InterruptedException {
        if (task.isSimple()) {
			simpleTaskQue.put(task);
			return;
		}
    	//taskQue.put(task);
        Computer computer = computers.take();
        computer.addTask(task);
    }

    @Override
    public void publishTask(Task task) throws RemoteException, InterruptedException {
        if (!resultQs.containsKey(task.getJobId())) resultQs.put(task.getJobId(), new LinkedBlockingQueue<Result>());
        if (!taskEvents.contains(task.getJobId())) taskEvents.put(task.getJobId(), new LinkedBlockingDeque<TaskEvent>());
        task.setOwnerId(getId());
        put(task);
    }

    @Override
    public void registerSpace(Space space) throws RemoteException {
        // TODO: Create a Space proxy to talk to instead
        Space proxy = new SpaceProxy(space);
        Computer spaceWorkStealer = new SpaceWorkStealerProxy(this, proxy);
        spaces.put(proxy.getId(), proxy);
        System.out.println("A Space has registered itself");
    }

    @Override
    public void deregisterSpace(Space space) throws RemoteException {
        spaces.remove(space.getId());
        System.out.println("A Space has unregistered itself");
    }

    @Override
    public String getId() throws RemoteException {
        return id;
    }

    @Override
    public List<Computer> getComputers() throws RemoteException {
    	List<Computer> computer = new ArrayList<Computer>(computers);
    	return computer;
    	
    }

    @Override
    public TaskEvent nextEvent(String jobId) throws RemoteException, InterruptedException {
        BlockingQueue<TaskEvent> eventQ = taskEvents.get(jobId);
        return eventQ.take();
    }

    @Override
    public Result getResult(String jobId) throws RemoteException, InterruptedException {
        BlockingQueue<Result> resultQ = resultQs.get(jobId);
        return resultQ.take();
    }


    @Override
    public Task<?> takeTask() throws RemoteException, InterruptedException {
        return taskQue.take();
    }
  
    @Override
    public Task<?> takeSimpleTask() throws InterruptedException {
    	return simpleTaskQue.take();
    }


    @Override
    public synchronized void stop() throws RemoteException {
        for (Computer computer : computers) {
            computer.stop();
        }
    }

    private synchronized void registerSpaceComputer(Computer computer) throws RemoteException{
        for (Shared shared : sharedMap.values())
            if(shared != null) computer.setShared(shared);
        computers.add(computer);
        // Start the ComputerProxy which steals task from other Spaces
        System.out.println("The space computer is successfully registered!");

    }

    @Override
    public synchronized void register(Computer computer) throws RemoteException {
        ComputerProxy proxy = null;
        if (!(computer instanceof ComputerProxy)) {
            proxy = new ComputerProxy(computer, this);
        } else {
            System.out.println("Registering a computer proxy");
            proxy = (ComputerProxy) computer;
        }
        for (Shared shared : sharedMap.values())
            if(shared != null) proxy.setShared(shared);

        for (Computer c : computers) {
            if (!(c instanceof SpaceComputer)) {
                c.registerComputer(proxy);
                proxy.registerComputer(c);
            }
        }
        computers.add(proxy);
        System.out.println("A computer has registered it self");
    }


    @Override
    public synchronized void deregister(Computer computer) throws RemoteException {
        computers.remove(computer); // a cproxy here as well
        for (Computer c : computers) {
            c.deregisterComputer(computer);
        }
    }

    @Override
    public synchronized void putResult(Result result) throws RemoteException, InterruptedException {
    	if (result == null) return;

        // Bah comparing string is slow :/
        if (!result.getOwnerId().equals(getId())) {
            Space proxy = spaces.get(result.getOwnerId());      // Assumes that Space in spaces is a spaceProxy
            proxy.putResult(result);
            return;
        }

        // else continue to process result
        if (result instanceof ContinuationResult) {
            ContinuationTask continuationTask = (ContinuationTask) result.getTaskReturnValue();
            registerContin(continuationTask);
            return;
        }


        String id = result.getTaskIdentifier();
        if (waitingTasks.containsKey(id)) {
            ContinuationTask continuation = waitingTasks.get(id);
            continuation.ready(result);
            waitingTasks.remove(id);
            if (continuation.isReady()) {
            	if (continuation.isSimple()) {
					simpleTaskQue.put(continuation);
				} else {
					taskQue.put(continuation);
				}
            }
        }
        else {
            // if not it is probably the end result (as I see it now)
            // in the case of fib. It should just be 1 number left
            if (resultQs.containsKey(result.getJobId())) {
                BlockingQueue<Result> resultFetcher = resultQs.get(result.getJobId());
                resultFetcher.put(result);
            } else {
                System.out.println("Denne skal aldri bli kalt. Hvis den blir det, er det et delresultat som ikke skal komme hit" +
                        "men mandelclient. Det er bare på Tsp og Fib denne strengen blir printet ut.. mmm muffins...");
            }
        }

    }

    @Override
    public synchronized void registerContin(ContinuationTask continuation) throws RemoteException {
        changed = true;
        for (Task task : continuation.getTasks()) {
            try {

                // Ignore cached tasks.
            	if (!task.getCached()) {
            		taskQue.put(task);
				}

            	if (task.isSimple()){
            		simpleTaskQue.put(task);
            	}
                
                waitingTasks.put(task.getTaskIdentifier(), continuation);


            } catch (InterruptedException e) {
                e.printStackTrace();
            }
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
		if (checkAndSetSharedThreadSafe(shared)) {
            System.out.println("Updating shared " + shared.getValue());
			for (Computer computer : computers) {
				    computer.setShared(shared);
			}
            for (Space space : spaces.values()) {
                space.setShared(shared);
            }
		}
	}
    
    public HashMap<String, String> getInfo() {
    	HashMap<String, String> info = new HashMap<String, String>();
    	
    	info.put("Computers_running", ((Integer)computers.size()).toString());

    	return null;
    }

    @Override
    public State getState() {
        ArrayList<ContinuationTask> cts = new ArrayList<ContinuationTask>();
        ArrayList<Shared<?>> shareds = new ArrayList<Shared<?>>();

        for (ContinuationTask ct : waitingTasks.values()) {
            cts.add(ct);
        }
        for (Shared<?> s : sharedMap.values()) {
            shareds.add(s);
        }

        return new SpaceState(cts, shareds, id);
    }

    @Override
    public boolean stateChanged() {
        return changed;
    }

    private void queueTask(Task task) {
        try {
            taskQue.put(task);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void recover() {
        System.out.println("Trying to recover.");
        waitingTasks = new ConcurrentHashMap<Object, ContinuationTask>();
        Persistor persistor = new Persistor("/tmp/spaceimpl.data");

        SpaceState state = null;
        try {
            state = (SpaceState)persistor.read();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("No recovery file was found. Continuing");
            return;
        }

        if (state.continuationTasks.size() == 0 && state.shareds.size() == 0) {
            System.out.println("Nothing to recover");
            return;
        }

        System.out.println("Recovering Space [" + state.spaceid +"]");

        System.out.println("  * Recovering Continuation Tasks");
        for (ContinuationTask continuationTask : state.continuationTasks) {
            System.out.println("  - Recovered: "+ continuationTask);
            for (Task task : continuationTask.getTasks()) {
                waitingTasks.put(task.getTaskIdentifier(), continuationTask);
                queueTask(task);
            }
        }

        System.out.println("  * Recovering Shareds");
        for (Shared<?> shared : state.shareds) {
            try {
                sharedMap.put(shared.getJobId(), shared);
                System.out.println("  - Recovered Shared for job: " + shared.getJobId());
            } catch (RemoteException ignore) {}
        }



        System.out.println("Done recovering");
    }

    @Override
    public void run() {
        try {
            Computer comp = new ComputerImpl(this);
            SpaceComputer spaceComputer = new SpaceComputer(comp,this);
            this.registerSpaceComputer(spaceComputer);

        } catch (RemoteException ignore) { }
    }

    @Override
    public void propagateTaskEvent(TaskEvent taskEvent) throws RemoteException {
        if (taskEvent.getOwnerId().equals(id)) {
            BlockingQueue<TaskEvent> eventQ =  taskEvents.get(taskEvent.getJobId());

            try {
                eventQ.put(taskEvent);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
        else {
            // return to task owner
            Space space = spaces.get(taskEvent.getOwnerId());
            try {
                space.propagateTaskEvent(taskEvent);
            } catch (RemoteException e) {
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

            // Connect to SpaceCoordinator
            Registry spaceProviderRegistry = LocateRegistry.getRegistry(host, 8887);
            SpaceCoordinator spaceProvider = (SpaceCoordinator) spaceProviderRegistry.lookup(SpaceCoordinator.SERVICE_NAME);
            spaceProvider.registerSpace(space);
            System.out.println("Space connected to SpaceCoordinator at " + host);

        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (NotBoundException e) {
            e.printStackTrace();
        }
    }


}
