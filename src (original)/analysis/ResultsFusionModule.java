package analysis;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import util.BaseTools;

/**
 * Used to combine a prediction from the logistic regression with the test set data
 * 		This is done so as to label the residues in the prediction
 * 
 * @author Benjy Strauss
 * 
 */

public class ResultsFusionModule extends BaseTools {
	
	/*public static void main(String[] args) {
		try {
			CSV_Fusion("test-uniprot-so.csv","exact_predicted.csv", "foo.csv");
		} catch (IOException e) {
			qp("Fusion Failed");
		}
	}*/
	
	public static void CSV_Fusion(String testSet, String dataCSV, String fusionFileName) throws IOException {
		//qp(">> " + dataCSV);
		if(!dataCSV.endsWith(CSV)) { dataCSV += CSV; }
		if(!testSet.endsWith(CSV)) { testSet += CSV; }
		
		ArrayList<String> testSetData = new ArrayList<String>();
		ArrayList<String> predictedData = new ArrayList<String>();
		ArrayList<String> fusionData = new ArrayList<String>();
		
		testSet = determineTrueFileName(testSet);
		dataCSV = determineTrueFileName(dataCSV);
		
		BufferedReader testReader = new BufferedReader(new FileReader(testSet));
		BufferedReader predReader = new BufferedReader(new FileReader(dataCSV));
		
		while(testReader.ready()) { testSetData.add(testReader.readLine()); }
		while(predReader.ready()) { predictedData.add(predReader.readLine()); }
		
		testReader.close();
		predReader.close();
		
		for(int index = 0; index < testSetData.size(); ++index) {
			String line = testSetData.get(index);
			
			String num = line.substring(0,line.indexOf(","));
			line = line.substring(line.indexOf(",")+1);
			String chain = line.substring(0,line.indexOf(","));
			line = line.substring(line.indexOf(",")+1);
			line = line.substring(line.indexOf(",")+1);
			String letter = line.substring(0,line.indexOf(","));
			
			String isSwitch = line.substring(line.lastIndexOf(",")+1);
			if(isSwitch.contains("0")) { isSwitch = "false"; }
			else if(isSwitch.contains("1")) { isSwitch = "true"; }
			String line2 = null;
			
			try {
				line2 = predictedData.get(index);
			} catch(java.lang.IndexOutOfBoundsException IOOBE) {
				qp("line: " + line);
				qp("index: " + index);
				qp("size: " + predictedData.size());
				
				System.exit(0);
			}
			
			fusionData.add(num + "," + chain + "," + letter + "," + line2.substring(line2.indexOf(",")+1) + "," + isSwitch);
		}
		//shorten column names
		fusionData.set(0, fusionData.get(0).replaceAll("Predicted Values Model:isSwitch ~ ", ""));
		fusionData.set(0, fusionData.get(0).replaceAll("Residue, levels=SideChainIDs", "SEQ"));
		fusionData.set(0, fusionData.get(0).replaceAll("Vkabat", "VK"));
		fusionData.set(0, fusionData.get(0).replaceAll("isUnstruct", "IsU"));
		fusionData.set(0, fusionData.get(0).replaceAll("amber95", "A95"));
		
		PrintWriter writer = new PrintWriter(fusionFileName);
		for(int index = 0; index < fusionData.size(); ++index) {
			writer.write(fusionData.get(index) + "\n");
		}
		
		writer.close();
	}
}
