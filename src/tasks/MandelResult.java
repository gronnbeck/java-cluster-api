package tasks;

import system.ResultImpl;

public class MandelResult extends ResultImpl{
	
	private int[][] count;

    /**
     * An object to store the result of solving a MandelbrotSet Task.
     * @param count the result of solving a mandelbrotset task
     * @param id the id corresponding to the task this result belongs to
     */
	public MandelResult(int[][] count, String id) {
		super(id);
		this.count = count;
	}
	

	@Override
	public Object getTaskReturnValue() {
		return count;
	}

}
