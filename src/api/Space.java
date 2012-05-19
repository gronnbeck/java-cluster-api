package api;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Space extends Remote, Computer2Space, Task2Space {
    public static String SERVICE_NAME = "Space";

    /**
     * Puts a task into the Computer Space
     * @param task To put into the computer space
     * @throws RuntimeException
     */
    void put(Task<?> task) throws RuntimeException, RemoteException, InterruptedException;

    /**
     * Takes a result from the space. If no result
     * are present in the space, the method blocks until
     * it one appears.
     * @return A result in the Computer Space
     * @throws java.rmi.RemoteException
     */
    Result<?> take() throws RemoteException, InterruptedException;

    /**
     * First stops all the Computers in Space, then it shuts it self down
     * @throws java.rmi.RemoteException
     */
    void stop() throws RemoteException;
    
    /**
     * Sets the shared mutable object that is accessible by all tasks. 
     * The value set by this method will propagate to all tasks in best effort manner.
     * This value is used as a basis for pruning.
     * @param shared
     */
    
    void setShared(Shared<?> shared) throws RemoteException;

}
