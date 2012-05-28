package tasks;

import java.rmi.RemoteException;
import java.util.ArrayList;

import system.ContinuationTaskImpl;

import api.ContinuationTask;
import api.Result;
import api.Task;
import api.Task2Space;

public class MandelContin extends ContinuationTaskImpl {
	
	private ArrayList<Task> tasks;
	private ArrayList<Result> results;
	private int counter;
	private int[][] count;
	private long runTime;
	private boolean simple;


    /**
     * The task that should be executed after all the subtasks of a MandelBrot Set task
     * has completed.
     * @param tasks the tasks that needs to be completed before this should run
     */
	public MandelContin(ArrayList<Task> tasks){
        super(tasks);
		this.tasks = tasks;
		this.simple = false;
		counter = tasks.size();
		results = new ArrayList<Result>();
		simple = true;
	}

	

	@Override
	public Result<MandelResult> execute() {
		int len = results.get(0).getTaskIdentifier().length();
		int n =512; //Size of the resulting array
		if (len==3){
			n = 1024;
		}
		
		count = new int[n][n];
		for (Result mandelSubResult : results) {
			String id = mandelSubResult.getTaskIdentifier();
			int pos = Integer.parseInt(id.substring(id.length()-1));
			int curX =0;
			int curY =0;
			if (pos ==1) curX = n/2;
			if (pos ==2) curY = n/2;
			if (pos ==3) {curX=n/2; curY = n/2;}
			
			int[][]temp = (int[][])mandelSubResult.getTaskReturnValue();
			for (int y= 0; y < temp.length; y++) {//X-coordinates
				for (int x = 0; x < temp.length; x++) { // Y-Coordinates
					count[curY][curX] = temp[y][x];
					curX++;
				}
				curX =curX-(n/2);
				curY++;
			}
		}
		Result res = new MandelResult(count, getTaskIdentifier());
		res.setTaskRunTime(getTaskRunTime());
		return res;
	}

	@Override
	public ArrayList<Task> getTasks() {
		return tasks;
	}

	@Override
	public boolean isReady() {
		return counter ==0;
	}

	@Override
	public void ready(Result result) {
		results.add(result);
		counter--;
		
	}



	@Override
	public boolean isSimple() {
		return this.simple;
	}


}
