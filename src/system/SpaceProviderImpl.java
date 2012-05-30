package system;

import api.*;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.LinkedBlockingQueue;

public class SpaceProviderImpl extends UnicastRemoteObject implements SpaceProvider {

    private class TaskPublisher extends Thread {

        Task task;
        Space space;
        public TaskPublisher(Task task, Space space){
            this.task = task;
            this.space = space;
        }

        @Override
        public void run() {
            try {
                space.publishTask(task);
                do {
                    Result result = space.getResult(jobid);
                    resultQ.put(result);
                } while(true);
            } catch (RemoteException e) {
                System.out.println("Have assumed that a Space will never shutdown unexpectedly. Exiting.");
                System.exit(0);
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private List<Space> spaces;
    private BlockingQueue<Result> resultQ;
   // private ConcurrentMap<String, BlockingQueue<Result>> resultQs;
    private long startTime;
    private Shared shared;
    private String jobid;
    
    
    protected SpaceProviderImpl() throws RemoteException {
        super();
        spaces = new ArrayList<Space>();
        resultQ = new LinkedBlockingQueue<Result>();
        //resultQs = new ConcurrentHashMap<String, BlockingQueue<Result>>();
        
        startTime = System.nanoTime();
    }

    @Override
    public Result publishTask(Task task) throws RemoteException, InterruptedException {
        System.out.println("A task was published to SpaceProvider");
        jobid = task.getJobId();
        System.out.println(jobid);
        Result result = task.execute();
        if (result instanceof ContinuationResult) {
            ContinuationTask continuationTask = (ContinuationTask) result.getTaskReturnValue();
            int counter = 0;
            System.out.println("Breaking up task into subtasks");
            for (Task currentTask : continuationTask.getTasks()) {
                TaskPublisher taskPublisher = new TaskPublisher(currentTask, spaces.get(counter));
                taskPublisher.start();
                counter = (counter + 1) % spaces.size();
            }
            System.out.println("Waiting for results");
            for (int i = 0; i < continuationTask.getTasks().size(); i++) {
                Result subResult = resultQ.take();
                continuationTask.ready(subResult);
                System.out.println(i+1+"/"+continuationTask.getTasks().size()+" results received.");
            }
            result = continuationTask.execute();
            System.out.println("  done.");
        }

        return result;
    }

    @Override
    public void registerSpace(Space space) throws RemoteException {
        System.out.println("A Space has connected itself to this SpaceProvider");
        if (shared != null) space.setShared(shared);
        spaces.add(space);
    }

    @Override
    public void deregisterSpace(Space space) throws RemoteException {
        System.out.println("A Space has disconnected itself from this SpaceProvider");
        spaces.remove(space);
    }

    @Override
	public String getInfo() throws RemoteException {
    	String info = " ";
    	info += "\rUptime: " + Math.round(((System.nanoTime() - startTime)) / 10E8) + "seconds\n";
    	info += "\rNumber of spaces: " + Math.random();
    	
    	for (Space space : spaces) {
    		space.getInfo();
		}
    	info += "\rNumber of Computers: \n";

		return info;
	}

    @Override
    public void setShared(Shared<?> shared) throws RemoteException {
        this.shared = shared;
        for (Space space : spaces) {
            space.setShared(shared);
        }
    }

    public static void main(String[] args) {
        int port = 8887;
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new java.rmi.RMISecurityManager());
        }
        try {
            SpaceProvider spaceProvider = new SpaceProviderImpl();

            Registry reg = LocateRegistry.createRegistry(port);
            reg.rebind(SERVICE_NAME, spaceProvider);
            System.out.println("SpaceProvider is online!");

        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
