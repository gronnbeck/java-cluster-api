package api;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Task2Space extends Remote {

    /**
     * Register a continuation to the space
     * @param continuation the continuation to register
     * @throws java.rmi.RemoteException
     */
    void registerContin(ContinuationTask continuation) throws RemoteException;

    /**
     * Register a result of this task to the computer space
     * @param result the result of this task you want to regsiter to space
     * @throws java.rmi.RemoteException
     * @throws InterruptedException
     */
    void putResult(Result result) throws RemoteException, InterruptedException;

}
