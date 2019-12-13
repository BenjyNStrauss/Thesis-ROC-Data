package bio;

import bio.exceptions.ResidueAlignmentException;
import bio.exceptions.ResidueAlignmentRuntimeException;
import bio.exceptions.ResidueIndexOutOfBoundsException;
import bio.tools.SequenceAligner;

/**
 * An upgraded version of ChainCluster that also aligns the chains in the cluster
 * @author Benjy Strauss
 *
 */

public class AlignedCluster extends ChainCluster {
	private static final long serialVersionUID = 1L;
	
	public static final char MISSING_DSSP_FILE = '#';
	public static final char OUTSIDE_DSSP_RANGE = '*';
	
	private static final String MISSING = " ";
	
	private class PotenialSwitch {
		public AminoAcid amino;
		public SwitchType isSwitch;
		public void mark() { amino.setSwitch(isSwitch); }
	}
	
	/**
	 * Create a new ChainCluster
	 * @param dominant: the dominant ProteinChain
	 */
	public AlignedCluster(ProteinChain dominant) {
		super(dominant);
	}
	
	/**
	 * Create a new ChainCluster object:
	 * @param ref: the reference chain
	 * @param members: other chains
	 */
	public AlignedCluster(ProteinChain dominant, ProteinChain... members) {
		super(dominant);
		for(ProteinChain pc: members) { addChain(pc); }
	}
	
	/**
	 * Adds a ProteinChain to the cluster, marking switches as it finds them
	 * IMPORTANT: this does NOT mark switches in the dominant chain, but rather in the chain being added!
	 * @param pc: the ProteinChain to add
	 */
	public void addAndMarkChain(ProteinChain pc) {
		pc = SequenceAligner.markSwitches(pc, getDominant(), 1);
		addChain(pc);
	}
	
	/**
	 * Adds a chain to the cluster
	 * @throws ResidueAlignmentRuntimeException 
	 */
	public void addChain(ProteinChain chain) {
		
		int domLen = getDominant().length();
		
		try {
			SequenceAligner.superAlign(getDominant(), chain);
			
			super.addChain(chain);
		} catch (ResidueAlignmentException e) {
			throw new ResidueAlignmentRuntimeException("Cannot cluster " + chain.fullID() + " with " + getDominant().fullID());
		}
		
		//if the length of the dominant chain changed, we have to re-align everything
		if(domLen != getDominant().length()) {
			ProteinChain[] chains = super.getChains();
			
			for(ProteinChain prCh: chains) {
				try {
					SequenceAligner.superAlign(getDominant(), prCh);
				} catch (ResidueAlignmentException e) {
					throw new ResidueAlignmentRuntimeException("Chain " + prCh.fullID() + " was modified!");
				}
			}
		}
		
		trimFix();
	}
	
	/**
	 * Marks all the switches in the dominant chain: based on all the other chains at once
	 * Used to mark switches that cannot be detected by comparing chains one-by-one
	 * (Specifically, where a single chain compare would fail the length requirement to register a switch)
	 * 
	 * This is functionally the same as markDominant(), but uses a slightly different algorithm
	 * 
	 * @param runLength: how many secondary structure differences in a row do there have to be to mark a switch
	 * 		if this value is less than 1, it will be set to 1
	 */
	public void verifySwitches(int runLength) {
		if(runLength <= 0) { runLength = 1; }
		
		//int dominantOffset = getDominant().firstIndex();
		
		//set up the potential switches
		PotenialSwitch potenial[] = new PotenialSwitch[getDominant().length()];
		for(int index = 0; index < potenial.length; ++index) {
			potenial[index] = new PotenialSwitch();
			potenial[index].amino = getDominant().getAmino(index);
			potenial[index].isSwitch = SwitchType.NONE;
		}
		
		ProteinChain others[] = getChains();
		
		//determine all of the potential switches
		for(ProteinChain chain: others) {
			for(int index = 0; index < potenial.length; ++index) {
				if(potenial[index].amino != null) {
					AminoAcid test = chain.getAmino(index);
					if(test != null) {
						if(potenial[index].amino.secondary().simpleClassify() != test.secondary().simpleClassify()) {
							//make sure we don't overwrite ordered with disordered
							if(potenial[index].isSwitch != SwitchType.ASSINGED) {
								//determine the type of switch
								if(potenial[index].amino.secondary().simpleClassify() == SecondarySimple.Unassigned) {
									potenial[index].isSwitch = SwitchType.UNASSIGNED;
								} else if(test.secondary().simpleClassify() == SecondarySimple.Unassigned) {
									potenial[index].isSwitch = SwitchType.UNASSIGNED;
								} else {
									potenial[index].isSwitch = SwitchType.ASSINGED;
								}
							}
						}
					}
				}
			}
		}
		
		if(runLength == 1) {
			for(int index = 0; index < potenial.length; ++index) {
				potenial[index].mark();
			}
		} else {
			int lastNonSwitch = -1;
			
			for(int index = 0; index < potenial.length; ++index) {
				if(potenial[index].isSwitch != SwitchType.NONE) {
					if((index - lastNonSwitch -1) >= runLength) {
						for(int sub_index = lastNonSwitch+1; sub_index <= index; ++sub_index) {
							potenial[sub_index].mark();
						}
					}
					lastNonSwitch = index;
				}
			}
		}
	}
	
