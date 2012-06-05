package tasks;

import api.Result;
import api.Task;
import java.util.ArrayList;
import system.TaskImpl;

public class FibTask extends TaskImpl {

	private static final long serialVersionUID = 4561161664277175480L;
	private int num;
    private boolean simple;

    /**
     * Computes the n-th number of the Fibonacci sequence
     * @param num the n-th number of the sequence you want to be computed
     */
    public FibTask(int num) {
        super();
        this.num = num;
        this.simple = false;
        
    }

    @Override
    public Result<?> execute() {
        if (num < 2) {
            return createResult(new FibResult(num));
        }
        FibTask fib1 = new FibTask(num - 1);
        FibTask fib2 = new FibTask(num - 2);
        ArrayList<Task<?>> tasks = new ArrayList<Task<?>>();
        tasks.add(fib1);
        tasks.add(fib2);

        return createContinuationResult(new FibContin(tasks));
    }

	@Override
	public boolean isSimple() {
		return this.simple;
	}

}
