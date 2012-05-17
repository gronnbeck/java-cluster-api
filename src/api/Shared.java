package api;

import java.io.Serializable;
import java.rmi.RemoteException;

public interface Shared<T> extends Serializable {
	boolean isNewerThan(Shared shared) throws RemoteException;
    T getValue();

}
