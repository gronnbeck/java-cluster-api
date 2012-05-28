package system;

import api.Shared;

import java.util.concurrent.ConcurrentHashMap;

public class SharedMap {

    String id;
    ConcurrentHashMap<String, Shared> sharedMap;

    public SharedMap(String id) {
        this.sharedMap = new ConcurrentHashMap<String, Shared>();
        this.id = id;

    }
}
