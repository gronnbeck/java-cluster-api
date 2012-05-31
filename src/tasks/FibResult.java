package tasks;

import system.ResultImpl;

public class FibResult extends ResultImpl {

    private int result;
    
    public FibResult(int result){
        this.result = result;
    }
    @Override
    public Object getTaskReturnValue() {
        return result;
    }


}
