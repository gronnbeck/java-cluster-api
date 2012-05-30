package system;


import api.*;

import java.rmi.RemoteException;
import java.util.List;

public class SpaceWorkStealerProxy extends ComputerProxy implements Runnable {

    public int STEAL_ALLOWED_SIZE = 1;
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
        super.STEAL_ALLOWED_SIZE = STEAL_ALLOWED_SIZE;
        super.WANT_TO_STEAL_SIZE = 3;

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
        return otherSpace.getComputers();
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

        WorkStealer workStealer = new WorkStealer(this, true);
        Thread wsThread = new Thread(workStealer);
        wsThread.start();

        System.out.println("Started a SpaceWorkStealer to start stealing tasks from another space");

    }

}
