package system;


import api.*;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collections;
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
    }

    @Override
    public void registerComputer(Computer cp) throws RemoteException {
        System.out.println("SpaceWorkStealerProxy doesn't use registerComputer");
    }

    @Override
    public void deregisterComputer(Computer cp) throws RemoteException {
        System.out.println("SpaceWorkStealerProxy doesn't use deregisterComputer");

    }

    public void start() {
        Thread thread = new Thread(this);
        thread.start();
    }

    public void run() {
        try {
            List<Computer> otherComputers = Collections.synchronizedList(otherSpace.getComputers());
            List<Task> tasks = new ArrayList<Task>();

            try {
                thisSpace.register(this);
            } catch (RemoteException ignore) { }

            WorkStealer workStealer = new WorkStealer(this);
            Thread wsThread = new Thread(workStealer);
            //wsThread.start();
        } catch (RemoteException e) {
            System.out.println("Was not able to get the computer list from the other space. Shutting down workstealer");
            return;
        }
        System.out.println("Started a SpaceWorkStealer to start stealing tasks from another space");
        while (true) {
            try {
                Thread.sleep(3000);
                Thread.yield();
                System.out.println("Stolen task size: " + getTaskQ().size());
            } catch (InterruptedException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            } catch (RemoteException ignore) {
            }
        }
    }

}
