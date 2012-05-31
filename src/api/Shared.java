package api;

import java.io.Serializable;
import java.rmi.RemoteException;

public interface Shared<T> extends Serializable {

    // TODO Add javadoc comments
	boolean isNewerThan(Shared<T> shared) throws RemoteException;
    String getJobId() throws RemoteException;
	T getValue();

}
