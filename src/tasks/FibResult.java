package tasks;

import api.Result;
import api.Task;

public class FibResult implements Result {

    private int result;
    private String id;
    private long elapsedTime;
    public FibResult(int result, String id){
        this.result = result;
        this.id = id;
        this.elapsedTime = 0;
    }
    @Override
    public Object getTaskReturnValue() {
        return result;
    }

    @Override
    public void setTaskRunTime(long elapsed_time) {
        elapsedTime = elapsed_time;
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
