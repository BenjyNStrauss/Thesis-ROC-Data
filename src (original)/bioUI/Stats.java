package bioUI;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.NoSuchElementException;

import analysis.CSVParser;
import analysis.Descriptor;
import analysis.Prediction;
import analysis.PropensityRecord;
import analysis.ResultsFusionModule;
import analysis.stats.ROCRecord;
import analysis.visualization.ROCVisualizer;
import bio.exceptions.InvalidResolutionException;
import bio.exceptions.MissingDataException;

/**
 * Contains the implementation for statistics-related functions.
 * 
 * @author Benjy Strauss
 *
 */

public class Stats extends JBioMain {
	private static final int DEFAULT_PROPENSITY_INTERVALS = 20; 
	
	private static DecimalFormat thresholdFormatter;
	
	//the last file that data was read from
	protected static String lastLoadedFileName;
	
	/**
	 * Load prediction data from the file given
	 * @param args
	 */
	static void loadData(String args[]) {
		String line = "";
		
		if(args.length < 2) {
			qp("Enter: file-to-read");
			
			try {
				line = input.nextLine();
			} catch (NoSuchElementException NSEE) { return; }
		} else {
			line = args[1];
		}
		
		line = verifyCSV(line);
		
		loadData(line);
	}
	
	
	public static void rocFit(double thresholdIncrement) { rocFit(null, thresholdIncrement); }
	public static void rocFit() { rocFit(null); }
	
	/**
	 * Generates a file with the ROC data for the predictions in memory
	 * @param args: 
	 */
	static void rocFit(String[] args) {
		rocFit(args, 0.01);
	}
	
	/**
	 * Generates a file with the ROC data for the predictions in memory
	 * @param args
	 * @param thresholdIncrement
	 */
	static void rocFit(String[] args, double thresholdIncrement) {
		if(thresholdIncrement <= 0 || thresholdIncrement >= 1) {
			throw new InvalidResolutionException("Threshold must be between 0 and 1");
		}
		
		String logName = makeLogFileName();
		ROCVisualizer visualizer = new ROCVisualizer(logName);
		qpl("Computing ROC Stats for: " + logName);
		
		File rocLog = new File(logName);
		if(rocLog.exists()) { rocLog.delete(); }
		
		PrintWriter rocWriter = null;
		try {
			rocWriter = new PrintWriter(new FileOutputStream(new File(logName),true));
			rocWriter.write(ROCRecord.fullROCLogHeader() + "\n");
			rocWriter.close();
		} catch (FileNotFoundException e) {
			qp("Error writing ROC log.");
			e.printStackTrace();
			return;
		}
		
		setThresholdFormatterToProperlyFormat(thresholdIncrement);
		
		for(double threshold = 0.0; threshold < 1.0; threshold += thresholdIncrement) {
			String arguments[] = { "log", ""+threshold, "" };
			if(args != null) {
				if(stringArrayContains(args, "print")) { arguments[2] = "print"; }
			}
			rocStats(arguments, visualizer);
		}
		
		visualizer.makePlot(thresholdIncrement);
	}

