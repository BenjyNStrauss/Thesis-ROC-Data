package bio;

import bio.exceptions.ResidueIndexOutOfBoundsException;
import chem.Atom;

/**
 * 
 * @author Benjy Strauss
 *
 */

public class AminoTriplet extends Tablizable {
	private static final long serialVersionUID = 1L;
	
	private AminoAcid triplet[];
	
	/**
	 * 
	 * @param a
	 * @param b
	 * @param c
	 */
	public AminoTriplet(AminoAcid a, AminoAcid b, AminoAcid c) {
		triplet = new AminoAcid[3];
		triplet[0] = a;
		triplet[1] = b;
		triplet[2] = c;
	}
	
	/**
	 * 
	 * @param region
	 */
	private AminoTriplet(AminoAcid[] region) { triplet = region; }
	
	/**
	 * 
	 * @param aa
	 * @param i
	 * @return
	 */
	public AminoAcid setResidue(AminoAcid aa, int i) {
		if(i > 3 || i < 0) { throw new ResidueIndexOutOfBoundsException(i < 0 ? -1 : 1); }
		AminoAcid retVal = triplet[i];
		triplet[i] = aa;
		return retVal;
	}
	
	/**
	 * 
	 * @param i
	 * @return
	 */
	public AminoAcid getResidue(int i) {
		if(i > 3 || i < 0) { throw new ResidueIndexOutOfBoundsException(i < 0 ? -1 : 1); }
		return triplet[i];
	}

	@Override
	public String impliedFileName() {
		if(triplet[1] != null) {
			return triplet[1].impliedFileName(); 
		} else {
			return null;
		}
	}
	
	@Override
	public ResidueType residueType() {
		if(triplet[1] != null) {
			return triplet[1].residueType(); 
		} else {
			return null;
		}
	}
	
	@Override
	public SecondaryStructure secondary() {
		if(triplet[1] != null) {
			return triplet[1].secondary(); 
		} else {
			return null;
		}
	}

	@Override
	public double vKabat() {
		if(triplet[1] != null) {
			return triplet[1].vKabat();
		} else {
			return Double.NaN;
		}
	}

	@Override
	public int vKabatCompletion() { 
		if(triplet[1] != null) {
			return triplet[1].vKabatCompletion();
		} else {
			return -1;
		}
	}

	@Override
	public double isUnstruct() { 
		double isUnstruct = 0;
		double valid = 0;
		
		if(triplet[0] != null) { if(!Double.isNaN(triplet[0].isUnstruct())) { isUnstruct += triplet[0].isUnstruct(); ++valid; } }
		if(triplet[1] != null) { if(!Double.isNaN(triplet[1].isUnstruct())) { isUnstruct += triplet[1].isUnstruct(); ++valid; } }
		if(triplet[2] != null) { if(!Double.isNaN(triplet[2].isUnstruct())) { isUnstruct += triplet[2].isUnstruct(); ++valid; } }
		
		if(valid != 0) {
			return (isUnstruct / valid);
		} else {
			return Double.NaN;
		}
	}

	@Override
	public double E6() { 
		double e6 = 0;
		double valid = 0;
		
		if(triplet[0] != null) { if(!Double.isNaN(triplet[0].E6())) { e6 += triplet[0].E6(); ++valid; } }
		if(triplet[1] != null) { if(!Double.isNaN(triplet[1].E6())) { e6 += triplet[1].E6(); ++valid; } }
		if(triplet[2] != null) { if(!Double.isNaN(triplet[2].E6())) { e6 += triplet[2].E6(); ++valid; } }
		
		if(valid != 0) {
			return (e6 / valid);
		} else {
			return Double.NaN;
		}
	}

	@Override
	public double E20() { 
		double e20 = 0;
		double valid = 0;
		
		if(triplet[0] != null) { if(!Double.isNaN(triplet[0].E20())) { e20 += triplet[0].E20(); ++valid; } }
		if(triplet[1] != null) { if(!Double.isNaN(triplet[1].E20())) { e20 += triplet[1].E20(); ++valid; } }
		if(triplet[2] != null) { if(!Double.isNaN(triplet[2].E20())) { e20 += triplet[2].E20(); ++valid; } }
		
		if(valid != 0) {
			return (e20 / valid);
		} else {
			return Double.NaN;
		}
	}

	@Override
	public double E22() { 
		double e22 = 0;
		double valid = 0;
		
		if(triplet[0] != null) { if(!Double.isNaN(triplet[0].E22())) { e22 += triplet[0].E22(); ++valid; } }
		if(triplet[1] != null) { if(!Double.isNaN(triplet[1].E22())) { e22 += triplet[1].E22(); ++valid; } }
		if(triplet[2] != null) { if(!Double.isNaN(triplet[2].E22())) { e22 += triplet[2].E22(); ++valid; } }
		
		if(valid != 0) {
			return (e22 / valid);
		} else {
			return -1;
		}
	}

