package util;

/**
 * 
 * @author Benjy Strauss
 *
 */

public class BadArraySizeException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public BadArraySizeException() { }
	
	public BadArraySizeException(String msg) { super(msg); }
	
}
