package tasks;
import api.Shared;
import system.SharedImpl;

public class DoubleShared extends SharedImpl<Double> {
    private double val;

    public DoubleShared(double val, String jobId) {
        super(jobId);
        this.val = val;
    }

	@Override
	public boolean isNewerThan(Shared shared) {
        if (shared == null) return true;
        return val > (Double) shared.getValue();
    }

    @Override
    public Double getValue() {
        return val;
    }

}
