package tasks;

import api.ContinuationTask;
import api.Result;
import api.Task;
import api.Task2Space;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;

import system.ContinuationResult;
import system.TaskImpl;

public class FibTask extends TaskImpl {


    private int num;
    private String id;
    private boolean simple;

    /**
     * Computes the n-th number of the Fibonacci sequence
     * @param num the n-th number of the sequence you want to be computed
     */
    public FibTask(int num) {
        this.num = num;
        UUID uuid = UUID.randomUUID();
        this.id = this.toString() +  uuid.toString();
        this.simple = false;
        
    }

    @Override
    public Result execute() {
        if (num < 2) {
            return new FibResult(num, getTaskIdentifier());
        }
        Task fib1 = new FibTask(num - 1);
        Task fib2 = new FibTask(num - 2);
        ArrayList<Task> tasks = new ArrayList<Task>();
        tasks.add(fib1);
        tasks.add(fib2);
        
        return new ContinuationResult(new FibContin(tasks, getTaskIdentifier()));
    }

    @Override
    public String getTaskIdentifier() {      // Don't know if it necessary to use MD5 as identifier...
        return id;
    }

	@Override
	public boolean isSimple() {
		return this.simple;
	}

}
