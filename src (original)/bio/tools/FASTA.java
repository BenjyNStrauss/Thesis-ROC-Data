package bio.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.URL;
import java.util.Scanner;

import bio.BioObject;
import bio.ProteinChain;
import bio.exceptions.DataRetrievalException;
import util.BaseTools;
import util.PythonScript;

/**
 * Contains all methods involving FASTA Retrieval
 * 
 * @author Benjy Strauss
 *
 */

public final class FASTA extends BaseTools {
	public static final String RCSB_FASTA_DIRECTORY = "files/rcsb/";
	public static final String GENBANK_FASTA_DIRECTORY = "files/genbank/";
	public static final String UNIPROT_FASTA_DIRECTORY = "files/uniprot/";
	public static final String PYTHON_FASTA_PATH = "scripts/fasta/";
	
	private static final String OUTPUT = "output/";
	
	private static final String CURL = "curl";
	
	private static final String RCSB_URL = "https://www.rcsb.org/pdb/download/downloadFile.do?fileFormat=fastachain&compression=NO&structureId=";
	private static final String RCSB_URL2 = "&chainId=";
	private static final String UNIPROT_FASTA_URL = "https://www.uniprot.org/uniprot/";
	private static final String UNIPROT_FASTA_EXT = ".fasta";
	private static final String RCSB_PAGE_URL = "https://www.rcsb.org/structure/";
	
	private static final String UNIPROT_ID_LOCATOR = "href=\"http://www.uniprot.org/uniprot/";
	//don't put quotes around URL
	private static final String GENBANK_FASTA_URL_START = "https://eutils.ncbi.nlm.nih.gov/entrez/eutils/efetch.fcgi?db=Protein&id=";
	private static final String GENBANK_FASTA_URL_END = "&rettype=fasta&retmode=text";
	
	public static final String TXT = ".txt";
	
	/**
	 * Downloads a FASTA from GenBank
	 * @param protein: the name of the protein
	 * @param chain: chainID
	 * @throws DataRetrievalException
	 */
	public static void getGenBank(String protein, char chain) throws DataRetrievalException {
		protein = protein.toUpperCase();
		chain = Character.toUpperCase(chain);
		
		PythonScript giRetrieval = new PythonScript("scripts/get-gi.py", protein+chain);
		giRetrieval.run();
		String gi_no = giRetrieval.getStdOut();
		gi_no = gi_no.trim();
		getGenBank(protein, chain, gi_no);
	}
	
	/**
	 * Downloads a FASTA from GenBank
	 * @param protein: the name of the protein
	 * @param chain: chainID
	 * @param giNumber: the protein's GI number
	 * @throws DataRetrievalException
	 */
	public static void getGenBank(String protein, char chain, long giNumber) throws DataRetrievalException {
		getGenBank(protein, chain, ""+giNumber);
	}
	
	/**
	 * Downloads a FASTA from GenBank
	 * @param protein: the name of the protein
	 * @param chain: chainID
	 * @param giNumber: the protein's GI number
	 * @throws DataRetrievalException 
	 */
	public static void getGenBank(String protein, char chain, String giNumber) throws DataRetrievalException {
		protein = protein.toUpperCase();
		chain = Character.toUpperCase(chain);
		String url = GENBANK_FASTA_URL_START + giNumber + GENBANK_FASTA_URL_END;
		
		String success = "Downloaded GenBank Fasta for " + protein + chain;
		String fail = "Failed to download GenBank Fasta for " + protein + chain;
		String filename = GENBANK_FASTA_DIRECTORY + protein + chain + TXT;
		
		curl(url, filename, success, fail);
	}
	
	/**
	 * Downloads a FASTA file into the scripts/fasta directory
	 * @param protein: The name of the protein to download
	 * @param chain: The chain of the protein
	 */
	public static void getRCSB(String protein, char chain) {
		String fileName = "scripts/fasta/" + protein + chain + TXT;
		getRCSB(protein, chain, fileName);
	}
	
