package bio;

import bio.adapters.PDBFlexInterface;
import bio.exceptions.DataRetrievalException;

/**
 * A protein is one or more chains of amino acid residues
 * 
 * @author Benjy Strauss
 *
 */

public class Protein extends BioObject {
	private static final long serialVersionUID = 1L;
	//whether or not E22 is printed
	static boolean enableE22 = false;

	private DataSource source;
	
	//protein name
	protected String name;
	//the residue chains
	protected ProteinChain chains[];
	
	public Protein(String name, ProteinChain... chains) {
		this(name, DataSource.OTHER, chains);
	}
	
	public Protein(String name, DataSource source, ProteinChain... chains) {
		this.name = name;
		this.chains = chains;
		this.source = source;
	}
	
	public Protein(String name, DataSource source, String... chains) {
		this.name = name;
		this.chains = new ProteinChain[chains.length];
		this.source = source;
		
		char chainID = 'A';
		for(int index = 0; index < chains.length; ++index) {
			this.chains[index] = new ProteinChain(name, chainID, chains[index], source);

			chainID++;
		}
	}
	
	public DataSource getSource() { return source; }
	
	public ProteinChain[] getAllChains() { return chains; }
	
	/**
	 * Enables E22 Term in CSV format
	 */
	public static void enableE22() { enableE22 = true; }
	
	/**
	 * Disables E22 Term in CSV format
	 */
	public static void disableE22() { enableE22 = false; }
	
	/**
	 * Returns whether E22 Term is present in CSV format
	 * @return
	 */
	public static boolean e22() { return enableE22; }
	
	/**
	 * Gets the header for a CSV file
	 * @return
	 */
	public static String csvHeader() {
		if(enableE22) {
			return "PROTEIN_NAME,CHAIN_ID,CHAIN_INDEX,RESIDUE_TYPE,SECONDARY_STRUCTURE,E6,E20,E22,V_KABAT,LOBANOV_GALZISKAYA,N,HN,C_ALPHA,C_BETA,C_PRIME,O,AVG_CHARGE";
		} else {
			return "PROTEIN_NAME,CHAIN_ID,CHAIN_INDEX,RESIDUE_TYPE,SECONDARY_STRUCTURE,E6,E20,V_KABAT,LOBANOV_GALZISKAYA,N,HN,C_ALPHA,C_BETA,C_PRIME,O,AVG_CHARGE";
		}
	}
	
	/**
	 * Gets the chain determined by the character, or null if no such chain exists.
	 * @param chainID
	 * @return
	 */
	public ProteinChain getChain(char chainID) {
		for(int i = 0; i < chains.length; ++i) {
			if(chains[i].getID() == chainID) { return chains[i]; }
		}
		return null;
	}
	
	/**
	 * Looks up Flexibilities for the protein chain's residues in PDBFlex
	 * @throws DataRetrievalException: if there is a problem getting the data from PDBFlex
	 */
	public void lookUpFlexibilities() throws DataRetrievalException {
		
		for(ProteinChain chain: chains) {
			chain.flexInit = true;
			String queryData = PDBFlexInterface.getRMSDData(this)[0];
			String values = queryData.substring(queryData.indexOf("[")+1, queryData.indexOf("]"));
			
			if(values.equals("")) {
				for(int index = 0; index < chain.length(); ++index) {
					chain.setFlexibility(index, Double.NaN);
				}
				throw new DataRetrievalException("Flexibility data could not be found.");
			}
			
			values += ",";
			
			for(int index = 0; index < chain.length(); ++index) {
				String thisDouble = values.substring(0, values.indexOf(","));
				values = values.substring(values.indexOf(",")+1);
				
				try {
					chain.setFlexibility(index, Double.parseDouble(thisDouble));
				} catch (NumberFormatException NFE) {
					chain.flexInit = false;
					throw new DataRetrievalException("Data Format Unrecognized");
				}
			}
		}
	}
	
	/**
	 * Return a deep copy of the protein object
	 */
	public Protein clone() {
		ProteinChain[] clonedChains = new ProteinChain[chains.length];
		
		for(int i = 0; i < chains.length; ++i) {
			clonedChains[i] = chains[i].clone();
		}
		
		Protein myClone = new Protein(name, clonedChains);
		return myClone;
	}
	
	/**
	 * 
	 * @return
	 */
	public String piscesChains() {
		String retVal = "";
		for(ProteinChain chain: chains) {
			retVal += name + ":" + chain.getID() + "\n";
		}
		
		return retVal.toLowerCase();
	}
	
	/**
	 * 
	 * @return
	 */
	public String toFasta() {
		StringBuilder fastaBuilder = new StringBuilder();
		boolean fullLine = false;
		
		for(ProteinChain chain: chains) {
			
			if(chain == null) {
				qp("null chain");
				continue;
			}
			
			fastaBuilder.append(">" + name + ":" + Character.toUpperCase(chain.getID()) + STATIC_FASTA_HEADER + "\n");
			
			for(int i = 0; i < chain.length(); ++i) {
				fullLine = false;
				
				if(chain.getAmino(i) == null) {
					fastaBuilder.append("-");
				} else {
					fastaBuilder.append(chain.getAmino(i).toChar());
				}
				
				if((i+1) % 80 == 0) { fastaBuilder.append("\n"); fullLine = true; }
			}
			if(!fullLine) { fastaBuilder.append("\n"); }
		}
		
		return fastaBuilder.toString();
	}
	

	@Override
	public String impliedFileName() { return name; }
	
	public String toString() { return name; }
}
