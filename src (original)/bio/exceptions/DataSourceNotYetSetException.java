package bio.exceptions;

/**
 * 
 * @author Benjy Strauss
 *
 */

public class DataSourceNotYetSetException extends RuntimeException implements JBioSJSUException {
	private static final long serialVersionUID = 1L;

	public DataSourceNotYetSetException(String msg) {
		super(msg);
	}
	
	public DataSourceNotYetSetException() { }
}
