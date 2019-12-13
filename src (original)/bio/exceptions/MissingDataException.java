package bio.exceptions;

import bio.BioObject;

/**
 * Used to signify when something is missing data
 * @author Benjy Strauss
 *
 */

public class MissingDataException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	
	public BioObject obj;
	
	public MissingDataException(BioObject obj) {
		super("Missing data for: " + obj);
		this.obj = obj;
	}
	
	public MissingDataException(String msg) {
		super(msg);
	}
	
	public MissingDataException() { }
	
}
