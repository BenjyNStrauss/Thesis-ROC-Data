package bio.exceptions;

/**
 * Thrown when data cannot be retrieved
 * @author Benjy Strauss
 *
 */

public class DataRetrievalException extends Exception implements JBioSJSUException {
	private static final long serialVersionUID = 1L;

	public DataRetrievalException() { }

	public DataRetrievalException(String message) {
		super(message);
	}

	public DataRetrievalException(Throwable cause) {
		super(cause);
	}
	
	public DataRetrievalException(String message, Throwable cause) {
		super(message, cause);
	}

	public DataRetrievalException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
