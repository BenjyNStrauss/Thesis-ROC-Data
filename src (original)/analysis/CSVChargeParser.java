package analysis;

import java.io.IOException;
import java.util.ArrayList;

/**
 * 
 * @author Benjy Strauss
 *
 */

public class CSVChargeParser extends CSVParser {
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
			{ Descriptor.E20, Descriptor.RESIDUE_TYPE, Descriptor.ISUNSTRUCT, Descriptor.VKABAT },
			
			{ Descriptor.AMBER95 },
			
			//amber95 versions
			{ Descriptor.AMBER95, Descriptor.RESIDUE_TYPE },
			{ Descriptor.AMBER95, Descriptor.E6 },
			{ Descriptor.AMBER95, Descriptor.E20 },
			{ Descriptor.AMBER95, Descriptor.ISUNSTRUCT },
			{ Descriptor.AMBER95, Descriptor.VKABAT },
			
			//2-level models + amber95
			{ Descriptor.AMBER95, Descriptor.E6, Descriptor.RESIDUE_TYPE },
			{ Descriptor.AMBER95, Descriptor.E6, Descriptor.ISUNSTRUCT },
			{ Descriptor.AMBER95, Descriptor.E6, Descriptor.VKABAT },
			{ Descriptor.AMBER95, Descriptor.E20, Descriptor.RESIDUE_TYPE },
			{ Descriptor.AMBER95, Descriptor.E20, Descriptor.ISUNSTRUCT },
			{ Descriptor.AMBER95, Descriptor.E20, Descriptor.VKABAT },
			{ Descriptor.AMBER95, Descriptor.ISUNSTRUCT, Descriptor.VKABAT },
			{ Descriptor.AMBER95, Descriptor.ISUNSTRUCT, Descriptor.RESIDUE_TYPE },
			{ Descriptor.AMBER95, Descriptor.VKABAT, Descriptor.RESIDUE_TYPE },
			
			//3-level models + amber95
			{ Descriptor.AMBER95, Descriptor.E6, Descriptor.ISUNSTRUCT, Descriptor.VKABAT },
			{ Descriptor.AMBER95, Descriptor.E6, Descriptor.RESIDUE_TYPE, Descriptor.VKABAT },
			{ Descriptor.AMBER95, Descriptor.E6, Descriptor.RESIDUE_TYPE, Descriptor.ISUNSTRUCT },
			{ Descriptor.AMBER95, Descriptor.E20, Descriptor.ISUNSTRUCT, Descriptor.VKABAT },
			{ Descriptor.AMBER95, Descriptor.E20, Descriptor.RESIDUE_TYPE, Descriptor.VKABAT },
			{ Descriptor.AMBER95, Descriptor.E20, Descriptor.RESIDUE_TYPE, Descriptor.ISUNSTRUCT },
			{ Descriptor.AMBER95, Descriptor.RESIDUE_TYPE, Descriptor.ISUNSTRUCT, Descriptor.VKABAT },

			//4-level models + amber95
			{ Descriptor.AMBER95, Descriptor.E6, Descriptor.RESIDUE_TYPE, Descriptor.ISUNSTRUCT, Descriptor.VKABAT },
			{ Descriptor.AMBER95, Descriptor.E20, Descriptor.RESIDUE_TYPE, Descriptor.ISUNSTRUCT, Descriptor.VKABAT }	
	};
	
	/**
	 * 
	 * @param fileName
	 * @return
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
			double propensity = Double.parseDouble(lineValues[index+3]);
			predictions[index] = new Prediction(protein, chain, resNum, propensity, isSwitch);
		}
		
		return predictions;
	}
	
	public static int descriptors() { return DESCRIPTOR_LIST.length; }
}
