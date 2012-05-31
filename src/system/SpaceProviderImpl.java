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

public class SpaceProviderImpl extends UnicastRemoteObject implements SpaceProvider, Computer {

    private List<Space> spaces;
    private BlockingQueue<Result> resultQ;
   // private ConcurrentMap<String, BlockingQueue<Result>> resultQs;
    private long startTime;
    private Shared shared;
    private String jobid;
    private Computer computer;
    
    
    protected SpaceProviderImpl() throws RemoteException {
        super();
        spaces = new ArrayList<Space>();
        resultQ = new LinkedBlockingQueue<Result>();
        startTime = System.nanoTime();
    }

    @Override
    public Result publishTask(Task task) throws RemoteException, InterruptedException {
        // Just publish a task to a random space, and let the workstealing begin
        Space space  = spaces.get(0);
        space.publishTask(task);
        return space.getResult(task.getJobId());
    }

    @Override
    public void registerSpace(Space space) throws RemoteException {
        System.out.println("A Space has connected itself to this SpaceProvider");
        if (shared != null) space.setShared(shared);
        for (Space aSpace : spaces) {
            space.registerSpace(aSpace);
            aSpace.registerSpace(space);
        }
        spaces.add(space);
    }

    @Override
    public void deregisterSpace(Space space) throws RemoteException {
        System.out.println("A Space has disconnected itself from this SpaceProvider");
        spaces.remove(space);
        for (Space aSpace : spaces) {
            aSpace.deregisterSpace(space);
        }
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

    @Override
    public Shared getShared(String id) throws RemoteException {
        return shared;
    }


    public static void main(String[] args) {
        int port = 8887;
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new java.rmi.RMISecurityManager());
        }
        try {
            SpaceProvider spaceProvider = new SpaceProviderImpl();

            Registry reg = LocateRegistry.createRegistry(port);
            reg.rebind(SpaceProvider.SERVICE_NAME, spaceProvider);
            System.out.println("SpaceProvider is online!");

        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public Result<?> execute(Task<?> task) throws RemoteException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void stop() throws RemoteException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean hasCached() throws RemoteException {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Result executeCachedTask() throws RemoteException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Space getSpace() throws RemoteException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void setSpace(Space space) throws RemoteException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void registerComputer(Computer cp) throws RemoteException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void deregisterComputer(Computer cp) throws RemoteException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<Computer> getComputers() throws RemoteException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<Task> getTaskQ() throws RemoteException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean canSteal() throws RemoteException {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean want2Steal() throws RemoteException {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Task stealTask() throws RemoteException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void addTask(Task task) throws RemoteException {
        //To change body of implemented methods use File | Settings | File Templates.
    }


}
