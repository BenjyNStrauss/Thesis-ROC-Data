package bio.exceptions;

/**
 * Thrown when a protein is unrecognized
 * @author Benjy Strauss
 *
 */

public class UnrecognizedProteinException extends RuntimeException implements JBioSJSUException {
	private static final long serialVersionUID = 1L;
	
	private String name;
	
	public UnrecognizedProteinException(String identifier) {
		this(identifier, "");
	}
	
	public UnrecognizedProteinException(String identifier, String msg) {
		super(msg);
		name = identifier;
	}
	
	public String getProteinCode() {
		return name;
	}
	
}
