package system;

import api.ContinuationTask;
import api.Result;
import api.Task;

import java.util.ArrayList;
import java.util.List;

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
    public List<Task> markAsCached(int n) {
        List<Task> cached = new ArrayList<Task>();
        for (int i = 1; i <= n; i ++) {
            Task task = tasks.get(i-1);
            task.setCached(true);
            cached.add(task);
        }
        return cached;
    }

    @Override
    public List<Task> getCachedTasks(){
        List<Task> cached = new ArrayList<Task>();
        for (Task task : tasks) {
            if (task.getCached()) cached.add(task);
        }
        return cached;
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
