package analysis;

import java.text.NumberFormat;

import bio.BioObject;
import bio.exceptions.DataValueOutOfRangeException;

/**
 * This class represents how many residues are predicted to be switches vs how many are not
 * for various propensity thresholds
 * 
 * @author BenjyStrauss
 * This is only used by the bioUI.stats class
 */

public class PropensityRecord extends BioObject {
	private static final long serialVersionUID = 1L;
	
	public static final int FALSE = 0;
	public static final int TRUE = 1;
	
	//The name of the record
	private String name;
	//the array size, stored in case needed later
	private int intervals;
	//the actual data, how many predictions were true vs false
	private int tally[][] = new int[2][];
	//not sure what this does anymore, but it's necessary: don't delete it
	private double increments;
	
	/**
	 * 
	 * @param intervals: the number of propensity thresholds
	 */
	public PropensityRecord(int intervals) {
		this(null, intervals);
	}
	
	/**
	 * 
	 * @param name: what to name the record
	 * @param intervals: the number of propensity thresholds
	 */
	public PropensityRecord(String name, int intervals) {
		this.name = name;
		this.intervals = intervals;
		tally[0] = new int[intervals];
		tally[1] = new int[intervals];
		double temp = intervals;
		increments = 1 / temp;
	}
	
	/**
	 * Getter method for the number of intervals contained by the PropensityRecord
	 * @return: number of intervals contained by the PropensityRecord
	 */
	public int intervals() { return intervals; }
	
	@Override
	public String impliedFileName() { return name; }
	
	/**
	 * Load the PropensityRecord with data from a Prediction object
	 * @param pred: the Prediction object to load data from
	 */
	public void processPrediction(Prediction pred) {
		if(pred.propensity() < 0) { throw new DataValueOutOfRangeException(); }
		if(pred.propensity() > 1) { throw new DataValueOutOfRangeException(); }
		
		for(int mult = 0; mult < intervals; ++mult) {
			if(mult * increments >= pred.propensity()) {
				int isSwitch = (pred.isSwitch()) ? 1 : 0;
				++tally[isSwitch][mult];
				return;
			}
		}
		
		throw new DataValueOutOfRangeException();
	}
	
	public String toString() {
		StringBuilder retValBuilder = new StringBuilder();
		retValBuilder.append(name+ "\n");
		
		NumberFormat formatter = NumberFormat.getInstance();
		formatter.setMinimumFractionDigits(3);
		formatter.setMaximumFractionDigits(3);
		
		for(int index = 0; index < intervals; ++index) {
			StringBuilder lineBuilder = new StringBuilder();
			lineBuilder.append("> ");
			double mult = index;
			lineBuilder.append(formatter.format(mult * increments));
			lineBuilder.append(" to ");
			lineBuilder.append(formatter.format((mult+1) * increments));
			lineBuilder.append(":\ttrue: ");
			lineBuilder.append(tally[TRUE][index]);
			
			if(tally[TRUE][index] < 10) { lineBuilder.append("\t"); }
			
			lineBuilder.append("\t| false: ");
			lineBuilder.append(tally[FALSE][index]);
			retValBuilder.append(lineBuilder.toString() + "\n");
		}
		
		return retValBuilder.toString();
	}
}
