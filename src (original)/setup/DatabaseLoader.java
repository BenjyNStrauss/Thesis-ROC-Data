package setup;

import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Amber95      FREE          SD         BURIED          SD        HELIX         SD        SHEET      SD
 * 
 * N
 * H - HN
 * A - CA
 * P - C'
 * B - CB
 * O
 * 
 * @author Benjy Strauss
 * VALUES (value1, value2, value3, ...);
 * 
 * + "(\"atoms\", \"residue\", \"amber95\",  \"free\", \"free-sd\",\"buried\", \"buried-sd\",  \"helix\", \"helix-sd\", \"sheet\", \"sheet-sd\") "
 */

@SuppressWarnings("unused")
public class DatabaseLoader {
	private static final String STATEMENT_START = "INSERT INTO \"partial-charges-protein-3d-structures\"\nVALUES (";
	private static final String STATEMENT_END = ");";
	
	private static final String myDriver = "org.sqlite.JDBC";
	private static final String jbioDBURL = "jdbc:sqlite:JBioDataBase.db";
	
	private static String atomCode;
	
	public static void main(String args[]) {
		BufferedReader br;
		
		try {
			br = new BufferedReader(new FileReader("table.txt"));
		    String line = br.readLine();

		    for (line = br.readLine(); line != null; line = br.readLine()) {
		    		if(line.charAt(0) == '#') {
		    			atomCode = line.substring(1);
		    			continue;
		    		}
		    	
		    		writeData(line);
		    }
		    
		    br.close();
		} catch (Exception e){ 
			
		}
	}
	
	private static final void writeData(String line) {
		String parsedLine = fixLine(line);
		
		String query = STATEMENT_START + parsedLine + STATEMENT_END;
		
		qp(query);
		
		Connection conn = null;
		Statement st = null;
		int result;
		
		try {
			Class.forName(myDriver);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		try {
			conn = DriverManager.getConnection(jbioDBURL);
			st = conn.createStatement();
			result = st.executeUpdate(query);
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private static final String fixLine(String line) {
		String retVal = "\'" + atomCode + "\'";
		String vals[] = line.split("\\s+");
		
		for(int i = 0; i < vals.length ; ++i) {
			if(i == 0) {
				retVal += ",\'" + vals[i] + "\'";
			} else {
				retVal += "," + vals[i];
			}
		}
		
		return retVal;
	}
	
	/**
	 * qp stands for quick-print
	 * mainly for use in debugging
	 * @param arg0: the object to print
	 */
	protected static final void qp(Object arg0) {
		if(arg0 != null && arg0.getClass().isArray()) {
			Object[] i_arg0 = (Object[]) arg0;
			for(Object o: i_arg0) {
				System.out.println(o);
			}
		} else {
			System.out.println(arg0);
		}
	}
}
