package bio.exceptions;

/**
 * Thrown when two Amino Acid Sequences cannot be lined up
 * @author Benjy Strauss
 *
 */

public class ResidueAlignmentException extends Exception implements JBioSJSUException {
	private static final long serialVersionUID = 1L;
	
	public ResidueAlignmentException() { } 
	
	public ResidueAlignmentException(String msg) {
		super(msg);
	}
	
}
