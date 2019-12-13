package bio.exceptions;

/**
 * 
 * @author Benjy Strauss
 *
 */

public class InvalidResolutionException extends RuntimeException implements JBioSJSUException {
	private static final long serialVersionUID = 1L;
	
	public InvalidResolutionException(String msg) { super(msg); }
	public InvalidResolutionException() { }
}
