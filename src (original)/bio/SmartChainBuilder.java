package bio;

import java.util.ArrayList;

import bio.tools.BioLookup;
import util.Pair;

/**
 * A StringBuilder for ProteinChains
 * @author Benjy Strauss
 *
 */

public class SmartChainBuilder {
	private ArrayList<Pair<AminoAcid, Integer>> buildList;
	
	/**
	 * 
	 */
	public SmartChainBuilder() {
		buildList = new ArrayList<Pair<AminoAcid, Integer>>();
	}
	
	/**
	 * 
	 * @param aa
	 */
	public void append(Pair<AminoAcid, Integer> aa) {
		buildList.add(aa);
	}
	
	/**
	 * 
	 * @param name
	 * @param ch
	 * @return
	 */
	public ProteinChain toChain(String name, char ch) {
		AminoAcid[] list;
		
		int min = 0;
		int max = 0;
		
		for(Pair<AminoAcid, Integer> p: buildList) {
			if(p.y < min) { min = p.y; }
			if(p.y > max) { max = p.y; }
		}
		
		
		//qp("min " + min);
		//qp("max " + max);
		list = new AminoAcid[max-min+1];
		//qp(list.length);
		
		for(Pair<AminoAcid, Integer> p: buildList) {
			list[p.y] = p.x;
		}
		
		return new ProteinChain(name, ch, list, min, BioLookup.fastaType());
	}
	
	/**
	 * Resets the SmartChainBuilder
	 */
	public void reset() {
		buildList.clear();
	}
	
	/**
	 * 
	 * @return
	 */
	public int length() {
		return buildList.size();
	}
	
	/**
	 * qp stands for quick-print
	 * mainly for use in debugging
	 * @param arg0: the object to print
	 */
	@SuppressWarnings("unused")
	private static void qp(Object arg0) {
		if(arg0 != null && arg0.getClass().isArray()) {
			Object[] i_arg0 = (Object[]) arg0;
			for(Object o: i_arg0) {
				System.out.println(o);
			}
		} else {
			System.out.println(arg0);
		}
	}
}
