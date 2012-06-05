package system;

import api.ContinuationTask;

public class ContinuationResult extends ResultImpl<ContinuationTask> {


	private static final long serialVersionUID = 6093065798339632117L;
	private ContinuationTask task;
//    private long elapsedTime;

    protected ContinuationResult(ContinuationTask task) {
        this.task = task;

    }

    @Override
    public ContinuationTask getTaskReturnValue() {
        return task;
    }

    @Override
    public void setTaskRunTime(long elapsed_time) {
        task.setTaskRunTime(elapsed_time);
    }

    @Override
    public long getTaskRunTime() {
        return task.getTaskRunTime();
    }
}
