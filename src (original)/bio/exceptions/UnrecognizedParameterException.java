package bio.exceptions;

/**
 * 
 * @author Benjy Strauss
 *
 */

public class UnrecognizedParameterException extends RuntimeException implements JBioSJSUException {
	private static final long serialVersionUID = 1L;
	
	public UnrecognizedParameterException(String msg) {
		super(msg);
	}
	
	public UnrecognizedParameterException(char msg) {
		super("Unrecognized char: " + msg);
	}
	
	public UnrecognizedParameterException(int msg) {
		super("Unrecognized method: " + msg);
	}
	
	public UnrecognizedParameterException() { }
	
}
