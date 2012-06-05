package system;


import api.*;

import java.rmi.RemoteException;
import java.util.List;

public class SpaceWorkStealerProxy extends ComputerProxy implements Runnable {

	private static final long serialVersionUID = 8616855479558607248L;
	private Space thisSpace;
    private Space otherSpace;

    /**
     * Creates a Proxy for handling Computers
     *
     * @param thisSpace    On what space the tasks and results are published
     */
    public SpaceWorkStealerProxy(Space thisSpace, Space otherSpace) throws RemoteException {
        super(new ComputerImpl(thisSpace), thisSpace);
        this.thisSpace = thisSpace;
        this.otherSpace = otherSpace;
        this.LOW_WATERMARK = 0;
        this.HIGH_WATERMARK = 0;

        thisSpace.register(this);
    }

    @Override
    public void registerComputer(Computer cp) throws RemoteException {
        System.out.println("SpaceWorkStealerProxy doesn't use registerComputer");
    }

    @Override
    public void deregisterComputer(Computer cp) throws RemoteException {
        System.out.println("SpaceWorkStealerProxy doesn't use deregisterComputer");
    }


    @Override
    public List<Computer> getComputers() throws RemoteException {
        List<Computer> computers = null;
        try {
            computers = otherSpace.getComputers();
        } catch (RemoteException e) {
            System.out.println("Unhandled error");
            System.exit(0);
        }
        return computers;
    }

    @Override
    public void start() {
        Thread thread = new Thread(this);
        thread.start();
    }

    @Override
    public void run() {
        try {
            thisSpace.register(this);
        } catch (RemoteException ignore) { }

        WorkStealer workStealer = new WorkStealer(this);
        Thread wsThread = new Thread(workStealer);
        wsThread.start();

        System.out.println("Started a SpaceWorkStealer to start stealing tasks from another space");

    }

}
