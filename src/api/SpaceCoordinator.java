package api;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface SpaceCoordinator extends Remote {
    public static final String SERVICE_NAME = "SpaceCoordinator";

    /**
     * A space that want to distribute tasks to Computers must use this
     * method to register itself to the clients SpaceCoordinator. The SpaceCoordinator then
     * tells the Space which spaces that has already connected to this provider, and tells the
     * other spaces that a space has connected
     * @param space The space that want to register it self to the SpaceProivder
     * @throws RemoteException
     */
    public void registerSpace(Space space) throws RemoteException;

    /**
     * When a space no longer wants to compute tasks for a client it uses this method to deregister itself
     * @param space The space that does not want to do any more computations for this SpaceCoordinator
     * @throws RemoteException
     */
    public void deregisterSpace(Space space) throws RemoteException;
    



}
