package setup;

import java.io.File;
import java.io.IOException;

import bio.exceptions.ScriptException;
import util.Script;

/**
 * Creates most of the directories necessary for this program to function
 * Can also download the DSSP files
 * @author Benjy Strauss
 *
 */

public final class FileManager extends Script {
	private static final String NESSESARY_FOLDERS[] = { "files", "files/DSSP", "files/genbank", "files/gi_number", "files/PDB", 
			"files/rcsb", "files/saved", "files/uniprot", "output", "patches", "patches/rcsb", "patches/genbank",
			"patches/uniprot", "scripts", "scripts/blast", "scripts/blast/blast_rcsb", "scripts/blast/blast_genbank",
			"scripts/blast/blast_uniprot", "scripts/fasta", "scripts/secstructprediction", "regression", "regression/input",
			"patches/dssp", "patches/other"};
	
	private static final String DATA_FOLDERS[] = { "files/genbank", "files/gi_number", "files/PDB", "files/rcsb", "files/saved", 
			"files/uniprot", "output", "patches", "patches/rcsb", "patches/genbank", "patches/uniprot", "scripts/blast",
			"scripts/blast/blast_rcsb", "scripts/blast/blast_genbank", "scripts/blast/blast_uniprot", "scripts/fasta",
			"scripts/secstructprediction", "regression/input", "patches/dssp", "patches/other"};
	
	private static final String[] DSSP_ARGS = { "rsync", "-avz", "rsync://rsync.cmbi.ru.nl/dssp/", "files/DSSP/" };
	
	protected static String dssp_output;
	protected static String dssp_error;
	
	/**
	 * Sets up the file system to use this program
	 * @param args: not used
	 */
	public static void main(String[] args) {
		makeFolders();
		loadDSSP();
	}
	
	/**
	 * Downloads the DSSP Files into files/DSSP.
	 * If this method fails to download everything, just run it again.
	 */
	public static void loadDSSP() {
		qpl("Downloading DSSP Files...");
		ProcessBuilder builder = new ProcessBuilder(DSSP_ARGS);
		Process proc;
		
		int retval = 0;
		
		try {
			proc = builder.start();
			dssp_output = getInputAsString(proc.getInputStream());
			dssp_error = getInputAsString(proc.getErrorStream());
			
			retval = proc.waitFor();
			qpl(dssp_output);
			
			analyzeScriptReturnValue(retval);
			
		} catch (IOException e) {
			throw new ScriptException("I/O Error for DSSP Download Process");
		} catch (InterruptedException e) {
			throw new ScriptException("DSSP Download Process Interrupted");
		}
		
		if(dssp_error != "") { 
			qerr(dssp_error);
			throw new ScriptException("DSSP Download Error:\n" + dssp_error);
		}
		qpl("Finished downloading DSSP files.");
	}
	
	public String getStdOut() { return output; }
	public String getStdErr() { return error; }
	
	/**
	 * Make all of the folders if they don't already exist
	 */
	public static final void makeFolders() {
		qpl("Verifying file system...");
		for(String folder: NESSESARY_FOLDERS) { verifyFolder(folder); }
		qpl("Finished verifying file system.");
	}
	
	/**
	 * Remove all generated files
	 */
	public static final void cleanUp() {
		qpl("Cleaning Data Files...");
		for(String folder: DATA_FOLDERS) { removeDataFromFolder(folder); }
		qpl("Finished");
	}
	
	/**
	 * Verifies that a single folder exists
	 */
	public static final void verifyFolder(String path) {
		File folder = new File(path);
		if(!folder.exists()) { folder.mkdir(); }
	}
	
	/**
	 * Cleans the Fastas out of scripts/fasta
	 */
	public static final void clearFastas() { removeDataFromFolder("scripts/fasta"); }
	
	/**
	 * Removes all files from a folder
	 * @param path
	 */
	private static final void removeDataFromFolder(String path) {
		File folder = new File(path);
		if(!folder.exists()) { folder.mkdir(); return; }
		
		String[] entries = folder.list();
		for(String str: entries) {
			File entry = new File(path + "/" + str);
			if(!entry.isDirectory()) {
				entry.delete();
			}
		}
		
	}
}
