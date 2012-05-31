package system;

import api.TaskEvent;
import api.TaskListener;

public class TaskListenerImpl implements TaskListener {

    @Override
    public void taskUpdate(TaskEvent taskevent) {
        System.out.println(taskevent.getType());
    }

}
