package bio;

import java.util.ArrayList;

import bio.tools.BioLookup;
import util.BaseTools;

/**
 * A StringBuilder for ProteinChains
 * @author Benjy Strauss
 *
 */

public class ChainBuilder extends BaseTools {
	private ArrayList<AminoAcid> buildList;
	private int startIndex;
	
	/**
	 * Creates a new ChainBuilder
	 */
	public ChainBuilder() {
		this(0);
	}
	
	/**
	 * Creates a new ChainBuilder starting at the index given
	 * @param startIndex: the chain index to start at if the chain does not start at index 0
	 */
	public ChainBuilder(int startIndex) {
		this.startIndex = startIndex;
		buildList = new ArrayList<AminoAcid>();
	}
	
	/**
	 * Appends an AminoAcid(s) to the ProteinChain
	 * @param aa: The AminoAcid(s) to append
	 */
	public void append(AminoAcid... seq) {
		for(AminoAcid aa: seq) {
			buildList.add(aa);
		}
	}
	
	/**
	 * Appends an AminoAcid(s) to the ProteinChain
	 * @param seq: a String containing the single-letter code of the AminoAcids to append
	 */
	public void append(String seq) {
		for(int index = 0; index < seq.length(); ++index) {
			if(seq.charAt(index) != '_') {
				buildList.add(new AminoAcid(seq.charAt(index)));
			}
		}
	}
	
	/**
	 * Pads the internal to make sure residues get put in the right place
	 * @param index
	 */
	public void fillTo(int index) {
		while(index > (buildList.size() + startIndex)) {
			AminoAcid nullAmino = null;
			append(nullAmino);
		}
	}
	
	/**
	 * Create a new ProteinChain with the sequence in the ChainBuilder
	 * @param name: The protein's name (the RCSB-PDB ID)
	 * @param ch: The chain ID
	 * @return ProteinChain object with sequence in the ChainBuilder
	 */
	public ProteinChain toChain(String name, char ch, DataSource source) {
		AminoAcid[] list;
		list = new AminoAcid[buildList.size()];
		buildList.toArray(list);
		return new ProteinChain(name, ch, list, startIndex, source);
	}
	
	/**
	 * Create a new ProteinChain with the sequence in the ChainBuilder
	 * @param name: The protein's name (the RCSB-PDB ID)
	 * @param ch: The chain ID
	 * @return ProteinChain object with sequence in the ChainBuilder
	 */
	public ProteinChain toChain(String name, char ch) {
		return toChain(name, ch, BioLookup.fastaType());
	}
	
	/**
	 * Empty out the ChainBuilder
	 */
	public void reset() {
		buildList.clear();
	}
	
	/**
	 * returns the length of the ChainBuilder
	 * @return
	 */
	public int length() {
		return buildList.size();
	}
}
