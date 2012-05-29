package system;

import api.ContinuationTask;
import api.Shared;
import checkpointing.State;

import java.util.ArrayList;
import java.util.HashMap;

public class SpaceState implements State {

    ArrayList<Shared<?>> shareds;
    ArrayList<ContinuationTask> continuationTasks;

    public SpaceState(ArrayList<ContinuationTask> continuationTasks,
                      ArrayList<Shared<?>> shareds) {
        this.continuationTasks = continuationTasks;
        this.shareds = shareds;
    }

}
