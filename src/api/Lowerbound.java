package api;

import java.io.Serializable;

public interface Lowerbound<T> extends Serializable {

	/**
	 * Returns the lower bound for the task at hand.
	 * 
	 * @return
	 */
	T getLowerBound();

}
