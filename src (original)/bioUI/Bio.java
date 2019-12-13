package bioUI;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.NoSuchElementException;

import analysis.SwitchValidationModule;
import bio.AlignedCluster;
import bio.AminoAcid;
import bio.DataSource;
import bio.Protein;
import bio.ProteinChain;
import bio.exceptions.DataRetrievalException;
import bio.exceptions.DataValueOutOfRangeException;
import bio.exceptions.PythonException;
import bio.exceptions.ResidueAlignmentException;
import bio.exceptions.ResidueAlignmentRuntimeException;
import bio.tools.BioIOStream;
import bio.tools.BioLookup;
import bio.tools.CSVWriter;
import bio.tools.ClusterReader;
import bio.tools.FASTA;

/**
 * 
 * @author Benjy Strauss
 *
 */

public class Bio extends JBioMain {
	
	/**
	 * Set the source to download FASTA files from
	 * Currently GenBank, RCSB-PDB, Uniprot, and the on-disk DSSPs are the only supported sources
	 */
	static void setSourceUser(String[] args) {
		String line = null;
		if(args.length < 2) {
			qp("Enter FASTA source:");
			
			try {
				line = input.nextLine();
			} catch (NoSuchElementException NSEE) { }
		} else {
			line = args[1];
		}
		
		if(line.trim().length() == 0) { line = "o"; }
		char switchChar = Character.toUpperCase(line.charAt(0));
		
		BioLookup.setDataSource(DataSource.getDataSource(switchChar));
	}
	
	/**
	 * Displays the ProteinChain objects in "myChains" by printing them to the console
	 */
	static void display() {
		qp("List of Chains");
		for(int index = 0; index < myChains.size(); ++index) {
			qp(myChains.get(index));
		}
	}
	
	/**
	 * Set the name to save the data as
	 * @param args: if args[1] exists, it will be what the save file name is set to,
	 *  else the user will be prompted for input
	 */
	static void setSaveFileName(String[] args) {
		String line = null;
		
		if(args.length < 2) {
			qp("Enter File Name to Save As:");
			
			try {
				line = input.nextLine();
			} catch (NoSuchElementException NSEE) { }
		} else {
			line = args[1];
		}
		
		if(!line.contains("/")) {
			saveFileName = line;
			qpl("Proteins will now be saved as: \"files/saved/" + saveFileName + "\"");
		} else {
			qpl("Error: cannot save file as: \"" + line + "\"");
		}
	}
	
	/**
	 * Assigns charge to all of the chains in memory
	 */
	static void autoCharge() {
		for(ProteinChain chain: myChains) {
			chain.autoAssignCharges();
		}
	}
	
	/**
	 * Creates a .csv file that compares secondary structures
	 */
	static void makeSecStructCompCSV() {
		qp("Enter Path to Cluster File:");
		String line = input.nextLine();
		
		//read the clusters
		String[][] clusters = ClusterReader.readClusters(line);
		
		if(clusters == null) { return; }
		
		qpl("Downloading FASTAs");
		//download all the fastas
		for(int i = 0; i < clusters.length; ++i) {
			for(int j = 0; j < clusters[i].length; ++j) {
				try {
					FASTA.verifyUniprot(clusters[i][j].substring(0, 4), clusters[i][j].charAt(4));
				} catch (DataRetrievalException e) {
					qerr("Could not download FASTA for " + clusters[i][j].substring(0, 5));
				}
			}
		}
		
		qpl("Making Clusters");
		//Create the chain clusters
		AlignedCluster[] chainClusters = new AlignedCluster[clusters.length];
		
		for(int i = 0; i < clusters.length; ++i) {
			try {
				chainClusters[i] = new AlignedCluster(BioLookup.readChainFromFasta(clusters[i][0].substring(0, 4), clusters[i][0].charAt(4)));
			} catch (FileNotFoundException e1) {
				qpl("Could not read FASTA for chain: " + clusters[i][0]);
				continue;
			}
			
			try {
				BioLookup.assignSecondary(chainClusters[i].getDominant());
			} catch (ResidueAlignmentException e) {
				qpl("Secondary structure match failed on index: " + i);
			}
			
			for(int j = 1; j < clusters[i].length; ++j) {
				ProteinChain compareChain = null;
				try {
					compareChain = BioLookup.readChainFromFasta(clusters[i][j].substring(0, 4), clusters[i][0].charAt(4));
				} catch (FileNotFoundException e2) {
					qpl("Could not read FASTA for chain: " + clusters[i][j]);
					continue;
				}
					
				try {
					BioLookup.assignSecondary(compareChain);
				} catch (ResidueAlignmentException e) {
					qpl("Secondary structure match failed on index: " + i + "," + j + " (" + clusters[i][j] + ")");
				}
				
				chainClusters[i].addChain(compareChain);
			}
			
			CSVWriter.writeSecondaryStructureComparisonCSV(chainClusters[i].impliedFileName(), chainClusters[i]);
		}
	}
	
