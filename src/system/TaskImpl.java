package system;

import api.*;
import java.rmi.RemoteException;
import java.util.UUID;

public abstract class TaskImpl implements Task {

    private String taskId;
    private String jobId;
	private Computer computer;
    private Space owner;
	private boolean isCached;

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
	public void setShared(Shared shared ) throws RemoteException { computer.setShared( shared ); }

    @Override
    public  void  setComputer( Computer computer ) { this.computer = computer; }

    @Override
	public  void setCached(boolean bol){this.isCached = bol;}

    @Override
	public boolean getCached(){return this.isCached;}

    @Override
    public void setOwner(Space owner) { this.owner = owner; }

    @Override
    public Space getOwner() { return this.owner; }


    public ContinuationResult createContinuationResult(ContinuationTask continuationTask) {
        continuationTask.setTaskIdentifier(getTaskIdentifier());
        continuationTask.setJobId(getJobId());
        for (Task task : continuationTask.getTasks()) {
            task.setJobId(this.jobId);
        }
        return new ContinuationResult(continuationTask);
    }

}
