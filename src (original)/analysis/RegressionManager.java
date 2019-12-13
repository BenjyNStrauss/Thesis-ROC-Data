package analysis;

import java.io.IOException;
import java.util.Scanner;

import bio.exceptions.FileNotFoundRuntimeException;
import util.BaseTools;
import util.PythonScript;

/**
 * 
 * @author Benjy Strauss
 *
 */

public class RegressionManager extends BaseTools {
	
	/**
	 * 
	 * @param args
	 * @param scanner
	 */
	public static void logistic_regression(String args[], Scanner scanner) {
		logistic_regression(args, scanner, 2);
	}
	
	/**
	 * 
	 * @param args
	 * @param scanner
	 * @param version
	 */
	public static void logistic_regression(String args[], Scanner scanner, int version) {
		if(args.length < 2) {
			logistic_regression(scanner);
		} else if(args.length < 3) {
			qpl("Using \"" + args[1] + "\" as both learning and test sets.");
			if(determineTrueFileName(args[1]) != null) {
				switch(version) {
				case 3:		logistic_regression_charge(args[1], args[2]);	break;
				default:		logistic_regression(args[1], args[2]);			//this is version 2
				}
			} else {
				throw new FileNotFoundRuntimeException(args[1]);
			}
		} else {
			if(determineTrueFileName(args[1]) != null) {
				if(determineTrueFileName(args[2]) != null) {
					switch(version) {
					case 3:		logistic_regression_charge(args[1], args[2]);	break;
					default:		logistic_regression(args[1], args[2]);			//this is version 2
					}
				} else {
					throw new FileNotFoundRuntimeException(args[2]);
				}
			} else {
				throw new FileNotFoundRuntimeException(args[1]);
			}
		}
	}
	
	/**
	 * 
	 * @param scanner
	 */
	public static void logistic_regression(Scanner scanner) {
		String learning_set = null;
		String test_set = null;
		
		String line = null;
		
		while(determineTrueFileName(line) == null) {
			qp("Enter Learning Set path:");
			line = scanner.next();
		}
		learning_set = line;
		
		line = null;
		
		while(determineTrueFileName(line) == null) {
			qp("Enter Test Set path:");
			line = scanner.next();
		}
		test_set = line;
		
		logistic_regression(learning_set, test_set);
	}
	
	/**
	 * Perform a logistic regression on a pair of data sets
	 * @param learning_set
	 * @param test_set
	 */
	public static void logistic_regression(String learning_set, String test_set) {
		if(learning_set == null) { throw new NullPointerException("Learning Set cannot be NULL!"); }
		if(test_set == null) { throw new NullPointerException("Test Set cannot be NULL!"); }
		
		PythonScript.runPythonScript("regression/logistic-switch2.py", learning_set, test_set);
		autoFuse(test_set, false);
	}
	
	/**
	 * Perform a logistic regression on a pair of data sets
	 * @param learning_set
	 * @param test_set
	 */
	public static void logistic_regression_charge(String learning_set, String test_set) {
		if(learning_set == null) { throw new NullPointerException("Learning Set cannot be NULL!"); }
		if(test_set == null) { throw new NullPointerException("Test Set cannot be NULL!"); }
		
		PythonScript.runPythonScript("regression/logistic-switch3.py", learning_set, test_set);
		autoFuse(test_set, true);
	}
	
	/**
	 * 
	 * @param test_set
	 */
	private static void autoFuse(String test_set, boolean amber95Enabled) {
		String modelFile = "regression/models/"+test_set.substring(test_set.lastIndexOf("/")+1);
		modelFile = modelFile.replaceAll(CSV, "");
		modelFile += "_exact_pred" + CSV;
		
		String modelFile2 = "regression/models/"+test_set.substring(test_set.lastIndexOf("/")+1);
		modelFile2 = modelFile2.replaceAll(CSV, "");
		modelFile2 += "_pred" + CSV;
		
		try {
			ResultsFusionModule.CSV_Fusion(test_set, modelFile, OUTPUT+"exact_"+test_set);
			ResultsFusionModule.CSV_Fusion(test_set, modelFile2, OUTPUT+"pred_"+test_set);
		} catch (IOException e) {
			qp("CSV Fusion Failed");
			e.printStackTrace();
		}
	}
}
