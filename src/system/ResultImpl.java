package system;

import api.Result;
import api.Space;

public abstract class ResultImpl implements Result{
	
	protected long taskTime;
	protected String id;
    private String jobId;
    private Space owner;

    public ResultImpl(String id, String jobId) {
		this.id = id;
        this.jobId = jobId;
    }

    @Override
    public void setTaskRunTime(long elapsed_time) {
        taskTime = elapsed_time;
    }

	@Override
	public long getTaskRunTime() {
		return taskTime;
	}

	@Override
	public String getTaskIdentifier() {
		return id;
	}

    @Override
    public String getJobId() {
        return jobId;
    }

    @Override
    public void setOwner(Space owner) { this.owner = owner; }

    @Override
    public Space getOwner() { return this.owner; }
	
}
