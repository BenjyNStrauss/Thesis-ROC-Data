package bio.tools;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Scanner;

import bioUI.Stats;
import chem.Atom;
import setup.FileManager;
import util.BaseTools;
import util.Coordinate;
import util.Pair;
import util.PythonScript;
import bio.*;
import bio.exceptions.DataSourceNotYetSetException;
import bio.exceptions.InconsistentFileDataException;
import bio.exceptions.PythonException;
import bio.exceptions.ResidueAlignmentException;
import bio.exceptions.UnrecognizedParameterException;
import bio.exceptions.UnrecognizedProteinException;
import bio.tools.isUnstruct.IsUnstruct;

/**
 * Used for looking up values for Proteins and ProteinChains
 * Note that it's important to create a new object when getting descriptors to ensure that
 * Values are assigned to the right residue
 * @author Benjy Strauss
 *
 */

public final class BioLookup extends BaseTools {
	@SuppressWarnings("unused")
	private static final char CHAIN_TRANSITION_CHAR = '*';
	
	public static final int PDB_FASTA_LINE_LENGTH = 80;
	
	private static final int N = 0;
	private static final int HN = 1;
	private static final int CA = 2;
	private static final int CB = 3;
	private static final int CP = 4;
	private static final int O = 5;
	
	private static final int FREE = 4;
	@SuppressWarnings("unused")
	private static final int BURIED = 6;
	private static final int HELIX = 8;
	private static final int SHEET = 10;
	
	private static final int LETTER = 2;
	private static final int E6 = 1;
	private static final int E20 = 0;
	
	private static final int REGION_MATCH_DEFAULT = 10;
	private static final int UNSTARTED = -99999;
	
	private static final String ATOM = "ATOM  ";
	public static final String[] ATOM_CODES = {"N", "HN", "CA", "CB", "CP", "O" };
	
	private static final String myDriver = "org.sqlite.JDBC";
	private static final String jbioDBURL = "jdbc:sqlite:JBioDataBase.db";
	
	private static final String CHARGE_QUERY_START = "SELECT * FROM \"partial-charges-protein-3d-structures\" WHERE ";
	private static final String STATEMENT_END = "\";";
	
	private static final String AMBER95_QUERY_START = "SELECT amber95 FROM \"partial-charges-protein-3d-structures\" WHERE residue like \"";
	
	private static final String DSSP = ".dssp";
	private static final String DSSP_FOLDER = "files/DSSP/";

	private static final Scanner input = new Scanner(System.in);
	
	//private static final String GET_BLAST = "scripts/download_blast.py";
	private static final String E6_PATH = "scripts/calculate_entropy.py";
	private static final String BLAST_PATH = "scripts/blast/";
	private static final String PDB_PATH = "files/PDB/";
	public static final String IS_UNSTRUCT_PATH = "files/isUnstruct/";
	
	private enum ChainReaderStatus { UNSTARTED, SEARCHING, STARTED, FINISHED };
	
	private static DataSource fastaType = null;
	
	private static boolean queryX = false;
	
	/**
	 * 
	 * @return where FASTAs are being downloaded from
	 */
	public static String getDataSource() {
		if(fastaType == null) {
			return "Not Set.";
		} else {
			return fastaType.toString();
		}
	}
	
	/**
	 * Set the FASTA source to the the parameter
	 * those FASTAs will now be used exclusively
	 */
	public static void setDataSource(DataSource src) {
		log("Data Source set to: " + src);
		fastaType = src;
		VkabatRecoveryModule.setPatchPath(fastaType);
		FileManager.clearFastas();
	}
	
	/** @return the type of FASTA */
	public static DataSource fastaType() { return fastaType; }
	
