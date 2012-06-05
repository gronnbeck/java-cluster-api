package system;

import api.ContinuationTask;
import api.Shared;
import checkpointing.State;
import java.util.ArrayList;

public class SpaceState implements State {

	private static final long serialVersionUID = -3064857360703568177L;
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
