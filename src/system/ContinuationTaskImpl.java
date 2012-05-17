package system;

import api.ContinuationTask;
import api.Result;
import api.Task;

import java.util.ArrayList;

public abstract class ContinuationTaskImpl extends TaskImpl implements ContinuationTask {

    private long elapsedTime;
    protected ArrayList<Result> results;
    protected ArrayList<Task> tasks;

    public ContinuationTaskImpl(ArrayList<Task> tasks) {
        results = new ArrayList<Result>();
        this.tasks = tasks;
        elapsedTime = 0;
    }

    @Override
    public synchronized void setTaskRunTime(long elapsedTime) {
        this.elapsedTime = elapsedTime;
    }

    @Override
    public synchronized long getTaskRunTime() {
        long subRuntime = 0;
        for (Result result : results) {
          subRuntime += result.getTaskRunTime();
        }
        return elapsedTime + subRuntime;
    }

}
