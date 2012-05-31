package system;

import api.Result;

public abstract class ResultImpl implements Result{
	
	protected long taskTime;
	protected String id;
    private String jobId;
    private String ownerId;

    @Override
    public void setTaskRunTime(long elapsed_time) { taskTime = elapsed_time; }

	@Override
	public long getTaskRunTime() { return taskTime; }


	@Override
	public String getTaskIdentifier() { return id; }

    @Override
    public void setTaskIdentifier(String taskIdentifier) { this.id = taskIdentifier; }

    @Override
    public String getJobId() { return jobId; }

    @Override
    public void setJobId(String jobId) { this.jobId = jobId; }

    @Override
    public void setOwnerId(String ownerId) { this.ownerId = ownerId; }

    @Override
    public String getOwnerId() { return this.ownerId; }


	
}
