package api;

import system.TaskImpl;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Computer extends Remote, Serializable {
    String SERVICE_NAME = "computer";

    /**
     * Executes its given task
     * @param task the task to be executed
     */
    void execute(Task task) throws RemoteException;

    /**
     * Stops the computer
     * @throws java.rmi.RemoteException
     */
    void stop() throws RemoteException;
    
    /**
     * Returns the shared object for this type of task.
     * @return
     */

	Object getShared() throws RemoteException;
	
	/**
	 * Sets the shared object of this computer.
	 * This value is used to prune the search tree. 
	 * @param shared
	 */
	void setShared(Shared shared) throws RemoteException;
	
	
    Space getSpace() throws RemoteException;


	void setSpace(Space space) throws RemoteException ;
}
