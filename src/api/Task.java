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
     * Sets the taskIdentifier.
     * @param identifier
     */
    void setTaskIdentifier(String identifier);
    
    

}
