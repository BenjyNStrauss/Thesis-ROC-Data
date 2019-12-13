package bio.exceptions;

import bio.Protein;

/**
 * Get's thrown if the Protein in memory differs from what's in a file
 * @author Benjy Strauss
 * 
 */

public class InconsistentFileDataException extends RuntimeException implements JBioSJSUException{
	private static final long serialVersionUID = 1L;

	public InconsistentFileDataException(Protein protein, char chainChar, int chainIndex, char resTypeChar) {
		super(genMsg(protein, chainChar, chainIndex, resTypeChar));
	}
	
	private static final String genMsg(Protein protein, char chainChar, int chainIndex, char resTypeChar) {
		String retVal = "Error on protein: " + protein.toString() + ":" + chainChar + "\n";
		retVal += "Residue [" + chainIndex + "] has a conflict of " + protein.getChain(chainChar).getAmino(chainIndex).toChar();
		retVal += " vs " + resTypeChar;
		return retVal;
	}
}
