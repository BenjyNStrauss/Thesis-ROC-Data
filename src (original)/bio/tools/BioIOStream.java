package bio.tools;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import bio.BioObject;
import util.BaseTools;

/**
 * 
 * @author Benjy Strauss
 *
 */

public final class BioIOStream extends BaseTools {
	public static final String SAVE_PATH = "files/saved/";
	
	public static void saveObject(BioObject... objects) { 
		for(BioObject object: objects) {
			saveObject(object, object.impliedFileName());
		}
	}
	
	public static void saveObject(BioObject object) { saveObject(object, object.impliedFileName()); }
	
	public static void saveObject(Serializable object, String fileName) {
		FileOutputStream outFile = null;
		ObjectOutputStream outStream = null;
		
		try {
			outFile = new FileOutputStream(SAVE_PATH + fileName);
			outStream = new ObjectOutputStream(outFile);
			outStream.writeObject(object);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (outFile != null) {
				try {
					outFile.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			if (outStream != null) {
				try {
					outStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public static Serializable readObject(String fileName) throws FileNotFoundException {
		Serializable retVal = null;
		FileInputStream fileInput = null;
		ObjectInputStream objectInput = null;

		fileInput = new FileInputStream(fileName);
		
		try {
			objectInput = new ObjectInputStream(fileInput);
			retVal = (Serializable) objectInput.readObject();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} finally {
			if (fileInput != null) {
				try {
					fileInput.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			if (objectInput != null) {
				try {
					objectInput.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		}
		return retVal;
	}
}