	@Override
	public double amber95() { 
		double amber = 0;
		double averageOver = 0;
		boolean valid = false;
		
		if(triplet[0] != null) { amber += triplet[0].amber95(); ++averageOver; valid = true; }
		if(triplet[1] != null) { amber += triplet[1].amber95(); ++averageOver; valid = true; }
		if(triplet[2] != null) { amber += triplet[2].amber95(); ++averageOver; valid = true; }
		
		if(valid) {
			return (amber / averageOver);
		} else {
			return Double.NaN;
		}
	}

	@Override
	public boolean isSwitch() { 
		boolean isSwitch = false;
		if(triplet[1] != null) { isSwitch = triplet[1].isSwitch(); }
		return isSwitch;
	}
	
	@Override
	public SwitchType switchType() { 
		SwitchType isSwitch = SwitchType.MISSING_RESIDUE;
		if(triplet[1] != null) { isSwitch = triplet[1].switchType(); }
		return isSwitch;
	}
	
	@Override
	public double averageCharge() { 
		double avgCharge = 0;
		boolean valid = false;
		
		if(triplet[0] != null) { avgCharge += triplet[0].averageCharge(); valid = true; }
		if(triplet[1] != null) { avgCharge += triplet[1].averageCharge(); valid = true; }
		if(triplet[2] != null) { avgCharge += triplet[2].averageCharge(); valid = true; }
		
		if(valid) {
			return (avgCharge / 3);
		} else {
			return -1;
		}
	}
	
	public String getStandardCharges() {
		StringBuilder dataBuilder = new StringBuilder();
		for(String s: STANDARD_ATOM_KEYS) {
			Atom atom = null;
			double chargeSum = 0;
			boolean valid = false;
			
			if(triplet[0] != null) { atom = triplet[0].getAtom(s); }
			if(atom != null) { chargeSum = atom.charge(); valid = true; }
			if(triplet[1] != null) { atom = triplet[1].getAtom(s); }
			if(atom != null) { chargeSum = atom.charge(); valid = true; }
			if(triplet[2] != null) { atom = triplet[2].getAtom(s); }
			if(atom != null) { chargeSum = atom.charge(); valid = true; }
			
			if(valid) {
				dataBuilder.append(chargeSum + ",");
			} else {
				dataBuilder.append(Double.NaN + ",");
			}
		}
		return dataBuilder.toString();
	}
	
	public Atom getAtom(String name) { 
		if(triplet[1] != null) {
			return triplet[1].getAtom(name);
		} else {
			return null;
		}
	}
	
	public String toCode() {
		if(triplet[1] != null) { return triplet[1].toCode(); }
		if(triplet[0] != null) { return triplet[0].toCode(); }
		if(triplet[2] != null) { return triplet[2].toCode(); }
		return "null";
	}
	
	public String toFullCode() {
		StringBuilder builder = new StringBuilder();
		if(triplet[0] != null) { builder.append(triplet[0].toCode()); }
		if(triplet[1] != null || triplet[1] != null) { builder.append("-"); }
		if(triplet[1] != null) { builder.append(triplet[1].toCode()); }
		if(triplet[2] != null || triplet[2] != null) { builder.append("-"); }
		if(triplet[2] != null) { builder.append(triplet[2].toCode()); }
		return builder.toString();
	}
	
	@Override
	public char toChar() {
		if(triplet[1] != null) { return triplet[1].toChar(); }
		if(triplet[0] != null) { return triplet[0].toChar(); }
		if(triplet[2] != null) { return triplet[2].toChar(); }
		return '_';
	}
	
	/**
	 * 
	 * @return
	 */
	public String toChars() {
		StringBuilder builder = new StringBuilder();
		if(triplet[0] != null) { builder.append(triplet[0].toChar()); }
		if(triplet[1] != null) { builder.append(triplet[1].toChar()); }
		if(triplet[2] != null) { builder.append(triplet[2].toChar()); }
		return builder.toString();
	}
	
	/**
	 * 
	 */
	public void autoAssignCharges() {
		triplet[0].autoAssignCharges();
		triplet[1].autoAssignCharges();
		triplet[2].autoAssignCharges();
	}
	
	/**
	 * 
	 */
	public AminoTriplet clone() {
		AminoAcid tripletClone[] = new AminoAcid[3];
		
		tripletClone[0] = triplet[0].clone();
		tripletClone[1] = triplet[1].clone();
		tripletClone[2] = triplet[2].clone();
		
		AminoTriplet myClone = new AminoTriplet(tripletClone);
		return myClone;
	}
	
	public boolean isUnwritable() { 
		if(triplet[1] == null) { return true; }
		else return triplet[1].isUnwritable();
	}
	
	/**
	 * 
	 */
	public String toString() {
		StringBuilder builder = new StringBuilder();
		if(triplet[0] != null) { builder.append(triplet[0].toString()); } else { builder.append("null"); }
		builder.append(",");
		if(triplet[1] != null) { builder.append(triplet[1].toString()); } else { builder.append("null"); }
		builder.append(",");
		if(triplet[1] != null) { builder.append(triplet[2].toString()); } else { builder.append("null"); }
		return builder.toString();
	}
}
