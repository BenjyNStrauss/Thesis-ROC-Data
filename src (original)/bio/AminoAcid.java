package bio;

import java.util.Set;

import bio.exceptions.DataValueOutOfRangeException;
import bio.exceptions.UnknownCodeException;
import bio.tools.BioLookup;
import chem.Atom;

/**
 * Represents an Amino Acid or an Amino Acid residue
 * @author Benjy Strauss
 *
 */

public class AminoAcid extends Tablizable {
	private static final long serialVersionUID = 1L;
	
	//type of amino acid residue
	protected ResidueType type;
	//type of secondary structure associated with this amino acid
	protected SecondaryStructure secondary;
	//amber95 average charge value
	protected double amber95 = Double.NaN;
	//Vkabat secondary structure prediction value
	protected double vKabat = -1;
	//how many of the 15 Vkabat algorithms results were available to use 
	protected int vKabatCompletion = 0;
	
	/*
	 * Lobanov-Galziskaya value:
	 * Probability that the residue will be disordered
	 * or have no secondary structure
	 */
	protected double isUnstruct = Double.NaN;
	//6-term entropy
	protected double E6 = Double.NaN;
	//20-term entropy
	protected double E20 = Double.NaN;
	/* Benjy's special 22-term entropy
	 * Not used, but designed for use if Pyrrolysine and Selenocysteine need to be considered
	 */
	protected double E22 = Double.NaN;
	
	protected double flex = Double.NaN;
	
	//contains data about the atoms in the residue
	private AtomTable atoms;
	//if the amino acid is part of a switch-like region of a protein chain
	protected SwitchType isSwitch = SwitchType.NONE;
	
	/**
	 * Creates a new AminoAcid Object
	 * @param residueType: the type of residue (single letter)
	 */
	public AminoAcid(char residueType) {
		this(ResidueType.letterLookup(residueType));
	}
	
	/**
	 * Creates a new AminoAcid Object
	 * @param residueType: the type of residue (multi-letter sequence)
	 * @throws UnknownCodeException 
	 */
	public AminoAcid(String residueType) throws UnknownCodeException {
		this(ResidueType.parseCode(residueType));
	}
	
	/**
	 * Creates a new AminoAcid Object
	 * @param type: the type of residue (enumeration)
	 */
	public AminoAcid(ResidueType type) {
		if(type == null) { throw new NullPointerException("Amino Acid Type Cannot Be Null"); }
		
		this.type = type;
		atoms = new AtomTable();
	}
	
	/**
	 * Return's the amino acid's single character side chain representation
	 */
	public char toChar() { return type.toChar(); }
	
	/**
	 * Set the VKabat value
	 * @param val: the VKabat value
	 */
	public void setVkabat(double val) { vKabat = val; }
	
	/**
	 * Sets the Vkabat completion value
	 * This is the number of servers that gave secondary structure prediction results
	 * @param length
	 */
	public void setVkabatCompletion(int length) { vKabatCompletion = length; }
	
	/**
	 * Set the flexibility of the amino acid
	 * @param flexibility: what to set the flexibility to
	 */
	public void setFlex(double flexibility) { flex = flexibility; }
	
	/**
	 * Set the Lobinov-Galziskaya (IsUnstruct) value
	 * @param val: the Lobinov-Galziskaya value
	 * @throws DataValueOutOfRangeException: if the value is not between 0 and 1
	 */
	public void setIsUnstruct(double val) { 
		if(val >= 0 && val <= 1) {
			isUnstruct = val;
		} else {
			throw new DataValueOutOfRangeException("IsUnstruct needs to be between 0 and 1: [" + val + "]");
		}
	}
	
	/** Sets the secondary structure of the amino acid */
	public void setSecondaryStructure(SecondaryStructure type) { secondary = type;}
	
	/** Assigns partial charges of atoms based on residue type and secondary structure */
	public void autoAssignCharges() { 
		if(secondary != null) { 
			BioLookup.assignChargeValues(this);	
		}
		BioLookup.assignNetAmber95(this);
	}
	
	/**
	 * Add data on an atom(s) of the amino acid
	 * Make sure the atom's name is not null, it's the main way to retrieve this data
	 * 
	 * @param atom
	 */
	public void addAtom(Atom atom) { atoms.put(atom.name(), atom); }
	
	/**
	 * Set the 6-term entropy value
	 * @param val
	 */
	public void setE6(double val) { E6 = val; }
	
	/**
	 * Set the 20-term entropy value
	 * @param val
	 */
	public void setE20(double val) { E20 = val; }
	
	/**
	 * Set the 22-term entropy value
	 * Note that the E22 is not calculated by this program as of 1/28/19
	 * @param val
	 */
	public void setE22(double val) { E22 = val; }
	