	/**
	 * Generates the backbone for files where computer-detected switches can be compared to manual ones
	 */
	static void compareDetect() {
		qp("Enter Path to Cluster File:");
		String line = input.nextLine();
		
		String[][] clusters = ClusterReader.readClusters(line);
		AlignedCluster[] chainClusters = null;
		
		if(clusters == null) { 
			qp("Trying to read file: " + line + ".txt");
			if(!line.endsWith(".txt")) { clusters = ClusterReader.readClusters(line + ".txt"); }
			if(clusters == null) { return; } else { qp("Cluster Read Success"); }
		}
		
		chainClusters = cluster(clusters);
		
		for(AlignedCluster cluster: chainClusters) {
			cluster.markDominant(1);
			try {
				SwitchValidationModule.printPreliminarySwitchLinup(cluster);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Creates a data set based on the filename passed in
	 * if no filename is given, the user will be asked to specify a file
	 */
	static void makeDataSet(String[] args) {
		String line;
		if(args.length < 1) {
			qp("Enter Path to Cluster File:");
			line = input.nextLine();
		} else {
			line = args[1];
		}
		
		//read the clusters
		String[][] clusters = ClusterReader.readClusters(line);
		AlignedCluster[] chainClusters = null;
		
		if(clusters == null) { 
			qp("Trying to read file: " + line + ".txt");
			if(!line.endsWith(".txt")) { clusters = ClusterReader.readClusters(line + ".txt"); }
			if(clusters == null) { return; } else { qp("Cluster Read Success"); }
		}
		
		chainClusters = cluster(clusters);
		
		qp("Assigning IsUnstruct");
		for(int index = 0; index < chainClusters.length; ++index) {
			ProteinChain chain = chainClusters[index].getDominant();
			assignDerived(chain);
			myChains.add(chain);
		}
		
		clusterList = chainClusters;
	}
	
	/**
	 * Turns a 2D array of strings into an array of AlignedCluster objects
	 * @param clusters a list of the names of protein chains to cluster
	 * 		this is either the RCSB-ID or the RCSB-ID and Uniprot ID together in a string
	 * @return an array of AlignedCluster objects, where each cluster is from an array of strings
	 */
	static AlignedCluster[] cluster(String[][] clusters) {
		qpl("Making Clusters");
		//Create the chain clusters
		AlignedCluster[] chainClusters = new AlignedCluster[clusters.length];
		
		for(int i = 0; i < clusters.length; ++i) {
			//make sure there is no colon in the string
			clusters[i][0] = clusters[i][0].replaceAll(":", "").toUpperCase();
			//extract the protein's name
			String protName = clusters[i][0].substring(0, 4);
			//extract the protein's chain
			char chainName = clusters[i][0].charAt(4);
			
			//if the length is 5 or it's NOT from uniprot which uses different ID numbers
			if(clusters[i][0].length() == 5 || (BioLookup.fastaType() != DataSource.UNIPROT)) {
				//do standard verify
				FASTA.verify(protName, chainName);
			} else {
				//if it's from uniprot, get the uniprotID and then verify
				String[] lineData = ClusterReader.parseCompositeLine(clusters[i][0]);
				try {
					FASTA.verifyUniprot(lineData[0], lineData[1].charAt(0), lineData[2]);
				} catch (DataRetrievalException e) {
					qpl("Could not verify Uniprot fasta for: " + protName + chainName);
					e.printStackTrace();
				}
			}
			
			//read the dominant chain from it's FASTA
			try {
				chainClusters[i] = new AlignedCluster(BioLookup.readChainFromFasta(clusters[i][0].substring(0, 4), clusters[i][0].charAt(4)));
			} catch (FileNotFoundException e1) {
				qpl("Clustering Error: could not read FASTA for chain: " + clusters[i][0]);
				continue;
			}
			
			//assign the secondary structure to the dominant chain
			try {
				BioLookup.assignSecondary(chainClusters[i].getDominant());
			} catch (ResidueAlignmentException e) {
				qpl("Clustering Error: Secondary structure match failed on index: " + i);
			}
			
			//assign charge data to the dominant chain
			chainClusters[i].getDominant().autoAssignCharges();
			
			//for the non-dominant chains in the cluster
			for(int j = 1; j < clusters[i].length; ++j) {
				//extract the protein's name
				protName = clusters[i][j].substring(0, 4);
				//extract the protein's chain
				chainName = clusters[i][j].charAt(4);
				ProteinChain compareChain = null;
				
				//if the length is 5 or it's NOT from uniprot which uses different ID numbers
				if(clusters[i][j].length() == 5 || (BioLookup.fastaType() != DataSource.UNIPROT)) {
					FASTA.verify(protName, chainName);
				} else {
					String[] lineData = ClusterReader.parseCompositeLine(clusters[i][j]);
					try {
						FASTA.verifyUniprot(lineData[0], lineData[1].charAt(0), lineData[2]);
					} catch (DataRetrievalException e) {
						qp("Could not verify Uniprot fasta for: " + protName + chainName);
						e.printStackTrace();
					}
				}
				
				//read all of the non-dominant chains from the fasta
				try {
					compareChain = BioLookup.readChainFromFasta(clusters[i][j].substring(0, 4), clusters[i][j].charAt(4));
				} catch (FileNotFoundException e2) {
					qpl("Clustering Error: Could not read FASTA for chain: " + clusters[i][j]);
					continue;
				}
				
				///assign secondary structures to all of the non-dominant chains
				try {
					qpl("Assigning secondary structures to non-dominant chain: " + clusters[i][j]);
					BioLookup.assignSecondary(compareChain);
				} catch (ResidueAlignmentException e) {
					qpl("Secondary structure match failed on index: " + i + "," + j + " (" + clusters[i][j] + ")");
					e.printStackTrace();
				}
				
				//add all of the non-dominant chains to the cluster
				try {
					chainClusters[i].addChain(compareChain);
				} catch (ResidueAlignmentRuntimeException RARE) {
					qpl("Need different UNIPROT ID for " + compareChain.fullID());
					qpl(RARE.getMessage());
				}
				
			}
			chainClusters[i].markDominant(1);
		}
		return chainClusters;
	}
	
	/**
	 * Assigns all values that do not require a download
	 * At this time this is just isUnstruct and charges
	 * @param chain
	 * @return
	 */
	static ProteinChain assignDerived(ProteinChain chain) {
		try {
			BioLookup.assignIsUnstruct(chain);
		} catch (ResidueAlignmentException e) {
			qpl("Error: could not assign isUnstruct data for: " + chain.getProteinName() + chain.getID());
		}
		
		//always collect garbage here, otherwise risk out-of-memory error
		for(int amino = 0; amino < chain.length(); ++amino) {
			AminoAcid aa = chain.getAmino(amino);
			
			if(aa != null) {
				if(aa.secondary() != null) {
					BioLookup.assignChargeValues(chain.getAmino(amino));
				}
			}
			
			if(amino % 256 == 0) { System.gc(); }
		}
		
		
		return chain;
	}
	
	/**
	 * Assigns entropy (E6 and E20) to all chains in "myChains"
	 */
	static void assignEntropy() {
		qpl("Assigning Entropy");
		for(int index = 0; index < myChains.size(); ++index) {
			ProteinChain chain = myChains.get(index);
			
			if(!chain.entropy_init) {
				try {
					BioLookup.assignEntropy(chain);
					BioIOStream.saveObject(myChains, saveFileName);
				} catch (ResidueAlignmentException e) {
					qpl("Error: could not assign data for: " + chain.getProteinName() + chain.getID());
				} catch (PythonException pe) {
					pe.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * Loads an object file of saved protein chains into memory
	 * @return: 
	 * @throws FileNotFoundException: if the file to read cannot be found
	 */
	@SuppressWarnings("unchecked")
	static ArrayList<ProteinChain> loadSaved() throws FileNotFoundException {
		return (ArrayList<ProteinChain>) BioIOStream.readObject(BioIOStream.SAVE_PATH + saveFileName);
	}
	
	/**
	 * Assigns vkabat to all chains in "myChains"
	 */
	static void assignVkabat() {
		qpl("Assigning Vkabat");
		for(int index = 0; index < myChains.size(); ++index) {
			ProteinChain chain = myChains.get(index);
			
			if(!chain.vkabat_init) {
				qpl("Assigning Vkabat for " + chain.fullID());
				try {
					BioLookup.assignVkabat(chain);
				} catch (ResidueAlignmentException e) {
					qpl("Error: could not assign data for: " + chain.fullID());
				} catch (DataValueOutOfRangeException e) {
					qpl("Error: could not assign data for: " + chain.fullID());
				}
				BioIOStream.saveObject(myChains, saveFileName);
			} else {
				qpl("Already Assigned for " + chain.fullID());
			}
			
		}
	}
	
	/**
	 * TODO (always todo)
	 */
	static void debug() {
		
	}
	
	/**
	 * 
	 */
	static void checkValues() {
		if(myChains == null) {
			myChains = new ArrayList<ProteinChain>();
		}
		
		for(ProteinChain pc : myChains) {
			AminoAcid tempRes = null;
			
			for(int i = 0; i < pc.length(); ++i) {
				tempRes = pc.getAmino(i);
				if(tempRes != null) { break; }
			}
			
			qp(pc.fullID() + " (" + pc.getSource() + ") : " + pc.entropy_init + " : " + pc.vkabat_init + " (" + tempRes.vKabatCompletion() + ")");
		}
	}
	
	/**
	 * [not-used] [-src] [ids]
	 * 
	 * 
	 * TODO: FINISH
	 * @param params
	 *
	static void loadProteinChain(String[] params) {
		loadProteinChainDSSP(params);
	}*/
	
	/**
	 * Loads a ProteinChain(s) from a fasta, aligned with secondary structures from DSSPs
	 * @param params
	 */
	static void loadProteinChain(String[] params) {
		if(params.length < 2) {
			qp("Error: no protein chains to load");
		}
		
		for(int i = 1; i < params.length; ++i) {
			
			//if given the name of a protein
			if(params[i].length() == 4) {
				Protein meta = BioLookup.readProteinDSSP(params[i]);
				//qp(meta);
				
				if(meta == null) {
					qp("Could not find: " + params[i]);
				} else {
					ProteinChain chains[] = meta.getAllChains();
					//qp(chains);
					
					String chainNames[] = new String[chains.length+1];
					//this is just for debug purposes
					chainNames[0] = "load-internal";
					
					for (int j = 0; j < chains.length; ++j) {
						//qp(chains[j]);
						chainNames[j+1] = chains[j].getProteinName() + chains[j].getID();
					}
					
					//qp(chainNames);
					
					loadProteinChain(chainNames);
					
					qpl("Added all " + chains.length + " chains of: " + params[i]);
				}
				
			//given the name of an individual chain
			} else if(params[i].length() == 5) {
				params[i] = params[i].toUpperCase();
				FASTA.verify(params[i].substring(0, 4), params[i].charAt(4));
				
				ProteinChain temp = null;
				
				try {
					temp = BioLookup.readChainFromFasta(params[i].substring(0, 4), params[i].charAt(4));
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
				
				try {
					temp = BioLookup.assignSecondary(temp);
				} catch (ResidueAlignmentException e) {
					e.printStackTrace();
				}
				
				if(temp != null) {
					myChains.add(temp);
				} else {
					qpl("Error: Unable to find chain: " + params[i]);
				}
				
			} else {
				qpl("Error: " + params[i] + " is not a valid protein chain");
			}
		}
	}
	
	/**
	 * Loads a Protein from the DSSPs
	 * @param params
	 */
	static void loadProtein(String[] params) {
		if(params.length < 2) {
			qp("Error: no protein chains to load");
		}
		
		for(int i = 1; i < params.length; ++i) {
			if(params[i].length() != 4) {
				qpl("Error: " + params[i] + " is not a valid protein");
			} else {
				Protein test = BioLookup.readProteinDSSP(params[i]);
				if(test != null) {
					myProteins.add(test);
					ProteinChain chains[] = test.getAllChains();
					for(ProteinChain chain: chains) { myChains.add(chain);}
					
					qpl("Added: " + params[i]);
				} else {
					qpl("Error: Unable to find protein: " + params[i]);
				}
			}
			
		}
	}
	
	/**
	 * 
	 * @param args
	 */
	static void generateSwitchList(String[] args) {
		String line;
		
		if(args.length < 2) {
			qp("Enter Path to Cluster File:");
			line = input.nextLine();
		} else { 
			line = args[1];
		}
		
		//read the clusters
		String[][] clusters = ClusterReader.readClusters(line);
		
		qp("read clusters");
		
		AlignedCluster[] chainClusters = new AlignedCluster[clusters.length];
		
		for(int i = 0; i < clusters.length; ++i) {
			String ref = clusters[i][0];
			chainClusters[i] = new AlignedCluster(BioLookup.readProteinChainDSSP(ref.substring(0, 4), ref.charAt(4)));
			
			for(int j = 1; j < clusters[i].length; ++j) {
				ProteinChain compareChain = null;
				compareChain = BioLookup.readProteinChainDSSP(clusters[i][j].substring(0, 4), clusters[i][j].charAt(4));
				chainClusters[i].addAndMarkChain(compareChain);
			}
			
			chainClusters[i].markDominant();	
		}
		
		qp("made cluster objects");
		CSVWriter.writeSwitchRecordCSV("all-switches.csv", chainClusters);
	}
	
	/**
	 * Run isUnstruct on all chains that need it...
	 */
	static void isUnstruct() {
		for(ProteinChain chain: myChains) {
			try {
				if(!chain.isunstruct_init) {
					BioLookup.assignIsUnstruct(chain);
					chain.isunstruct_init = true;
				}
			} catch (ResidueAlignmentException e) {
				e.printStackTrace();
			}
		}
	}
}
