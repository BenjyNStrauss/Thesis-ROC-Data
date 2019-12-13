package bio.tools;

import bio.AminoAcid;
import bio.DataSource;
import bio.ProteinChain;
import bio.SecondarySimple;
import bio.SwitchType;
import bio.exceptions.MissingDataException;
import bio.exceptions.ResidueAlignmentException;
import util.BaseTools;

/**
 * The job of the SequenceAligner class is to align two protein sequences,
 * thus fixing the problems of different sources having different index numbers
 * for different residues
 * 
 * @author Benjy Strauss
 *
 */

public class SequenceAligner extends BaseTools {
	public static final int DEFAULT_REGION_MATCH_LENGTH = 32;
	public static final int DEFAULT_SUPER_ALIGN_SUBSEQ_EXIT_CONST = 4;
	private static final int SEARCH_START = 0;
	private static final int SEARCH_OFFSET = 4;
	private static final int MIN_SIZE_FOR_ALIGNABLE_SEGMENT = 8;
	
	public static final char INSERTED = '_';
	
	private static int SUPER_ALIGN_SUBSEQ_EXIT = DEFAULT_SUPER_ALIGN_SUBSEQ_EXIT_CONST;
	
	/**
	 * Construct a new SequenceAligner object
	 * Typically unused, but here in case someone wants to make a subclass
	 */
	public SequenceAligner() { }
	
	/**
	 * Determines an alignment value of 2 protein chains
	 * The alignment value is the index of chain2 that chain1 starts at
	 * If the alignment value is negative, chain1 has something extra at the start
	 * 
	 * @param chain1: the protein chain to align to chain2
	 * @param chain2: the protein chain to align chain1 too
	 * @return the index of chain2 that chain1 starts at
	 * @throws ResidueAlignmentException: if the chains could not be aligned using a sequence of 32 residues
	 */
	public static int alignSequence(ProteinChain chain1, ProteinChain chain2) throws ResidueAlignmentException {
		return alignSequence(chain1, chain2, DEFAULT_REGION_MATCH_LENGTH);
	}
	
	/**
	 * Determines an alignment value of 2 protein chains
	 * The alignment value is the index of chain2 that chain1 starts at
	 * If the alignment value is negative, chain1 has something extra at the start
	 * 
	 * @param chain1: the protein chain to align to chain2
	 * @param chain2: the protein chain to align chain1 too
	 * @return the index of chain2 that chain1 starts at
	 * @throws ResidueAlignmentException: if the chains could not be aligned using a sequence of the given length
	 */
	public static int alignSequence(ProteinChain chain1, ProteinChain chain2, int matchRegionLength) throws ResidueAlignmentException {
		return alignSequence(chain1.toSequence(), chain2.toSequence(), matchRegionLength);
	}
	
	/**
	 * Determines an alignment value of 2 protein chains
	 * The alignment value is the index of chain2 that chain1 starts at
	 * If the alignment value is negative, chain1 has something extra at the start
	 * 
	 * @param seq1: a string who's characters represent amino acid residues 
	 * @param seq2: a string who's characters represent amino acid residues 
	 * @return the index of chain2 that chain1 starts at
	 * @throws ResidueAlignmentException: if the chains could not be aligned using a sequence of 32 residues
	 */
	public static int alignSequence(String seq1, String seq2) throws ResidueAlignmentException {
		return alignSequence(seq1, seq2, DEFAULT_REGION_MATCH_LENGTH);
	}
	