	/** Marks the residue as a switch or not */
	public void setSwitch(SwitchType isSwitch) { this.isSwitch = isSwitch; }
	
	/** Sets the amber95 value */
	public void setAmber95(double val) { amber95 = val; }
	
	/** @return: type of amino acid side chain */
	public ResidueType residueType() { return type; }
	/** @return: type of amino acid secondary structure */
	public SecondaryStructure secondary() { return secondary; }
	
	/** @return: vKabat value */
	public double vKabat() { return vKabat; }
	/** @return: How many values was the vKabat calculated from */
	public int vKabatCompletion() { return vKabatCompletion; }
	/** @return: isUnstruct disorder propensity */
	public double isUnstruct() { return isUnstruct; }
	/** @return: 6-term Entropy */
	public double E6() { return E6; }
	/** @return: 20-term Entropy */
	public double E20() { return E20; }
	/** @return: 22-term Entropy */
	public double E22() { return E22; }
	
	public double flex() { return flex; }
	
	/** @return: net amber95 charge */
	public double amber95() { return amber95; }
	
	public SwitchType switchType() { return isSwitch; }
	public boolean isSwitch() { return (isSwitch != SwitchType.NONE); }
	
	public Atom getAtom(String name) { return atoms.get(name); }
	
	/**
	 * Removes (and returns) the atom with the specified name
	 * @param name: the name of the atom to return
	 * @return: the atom removed, or null if an atom with the specified name was not found
	 */
	public Atom removeAtom(String name) { return atoms.remove(name); }
	
	/**
	 * Returns the average charge for all of the atoms with known charges: 
	 * A value will be skipped over if it cannot be applied.
	 * @return Average charge of the known atoms of the amino acid
	 */
	public double averageCharge()  {
		double retVal = 0;
		int numberOfCharges = 0;
		
		Set<String> keys = atoms.keySet();
		for(String key: keys) {
			Atom atom = atoms.get(key);
			if(atom.charge() != Double.NaN) {
				retVal += atom.charge();
				++numberOfCharges;
			}
		}
		
		retVal /= numberOfCharges;
		
		return retVal;
	}
	
	public void setResidueType(ResidueType res) { type = res; }
	
	/**
	 * Make a deep copy of the AminoAcid Object
	 */
	public AminoAcid clone() {
		AminoAcid retVal = new AminoAcid(type);
		retVal.E6  = E6;
		retVal.E20 = E20;
		retVal.E22 = E22;
		retVal.atoms = atoms.clone();
		retVal.vKabat = vKabat;
		retVal.isUnstruct = isUnstruct;
		retVal.vKabatCompletion = vKabatCompletion;
		retVal.amber95 = amber95;
		retVal.flex = flex;
		
		retVal.secondary = secondary;
		retVal.isSwitch = isSwitch;
		
		return retVal;
	}
	
	/**
	 * Debug method
	 * @return
	 */
	public String toAllData() {
		String retVal = type.toCode() + "(" + secondary + ")\n";
		retVal += E6 + "--" + E20 + "--" + E22 + "--" + vKabat + "--" + isUnstruct + "\n";
//		retVal += charges[NITROGEN] + "--" + charges[H_NITROGEN] + "--" + charges[C_ALPHA] + "--" + charges[C_BETA] + "--" + charges[C_PRIME] + "--" + charges[OXYGEN] + "\n";
		return retVal;
	}
	
	public String getStandardCharges() {
		StringBuilder dataBuilder = new StringBuilder();
		for(String s: Tablizable.STANDARD_ATOM_KEYS) {
			Atom atom = getAtom(s);
			if(atom != null) {
				dataBuilder.append(atom.charge() + ",");
			} else {
				dataBuilder.append(Double.NaN + ",");
			}
		}
		return dataBuilder.toString();
	}
	
	/**
	 * 
	 * @return 3-Letter Amino Acid Code
	 */
	public String toCode() { return type.toCode(); }
	
	@Override
	public String impliedFileName() { return type.toCode(); }
	
	/**
	 * Copies all of the atoms from the source to the target
	 * @param target
	 * @param source
	 */
	public static void copyAtoms(AminoAcid target, AminoAcid source) {
		Set<String> keys = source.atoms.keySet();
		for(String key: keys) {
			Atom atom = source.getAtom(key);
			target.addAtom(atom);
		}
	}
	
	public boolean isUnwritable() { return Double.isNaN(isUnstruct); }
	
	/**
	 * 
	 * @param other
	 * @return
	 */
	public boolean equals(AminoAcid other) { return type.equals(other.type); }
	
	/**
	 * 
	 */
	public String toString() { return type.toString(); }
}
