package bio.exceptions;

/**
 * 
 * @author Benjy Strauss
 *
 */

public class ScriptException extends RuntimeException implements JBioSJSUException {
	private static final long serialVersionUID = 1L;
	
	public ScriptException(String msg) {
		super(msg);
	}
	
	public ScriptException() { }
}
