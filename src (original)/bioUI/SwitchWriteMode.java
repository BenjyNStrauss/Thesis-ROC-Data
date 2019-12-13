package bioUI;

/**
 * 
 * @author Benjy Strauss
 *
 */

public enum SwitchWriteMode {
	ASSIGNED, UNASSIGNED, BOTH, WEIGHTED;
	
	public double DISORDERED_WEIGHT = 0.5;
	
	public static SwitchWriteMode parse(char ch) {
		switch(Character.toLowerCase(ch)) {
		case 'u':
		case 'd':	return UNASSIGNED;
		case 'a':
		case 'o':	return ASSIGNED;
		case 'w':	return WEIGHTED;
		default:		return BOTH;
		}
	}
	
	public char toChar() {
		switch(this) {
		case BOTH:		return 'b';
		case UNASSIGNED:	return 'u';
		case ASSIGNED:	return 'a';
		case WEIGHTED:	return 'w';
		default:
			throw new NullPointerException();
		}
	}
}
