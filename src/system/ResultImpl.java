package system;

import api.Result;

public abstract class ResultImpl implements Result{
	
	protected long taskTime;
	protected String id;
	
	public ResultImpl(String id) {
		this.id = id;
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
	
}
