package bio;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;

import bio.exceptions.ResidueIndexOutOfBoundsException;

/**
 * Represents a protein molecule as a named list of amino acids
 * Note that get...(int index) methods use the physical index in the internal array;
 * To use the PDB index, use get...At(int index) methods.
 *  (This assumes that the ProteinChain has been constructed correctly)
 * 
 * @author Benjy Strauss
 *
 */

public class ProteinChain extends AminoChain {
	private static final long serialVersionUID = 1L;
	
	private AminoAcid sequence[];
	//where the chain starts in the PDB file
	private int firstIndex = 0;
	
	/**
	 * Constructs a new ProteinChain
	 * @param protein: Name of Protein (use RCSB PDB name)
	 * @param id: Which chain of the Protein: a, b, c, etc
	 */
	public ProteinChain(String protein, char id, AminoAcid seq[]) {
		this(protein, id, seq, 0, DataSource.OTHER);
	}
	
	/**
	 * Constructs a new ProteinChain
	 * @param protein: Name of Protein (use RCSB PDB name)
	 * @param id: Which chain of the Protein: a, b, c, etc
	 */
	public ProteinChain(String protein, char id, AminoAcid seq[], DataSource source) {
		this(protein, id, seq, 0, source);
	}
	
	/**
	 * Constructs a new ProteinChain
	 * @param protein: Name of Protein (use RCSB PDB name)
	 * @param id: Which chain of the Protein: a, b, c, etc
	 * @param seq: an array of AminoAcid objects that comprise the protein's amino acid sequence
	 * @param firstIndex: The first index of the chain
	 */
	public ProteinChain(String protein, char id, AminoAcid seq[], int firstIndex, DataSource source) {
		super(protein.toUpperCase(), id, source);
		sequence = seq;
		vkabat_init = false;
	}
	
	/**
	 * Constructs a new ProteinChain
	 * @param protein: Name of Protein (use RCSB PDB name)
	 * @param id: Which chain of the Protein: a, b, c, etc
	 * @param seq: The Amino Acid letter sequence as a string
	 */
	public ProteinChain(String protein, char id, String seq) {
		this(protein, id, seq, 0, DataSource.OTHER);
	}
	
	/**
	 * Constructs a new ProteinChain
	 * @param protein: Name of Protein (use RCSB PDB name)
	 * @param id: Which chain of the Protein: a, b, c, etc
	 * @param seq: The Amino Acid letter sequence as a string
	 */
	public ProteinChain(String protein, char id, String seq, DataSource source) {
		this(protein, id, seq, 0, source);
	}
	
	/**
	 * Constructs a new ProteinChain
	 * @param protein: Name of Protein (use RCSB PDB name)
	 * @param id: Which chain of the Protein: a, b, c, etc
	 * @param seq: The Amino Acid letter sequence as a string
	 * @param firstIndex: Sometimes protein chains don't start at index 0, such as in DSSP files.  When that happens
	 * 						use this constructor
	 */
	public ProteinChain(String protein, char id, String seq, int firstIndex, DataSource source) {
		super(protein.toUpperCase(), id, source);
		sequence = new AminoAcid[seq.length()];
		this.firstIndex = firstIndex;
		
		for(int index = 0; index < seq.length(); ++index) {
			if(seq.charAt(index) != RESIDUE_DOES_NOT_EXIST) {
				sequence[index] = new AminoAcid(ResidueType.letterLookup(seq.charAt(index)));
			}
		}
	}

	/**
	 * Sets the flexibility value at a specified index
	 * @param index: the index into the internal array at which to set the flexibility value
	 * @param flexVal: the value to set the flexibility to
	 */
	void setFlexibility(int index, double flexibility) { 
		if(sequence[index] != null) {
			sequence[index].setFlex(flexibility);
		} else {
			throw new NullPointerException("Residue at index " + index + " is null!");
		}
	}
	
	/**
	 * Sets the chain's first PDB index
	 * Be careful when using this method, this will change how methods containing "At" function
	 * 
	 * @param newFirstIndex
	 */
	public void setFirstIndex(int newFirstIndex) { firstIndex = newFirstIndex; }
	
	/**
	 * Get the protein's amino acid sequence
	 * @return: the protein's amino acid sequence as an array of AminoAcid objects
	 */
	public AminoAcid[] sequence() { return sequence; }
	
	/**
	 * Return the amino acid residue at the given index
	 * Index is the array index, NOT the PDB index!
	 * 
	 * @param index: the index into the sequence array object
	 * @return: the amino acid object at the specified index
	 */
	//@Deprecated
	public AminoAcid getAmino(int arrayIndex) {
		if((arrayIndex < sequence.length) && (arrayIndex >= 0)) {
			return sequence[arrayIndex];
		} else if(arrayIndex < sequence.length) {
			throw new ResidueIndexOutOfBoundsException(ResidueIndexOutOfBoundsException.TOO_SMALL);
		} else {
			throw new ResidueIndexOutOfBoundsException(ResidueIndexOutOfBoundsException.TOO_LARGE);
		}
	}
	
