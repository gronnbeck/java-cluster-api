package api;

import java.io.Serializable;
import java.util.EventListener;

public interface TaskListener extends EventListener, Serializable {

    void taskUpdate(TaskEvent<?> taskevent);

}
