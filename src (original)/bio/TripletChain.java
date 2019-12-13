package bio;

import bio.exceptions.ResidueIndexOutOfBoundsException;

/**
 * Represents a chain of Amino Acid triplets
 * @author Benjy Strauss
 *
 */

public class TripletChain extends AminoChain {
	private static final long serialVersionUID = 1L;

	private AminoTriplet sequence[];
	
	public TripletChain(String protein, char id, int chainLength, DataSource source) {
		super(protein.toUpperCase(), id, source);
		sequence = new AminoTriplet[chainLength];
	}
	
	public TripletChain(String protein, char id, AminoTriplet sequence[], DataSource source) {
		super(protein.toUpperCase(), id, source);
		this.sequence = sequence;
	}
	
	/**
	 * Get the triplet at the index specified
	 */
	public AminoTriplet getAmino(int index) { return getTriplet(index); }
	
	/**
	 * Get the an AminoAcid from the triplet at the index specified
	 * @param index: the index of the triplet
	 * @param index2: the index of the AminoAcid in the triplet
	 * @return
	 */
	public AminoAcid getAmino(int index, int index2) { return getTriplet(index).getResidue(index2); }
	
	/**
	 * Changes the Amino Acid at a given index
	 * @param aa: the new Amino Acid
	 * @param index: the index of the Amino Acid to replace
	 */
	public void setTriplet(AminoTriplet at, int index) { sequence[index] = at; }
	
	/**
	 * Gets the amino acid at the specified index
	 * @param index: the index into the protein
	 * @return: the amino acid at the specified index
	 */
	public AminoTriplet getTriplet(int index) {
		if((index < sequence.length) && (index >= 0)) {
			return sequence[index ];
		} else if(index < sequence.length) {
			throw new ResidueIndexOutOfBoundsException(ResidueIndexOutOfBoundsException.TOO_SMALL);
		} else {
			throw new ResidueIndexOutOfBoundsException(ResidueIndexOutOfBoundsException.TOO_LARGE);
		}
	}
	
	/**
	 * Get a deep copy of the TripletChain
	 */
	public TripletChain clone() {
		AminoTriplet sequenceClone[] = new AminoTriplet[sequence.length];
		
		for(int index = 0; index < sequence.length; ++index) {
			sequenceClone[index] = sequence[index].clone();
		}
		
		TripletChain myClone = new TripletChain(getProteinName(), getID(), sequenceClone, getSource());
		myClone.flexInit = flexInit;
		myClone.description = description;
		
		return myClone;
	}
	
	/**
	 * Get's the length of the protein in amino acids
	 * @return
	 */
	public int length() { return sequence.length; }
	
	/**
	 * 
	 * @return
	 */
	public String toFasta() {
		StringBuilder fastaBuilder = new StringBuilder();
		
		fastaBuilder.append(">" + getProteinName() + ":" + Character.toUpperCase(getID()) + STATIC_FASTA_HEADER + "\n");
		
		StringBuilder codeBuilder = new StringBuilder();
		codeBuilder.append(getTriplet(0).toCode());
			
		for(int i = 1; i < length(); ++i) {
			codeBuilder.append(getTriplet(i).toChars().charAt(2));
		}
		
		String codeString = codeBuilder.toString();
		
		int strIndex = 0;
		
		for(; strIndex + FASTA_LINE_LENGTH < codeString.length(); strIndex += FASTA_LINE_LENGTH) {
			fastaBuilder.append(codeString.substring(strIndex, strIndex+FASTA_LINE_LENGTH));
			fastaBuilder.append("\n");
		}
		
		fastaBuilder.append(codeString.substring(strIndex));
		fastaBuilder.append("\n");
		
		return fastaBuilder.toString();
	}
}
