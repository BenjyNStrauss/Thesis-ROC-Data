package util;

import java.io.InputStream;

/**
 * Just some handy tools for running scripts
 * @author Benjy Strauss
 *
 */

public abstract class Script extends BaseTools {
	protected String output;
	protected String error;
	
	/**
	 * Used only to redirect Python output to Java Output 
	 * @param is: The InputStream
	 * @return: the text in the stream
	 */
	protected static final String getInputAsString(InputStream is) {
		try(java.util.Scanner s = new java.util.Scanner(is)) { 
			return s.useDelimiter("\\A").hasNext() ? s.next() : ""; 
		}
	}
	
	/**
	 * 
	 * @param retval
	 */
	protected static void analyzeScriptReturnValue(int retval) {
		if (retval == 0) {
			System.out.println("Success!");
		} else {
			System.out.println("Fail ("+retval+")");
		}
	}
}
