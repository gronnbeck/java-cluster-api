package test;

import java.io.Serializable;
import java.util.ArrayList;
import api.Result;
import api.Task;
import system.TaskImpl;

public class TestTask extends TaskImpl implements Serializable{

	private static final long serialVersionUID = -712612492727680841L;
	private int level;
	private int splitFactor;
	
	public TestTask(int depth, int splitFactor) {
		super();
		this.level = depth;
		this.splitFactor = splitFactor;
	}
	
	@Override
	public Result<?> execute() {
		
		ArrayList<Task> subtasks = new ArrayList<Task>();
		System.out.println("Executing task with priority: " + this.getPriority());
		
		if (level > 0) {
			
			for (int i = 0; i < splitFactor; i++) {
				Task<?> t;
				
				t = new TestTask(this.level - 1, this.splitFactor);
				t.setPriority(i);
				subtasks.add(t);
			}
			
			
			return createContinuationResult(new TestContinuationTask(subtasks));
		}
		else {
			
			int counter = 0;
			
			//Count to a million
			for (int i = 0; i < 10.5E7; i++) {
				counter++;
			}
			
			return createResult(new TestResult());
		}
	}

}
