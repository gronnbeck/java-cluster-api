package tasks;

import api.ContinuationTask;
import api.Result;
import api.Task;
import api.Task2Space;
import java.rmi.RemoteException;
import java.util.ArrayList;
import system.ContinuationTaskImpl;

public class FibContin extends ContinuationTaskImpl {

    private int counter;
    private boolean simple;

    public FibContin(ArrayList<Task> tasks) {
        super(tasks);
        counter = tasks.size();
        this.simple = true;

    }

    @Override
    public ArrayList<Task> getTasks() {
        return tasks;
    }

    @Override
    public synchronized boolean isReady() {
        return counter == 0;
    }

    @Override
    public synchronized void ready(Result result) {
        results.add(result);
        counter--;
    }


    @Override
    public Result<Integer> execute() {
        int sum = 0;
        for (Result<Integer> res : results) {
            sum += res.getTaskReturnValue();
        }
        // TODO One should not have to send the jobId with the init.
        Result<Integer> result = new FibResult(sum, getTaskIdentifier(), getJobId());
        result.setTaskRunTime(getTaskRunTime());
        result.setOwner(getOwner());

        return result;
    }

	@Override
	public boolean isSimple() {
		return this.simple;
	}


}
