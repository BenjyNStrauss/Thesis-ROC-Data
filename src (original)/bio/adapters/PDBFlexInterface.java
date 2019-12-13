package bio.adapters;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import bio.*;

/**
 * 
 * @author Benjy Strauss
 *
 */

@SuppressWarnings("unused")
public final class PDBFlexInterface {
	private static final String pdbID = "pdbID";
	private static final String chainID = "chainID";
	
	/**
	 * Get the RMSD data of a specific protein chain
	 * @param protein
	 * @param chain
	 * @return
	 */
	public static String getChainRMSDData(String protein, char chain) {
		String urlString = "http://pdbflex.org/php/api/rmsdProfile.php?pdbID=" + protein + "&chainID=" + chain;
		String retVal = null;
		URL PDBFlex = null;
		URLConnection PDBConnection = null;
		BufferedReader in = null;
		
		//qp(urlString);
		
		try {
			PDBFlex = new URL(urlString);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		
		try {
			PDBConnection = PDBFlex.openConnection();
			in = new BufferedReader(new InputStreamReader(PDBConnection.getInputStream()));
			
			String inputLine;
			
			retVal = in.readLine();
			
		} catch (IOException e) {
			e.printStackTrace();
		}

		return retVal;
	}
	
	/**
	 * Get the RMSD data of a specific protein(s)
	 * @param chains
	 * @return
	 */
	public static String[] getRMSDData(Protein... proteins) {
		String[] retVal;
		ArrayList<String> lines = new ArrayList<String>();
		
		for(Protein protein: proteins) {
			ProteinChain chains[] = protein.getAllChains();
			for(ProteinChain chain: chains) {
				lines.add(getChainRMSDData(protein.toString(), chain.getID()));
			}
		}
		
        retVal = new String[lines.size()];
        lines.toArray(retVal);
		return retVal;
	}
	
	private static void qp(Object arg0) {
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
