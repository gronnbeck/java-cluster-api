package api;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface TaskEventHandler extends Remote {

    void propagateTaskEvent(TaskEvent<?> taskEvent) throws RemoteException;
}
