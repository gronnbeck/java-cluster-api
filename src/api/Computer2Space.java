package api;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;

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
    
    Task<?> takeSimpleTask() throws RemoteException, InterruptedException;

}
