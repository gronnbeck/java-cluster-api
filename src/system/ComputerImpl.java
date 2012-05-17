package system;


import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;

import tasks.TspTask;
import workpool.WorkpoolImpl;

import api.*;

public class ComputerImpl extends UnicastRemoteObject implements Computer, Runnable {

    private Space space;
	private Shared shared;
    private WorkpoolImpl workpool;
    private List<Task> taskCache;
    private boolean cachingOn = false;
    
	public ComputerImpl(int workpoolSize, boolean cachingOn) throws RemoteException {
		super();
        this.workpool = new WorkpoolImpl(workpoolSize);
        this.taskCache = new ArrayList<Task>();
        this.cachingOn = cachingOn;
        this.workpool.start();
        // Starting result fetcher.
        Thread t = new Thread(this);
        t.start();
	}
	
    public synchronized Space getSpace() throws RemoteException {
		return space;
	}


	public synchronized void setSpace(Space space) throws RemoteException  {
		this.space = new SpaceProxy(space);
	}


    private synchronized void cacheTask(Task task) {
        taskCache.add(task);
    }

    private synchronized Task getCachedTask(){
        if (taskCache.size() > 0) {
            Task task = taskCache.get(0);
            taskCache.remove(0);
            return task;
        }
        return null;
    }

    private void scheduleCachedTask() throws InterruptedException {
        Task task = getCachedTask();
        if (task == null) return;
        System.out.println("Running Cached task");

        workpool.putTask(task);
    }

	@Override
	public void execute(Task task) throws RemoteException {
        task.setComputer(this);
        try {
            scheduleCachedTask();
			workpool.putTask(task);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}


	}

	@Override
	public void stop() throws RemoteException {
		System.exit(0);		
	}
	

	@Override
	public synchronized Object getShared() throws RemoteException {
		return shared;
	}

    private synchronized boolean checkAndSetSharedThreadSafe(Shared shared) throws RemoteException {
        if (shared.isNewerThan(this.shared)) {
            this.shared = shared;
            return true;
        }
        return false;
    }

	@Override
	public void setShared(Shared proposedShared) throws RemoteException {
        if (checkAndSetSharedThreadSafe(proposedShared)){
		    space.setShared( shared );
		}
		
	}

	@Override
	public void run() {
		while (true) {
			try {

				Result result = workpool.takeResult();

                if (cachingOn && result instanceof ContinuationResult) {
                    System.out.println("Cached");
                    // here goes the chuchu calling train..
			        Task cachedTask = ((ContinuationTask) result.getTaskReturnValue()).getTasks().get(0);
                    cachedTask.setComputer(this);
                    cachedTask.setCached(true);
                    // Something wrong happens when we Cache a task...
                    cacheTask(cachedTask);
				}
   			    space.putResult(result);

			    
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (RemoteException ignore) { }

		}
		
	}
	

	public static void main(String[] args) {
		try {
            if (args.length == 0) {
                System.out.println("Argument missing");
                System.exit(0);
            }
            String url = args[0];
            int port = 8888;
            int workers = 1;
            boolean caching = false;
            if (args.length >= 2) {
                port = Integer.parseInt(args[1]);
            }

            if (args.length >= 3 && args[2].equals("-p")) {
                workers = Runtime.getRuntime().availableProcessors();
                System.out.println("   - [Multiprocessing] ON");
            }
            if (args.length >= 4 && args[3].equals("-l")) {
                caching = true;
                System.out.println("   - [Caching] ON");
            }

			String urlString = "rmi://"+url+":"+port+"/"+Space.SERVICE_NAME;
			System.out.println("Connecting to " + url + ":" + port + ". ");
			//Registry registry = LocateRegistry.getRegistry(url,port);
			Space space = (Space) Naming.lookup(urlString);
            ComputerImpl computer = new ComputerImpl(workers, caching);
            space.register(computer);
			System.out.println("Computer successfully registered!");
		} catch (Exception e) {
			System.out.println("Something went wrong!");
			e.printStackTrace();

		}



	}


}
