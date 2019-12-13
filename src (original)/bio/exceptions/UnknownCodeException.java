package bio.exceptions;

/**
 * 
 * @author Benjy Strauss
 *
 */

public class UnknownCodeException extends RuntimeException implements JBioSJSUException {
	private static final long serialVersionUID = 1L;

	public UnknownCodeException(String code) {
		super("Unrecognized code: " + code);
	}

}
