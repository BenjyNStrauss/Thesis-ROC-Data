package util;

import java.io.Serializable;

/**
 * Copied from Trigamon Mithril game Engine
 * @author Benjy Stauss
 *
 * @param <X>
 * @param <Y>
 */

public class Pair<X, Y> implements Serializable {
	private static final long serialVersionUID = 1L;
	public X x;
	public Y y;
	
	public Pair() { }
	
	public Pair(X x, Y y) {
		this.x = x;
		this.y = y;
	}
	
	/**
	 * Clones the pair, but not the data
	 */
	public Pair<X, Y> clone() {
		Pair<X, Y> retVal = new Pair<X, Y>();
		retVal.x = x;
		retVal.y = y;
		return retVal;
	}
	
	public boolean equals(Pair<?, ?> other) {
		if(x.equals(other.x) && y.equals(other.y)) {
			return true;
		} else {
			return false;
		}
	}
	
	public String toString() { return x + "," + y; }
}