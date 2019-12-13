package bio;

/**
 * 
 * @author Benjy Strauss
 *
 */

public abstract class AminoChain extends BioObject {
	private static final long serialVersionUID = 1L;
	
	public static final char RESIDUE_DOES_NOT_EXIST = '_';
	
	protected static final char MISSING_RESIDUE = '!';
	protected static final char MISSING_SECONDARY = '?';
	
	protected static final int FASTA_LINE_LENGTH = 80;
	//the RCSB-ID of the protein
	private String proteinName;
	//the char ID of the chain in the protein
	private char chainID;
	//where did the sequence come from?
	private DataSource source;
	
	protected boolean missingDSSP = false;
	
	boolean flexInit = false;
	//set to true if the vkabat data has been assigned
	public boolean isunstruct_init = false;
	//set to true if the vkabat data has been assigned
	public boolean vkabat_init = false;
	//set to true if the entropy data has been assigned
	public boolean entropy_init = false;
	
	public String description;
	//the UniprotKB id of the protein
	private String uniprotID;
	
	/**
	 * Constructs a new AminoChain object
	 * @param proteinName
	 * @param chainID
	 */
	protected AminoChain(String proteinName, char chainID) {
		this(proteinName, chainID, DataSource.OTHER);
	}
	
	/**
	 * Constructs a new AminoChain object
	 * @param proteinName
	 * @param chainID
	 * @param source
	 */
	protected AminoChain(String proteinName, char chainID, DataSource source) {
		this.proteinName = proteinName;
		this.chainID = chainID;
		this.source = source;
	}
	
	/** @return the source of the sequence */
	public final DataSource getSource() { return source; }
	/** @return UniprotKB ID of the chain*/
	public final String uniprotID() { return uniprotID; }
	public void setUniprotID(String uniprotID) { this.uniprotID = uniprotID; }

	/**
	 * Returns the chains character ID
	 * @return: Which chain of the Protein: a, b, c
	 */
	public char getID() { return chainID; }
	
	public void setProteinName(String name) { proteinName = name.toUpperCase(); }
	public String getProteinName() { return proteinName; }
	
	public abstract int length();
	public abstract Tablizable getAmino(int index);
	
	/** @return the index that the chain starts at */
	public int firstIndex() { return 0; }
	/** @return protein's name + chain ID */
	public String fullID() { return getProteinName() + chainID; }
	
	@Override
	public String impliedFileName() {
		String retVal = proteinName+chainID;
		return retVal.toLowerCase();
	}
}
