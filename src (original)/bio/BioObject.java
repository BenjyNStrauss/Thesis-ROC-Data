package bio;

import java.io.Serializable;

import util.BaseTools;

/**
 * A BioObject is represents anything that is made of biological matter
 * 
 * @author Benjy Strauss
 *
 */

public abstract class BioObject extends BaseTools implements Serializable {
	private static final long serialVersionUID = 1L;
	//used for writing FASTAs
	public static final String STATIC_FASTA_HEADER = "|PDBID|CHAIN|SEQUENCE";
	//mass of the BioObject
	protected double mass;
	
	/**
	 * Default Constructor
	 */
	protected BioObject() { }
	
	/**
	 * Returns the implied file name: this string is used as the default file name to save
	 * the object as
	 * @return String to use as default file name
	 */
	public abstract String impliedFileName();
}
