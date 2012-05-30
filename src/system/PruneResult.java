package system;

import api.Result;

public class PruneResult extends ResultImpl {

    private String id;

    public PruneResult(String id, String jobId) {
        super(id,jobId);
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
