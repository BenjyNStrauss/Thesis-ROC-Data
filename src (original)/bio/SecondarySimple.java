package bio;

/**
 * 
 * @author Benjy Strauss
 *
 */

public enum SecondarySimple {
	Helix, Sheet, Other, Unassigned;
	
	public char toChar() {
		switch(this) {
		case Helix:			return 'H';
		case Other:			return 'O';
		case Sheet:			return 'S';
		case Unassigned:
		default:				return 'D';
			
		}
	}
}
