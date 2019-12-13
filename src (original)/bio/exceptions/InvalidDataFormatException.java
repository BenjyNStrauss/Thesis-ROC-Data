package bio.exceptions;

/**
 * 
 * @author Benjy Strauss
 *
 */

public class InvalidDataFormatException extends RuntimeException implements JBioSJSUException {
	private static final long serialVersionUID = 1L;
	
	public InvalidDataFormatException(String msg) { super(msg); }
	public InvalidDataFormatException() { }
}
