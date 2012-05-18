package system;

import api.Computer;
import api.Result;
import api.Shared;
import api.Space;
import api.Task;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class ComputerProxy extends UnicastRemoteObject implements Runnable, Computer {

    private Computer computer;
    protected Space space;

    /**
     * Creates a Proxy for handling Computers
     * @param computer The computer you wish to proxy
     * @param space On what space the tasks and results are published
     */
    public ComputerProxy(Computer computer, Space space) throws RemoteException {
        super();
        this.computer = computer;
        this.space = space;
        Thread t = new Thread(this);
        t.start();
    }

    @Override
    public Result execute(Task task) throws RemoteException {
        return computer.execute(task);
    }

    @Override
    public void stop() throws RemoteException {
        computer.stop();
    }

    /**
     * Start the ComputerProxy process
     * It waits on a client to publish a task. When a task is published
     * it tires to assign the task to its corresponding Computer.
     * If the computer returns a result it sends the result back to the space.
     * However, if the Computer raises a RemoteException this proxy puts the
     * task back into the space, and deregisters it self.
     */
    public void run() {
        System.out.println("ComputerProxy running");
        do {
            try {
                Task task = space.takeTask();
                try {
                    Result result = execute(task);
                    space.putResult(result);
                } catch (RemoteException e) {
                    try {
                        space.put(task); // Handle faulty computers. Just put the current task bak into space
                        space.deregister(this);
                        return;          // exit thread . The proxy is no longer needed
                    } catch (RemoteException ignore) { }
                }
            } catch (RemoteException ignore) { }
            catch (InterruptedException e) {
                e.printStackTrace();            // don't know how we shall handle this one, yet...
            }
        } while(true);
    }

	@Override
	public Object getShared() throws RemoteException {
		return computer.getShared();
	}

	@Override
	public void setShared(Shared shared) throws RemoteException {
		computer.setShared(shared);
		
	}

    @Override
    public Space getSpace() throws RemoteException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void setSpace(Space space) throws RemoteException {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
