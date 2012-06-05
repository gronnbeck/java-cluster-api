package system;

import api.*;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class SpaceCoordinatorImpl extends UnicastRemoteObject implements SpaceCoordinator {

	private static final long serialVersionUID = -6142857838122685348L;
	private List<Space> spaces;
    private BlockingQueue<Result<?>> resultQ;
   // private ConcurrentMap<String, BlockingQueue<Result>> resultQs;
    private Shared<?> shared;
    private long startTime;
    private String jobid;
    private Computer computer;
    
    
    protected SpaceCoordinatorImpl() throws RemoteException {
        super();
        spaces = new ArrayList<Space>();
        resultQ = new LinkedBlockingQueue<Result<?>>();
        startTime = System.nanoTime();
    }

    @Override
    public void registerSpace(Space space) throws RemoteException {
        System.out.println("A Space has connected itself to this SpaceCoordinator");
        if (shared != null) space.setShared(shared);
        for (Space aSpace : spaces) {
            space.registerSpace(aSpace);
            aSpace.registerSpace(space);
        }
        spaces.add(space);
    }

    @Override
    public void deregisterSpace(Space space) throws RemoteException {
        System.out.println("A Space has disconnected itself from this SpaceCoordinator");
        spaces.remove(space);
        for (Space aSpace : spaces) {
            aSpace.deregisterSpace(space);
        }
    }


    public static void main(String[] args) {
        int port = 8887;
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new java.rmi.RMISecurityManager());
        }
        try {
            SpaceCoordinator spaceProvider = new SpaceCoordinatorImpl();

            Registry reg = LocateRegistry.createRegistry(port);
            reg.rebind(SpaceCoordinator.SERVICE_NAME, spaceProvider);
            System.out.println("SpaceCoordinator is online!");

        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }


}
