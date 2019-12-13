package bio.exceptions;

/**
 * 
 * @author Benjy Strauss
 *
 */

public class ResidueAlignmentRuntimeException extends RuntimeException implements JBioSJSUException {
	private static final long serialVersionUID = 1L;
	
	public ResidueAlignmentRuntimeException() { } 
	
	public ResidueAlignmentRuntimeException(String msg) {
		super(msg);
	}

}