	/**
	 * Determines an alignment value of 2 protein chains
	 * The alignment value is the index of chain2 that chain1 starts at
	 * If the alignment value is negative, chain1 has something extra at the start
	 * 
	 * @param seq1: a string who's characters represent amino acid residues 
	 * @param seq2: a string who's characters represent amino acid residues 
	 * @param matchRegionLength
	 * @return the index of chain2 that chain1 starts at
	 * @throws ResidueAlignmentException: if the chains could not be aligned using a sequence of the given length
	 */
	public static int alignSequence(String seq1, String seq2, int matchRegionLength) throws ResidueAlignmentException {
		
		for(int offset = SEARCH_START; (offset+matchRegionLength) < seq1.length() ; offset += SEARCH_OFFSET) {
			String tryAndMatch = seq1.substring(offset, offset+matchRegionLength);
			
			//if the sequence isn't unique, try again
			if(seq1.indexOf(tryAndMatch) != seq1.lastIndexOf(tryAndMatch)) { continue; }
			
			int firstInSeq2 = seq2.indexOf(tryAndMatch);
			int lastInSeq2 = seq2.lastIndexOf(tryAndMatch);
			
			//if it's not found in sequence 2
			if(firstInSeq2 == -1) { continue; }
			
			//if it's not unique in sequence 2
			if(firstInSeq2 != lastInSeq2) { continue; }
			
			return firstInSeq2 - offset;
		}
		throw new ResidueAlignmentException("Could not find a matching sequence of length="+matchRegionLength + " in " +
				seq1 + " and " + seq2);
	}
	
	/**
	 * Adjusts the first PDB index of chain1 so it lines up with chain2
	 * @param chain1: the chain we are aligning
	 * @param chain2: the chain being used as a reference
	 * @throws ResidueAlignmentException
	 */
	public static void adjustSequence(ProteinChain chain1, ProteinChain chain2) throws ResidueAlignmentException {
		if(chain1.getSource() == chain2.getSource() && chain1.getSource() != DataSource.OTHER) {
			int alignVal = alignSequence(chain1, chain2);
			chain1.setFirstIndex(alignVal + chain2.firstIndex());
		} else {
			superAlign(chain1, chain2);
		}
	}
	
	/**
	 * Helper method for SuperAlign
	 * 
	 * @param chain1
	 * @param chain2
	 * @return
	 * @throws ResidueAlignmentException
	 */
	private static int alignSequenceReverse(ProteinChain chain1, ProteinChain chain2) throws ResidueAlignmentException {
		int retVal = 0;
		
		try {
			retVal = alignSequenceReverse(chain1.toSequence(), chain2.toSequence());
		} catch (ResidueAlignmentException RAE) {
			throw new ResidueAlignmentException("Could not find a matching sequence of length 32 in " +
					chain1.getProteinName()+chain1.getID() + " and " + chain2.getProteinName()+chain2.getID());
		}
		
		return retVal;
	}
	
	/**
	 * 
	 * @param seq1
	 * @param seq2
	 * @return
	 * @throws ResidueAlignmentException
	 */
	private static int alignSequenceReverse(String seq1, String seq2)  throws ResidueAlignmentException {
		
		seq1 = new StringBuilder(seq1).reverse().toString();
		seq2 = new StringBuilder(seq2).reverse().toString();
		
		for(int offset = SEARCH_START; (offset+DEFAULT_REGION_MATCH_LENGTH) < seq1.length() ; offset += SEARCH_OFFSET) {
			String tryAndMatch = seq1.substring(offset, offset+DEFAULT_REGION_MATCH_LENGTH);
			
			//if the sequence isn't unique, try again
			if(seq1.indexOf(tryAndMatch) != seq1.lastIndexOf(tryAndMatch)) { continue; }
			
			int firstInSeq2 = seq2.indexOf(tryAndMatch);
			int lastInSeq2 = seq2.lastIndexOf(tryAndMatch);
			
			//if it's not found in sequence 2
			if(firstInSeq2 == -1) { continue; }
			
			//if it's not unique in sequence 2
			if(firstInSeq2 != lastInSeq2) { continue; }
			
			return firstInSeq2 - offset;
		}
		throw new ResidueAlignmentException("Could not find a matching sequence of length="+DEFAULT_REGION_MATCH_LENGTH + " in " +
				seq1 + " and " + seq2);
	}

