package util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Contains methods for all classes in all packages to use
 * 
 * @author Benjy Strauss
 *
 */

public abstract class BaseTools {
	private static final String SYSTEM_LOG = "SysLog.txt";
	private static final DateTimeFormatter LOG_FORMATTER = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss");
	
	public static final String CSV = ".csv";
	public static final String TXT = ".txt";
	protected static final String ARG_PREFIX = "-";
	protected static final String DELIMITER = " ";
	protected static final String REGRESSION_INPUT = "regression/input/";
	protected static final String REGRESSION_MODELS = "regression/models/";
	protected static final String OUTPUT = "output/";
	protected static final String FINISHED = "Operations Completed.";
	
	protected static final String BMAIL = "Benjynstrauss@gmail.com";
	
	private static boolean system_debug_mode = false;
	
	/*
	 * Currently unused
	 * This string is supposed to be a string that the user would never enter via the terminal.
	 * Thus, if a main() method was called using this string as an argument, one would know that
	 * that main() method was called internally
	 */
	protected static final String[] EMBED = { "|✔︎♥︎↳➟⨝⚘⚘⨁|" };
	
	public static final String FUSION_DIRECTORY = OUTPUT;
	
	/**
	 * Integer.min(...) does not exist on some Linux versions of Java
	 * @param a
	 * @param b
	 * @return: the minimum integer
	 */
	public static final int min(int a, int b) {
		if(a < b) { return a; } else { return b; }
	}
	
	/**
	 * Integer.min(...) does not exist on some Linux versions of Java,
	 * so it would make sense that Double.min(...) also does not exist
	 * @param a
	 * @param b
	 * @return: the minimum double
	 */
	public static final double min(double a, double b) {
		if(a < b) { return a; } else { return b; }
	}
	
	/**
	 * Integer.max(...) does not exist on some Linux versions of Java
	 * @param a
	 * @param b
	 * @return: the maximum integer
	 */
	public static final int max(int a, int b) {
		if(a > b) { return a; } else { return b; }
	}
	
	/**
	 * Integer.max(...) does not exist on some Linux versions of Java,
	 * so it would make sense that Double.max(...) also does not exist
	 * @param a
	 * @param b
	 * @return: the maximum double
	 */
	public static final double max(double a, double b) {
		if(a > b) { return a; } else { return b; }
	}
	
	/**
	 * 
	 * @param debug
	 */
	protected static final void setDebug(boolean debug) {
		system_debug_mode = debug;
	}
	
	/**
	 * Verifies that a string ends in .csv
	 * Useful for ensuring that user inputed filenames can be opened
	 * @param arg: the string to verify
	 * @return: a string that is guaranteed to end in .csv
	 */
	public static final String verifyCSV(String arg) {
		if(!arg.endsWith(".csv")) {
			arg += CSV;
		}
		return arg;
	}
	
	/**
	 * This method is intentional--don't erase
	 * Call this to signify that a method has not been coded yet and still needs work
	 */
	protected static final void notCodedYet() {
		qp("Not Coded Yet.");
	}
	
	/**
	 * Determine if an array of Strings contains a given string
	 * @param array
	 * @param searchTerm
	 * @return
	 */
	protected static final boolean stringArrayContains(String array[], String searchTerm) {
		return stringArrayContains(array, searchTerm, true);
	}
	
	/**
	 * Determine if an array of Strings contains a given string
	 * @param array
	 * @param searchTerm
	 * @param ignoreCase
	 * @return
	 */
	protected static final boolean stringArrayContains(String array[], String searchTerm, boolean ignoreCase) {
		if(ignoreCase) { 
			searchTerm = searchTerm.toLowerCase();
			for(String str: array) { if(str.toLowerCase().equals(searchTerm)) { return true; } }
		} else {
			for(String str: array) { if(str.equals(searchTerm)) { return true; } }
		}
		
		return false;
	}
	
	/**
	 * Determine if an array of Strings contains one of many strings
	 * @param array
	 * @param searchTerms
	 * @param ignoreCase
	 * @return
	 */
	protected static final boolean stringArrayContains(String array[], String searchTerms[], boolean ignoreCase) {
		for(String str: searchTerms) { if(stringArrayContains(array, str, ignoreCase)) { return true; } }
		return false;
	}
	
