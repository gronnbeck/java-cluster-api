package system;

import api.Shared;

public abstract class SharedImpl<T> implements Shared {

    private String jobId;

    public SharedImpl(String jobId) {
        this.jobId = jobId;
    }

    @Override
    public String getJobId(){
        return jobId;
    }


}
