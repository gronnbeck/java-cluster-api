package system;

import api.*;

import java.rmi.RemoteException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class SpaceProxy implements Space, Runnable {

    private BlockingQueue<Result> resultQ;
    private Space space;
    public SpaceProxy(Space space) {
        this.space = space;
        this.resultQ = new LinkedBlockingQueue<Result>();
        Thread t = new Thread(this);
        t.start();
    }

    @Override
    public void setShared(Shared shared) throws RemoteException {
        final Shared ashared = shared;
        Thread run = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    space.setShared(ashared);
                } catch (RemoteException e) {
                    System.out.println("Failed to setShared. Exiting");
                    System.exit(0);
                }
            }
        });
    	run.start();
    }

    @Override
    public void putResult(Result result) throws RemoteException, InterruptedException {
        space.putResult(result);
    }

    @Override
    public void run() {
        while (true) {
            try {
                Result result = resultQ.take();
                try {
                    putResult(result);
                } catch (RemoteException e) {
                    // TODO: Handle exceptions correctly
                    e.printStackTrace();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }


    @Override
    public void register(Computer computer) throws RemoteException {
        throw new IllegalAccessError("Not implemented");
    }

    @Override
    public void deregister(Computer computer) throws RemoteException {
        throw new IllegalAccessError("Not implemented");
    }

    @Override
    public Task takeTask() throws RemoteException, InterruptedException {
        throw new IllegalAccessError("Not implemented");
    }

    @Override
    public void registerContin(ContinuationTask continuation) throws RemoteException {
        throw new IllegalAccessError("Not implemented");
    }

    @Override
    public void put(Task task) throws RuntimeException, RemoteException, InterruptedException {
        throw new IllegalAccessError("Not implemented");
    }

    @Override
    public Result take() throws RemoteException, InterruptedException {
        throw new IllegalAccessError("Not implemented");
    }

    @Override
    public void stop() throws RemoteException {
        throw new IllegalAccessError("Not implemented");
    }

	@Override
	public Task takeSimpleTask() throws InterruptedException {
		throw new IllegalAccessError("This shouldnt be used here!");

	}
}