	/**
	 * 
	 * @return
	 */
	public String toSecStructCSV() {
		ProteinChain chainArray[] = getAllChains();
		boolean finished[] = new boolean[chainArray.length];
		
		StringBuilder builder = new StringBuilder();
		builder.append("Index,Residue,");
		
		for(ProteinChain prCh: chainArray) {
			builder.append(prCh.getProteinName() + prCh.getID() + ",");
		}
		
		builder.append("\n");
		
		for(int index = 0; isFinished(finished); ++index) {
			builder.append(index + ",");
			
			try {
				if(getDominant().getAmino(index) == null) {
					builder.append("-,");
				} else {
					builder.append(getDominant().getAmino(index).toChar() + ",");
				}
			} catch (ResidueIndexOutOfBoundsException RIOOBE) {
				builder.append("-,");
			}
			
			for(int index2 = 0; index2 < chainArray.length; ++index2) {
				
				if(!finished[index2]) {
					//if we throw an exception, we're out of bounds
					try {
						
						if(chainArray[index2].getAmino(index) == null) {
							builder.append(MISSING);
						} else {
							if(chainArray[index2].getAmino(index).secondary() == null) {
								if(chainArray[index2].missingDSSP()) {
									builder.append(MISSING_DSSP_FILE);
								} else {
									builder.append(OUTSIDE_DSSP_RANGE);
								}
							} else {
								builder.append(chainArray[index2].getAmino(index).secondary().simpleClassify().toChar());
							}
							
							try {
								if(chainArray[0].getAmino(index) != null) {
									if(!chainArray[index2].getAmino(index).equals(chainArray[0].getAmino(index))) {
										builder.append("(" + chainArray[index2].getAmino(index).toChar() + ")");
									}
								} else {
									builder.append("(" + chainArray[index2].getAmino(index).toChar() + ")");
								}
								
							} catch (ResidueIndexOutOfBoundsException RIOOBE) {
								builder.append("(" + chainArray[index2].getAmino(index).toChar() + ")");
							}
						}
						
						builder.append(",");
					} catch (ResidueIndexOutOfBoundsException RIOOBE) {
						//if the exception is because the index is too large, then the chain is done
						if(RIOOBE.tooLarge()) {
							finished[index2] = true;
						}
						builder.append(" ,");
					}
				} else {
					builder.append(" ,");
				}
			}
			builder.append("\n");
		}
		return builder.toString();
	}
	
	/**
	 * Debug method to make sure alignment works
	 */
	public void debugClass() {
		ProteinChain chainArray[] = getAllChains();
		boolean finished[] = new boolean[chainArray.length];
		
		qp("testing");
		
		for(int index = 0; isFinished(finished); ++index) {
			//qp("index is " + index);
			
			for(int index2 = 0; index2 < chainArray.length; ++index2) {
				if(!finished[index2]) {
					//if we throw an exception, we're out of bounds
					try {
						System.out.print(chainArray[index2].getAmino(index).toChar() + "-");
					} catch (ResidueIndexOutOfBoundsException RIOOBE) {
						//if the exception is because the index is too large, then the chain is done
						if(RIOOBE.tooLarge()) {
							finished[index2] = true;
						}
						System.out.print(" -");
					}
				} else {
					System.out.print(" -");
				}
			}
			qp("");
		}
	}
	
	/**
	 * Mark all switches in the reference chain
	 * A residue in the reference chain is marked as a switch if there exists a corresponding
	 * consecutive segment of "switchLength" residues in another chain in the set, each with
	 * different secondary structure.
	 */
	public void markDominant() { markDominant(getSwitchLength());	}
	
	/**
	 * Mark all switches in the reference chain
	 * A residue in the reference chain is marked as a switch if there exists a corresponding
	 * consecutive segment of N residues in another chain in the set, each with different
	 * secondary structure.
	 * 
	 * @param switchLen: the parameter N, how many consecutive residues must have different
	 *  secondary structures in order for a switch to me marked
	 */
	public void markDominant(int switchLen) {
		ProteinChain chainArray[] = getChains();
		SequenceAligner.markSwitches(getDominant(), switchLen, chainArray);
	}
	
	/**
	 * 
	 * @param test
	 * @return
	 */
	private static boolean isFinished(boolean[] test) {
		boolean retVal = true;
		for(boolean b: test) { retVal = retVal && b; }
		return !retVal;
	}
	
	/**
	 * Prevents unnessesary trailing blanks
	 */
	private void trimFix() {
		int domLen = getDominant().length();
		int trimTo = domLen - getDominant().trailingBlanks();
		
		ProteinChain chains[] = getChains();
		for(ProteinChain ch: chains) {
			trimTo = min(trimTo, ch.length() - ch.trailingBlanks());
		}
		
		for(ProteinChain ch: chains) {
			ch.trimTrailingBlanks(ch.length() - trimTo);
		}
	}
}
