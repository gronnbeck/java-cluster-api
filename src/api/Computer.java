package api;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface Computer extends Remote, Serializable, TaskEventHandler {
    String SERVICE_NAME = "computer";

    /**
     * Executes its given task
     * @param task the task to be executed
     */
    Result<?> execute(Task<?> task) throws RemoteException;

    /**
     * Stops the computer
     * @throws java.rmi.RemoteException
     */
    void stop() throws RemoteException;
    
    /**
     * Returns the shared object for this type of task.
     * @return
     */
    Shared getShared(String id) throws RemoteException;
	
	/**
	 * Sets the shared object of this computer.
	 * This value is used to prune the search tree. 
	 * @param shared
	 */
	void setShared(Shared<?> shared) throws RemoteException;
	

    // TODO: Add java doc comments
    boolean hasCached() throws RemoteException;
    Result executeCachedTask() throws RemoteException;
    public void registerComputer(Computer cp) throws RemoteException;
    public void deregisterComputer(Computer cp) throws RemoteException;
    public List<Computer> getComputers() throws RemoteException;
    public int getTaskQSize() throws RemoteException;
    public boolean canSteal() throws RemoteException;
    public boolean want2Steal() throws RemoteException;
    public Task stealTask() throws RemoteException, InterruptedException;
    void addTask(Task task) throws RemoteException;


}
