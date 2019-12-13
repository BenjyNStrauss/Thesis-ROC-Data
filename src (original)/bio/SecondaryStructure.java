package bio;

/**
 * Represents the secondary structure of an Amino Acid in a protein
 * @author Benjy Strauss
 *
 */

public enum SecondaryStructure {
	//alpha helix
	ALPHA_HELIX,
	//residue isolated in beta bridge
	BETA_BRIDGE,
	/*
	 * Not seen in DSSP files, but listed just in case
	 * Loop or irregular element
	 */
	COIL,
	//extended strand, participates in beta ladder
	EXTENDED_STRAND,
	//3-helix (3/10 helix)
	THREE_HELIX,
	//5 helix (pi helix)
	FIVE_HELIX,
	//hydrogen bonded turn
	TURN,
	//bend
	BEND,
	//(Blank in DSSP file)
	UNKNOWN,
	//Entry is missing from DSSP file
	UNASSIGNED;
	
	/**
	 * 
	 * @param ch: character read from DSSP file
	 * @return: Corresponding type of secondary structure
	 */
	public static SecondaryStructure parseFromDSSP(char ch) {
		switch(ch) {
		case 'B':		return BETA_BRIDGE;
		case 'C':		return COIL;
		case 'E':		return EXTENDED_STRAND;
		case 'G':		return THREE_HELIX;
		case 'H':		return ALPHA_HELIX;
		case 'I':		return FIVE_HELIX;
		case 'S':		return BEND;
		case 'T':		return TURN;
		case ' ':		return UNKNOWN;
		default:			return null;
		}
	}
	
	/**
	 * Classify residues as "Helix", "Sheet", or "Other"
	 * @return
	 */
	public SecondarySimple simpleClassify() {
		switch(this) {
		case ALPHA_HELIX:
		case THREE_HELIX:
		case FIVE_HELIX:
			return SecondarySimple.Helix;
		case BETA_BRIDGE:
		case EXTENDED_STRAND:
			return SecondarySimple.Sheet;
		case UNASSIGNED:
			return SecondarySimple.Unassigned;
		default:
			return SecondarySimple.Other;
		}
	}
	
	/**
	 * Get letter corresponding to RCSB PDB classification of secondary structure
	 * @return
	 */
	public char toLetter() {
		switch(this) {
		case ALPHA_HELIX:		return 'H';
		case THREE_HELIX:		return 'G';
		case FIVE_HELIX:			return 'I';
		case BETA_BRIDGE:		return 'B';
		case EXTENDED_STRAND:	return 'E';
		case TURN:				return 'T';
		case BEND:				return 'S';
		case UNASSIGNED:			return '*';
		default:					return ' ';
		}
	}
	
	/**
	 * RCSB official description of secondary structure type
	 * @return
	 */
	public String description() {
		switch(this) {
		case ALPHA_HELIX:		return "alpha helix";
		case THREE_HELIX:		return "3-helix (3/10 helix)";
		case FIVE_HELIX:			return "5 helix (pi helix)";
		case BETA_BRIDGE:		return "residue isolated in beta bridge";
		case EXTENDED_STRAND:	return "extended strand, participates in beta ladder";
		case TURN:				return "hydrogen bonded turn";
		case BEND:				return "bend";
		case COIL:				return "Not Seen in DSSP, ";
		default:					return "blank in DSSP file";
		}
	}
	
	/**
	 * Return the Enum String formatted correctly
	 */
	public String toString() {
		String retVal = super.toString();
		boolean lastBlank = true;
		retVal = retVal.replaceAll("_", " ");
		
		StringBuilder builder = new StringBuilder();
		
		for(int i = 0; i < retVal.length(); ++i) {
			if(lastBlank) {
				builder.append(retVal.charAt(i));
				lastBlank = false;
			} else if(retVal.charAt(i) == ' ') {
				builder.append(retVal.charAt(i));
				lastBlank = true;
			} else {
				builder.append(Character.toLowerCase(retVal.charAt(i)));
			}
		}
		
		return builder.toString();
	}
}