	/**
	 * Return the amino acid residue at the given index
	 * Index is PDB index, NOT the array index!
	 * 
	 * @param index: the PDB number of the amino acid to retrieve
	 * (Assuming the ProteinChain was constructed correctly)
	 * @return: the amino acid object at the specified index
	 */
	public AminoAcid getAminoAt(int index) {
		if((index - firstIndex < sequence.length) && (index - firstIndex >= 0)) {
			return sequence[index - firstIndex];
		} else if(index < sequence.length) {
			String msg = "Index[" + index + "] " + fullID();
			throw new ResidueIndexOutOfBoundsException(msg, ResidueIndexOutOfBoundsException.TOO_SMALL);
		} else {
			String msg = "Index[" + index + "] " + fullID();
			throw new ResidueIndexOutOfBoundsException(msg, ResidueIndexOutOfBoundsException.TOO_LARGE);
		}
	}
	
	/**
	 * Changes the type of an Amino Acid at a given index
	 * @param res: the new Amino Acid Residue Type
	 * @param index: the arrayIndex of the residue to replace
	 */
	public void updateAminoResidue(ResidueType res, int arrayIndex) {
		sequence[arrayIndex].setResidueType(res);
	}
	
	/**
	 * Changes the type of an Amino Acid at a given index
	 * @param res: the new Amino Acid Residue Type
	 * @param index: the PDB index of the residue to replace
	 */
	public void updateAminoResidueAt(ResidueType res, int index) {
		updateAminoResidue(res, firstIndex + index);
	}
	
	/**
	 * Changes the Amino Acid at a given index
	 * @param aa: the new Amino Acid
	 * @param arrayIndex: the array index of the Amino Acid to replace
	 */
	public void setAmino(AminoAcid aa, int arrayIndex) { sequence[arrayIndex] = aa; }
	
	/**
	 * Sets the AminoAcid object at the given index
	 * @param aa: the AminoAcid to set
	 * @param index: the index to set the AminoAcid object at
	 */
	public void setAminoAt(AminoAcid aa, int index) { setAmino(aa, index - firstIndex); }
	
	/**
	 * Get the protein's amino acid sequence
	 * @return the protein's amino acid sequence as a String
	 */
	public String toSequence() { 
		StringBuilder builder = new StringBuilder();
		
		for(AminoAcid aa: sequence) {
			if(aa != null) {
				builder.append(aa.toChar());
			} else {
				builder.append("_");
			}
		}
		
		return builder.toString();
	}
	
	/**
	 * Get a string representative of the ProteinChain's secondary structure
	 * Note that this does NOT include primary structure, just secondary structure if it is assigned
	 * @return: a string representative of the ProteinChain's secondary structure
	 */
	public String toSecondarySequence() { 
		StringBuilder builder = new StringBuilder();
		
		for(AminoAcid aa: sequence) {
			if(aa != null) {
				if(aa.secondary() != null) {
					builder.append(aa.secondary().toLetter());
				} else {
					builder.append(MISSING_SECONDARY);
				}
			} else {
				builder.append(MISSING_RESIDUE);
			}
		}
		
		return builder.toString();
	}
	
	public String toSwitches() {
		StringBuilder builder = new StringBuilder();
		
		for(AminoAcid aa: sequence) {
			if(aa != null) {
				switch(aa.switchType()) {
				case NONE:			builder.append("-");		break;
				case ASSINGED:		builder.append("|");		break;
				case UNASSIGNED:		builder.append("^");		break;
				default:
				}
			} else {
				builder.append(MISSING_RESIDUE);
			}
		}
		
		return builder.toString();
	}
	
	/**
	 * 
	 * @return: A string that can be written to a file as a FASTA representing this protein chain
	 */
	public String toFasta() {
		StringBuilder fastaBuilder = new StringBuilder();
		boolean fullLine = false;
		
		fastaBuilder.append(">" + getProteinName() + ":" + Character.toUpperCase(getID()) + STATIC_FASTA_HEADER + "\n");
			
		for(int i = 0; i < length(); ++i) {
			fullLine = false;
				
			if(getAmino(i) == null) {
				fastaBuilder.append("-");
			} else {
				fastaBuilder.append(getAmino(i).toChar());
			}
				
			if((i+1) % 80 == 0) { fastaBuilder.append("\n"); fullLine = true; }
		}
		if(!fullLine) { fastaBuilder.append("\n"); }
		
		return fastaBuilder.toString();
	}
	
