package bio.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import bio.ChainBuilder;
import bio.ProteinChain;
import bio.exceptions.PythonException;
import bio.exceptions.ResidueAlignmentException;
import bio.exceptions.UnrecognizedParameterException;
import util.BaseTools;
import util.PythonScript;
import bio.DataSource;

/**
 * Allows for possible recovery from a failed Vkabat run
 * This module is only designed to handle very specific errors-
 * Don't put too much faith in this!
 * 
 * @author Benjy Strauss
 * 
 * "GOR,DPM,GOR3,PHD,PREDATOR,HNN,MLRC,SOPM,JPred,PSIpred,JNET,YASPIN,SSPRO,PROF,DSC"
 */

public class VkabatRecoveryModule extends BaseTools {
	private static final String PATCH_FOLDER_PATH = "patches/";
	//private static final String VKABAT_SOURCES[] = { "GOR","DPM","GOR3","PHD","PREDATOR","HNN","MLRC","SOPM","JPred","PSIpred","JNET","YASPIN","SSPRO","PROF","DSC"};
	
	private static final int SYMPRED_OPTIONS = 8;
	
	private static final int SEQUENCE = 0;
	private static final int PHD = 1;
	private static final int PROF = 2;
	private static final int SSPRO = 3;
	private static final int PREDATOR = 4;
	private static final int YASPIN = 5;
	private static final int JNET = 6;
	private static final int PSIPRED = 7;
	
	private static final String IGNORE = "."; 
	
	private static int MAX_TRIES = 5;
	
	private static String patchPath;
	
	/**
	 * Tries to recover from a Vkabat Failure: main method
	 * @param msg: The error message produced
	 */
	public static void vkabatRecovery(String msg, String proteinChain) { vkabatRecovery(msg, proteinChain, 0); }
	
	/**
	 * Tries to recover from a Vkabat Failure: main method
	 * @param msg: The error message produced
	 * @param tryNo: how many times the algorithm has tried recursively to recover.
	 */
	private static void vkabatRecovery(String msg, String proteinChain, int tryNo) {
		proteinChain = proteinChain.toLowerCase();
		
		if(tryNo > MAX_TRIES) {
			qerr("Try limit exceeded: could not get Vkabat data for: " + proteinChain);
			return;
		}
		
		//determine the cause of the failure
		VkabatFailCause cause = determineFailureCause(msg);
		
		switch(cause) {
		case LIST_INDEX_OUT_OF_RANGE:
			repairFasta(proteinChain);
			break;
		default:
			qerr("Cause of error unknown for: " + proteinChain);
			qerr("Could not generate vkabat.");
		}
	}
	
	/**
	 * Determines why the Vkabat lookup failed by looking at the output
	 * @param msg: output of the fail
	 * @return
	 */
	public static VkabatFailCause determineFailureCause(String msg) {
		String[] lines = msg.split("\n");
		
		if(lines[lines.length-1].equals("IndexError: list index out of range")) {
			return VkabatFailCause.LIST_INDEX_OUT_OF_RANGE;
		} else {
			return VkabatFailCause.UNKNOWN;
		}
	}
	
	/**
	 * Attempts to repair a bad FASTA file
	 * @param proteinName: The name of the protein
	 */
	private static void repairFasta(String proteinChain) {
		qpl("Attempting to fix bad FASTA for: " + proteinChain);
		
		String protein = proteinChain.substring(0, 4);
		char chain = proteinChain.charAt(4);
		
		//verify that the Fasta exists
		FASTA.verifyRCSB(protein, chain);
		
		switch(proteinChain) {
		//for both of these proteins, there is an extra 'x' on the front which should not be there (9/14/2018)
		case "4UA3A":
		case "4UA3B":
			ProteinChain toFix = null;
			qp("Repairing: Verifying Fasta");
			try {
				toFix = BioLookup.readChainFromFasta(protein, chain);
			} catch (IOException e) {
				qerr("Unable to verify Fasta for " + proteinChain);
				qerr("Please check file system for errors.");
				return;
			}
			
			qp("Repairing: Generating new Fasta");
			ChainBuilder fixBuilder = new ChainBuilder();
			for(int index = 1; index < toFix.length(); ++index) {
				fixBuilder.append(toFix.getAmino(index));
			}
			
			ProteinChain fix = fixBuilder.toChain(protein, chain, BioLookup.fastaType());
			
			qp("Repairing: Writing new Fasta");
			try {
				FASTA.writeFasta(fix);
			} catch (FileNotFoundException e) {
				qerr("Unable to overwrite Fasta for " + proteinChain);
				qerr("Please check file system for errors.");
			}
			
			qp("Repairing: Running Vkabat for: " + proteinChain);
			try {
				PythonScript.runPythonScript("scripts/neo-vkabat.py", proteinChain);
			} catch (PythonException pe) {
				qerr("Repair Failed: Python code would not execute properly.");
			}
			
			break;
		default:
			qerr("No known way to fix Fasta for: " + proteinChain);
			qerr("You can try contacting the author at Benjynstrauss@gmail.com for");
			qerr("an updated version of this class's source code, should one exist.");
		}
	}
	
