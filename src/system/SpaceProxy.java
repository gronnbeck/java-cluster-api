package system;

import api.*;

import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;

public class SpaceProxy implements Space, Executor, Runnable {

    private BlockingQueue<Runnable> tasks;
    private Space space;

    public SpaceProxy (Space space) {
        this.space = space;
        tasks = new LinkedBlockingQueue<Runnable>();

        Thread t =  new Thread(this);
        t.start();
    }

    private void put(Runnable runnableTask) {
        try {
            tasks.put(runnableTask);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void put(final Task<?> task) throws RuntimeException, RemoteException, InterruptedException {
        Runnable putTask = new Runnable() {
            @Override
            public void run() {
                try {
                    space.put(task);
                } catch (RemoteException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        put(putTask);
    }

    @Override
    public void stop() throws RemoteException {
        Runnable stopTask = new Runnable() {
            @Override
            public void run() {
                try {
                    space.stop();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        };
        put(stopTask);
    }

    @Override
    public void setShared(final Shared<?> shared) throws RemoteException {
        Runnable setSharedTask = new Runnable() {
            @Override
            public void run() {
                try {
                    space.setShared(shared);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        };
        put(setSharedTask);
    }

    @Override
    public void publishTask(final Task task) throws RemoteException, InterruptedException {
        Runnable publishTaskTask = new Runnable() {
            @Override
            public void run() {
                try {
                    space.publishTask(task);
                } catch (RemoteException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                } catch (InterruptedException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            }
        };
        put(publishTaskTask);
    }

    @Override
    public void registerSpace(final Space aSpace) throws RemoteException {
        Runnable registerSpaceTask = new Runnable() {

            @Override
            public void run() {
                try {
                    space.registerSpace(aSpace);
                } catch (RemoteException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            }
        };
        put(registerSpaceTask);
    }

    @Override
    public void deregisterSpace(final Space aSpace) throws RemoteException {
        Runnable deregisterSpaceTask = new Runnable() {
            @Override
            public void run() {
                try {
                    space.deregisterSpace(aSpace);
                } catch (RemoteException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            }
        };
        put(deregisterSpaceTask);
    }

    @Override
    public void register(final Computer computer) throws RemoteException {
        Runnable registerComputerTask = new Runnable() {
            @Override
            public void run() {
                try {
                    space.register(computer);
                } catch (RemoteException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            }
        };
        put(registerComputerTask);
    }

    @Override
    public void deregister(final Computer computer) throws RemoteException {
        Runnable deregisterComputerTask = new Runnable() {
            @Override
            public void run() {
                try {
                    space.deregister(computer);
                } catch (RemoteException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            }
        };
        put(deregisterComputerTask);
    }

    @Override
    public Task<?> takeSimpleTask() throws RemoteException, InterruptedException {
        return space.takeSimpleTask();
    }

    @Override
    public void registerContin(final ContinuationTask continuation) throws RemoteException {
        Runnable regConTask = new Runnable() {
            @Override
            public void run() {
                try {
                    space.registerContin(continuation);
                } catch (RemoteException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            }
        };
        put(regConTask);
    }

    @Override
    public void putResult(final Result result) throws RemoteException, InterruptedException {
        Runnable putResultTask = new Runnable() {
            @Override
            public void run() {
                try {
                    space.putResult(result);
                } catch (RemoteException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                } catch (InterruptedException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            }
        };
        put(putResultTask);
    }




    // THESE ARE NOT ASYNC...
    private String _id = null;
    @Override
    public String getId() throws RemoteException {
        if (_id != null) return _id;
        String id = space.getId();
        _id = id;
        return _id;
    }

    @Override
    public List<Computer> getComputers() throws RemoteException {
        return space.getComputers();
    }

    @Override
    public Result getResult(String jobId) throws RemoteException, InterruptedException {
        return space.getResult(jobId);
    }

    @Override
    public HashMap getInfo() throws RemoteException {
        return space.getInfo();
    }

    @Override
    public Task<?> takeTask() throws RemoteException, InterruptedException {
        return space.takeTask();
    }

    @Override
    public void execute(Runnable runnable) {
        runnable.run();
    }

    @Override
    public void run() {

        while (true) {
            try {
                Runnable task = tasks.take();
                execute(task);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }
}
