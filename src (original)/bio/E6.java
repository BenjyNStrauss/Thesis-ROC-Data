package bio;

/**
 * Represents the 6 different classes of Amino Acid residue types used in the 
 * E6 descriptor
 * 
 * @author Benjy Strauss
 *
 */

public enum E6 {
	ALIPHATIC, AROMATIC, POLAR, POSITIVE, NEGATIVE, SPECIAL,
	UNACCOUNTED_FOR;
	
	/**
	 * Parses a String into an enum value
	 * @param str: the String to parse
	 * @return: the value that the String represents, or UNACCOUNTED_FOR if the String is not recognized
	 */
	public E6 parse(String str) {
		switch(str.toLowerCase().trim()) {
		case "aliphatic":		return ALIPHATIC;
		case "aromatic":			return AROMATIC;
		case "polar":			return POLAR;
		case "positive":			return POSITIVE;
		case "negative":			return NEGATIVE;
		case "special":			return SPECIAL;
		default:					return UNACCOUNTED_FOR;
		}
	}
}