	/**
	 * Aligns the chains by inserting blank residues wherever needed
	 * Designed for aligning chains whose sequences are not 100% identical
	 * 
	 * @param dominant
	 * @param chain
	 * @throws ResidueAlignmentException
	 */
	public static void superAlign(ProteinChain dominant, ProteinChain chain) throws ResidueAlignmentException {
		//first, verify that the chains are aligned
		int alignmentValue = SequenceAligner.alignSequence(dominant, chain);
		int reverseAlignmentValue = SequenceAligner.alignSequenceReverse(chain, dominant);
		//qp("alignmentValue: " + alignmentValue);
		
		//This line added 2/13/19 to fix a bug if Uniprot started at 1 and DSSP at 0
		dominant.setFirstIndex(alignmentValue);
		
		if(alignmentValue > 0) {
			dominant.insertBlanks(0, alignmentValue);
		} else {
			chain.insertBlanks(0, alignmentValue*-1);
		}
		
		superAlignHelper(dominant, chain, 0);
		
		if(reverseAlignmentValue < 0) {
			dominant.insertBlanks(dominant.length(), reverseAlignmentValue*-1);
		} else {
			chain.insertBlanks(chain.length(), reverseAlignmentValue);
		}
		
		superAlignHelperReverse(dominant, chain, 0);
		
		int minStartValue = min(dominant.firstIndex(), chain.firstIndex());
		dominant.setFirstIndex(minStartValue);
		chain.setFirstIndex(minStartValue);
	}
	
	/**
	 * Aligns the chains by inserting blank residues wherever needed
	 * Super-align in the forwards direction only
	 * 
	 * @param dominant
	 * @param chain
	 * @throws ResidueAlignmentException
	 */
	public static void superAlignForwards(ProteinChain dominant, ProteinChain chain) throws ResidueAlignmentException {
		//first, verify that the chains are aligned
		int alignmentValue = SequenceAligner.alignSequence(dominant, chain);
		
		if(alignmentValue > 0) {
			dominant.insertBlanks(0, alignmentValue);
		} else {
			chain.insertBlanks(0, alignmentValue*-1);
		}
		
		superAlignHelper(dominant, chain, 0);
		
		int minStartValue = min(dominant.firstIndex(), chain.firstIndex());
		dominant.setFirstIndex(minStartValue);
		chain.setFirstIndex(minStartValue);
	}
	
	/**
	 * This gets called recursively
	 * Used to align chains with gaps based on 50% residue-type difference
	 * 
	 * @param dominant
	 * @param chain
	 * @throws ResidueAlignmentException 
	 */
	private static void superAlignHelper(ProteinChain dominant, ProteinChain chain, int startHere) {
		int min_length = min(dominant.length(), chain.length());
		
		int incorrectRunStart = -1;
		int incorrectRun = 0;
		String dom = dominant.toSequence();
		String cha = chain.toSequence();
		
		boolean edited = false;
		
		for(int index = startHere; index < min_length; ++index) {
			//factor out places where blanks were inserted
			if(dom.charAt(index) != INSERTED && cha.charAt(index) != INSERTED) {
				if(dom.charAt(index) != cha.charAt(index)) {
					++incorrectRun;
				} else {
					--incorrectRun;
				}
				
				if(incorrectRun <= 0) { 
					incorrectRunStart = index+1;
					incorrectRun = 0;
				}
				
				//qp("index       : " + index);
				//qp("incorrectRun: " + incorrectRun);
				
				//we found something that we have to align
				if(incorrectRun >= MIN_SIZE_FOR_ALIGNABLE_SEGMENT) {
					String dd = dom.substring(incorrectRunStart);
					String cc = cha.substring(incorrectRunStart);
					
					int miniAlignVal = 0;
					
					try {
						miniAlignVal = SequenceAligner.alignSequence(dd, cc);
					} catch (ResidueAlignmentException e) {
						try {
							miniAlignVal = SequenceAligner.alignSequence(dd, cc, SUPER_ALIGN_SUBSEQ_EXIT);
						} catch (ResidueAlignmentException e2) {
							//at this point, we can't find a string of 4 identical residues, so we give up!
							return;
						}
					}
					
					if(miniAlignVal > 0) {
						dominant.insertBlanks(incorrectRunStart, miniAlignVal);
					} else {
						chain.insertBlanks(incorrectRunStart, miniAlignVal*-1);
					}
					
					if(miniAlignVal != 0) { edited = true; }
					
					break;
				}
			}
		}
		
		//if we've modified one or more sequence, recurse to make sure that there isn't more work to do...
		if(edited) {
			/*qp(dominant);
			qp(dominant.length());
			qp(chain);
			qp(chain.length());
			qp(incorrectRunStart);*/
			superAlignHelper(dominant, chain, incorrectRunStart-1);
		}
	}
	
