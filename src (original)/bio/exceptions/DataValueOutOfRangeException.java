package bio.exceptions;

/**
 * Thrown when a nonsensical data value is encountered
 * 		Example: negative probabilities
 * @author Benjy Strauss
 *
 */

public class DataValueOutOfRangeException extends RuntimeException implements JBioSJSUException {
	private static final long serialVersionUID = 1L;

	public DataValueOutOfRangeException(String msg) {
		super(msg);
	}
	
	public DataValueOutOfRangeException() { }
}
