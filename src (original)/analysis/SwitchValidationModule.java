package analysis;

import java.io.FileNotFoundException;
import java.io.PrintWriter;

import bio.AlignedCluster;
import bio.ProteinChain;
import bioUI.JBioMain;
import util.BaseTools;

/**
 * Used to compare the switches predicted this program to switches predicted by a human
 * 		Specifically, this is used to test Benjy's (computer) prediction against Edgardo's (manual) one
 * 		call "setManualPredictor()" to set the name of the human doing the predicting
 * 
 * @author Benjy Strauss
 *	
 *
 */

public class SwitchValidationModule extends BaseTools {
	
	private static String manualPredictor = "Edgardo";
	
	public static void setManualPredictor(String humanPredictor) {
		manualPredictor = humanPredictor;
	}
	
	/**
	 * 
	 * @param cluster
	 * @return
	 */
	public static String[] makeSwitchLinup(AlignedCluster cluster) {
		String retVal[] = null;
		cluster.markDominant(JBioMain.SWITCH_RUN_LENGTH);
		String[] header = makeHeader(cluster);
		
		String domSeq = cluster.getDominant().toSequence();
		ProteinChain chains[] = cluster.getAllChains();
		String s_seqs[] = new String[chains.length];
		
		for(int index = 0; index < chains.length; ++index) {
			s_seqs[index] = chains[index].toSecondarySequence();
		}
		
		int lineSets;
		if(domSeq.length() % 80 == 0) {
			lineSets = domSeq.length() / 80;
		} else {
			lineSets = domSeq.length() / 80 + 1;
		}
		
		int blockSize = 4 + chains.length;
		int totalFileLines = lineSets * blockSize + header.length;
		retVal = new String[totalFileLines];
		
		for(int index = 0; index < header.length; ++index) {
			retVal[index] = header[index];
		}
		
		String switches = cluster.getDominant().toSwitches();
		
		for(int lineNo = 0; lineNo < lineSets; ++lineNo) {
			int firstSectionIndex = (lineNo * blockSize) + header.length;
			retVal[firstSectionIndex] = "";
			
			String blockPrimary;
			if(domSeq.length() >= (lineNo+1)*80) {
				blockPrimary = domSeq.substring(lineNo*80, (lineNo+1)*80);
				for(int index = 0; index < chains.length; ++index) {
					retVal[firstSectionIndex + index + 2] = chains[index].fullID() + ":\t" + simplifySecondary(s_seqs[index].substring(lineNo*80, (lineNo+1)*80));
				}
				retVal[firstSectionIndex + chains.length + 2] = "B-SWI:\t" + switches.substring(lineNo*80, (lineNo+1)*80);
			} else {
				blockPrimary = domSeq.substring(lineNo*80);
				for(int index = 0; index < chains.length; ++index) {
					retVal[firstSectionIndex + index + 2] = chains[index].fullID() + ":\t" + simplifySecondary(s_seqs[index].substring(lineNo*80));
				}
				retVal[firstSectionIndex + chains.length + 2] = "B-SWI:\t" + switches.substring(lineNo*80);
			}
			
			retVal[firstSectionIndex+1] = "SEQ:\t"+blockPrimary;
			retVal[firstSectionIndex + chains.length + 3] = "E-SWI:\t";
		}
		
		return retVal;
	}
	
	/**
	 * 
	 * @param cluster
	 * @throws FileNotFoundException
	 */
	public static void printPreliminarySwitchLinup(AlignedCluster cluster) throws FileNotFoundException {
		String[] lines = makeSwitchLinup(cluster);
		PrintWriter writer = new PrintWriter(OUTPUT + "Validation-" + cluster.getDominant().getProteinName() + ".txt");
		for(String str: lines) {
			writer.write(str + "\n");
		}
		writer.close();
	}
	
	/**
	 * Produces a header for the file so that the reader knows what the symbols mean
	 * @param cluster: the cluster of chains
	 * @return: an array of Strings comprising the file header 
	 */
	private static String[] makeHeader(AlignedCluster cluster) {
		//qp(cluster.clusterSize());
		String retval[] = new String[cluster.clusterSize()+7];
		//qp(retval.length);
		retval[0] = cluster.getDominant().getProteinName() +"-Validation";
		retval[1] = "";
		retval[2] = "KEY:";
		retval[3] = "";
		retval[4] = "SEQ:\tPrimary Structure";
		retval[5] = cluster.getDominant().fullID() + ":\t" + cluster.getDominant().fullID() + " Secondary Structure\t(* = disordered, _ = other, H = Helix, E = Sheet)";
		
		ProteinChain chains[] = cluster.getChains();
		for(int index = 0; index < chains.length; ++index) {
			retval[index+6] = chains[index].fullID() + ":\t" + chains[index].fullID() + " Secondary Structure\t(* = disordered, _ = other, H = Helix, E = Sheet)";
		}
		
		char meta = Character.toUpperCase(manualPredictor.charAt(0));
		char bns = 'B';
		
		if(meta == bns) { bns = 'b'; }
		
		retval[chains.length+6] = bns + "-SWI:\tBenjy's Switches\t\t(| = switch, - = no switch)";
		retval[chains.length+7] = meta + "-SWI:\t" + manualPredictor + "'s switches\t\t(| = switch, - = no switch)";
		
		return retval;
	}
	
	/**
	 * Simplifies a sequence of secondary structures into just three: Helix (H), Sheet (E), and Other (_)
	 * @param secondarySequence: the sequence to simplify
	 * 		Note that if the sequence is sufficiently weird, not everything will be simplified
	 * @return: the simplified sequence
	 */
	public static String simplifySecondary(String secondarySequence) {
		secondarySequence = secondarySequence.toUpperCase();
		secondarySequence = secondarySequence.replaceAll(" ", "_");
		secondarySequence = secondarySequence.replaceAll("B", "E");
		secondarySequence = secondarySequence.replaceAll("S", "_");
		secondarySequence = secondarySequence.replaceAll("C", "_");
		secondarySequence = secondarySequence.replaceAll("T", "_");
		secondarySequence = secondarySequence.replaceAll("G", "H");
		secondarySequence = secondarySequence.replaceAll("I", "H");
		return secondarySequence;
	}
}
