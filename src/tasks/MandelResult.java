package tasks;


import api.Result;

public class MandelResult implements Result{
	
	private int[][] count;
	private String id;
	private long elapsedTime;

    /**
     * An object to store the result of solving a MandelbrotSet Task.
     * @param count the result of solving a mandelbrotset task
     * @param id the id corresponding to the task this result belongs to
     */
	public MandelResult(int[][] count,String id) {
		this.elapsedTime = 0;
		this.count = count;
		this.id = id;
	}
	

	@Override
	public Object getTaskReturnValue() {
		return count;
	}

	@Override
	public void setTaskRunTime(long elapsed_time) {
		this.elapsedTime = elapsed_time;
		
	}

	@Override
	public long getTaskRunTime() {
		return elapsedTime;
	}

	@Override
	public String getTaskIdentifier() {
		return id;
	}

}