	/**
	 * Determine if an array of Strings contains one of many strings
	 * @param array
	 * @param searchTerms
	 * @param ignoreCase
	 * @return
	 */
	protected static final boolean stringArrayContains(String array[], String searchTerms[]) {
		return stringArrayContains(array, searchTerms, true);
	}
	
	/**
	 * Determine if an array of Strings contains a given string
	 * @param array
	 * @param searchTerm
	 * @return
	 */
	protected static final boolean stringArrayContainsPartial(String array[], String searchTerm) {
		for(String str: array) { if(str.startsWith(searchTerm)) { return true; } }
		return false;
	}
	
	/**
	 * Determine if an array of Strings contains a given string
	 * @param array
	 * @param searchTerm
	 * @return
	 */
	protected static final boolean stringArrayContainsPartial(String array[], String searchTerm, boolean ignoreCase) {
		if(ignoreCase) {
			for(String str: array) { if(str.toLowerCase().startsWith(searchTerm.toLowerCase())) { return true; } }
		} else {
			for(String str: array) { if(str.startsWith(searchTerm)) { return true; } }
		}
		return false;
	}
	
	/**
	 * Determine if an array of Strings contains one of many strings
	 * @param array
	 * @param searchTerms
	 * @return
	 */
	protected static final boolean stringArrayContainsPartial(String array[], String searchTerms[]) {
		for(String str: searchTerms) { if(stringArrayContains(array, str, true)) { return true; } }
		return false;
	}
	
	/**
	 * Determine if an array of Strings contains one of many strings
	 * @param array
	 * @param searchTerms
	 * @return
	 */
	protected static final boolean stringArrayContainsPartial(String array[], String searchTerms[], boolean ignoreCase) {
		for(String str: searchTerms) { if(stringArrayContainsPartial(array, str, ignoreCase)) { return true; } }
		return false;
	}
	
	/**
	 * Determines where a file really is from a given name.
	 * @param filename
	 * @return
	 */
	protected static final String determineTrueFileName(String filename) {
		String trueFileName = null;
		if(fileExists(filename)) { return filename; }
		
		if(!filename.endsWith(CSV)) {
			trueFileName = filename + CSV;
			if(fileExists(trueFileName)) { return trueFileName; }
		}
		
		if(!filename.endsWith(TXT)) {
			trueFileName = filename + TXT;
			if(fileExists(trueFileName)) { return trueFileName; }
		}
		
		if(!filename.startsWith(OUTPUT)) {
			trueFileName = OUTPUT + filename;
			if(fileExists(trueFileName)) { return trueFileName; }
			
			if(!filename.endsWith(CSV)) {
				trueFileName = trueFileName + CSV;
				if(fileExists(trueFileName)) { return trueFileName; }
			}
			
			if(!filename.endsWith(TXT)) {
				trueFileName = trueFileName.substring(0, trueFileName.length()-CSV.length()) + TXT;
				if(fileExists(trueFileName)) { return trueFileName; }
			}
		}
		
		if(!filename.startsWith(REGRESSION_INPUT)) {
			trueFileName = REGRESSION_INPUT + filename;
			if(fileExists(trueFileName)) { return trueFileName; }
			
			if(!filename.endsWith(CSV)) {
				trueFileName = trueFileName + CSV;
				if(fileExists(trueFileName)) { return trueFileName; }
			}
			
			if(!filename.endsWith(TXT)) {
				trueFileName = trueFileName.substring(0, trueFileName.length()-CSV.length()) + TXT;
				if(fileExists(trueFileName)) { return trueFileName; }
			}
		}
		
		if(!filename.startsWith(REGRESSION_MODELS)) {
			trueFileName = REGRESSION_MODELS + filename;
			if(fileExists(trueFileName)) { return trueFileName; }
			
			if(!filename.endsWith(CSV)) {
				trueFileName = trueFileName + CSV;
				if(fileExists(trueFileName)) { return trueFileName; }
			}
			
			if(!filename.endsWith(TXT)) {
				trueFileName = trueFileName.substring(0, trueFileName.length()-CSV.length()) + TXT;
				if(fileExists(trueFileName)) { return trueFileName; }
			}
		}
		
		return filename;
	}
	
