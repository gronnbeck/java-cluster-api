package system;

import api.TaskEvent;

public class TaskEventImpl implements TaskEvent<Object> {

	private static final long serialVersionUID = 6586230242298272950L;
	private String ownerId;	
    private String jobId;
    private String type;
    private Object value;

    protected TaskEventImpl(String ownerId, String jobId, String type, Object value) {
        this.ownerId = ownerId;
        this.jobId = jobId;
        this.type = type;
        this.value = value;
    }

    @Override
    public String getOwnerId() {
        return ownerId;
    }

    @Override
    public String getJobId() {
        return jobId;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public Object getValue() {
        return value;
    }
}
