package api;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface SpaceProvider extends Remote {
    public static final String SERVICE_NAME = "SpaceProvider";

    /**
     * The client uses this method to publish a task to the java cluster
     * @param task The task the client wants to be solved
     * @return The result of this task
     * @throws RemoteException
     */
    public Result publishTask(Task task) throws RemoteException, InterruptedException;


    /**
     * A space that want to distribute tasks to Computers must use this
     * method to register itself to the clients SpaceProivder
     * @param space The space that want to register it self to the SpaceProivder
     * @throws RemoteException
     */
    public void registerSpace(Space space) throws RemoteException;

    /**
     * When a space no longer wants to compute tasks for a client it uses this method to deregister itself
     * @param space The space that does not want to do any more computations for this SpaceProvider
     * @throws RemoteException
     */
    public void deregisterSpace(Space space) throws RemoteException;
    
    
    /**
     * Show feedback to the user about the work being done
     * @return A string containing 
     * @throws RemoteException
     */
    public String getInfo() throws RemoteException;


}
