package bio;

import java.util.HashSet;
import bio.tools.SequenceAligner;

/**
 * Represents a cluster of ProteinChains
 * @author Benjy Strauss
 *
 */

public class ChainCluster extends BioObject {
	private static final long serialVersionUID = 1L;
	
	private static int switchLength = 3;
	
	private HashSet<ProteinChain> chainSet;
	private ProteinChain dominant;
	
	/**
	 * Create a new ChainCluster object:
	 * @param ref: the reference chain
	 */
	public ChainCluster(ProteinChain ref) {
		if(ref == null) { throw new NullPointerException("Reference chain cannot be null!"); }
		chainSet = new HashSet<ProteinChain>();
		dominant = ref;
	}
	
	/**
	 * Create a new ChainCluster object:
	 * @param ref: the reference chain
	 * @param members: other chains
	 */
	public ChainCluster(ProteinChain ref, ProteinChain... members) {
		if(ref == null) { throw new NullPointerException("Reference chain cannot be null!"); }
		chainSet = new HashSet<ProteinChain>();
		dominant = ref;
		
		for(ProteinChain pc: members) { addChain(pc); }
	}
	
	/**
	 * Set the consecutive number of residues with different secondary structures
	 *  that need to be different in order to mark a switch-like region
	 * @param i
	 */
	public static void setSwtichLength(int i) { switchLength = i; }
	
	/**
	 * Adds a ProteinChain to the cluster
	 * @param pc: the ProteinChain to add
	 * 
	 * TODO: this should also align the chains!
	 */
	public void addChain(ProteinChain pc) { chainSet.add(pc); }
	
	/**
	 * Adds a ProteinChain to the cluster, marking switches as it finds them
	 * @param pc: the ProteinChain to add
	 */
	public void addAndMarkChain(ProteinChain pc) {
		pc = SequenceAligner.markSwitches(pc, dominant, 1);
		addChain(pc);
	}
	
	/**
	 * Returns the ProteinChain Object used as the reference
	 * @return: reference chain.
	 */
	public ProteinChain getDominant() { return dominant; }
	
	/**
	 * Sets the dominant chain in the cluster
	 * used to modify the dominant chain in the cluster
	 * @param pc
	 */
	protected void setDominant(ProteinChain pc) { dominant = pc; }
	
	protected static int getSwitchLength() { return switchLength; }
	
	/**
	 * Returns an array containing all non-dominant chains
	 * @return
	 */
	public ProteinChain[] getChains() {
		ProteinChain[] retVal = new ProteinChain[chainSet.size()];
		chainSet.toArray(retVal);
		return retVal;
	}
	
	/**
	 * Returns an array containing all chains in the cluster
	 * @return
	 */
	public ProteinChain[] getAllChains() {
		ProteinChain[] preliminary = new ProteinChain[chainSet.size()];
		chainSet.toArray(preliminary);
		
		ProteinChain retVal[] = new ProteinChain[chainSet.size()+1];
		retVal[0] = dominant;
		
		for(int i = 0; i < preliminary.length; ++i) {
			retVal[i+1] = preliminary[i];
		}
		
		return retVal;
	}
	
	/**
	 * Obtains the total number of chains in the ChainCluster
	 * This includes the reference chain!
	 * @return: the number of chains in the cluster
	 */
	public int clusterSize() { return chainSet.size() + 1; }

	@Override
	public String impliedFileName() { return "Cluster around " + dominant.impliedFileName(); }
	
	/**
	 * Obtain a clone of the ChainCluster
	 * This method makes a deep copy of the ChainCluster and everything inside it!
	 */
	public ChainCluster clone() {
		ChainCluster myClone = new ChainCluster(dominant.clone());
		
		for(ProteinChain pc: chainSet) {
			myClone.chainSet.add(pc.clone());
		}
		
		return myClone;
	}
	
	/**
	 * Delete all non-dominant chains
	 */
	protected void clearChainSet() { chainSet.clear(); }
	
	/**
	 * Determines if two ChainClusters are the same
	 * To be the same, two ChainCluster must have the same reference chain and the same
	 * chains in their chainSets
	 * 
	 * @param other: the ChainCluster to compare this to
	 * @return
	 */
	public boolean equals(ChainCluster other) {
		if(!dominant.equals(other.dominant)) { return false; }
		
		for(ProteinChain pc: chainSet) {
			if(!other.chainSet.contains(pc)) { return false; }
		}
		
		for(ProteinChain pc: other.chainSet) {
			if(!chainSet.contains(pc)) { return false; }
		}
		
		return true;
	}
	
	/**
	 * 
	 */
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Cluster around [" + dominant.getProteinName() + dominant.getID() + "]\n");
		builder.append(dominant + "\n");
		for(ProteinChain prCh: chainSet) {
			builder.append(prCh + "\n");
		}
		
		return builder.toString();
	}
}