	/**
	 * Print out the ROC stats for a given threshold
	 * @param visualizer 
	 * @param args: arguments to modify the function's behavior
	 * 		args[1] is the threshold: if no threshold is specified, the user will be asked for one
	 * 		"log" tells the function to append the printed to a log file
	 * 		no other arguments are valid at this time
	 * 
	 * @return an array containing the ratios
	 */
	private static double[] rocStats(String[] args, ROCVisualizer visualizer) {
		dqp("rocStats(): " + args[1]);
		//do we write the results to a file
		boolean log = stringArrayContains(args, "log");
		//the threshold to evaluate at
		//the default value of -1 triggers the program to prompt the user for a valid threshold
		double threshold = -1;
		
		boolean print = stringArrayContains(args, "print");
		
		if(args != null) {
			if(args.length >= 2) {
				if(args[1] != null) {
					try {
						threshold = Double.parseDouble(args[1].trim());
					} catch (NumberFormatException NFE) { }
				}
			}
		}
		
		//if the threshold is invalid
		while(threshold < 0 || threshold > 1) {
			String line = "";
			
			qp("Enter threshold: between 0 and 1");
			try {
				line = input.nextLine();
			} catch (NoSuchElementException NSEE) { }
			
			try {
				threshold = Double.parseDouble(line.trim());
			} catch (NumberFormatException NFE) {
				qp("Could not parse threshold");
			}
		}
		
		double[] retval = new double[SET_SIZE+1];
		ROCRecord records[] = new ROCRecord[SET_SIZE+1];
		
		//initialize the ROCRecords in the array with the descriptors they are using
		for (int index = 0; index < SET_SIZE; index++) {
			records[index] = new ROCRecord(Descriptor.label(CSVParser.DESCRIPTOR_LIST[index]));
		}
		
		//the last ROCRecord is the total
		records[SET_SIZE] = new ROCRecord(TOTAL);
		
		for(Prediction[] set: data) {
			//this is the sum of all of the propensities, which gets used as a kind of average
			double totalPropensity = 0;
			
			for(int index = 0; index < set.length; ++index) {
				totalPropensity += set[index].propensity();
				if(set[index].isSwitch()) {
					if(set[index].propensity() >= threshold) {
						++records[index].truePos;
					} else {
						++records[index].falseNeg;
					}
				} else {
					if(set[index].propensity() >= threshold) {
						++records[index].falsePos;
					} else {
						++records[index].trueNeg;
					}
				}
			}
			
			if(set[0].isSwitch()) {
				if(totalPropensity >= (threshold*SET_SIZE)) {
					++records[SET_SIZE].truePos;
				} else {
					++records[SET_SIZE].falseNeg;
				}
			} else {
				if(totalPropensity >= (threshold*SET_SIZE)) {
					++records[SET_SIZE].falsePos;
				} else {
					++records[SET_SIZE].trueNeg;
				}
			}	
		}
		
		//start writing the results
		PrintWriter rocWriter = null;
		boolean opened = false;
		if(log) {
			try {
				rocWriter = new PrintWriter(new FileOutputStream(new File(makeLogFileName()),true));
				opened = true;
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
		
		for(int index = 0; index < SET_SIZE+1; ++index) {
			if(print) {
				qp(records[index]);
				qp("True  Positive: " + records[index].truePos);
				qp("False Positive: " + records[index].falsePos);
				qp("True  Negative: " + records[index].trueNeg);
				qp("False Negative: " + records[index].falseNeg);
			}
			
			if(records[index].falsePos == 0) {
				retval[index] = 0;
			} else {
				retval[index] = (double) records[index].truePos / (double) records[index].falsePos;
			}
			
			if(opened) {
				String logRow = thresholdFormatter.format(threshold) + "," + records[index].toCSVRow();
				if(print) { qp(logRow); }
				rocWriter.write(logRow + "\n");
			}
			
			if(print) { qp(">T/F Pos Ratio: " + retval[index] + "\n"); }
		}
		
		if(opened) { rocWriter.close(); }
		
		if(visualizer != null) {
			visualizer.addRecords(records);
		}
		
		return retval;
	}
	
	/**
	 * Loads regression output data
	 * @param fileName: the name of the file to read from
	 */
	static void loadData(String fileName) {
		String trueFileName = determineTrueFileName(fileName);
		
		try {
			data = CSVParser.readFile(trueFileName);
			lastLoadedFileName = trueFileName;
			qpl("Loaded prediction data from: " + trueFileName);
			return;
		} catch (Exception e) {
			qpl("Could not read data from file: " + fileName);
			e.printStackTrace();
		}
	}
	
	/**
	 * Count the number of residues predicted to be switches or not at certain propensity thresholds.
	 */
	static void countPropensity(String[] args) {
		int propensity_intervals = DEFAULT_PROPENSITY_INTERVALS;
		
		if(args.length > 1) {
			try { propensity_intervals = Integer.parseInt(args[1]); } catch (NumberFormatException NFE) { }
		}
		
		PropensityRecord records[] = new PropensityRecord[SET_SIZE+1];
		
		for (int index = 0; index < SET_SIZE; index++) {
			records[index] = new PropensityRecord(Descriptor.label(CSVParser.DESCRIPTOR_LIST[index]), propensity_intervals);
		}
		
		records[SET_SIZE] = new PropensityRecord(TOTAL, propensity_intervals);
		
		for(Prediction[] set: data) {
			for(int index = 0; index < set.length; ++index) {
				records[index].processPrediction(set[index]);
				records[SET_SIZE].processPrediction(set[index]);
			}
		}
		
		qp(DIVIDER);
		for(PropensityRecord r: records) { 
			qp(r);
			qp(DIVIDER);
		}
	}
	
	/**
	 * Adds test set data to a prediction file generated by the logistic regression
	 * @param args
	 * 		args[1] = test set
	 *  		args[2] = data csv from logistic regression
	 *  
	 *  Note that args[0] is not used!
	 */
	static void fuse(String[] args) {
		//prevents a null pointer crash
		if(args == null) { args = new String[1]; }
		
		String testSet = "";
		String dataCSV = "";
		
		//if the test set is not specified, ask the user for the name
		if(args.length < 2) {
			qp("Enter: test set file name");
			try {
				testSet = input.nextLine();
			} catch (NoSuchElementException NSEE) { return; }
		} else {
			testSet = args[1];
		}
		
		//if the data csv is not specified, ask the user for the name
		if(args.length < 3) {
			qp("Enter: data CSV file name");
			try {
				dataCSV = input.nextLine();
			} catch (NoSuchElementException NSEE) { return; }
		} else {
			dataCSV = args[2];
		}
		
		//perform the fusion, overwriting the original data CSV
		try {
			ResultsFusionModule.CSV_Fusion(testSet, dataCSV, dataCSV);
		} catch (IOException e) {
			qp("Could not fuse test set and data CSV");
			e.printStackTrace();
		}
	}
	
	/**
	 * Prints out the minimum, maximum propensities, and propensity range
	 */
	static void range() {
		double minimums[] = new double[SET_SIZE+1];
		double maximums[] = new double[SET_SIZE+1];
		
		for(int index = 0; index < minimums.length; ++index) { minimums[index] = 1; }
		
		for(Prediction[] set: data) {
			for(int index = 0; index < set.length; ++index) {
				if(set[index].propensity() < minimums[index]) {
					minimums[index] = set[index].propensity();
				}
				
				if(set[index].propensity() > maximums[index]) {
					maximums[index] = set[index].propensity();
				}
				
				if(set[index].propensity() < minimums[SET_SIZE]) {
					minimums[SET_SIZE] = set[index].propensity();
				}
				
				if(set[index].propensity() > maximums[SET_SIZE]) {
					maximums[SET_SIZE] = set[index].propensity();
				}
			}
		}
		
		for(int index = 0; index < SET_SIZE; ++index) {
			qp(Descriptor.label(CSVParser.DESCRIPTOR_LIST[index]) + ":");
			qp("min: " + minimums[index]);
			qp("max: " + maximums[index]);
			qp("range: " + (maximums[index]-minimums[index]));
		}
		
		qp("Total:");
		qp("min: " + minimums[SET_SIZE]);
		qp("max: " + maximums[SET_SIZE]);
		qp("range: " + (maximums[SET_SIZE]-minimums[SET_SIZE]));
	}
	
	/**
	 * Called when a data analysis method is called, but no data is loaded
	 */
	static void nullErr() {
		qp("ERROR: please load data!");
	}
	
	/**
	 * 
	 */
	static void getAvgError() {
		double errors[] = new double[CSVParser.descriptors()];
		
		for(Prediction[] set: data) {
			for(int i = 0; i < CSVParser.descriptors(); ++i) {
				errors[i] += set[i].error();
			}
		}
		
		//this is all output formatting
		String errorStr[] = new String[CSVParser.descriptors()];
		int maxStrLen = 0;
		for(int i = 0; i < CSVParser.descriptors(); ++i) {
			errorStr[i] = "Error: " + Descriptor.label(CSVParser.DESCRIPTOR_LIST[i]);
			if(errorStr[i].length() > maxStrLen) { maxStrLen = errorStr[i].length(); }
		}
		
		for(int i = 0; i < CSVParser.descriptors(); ++i) {
			while(errorStr[i].length() < maxStrLen) { errorStr[i] += " "; }
			errors[i] /= data.length;
			qp(errorStr[i] + ": " + errors[i]);
		}
	}
	
	/**
	 * 
	 */
	static void getAvgTrueError() {
		double errors[] = new double[CSVParser.descriptors()];
		int switches = 0;
		
		for(Prediction[] set: data) {
			if(set[0].isSwitch()) {
				for(int i = 0; i < CSVParser.descriptors(); ++i) {
					errors[i] += set[i].error();
				}
				++switches;
			}
		}
		
		//this is all output formatting
		String errorStr[] = new String[CSVParser.descriptors()];
		int maxStrLen = 0;
		for(int i = 0; i < CSVParser.descriptors(); ++i) {
			errorStr[i] = "Error: " + Descriptor.label(CSVParser.DESCRIPTOR_LIST[i]);
			if(errorStr[i].length() > maxStrLen) { maxStrLen = errorStr[i].length(); }
		}
		
		for(int i = 0; i < CSVParser.descriptors(); ++i) {
			while(errorStr[i].length() < maxStrLen) { errorStr[i] += " "; }
			errors[i] /= switches;
			qp(errorStr[i] + ": " + errors[i]);
		}
	}
	
	/**
	 * Print all the prediction data in the program to the console
	 */
	static void scan() {
		if(data == null) {
			qp("No prediction data.");
			return;
		}
		
		for(Prediction[] set: data) {
			for(Prediction pred: set) {
				qp(pred);
			}
		}
	}
	
	/**
	 * 
	 */
	public static void generateCSVReport(String[] args) {
		String line = "";
		String inFile = "", outFile = "";
		double threshold = 0.5;
		
		if(args.length > 3) {
			inFile = args[1];
			outFile = args[2];
			try {
				threshold = Double.parseDouble(args[3]);
			} catch (NumberFormatException NFE) {
				qp("Could not parse threhhold");
				threshold = getDoubleFromUser("Please enter threshold:");
			}
		} else {
			qp("Enter: [file-to-read] [file-to-write] [threshold]: (without brackets)");
			
			try {
				line = input.nextLine();
			} catch (NoSuchElementException NSEE) { return; }
			
			try {
				inFile = line.substring(0, line.indexOf(DELIMITER));
				line = line.substring(line.indexOf(DELIMITER)+1);
				outFile = line.substring(0, line.indexOf(DELIMITER));
				line = line.substring(line.indexOf(DELIMITER)+1);
				threshold = Double.parseDouble(line.trim());
			} catch (RuntimeException re) {
				qp("Could not parse input.");
				re.printStackTrace();
				return;
			}
		}
		
		generateCSVReport(inFile, outFile, threshold);
	}
	
	/**
	 * List all the residues in a (patched) data file with a propensity above the given threshold.
	 * If the given threshold is too high, the output file will be blank.
	 * @param inFile: the file to read from
	 * @param outFile: the file to write to
	 * @param threshold: the propensity threshold
	 */
	public static void generateCSVReport(String inFile, String outFile, double threshold) {
		inFile = determineTrueFileName(inFile);
		ArrayList<String> data;
		
		if(!outFile.contains("/")) {
			outFile = OUTPUT + outFile;
		}
		
		try {
			data = analyze(inFile, threshold);
			writePredicted(data, outFile);
		} catch (IOException e) {
			qpl("Unable to Generate CSV Report for " + inFile);
			e.printStackTrace();
		}
		
	}
	
	/**
	 * 
	 * @param fileName
	 * @param threshold
	 * @return
	 * @throws IOException
	 */
	public static ArrayList<String> analyze(String fileName, double threshold) throws IOException {
		if(!fileName.endsWith(ResultsFusionModule.CSV)) { fileName += ResultsFusionModule.CSV; }
		
		BufferedReader reader = new BufferedReader(new FileReader(fileName));
		
		String header = reader.readLine();
		
		header = header.replaceAll("Predicted Values Model:isSwitch ~", "");
		header = header.replaceAll("[()]", "");
		header = header.replaceAll("[\"]", "");
		header = header.replaceAll("CResidue, levels=SideChainIDs", "Residue Type");
		
		String columns[] = header.split(",");
		
		//qp(columns);
		//qp(columns.length);
		//qp("");
		
		ArrayList<String[]> dataList = new ArrayList<String[]>();
		ArrayList<String> switchList = new ArrayList<String>();
		
		for(String dataLine = reader.readLine(); reader.ready(); dataLine = reader.readLine()) {
			String data[] = dataLine.split(",");
			//qp(data.length);
			dataList.add(data);
		}
		
		reader.close();
		
		for(int index = 0; index < dataList.size(); ++index) {
			
			for(int row_index = 3; row_index < columns.length-1; ++row_index) {
				
				double val = Double.parseDouble(dataList.get(index)[row_index]);
				if(val >= threshold) {
					StringBuilder entryBuilder = new StringBuilder();
					entryBuilder.append(dataList.get(index)[1]);
					entryBuilder.append(" [");
					entryBuilder.append(formatInt3(dataList.get(index)[0]));
					entryBuilder.append("] (");
					entryBuilder.append(dataList.get(index)[2]);
					entryBuilder.append(") ");
					entryBuilder.append(columns[row_index]);
					entryBuilder.append(": ");
					entryBuilder.append(dataList.get(index)[row_index]);
					switchList.add(entryBuilder.toString());
				}
			}
		}
		
		return switchList;
	}
	
	/**
	 * 
	 * @param data
	 * @param fileName
	 * @throws FileNotFoundException
	 */
	public static void writePredicted(ArrayList<String> data, String fileName) throws FileNotFoundException {
		if(fileName.endsWith(".csv")) { fileName = fileName.replaceAll(".csv", ".txt"); }
		else if(!fileName.endsWith(".txt")) { fileName += ".txt"; }
		
		PrintWriter writer = new PrintWriter(fileName);
		for(int index = 0; index < data.size(); ++index) {
			writer.write(data.get(index)+"\n");
		}
		writer.close();
	}
	
	/**
	 * Determine the name of the roc-log file
	 * @return name of the roc-log file
	 */
	private static String makeLogFileName() {
		if(lastLoadedFileName == null) {
			throw new MissingDataException("No prediction data has been loaded!");
		}
		
		String logFileName;
		if(lastLoadedFileName.endsWith(CSV)) {
			logFileName = lastLoadedFileName.substring(0, lastLoadedFileName.indexOf(CSV));
		} else {
			logFileName = lastLoadedFileName;
		}
		
		if(logFileName.contains("/")) {
			logFileName.substring(logFileName.indexOf("/")+1);
		}
		
		logFileName = logFileName + "-roc-log.csv";
		
		return logFileName;
	}
	
	/**
	 * 
	 * @param s
	 * @return
	 */
	public static String formatInt3(String s) {
		if(s.length() == 0) { return "000"; }
		else if(s.length() == 1) { return "00" + s; }
		else if(s.length() == 2) { return "0" + s; }
		else { return s; }
	}
	
	/**
	 * Formats an integer as a string with length 3
	 * @param i
	 * @return
	 */
	public static String formatInt3(int i) {
		if(i < 10) { return "00" + i; }
		else if(i < 100) { return "0" + i; }
		else { return "" + i; }
	}
	
	/**
	 * 
	 * @param lastLoadedFileName
	 */
	@SuppressWarnings("unused")
	private static ArrayList<String[]> extractROCPlotData(String lastLoadedFileName) {
		String shavedFileName = lastLoadedFileName.substring(0, lastLoadedFileName.indexOf(".")-8);
		shavedFileName = shavedFileName.substring(shavedFileName.indexOf("/"));
		shavedFileName = OUTPUT + shavedFileName + "-ROCPlot";
		
		File plotFolder = new File(shavedFileName);
		if(plotFolder.exists()) { plotFolder.delete(); }
		plotFolder.mkdir();
		
		BufferedReader rocLogReader = null;
		ArrayList<String[]> dataList = new ArrayList<String[]>();
		
		try {
			rocLogReader = new BufferedReader(new FileReader(makeLogFileName()));
			
			//throw away the header
			rocLogReader.readLine();
			
			while(rocLogReader.ready()) {
				String line = rocLogReader.readLine();
				String lineData[] = new String[3];				
				
				line = line.substring(line.indexOf(",")+1);
				lineData[0] = line.substring(0, line.indexOf(","));
				lineData[2] = line.substring(line.lastIndexOf(",")+1);
				line = line.substring(0, line.lastIndexOf(","));
				lineData[1] = line.substring(line.lastIndexOf(",")+1);
				
				dataList.add(lineData);
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return dataList;
	}
	
	/**
	 * Properly set the decimal formatter
	 * @param decimal
	 */
	private static void setThresholdFormatterToProperlyFormat(double decimal) {
		double thresholdLog = Math.log10(decimal);
		int thresholdFormat = (int) thresholdLog;
		
		if(thresholdFormat > thresholdLog) {
			thresholdLog -= 1;
		}
		 
		thresholdFormat *= -1;
		
		StringBuilder thresholdFormatBuilder = new StringBuilder();
		thresholdFormatBuilder.append(".");
		for(int i = 0; i < thresholdFormat; ++i) {
			thresholdFormatBuilder.append("#");
		}
		
		thresholdFormatter = new DecimalFormat(thresholdFormatBuilder.toString());
	}
}
