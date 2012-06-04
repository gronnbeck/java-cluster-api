package test;

import java.util.ArrayList;
import java.util.HashMap;

import api.Result;
import api.Task;
import system.ContinuationTaskImpl;
import system.PruneResult;

public class TestContinuationTask extends ContinuationTaskImpl{

	private ArrayList<Task> tasks;
    private HashMap<String, Task> taskMap;

	
	public TestContinuationTask(ArrayList<Task> tasks) {
		super(tasks);
		this.tasks = tasks;
		taskMap = new HashMap<String, Task>();
		
        for (Task t : tasks) {
            taskMap.put(t.getTaskIdentifier(), t);
        }
        simple = true;
	}

	@Override
	public ArrayList<Task> getTasks() {
		return this.tasks;
	}

	@Override
	public boolean isReady() {
        return taskMap.size() == 0;
	}

	@Override
	public void ready(Result<?> result) {
		if (taskMap.containsKey(result.getTaskIdentifier())) {
            results.add(result);
            taskMap.remove(result.getTaskIdentifier());
        }
	}

	@Override
	public Result<?> execute() {
		return createResult(new TestResult());
	}

}
