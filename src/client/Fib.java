package client;

import api.Result;
import api.Space;
import tasks.*;
import java.rmi.registry.*;

public class Fib {

    public static void main(String[] args) throws Exception {
        if (args.length == 0) return;

        int NTH_NUMBER = 20;
        int port = 8888;
        String url = args[0];
        Registry registry = LocateRegistry.getRegistry(url, port);

        Space space = (Space) registry.lookup(Space.SERVICE_NAME);
        FibTask fibTask = new FibTask(NTH_NUMBER);
        
//        ClientInfoThread clt = new ClientInfoThread(space);
        
        long clientRunTime = System.nanoTime();
        space.publishTask(fibTask);
        Result result = space.getResult(fibTask.getJobId());
        System.out.println(NTH_NUMBER + "th number of fib: " + result.getTaskReturnValue());
//        System.out.println("Runtime: " + result.getTaskRunTime());
//        System.out.println("Client time: " + (System.nanoTime() - clientRunTime));

    }

}
