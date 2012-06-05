package system;

import api.TaskEvent;
import api.TaskListener;

public class TaskListenerImpl implements TaskListener {

	private static final long serialVersionUID = 6966179781258697996L;

	@Override
    public void taskUpdate(TaskEvent<?> taskevent) {
        System.out.println(taskevent.getType());
    }

}
