package tasks;

import api.*;
import system.ContinuationTaskImpl;
import system.PruneResult;
import system.TaskImpl;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;

public class TspContin extends ContinuationTaskImpl {


    private HashMap<String, Task> taskMap;
    private int currentCity;
    private boolean simple;

  

	public TspContin(int currentCity, ArrayList<Task> tasks) {
        super(tasks);
        this.currentCity = currentCity;
        results = new ArrayList<Result>();
        this.tasks = tasks;
        taskMap = new HashMap<String, Task>();
        for (Task t : tasks) {
            taskMap.put(t.getTaskIdentifier(), t);
        }
        simple = true;

    }

    @Override
    public synchronized ArrayList<Task> getTasks() {
        return (ArrayList<Task>)tasks.clone();       // this is bad... i think
    }

    @Override
    public synchronized boolean isReady() {
        return taskMap.size() == 0;
    }

    @Override
    public synchronized void ready(Result result) {
        if (taskMap.containsKey(result.getTaskIdentifier())) {
            if (!(result instanceof PruneResult)) {
                results.add(result);
            }

            taskMap.remove(result.getTaskIdentifier());
        }
    }

    @Override
    public Result<TspResult> execute() {

        Result minRes = new TspResult(new ArrayList<Integer>(), Double.MAX_VALUE, getTaskIdentifier());
        double minCost = Double.MAX_VALUE;

        for (Result currentResult : results) {
            if (minRes == null) {
                minRes = currentResult;
                minCost = ((Pair<Double, ArrayList<Integer>>)minRes.getTaskReturnValue()).getLeft();
                continue;
            }
            double currentCost =  ((Pair<Double, ArrayList<Integer>>)currentResult.getTaskReturnValue()).getLeft();

            if (currentCost < minCost) {
                minRes = currentResult;
                minCost = currentCost;
            }

        }

        ArrayList<Integer> path = ((Pair<Double, ArrayList<Integer>>) minRes.getTaskReturnValue()).getRight();

        path.add(0, currentCity);

        Result result = new TspResult(path, minCost, getTaskIdentifier());
        //Hmm this one will probably be overwritten. TODO look into it
        result.setTaskRunTime(getTaskRunTime());
        return result;

    }

	@Override
	public boolean isSimple() {
		return this.simple;
	}



}
