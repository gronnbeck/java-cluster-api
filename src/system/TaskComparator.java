package system;

import java.util.Comparator;
import api.Task;

public class TaskComparator implements Comparator<Task<?>>{

    // SINGLETON!
    private TaskComparator() { }

    private static TaskComparator tc;

    public static Comparator getSingleton() {
        return tc == null ? tc = new TaskComparator() : tc;
    }

	@Override
	public int compare(Task<?> o1, Task<?> o2) {
		
		if (o1.getPriority() > o2.getPriority())
			return -1;
		else if (o1.getPriority() < o2.getPriority())
			return 1;
		else
			return 0;
	}

}
