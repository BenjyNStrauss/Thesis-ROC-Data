package setup;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import util.BaseTools;

/**
 * Was used to create files containing the learning set and test set from the database where
 * the proteins were added after being manually selected.  Note that Uniprot IDs will have to
 * be added by hand to the output files for some chains
 * @author Benjy Strauss
 *
 */

@SuppressWarnings("unused")
public class ClusterScanner extends BaseTools {
	private static final String myDriver = "org.sqlite.JDBC";
	private static final String jbioDBURL = "jdbc:sqlite:fastas.db";
	
	//
	private static final String QUERY = "select \"Protein-Chain\", \"Perfect Matches\", \"Imperfect Matches\" from Acetyltransferaces " + 
			"where (\"Notes\" not like '%REDUNDANT%' and \"Notes\" not like '%OBSOLETE%') OR \"Notes\" is null " + 
			"order by \"Protein-Chain\"";
	
	private static final String SIRT_QUERY = "select \"Protein-Chain\", \"Perfect-Matches\", \"Imperfect-Matches\" from Sirtuins " + 
			"where (\"Notes\" not like '%REDUNDANT%' and \"Notes\" not like '%OBSOLETE%') OR \"Notes\" is null " + 
			"order by \"Protein-Chain\"";

	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		//some kind of setup...
		try {
			Class.forName(myDriver);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		Connection conn = null;
		Statement st = null;
		ResultSet results = null;
		
		ArrayList<ArrayList<String>> dataList = new ArrayList<ArrayList<String>>();
		
		try {
			conn = DriverManager.getConnection(jbioDBURL);
			st = conn.createStatement();
			//change this to "QUERY" to generate the learning set
			results = st.executeQuery(SIRT_QUERY);
			
			for(; !results.isAfterLast(); results.next()) {
				ArrayList<String> clusterData = new ArrayList<String>();
				
				String cluster_ref = results.getString(1);
				String same = results.getString(2);
				String similar = results.getString(3);
				
				cluster_ref = cluster_ref.trim().replaceAll(":", "");
				
				//if the cluster contains a single protein
				if(same == null && similar == null) { continue; }
				//add the reference
				clusterData.add(cluster_ref);
				
				if(same != null) {
					String seq100[] = same.split("\n");
					
					for(String s: seq100) {
						clusterData.add(s.trim().replaceAll(":", ""));
					}
				}
				
				if(similar != null) {
					String almost[] = similar.split("\n");
					
					for(String s: almost) {
						clusterData.add(s.trim().substring(0, 6).replaceAll(":", ""));
					}
				}
				dataList.add(clusterData);
			}
			
		} catch (SQLException e) {
			qp("Error in obtaining queries");
			e.printStackTrace();
		}
		
		try {
			PrintWriter writer = new PrintWriter("test.txt");
			
			for(ArrayList<String> subList: dataList) {
				for(String protChain: subList) {
					writer.write(protChain + "\n");
				}
				writer.write("\n");
			}
			
			writer.close();
		} catch (FileNotFoundException e) {
			qp("Error in writing clusters");
			e.printStackTrace();
		}
		
	}
}