	/**
	 * 
	 * This gets called recursively
	 * Used to align chains with gaps based on 50% residue-type difference
	 * 
	 * @param dominant
	 * @param chain
	 * @throws ResidueAlignmentException 
	 */
	private static void superAlignHelperReverse(ProteinChain dominant, ProteinChain chain, int startHere) {
		int min_length = min(dominant.length(), chain.length());
		
		int incorrectRunStart = -1;
		int incorrectRun = 0;
		String dom = new StringBuilder(dominant.toSequence()).reverse().toString();
		String cha = new StringBuilder(chain.toSequence()).reverse().toString();
		
		boolean edited = false;
		
		for(int index = startHere; index < min_length; ++index) {
			//factor out places where blanks were inserted
			if(dom.charAt(index) != INSERTED && cha.charAt(index) != INSERTED) {
				if(dom.charAt(index) != cha.charAt(index)) {
					++incorrectRun;
				} else {
					--incorrectRun;
				}
				
				if(incorrectRun <= 0) { 
					incorrectRunStart = index+1;
					incorrectRun = 0;
				}
				
				//qp("index       : " + index);
				//qp("incorrectRun: " + incorrectRun);
				
				//we found something that we have to align
				if(incorrectRun >= MIN_SIZE_FOR_ALIGNABLE_SEGMENT && incorrectRunStart >= 0) {
					String dd = dom.substring(incorrectRunStart);
					String cc = cha.substring(incorrectRunStart);
					
					int miniAlignVal = 0;
					
					try {
						miniAlignVal = SequenceAligner.alignSequence(dd, cc);
					} catch (ResidueAlignmentException e) {
						try {
							miniAlignVal = SequenceAligner.alignSequence(dd, cc, SUPER_ALIGN_SUBSEQ_EXIT);
						} catch (ResidueAlignmentException e2) {
							//at this point, we can't find a string of 4 identical residues, so we give up!
							return;
						}
					}
					
					if(miniAlignVal > 0) {
						dominant.insertBlanks(dominant.length()-incorrectRunStart, miniAlignVal);
					} else {
						chain.insertBlanks(chain.length()-incorrectRunStart, miniAlignVal*-1);
					}
					
					if(miniAlignVal != 0) { edited = true; }
					break;
				}
			}
		}
		
		//if we've modified one or more sequence, recurse to make sure that there isn't more work to do...
		if(edited) {
			superAlignHelperReverse(dominant, chain, incorrectRunStart-1);
		}
	}
	
	/**
	 * Marks switch-like regions in the ProteinChain "chain"
	 * Switch-like regions are regions of at least switchLen residues, where all residues in
	 * chain have a different secondary structure than in "ref"
	 * Assumes chains are pre-aligned
	 * 
	 * @param chain: The chain to mark switches
	 * @param refs: The chains to use as reference
	 * @param switchLen: The length of the region necessary to determine a switch
	 * @return: chain with switches marked
	 */
	public static ProteinChain markSwitches(ProteinChain chain, int switchLen, ProteinChain... refs ) {
		for(ProteinChain ref: refs) {
			if(!ref.missingDSSP()) {
				chain = markSwitches(chain, ref, 1, false);
			}
		}
		
		int switchRun = 0;
		for(int index = 0; index < chain.length(); ++index) {
			if(chain.getAmino(index) == null) { continue; }	
			if(chain.getAmino(index).isSwitch()) {
				++switchRun;
			} else {
				if(switchRun < switchLen) {
					//unmark marked switches
					for(int unmarkThis = index-switchRun; unmarkThis < index; ++unmarkThis) {
						if(chain.getAmino(unmarkThis) != null) {
							chain.getAmino(unmarkThis).setSwitch(SwitchType.NONE);
						}
					}
				}
				switchRun = 0;
			}
		}
		
		return chain;
	}
	
