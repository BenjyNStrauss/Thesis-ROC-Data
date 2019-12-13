package analysis;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import util.BaseTools;

/**
 * Parses combined test-set/logisitc regression files into 'Prediction' objects
 * 
 * @author Benjy Strauss
 *
 * 
 */

public class CSVParser extends BaseTools {
	public static final Descriptor[][] DESCRIPTOR_LIST = {
			//1-level models
			{ Descriptor.RESIDUE_TYPE },
			{ Descriptor.E6 },
			{ Descriptor.E20 },
			{ Descriptor.ISUNSTRUCT },
			{ Descriptor.VKABAT },
			
			//2-level models
			{ Descriptor.E6, Descriptor.RESIDUE_TYPE },
			{ Descriptor.E6, Descriptor.ISUNSTRUCT },
			{ Descriptor.E6, Descriptor.VKABAT },
			{ Descriptor.E20, Descriptor.RESIDUE_TYPE },
			{ Descriptor.E20, Descriptor.ISUNSTRUCT },
			{ Descriptor.E20, Descriptor.VKABAT },
			{ Descriptor.ISUNSTRUCT, Descriptor.VKABAT },
			{ Descriptor.ISUNSTRUCT, Descriptor.RESIDUE_TYPE },
			{ Descriptor.VKABAT, Descriptor.RESIDUE_TYPE },
			
			//3-level models
			{ Descriptor.E6, Descriptor.ISUNSTRUCT, Descriptor.VKABAT },
			{ Descriptor.E6, Descriptor.RESIDUE_TYPE, Descriptor.VKABAT },
			{ Descriptor.E6, Descriptor.RESIDUE_TYPE, Descriptor.ISUNSTRUCT },
			{ Descriptor.E20, Descriptor.ISUNSTRUCT, Descriptor.VKABAT },
			{ Descriptor.E20, Descriptor.RESIDUE_TYPE, Descriptor.VKABAT },
			{ Descriptor.E20, Descriptor.RESIDUE_TYPE, Descriptor.ISUNSTRUCT },
			{ Descriptor.RESIDUE_TYPE, Descriptor.ISUNSTRUCT, Descriptor.VKABAT },

			//4-level models
			{ Descriptor.E6, Descriptor.RESIDUE_TYPE, Descriptor.ISUNSTRUCT, Descriptor.VKABAT },
			{ Descriptor.E20, Descriptor.RESIDUE_TYPE, Descriptor.ISUNSTRUCT, Descriptor.VKABAT }	
	};
	
	/**
	 * 
	 * @param fileName: the name of the file to parse
	 * @return: An array of predictions representing the file data
	 */
	public static Prediction[][] readFile(String fileName) throws IOException {
		ArrayList<String> fileLines;
		
		fileLines = getData(fileName);
		
		int residues = fileLines.size()-1;
		
		Prediction[][] retVal = new Prediction[residues][];
		
		for(int index = 0; index < residues; ++index) {
			if(!fileLines.get(index+1).equals("")) {
				retVal[index] = parseLine(fileLines.get(index+1));
			}
		}
		
		return retVal;
	}
	
	/**
	 * Reads a text file into an arraylist of Strings
	 * @param fileName: the file to read from
	 * @return: An ArrayList<String> containing the contents of the file
	 * @throws IOException: If the file cannot be found, or an error in the reading occurs
	 */
	protected static final ArrayList<String> getData(String fileName) throws IOException {
		ArrayList<String> fileLines = new ArrayList<String>();
		BufferedReader reader = new BufferedReader(new FileReader(fileName));
		String line = "";
		
		for(line = reader.readLine(); reader.ready(); line = reader.readLine()) {
			//qp(line);
			fileLines.add(line);
		}
		reader.close();
		
		return fileLines;
	}
	
	/**
	 * Turns a line of text from a logistic regression results file into an array of 'Prediction' objects
	 * @param line: the String to parse
	 * @return: array of 'Prediction' objects representing the data in the String
	 */
	private static Prediction[] parseLine(String line) {
		Prediction[] predictions = new Prediction[DESCRIPTOR_LIST.length];
		String[] lineValues = line.split(",");
		
		String protein = lineValues[1].substring(0, 4);
		char chain = lineValues[1].charAt(4);
		
		int resNum = Integer.parseInt(lineValues[0]);
		boolean isSwitch = Boolean.parseBoolean(lineValues[DESCRIPTOR_LIST.length+3]);
		
		for(int index = 0; index < DESCRIPTOR_LIST.length; ++index) {
			double propensity = Double.NaN;
			
			try {
				propensity = Double.parseDouble(lineValues[index+3]);
			} catch (NumberFormatException NFE) {
				qerr("NumberFormatException on line: " + line);
				System.exit(0);
			}
			predictions[index] = new Prediction(protein, chain, resNum, propensity, isSwitch);
			predictions[index].addDescriptor(DESCRIPTOR_LIST[index]);
		}
		
		return predictions;
	}
	
	/**
	 * 
	 * @return length of the list of descriptors (and combinations thereof)
	 */
	public static int descriptors() { return DESCRIPTOR_LIST.length; }
}
