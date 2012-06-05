package system;

import api.Shared;

public abstract class SharedImpl<T> implements Shared<T> {

	private static final long serialVersionUID = 6746454281647770388L;
	private String jobId;

    public SharedImpl(String jobId) {
        this.jobId = jobId;
    }

    @Override
    public String getJobId(){
        return jobId;
    }


}
