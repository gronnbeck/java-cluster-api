package client;

import api.Result;
import api.Space;
import api.SpaceProvider;
import tasks.*;
import java.rmi.registry.*;

public class Fib {

    public static void main(String[] args) throws Exception {
        if (args.length == 0) return;

        int NTH_NUMBER = 20;
        int port = 8887;
        String url = args[0];
        Registry registry = LocateRegistry.getRegistry(url, port);

        SpaceProvider space = (SpaceProvider) registry.lookup(SpaceProvider.SERVICE_NAME);
        FibTask fibTask = new FibTask(NTH_NUMBER);

        long clientRunTime = System.nanoTime();
        Result result = space.publishTask(fibTask);
        System.out.println(NTH_NUMBER + "th number of fib: " + result.getTaskReturnValue());
        System.out.println("Runtime: " + result.getTaskRunTime());
        System.out.println("Client time: " + (System.nanoTime() - clientRunTime));

    }

}
