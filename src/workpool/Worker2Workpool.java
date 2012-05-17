package workpool;

import api.Result;

public interface Worker2Workpool {

    void addReadyWorker(Worker worker);

    void returnResult(Result result);

    void registerWorker(Worker worker);

}
