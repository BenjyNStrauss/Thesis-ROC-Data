package bioUI;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Scanner;
import analysis.CSVParser;
import analysis.Prediction;
import analysis.RegressionManager;
import analysis.visualization.OverlayWriter;
import bio.*;
import bio.tools.BioIOStream;
import bio.tools.BioLookup;
import bio.tools.CSVWriter;
import bio.tools.DataWriter;
import bio.tools.VkabatRecoveryModule;
import setup.FileManager;
import util.BaseTools;

/**
 * 
 * Designed as a way to use the JBio package's services without writing Java classes
 * Allows command-line use of some of the package's features.
 * 
 * @author Benjy Strauss
 *
 */

public class JBioMain extends BaseTools {
	private static final String[] LATEX_TERMS = { "-l", "-latex" };
	
	protected static final String DELIMITER = " ";
	
	public static final int SWITCH_RUN_LENGTH = 1;
	static final int SET_SIZE = CSVParser.DESCRIPTOR_LIST.length;
	
	//Default name for the save file
	private static final String DEFAULT_SAVE_FILE_NAME = "mychains";
	static final String DIVIDER = "------------------------------------------------";
	static final String TOTAL = "TOTAL";
	static final String DEFAULT_DATA_CSV_FILE_NAME = "exact_predicted.csv";
	static final String TEST_FILE = "test-uniprot-sb-expred.csv";
	static final String ROC_FILE_NAME = OUTPUT + "roc-log.csv";
	
	//scanner for user input
	static Scanner input;
	//stores protein chains
	static ArrayList<ProteinChain> myChains;
	//stores proteins
	static ArrayList<Protein> myProteins;
	//name for the save file
	static String saveFileName = DEFAULT_SAVE_FILE_NAME;
	
	protected static final String SHIFT = "Shifting to analysis mode.";
	
	protected static AlignedCluster[] clusterList;
	
	private static final String EMPTY[] = { "" }; 
	
	static Prediction[][] data;
	
	public static final void main(String args[]) {
		log("Starting Program:");
		init();
		commandLoop();
		qp(FINISHED);
	}
	
	/**
	 * Allows a user to input instructions continuously
	 */
	public static void commandLoop() {
		if(input == null) { init(); }
		
		String line = "", params[];
		Bio.setSaveFileName(EMPTY);
		
		boolean save = true;
		
		//BioLookup.setDataSourceGenBank();
		BioLookup.setDataSource(DataSource.UNIPROT);
		
		while(true) {
			save = false;
			//prompt for input
			qp("Ready...");
			
			try {
				line = input.nextLine();
			} catch (NoSuchElementException NSEE) { return; }
			
			line = line.replaceAll("\t", DELIMITER);
			params = line.split(DELIMITER);
			params[0] = params[0].toLowerCase();
			
			log("User Entered: \"" + line + "\"\nInterpreted as: \"" + toStringFromArray(params) + "\"");
			
			try {
				myChains = Bio.loadSaved();
			} catch (FileNotFoundException e) { }
			
			switch(params[0]) {
			case "help":					showHelpMenu();												break;
			
			case "analyse":
			case "analyze":
			case "simple":
			case "simple-report":		Stats.generateCSVReport(params);								break;
			case "charge":				Bio.autoCharge();							save = true;		break;
			case "clear":
			case "empty":				myChains.clear();							save = true;		break;
			case "comp-det":
			case "compare-detect":		Bio.compareDetect();											break;
			case "comp-sec":				Bio.makeSecStructCompCSV();					save = true;		break;
			case "data-set":				Bio.makeDataSet(params);						save = true;		break;
			case "debug":				Bio.debug();									save = true;		break;
			case "display":				Bio.display();												break;
			case "exit":
			case "quit":					return;
			case "isu":
			case "isunstruct":
			case "lobanov":				Bio.isUnstruct();							save = true;		break;
			case "re-s":
			case "redo-s":
				for(ProteinChain chain: myChains) { chain.entropy_init = false; }
			case "get-s":
			case "get-entropy":			Bio.assignEntropy();							save = true;		break;
			case "load-c":
			case "load-chain":			Bio.loadProteinChain(params);				save = true;		break;
			case "load-p":
			case "load-protein":			Bio.loadProtein(params);						save = true;		break;
			case "ls":
			case "view":					Bio.checkValues();											break;
			case "patch":
			case "patch-vk":				VkabatRecoveryModule.applyAllPatches();						break;
			case "src":
			case "set-source":
			case "source":				Bio.setSourceUser(params);									break;
			case "switch-list":			Bio.generateSwitchList(params);				save = true;		break;
			case "rename":
			case "save-as":				Bio.setSaveFileName(params);					save = true;		break;
			case "re-vk":
			case "redo-vk":
			case "redo-vkabat":
				for(ProteinChain chain: myChains) { chain.vkabat_init = false; }
			case "get-vk":
			case "get-vkabat":			Bio.assignVkabat();							save = true;		break;
			
			case "summary":
				if(stringArrayContains(params, LATEX_TERMS, true)) {
					DataWriter.writeChainSummary(myChains, saveFileName + "-summary.txt", true);		break;
				} else {
					DataWriter.writeChainSummary(myChains, saveFileName + "-summary.txt", false);		break;
				}
				
			case "write":				CSVWriter.writeData(params, saveFileName, myChains);			break;
			
			case "count":				Stats.countPropensity(params);								break;
			case "fuse":					Stats.fuse(params);											break;
			case "err":
			case "get-err":
			case "get-error":			Stats.getAvgError();											break;
			case "true-err":
			case "true-error":			Stats.getAvgTrueError();										break;
			case "range":
			case "min-max":
			case "max-min":				if(data != null) { Stats.range(); } else { Stats.nullErr(); }	break;
			case "load-pred":			Stats.loadData(params);										break;
			//case "load-i":				Stats.loadData(TEST_FILE);									break;
			case "scan":					if(data != null) { Stats.scan(); } else { Stats.nullErr(); }	break;
			//case "roc":					Stats.rocStats(params, null);							break;
			case "fit-roc":
			case "roc-fit":				Stats.rocFit(params);										break;
			case "load":					disambiguateLoad(params);									break;
			
			case "overlay":	for(ProteinChain chain: myChains) { OverlayWriter.writeOverlay(chain); }	break;
			case "color-overlay":
			case "overlay-color":
			case "overlay-rtf":
			case "overlay+":	for(ProteinChain chain: myChains) { OverlayWriter.writeOverlayRTF(chain); }	break;
			
			case "setup":				FileManager.main(params);									break;
			case "pack":					pack();														break;
			case "regression":
			case "logreg":
			case "log-reg":				RegressionManager.logistic_regression(params, input);			break;
			case "logreg3":	
			case "log-reg3":				RegressionManager.logistic_regression(params, input, 3);		break;
			case "":																					break;
			default:						qp("Instruction not recognized.");
			}
			
			if(save) { BioIOStream.saveObject(myChains, saveFileName); }
		}
	}
	
