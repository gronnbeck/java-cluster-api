package api;

import system.SpaceWorkStealerProxy;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.List;

public interface Space extends Remote, Computer2Space, Task2Space, TaskEventHandler {
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

    /**
     * Register to this that a space is available
     * @param space an other space that is available
     * @throws RemoteException
     */
    void registerSpace(Space space) throws RemoteException;

    /**
     * When a space disconnects it should in- or directly tell the other spaces
     * that it has shutdown.
     * @param space the space that has disconnected
     * @throws RemoteException
     */
    void deregisterSpace(Space space) throws RemoteException;


    /**
     * Returns the unique id of a Space
     * @return the unique id of a Space as a string
     * @throws RemoteException
     */
    String getId() throws RemoteException;

    /**
     * Gives the caller a cloned list of the computers running on the space
     * @return returns a list of computers (as computer proxies)
     * @throws RemoteException
     */
    List<Computer> getComputers() throws RemoteException;


    TaskEvent nextEvent(String jobId) throws RemoteException, InterruptedException;


    // TODO add javadoc comments
    Result getResult(String jobId) throws RemoteException, InterruptedException;

    
    HashMap getInfo() throws RemoteException;

}
