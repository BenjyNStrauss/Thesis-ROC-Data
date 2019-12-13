package bio.exceptions;

/**
 * 
 * @author Benjy Strauss
 *
 */

public class PointValueOutOfRangeException extends RuntimeException implements JBioSJSUException {
	private static final long serialVersionUID = 1L;

	public PointValueOutOfRangeException(String msg) {
		super(msg);
	}
	
	public PointValueOutOfRangeException() { }
}