	/**
	 * Patch files
	 * Patch files should be named "XY-Z.txt"
	 * X represents the protein's 4-character ID
	 * Y represents the character describing the chain of the protein
	 * Z represents what type of patch it is
	 */
	public static void applyAllPatches() {
		File patchFolder = new File(patchPath);
		String[] patchList = patchFolder.list();
		
		for(String str: patchList) {
			if(str.startsWith(IGNORE)) { continue; }
			
			String filePath = patchPath + str;
			String chain = str.substring(0,5).toUpperCase();
			String patchType = "";
			
			if(str.indexOf(".") >= 1) {
				patchType = str.substring(str.indexOf("-")+1,str.indexOf(".")).toUpperCase();
			} else {
				patchType = str.substring(str.indexOf("-")+1).toUpperCase();
			}
			
			try {
				applyPatch(patchType, filePath, chain);
			} catch (IOException e) {
				qerr("IO Exception occured with file: " + filePath);
			} catch (ResidueAlignmentException e) {
				qerr("Residue Alignment Exception occured with file: " + filePath);
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 
	 * @param patchType
	 * @param filePath
	 * @param chain
	 * @throws IOException
	 * @throws ResidueAlignmentException
	 */
	private static void applyPatch(String patchType, String filePath, String chain) throws IOException, ResidueAlignmentException {
		FileReader patchReader = null;
		FileReader internalReader = null;
		
		try {
			patchReader = new FileReader(filePath);
		} catch (FileNotFoundException FNFE) {
			qerr(patchType + " patch file " + filePath + " mysteriously disappeared!");
			return;
		}
		
		String secpred_path = VkabatReader.SECPRED_PATH + chain + ".csv";
		String secpred_path_out = VkabatReader.SECPRED_PATH + chain.toUpperCase() + ".csv";
		
		try {
			internalReader = new FileReader(secpred_path);
		} catch (FileNotFoundException FNFE) {
			qerr(patchType + " SecPred file " + secpred_path + " could not be found.");
			patchReader.close();
			return;
		}
		
		BufferedReader reader = new BufferedReader(patchReader);
		BufferedReader secpredReader = new BufferedReader(internalReader);
		
		/* patchData[0] = sequence
		 * patchData[1....n] = secondary structure prediction
		 */
		String patchData[] = null;
		
		ArrayList<String> secpred_lines = new ArrayList<String>();
		ArrayList<String> secpred_newLines = new ArrayList<String>();
		
		while(secpredReader.ready()) {
			secpred_lines.add(secpredReader.readLine());
		}
		
		switch(patchType) {
		case "JPRED":		patchData = readJPredPatch(reader);				break;
		case "YASPIN":		patchData = readYASPINPatch(reader);				break;
		case "PROF":			patchData = readPROFPatch(reader);				break;
		case "PSIPRED":		patchData = readPsiPredPatch(reader);			break;
		case "MLRC":			patchData = readMLRC(reader);					break;
		case "PHD":			patchData = readPHD(reader);						break;
		case "JNET":	
		case "SSPRO":		patchData = readSymPred(reader, patchType);		break;
		case "SYMPRED+":		patchData = readSymPredMulti(reader);			break;
		default:
			patchReader.close();
			internalReader.close();
			throw new UnrecognizedParameterException("Unrecognized Patch Type: " + patchType);
		}
		
		patchReader.close();
		internalReader.close();
		
		//verify length to ensure it's the right fasta
		if(secpred_lines.size() != patchData[0].length()+1) {
			qerr("Incompatible file sizes for patch: " + filePath);
			qerr(secpred_lines.size() + " vs " + (patchData[0].length()+1));
			return;
		}
		
		boolean patch_selector[] = new boolean[patchData.length];
		
		//this means it's a multi-patch
		if(patchType.contains("+")) {
			if(patchType.equals("SYMPRED+")) {
				StringBuilder headerBuilder = new StringBuilder();
				headerBuilder.append(secpred_lines.get(0));
				
				//qp(patchData[PHD]);
				
				if(patchData[PHD] != null && !secpred_lines.get(0).toUpperCase().contains("PHD")) { 
					headerBuilder.append(",PHD");
					patch_selector[PHD] = true;
					System.exit(0);
				}
				
				if(patchData[PROF] != null && !secpred_lines.get(0).toUpperCase().contains("PROF")) {
					headerBuilder.append(",PROF");
					patch_selector[PROF] = true;
				}				
			
				if(patchData[SSPRO] != null && !secpred_lines.get(0).toUpperCase().contains("SSPRO")) {
					headerBuilder.append(",SSPRO");
					patch_selector[SSPRO] = true;
				}
			
				if(patchData[PREDATOR] != null && !secpred_lines.get(0).toUpperCase().contains("PREDATOR")) { 
					headerBuilder.append(",PREDATOR");
					patch_selector[PREDATOR] = true;
				}
			
				if(patchData[YASPIN] != null && !secpred_lines.get(0).toUpperCase().contains("YASPIN")) { 
					headerBuilder.append(",YASPIN");
					patch_selector[YASPIN] = true;
				}
			
				if(patchData[JNET] != null && !secpred_lines.get(0).toUpperCase().contains("JNET")) { 
					headerBuilder.append(",JNET");
					patch_selector[JNET] = true;
				}
			
				if(patchData[PSIPRED] != null && !secpred_lines.get(0).toUpperCase().contains("PSIPRED")) { 
					headerBuilder.append(",PSIPRED");
					patch_selector[PSIPRED] = true;
				}
				
				secpred_newLines.add(headerBuilder.toString());
			}
		} else {
			//single patch
			if(secpred_lines.get(0).toUpperCase().contains(patchType)) {
				qerr("Error: " + chain + " secpred file already has " + patchType + " data.");
				return;
			}
			
			patch_selector[1] = true;
			
			//copy over the header line
			secpred_newLines.add(secpred_lines.get(0) + "," + patchType);
		}
		
		//qp(patch_selector);
		StringBuilder lineAugmenter = new StringBuilder();
		
		for(int index = 0; index < patchData[0].length(); ++index) {
			//verify the residue letter
			char letter = '0';
			if(index < 9) { letter = secpred_lines.get(index+1).charAt(8); }
			else if(index < 99) { letter = secpred_lines.get(index+1).charAt(9); }
			else if(index < 999) { letter = secpred_lines.get(index+1).charAt(10); }
			else if(index < 9999) { letter = secpred_lines.get(index+1).charAt(11); }
			else { letter = secpred_lines.get(index+1).charAt(12); }
			
			if(letter == patchData[0].charAt(index)) {
				for(int i = 1; i < patchData.length; ++i) {
					if(patch_selector[i]) {
						//augment all data lines
						lineAugmenter.append("," + patchData[i].charAt(index));
					}
				}
				
				secpred_newLines.add(secpred_lines.get(index+1) + lineAugmenter.toString());
				lineAugmenter.setLength(0);
				//qp(secpred_newLines.get(index+1));
			} else {
				residueAlignmentExceptionCannon(patchData[0], index, letter);
			}
		}
		
		//
		PrintWriter writer = new PrintWriter(secpred_path_out);
		for(int index = 0; index < patchData[0].length()+1; ++index) {
			writer.write(secpred_newLines.get(index) + "\n");
		}
		writer.close();
		qpl("Patched: " + chain + " secpred file with " + patchType + " data.");
	}
	
	/**
	 * 
	 * @param reader
	 * @return
	 * @throws IOException
	 */
	private static String[] readPHD(BufferedReader reader) throws IOException {
		String[] retVal = new String[2];
		retVal[0] = "";
		retVal[1] = "";
		
		int lineNo = 0;
		
		while(reader.ready()) {
			String line = reader.readLine().trim();
			
			retVal[lineNo] += line;
			lineNo = 1 - lineNo;
		}
		
		retVal[1] = retVal[1].toUpperCase();
		
		return retVal;
	}

	/**
	 * Reads a JPred Patch
	 * @param reader
	 * @return
	 * @throws IOException
	 */
	protected static String[] readJPredPatch(BufferedReader reader) throws IOException {
		String[] retVal = new String[2];
		
		//there are only 2 lines with a JPred patch: read both
		retVal[0] = reader.readLine();
		retVal[1] = reader.readLine();
		return retVal;
	}
	
	/**
	 * 
	 * @param reader
	 * @return
	 * @throws IOException
	 */
	protected static String[] readYASPINPatch(BufferedReader reader) throws IOException {
		String[] retVal = new String[2];
		retVal[0] = "";
		retVal[1] = "";
		
		while(reader.ready()) {
			String line = reader.readLine();
			
			if(line.startsWith("Sequence :")) {
				line = line.substring(11);
				retVal[0] += line;
			} else if(line.startsWith("Prediction:")) {
				line = line.substring(12);
				retVal[1] += line;
			}
		}
		
		return retVal;
	}
	
	/**
	 * TODO unfinished
	 * @param reader
	 * @return
	 * @throws IOException
	 */
	protected static String[] readPROFPatch(BufferedReader reader) throws IOException {
		String[] retVal = new String[2];
		boolean started = false;
		
		StringBuilder seqBuilder = new StringBuilder();
		StringBuilder strBuilder = new StringBuilder();
		
		while(reader.ready()) {
			String line = reader.readLine();
			
			if(line.startsWith("No")) { started = true; continue; }
			if(line.equals("") && started) { break; }
			
			if(started) {
				line = line.substring(line.indexOf("\t")+1);
				seqBuilder.append(line.charAt(0));
				line = line.substring(line.indexOf("\t")+1);
				line = line.substring(line.indexOf("\t")+1);
				strBuilder.append(line.charAt(0));
			}
		}
		
		retVal[0] = seqBuilder.toString();
		retVal[1] = strBuilder.toString();
		
		return retVal;
	}
	
	//@SuppressWarnings("unused")
	private static String[] readPsiPredPatch(BufferedReader reader) throws IOException {
		String[] retVal = new String[2];
		retVal[0] = "";
		retVal[1] = "";
		
		while(reader.ready()) {
			String line = reader.readLine().trim();
			
			if(line.startsWith("AA:")) {
				line = line.substring(line.indexOf(" ")+1);
				retVal[0] += line;
			} else if(line.startsWith("Pred:")) {
				line = line.substring(line.indexOf(" ")+1);
				retVal[1] += line;
			}
		}
		//testPatchOutput(retVal);
		
		return retVal;
	}
	
	private static String[] readSymPred(BufferedReader reader, String queryType) throws IOException {
		String[] retVal = new String[2];
		retVal[0] = "";
		retVal[1] = "";
		
		StringBuilder predBuilder = new StringBuilder();
		
		while(reader.ready()) {
			String line = reader.readLine().trim();
			
			if(line.startsWith("Sequence")) {
				line = line.substring(line.indexOf(" ")+1).trim();
				retVal[0] += line;
			} else if(line.startsWith(queryType.toUpperCase())) {
				predBuilder.append(line.substring(line.indexOf("\t")+1));
			}
		}
		
		for(int pad = predBuilder.length(); pad < retVal[0].length(); ++pad) {
			predBuilder.append("-");
		}
		
		retVal[1] = predBuilder.toString().replaceAll(" ", "-");
		
		return retVal;
	}
	
	/**
	 * 
	 * 
	 * @param reader
	 * @return
	 * @throws IOException 
	 */
	private static String[] readSymPredMulti(BufferedReader reader) throws IOException {
		String[] retVal = new String[SYMPRED_OPTIONS];
		StringBuilder[] retValBuilders = new StringBuilder[SYMPRED_OPTIONS];
		boolean valid[] = new boolean[SYMPRED_OPTIONS];
		
		for(int i = 0; i < SYMPRED_OPTIONS; ++i) { retValBuilders[i] = new StringBuilder(); }
		
		while(reader.ready()) {
			String line = reader.readLine();
			
			if(line.startsWith("Sequence")) {
				line = line.substring(line.indexOf(" ")+1).trim();
				retValBuilders[SEQUENCE].append(line);
			} else if(line.startsWith("PHD")) {
				retValBuilders[PHD].append(line.substring(line.indexOf("\t")+1));
				valid[PHD] = true;
			} else if(line.startsWith("PROF")) {
				retValBuilders[PROF].append(line.substring(line.indexOf("\t")+1));
				valid[PROF] = true;
			} else if(line.startsWith("SSPRO")) {
				retValBuilders[SSPRO].append(line.substring(line.indexOf("\t")+1));
				valid[SSPRO] = true;
			} else if(line.startsWith("PREDATOR")) {
				retValBuilders[PREDATOR].append(line.substring(line.indexOf("\t")+1));
				valid[PREDATOR] = true;
			} else if(line.startsWith("YASPIN")) {
				retValBuilders[YASPIN].append(line.substring(line.indexOf("\t")+1));
				valid[YASPIN] = true;
			} else if(line.startsWith("JNET")) {
				retValBuilders[JNET].append(line.substring(line.indexOf("\t")+1));
				valid[JNET] = true;
			} else if(line.startsWith("PSIPRED")) {
				retValBuilders[PSIPRED].append(line.substring(line.indexOf("\t")+1));
				valid[PSIPRED] = true;
			} 
		}
		
		//qp(retValBuilders[PHD]);
		
		retVal[0] = retValBuilders[0].toString();
		for(int index = 1; index < SYMPRED_OPTIONS; ++index) {
			//With SYMPRED, if a period is in the output, that means the algorithm failed
			if(retValBuilders[index].toString().indexOf(".") != -1) {
				valid[index] = false;
			}
			
			if(valid[index]) {
				for(int pad = retValBuilders[index].length(); pad < retValBuilders[0].length(); ++pad) {
					retValBuilders[index].append("-");
				}
				retVal[index] = retValBuilders[index].toString().replaceAll(" ", "-");
			}
		}
		
		return retVal;
	}
	
	/**
	 * Intended to be package access, nothing outside of BioLookup should ever need to call this
	 * @param src
	 */
	static void setPatchPath(DataSource src) {
		switch(src) {
		case RCSB_PDB: patchPath = PATCH_FOLDER_PATH + "rcsb/";    break;
		case GENBANK:  patchPath = PATCH_FOLDER_PATH + "genbank/"; break;
		case UNIPROT:  patchPath = PATCH_FOLDER_PATH + "uniprot/"; break;
		case DSSP:     patchPath = PATCH_FOLDER_PATH + "dssp/";    break;
		default:
			patchPath = PATCH_FOLDER_PATH + "other/";
		}
	}
	
	/**
	 * 
	 * @param reader
	 * @return
	 * @throws IOException 
	 */
	private static String[] readMLRC(BufferedReader reader) throws IOException {
		String[] retVal = new String[2];
		retVal[0] = "";
		retVal[1] = "";
		
		while(reader.ready()) {
			String line = reader.readLine().trim();
			
			if(Character.isUpperCase(line.charAt(0))) {
				retVal[0] += line;
			} else if(Character.isLowerCase(line.charAt(0))) {
				retVal[1] += line;
			}
		}
		
		retVal[1] = retVal[1].toUpperCase();
		
		return retVal;
	}
	
	/**
	 * 
	 * @param data
	 */
	@SuppressWarnings("unused")
	private static void testPatchOutput(String[] data) {
		qp1(data[0]);
		qp1(data[1]);
		System.exit(0);
	}
	
	/**
	 * Used to avoid a warning
	 * @param seq
	 * @param index
	 * @param letter
	 * @throws ResidueAlignmentException
	 */
	private static void residueAlignmentExceptionCannon(String seq, int index, char letter) throws ResidueAlignmentException {
		throw new ResidueAlignmentException(letter + " vs " + seq.charAt(index));
	}
	
	/**
	 * 'qp' stands for quick-print
	 * mainly for use in debugging
	 * @param arg0: the object to print
	 */
	protected static void qp1(Object arg0) {
		if(arg0 instanceof boolean[]) {
			boolean[] i_arg0 = (boolean[]) arg0;
			for(boolean b: i_arg0) {
				System.out.print(b ? 1 : 0);
			}
			System.out.print("\n");
			return;
		}
		
		if(arg0 != null && arg0.getClass().isArray()) {
			Object[] i_arg0 = (Object[]) arg0;
			for(Object o: i_arg0) {
				System.out.println(o);
			}
		} else if(arg0 instanceof List) {
			List<?> l_arg0 = (List<?>) arg0;
			for(Object o: l_arg0) {
				System.out.println(o);
			}
		} else {
			System.out.println(arg0);
		}
	}
}
