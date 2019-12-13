package bio.exceptions;

/**
 * 
 * @author Benjy Strauss
 *
 */

public class FileNotFoundRuntimeException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	
	private String fileNotToBeFound;
	
	public FileNotFoundRuntimeException(String filename, String msg) {
		super(msg);
		fileNotToBeFound = filename;
	}
	
	public FileNotFoundRuntimeException(String filename) {
		fileNotToBeFound = filename;
	}
	
	public String fileNotFound() { return fileNotToBeFound; }
}
