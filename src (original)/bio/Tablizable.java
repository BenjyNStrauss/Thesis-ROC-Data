package bio;

import bio.tools.BioLookup;
import chem.Atom;

/**
 * Tablizable represents a set of one or more AminoAcids
 * 
 * @author Benjy Strauss
 *
 */

public abstract class Tablizable extends BioObject {
	private static final long serialVersionUID = 1L;
	
	//The default weight of a disordered switch to use in a "weighted" data set file
	public static final double DISORDERED_WEIGHT = 0.5;
	
	public static final String _E6  = "e6";
	public static final String _E20 = "e20";
	public static final String _E22 = "e22";
	public static final String _VK  = "vk";
	public static final String _ISU = "isu";
	
	public static final String NORMALIZE = "norm";
	
	public static final String STANDARD_ATOM_KEYS[] = BioLookup.ATOM_CODES;
	/** @return: the type of residue */
	public abstract ResidueType residueType();
	/** @return: the type of secondary structure */
	public abstract SecondaryStructure secondary();
	/** @return: vKabat value */
	public abstract double vKabat();
	/** @return: How many values was the vKabat calculated from */
	public abstract int vKabatCompletion();
	/** @return: isUnstruct disorder propensity */
	public abstract double isUnstruct();
	/** @return: 6-term Entropy */
	public abstract double E6();
	/** @return: 20-term Entropy */
	public abstract double E20();
	/** @return: 22-term Entropy */
	public abstract double E22();
	/** @return: amber95 average charge */
	public abstract double amber95();
	/** @return: if the object is a switch */
	public abstract SwitchType switchType();
	/** @return: if the object is a switch */
	public abstract boolean isSwitch();
	/** @return: character representation of middle residue */
	public abstract char toChar();
	/** @return: abbreviated representation of residue(s)*/
	public abstract String toCode();
	/** @return: average charge of all atoms assigned to the residue*/
	public abstract double averageCharge();
	
	/**
	 * Gets an atom assigned to the object
	 * @param key: The name of the atom to get
	 * @return: the atom with the name specified, or null if such an atom cannot be found.
	 */
	public abstract Atom getAtom(String key);
	
	public abstract String getStandardCharges();
	
	public boolean isUnwritable() { return false; }
	
	/**
	 * 
	 * @param entropy: entropy value
	 * @param entropyType: number of terms in entropy
	 * @param flip: whether to flip the entropy value
	 * @param normalize: whether to normalize the entropy value
	 * @return
	 */
	public static final String processEntropy(double entropy, int entropyType, boolean flip, boolean normalize) {
		double retVal;
		double maxVal = Math.log(entropyType)/Math.log(2);
		if(flip) { retVal = maxVal - entropy; } else { retVal = entropy; }
		if(normalize) { retVal /= maxVal; }
		return retVal + ",";
	}
}
