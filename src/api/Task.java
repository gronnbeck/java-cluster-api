package api;

import java.io.Serializable;

public interface Task<T> extends Serializable {
    /**
     * Executes the given task
     * @return The tasks result
     */
    Result<?> execute();

    /**
     * Returns the tasks identifier
     * @return an ID as String
     */
    String getTaskIdentifier();
    
    /**
     * 
     * @return true if the task has been cached
     */
    
    boolean getCached();
    
    /**
     * Sets the tasks isCached attribute.
     * This value is set to true if the task has been cached at a computer
     * 
     */
    void setCached(boolean b);

    /**
     * Sets the computer associated with this task.
     * 
     * @param computer
     */
    void setComputer(Computer computer);
    
    /**
     * If the task is considered very simple, the task can be executed on space. 
     * A task is considered very simple if the time it takes to solve it is less than or equal to the time it takes to marshal/unmarshal + latency.
     * @return true if the task is marked as simple
     */
    
    boolean isSimple();

    /**
     * Sets the task id
     * @param identifier The id represented as a String. The id must be unique.
     */
    void setTaskIdentifier(String identifier);

    /**
     * If a client wants to use branch & bound on the system. They need add a jobId reference to the shared object.
     * The id should be unique, if else, the system may return the wrong shared object.
     * @return a unique job id as a String
     */
    String getJobId();

    /**
     * advanced:
     * Sets the job id for this task. This method is used by the framework.
     * The method is available for developers who needs to modify the jobId for a task. However, it is not recommended to edit it.
     * @param jobId The job id of a task.
     */
    void setJobId(String jobId);

}