	/**
	 * Marks switch-like regions in the ProteinChain "chain"
	 * Switch-like regions are regions of at least switchLen residues, where all residues in
	 * chain have a different secondary structure than in "ref"
	 * Assumes chains are pre-aligned
	 * 
	 * @param chain: The chain to mark switches
	 * @param ref: The chain to use as reference
	 * @param switchLen: The length of the region necessary to determine a switch
	 * @return: chain with switches marked
	 */
	public static ProteinChain markSwitches(ProteinChain chain, ProteinChain ref, int switchLen) {
		return markSwitches(chain, ref, switchLen, false);
	}
	
	/**
	 * Marks switch-like regions in the ProteinChain "chain"
	 * Switch-like regions are regions of at least switchLen residues, where all residues in
	 * chain have a different secondary structure than in "ref"
	 * Assumes chains are pre-aligned
	 * 
	 * @param chain: The chain to mark switches
	 * @param ref: The chain to use as reference
	 * @param switchLen: The length of the region necessary to determine a switch
	 * @param reset: Reset all the switches before starting?
	 * @return: chain with switches marked
	 */
	public static ProteinChain markSwitches(ProteinChain chain, ProteinChain ref, int switchLen, boolean reset) {
		int minChainLen = min(ref.length(), chain.length());
		
		if(chain.missingDSSP()) {
			throw new MissingDataException(chain);
		} else if(ref.missingDSSP()) {
			throw new MissingDataException(ref);
		}
		
		
		if(reset) {
			//reset switch values for test chain
			for(int i = 0; i < chain.length(); ++i) {
				if(chain.getAmino(i) != null) {
					chain.getAmino(i).setSwitch(SwitchType.NONE);
				}
			}
		}
		
		int runLength = 0;
		
		for(int index = 0; index < minChainLen; ++index) {
			AminoAcid r = ref.getAmino(index), t = chain.getAmino(index);
			
			try {
				//if both values are not null
				if(r != null && t != null) {
					//if the secondary structures are the same
					
					if(r.secondary() == null || t.secondary() == null) {
						throw new NullPointerException("[" + index + "]" + r + ":" + r.secondary() + " vs " + t + ":" + t.secondary());
					}
					
					if(r.secondary().simpleClassify().equals(t.secondary().simpleClassify())) {
						runLength = 0;
					//if the secondary structures are not the same: a switch is found
					} else {
						runLength++;
						if(runLength == switchLen) {
							//loop backwards to mark switches
							for(int markIndex = 0; markIndex < runLength; ) {
								AminoAcid m = chain.getAmino(index-markIndex);
								if(m != null) {
									if(m.switchType() != SwitchType.ASSINGED) {
										m.setSwitch(determineSwitchType(r.secondary().simpleClassify(), t.secondary().simpleClassify()));
									}
									++markIndex;
								}
							}
						} else if(runLength > switchLen) {
							if(t.switchType() != SwitchType.ASSINGED) {
								t.setSwitch(determineSwitchType(r.secondary().simpleClassify(), t.secondary().simpleClassify()));
							}
						}
					}
				}
			} catch (NullPointerException NPE) {
				//qp("chain = " + chain);
				//qp("ref   = " + ref);
				NPE.printStackTrace();
			}
		}
		
		return chain;
	}
	
	/**
	 * Determine the type of switch based on two categories of Secondary Structures
	 * @param a
	 * @param b
	 * @return: 
	 */
	protected static final SwitchType determineSwitchType(SecondarySimple a, SecondarySimple b) {
		if(a == SecondarySimple.Unassigned) {
			return SwitchType.UNASSIGNED;
		} else if(b == SecondarySimple.Unassigned) {
			return SwitchType.UNASSIGNED;
		} else {
			return SwitchType.ASSINGED;
		}
	}
}
