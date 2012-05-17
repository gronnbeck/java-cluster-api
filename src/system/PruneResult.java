package system;

import api.Result;

public class PruneResult implements Result {

    private String id;

    public PruneResult(String id) {
        this.id = id;
    }


    @Override
    public Object getTaskReturnValue() {
        return null;
    }

    @Override
    public void setTaskRunTime(long elapsed_time) {

    }

    @Override
    public long getTaskRunTime() {
        return 0;
    }

    @Override
    public String getTaskIdentifier() {
        return id;
    }
}