	/**
	 * Two ProteinChains are considered to be equal if they have the same Amino Acid residue sequence
	 * NOTE: this method only takes sequence into consideration
	 * NOTE: use a sequence aligner before using this method!
	 * @param other: the Sequence of which to compare this
	 * @return true if the sequences are the same (or could be the same), otherwise false
	 */
	public boolean equals(ProteinChain other) {
		return equals(other, 0);
	}
	
	/**
	 * Two ProteinChains are considered to be equal if they have the same Amino Acid residue sequence
	 * NOTE: this method only takes sequence into consideration
	 * NOTE: use a sequence aligner before using this method!
	 * @param other: the Sequence of which to compare this
	 * @param allowedErrors: number of errors allowed
	 * @return true if the sequences are the same (or could be the same), otherwise false
	 */
	public boolean equals(ProteinChain other, int allowedErrors) {
		if(sequence.length != other.sequence.length) { return false; }
		
		int errors = 0;
		
		for(int index = 0; index < sequence.length; ++index) {
			if(!sequence[index].residueType().couldBe(other.sequence[index].residueType())) {
				++errors;
			}
		}
		
		if(errors < allowedErrors) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Fills all nulls with BadAminos
	 */
	public void fillBlanks() {
		for(int index = 0; index < sequence.length; ++index) {
			if(sequence[index] == null) {
				sequence[index] = new BadAmino();
			}
		}
	}
	
	/**
	 * Get's the length of the protein chain in amino acids
	 * @return: the physical length of the chain: (the array size)
	 */
	public int length() { return sequence.length; }
	
	/**
	 * get's the index of the last amino acid in the chain, +1
	 * @return length of the array, offset by whatever index number the chain starts at
	 */
	public int lastIndex() { return sequence.length + firstIndex; }
	
	/**
	 * Gets the flexibility of a certain residue index, provided that it is known
	 * If the data could not be found, the value will be Double.NaN
	 * 
	 * @param index: the index to get the flexibility for
	 * @return: the flexibility
	 * @throws NullPointerException: if the flexibility data is not initialized
	 */
	public double getFlex(int index) {
		if(flexInit) {
			if((index < sequence.length) && (index >= 0)) {
				return sequence[index].flex();
			} else {
				throw new NullPointerException("No reside for index [" + index + "]");
			}
		} else {
			throw new NullPointerException("Flexibilities are not initialized.");
		}
	}
	
	/**
	 * Insert null residues into the chain: designed for use in aligning chains
	 * Runs in O(n), where n is the number of residues in the chain
	 * Blanks will be inserted before the given index, so if given the parameters (0, 2)
	 * the result will be [blank] [blank] [chain residues]
	 * 
	 * Also note that inserting blanks at the beginning of a chain (position 0)
	 * will change what the first PDB index is listed as.
	 * Reset the first PDB index if necessary with setFirstIndex(int index)
	 * 
	 * @param index: what index to insert the blanks
	 * @param blanks: how many blanks to insert
	 */
	public void insertBlanks(int index, int blanks) {
		if(blanks < 0) {
			trimTrailingBlanks(blanks*-1);
			return;
		}
		
		//if we're inserting blanks at the start of the chain, update the first PDB index
		if(index == 0) { firstIndex -= blanks; }
		
		//allocate enough room for the new chain
		AminoAcid[] newSequence = new AminoAcid[sequence.length + blanks];
		
		int sequenceIndex = 0;
		int newSequenceIndex = 0;
		
		for(; sequenceIndex < sequence.length; ++sequenceIndex, ++newSequenceIndex) {
			if(sequenceIndex == index) {
				newSequenceIndex += blanks;
			}
			newSequence[newSequenceIndex] = sequence[sequenceIndex];
		}
		
		sequence = newSequence;
	}
	
	/**
	 * Tells the number of trailing blanks
	 * @return
	 */
	int trailingBlanks() {
		//how many blanks will actually be trimmed
		int trimming = 0;
		for(int i = 0; i < sequence.length; ++i) {
			if(sequence[sequence.length-1-i] != null) { break; }
			++trimming;
		}
		return trimming;
	}
	
	/**
	 * Trims trailing null residues from the ProteinChain
	 * @param trim
	 */
	void trimTrailingBlanks(int trimSize) {
		if(trimSize <= 0) { return; }
		
		//how many blanks will actually be trimmed
		int trimming = min(trimSize, trailingBlanks());
		
		AminoAcid newSequence[] = new AminoAcid[sequence.length - trimming];
		for(int i = 0; i < newSequence.length; ++i) { newSequence[i] = sequence[i]; }
		sequence = newSequence;
	}
	
	/**
	 * Gets the information on the amino acid at the given position
	 * @param index: the position
	 * @return: a string with the data about that amino acid
	 */
	public String getResidueInfo(int index) {
		NumberFormat formatter = new DecimalFormat("#0.000");
		String retVal = sequence[index].toChar() + " ";
		
		if(flexInit) {
			retVal += "[";
			
			if(!Double.isNaN(sequence[index].flex())) {
				retVal += formatter.format(sequence[index].flex());
			} else {
				retVal += "-----";
			}
			
			retVal += "] ";
		}
		
		retVal += "(" + sequence[index] + ")";
		return retVal;
	}
	
	/**
	 * Automatically assigns charges based on secondary structure averages to all residues in the chain
	 */
	public void autoAssignCharges() {
		qp("Assigning charges based on primary and secondary structure to: " + fullID());
		for(AminoAcid aa: sequence) {
			if(aa != null) {
				aa.autoAssignCharges();
			}
		}
		/* Don't delete this!
		 * If we don't call garbage collection, then the system will run out of memory
		 * when assigning charge to multiple chains
		 */
		System.gc();
	}
	
	/**
	 * Cleans Bad Amino acids out of the sequence, deleting them for good
	 */
	public void clean() {
		ArrayList<AminoAcid> aminoList = new ArrayList<AminoAcid>();
		for(AminoAcid aa: sequence) {
			if(aa == null) { aminoList.add(aa); }
			else if(!(aa instanceof BadAmino)) { aminoList.add(aa); }
		}
		
		sequence = new AminoAcid[aminoList.size()];
		aminoList.toArray(sequence);
	}
	
	public int firstIndex() { return firstIndex; }
	
	/**
	 * Generates a chain of AminoAcid Triplets with the same sequence as this chain
	 * 	Note that the generated chain is a deep copy, not a shallow one!
	 * @return: a TripletChain with the same sequence as this chain
	 */
	public TripletChain generateTripletChain() {
		TripletChain retVal = new TripletChain(getProteinName(), getID(), sequence.length, getSource());
		
		AminoAcid seq0 = null, seq1 = null;
		if(sequence[0] != null) { seq0 = sequence[0].clone(); }
		if(sequence[1] != null) { seq1 = sequence[1].clone(); }
		
		AminoTriplet startTriplet = new AminoTriplet(null, seq0, seq1);
		retVal.setTriplet(startTriplet, 0);
		
		for(int index = 1; index < sequence.length-1; ++index) {
			AminoAcid seq_l = null, seq_m = null, seq_g = null;
			
			if(sequence[index-1] != null) { seq_l = sequence[index-1].clone(); }
			if(sequence[index]   != null) { seq_m = sequence[index].clone(); }
			if(sequence[index+1] != null) { seq_g = sequence[index+1].clone(); }
			
			AminoTriplet at = new AminoTriplet(seq_l, seq_m, seq_g);
			retVal.setTriplet(at, index);
		}
		
		AminoAcid seq0x = null, seq1x = null;
		if(sequence[sequence.length-2] != null) { seq0x = sequence[sequence.length-2].clone(); }
		if(sequence[sequence.length-1] != null) { seq1x = sequence[sequence.length-1].clone(); }
		
		AminoTriplet endTriplet = new AminoTriplet(seq0x, seq1x, null);
		retVal.setTriplet(endTriplet, sequence.length-1);
		
		retVal.vkabat_init = vkabat_init;
		retVal.entropy_init = entropy_init;
		retVal.flexInit = flexInit;
		retVal.description = description;
		retVal.missingDSSP = missingDSSP;
		
		return retVal;
	}
	
	/**
	 * Make a deep copy of the protein chain
	 */
	public ProteinChain clone() {
		AminoAcid sequenceClone[] = new AminoAcid[sequence.length];
		
		for(int index = 0; index < sequence.length; ++index) {
			sequenceClone[index] = sequence[index].clone();
		}
		
		ProteinChain myClone = new ProteinChain(getProteinName(), getID(), sequenceClone, firstIndex, getSource());
		
		myClone.vkabat_init = vkabat_init;
		myClone.entropy_init = entropy_init;
		myClone.flexInit = flexInit;
		myClone.description = description;
		myClone.missingDSSP = missingDSSP;
		
		return myClone;
	}
	
	/** @param val: true if the DSSP file for this protein chain is known to be missing, else false */
	public void setMissingDSSP(boolean val) { missingDSSP = val; }
	
	/** @return: true if the DSSP file for this protein chain is known to be missing */
	public boolean missingDSSP() { return missingDSSP; }
	
	/**
	 * Returns a String representing the ProteinChain object and it's sequence
	 */
	public String toString() {
		return "[" + fullID() + "] " + toSequence();
	}
}
