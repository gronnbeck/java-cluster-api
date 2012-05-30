package api;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.HashMap;

public interface Space extends Remote, Computer2Space, Task2Space {
    public static String SERVICE_NAME = "Space";

    /**
     * Puts a task into the Computer Space
     * @param task To put into the computer space
     * @throws RuntimeException
     */
    void put(Task<?> task) throws RuntimeException, RemoteException, InterruptedException;


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


    /**
     * This method should be invoked when a clients wants to publish a task to Space.
     * @param task The task that should be published
     * @throws java.rmi.RemoteException
     * @throws java.lang.InterruptedException
     */
    void publishTask(Task task) throws RemoteException, InterruptedException;


    Result getResult(String jobId) throws RemoteException, InterruptedException;

    
    HashMap getInfo() throws RemoteException;

}
