package client;

import api.Result;
import api.Space;
import tasks.FibTask;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Fib2 {

    public static void main(String[] args) {
        if (args.length == 0) return;

        int NTH_NUMBER = 22;
        int port = 8888;
        String url = args[0];
        Registry registry = null;
        Space space = null;
        try {
            registry = LocateRegistry.getRegistry(url, port);
            space = (Space) registry.lookup(Space.SERVICE_NAME);
        } catch (RemoteException e) {
            System.out.println("Could not locate space registry. Exiting");
            System.exit(1);
        } catch (NotBoundException e) {
            System.out.println("Could not locate space in registry. Exiting");
            System.exit(1);
        }

        FibTask fibTask = new FibTask(NTH_NUMBER);
        
//        ClientInfoThread clt = new ClientInfoThread(space);
        
        long clientRunTime = System.nanoTime();
        try {
            space.publishTask(fibTask);
            Result result = space.getResult(fibTask.getJobId());
            System.out.println(NTH_NUMBER + "th number of fib: " + result.getTaskReturnValue());
            System.out.println("Client time: " + (System.nanoTime() - clientRunTime));
        } catch (RemoteException e) {
            System.out.println("The spaced you connected to crashed during execution.");
            System.exit(1);
        } catch (InterruptedException e) {
            System.out.println("Couldn't receive result, was interrupted");
            System.exit(1);
        }


//        System.out.println("Runtime: " + result.getTaskRunTime());


    }

}
