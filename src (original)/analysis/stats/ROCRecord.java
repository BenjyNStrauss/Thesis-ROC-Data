package analysis.stats;

import analysis.CSVParser;

/**
 * 
 * @author Benjy Strauss
 *
 */

public class ROCRecord extends StatsObject {
	private static final long serialVersionUID = 1L;
	private String description;
	public int falseNeg;
	public int falsePos;
	public int trueNeg;
	public int truePos;
	
	public ROCRecord(String desc) { description = desc; }
	
	public ROCRecord() { }
	
	/**
	 * Get the header for a log file of ROC Records
	 * @return
	 */
	public static String fullROCLogHeader() {
		return "Threshhold,Description,TruePos,FalsePos,TrueNeg,FalseNeg,TruePositiveRate,FalsePositiveRate,Accuracy";
	}
	
	/**
	 * Returns the data in the ROCRecord as a string ready to be written to a .csv file
	 * @return a csv-ready string containing the data in the ROCRecord
	 */
	public String toCSVRow() {
		String retVal = description + "," + truePos + "," + falsePos + "," + trueNeg + "," + falseNeg + ","
				+ truePositiveRate() + "," + falsePositiveRate()  + "," + accuracy();
		return retVal;
	}
	
	/**
	 * Defined as the ratio of positive predictions that are accurate
	 * @return fraction true positive of all positive
	 */
	public double posHitRatio() {
		double posRatio = (double) truePos / ((double) truePos + (double) falsePos);
		if(Double.isNaN(posRatio)) { posRatio = 0; }
		return posRatio;
	}
	
	/**
	 * Defined as the ratio of negative predictions that are accurate
	 * @return fraction true negative of all negative
	 */
	public double negHitRatio() {
		double negRatio = (double) trueNeg / ((double) trueNeg + (double) falseNeg);
		if(Double.isNaN(negRatio)) { negRatio = 0; }
		return negRatio;
	}
	
	public double accuracy() {
		double accuracy = (double) (truePos + trueNeg) / (double) (truePos + falsePos + trueNeg + falseNeg);
		return accuracy;
	}
	
	/**
	 * Get the True Positive Rate (TPR)
	 * TPR = TP ÷ (TP + FN)
	 * @return TPR
	 */
	public double truePositiveRate() {
		double truePositiveRate = (double) truePos / ((double) truePos + (double) falseNeg);
		if(Double.isNaN(truePositiveRate)) { truePositiveRate = 0; }
		return truePositiveRate;
	}
	
	/**
	 * Get the False Positive Rate (FPR)
	 * FPR = FP ÷ (FP + TN)
	 * @return FPR
	 */
	public double falsePositiveRate() {
		double falsePositiveRate = (double) falsePos / ((double) falsePos + (double) trueNeg);
		if(Double.isNaN(falsePositiveRate)) { falsePositiveRate = 0; }
		return falsePositiveRate;
	}
	
	/**
	 * 
	 * @param infile
	 * @param outfile
	 */
	public static void organizeROCRecordFile(String infile, String outfile) {
		//2401 long
		String[] data = getFileLines(infile);
		
		int combinations = CSVParser.DESCRIPTOR_LIST.length+1;
		String[][] tableOfLines = new String[combinations][100];
		
		for(int index = 0; index < (data.length-1); ++index) {
			tableOfLines[index % combinations][index / combinations] = data[index+1];
		}
		
		for(int descriptorID = 0; descriptorID < CSVParser.DESCRIPTOR_LIST.length; ++descriptorID) {
			for(int index = 0; index < 100; ++index) {
				data[descriptorID*100+index+1] = tableOfLines[descriptorID][index];
		 	}
		 }
		
		writeFileLines(outfile, data);
	}
	
	public String toString() {
		if(description != null) {
			return description;
		} else {
			return "ROCRecord";
		}
	}
}
