package analysis;

import java.text.NumberFormat;
import java.util.ArrayList;

import bio.BioObject;

/**
 * Represents a prediction of a switch at a given residue position in a protein chain
 * 
 * @author Benjy 
 *
 */

public class Prediction extends BioObject {
	private static final long serialVersionUID = 1L;

	//list of descriptors
	private ArrayList<Descriptor> descriptors;
	
	//The protein's RCSB-PDB id
	private String protein;
	//The protein's RCSB-PDB chain id
	private char chain;
	//the index of the residue in the protein chain that this prediction is about
	private int residue;
	//propensity: the predicted likelihood that the residue was a switch, by whichever descriptors were used.
	private double propensity;
	//whether or not the residue was actually as switch (true == switch)
	private boolean isSwitch;
	
	/**
	 * Construct a new Prediction object
	 * @param protein: the protein's RCSB-PDB id
	 * @param chain: the protein's RCSB-PDB chain id
	 * @param residue: the index of the residue in the protein chain
	 * @param propensity: the predicted likelihood that the residue was a switch
	 * @param isSwitch: whether or not the residue was actually as switch (true == switch)
	 */
	public Prediction(String protein, char chain, int residue, double propensity, boolean isSwitch) {
		descriptors = new ArrayList<Descriptor>();
		this.protein = protein;
		this.chain = chain;
		this.residue = residue;
		this.propensity = propensity;
		this.isSwitch = isSwitch;
	}
	
	/**
	 * Add a descriptor(s) to the list of descriptors used to make the prediction
	 * @param desc: the descriptor to add
	 */
	public void addDescriptor(Descriptor... desc) {
		for(Descriptor descriptor: desc) { 
			if(!descriptors.contains(descriptor)) { 
				descriptors.add(descriptor);
			}
		}
	}
	
	/**
	 * Remove a descriptor to the list of descriptors used to make the prediction
	 * @param desc: the descriptor to remove
	 */
	public boolean removeDescriptor(Descriptor desc) { return descriptors.remove(desc); }
	
	/**
	 * Sets whether or not the residue is actually a switch
	 * @param isSwitch: is the residue a switch
	 */
	public void setSwitch(boolean isSwitch) { this.isSwitch = isSwitch; }
	
	/**
	 * Tells whether or not the residue this prediction is about is a switch
	 * @return true if the residue was actually a switch, else false
	 */
	public boolean isSwitch() { return isSwitch; }
	
	/**
	 * The probability (aka propensity) that the algorithm thought that
	 * the residue would be a switch (exhibit switch-like behavior)
	 * @return propensity
	 */
	public double propensity() { return propensity; }
	
	/**
	 * Obtain the prediction's accuracy value:
	 * a switch's accuracy is it's propensity
	 * a non-switch's accuracy is 1 - propensity]
	 * @return accuracy value
	 */
	public double accuracy() {
		if(isSwitch) {
			return propensity;
		} else {
			return 1 - propensity;
		}
	}
	
	/**
	 * Obtain the prediction's error value:
	 * Error is defined as 1 - accuracy
	 * @return error value
	 */
	public double error() { return 1 - accuracy(); }
	
	/**
	 * Obtain the residue's name and number
	 * @return a string identifying the prediction
	 */
	public String name() { return protein + ":" + chain + " [" + residue + "]"; }
	
	@Override
	public String impliedFileName() { return toString(); }
	
	/**
	 * 
	 */
	public String toString() {
		StringBuilder builder = new StringBuilder();
		NumberFormat formatter = NumberFormat.getInstance();
		formatter.setMinimumFractionDigits(5);
		formatter.setMaximumFractionDigits(5);
		String propensityStr = formatter.format(propensity);
		
		builder.append(name());
		builder.append(" ");
		builder.append(propensityStr);
		builder.append(" (");
		builder.append(isSwitch);
		builder.append(") [");
		for(Descriptor d: descriptors) { builder.append(d.toAbbrev() + ","); }
		builder.setLength(builder.length()-1);
		builder.append("]");
		return builder.toString();
	}
}
