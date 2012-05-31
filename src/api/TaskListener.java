package api;

import java.io.Serializable;
import java.rmi.Remote;
import java.util.EventListener;

public interface TaskListener extends EventListener, Serializable {

    void taskUpdate(TaskEvent taskevent);

}
