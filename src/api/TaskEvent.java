package api;

import java.io.Serializable;

public interface TaskEvent<T> extends Serializable {

    String getOwnerId();

    String getJobId();

    String getType();

    T getValue();


}