	/**
	 * Downloads a FASTA file
	 * @param protein: The name of the protein to download
	 * @param chain: The chain of the protein
	 * @param fileName: what to save the fasta file as
	 */
	private static void getRCSB(String protein, char chain, String fileName) {
		//first have to download the files...
		InputStream url;
		protein = protein.toUpperCase();
		chain = Character.toUpperCase(chain);
				
		String writerTarget = null;
		
		if(!fileName.startsWith(RCSB_FASTA_DIRECTORY)) {
			writerTarget = RCSB_FASTA_DIRECTORY + fileName.toLowerCase();
		} else {
			writerTarget = fileName.toLowerCase();
		}
		
		Scanner fasta = null;
		PrintWriter writer = null;
		
		//loads the fasta
		try{
		    	url = new URL(RCSB_URL+protein+RCSB_URL2+chain).openStream();
		    	
		    	fasta = new Scanner(url);
		    	writer = new PrintWriter(writerTarget);
		    	
		    	//output file is prepared.
		    while(fasta.hasNextLine()){
		    		writer.write(fasta.nextLine()+"\n");
		    }
		    
		} catch (Exception e) {
			System.err.println("File input error on: " + protein+chain);
		} finally {
			if(writer != null) {
				 writer.flush();
				 writer.close();
			}
			if(fasta != null) {
				fasta.close();
			}
		}
	}
	
	/**
	 * 
	 * @param protein
	 * @param chain
	 * @throws DataRetrievalException
	 */
	public static void getUniprot(String protein, char chain) throws DataRetrievalException {
		getUniprot(protein, chain, null);
	}
	
	/**
	 * Downloads a FASTA for Uniprot using the Uniprot ID or the RCSB-PDB ID if the Uniprot ID is not known
	 * Note that this method will not always download the right chain with just the RCSB-PDB ID
	 * 
	 * @param protein: RCSB protein ID
	 * @param chain: RCSB protein chain ID
	 * @param uniprotID: if the uniprot ID is known, else this should be null
	 * @throws DataRetrievalException
	 */
	public static void getUniprot(String protein, char chain, String uniprotID) throws DataRetrievalException {
		protein = protein.toUpperCase();
		chain = Character.toUpperCase(chain);
		
		String url = null;
		
		if(uniprotID == null) {
			url = UNIPROT_FASTA_URL + rcsbToUniprot(protein) + UNIPROT_FASTA_EXT;
		} else {
			url = UNIPROT_FASTA_URL + uniprotID + UNIPROT_FASTA_EXT;
		}
		
		String success = "Downloaded Uniprot Fasta for " + protein + chain;
		String fail = "Failed to download Uniprot Fasta for " + protein + chain;
		String filename = UNIPROT_FASTA_DIRECTORY + protein + chain + TXT;
		
		curl(url, filename, success, fail);
	}
	
	/**
	 * Determines the UniprotKB ID from the RCSB-PDB ID
	 * Does not work if RCSB protein is linked to multiple UniprotKB entries
	 * as this method cannot distinguish between them and will grab the first
	 * 
	 * @param protein: 4-character PDB protein name
	 * @return: UniprotKB protein name
	 * @throws DataRetrievalException: if data could not be retrieved
	 */
	public static String rcsbToUniprot(String protein) throws DataRetrievalException {
		String terminalCode[] = new String[3];
		terminalCode[0] = CURL;
		terminalCode[1] = "-s";
		terminalCode[2] = RCSB_PAGE_URL + protein.toUpperCase();
		
	    ProcessBuilder builder = new ProcessBuilder(terminalCode);
	    	Process proc;
	    		
	    	String stdOut = "", stdErr = "";
	    	int retval = 0;
	    		
	    	try {
	    		proc = builder.start();
	    		stdOut = getInputAsString(proc.getInputStream());
	    		stdErr = getInputAsString(proc.getErrorStream());
	    			
	    		retval = proc.waitFor();
	    		if (retval == 0) {
	    			System.out.println("Downloaded RCSB HTML for " + protein);
	    		} else {
	    			System.out.println("Failed to download RCSB HTML for " + protein);
	    			if(!stdOut.equals("")) { qp(stdOut); }
	    			if(!stdErr.equals("")) { qp(stdErr); }
	    		}
	    	} catch (IOException e) {
	    		throw new DataRetrievalException();
	    	} catch (InterruptedException e) {
	    		throw new DataRetrievalException();
	    	}
	    	
	    	int uniprotIDLocation = stdOut.indexOf(UNIPROT_ID_LOCATOR) + UNIPROT_ID_LOCATOR.length();
	    	
	    	String uniprotIDStart = stdOut.substring(uniprotIDLocation);
	    	String uniprotID = uniprotIDStart.substring(0, uniprotIDStart.indexOf("\""));
	    	
		return uniprotID;
	}
	
