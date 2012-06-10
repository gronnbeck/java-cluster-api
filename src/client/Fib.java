package client;

import api.Space;
import system.JobInfo;
import tasks.*;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.*;

public class Fib {

    public static void main(String[] args) {
        if (args.length == 0) return;

        int NTH_NUMBER = 20;
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
        long clientRunTime = System.nanoTime();
        
        try {
            space.publishTask(fibTask);
            FibResult result = (FibResult)space.getResult(fibTask.getJobId());
            System.out.println(NTH_NUMBER + "th number of fib: " + result.getTaskReturnValue());
            System.out.println("Client time: " + (System.nanoTime() - clientRunTime));
            
            JobInfo j = space.getJobInfo(fibTask.getJobId(), true);
            System.out.println(j.toString());
            
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
