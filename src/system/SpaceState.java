package system;

import api.ContinuationTask;
import api.Shared;
import checkpointing.State;

import java.util.ArrayList;
import java.util.HashMap;

public class SpaceState implements State {

    ArrayList<Shared<?>> shareds;
    ArrayList<ContinuationTask> continuationTasks;
    String spaceid;

    public SpaceState(ArrayList<ContinuationTask> continuationTasks,
                      ArrayList<Shared<?>> shareds, String spaceid) {
        this.continuationTasks = continuationTasks;
        this.shareds = shareds;
        this.spaceid = spaceid;
    }

}
