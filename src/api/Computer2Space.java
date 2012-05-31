package api;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface Computer2Space extends Serializable, Remote {

    /**
     * Registers a computer to the space
     * @param computer The Computer the Space should assign tasks
     * @throws java.rmi.RemoteException
     */
    void register(Computer computer) throws RemoteException;

    /**
     * Removes a computer from the space
     * @param computer The computer you no longer wish to assign tasks
     * @throws java.rmi.RemoteException
     */
    void deregister(Computer computer) throws RemoteException;

    /**
     * Get a task from Space
     * @return A task to be solved by a Computer
     * @throws java.rmi.RemoteException
     * @throws InterruptedException
     */
    Task<?> takeTask() throws RemoteException, InterruptedException;

    /**
     * Takes a Simple task from Space.
     * A Simple task is a task marked as simple by the developer and should be easy to execute (i.e. linear time algorithms)
     * @return A task marked as simple, blocks if the taskQ is empty
     * @throws RemoteException
     * @throws InterruptedException
     */
    Task<?> takeSimpleTask() throws RemoteException, InterruptedException;

}
