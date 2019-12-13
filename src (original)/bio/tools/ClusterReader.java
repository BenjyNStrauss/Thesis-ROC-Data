package bio.tools;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import util.BaseTools;

/**
 * 
 * @author Benjy Strauss
 *
 */

public final class ClusterReader extends BaseTools {
	public static final String IGNORE = "*";
	
	/**
	 * Reads multiple clusters from a file.
	 * Each cluster is a line, protein names are separated by spaces
	 * 
	 * @param fileName: the name of the file to read
	 * @return: An array containing arrays of strings, with each array of strings
	 * representing the proteins in the cluster.
	 */
	public static String[][] readClustersPDF(String fileName) {
		String[][] retVal = null;
		ArrayList<String[]> proteinNames = new ArrayList<String[]>();
		
		try {
			BufferedReader reader = new BufferedReader(new FileReader(fileName));
			
			String line;
			for(line = reader.readLine(); line != null; line = reader.readLine()) {
				ArrayList<String> clusterNames = new ArrayList<String>();
				
				line = line.trim();
				String[] temp = line.split(" ");
				
				//this should remove the duplicates
				temp = removeDuplicates(temp);
				
				//this is just a check to make sure we're only reading proteins from the file
				for(String s: temp) {
					if(s.length() >= 4 && s.length() <= 5) { clusterNames.add(s); }
				}
				
				String[] meta = new String[clusterNames.size()];
				
				clusterNames.toArray(meta);
				proteinNames.add(meta);
			}
			
			reader.close();
			
		} catch (FileNotFoundException e) {
			qpl("Could not find file \"" + fileName + "\" to read clusters from.");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		retVal = new String[proteinNames.size()][];
		proteinNames.toArray(retVal);
		return retVal;
	}
	
	/**
	 * Reads multiple clusters from a file.
	 * Each chain is a line, protein names are separated by extra return
	 * 
	 * @param fileName: the name of the file to read
	 * @return: An array containing arrays of strings, with each array of strings
	 * representing the proteins in the cluster.
	 */
	public static String[][] readClusters(String fileName) {
		String[][] retVal = null;
		ArrayList<String[]> proteinNames = new ArrayList<String[]>();
		
		try {
			BufferedReader reader = new BufferedReader(new FileReader(fileName));
			ArrayList<String> clusterNames = new ArrayList<String>();
			
			String line;
			for(line = reader.readLine(); line != null; line = reader.readLine()) {
				
				line = line.trim();
				
				if(!line.equals("")) {
					if(!line.startsWith(IGNORE)) {
						clusterNames.add(line);
					}
				} else {
					if(clusterNames.size() != 0) {
						String[] temp = new String[clusterNames.size()];
						clusterNames.toArray(temp);
						//this should remove the duplicates
						String temp2[] = removeDuplicates(temp);
						
						proteinNames.add(temp2);
						clusterNames.clear();
					}
				}
			}
			
			if(clusterNames.size() != 0) {
				String[] temp = new String[clusterNames.size()];
				clusterNames.toArray(temp);
			
				String[] temp2 = removeDuplicates(temp);
				proteinNames.add(temp2);
			}
			
			reader.close();
			
		} catch (FileNotFoundException e) {
			qpl("Could not find file: \"" + fileName + "\" to read clusters from.");
			return null;
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		retVal = new String[proteinNames.size()][];
		proteinNames.toArray(retVal);
		
		return retVal;
	}
	
	/**
	 * Takes a line in the form [PDB-ID][PDB-chain][space][uniprot-ID] and organizes it
	 * PDB-ID is length 4, PDB-chain length 1, 
	 * space is a singular space (or tab)
	 * uniprot-ID can be any length
	 * 
	 * @param line
	 * @return An array containing 3 data values, as follows:
	 * [0] = Protein PDB Name
	 * [1] = Protein PDB chainID
	 * [2] = Uniprot ID, or null if no ID was found
	 * 
	 */
	public static String[] parseCompositeLine(String line) {
		String retVal[] = new String[3];
		
		//remove all tabs
		line.replaceAll("\t", " ");
		
		String values[] = line.split(" ");
		
		retVal[0] = values[0].substring(0, 4);
		retVal[1] = "" + values[0].charAt(4);
		retVal[2] = values[1];

		return retVal;
	}
	
	/**
	 * Removes duplicates and preserves order
	 * @param input
	 * @return
	 */
	public static String[] removeDuplicates(String[] input) {
		String[] retVal;
		ArrayList<String> strList = new ArrayList<String>();
		
		for(int i = 0; i < input.length; ++i) {
			if(input[i] != null) {
				strList.add(input[i]);
				
				for(int j = i+1; j < input.length; ++j) {
					if(input[i].equals(input[j])) {
						input[j] = null;
					}
				}
			}
		}
		
		retVal = new String[strList.size()];
		strList.toArray(retVal);
		
		return retVal;
	}
}
