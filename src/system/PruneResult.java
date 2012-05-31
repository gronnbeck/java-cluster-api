package system;

import api.Result;

public class PruneResult extends ResultImpl {

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
}

