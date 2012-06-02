package system;

import api.*;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.UUID;

public abstract class TaskImpl implements Task {

    private String taskId;
    private String jobId;
	private Computer computer;
    private String ownerId;
	private boolean isCached;
    private int priority;

    public TaskImpl() {
        UUID uuid = UUID.randomUUID();
        this.jobId = uuid.toString();
        this.taskId = this.toString() +  uuid.toString();
    }

    @Override
    public void setJobId(String jobId) { this.jobId = jobId; }

    @Override
    public String getJobId() { return jobId; }

    @Override
    public String getTaskIdentifier() { return taskId; }

    @Override
    public void setTaskIdentifier(String id) { this.taskId = id;}

    @Override
	abstract public Result<?> execute();

    @Override
 	public Shared getShared() throws RemoteException { return computer.getShared(jobId); }

    @Override
	public void setShared(Shared shared) throws RemoteException { computer.setShared(shared); }

    @Override
    public  void  setComputer( Computer computer ) { this.computer = computer; }

    @Override
	public  void setCached(boolean bol){this.isCached = bol;}

    @Override
	public boolean getCached(){return this.isCached;}

    @Override
    public void setOwnerId(String owner) { this.ownerId = owner; }

    @Override
    public String getOwnerId() { return this.ownerId; }

    @Override
    public int getPriority()  { return this.priority; }

    @Override
    public void setPriority(int priority) { this.priority = priority; }

    @Override
    public void propagateTaskEvent(TaskEvent taskEvent) throws RemoteException {
        computer.propagateTaskEvent(taskEvent);
    }


    @Override
    public ContinuationResult createContinuationResult(ContinuationTask continuationTask) {
        continuationTask.setTaskIdentifier(getTaskIdentifier());
        continuationTask.setJobId(getJobId());
        continuationTask.setOwnerId(getOwnerId());
        for (Task task : continuationTask.getTasks()) {
            task.setJobId(this.jobId);
            task.setOwnerId(getOwnerId());
        }
        ContinuationResult cr = new ContinuationResult(continuationTask);
        cr.setTaskIdentifier(getTaskIdentifier());
        cr.setOwnerId(getOwnerId());
        cr.setJobId(getJobId());
        return cr;
    }

    @Override
    public Result createResult(Result aResult) {
        aResult.setTaskIdentifier(getTaskIdentifier());
        aResult.setJobId(getJobId());
        aResult.setOwnerId(getOwnerId());
        return aResult;
    }


    protected TaskEvent createTaskEvent(String type, Object value) {
        return new TaskEventImpl(getOwnerId(), getJobId(), type, value);
    }

}
