package api;

import java.io.Serializable;
import java.rmi.RemoteException;

public interface Shared<T> extends Serializable {

	/**
	 * Checks to see if this object is newer than the shared object passed as argument
	 * @param thisShared
	 * @return
	 * @throws RemoteException
	 */
	boolean isNewerThan(Shared<?> thisShared) throws RemoteException;
	
	/**
	 * 
	 * @return Returns the jobid which the shared object belongs to
	 * @throws RemoteException
	 */
    String getJobId() throws RemoteException;
    
    /**
     * 
     * @return Returns the value held by the shared object
     */
	T getValue();

}