	/**
	 * Assigns the charge values to a single Amino Acid
	 * To do this, the Secondary Structure of the Amino Acid must be known
	 * 
	 * @param residue: the Amino Acid for which charge values are being requested 
	 * @return the Amino Acid with charges in charge fields
	 */
	public static AminoAcid assignChargeValues(AminoAcid residue) {
		if(residue == null) { return null; }
		//prevent out of memory error
		System.gc();
		
		if(residue.secondary() == null) {
			throw new NullPointerException("Secondary Structure cannot be NULL!");
		}
		
		ResultSet[] results = new ResultSet[6];
		
		try {
			Class.forName(myDriver);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		Connection conn = null;
		Statement st = null;
		
		//Where residue = "ALA" and atoms = "N";
		for(int index = 0; index < ATOM_CODES.length; ++index) {
			String query = CHARGE_QUERY_START + "residue like \"";
			query += residue.toCode() + "\" AND ATOMS = \"";
			query += ATOM_CODES[index] + STATEMENT_END;
			
			try {
				conn = DriverManager.getConnection(jbioDBURL);
				st = conn.createStatement();
				results[index] = st.executeQuery(query);
				
			} catch (SQLException e) {
				qpl("Error in obtaining charge values from database for " + residue);
				e.printStackTrace();
			}
		}
		
		try {
			switch(residue.secondary().simpleClassify()) {
			case Helix:
				AssignCharges(residue, results, HELIX);
				break;
			case Sheet:
				AssignCharges(residue, results, SHEET);
				break;
			case Other:
				AssignCharges(residue, results, FREE);
				break;
			default:
				
			}
		} catch (SQLException e) { 
			
		}
		
		try {
			conn.close();
		} catch (SQLException SQLE) {
			SQLE.printStackTrace();
		} catch (NullPointerException NPE) {
			NPE.printStackTrace();
		}
		
		return residue;
	}
	
	/**
	 * Note: untested
	 * @param residue
	 * @param results
	 * @param secondary
	 * @return
	 * @throws SQLException
	 */
	private static AminoAcid AssignCharges(AminoAcid residue, ResultSet[] results, int secondary) throws SQLException {
		Atom atom = residue.getAtom("N");
		//set charge on N
		if(atom != null) {
			atom.setCharge(results[N].getDouble(secondary));
		} else {
			atom = new Atom("N", "N");
			atom.setCharge(results[N].getDouble(secondary));
			residue.addAtom(atom);
		}
		
		//in this case, "atom" represents 2 atoms
		atom = residue.getAtom("HN");
		//set charge on HN
		if(atom != null) {
			atom.setCharge(results[HN].getDouble(secondary));
		} else {
			atom = new Atom("HN", "HN");
			atom.setCharge(results[HN].getDouble(secondary));
			residue.addAtom(atom);
		}
		
		//alpha carbon
		atom = residue.getAtom("CA");
		//set charge on CA
		if(atom != null) {
			atom.setCharge(results[CA].getDouble(secondary));
		} else {
			atom = new Atom("C", "CA");
			atom.setCharge(results[CA].getDouble(secondary));
			residue.addAtom(atom);
		}
		
		//beta carbon
		atom = residue.getAtom("CB");
		//set charge on CB
		if(atom != null) {
			atom.setCharge(results[CB].getDouble(secondary));
		} else {
			atom = new Atom("C", "CB");
			atom.setCharge(results[CB].getDouble(secondary));
			residue.addAtom(atom);
		}
		
		//prime carbon, appears to be "CG" in PDB file
		atom = residue.getAtom("CP");
		if(atom == null) { atom = residue.getAtom("CG"); }
		
		if(atom != null) {
			atom.setName("CP");
			atom.setCharge(results[CP].getDouble(secondary));
		} else {
			atom = new Atom("C", "CP");
			atom.setCharge(results[CP].getDouble(secondary));
			residue.addAtom(atom);
		}
		
		atom = residue.getAtom("O");
		//set charge on O
		if(atom != null) {
			atom.setCharge(results[O].getDouble(secondary));
		} else {
			atom = new Atom("O", "O");
			atom.setCharge(results[O].getDouble(secondary));
			residue.addAtom(atom);
		}
		
		return residue;
	}
	
	/**
	 * Assigns Amber95 charges to an AminoAcid object
	 * @param residue: the object to assign a charge to
	 * @return: AminoAcid passed in with charge assigned
	 */
	public static AminoAcid assignNetAmber95(AminoAcid residue) {
		if(residue == null) { throw new NullPointerException("null residue"); }
		//qp(residue);
		
		if(!residue.residueType().isStandard()) { 
			qpl("Cannot assign charge for non-standard: " + residue);
			return residue;
		}
		
		try {
			Class.forName(myDriver);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		Connection conn = null;
		Statement st = null;
		
		ResultSet results;
		String query = AMBER95_QUERY_START + residue.toCode() + "\";";
		double amber95_sum = 0;
		//qp("f0");
		try {
			conn = DriverManager.getConnection(jbioDBURL);
			st = conn.createStatement();
			results = st.executeQuery(query);
			
			int vals = (residue.residueType() == ResidueType.Proline || residue.residueType() == ResidueType.Glycine) ? 5 : 6;
			
			for(int i = 0; i < vals; ++i) {
				results.next();
				double monitor = results.getDouble(1);
				//qp(".." + monitor);
				amber95_sum += monitor;
			}
			
			residue.setAmber95(amber95_sum);
		} catch (SQLException e) {
			qpl("Error in obtaining queries for: " + residue);
			e.printStackTrace();
		}
		return residue;
	}
	
	/**
	 * Loads a protein Asymmetric region from the DSSP file
	 * @param fileName: name of the file or protein
	 * @return Protein containing DSSP data
	 */
	public static Protein readProteinDSSP(String fileName) {
		ProteinChain[] chainList = null;
		
		if(!fileName.endsWith(DSSP)) { fileName += DSSP; }
		if(!fileName.startsWith(DSSP_FOLDER)) {fileName = DSSP_FOLDER + fileName; }
		
		ArrayList<ProteinChain> chains = new ArrayList<ProteinChain>();
		char lastChainChar = 'A';
		
		ProteinChain loadTest;
		
		for(loadTest = readProteinChainDSSP(fileName, lastChainChar); loadTest != null; lastChainChar++) {
			
			qpl("readProteinDSSP:fileName: " + fileName);
			
			loadTest = readProteinChainDSSP(fileName, lastChainChar);
			
			if(loadTest != null) {
				chains.add(loadTest);
			}
		}
		
		if(chains.size() == 0) { return null; }
		
		chainList = new ProteinChain[chains.size()];
		chains.toArray(chainList);
		return new Protein(fileName.substring(DSSP_FOLDER.length(), fileName.length()-DSSP.length()).toUpperCase(), DataSource.DSSP, chainList);
	}
	
	/**
	 * Reads a Protein Chain from a DSSP file
	 * @param fileName: Protein name or path to DSSP file
	 * @param chainID: which chain to read
	 * @return ProteinChain object containing DSSP data
	 */
	public static ProteinChain readProteinChainDSSP(String fileName, char chainID) {
		BufferedReader dsspReader = null;
		
		String proteinID = fileName.toUpperCase();
		//remove the prefix from the protein name
		if(proteinID.startsWith(DSSP_FOLDER.toUpperCase())) {
			proteinID = proteinID.substring(DSSP_FOLDER.length());
		}
		
		//remove the suffix from the protein name
		if(proteinID.endsWith(DSSP.toUpperCase())) {
			proteinID = proteinID.substring(0,proteinID.indexOf(DSSP.toUpperCase()));
		}
		
		if(!fileName.startsWith(DSSP_FOLDER)) {fileName = DSSP_FOLDER + fileName; }
		if(!fileName.endsWith(DSSP)) { fileName += DSSP; }
		
		ChainReaderStatus status = ChainReaderStatus.UNSTARTED;
		chainID = Character.toUpperCase(chainID);
		String fileLine = "";
		
		StringBuilder userPromptBuilder = new StringBuilder();
		
		boolean firstChainIndexInit = false;
		ChainBuilder builder = null;
		
		try {
			dsspReader = new BufferedReader(new FileReader(fileName));
			
			//read the file in line by line
			for (fileLine = dsspReader.readLine(); fileLine != null; fileLine = dsspReader.readLine()) {
				fileLine = fileLine.trim();
				
				if(status == ChainReaderStatus.STARTED  || status == ChainReaderStatus.SEARCHING) {
					if(fileLine.length() == 0) { break; }
					//qp(fileLine);
					
					fileLine = fileLine.substring(fileLine.indexOf(" "));
					fileLine = fileLine.trim();
					
					String strAsIndex = fileLine.substring(0, fileLine.indexOf(" "));
					int chainIndex = 0;
					
					try {
						chainIndex = Integer.parseInt(strAsIndex);
					//When the chain ends or there is a missing residue
					} catch (NumberFormatException NFE) { 
						if(strAsIndex.charAt(0) == '!') {
							continue;
						}
					}
					
					fileLine = fileLine.substring(fileLine.indexOf(" "));
					fileLine = fileLine.trim();
					char chainChar = fileLine.toUpperCase().charAt(0);
					
					//qp(status);
					
					//if we are searching
					if(status == ChainReaderStatus.SEARCHING) {
						//if the chain characters match
						if(chainID == chainChar) {
							//we've started
							status = ChainReaderStatus.STARTED;
						} else {
							//continue searching
							continue;
						}
					//if we have started
					} else {
						//end of the chain d
						if(chainID != chainChar) {
							//we're done
							status = ChainReaderStatus.FINISHED;
							break;
						}
					}
					
					//once we're sure the chain is right, initialize the chain builder.
					if(!firstChainIndexInit) {
						firstChainIndexInit = true;
						builder = new ChainBuilder(chainIndex);
					}
					
					fileLine = fileLine.substring(fileLine.indexOf(" "));
					fileLine = fileLine.trim();
					char resTypeChar = fileLine.charAt(0);
					//qp(resTypeChar);
					char secStructChar = fileLine.charAt(3);
					
					//DSSP files use 'b' to mean Cysteine with a Sulfur-Sulfur bond
					if(resTypeChar == 'b') { resTypeChar = 'C'; }
					
					AminoAcid aa = null;
					
					//maintain a user prompt of the characters
					userPromptBuilder.append(resTypeChar);
					
					if(userPromptBuilder.length() > 20) {
						userPromptBuilder.deleteCharAt(0);
					}
					
					if(resTypeChar == 'X' && queryX) {
						aa = fixDSSPUser(proteinID, userPromptBuilder.toString(), resTypeChar, chainIndex, chainChar);
					} else {
						try {
							aa = new AminoAcid(ResidueType.letterLookup(resTypeChar));
						} catch (UnrecognizedParameterException UPE) {
							aa = fixDSSPUser(proteinID,userPromptBuilder.toString(), resTypeChar, chainIndex, chainChar);
						}
					}
					
					aa.setSecondaryStructure(SecondaryStructure.parseFromDSSP(secStructChar));
					
					//qp(chainIndex);
					builder.fillTo(chainIndex);
					builder.append(aa);
					
				} else if(fileLine.charAt(0) == '#') {
					status = ChainReaderStatus.SEARCHING;
				}
			}
			dsspReader.close();
			
		} catch (FileNotFoundException e) {
			qpl("No .dssp file found for \"" + fileName + "\"");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//needed to prevent out of memory error
		System.gc();
		if(builder == null) {
			return null;
		} else {
			ProteinChain retVal = builder.toChain(proteinID, chainID, DataSource.DSSP);
			retVal.description = proteinID;
			return retVal;
		}
	}
	
	/**
	 * Reads a FASTA string from a file
	 * @param fileName
	 * @return
	 */
	public static String readFastaString(String fileName) {
		BufferedReader reader = getFastaReader(fileName);
		
		String sequence = "";
		
		try {
			//skip the first line
			reader.readLine();
			
			String fileLine;
			
			for (fileLine = reader.readLine(); fileLine != null; fileLine = reader.readLine()) {
				sequence += fileLine;
			}
			
			reader.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return sequence;
	}
	
	/**
	 * Reads a FASTA file into memory
	 * Determines the protein's name and chain from the FASTA's name
	 * @param fileName
	 * @return a ProteinChain Object with the contents of the FASTA file
	 * @throws FileNotFoundException 
	 */
	public static ProteinChain readChainFromFasta(String fileName) throws FileNotFoundException {
		int dotIndex = fileName.indexOf(".");
		String meta;
		
		if(dotIndex != -1) {
			meta = fileName.substring(fileName.lastIndexOf("/")+1, fileName.indexOf("."));
		} else {
			meta = fileName.substring(fileName.lastIndexOf("/")+1);
		}
		
		if(meta.length() != 5) {
			qpl("Could not 100% determine protein name from file name for " + fileName);
		}
		
		return readChainFromFasta(meta.substring(0, 4), meta.charAt(4), fileName);
	}
	
	/**
	 * Reads a ProteinChain object from a Uniprot FASTA file in the directory "files/uniprot/"
	 * NOTE: This does not and cannot read from a FASTA file with multiple protein chains listed
	 * @param protein: name of the protein
	 * @param chain: protein's chain ID
	 * @return ProteinChain object containing FASTA file data
	 * @throws FileNotFoundException: if the specified FASTA file does not exist in "files/uniprot/"
	 */
	public static ProteinChain readChainFromFasta(String protein, char chain) throws FileNotFoundException {
		return readChainFromFasta(protein, chain, formFastaPath(protein, chain));
	}
	
	/**
	 * Reads a ProteinChain object from a Uniprot FASTA file in the directory "files/uniprot/"
	 * NOTE: This does not and cannot read from a FASTA file with multiple protein chains listed
	 * @param protein: name of the protein
	 * @param chain: protein's chain ID
	 * @param fileName: name of the file to read from
	 * @return ProteinChain object containing FASTA file data
	 * @throws FileNotFoundException
	 */
	public static ProteinChain readChainFromFasta(String protein, char chain, String fileName) throws FileNotFoundException {
		ChainBuilder builder = new ChainBuilder();
		String sequence = readFastaString(fileName);
		
		for(int index = 0; index < sequence.length(); ++index) {
			builder.append(new AminoAcid(ResidueType.letterLookup(sequence.charAt(index))));
		}
		
		return builder.toChain(protein, chain, fastaType);
	}
	
	/**
	 * 
	 * @param fileName
	 * @return
	 */
	private static BufferedReader getFastaReader(String fileName) {
		BufferedReader reader = null;
		
		try {
			reader = new BufferedReader(new FileReader(fileName));
		} catch (FileNotFoundException e) { }
		
		if(reader != null) { return reader; }
			
		try {
			reader = new BufferedReader(new FileReader(fileName+".txt"));
		} catch (FileNotFoundException e) { }
		
		if(reader != null) { return reader; }

		try {
			reader = new BufferedReader(new FileReader(fileName+".fasta"));
		} catch (FileNotFoundException e) { }
		
		if(reader != null) { return reader; }
		
		try {
			reader = new BufferedReader(new FileReader("scripts/fasta/"+fileName+".txt"));
		} catch (FileNotFoundException e) { }
		
		if(reader != null) { return reader; }
		
		return reader;
	}
	
	/**
	 * Reads in a fasta file into an array of ProteinChains
	 * Should work with both PDB and Uniprot fastas
	 * 
	 * @param fileName: the name of the file to read
	 * @return: and array of the Proteins in the fasta file
	 */
	public static Protein[] readFasta(String fileName) {
		Protein[] retVal = null;
		ProteinChain[] currentChains = null;
		
		ArrayList<Protein> list = new ArrayList<Protein>();
		ArrayList<ProteinChain> chainList = new ArrayList<ProteinChain>();
		
		BufferedReader reader;
		String proteinName = "";
		String readProteinName = null;
		
		try {
			reader = new BufferedReader(new FileReader(fileName));
			String fileLine = "";
			String seq = "";
			boolean first = true;
			char chainChar = 'A';
			
			//read the file in line by line
			for (fileLine = reader.readLine(); fileLine != null; fileLine = reader.readLine()) {
				
				//if the line starts a new protein chain
				if(fileLine.charAt(0) == '>') {
					
					if(first) {
						first = false;
					} else {
						chainList.add(new ProteinChain(fileName, chainChar, seq, fastaType));
					}
					
					seq = "";
					readProteinName = fileLine.substring(1, fileLine.indexOf(':'));	
					//qp(readProteinName + " vs " + proteinName);
					
					//if its a new protein
					if(!proteinName.equals(readProteinName)) {
						if(proteinName != "") {
							currentChains = new ProteinChain[chainList.size()];
							chainList.toArray(currentChains);
							chainList.clear();
							list.add(new Protein(proteinName, currentChains));
						}
						proteinName = readProteinName;
					}
					
					chainChar = fileLine.substring(fileLine.indexOf(':')+1, fileLine.indexOf('|')).charAt(0);
					seq = "";
				} else {
					seq += fileLine;
				}
			}
			
			chainList.toArray(currentChains);
			list.add(new Protein(proteinName, currentChains));
			reader.close();
		} catch (IOException e) {
			qpl("Error in reading FASTA file: " + fileName);
		}
		
		retVal = new Protein[list.size()];
		list.toArray(retVal);
		return retVal;
	}
	
	/**
	 * 
	 * @param protein
	 * @return
	 * @throws IOException
	 */
	public static ProteinChain readChainPDB(String protein) throws IOException {
		protein = protein.replaceAll(":", "");
		return readChainPDB(protein.substring(0, 4), protein.charAt(4), PDB_PATH+protein.toUpperCase().substring(0, 4)+".txt");
	}
	
	/**
	 * @param protein
	 * @param chain
	 * @return
	 * @throws IOException 
	 */
	public static ProteinChain readChainPDB(String protein, char chain) throws IOException {
		return readChainPDB(protein, chain, PDB_PATH+protein.toUpperCase()+".txt");
	}
	
	/**
	 * Reads a Protein Chain from a .pdb file (NOT a FASTA)!
	 * @param protein: name of the protein
	 * @param chain: chain of the protein
	 * @param fileName: name of the file to read from
	 * @return ProteinChain object with PDB data (atoms)
	 * @throws IOException 
	 */
	public static ProteinChain readChainPDB(String protein, char chain, String fileName) throws IOException {
		verifyPDB(protein);
		
		SmartChainBuilder smartBuilder = new SmartChainBuilder();
		
		BufferedReader reader = null;
		chain = Character.toUpperCase(chain);
		
		reader = new BufferedReader(new FileReader(fileName));
		String fileLine = "";
			
		//String lastResidueName = BAD_RESIDUE;
		int lastResidueNum = UNSTARTED;
		char chainID;
			
		AminoAcid currentResidue = null;
			
		//read the file in line by line
		for (fileLine = reader.readLine(); fileLine != null; fileLine = reader.readLine()) {
			if(fileLine.startsWith(ATOM)) {
				//qp(fileLine);
				chainID = fileLine.charAt(21);
					
				//if its the wrong chain
				if(chain != chainID) {
					//if we haven't started yet
					if(lastResidueNum == UNSTARTED) {
						//qp("chain " + chain);
						//qp("chainID " + chainID);
						continue;
					} else {
						//finished with the chain
						break;
					}
				}
					
				int resNum = Integer.parseInt(fileLine.substring(23,26).trim());
					
				if(resNum != lastResidueNum) {
					if(lastResidueNum != UNSTARTED) {
						Pair<AminoAcid, Integer> entry = new Pair<AminoAcid, Integer>();
						entry.x = currentResidue;
						entry.y = new Integer(lastResidueNum);
						
						smartBuilder.append(entry);
						//qp(entry.x + ":" + entry.y);
					}
						
					lastResidueNum = resNum;
					String residueName = fileLine.substring(17,20).trim();
					currentResidue = new AminoAcid(ResidueType.parseCode(residueName));
				}
					
				String atomSerial = fileLine.substring(6, 11).trim();
				String atomName = fileLine.substring(12, 16).trim();
				String atomX = fileLine.substring(30, 38).trim();
				String atomY = fileLine.substring(38, 46).trim();
				String atomZ = fileLine.substring(46, 54).trim();
				String atomOccupancy = fileLine.substring(54, 60).trim();
				String tempFactor = fileLine.substring(60, 66).trim();
				String atomSymbol = fileLine.substring(77, 79).trim();
					
				Atom atom = new Atom(atomSymbol, atomName);
				atom.setPos(new Coordinate(Double.parseDouble(atomX),Double.parseDouble(atomY),Double.parseDouble(atomZ)));
				atom.setOccupancy(Double.parseDouble(atomOccupancy));
				atom.setTempFactor(Double.parseDouble(tempFactor));
				atom.setSerialNo(Integer.parseInt(atomSerial));
					
				currentResidue.addAtom(atom);
				//atom.debugPrintAll();
				//break;
			}
		}
		//make sure to handle the last residue
		Pair<AminoAcid, Integer> entry = new Pair<AminoAcid, Integer>();
		entry.x = currentResidue;
		entry.y = new Integer(lastResidueNum);
		smartBuilder.append(entry);
			
		reader.close();
		
		return smartBuilder.toChain(protein, chain);
	}
	
	/**
	 * 
	 * @param chain
	 * @return
	 * @throws ResidueAlignmentException 
	 */
	public static ProteinChain assignDataPDB(ProteinChain chain) throws ResidueAlignmentException {
		ProteinChain newChain = null;
		
		try {
			newChain = readChainPDB(chain.getProteinName(), chain.getID());
		} catch (IOException e) {
			qpl("Could not read PDB file for " + chain.getProteinName() + chain.getID());
			return chain;
		}
		
		//set the first index of the new chain so it lines up...
		SequenceAligner.superAlign(chain, newChain);
		
		if(newChain.lastIndex() > chain.lastIndex()) {
			chain.insertBlanks(chain.length(), newChain.lastIndex() - chain.lastIndex());
		}
		
		for(int index = chain.firstIndex(); index < newChain.lastIndex(); ++index) {
			checkResidueTypeAt(chain, newChain, index);
			
			if(newChain.getAminoAt(index) != null) {
				if(chain.getAminoAt(index) != null) {
					AminoAcid.copyAtoms(chain.getAminoAt(index), newChain.getAminoAt(index));
				} else {
					chain.setAmino(newChain.getAminoAt(index), index);
				}
			}
		}
		
		return chain;
	}
	
	/**
	 * Writes the flexibility data for the given protein chains
	 * @param fileName
	 * @param chains
	 */
	public static void writeFlexData(String fileName, ProteinChain... chains) {
		BufferedWriter writer;
		
		try {
			writer = new BufferedWriter(new FileWriter(fileName));
			
			for(ProteinChain chain: chains) {
				writer.write(chain.getID() + "\n");
				for(int index = 0; index < chain.length(); ++index) {
					writer.write("[" + String.format("%03d", index) + "] " + chain.getResidueInfo(index) + "\n");
				}
				writer.write("\n");
			}
			
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Called when reading a DSSP file if the program encounters a residue type that is unrecognized
	 * @param protein: protein name
	 * @param prompt: last 20 amino acids in the sequence
	 * @param unknown: which unknown character was read
	 * @param chainIndex: the index into the protein chain
	 * @param chainChar: which chain of the protein
	 * @return: AminoAcid object with user input
	 */
	protected static final AminoAcid fixDSSPUser(String protein, String prompt, char unknown, int chainIndex, char chainChar) {
		AminoAcid retVal = null;
		
		while(retVal == null) {
			qp("Unknown/unrecognized Amino acid type (" + unknown + ")");
			qp("Protein (" + protein + ") at chain (" + chainChar + ") index [ " + chainIndex + "]");
			qp("Please enter residue type, or \'X\' for unknown, then press return");
			qp("Preceding 20 values were: " + prompt);
			
			String userInput = input.next();
			
			try {
				retVal = new AminoAcid(ResidueType.letterLookup(Character.toUpperCase(userInput.charAt(0))));
			} catch (UnrecognizedParameterException UPE) {
				qp("User input not recognized, please try again.\n");
			}
		}
		
		return retVal;
	}
	
	/**
	 * 
	 * @param protein
	 * @return
	 * @throws IOException
	 */
	public static Protein assignSecondary(Protein protein) throws IOException {
		if(protein == null) { throw new NullPointerException("Null parameter"); }
		
		String fileName = DSSP_FOLDER + protein.toString() + ".dssp";
		BufferedReader dsspReader = new BufferedReader(new FileReader(fileName));
		String fileLine = "";
		
		boolean started = false;
		
		//read the file in line by line
		for (fileLine = dsspReader.readLine(); fileLine != null; fileLine = dsspReader.readLine()) {
			fileLine = fileLine.trim();
			
			if(started) {
				fileLine = fileLine.substring(fileLine.indexOf(" "));
				fileLine = fileLine.trim();
				String strAsIndex = fileLine.substring(0,fileLine.indexOf(" "));
				int chainIndex = Integer.parseInt(strAsIndex);
				
				fileLine = fileLine.substring(fileLine.indexOf(" "));
				fileLine = fileLine.trim();
				char chainChar = fileLine.charAt(0);
				
				char resTypeChar = fileLine.charAt(2);
				
				char secStructChar = fileLine.charAt(5);
				
				AminoAcid res = protein.getChain(chainChar).getAminoAt(chainIndex);
				//verify the amino acid residue type
				if(res.toChar() == resTypeChar) {
					res.setSecondaryStructure(SecondaryStructure.parseFromDSSP(secStructChar));
				} else {
					dsspReader.close();
					throw new InconsistentFileDataException(protein, chainChar, chainIndex, resTypeChar);
				}
				
			} else if(fileLine.charAt(0) == '#') {
				started = true;
			}
		}
		dsspReader.close();
		
		return protein;
	}
	
	public static ProteinChain getEntropy(String chain) {
		if(chain.length() != 5) {
			throw new UnrecognizedProteinException(chain, "String passed in cannot match any protein chain.");
		}
		
		String protein = chain.substring(0, 4);
		char chainID = chain.charAt(4);
		return getEntropy(protein, chainID);	
	}
	
	/**
	 * 
	 * @param protein
	 */
	public static void verifyPDB(String protein) {
		String fixedPDBPath = PDB_PATH + protein + ".txt";
        File pdbFile = new File(fixedPDBPath);
        if(!pdbFile.exists() || pdbFile.isDirectory()) {
        		getPDB(protein);
        }
	}
	
	/**
	 * Downloads a PDB file
	 * @param protein: The name of the protein to download
	 */
	public static void getPDB(String protein) {
		getPDB(protein, "PDB/"+protein.toUpperCase().substring(0, 4)+".txt");
	}
	
	/**
	 * Downloads a PDB file
	 * @param protein: The name of the protein to download
	 * @param fileName: what to save the fasta file as
	 */
	public static void getPDB(String protein, String fileName) {
		//first have to download the files...
		InputStream url;
		protein = protein.toUpperCase();
		
		Scanner pdb_in = null;
		PrintWriter writer = null;
		
		//loads the fasta
		try{
		    	url = new URL("https://files.rcsb.org/download/"+protein+".pdb1").openStream();
		    	pdb_in = new Scanner(url);
		    	writer = new PrintWriter(fileName);
		    	
		    	//output file is prepared.
		    while(pdb_in.hasNextLine()) {
		    		writer.write(pdb_in.nextLine()+"\n");
		    }
		    
		} catch (Exception e) {
			System.err.println("File input error on: " + protein);
		} finally {
		    writer.flush();
		    writer.close();
		    pdb_in.close();
		}
	}

	
	/**
	 * Obtain a ProteinChain object with E6 and E20 values assigned
	 * 
	 * @param protein: Protein's name
	 * @param chain: chain's char id
	 * @return ProteinChain with entropy values
	 */
	public static ProteinChain getEntropy(String protein, char chain) {
		FASTA.verify(protein, chain);
		FASTA.loadFASTA(protein, chain);
		
        String meta = protein+chain;
        meta = meta.toLowerCase();
        
        String fixedBlastPath = blastPath() + "/" + protein + chain + ".txt";
        
        File blastFile = new File(fixedBlastPath);
        if(!blastFile.exists() || blastFile.isDirectory()) {
        		downloadBlast(meta);
        }
        
        PythonScript.runPythonScript(E6_PATH, meta, directBlast());
		
		//Read the spreadsheet
		String csvName = meta + "_output.csv";
		BufferedReader csvReader = null;
		ChainBuilder chainMaker = new ChainBuilder();
		String[] data;
		
		try {
			csvReader = new BufferedReader(new FileReader(csvName));
			
			csvReader.readLine();
			
			while(csvReader.ready()) {
				data = csvReader.readLine().split(",");
				//qp(data);
				char resType = data[LETTER].charAt(0);
				AminoAcid aa = new AminoAcid(ResidueType.letterLookup(resType));
				aa.setE20(Double.parseDouble(data[E20]));
				aa.setE6(Double.parseDouble(data[E6]));
				
				chainMaker.append(aa);
			}
			
			csvReader.close();
			
		} catch (FileNotFoundException e) {
			qpl("Error! " + csvName + " was not generated properly!");
			return null;
		} catch (IOException e) {
			qpl("Error! " + csvName + " was unreadable!");
			return null;
		}
		
		//delete the now useless .csv file
		try {
			File csv = new File(csvName);
			csv.delete();  
		} catch(Exception e){
			e.printStackTrace();
		}
		
		return chainMaker.toChain(protein, chain, fastaType);
	}
	
	/**
	 * 
	 * @return
	 */
	private static String blastPath() {
		switch(fastaType) {
		case DSSP:
		case UNIPROT:		return BLAST_PATH + "blast_uniprot";
		case GENBANK:		return BLAST_PATH + "blast_genbank";
		case OTHER:
		case RCSB_PDB:		return BLAST_PATH + "blast_rcsb";
		default:
			throw new DataSourceNotYetSetException();
		}
	}

	/**
	 * Given a sequence of characters representing an amino acid sequence, get a ProteinChain object of that sequence with 
	 *  the isUnstruct (aka Lobinov-Galzitskaya) disorder propensities assigned to each residue
	 * @param chain the name of a protein chain in the form of AAAA:B or AAAAB where A is the RCSB-PDB ID and B is the Chain ID
	 * @return ProteinChain object of given sequence with isUnstruct disorder propensities assigned to each residue
	 */
	public static ProteinChain getIsUnstruct(String chain) {
		chain = chain.replaceAll(":", "");
		return getIsUnstruct(chain.substring(0, chain.length()-1), chain.charAt(chain.length()-1));	
	}
	
	/**
	 * Given a sequence of characters representing an amino acid sequence, get a ProteinChain object of that sequence with 
	 *  the isUnstruct (aka Lobinov-Galzitskaya) disorder propensities assigned to each residue
	 * @param protein: Protein's PDB ID
	 * @param chain: Which chain of the protein
	 * @return ProteinChain object of given sequence with isUnstruct disorder propensities assigned to each residue
	 */
	public static ProteinChain getIsUnstruct(String protein, char chain) {
		protein = protein.toUpperCase();
		chain = Character.toUpperCase(chain);
		
		FASTA.verify(protein, chain);
		
		qpl("Running IsUnstruct on " + protein + chain);
		String fastaName = formFastaPath(protein, chain);
		
		IsUnstruct lobGal = new IsUnstruct();
		String lobGalArgs[] = {"", fastaName};
		
		//run the translated IsUnstruct
		lobGal.main(lobGalArgs);
		
		BufferedReader iulReader = null;
		ChainBuilder builder = new ChainBuilder();
		
		String isUnstructPath = formFastaPath(protein, chain);
		isUnstructPath = isUnstructPath.substring(0, isUnstructPath.length()-3);
		
		try {
			iulReader = new BufferedReader(new FileReader(isUnstructPath + "iul"));
			String line;
			
			//Skip header lines
			for(line = iulReader.readLine(); !line.equals(""); line = iulReader.readLine()) { }
			
			for(line = iulReader.readLine(); line != null; line = iulReader.readLine()) {
				//trim off white space
				line = line.trim();
				//trim off residue number
				line = line.substring(line.indexOf(" "));
				line = line.trim();
				
				AminoAcid aa = new AminoAcid(ResidueType.letterLookup(line.charAt(0)));
				aa.setIsUnstruct(Double.parseDouble(line.substring(line.length()-5)));
				builder.append(aa);
			}
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return builder.toChain(protein, chain, fastaType);
	}
	
	/**
	 * Runs a Python script to get a blast file
	 * WARNING: do NOT run this method in parallel
	 * @param proteinChain: the chain to download the blast for
	 */
	public static void downloadBlast(String proteinChain) {
		qpl("Downloading Blast for: " + proteinChain.toUpperCase());
		switch(fastaType) {
		case RCSB_PDB:
		case GENBANK:
			PythonScript.runPythonScript("scripts/getBlast.py", proteinChain);			break;
		case UNIPROT:
		case DSSP:
			PythonScript.runPythonScript("scripts/getBlastUniprot.py", proteinChain);		break;
		default: //assume RCSB
			PythonScript.runPythonScript("scripts/getBlast.py", proteinChain);
		}
	}
	
	private static String directBlast() {
		switch(fastaType) {
		case GENBANK:
		case RCSB_PDB:
		case OTHER:
			return "blast_genbank";
		case UNIPROT:
		case DSSP:
			return "blast_uniprot";
		default:
			throw new DataSourceNotYetSetException();
		}
	}
	
	/**
	 * Given a ProteinChain object, this method assigns E6 and E20 Values
	 * E6 and E20 values are measures of Shannon Entropy
	 * This method will fail if you try to assign entropy data to a ProteinChain without sequence data
	 * To get a ProteinChain object with entropy data, call "getEntropy()" instead.
	 * 
	 * @param chain: the chain to assign entropy values to
	 * @return: The passed in object, now with entropy values
	 * @throws ResidueAlignmentException: If the ProteinChain's name does not match the ProteinChain's sequence or if the ProteinChain doesn't have any sequence data
	 */
	public static ProteinChain assignEntropy(ProteinChain chain) throws ResidueAlignmentException {
		return assignEntropy(chain, REGION_MATCH_DEFAULT);
	}
	
	/**
	 * Given a ProteinChain object, this method assigns E6 and E20 Values
	 * E6 and E20 values are measures of Shannon Entropy
	 * This method will fail if you try to assign entropy data to a ProteinChain without sequence data
	 * To get a ProteinChain object with entropy data, call "getEntropy()" instead.
	 * 
	 * 
	 * @param chain: the chain to assign entropy values to
	 * @param regionMatchLength: The length of the region to match when aligning the generated chain with the chain passed in
	 * @param minResiduesPresent: The minimum residues that must be present in the region to match when aligning
	 * @return: The passed in object, now with entropy values
	 * @throws ResidueAlignmentException: If the ProteinChain's name does not match the ProteinChain's sequence or if the ProteinChain doesn't have any sequence data
	 */
	public static ProteinChain assignEntropy(ProteinChain chain, int regionMatchLength) throws ResidueAlignmentException {
		if(regionMatchLength < 1) {
			regionMatchLength = REGION_MATCH_DEFAULT;
		}
		
		qp("<<Assigning Entropy>>");
		//Align the 2 chains
		ProteinChain newChain = getEntropy(chain.getProteinName(), chain.getID());
		
		if(newChain == null) { return null; }
		
		//set the first index of the new chain so it lines up...
		SequenceAligner.superAlign(chain, newChain);
		
		int chainStart = max(chain.firstIndex(), newChain.firstIndex());
		int chainLimit = min(chain.lastIndex(), newChain.lastIndex());
		
		for(int index = chainStart; index < chainLimit; ++index) {
			AminoAcid aaPointer = chain.getAminoAt(index);
			checkResidueTypeAt(chain, newChain, index);
			
			if(aaPointer == null) {
				chain.setAminoAt(newChain.getAminoAt(index), index);
			} else {
				if(newChain.getAminoAt(index) != null) {
					aaPointer.setE20(newChain.getAminoAt(index).E20());
					aaPointer.setE6(newChain.getAminoAt(index).E6());
					
					if(aaPointer.residueType() == ResidueType.ANY) {
						aaPointer.setResidueType(newChain.getAminoAt(index).residueType());
					}
				}
			}
		}
		chain.entropy_init = true;
		
		return chain;
	}
	
	/**
	 * Assigns isUnstruct data to a ProteinChain object
	 * This method will fail if you try to assign isUnstruct data to a ProteinChain without sequence data
	 * To get a ProteinChain object with isUnstruct data, call "isUnstruct()" instead.
	 * 
	 * @param chain: the chain to assign isUnstruct values to
	 * @return: The passed in object, now with isUnstruct values
	 * @throws ResidueAlignmentException: If the ProteinChain's name does not match the ProteinChain's sequence or if the ProteinChain doesn't have any sequence data
	 */
	public static ProteinChain assignIsUnstruct(ProteinChain chain) throws ResidueAlignmentException {
		return assignIsUnstruct(chain, REGION_MATCH_DEFAULT);
	}
	
	/**
	 * Assigns isUnstruct data to a ProteinChain object
	 * This method will fail if you try to assign isUnstruct data to a ProteinChain without sequence data
	 * To get a ProteinChain object with isUnstruct data, call "isUnstruct()" instead.
	 * 
	 * @param chain: the chain to assign isUnstruct values to
	 * @param regionMatchLength: The length of the region to match when aligning the generated chain with the chain passed in
	 * @param minResiduesPresent: The minimum residues that must be present in the region to match when aligning
	 * @return: The passed in object, now with isUnstruct values
	 * @throws ResidueAlignmentException: If the ProteinChain's name does not match the ProteinChain's sequence or if the ProteinChain doesn't have any sequence data
	 */
	public static ProteinChain assignIsUnstruct(ProteinChain chain, int regionMatchLength) throws ResidueAlignmentException {
		if(regionMatchLength < 1) {
			regionMatchLength = REGION_MATCH_DEFAULT;
		}
		
		//Align the 2 chains
		ProteinChain newChain = getIsUnstruct(chain.getProteinName(), chain.getID());
		
		//set the first index of the new chain so it lines up...
		SequenceAligner.superAlign(chain, newChain);
		
		int chainStart = max(chain.firstIndex(), newChain.firstIndex());
		int chainLimit = min(chain.lastIndex(), newChain.lastIndex());
		
		for(int index = chainStart; index < chainLimit; ++index) {
			AminoAcid aaPointer = chain.getAminoAt(index);
			checkResidueTypeAt(chain, newChain, index);
			
			if(aaPointer == null) {
				chain.setAminoAt(newChain.getAminoAt(index), index);
			} else {
				if(newChain.getAminoAt(index) != null) {
					aaPointer.setIsUnstruct(newChain.getAminoAt(index).isUnstruct());
					
					if(aaPointer.residueType() == ResidueType.ANY) {
						aaPointer.setResidueType(newChain.getAminoAt(index).residueType());
					}
				}
			}
		}
		
		return chain;
	}
	
	/**
	 * Returns a ProteinChain object with Vkabat values
	 * @param protein: the protein's name
	 * @param chain: the chain of the protein
	 * @return: ProteinChain Object with fasta and Vkabat data
	 */
	public static ProteinChain getVkabat(String protein, char chain) {
		protein = protein.toUpperCase();
		chain = Character.toUpperCase(chain);
		
		FASTA.verify(protein, chain);
		FASTA.loadFASTA(protein, chain);
		String outputPath = VkabatReader.generatePath(protein, chain);
		
		File vkFile = new File(outputPath);
		
		//If the VKabat file doesn't exist
		if(!vkFile.exists() || vkFile.isDirectory()) {
			qpl("Downloading Vkabat for: " + protein + chain + ".  Please Be Patient.");
			
			protein = protein.toUpperCase();
			chain = Character.toUpperCase(chain);
			
			try {
				PythonScript.runPythonScript("scripts/neo-vkabat.py", protein + chain);
			} catch (PythonException pe) {
				VkabatRecoveryModule.vkabatRecovery(pe.getMessage(), protein + chain);
			}
		}
		
		ProteinChain retVal = null;
		
		VkabatReader vkReader;
		try {
			vkReader = new VkabatReader(protein, chain);
			retVal = vkReader.getVkabat(protein, chain);
			vkReader.close();
			//vkReader.cleanUp();
		} catch (IOException e) {
			System.err.println("I/O Problem::No Vkabat file generated for: " + protein + chain);
			return null;
		}
		
		//qp(retVal);
		return retVal;
	}
	
	/**
	 * Assigns Vkabat data to a ProteinChain object
	 * This method will fail if you try to assign Vkabat data to a ProteinChain without sequence data
	 * To get a ProteinChain object with Vkabat data, call "getVkabat()" instead.
	 * 
	 * @param chain: the chain to assign Vkabat values to
	 * @return: The passed in object, now with Vkabat values
	 * @throws ResidueAlignmentException: If the ProteinChain's name does not match the ProteinChain's sequence or if the ProteinChain doesn't have any sequence data
	 */
	public static ProteinChain assignVkabat(ProteinChain chain) throws ResidueAlignmentException {
		return assignVkabat(chain, REGION_MATCH_DEFAULT);
	}
	
	/**
	 * Assigns Vkabat data to a ProteinChain object
	 * This method will fail if you try to assign Vkabat data to a ProteinChain without sequence data
	 * To get a ProteinChain object with Vkabat data, call "getVkabat()" instead.
	 * 
	 * @param chain: the chain to assign Vkabat values to
	 * @param regionMatchLength: The length of the region to match when aligning the generated chain with the chain passed in
	 * @param minResiduesPresent: The minimum residues that must be present in the region to match when aligning
	 * @return: The passed in object, now with Vkabat values
	 * @throws ResidueAlignmentException: If the ProteinChain's name does not match the ProteinChain's sequence or if the ProteinChain doesn't have any sequence data
	 */
	public static ProteinChain assignVkabat(ProteinChain chain, int regionMatchLength) throws ResidueAlignmentException {
		if(regionMatchLength < 1) {
			regionMatchLength = REGION_MATCH_DEFAULT;
		}
		
		ProteinChain newChain = getVkabat(chain.getProteinName(), chain.getID());
		if(newChain == null) { return null; }
		
		//set the first index of the new chain so it lines up...
		SequenceAligner.superAlign(chain, newChain);
		
		int chainStart = max(chain.firstIndex(), newChain.firstIndex());
		int chainLimit = min(chain.lastIndex(), newChain.lastIndex());

		for(int index = chainStart; index < chainLimit; ++index) {
			AminoAcid aaPointer = chain.getAminoAt(index);
			checkResidueTypeAt(chain, newChain, index);
			
			if(aaPointer == null) {
				chain.setAminoAt(newChain.getAminoAt(index), index);
			} else {
				if(newChain.getAminoAt(index) != null) {
					aaPointer.setVkabat(newChain.getAminoAt(index).vKabat());
					aaPointer.setVkabatCompletion(newChain.getAminoAt(index).vKabatCompletion());
					
					if(aaPointer.residueType() == ResidueType.ANY) {
						aaPointer.setResidueType(newChain.getAminoAt(index).residueType());
					}
				}
			}
		}
		
		chain.vkabat_init = true;
		
		return chain;
	}
	
	/**
	 * Assigns Secondary Structures to a ProteinChain object
	 * This method will fail if you try to assign secondary structures to a ProteinChain without sequence data
	 * First attempts to use DSSP data
	 * Second attempt is RCSB-PDB secondary structure data
	 * 
	 * @param chain: the chain to assign data Secondary Structures to
	 * @return: same as the parameter
	 * @throws ResidueAlignmentException if the chain's sequence could not be reconciled with the RCSB sequence
	 */
	public static ProteinChain assignSecondary(ProteinChain chain) throws ResidueAlignmentException {
		return assignSecondary(chain, REGION_MATCH_DEFAULT);
	}
	
	/**
	 * Assigns Secondary Structures to a ProteinChain object
	 * This method will fail if you try to assign secondary structures to a ProteinChain without sequence data
	 * First attempts to use DSSP data
	 * Second attempt is RCSB-PDB secondary structure data
	 * 
	 * @param chain: the chain to assign data Secondary Structures to
	 * @param regionMatchLength: the number of residues to match when aligning the chain versus read chain
	 * @return: same as the parameter
	 * @throws ResidueAlignmentException if the chain's sequence could not be reconciled with the RCSB sequence
	 */
	public static ProteinChain assignSecondary(ProteinChain chain, int regionMatchLength) throws ResidueAlignmentException {
		ProteinChain retVal = chain;
		try {
			retVal = assignSecondaryDSSP(chain, regionMatchLength);
			if(!retVal.missingDSSP()) { return retVal; }
		} catch (ResidueAlignmentException RAE) {
			qpl("Protein Chain: " + chain.fullID() + "'s sequence could not be reconciled with DSSP file");
		}
		
		//add newline so qerr doesn't print to same line
		qpl("Attempting RCSB alignment for " + chain.fullID() + "\n");
		retVal = assignSecondaryRCSB(chain, regionMatchLength);
		
		return retVal;
	}
	
	/**
	 * Assigns Secondary Structures to a ProteinChain object from the RCSB-PDB "ss.txt" file
	 * WARNING!  Use only as a backup if DSSP fails, RCSB chains are incomplete so secondary structures may rarely
	 * be mis-assigned for certain residues.
	 * 
	 * @param chain
	 * @param regionMatchLength
	 * @return same as the parameter
	 * @throws ResidueAlignmentException if the chain's sequence could not be reconciled with the RCSB sequence
	 */
	public static ProteinChain assignSecondaryRCSB(ProteinChain chain, int regionMatchLength) throws ResidueAlignmentException {
		BufferedReader rcsbSSReader = null;
		ArrayList<String> relevantLines = new ArrayList<String>();
		boolean relevant = false;
		String target = chain.getProteinName() + ":" + chain.getID();
		
		try {
			rcsbSSReader = new BufferedReader(new FileReader("files/ss.txt"));
			String line;
			
			for(line = rcsbSSReader.readLine(); rcsbSSReader.ready(); line = rcsbSSReader.readLine()) {
				if(line.contains(target)) { relevant = true; }
				if(relevant && line.contains(">") && !line.contains(target)) { break; }
				
				if(relevant) { relevantLines.add(line); }
			}
			
		} catch (FileNotFoundException e) {
			qpl("Internal Error: RCSB Structure file missing for chain: " + chain.fullID());
			return chain;
		} catch (IOException e) {
			qpl("I/O error occured assigning RCSB Secondary Structure for chain: " + chain.fullID());
			e.printStackTrace();
		}
		
		StringBuilder rcsbSequenceBuilder = new StringBuilder();
		StringBuilder rcsbSecStruct = new StringBuilder();
		boolean secondary = false;
		
		for(int index = 1; index < relevantLines.size(); ++index) {
			if(relevantLines.get(index).contains(">")) {
				secondary = true;
			} else {
				if(!secondary) {
					rcsbSequenceBuilder.append(relevantLines.get(index));
				} else {
					rcsbSecStruct.append(relevantLines.get(index));
				}
			}
		}
		
		ProteinChain rcsbGenerated = new ProteinChain(chain.getProteinName(), chain.getID(), rcsbSequenceBuilder.toString(), DataSource.RCSB_PDB);
		for(int index = 0; index < rcsbGenerated.length(); ++index) {
			rcsbGenerated.getAminoAt(index).setSecondaryStructure(SecondaryStructure.parseFromDSSP(rcsbSecStruct.charAt(index)));
		}
		
		SequenceAligner.superAlign(chain, rcsbGenerated);
		
		for(int index = 0; index < chain.length(); ++index) {
			//use getAmino because the chains should be aligned now!
			if(rcsbGenerated.getAmino(index) != null) {
				if(chain.getAmino(index) == null) {
					//set the amino acid if it's null
					chain.setAmino(rcsbGenerated.getAmino(index), index);
				} else {
					SecondaryStructure struct2 = rcsbGenerated.getAmino(index).secondary();
					chain.getAmino(index).setSecondaryStructure(struct2);
				}
			}
		}
		
		//proofread the chain, assign disordered to everything that was not accounted for
		for(int index = 0; index < chain.length(); ++index) {
			if(chain.getAmino(index) != null) {
				if(chain.getAmino(index).secondary() == null) {
					chain.getAmino(index).setSecondaryStructure(SecondaryStructure.UNASSIGNED);
				}
			}
		}
		
		qpl("RCSB alignment succeeded for " + chain.fullID());
		chain.setMissingDSSP(false);
		return chain;
	}

	/**
	 * Assigns secondary structures from a DSSP file to a ProteinChain object
	 * This method will fail if you try to assign secondary structures to a ProteinChain without sequence data
	 * To get a new ProteinChain object with secondary structures, call "readProteinChainDSSP()" instead.
	 * 
	 * @param chain: the chain to assign secondary structures  to
	 * @return: The passed in object, now with secondary structures, if known
	 * @throws ResidueAlignmentException: If the ProteinChain's name does not match the ProteinChain's sequence or if the ProteinChain doesn't have any sequence data
	 */
	public static ProteinChain assignSecondaryDSSP(ProteinChain chain) throws ResidueAlignmentException {
		return assignSecondaryDSSP(chain, REGION_MATCH_DEFAULT);
	}
	
	/**
	 * Assigns secondary structures from a DSSP file to a ProteinChain object
	 * This method will fail if you try to assign secondary structures to a ProteinChain without sequence data
	 * To get a new ProteinChain object with DSSP secondary structures, call "readProteinChainDSSP()" instead.
	 * 
	 * @param chain: the chain to assign secondary structures  to
	 * @param regionMatchLength: The length of the region to match when aligning the generated chain with the chain passed in
	 * @param minResiduesPresent: The minimum residues that must be present in the region to match when aligning
	 * @return: The passed in object, now with secondary structures, if known
	 * @throws ResidueAlignmentException: If the ProteinChain's name does not match the ProteinChain's sequence or if the ProteinChain doesn't have any sequence data
	 */
	public static ProteinChain assignSecondaryDSSP(ProteinChain chain, int regionMatchLength) throws ResidueAlignmentException {
		if(regionMatchLength < 1) {
			regionMatchLength = REGION_MATCH_DEFAULT;
		}
		
		//Align the 2 chains
		ProteinChain newChain = readProteinChainDSSP(chain.getProteinName(), chain.getID());
		
		//Uh-Oh!  No DSSP File!
		if(newChain == null) {
			chain.setMissingDSSP(true);
			qpl("No DSSP file found for " + chain.fullID());
			return chain;
		}
		
		//set the first index of the new chain so it lines up...
		//SequenceAligner.superAlignForwards(chain, newChain);
		SequenceAligner.superAlign(chain, newChain);
		
		int chainStart = max(chain.firstIndex(), newChain.firstIndex());
		int chainLimit = min(chain.lastIndex(), newChain.lastIndex());
		
		for(int index = chainStart; index < chainLimit; ++index) {
			AminoAcid aaPointer = chain.getAminoAt(index);
			
			checkResidueTypeAt(chain, newChain, index);
			
			if(aaPointer == null) {
				try {
					chain.setAminoAt(newChain.getAminoAt(index), index);
				} catch (java.lang.ArrayIndexOutOfBoundsException AIOOBE) {
					qerr("Internal Error!  Contact " + BMAIL);
					qerr("old chain: " + chain);
					qerr("new chain: " + newChain);
					
					AIOOBE.printStackTrace();
					System.exit(0);
				}
			} else {
				if(newChain.getAminoAt(index) == null) {
					aaPointer.setSecondaryStructure(SecondaryStructure.UNASSIGNED);
				} else {
					//copy the secondary structure
					aaPointer.setSecondaryStructure(newChain.getAminoAt(index).secondary());
					//copy the residue type iff it's not specified
					if(aaPointer.residueType() == ResidueType.ANY) {
						aaPointer.setResidueType(newChain.getAminoAt(index).residueType());
					}
				}
			}
		}
		
		//proofread the chain, assign disordered to everything that was not accounted for
		for(int index = 0; index < chain.length(); ++index) {
			if(chain.getAmino(index) != null) {
				if(chain.getAmino(index).secondary() == null) {
					chain.getAmino(index).setSecondaryStructure(SecondaryStructure.UNASSIGNED);
				}
			}
		}
		
		return chain;
	}
	
	/**
	 * Alerts the user if 2 residue types (that should be the same) are different
	 * if the residues are different: print a warning
	 * @param chain: 
	 * @param newChain:
	 * @param index: the index of both chains to check
	 */
	private static void checkResidueTypeAt(ProteinChain chain, ProteinChain newChain, int index) {
		//qp("Checking Residue Type");
		AminoAcid a1 = chain.getAminoAt(index);
		AminoAcid a2 = newChain.getAminoAt(index);
		char c1 = '~';
		char c2 = '~';
		
		if(a1 != null) { c1 = a1.toChar(); } else { return; }
		if(a2 != null) { c2 = a2.toChar(); } else { return; }
		
		if(c1 != c2) {
			qpl("WARNING: Index[" + Stats.formatInt3(index) + "] " + chain.fullID() + "(" + chain.getSource() + ") and " + newChain.fullID() + "(" + newChain.getSource() + ") {" + c1 + " vs " + c2 + "}");
		}
		
	}

	/**
	 * This is a quick method to get a protein chain with secondary structures, entropy values,
	 * isUnstruct propensities, and Vkabat information
	 * @param protein: the RCSB-PDB name of the protein
	 * @param chain: the protein chain's id
	 * @return ProteinChain object with all available descriptors
	 */
	public static ProteinChain getChainAllDescriptors(String protein, char chain) {
		ProteinChain retVal = getVkabat(protein, chain);
		
		try { retVal = assignSecondaryDSSP(retVal); } catch (ResidueAlignmentException e) {
			qpl("Error assigning secondary structures to " + protein + chain);
		}
		
		try { retVal = assignEntropy(retVal); } catch (ResidueAlignmentException e) {
			qpl("Error assigning entropy values to " + protein + chain);
		}
		
		try { retVal = assignIsUnstruct(retVal); } catch (ResidueAlignmentException e) {
			qpl("Error assigning isUnstruct values to " + protein + chain);
		}
		
		return retVal;
	}
	
	/**
	 * Used to get the path to a FASTA file for a given protein and chain id
	 * @param protein
	 * @param chain
	 * @return: path to that protein's FASTA file, if it exists.
	 */
	static String formFastaPath(String protein, char chain) {
		if(fastaType == null) { throw new DataSourceNotYetSetException(); }
		
		switch(fastaType) {
		case GENBANK:  return FASTA.GENBANK_FASTA_DIRECTORY + protein + chain + ".txt";
		case RCSB_PDB: return FASTA.RCSB_FASTA_DIRECTORY    + protein + chain + ".txt";
		case UNIPROT:  return FASTA.UNIPROT_FASTA_DIRECTORY + protein + chain + ".txt";
		default: throw new DataSourceNotYetSetException();
		}
	}
}