	/**
	 * Determine if a file with the given name exists.
	 * @param filename
	 * @return
	 */
	protected static final boolean fileExists(String filename) {
		File file = new File(filename);
		return file.exists();
	}
	
	/**
	 * 'qpl' stands for Quick-Print and Log
	 * @param arg0: the object to print
	 */
	protected static final void qpl(String arg0) {
		log(arg0);
		qp(arg0);
	}
	
	protected static final void log(String arg0) {
		FileWriter logWriter;
		LocalDateTime now = LocalDateTime.now();
		
		try {
			logWriter = new FileWriter(SYSTEM_LOG, true);
			logWriter.write(LOG_FORMATTER.format(now) + "\n");
			logWriter.write(arg0 + "\n\n");
			logWriter.close();
		} catch (IOException e) {
			qerr("Could not write to log: " + arg0);
			e.printStackTrace();
		}
	}
	
	/**
	 * Reads the lines of the specified file into an Array of Strings
	 * @param filename
	 * @return
	 */
	protected static final String[] getFileLines(String filename) {
		String retVal[] = null;
		ArrayList<String> fileLines = new ArrayList<String>();
		
		BufferedReader reader;
		String line;
		
		try {
			reader = new BufferedReader(new FileReader(filename));
			for(line = reader.readLine(); reader.ready(); line = reader.readLine()) {
				fileLines.add(line);
			}
			fileLines.add(line);
			reader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		retVal = new String[fileLines.size()];
		fileLines.toArray(retVal);
		return retVal;
	}

	/**
	 * 
	 * @param fileName
	 * @param lines
	 */
	protected static final void writeFileLines(String fileName, List<String> lines) {
		String[] stringArray = new String[lines.size()];
		lines.toArray(stringArray);
		writeFileLines(fileName, stringArray);
	}
	
	/**
	 * 
	 * @param fileName
	 * @param lines
	 */
	protected static final void writeFileLines(String fileName, String[] lines) {
		PrintWriter writer = null;
		
		try {
			writer = new PrintWriter(fileName);
			for(String line: lines) {
				writer.write(line + "\n");
			}
			writer.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Turns an array of strings into one string separated by white space
	 * @param args
	 * @return
	 */
	protected static final String toStringFromArray(String[] args) {
		StringBuilder resultBuilder = new StringBuilder();
		for(Object o: args) {
			resultBuilder.append(o + " ");
		}
		resultBuilder.setLength(resultBuilder.length()-1);
		return resultBuilder.toString();
	}
	
	protected static final void dqp(Object arg0) {
		if(system_debug_mode) { qp(arg0); }
	}
	
	/**
	 * 'qp' stands for Quick-Print
	 * @param arg0: the object to print
	 */
	protected static final void qp(Object arg0) {
		if(arg0 != null && arg0.getClass().isArray()) {
			Object[] i_arg0 = (Object[]) arg0;
			for(Object o: i_arg0) {
				System.out.println(o);
			}
		} else if(arg0 instanceof List) {
			List<?> l_arg0 = (List<?>) arg0;
			for(Object o: l_arg0) {
				System.out.println(o);
			}
		} else {
			System.out.println(arg0);
		}
	}
	
	/**
	 * 'qp' stands for quick-print
	 * @param arg0: the object to print
	 */
	protected static final void qps(Object arg0) {
		if(arg0 != null && arg0.getClass().isArray()) {
			Object[] i_arg0 = (Object[]) arg0;
			for(Object o: i_arg0) {
				System.out.println("*" + o + "*");
			}
		} else if(arg0 instanceof List) {
			List<?> l_arg0 = (List<?>) arg0;
			for(Object o: l_arg0) {
				System.out.println("*" + o + "*");
			}
		} else {
			System.out.println("*" + arg0 + "*");
		}
	}
	
	/**
	 * 'qerr' stands for quick-error
	 * @param arg0: the object to print to System.err
	 */
	protected static final void qerr(Object arg0) {
		if(arg0 != null && arg0.getClass().isArray()) {
			Object[] i_arg0 = (Object[]) arg0;
			for(Object o: i_arg0) {
				System.err.println(o);
			}
		} else if(arg0 instanceof List) {
			List<?> l_arg0 = (List<?>) arg0;
			for(Object o: l_arg0) {
				System.err.println(o);
			}
		} else {
			System.err.println(arg0);
		}
	}
}