	/**
	 * Verifies that the FASTA of a protein exists
	 * @param protein: the RCSB-ID of the protein chain to verify
	 * @param chain: the chain id of the protein chain to verify
	 */
	public static void verify(String protein, char chain) {
		try {
			switch(BioLookup.fastaType()) {
			case GENBANK: FASTA.verifyGenBank(protein, chain); break;
			case RCSB_PDB: FASTA.verifyRCSB(protein, chain); break;
			case UNIPROT:
			default: FASTA.verifyUniprot(protein, chain); break;
			}
		} catch (DataRetrievalException e1) {
			e1.printStackTrace();
		}
	}
	
	/**
	 * Verify that a FASTA file exists in the RCSB FASTA directory
	 * @param protein: the RCSB-ID of the protein chain to verify
	 * @param chain: the chain id of the protein chain to verify
	 */
	public static void verifyRCSB(String protein, char chain) {
		String fixedFastaPath = RCSB_FASTA_DIRECTORY + protein + chain + TXT;
        File fastaFile = new File(fixedFastaPath);
        if(!fastaFile.exists() || fastaFile.isDirectory()) {
        		getRCSB(protein, chain);
        }
	}
	
	/**
	 * Verifies that a UniprotKB fasta exists in the proper directory
	 * Note that the UniprotKB will be generated by searching the HTML of the RCSB-PDB's page for the given protein
	 * If the RCSB-PDB HTML lists multiple UniprotKB IDs, then the first one on the page will be used.
	 * 
	 * @param protein: the RCSB-ID of the protein chain to verify
	 * @param chain: the chain id of the protein chain to verify
	 * @throws DataRetrievalException 
	 */
	public static void verifyUniprot(String protein, char chain) throws DataRetrievalException {
		verifyUniprot(protein, chain, null);
	}
	
	/**
	 * Verifies that a UniprotKB FASTA exists in the proper directory
	 * @param protein: the RCSB-ID of the protein chain to verify
	 * @param chain: the chain id of the protein chain to verify
	 * @param uniprotID: the UniprotKB ID of the  protein chain to verify
	 * @throws DataRetrievalException
	 */
	public static void verifyUniprot(String protein, char chain, String uniprotID) throws DataRetrievalException {
		String fixedUniprotPath = UNIPROT_FASTA_DIRECTORY + protein.toUpperCase() + Character.toUpperCase(chain) + TXT;
        File uniprotFASTA = new File(fixedUniprotPath);
        
        if(!uniprotFASTA.exists() || uniprotFASTA.isDirectory()) {
        		getUniprot(protein, chain, uniprotID);
        }
	}
	
	/**
	 * Verifies that a GenBank FASTA exists in the proper directory
	 * @param protein: the RCSB-ID of the protein chain to verify
	 * @param chain: the chain id of the protein chain to verify
	 * @throws DataRetrievalException 
	 */
	public static void verifyGenBank(String protein, char chain) throws DataRetrievalException {
		String fixedGenBankPath = GENBANK_FASTA_DIRECTORY + protein.toUpperCase() + Character.toUpperCase(chain) + TXT;
        File genBankFASTA = new File(fixedGenBankPath);
        
        if(!genBankFASTA.exists() || genBankFASTA.isDirectory()) {
        		getGenBank(protein, chain);
        }
	}
	
	/**
	 * Writes a ProteinChain object to a FASTA
	 * @param chain: The chain to write
	 * @throws FileNotFoundException: if something goes wrong and the target directory doesn't exist
	 */
	public static void writeFasta(ProteinChain chain) throws FileNotFoundException {
		writeFasta(chain, OUTPUT + chain.getProteinName() + chain.getID() + TXT);
	}
	
	/**
	 * Writes a ProteinChain object to a FASTA
	 * @param chain: The chain to write
	 * @param fileName: the name of the file to write to
	 * @throws FileNotFoundException: if the file path is bad
	 */
	public static void writeFasta(ProteinChain chain, String fileName) throws FileNotFoundException {
		writeFasta(chain.toFasta(), fileName);
	}
	
