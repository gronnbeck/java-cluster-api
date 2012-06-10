package system;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import api.*;

public class ComputerImpl extends UnicastRemoteObject implements Computer  {

	private static final long serialVersionUID = 7766125330258198521L;
	private Space space;
	private ConcurrentHashMap<String, Shared<?>> sharedMap;
	private Task<?> cached;
	private UUID id;
	
	private HashMap<String, HashMap<String, Object>> computerStats;

	public ComputerImpl(Space space) throws RemoteException {
		super();
		this.space = new SpaceProxy(space);
		this.sharedMap = new ConcurrentHashMap<String, Shared<?>>();
		this.computerStats = new HashMap<String, HashMap<String, Object>>();
		id = UUID.randomUUID();
	}

	@Override
	public Result<?> execute(Task<?> task) throws RemoteException {
		long taskStartTime = System.nanoTime();	
		task.setComputer(this);

		Result<?> result = task.execute();
		//result.setTaskRunTime(taskStartTime);
		

		// TODO This part can be more elegant
		if (result instanceof ContinuationResult) {
			ContinuationTask continuationTask = (ContinuationTask) result.getTaskReturnValue();

			Collections.sort(continuationTask.getTasks(), TaskComparator.getSingleton());            
			List<Task<?>> cachedTasks = continuationTask.markAsCached(1);
			cached = cachedTasks.get(0);
		}
		
		addStats(task.getJobId(), System.nanoTime() - taskStartTime);
		return result;
	}
	
	/**
	 * This method adds stats such as task execution time,
	 * and number of tasks executed for a certain job.
	 * @param jobId
	 */
	private void addStats(String jobId, long executionTime) {
		
		// Add a hashmap for a job if not already there
		if (!this.computerStats.containsKey(jobId))
			this.computerStats.put(jobId, new HashMap<String, Object>());

		// Initialize data structure
		HashMap<String, Object> jobHashMap = this.computerStats.get(jobId);
		if (!jobHashMap.containsKey("tasksExecuted")) 
			jobHashMap.put("tasksExecuted", 1);
		
		if (!jobHashMap.containsKey("jobExecutionTime")) 
			jobHashMap.put("jobExecutionTime", executionTime);
		
		
		// Add info to job hashmap
		jobHashMap.put("tasksExecuted", (Integer)(jobHashMap.get("tasksExecuted")) + 1);
		jobHashMap.put("jobExecutionTime", (Long)(jobHashMap.get("jobExecutionTime")) + executionTime);
		
	}

	@Override
	public boolean hasCached() {
		return cached != null;
	}

	@Override
	public  Result<?> executeCachedTask() throws RemoteException {
		//        System.out.println("Running a cached task");
		// hate too return _null_. Fix later
		if (cached == null) return null;
		Task<?> task = cached;
		cached = null;
		return execute(task);
	}

	@Override
	public void stop() throws RemoteException {
		System.exit(0);		
	}

	public static void main(String[] args) {
		try {

			if (args.length == 0) {
				System.out.println("Argument missing");
				System.exit(0);
			}
			String url = args[0];
			int port = 8888;
			if (args.length == 2) {
				port = Integer.parseInt(args[1]);
			}


			String urlString = "rmi://"+url+":"+port+"/"+Space.SERVICE_NAME;
			System.out.println("Connecting to " + url + ":" + port + ". ");
			//Registry registry = LocateRegistry.getRegistry(url,port);
			final Space space = (Space) Naming.lookup(urlString);

			// en hack for å starte Computer i en tråd.
			for (int i = 0; i < Runtime.getRuntime().availableProcessors(); i++) {
				Thread t = new Thread(new Runnable() {
					@Override
					public void run() {
						ComputerImpl computer = null;
						try {
							computer = new ComputerImpl(space);
							space.register(computer);
						} catch (RemoteException e) {
							e.printStackTrace();
							System.exit(1);
						}
					}
				});
				t.start();
				System.out.println("Computer #"+(i + 1)+ " started.");
			}

			System.out.println("Computer successfully registered!");
		} catch (Exception e) {
			System.out.println("Something went wrong!");
			e.printStackTrace();
		}

	}

	@Override
	public synchronized Shared<?> getShared(String id) throws RemoteException {
		return sharedMap.get(id);
	}


	private synchronized boolean checkAndSetSharedThreadSafe(Shared<?> shared) throws RemoteException {
		Shared<?> thisShared = sharedMap.get(shared.getJobId());
		if (shared.isNewerThan(thisShared)) {
			sharedMap.put(shared.getJobId(), shared);
			return true;
		}
		return false;
	}

	@Override
	public void setShared(Shared<?> proposedShared) throws RemoteException {
		if (checkAndSetSharedThreadSafe(proposedShared))	{
			space.setShared( proposedShared );
		}
		
	}

	@Override
	public void registerComputer(Computer cp) throws RemoteException {
		throw new IllegalAccessError("Method not implemented in ComputerImpl");
	}

	@Override
	public void deregisterComputer(Computer cp) throws RemoteException {
		throw new IllegalAccessError("Method not implemented in ComputerImpl");
	}

	@Override
	public List<Computer> getComputers() throws RemoteException {
		throw new IllegalAccessError("Method not implemented in ComputerImpl");
	}

	@Override
	public int getTaskQSize() throws RemoteException {
		throw new IllegalAccessError("Method not implemented in ComputerImpl");
	}

	@Override
	public boolean canSteal() throws RemoteException {
		throw new IllegalAccessError("Method not implemented in ComputerImpl");
	}

	@Override
	public boolean want2Steal() throws RemoteException {
		throw new IllegalAccessError("Method not implemented in ComputerImpl");
	}

	@Override
	public Task<?> stealTask() throws RemoteException {
		return null;  //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public synchronized void addTask(Task<?> task) throws RemoteException {
		//To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public void propagateTaskEvent(TaskEvent<?> taskEvent) throws RemoteException {
		space.propagateTaskEvent(taskEvent);
	}

	@Override
	public HashMap<String, Object> getJobInfo(String jobId) throws RemoteException {
		if (this.computerStats.containsKey(jobId)) {
			return this.computerStats.get(jobId);
		}
		else{
			HashMap<String, Object> emptyMap = new HashMap<String, Object>();
			emptyMap.put("tasksExecuted", 0);
			emptyMap.put("jobExecutionTime", 0L);
			return emptyMap;
		}
	}

	@Override
	public String getId() throws RemoteException {
		return this.id.toString();
	}

}
