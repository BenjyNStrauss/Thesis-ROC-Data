package bio.exceptions;

/**
 * 
 * @author Benjy Strauss
 *
 */

public class ResidueIndexOutOfBoundsException extends ArrayIndexOutOfBoundsException implements JBioSJSUException{
	private static final long serialVersionUID = 1L;
	
	public static final int TOO_SMALL = -1;
	public static final int TOO_LARGE = 1;
	
	private int outOfBoundsDirection;
	
	public ResidueIndexOutOfBoundsException(int outOfBoundsDirection) {
		this.outOfBoundsDirection = outOfBoundsDirection;
	} 
	
	public ResidueIndexOutOfBoundsException(String msg, int outOfBoundsDirection) {
		super(msg);
		this.outOfBoundsDirection = outOfBoundsDirection;
	}
	
	public int outOfBoundsDirection() { return outOfBoundsDirection; }
	
	public boolean tooLarge() { return (outOfBoundsDirection > 0 ? true : false); }
	
	public boolean tooSmall() { return (outOfBoundsDirection < 0 ? true : false); }
}
