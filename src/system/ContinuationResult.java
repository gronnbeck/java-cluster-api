package system;


import api.ContinuationTask;
import api.Result;

public class ContinuationResult implements Result<ContinuationTask> {


    private ContinuationTask task;
    private String id;
    private long elapsedTime;

    protected ContinuationResult(ContinuationTask task) {
        this.task = task;
        this.id = task.getTaskIdentifier();
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

    @Override
    public String getTaskIdentifier() {
        return id;
    }
}
