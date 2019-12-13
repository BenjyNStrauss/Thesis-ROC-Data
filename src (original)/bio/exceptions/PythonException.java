package bio.exceptions;

/**
 * Thrown when an error occurs in python scripts
 * @author Benjy Strauss
 *
 */

public class PythonException extends ScriptException {
	private static final long serialVersionUID = 1L;
	
	public PythonException(String msg) {
		super(msg);
	}
	
	public PythonException(int val) {
		super("Python processes returned: " + val);
	}
	
	public PythonException() { }
}