	/**
	 * 
	 * @param fileName
	 * @param data
	 * @throws FileNotFoundException
	 */
	private static void writeFasta(String data, String fileName) throws FileNotFoundException {
		PrintWriter writer = new PrintWriter(fileName);
		writer.write(data);
		writer.close();
	}
	
	/**
	 * Replaces a RCSB-PDB FASTA with a look-alike generated from Uniprot data
	 * @param protein: Protein's RCSB ID
	 * @param chain: Protein's chain ID
	 */
	static void loadFASTA(String protein, char chain) {
		String filename = protein + chain + TXT;
		StringBuilder fastaBuilder = new StringBuilder();
		
		StringBuilder sequenceBuilder = new StringBuilder();
		fastaBuilder.append(">" + protein.toUpperCase() + ":" + Character.toUpperCase(chain) + BioObject.STATIC_FASTA_HEADER + "\n");
		
		BufferedReader fastaReader;
		boolean success = true;
		
		String path = BioLookup.formFastaPath(protein, chain);
		
		try {
			fastaReader = new BufferedReader(new FileReader(path));
			String line = fastaReader.readLine();
			
			for(line = fastaReader.readLine(); fastaReader.ready(); line = fastaReader.readLine()) {
				sequenceBuilder.append(line);
			}
			sequenceBuilder.append(line);
			fastaReader.close();
		} catch (IOException e) {
			BioLookup.qerr("Reading Error: Could not translate FASTA: " + filename);
			e.printStackTrace();
			success = false;
		}
		
		String sequence = sequenceBuilder.toString();
		
		while(sequence.length() > BioLookup.PDB_FASTA_LINE_LENGTH) {
			fastaBuilder.append(sequence.substring(0, BioLookup.PDB_FASTA_LINE_LENGTH) + "\n");
			sequence = sequence.substring(BioLookup.PDB_FASTA_LINE_LENGTH);
		}
		
		fastaBuilder.append(sequence + "\n");
		
		try {
			writeFasta(fastaBuilder.toString(), PYTHON_FASTA_PATH + filename);
		} catch (FileNotFoundException e) {
			BioLookup.qerr("Writing Error: Could not translate FASTA: " + filename);
			e.printStackTrace();
			success = false;
		}
		
		if(success) {
			qpl("Translated FASTA for " + protein.toUpperCase() + ":" + Character.toUpperCase(chain));
		}
	}
	
	@SuppressWarnings("unused")
	private static void curl(String url, String fileName) throws DataRetrievalException {
		curl(url, fileName, "", "");
	}
	
	/**
	 * Run curl to get a file
	 * @param url
	 * @param fileName
	 * @param successMsg
	 * @param failMsg
	 * @throws DataRetrievalException
	 */
	private static void curl(String url, String fileName, String successMsg, String failMsg) throws DataRetrievalException {
		String terminalCode[] = new String[3];
		terminalCode[0] = CURL;
		terminalCode[1] = "-s";
		terminalCode[2] = url;
		
		ProcessBuilder builder = new ProcessBuilder(terminalCode);
		Process proc;
		
		String stdOut = "", stdErr = "";
		int retval = 0;
		
		try {
			proc = builder.start();
			stdOut = getInputAsString(proc.getInputStream());
			stdErr = getInputAsString(proc.getErrorStream());
			
			retval = proc.waitFor();
			if (retval == 0) {
				if(!successMsg.equals("")) { qp(successMsg); }
			} else {
				if(!failMsg.equals("")) { qp(successMsg); }
				if(!stdOut.equals("")) { qp(stdOut); }
				if(!stdErr.equals("")) { qp(stdErr); }
			}
			
			writeFasta(stdOut, fileName);
			
		} catch (IOException e) {
			throw new DataRetrievalException();
		} catch (InterruptedException e) {
			throw new DataRetrievalException();
		}
	}
	
	/**
	 * Used only to redirect console (command line) output to Java Output 
	 * @param is: The InputStream
	 * @return: the text in the stream
	 */
	private static String getInputAsString(InputStream is) {
		try(java.util.Scanner s = new java.util.Scanner(is)) { 
			return s.useDelimiter("\\A").hasNext() ? s.next() : ""; 
		}
	}
}
