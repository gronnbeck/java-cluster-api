package api;

import java.util.ArrayList;

public interface ContinuationTask extends Task {
    /**
     * Get all the tasks needed to solve this problem
     * @return a lists of tasks needed for this continuation to execute
     */
    ArrayList<Task> getTasks();

    /**
     * Used to control if the task is ready to execute
     * @return true if all the subtasks has been executed, false if not
     */
    boolean isReady();

    /**
     * Save results of solved subtasks
     * @param result The result of a subtask
     */
    void ready(Result<?> result);


    /**
     * Log the run time of an executed task
     * @param elapsedTime how much time that elapsed for a task to complete. In ns
     */
    void setTaskRunTime(long elapsedTime);

    /**
     * Returns the logged run time
     * @return The run time in ns as a long
     */
    long getTaskRunTime();

}
