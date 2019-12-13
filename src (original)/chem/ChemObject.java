package chem;

import java.io.Serializable;

import util.BaseTools;

/**
 * 
 * @author Benjy Strauss
 *
 */

public abstract class ChemObject extends BaseTools implements Serializable {
	private static final long serialVersionUID = 1L;

	public abstract String impliedFileName();
	
}