	/**
	 * 
	 * @return
	 */
	protected static final String getLineUser() {
		String line = null;
		try {
			line = input.nextLine();
		} catch (NoSuchElementException NSEE) { System.exit(0); }
		return line;
	}
	
	/**
	 * 
	 */
	private static void init() {
		if(myChains == null) { myChains = new ArrayList<ProteinChain>(); }
		if(myProteins == null) { myProteins = new ArrayList<Protein>(); }
		if(input == null) { input = new Scanner(System.in); }
	}
	
	/**
	 * 
	 * @param args
	 */
	private static void disambiguateLoad(String params[]) {
		qp("Load what:");
		qp("(c = protein chain, prot = protein, pred = prediction)");
		String line = getLineUser().trim();
		if(line.startsWith("c")) {
			Bio.loadProteinChain(params);
		} else if(line.startsWith("prot")) {
			Bio.loadProtein(params);	
		} else if(line.startsWith("pred")) {
			Stats.loadData(params);
		}
		
	}
	
	/**
	 * Clean up all downloaded and generated data files
	 */
	private static void pack() {
		String line;
		qp("Erase generated data? (y/n)");
		while(input.hasNext()) { input.next(); }
		
		try {
			line = input.nextLine();
		} catch (NoSuchElementException NSEE) { return; }
		
		if(line.length() > 0) {
			if(Character.toLowerCase(line.charAt(0)) == 'y') {
				FileManager.cleanUp();
			}
		}
	}
	
	/**
	 * Gets a double from the user
	 * @param prompt: the prompt to show the user
	 * @return: the double that the user entered
	 */
	protected static double getDoubleFromUser(String prompt) {
		double val = Double.NaN;
		
		while(Double.isNaN(val)) {
			qp(prompt);
			try {
				val = Double.parseDouble(input.nextLine());
				break;
			} catch (NumberFormatException NFE) {
				val = Double.NaN;
			}
		}
		
		return 0;
	}
	
	/**
	 * Shows the help menu
	 * Note that this method is obsolete
	 */
	public static void showHelpMenu() {
		qp("Instructions are listed in the file \"Instructions.txt\" in the main folder");
		
		qp("\"instruction\" \"param_1\" \"param_2\" ...");
		qp("WARNING: data is autosaved after every instruction is executed");
		qp("--------------------------------------------------------------------------------------------------");
		qp("check,view: display all chains in list, as well as whether entropy and vkabat have been calculated");
		qp("data-set:   make a data set");
		qp("comp-sec:   make secondary structure comparison csv(s)");
		qp("--------------------------------------------------------------------------------------------------");
		qp("clear:      clears all protein chains from the list");	
		qp("fill:       compute all data for chains that can be done locally");
		qp("help:       shows help menu");
		qp("get-s:      assigns entropy (both E6 and E20) data to protein chains in the list");
		qp("get-vk:     assigns Vkabat data to protein chains in the list");
		qp("get1-vk:    assigns Vkabat data to a specific protein chain in the list");
		qp("load:       loads the protein chains passed in as parameters afterwards");
		qp("            if no chains are specified, all chains in the protein will be loaded");
		qp("patch:      apply vkabat patches in the \"patches\" folder");
		qp("redo-vk:    redo vkabat computation.  Does not re-download vkabat data");
		qp("rename:     set the fileName to save the data as");
		qp("write:      write the stored chains as a .csv file");
		qp("quit, exit: exit JBioMain program");
	}
}
