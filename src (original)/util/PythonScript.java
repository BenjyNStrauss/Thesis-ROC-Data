package util;

import java.io.IOException;

import bio.exceptions.PythonException;

/**
 * Represents (and runs) Python Scripts
 * @author Benjy Strauss
 *
 */

public class PythonScript extends Script {
	private static final String DEFAULT_PYTHON = "/usr/local/bin/python3";
	
	private static String python_path = DEFAULT_PYTHON;
	private String filePath;
	private String[] params;
	
	/**
	 * Sets the path for Python to "/usr/local/bin/python3"
	 * This is the location of Python3 on Mac OS X when installed with brew
	 * 
	 */
	public static void setDefaultPythonPath() { python_path = DEFAULT_PYTHON; }
	
	/**
	 * Sets the path for Python to the string specified
	 * @param path: the path to the Python executable
	 */
	public static void setPythonPath(String path) { python_path = path; }
	
	/**
	 * Gets the current path to Python
	 * @return Path to the Python verison
	 */
	public static String getPythonPath() { return python_path; }
	
	/**
	 * 
	 * @param filePath
	 * @param params
	 */
	public PythonScript(String filePath, String... params) {
		this.filePath = filePath;
		this.params = params;
	}
	
	/**
	 * 
	 * @return
	 */
	public int run() {
		String[] pythonCommands = new String[params.length+2];
		pythonCommands[0] = python_path;
		pythonCommands[1] = filePath;
		
		for(int index = 0; index < params.length; ++index) {
			pythonCommands[index+2] = params[index];
		}
		
		ProcessBuilder builder = new ProcessBuilder(pythonCommands);
		Process proc;
		//@SuppressWarnings("unused")
		int retval = 0;
		
		try {
			proc = builder.start();
			output = getInputAsString(proc.getInputStream());
			error = getInputAsString(proc.getErrorStream());
			
			retval = proc.waitFor();
			if (retval == 0) {
				System.out.println("Success!");
			} else {
				System.out.println("Fail ("+retval+")");
			}
		} catch (IOException e) {
			throw new PythonException("I/O Error for: " + filePath);
		} catch (InterruptedException e) {
			throw new PythonException("Process interrupted for: " + filePath);
		}
		
		if(error != "") { 
			qerr(error);
			throw new PythonException("Python Error:\n" + error);
		}
		
		return retval;
	}
	
	/**
	 * 
	 * @param filePath
	 * @param params
	 * @return
	 */
	public static int runPythonScript(String filePath, String... params) {
		String[] pythonCommands = new String[params.length+2];
		pythonCommands[0] = python_path;
		pythonCommands[1] = filePath;
		
		for(int index = 0; index < params.length; ++index) {
			pythonCommands[index+2] = params[index];
		}
		
		ProcessBuilder builder = new ProcessBuilder(pythonCommands);
		Process proc;
		//@SuppressWarnings("unused")
		String stdOut = "", stdErr = "";
		int retval = 0;
		
		try {
			proc = builder.start();
			stdOut = getInputAsString(proc.getInputStream());
			stdErr = getInputAsString(proc.getErrorStream());
			
			retval = proc.waitFor();
			analyzeScriptReturnValue(retval);
			
		} catch (IOException e) {
			throw new PythonException("I/O Error for: " + filePath);
		} catch (InterruptedException e) {
			throw new PythonException("Process interrupted for: " + filePath);
		}
		
		if(stdOut != "") { qp(stdOut); }
		
		if(stdErr != "") { 
			qerr(stdErr);
			throw new PythonException("Python Error:\n" + stdErr);
		}
		
		return retval;
	}
	
	public String getStdOut() { return output; }
	public String getStdErr() { return error; }
}
