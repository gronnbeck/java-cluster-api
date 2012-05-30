package system;


import api.ContinuationTask;
import api.Result;

public class ContinuationResult extends ResultImpl {


    private ContinuationTask task;
    private long elapsedTime;

    protected ContinuationResult(ContinuationTask task) {
        super(task.getTaskIdentifier(), task.getJobId());
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
