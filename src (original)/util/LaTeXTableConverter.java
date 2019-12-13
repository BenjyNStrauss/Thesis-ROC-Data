package util;

import java.util.ArrayList;
import java.util.Scanner;

import analysis.stats.RegressionResult;

/**
 * Turns a .csv file into a LaTeX file
 * @author Benjy Strauss
 *
 */

public class LaTeXTableConverter extends BaseTools {
	private static final String IN = "wald.csv";
	private static final String OUT = "wald.txt";
	
	private static Scanner input;
	
	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		String inFileName = IN;
		String outFileName = OUT;
		
		if(args.length > 0) { inFileName = args[0]; }
		if(args.length > 1) { outFileName = args[1]; }
		
		if(inFileName == null) {
			qp("Enter Infile: ");
			inFileName = input.nextLine();
		}
		
		if(inFileName == null) {
			qp("Enter Outfile: ");
			inFileName = input.nextLine();
		}
		
		convertCSVToTable(inFileName, outFileName);
	}
	
	/**
	 * 
	 * @param inFile: the file to read from
	 * @param outFile: the file to write to
	 */
	public static void convertCSVToTable(String inFile, String outFile) {
		String[] data = getFileLines(inFile);
		
		for(int index = 0; index < data.length; ++index) {
			data[index] = data[index].replaceAll(",", " & ") + "\\\\ \\hline";
		}
		
		writeFileLines(outFile, data);
	}
	
	/**
	 * 
	 * @param inFile: the file to read from
	 * @param outFile: the file to write to
	 */
	public static void convertModelResultsToTable(String inFile, String outFile) {
		String[] data = getFileLines(inFile);
		
		ArrayList<RegressionResult> resultList = new ArrayList<RegressionResult>();
		ArrayList<String> lineVector = new ArrayList<String>();
		
		for(int index = 0; index < data.length; ++index) {
			if(data[index].equals("")) {
				if(lineVector.size() != 0) {
					String tableLines[] = new String[lineVector.size()];
					lineVector.toArray(tableLines);
					resultList.add(new RegressionResult(tableLines));
					lineVector.clear();
				}
			} else {
				lineVector.add(data[index]);
			}
		}
		
		ArrayList<String> latexLines = new ArrayList<String>();
		
		for(RegressionResult result: resultList) {
			String lines[] = result.toLaTeX();
			for(String line: lines) {
				latexLines.add(line);
			}
			latexLines.add("");
		}
		
		data = new String[latexLines.size()];
		latexLines.toArray(data);
		
		writeFileLines(outFile, data);
	}
}
