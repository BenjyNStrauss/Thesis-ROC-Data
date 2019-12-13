package analysis;

/**
 * Represents a descriptor used to predict if an Amino Acid Residue
 * exhibits switch-like behavior.
 * 
 * @author Benjy Strauss
 *
 */

public enum Descriptor {
	RESIDUE_TYPE, ISUNSTRUCT, E6, E20, VKABAT, AMBER95, CHARGE;
	
	/**
	 * 
	 * @return 3-character representation of the descriptor
	 */
	public String toAbbrev() {
		switch(this) {
		case AMBER95:		return "A95";
		case CHARGE:			return "CRG";
		case E20:			return "E20";
		case E6:				return "E6 ";
		case ISUNSTRUCT:		return "IsU";
		case RESIDUE_TYPE:	return "Res";
		case VKABAT:			return "VK ";	
		default:
			return "ERR";
		}
	}
	
	/**
	 * 
	 */
	public String toString() {
		switch(this) {
		case AMBER95:		return "Amber95";
		case CHARGE:			return "Charge";
		case E20:			return "E20";
		case E6:				return "E6";
		case ISUNSTRUCT:		return "IsUnstruct";
		case RESIDUE_TYPE:	return "Side Chain Type";
		case VKABAT:			return "Vkabat";	
		default:
			return "ERROR";
		}
	}
	
	/**
	 * 
	 * @param descriptors
	 * @return
	 */
	public static String label(Descriptor[] descriptors) {
		if(descriptors == null) {
			throw new NullPointerException("No descriptors to construct label with!");
		} else if(descriptors.length == 0) {
			throw new ArrayIndexOutOfBoundsException("No descriptors to construct label with!");
		} 
		
		StringBuilder retVal = new StringBuilder();
		retVal.append(descriptors[0].toAbbrev());
		
		for(int i = 1; i < descriptors.length; ++i) { retVal.append(" + " + descriptors[i].toAbbrev()); }
		return retVal.toString();
	}
}
