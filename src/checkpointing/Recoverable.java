package checkpointing;


public interface Recoverable {

    State getState();

    boolean stateChanged();

    void recover();

}
