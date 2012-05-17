package tasks;

import java.io.Serializable;

import api.Shared;

public class DoubleShared implements Shared<Double>, Serializable{
	private Shared shared;
    private double val;


    public DoubleShared(double val) {
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
