package analysis.stats;

import java.io.Serializable;

import util.BaseTools;

/**
 * SuperClass for all objects that represents statistics rather than biological objects.
 * 
 * @author Benjy Strauss
 *
 */

public abstract class StatsObject extends BaseTools implements Serializable {
	private static final long serialVersionUID = 1L;
	
	protected StatsObject() { }
	
	public String toString() {
		return "StatsObject (" + super.toString() + ")";
	}
}
