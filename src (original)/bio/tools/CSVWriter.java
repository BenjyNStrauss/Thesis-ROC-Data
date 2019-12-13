package bio.tools;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import bio.AlignedCluster;
import bio.AminoAcid;
import bio.AminoChain;
import bio.ChainCluster;
import bio.Protein;
import bio.ProteinChain;
import bio.SwitchType;
import bio.Tablizable;
import bio.TripletChain;
import util.BaseTools;

/**
 * 
 * @author Benjy Strauss
 *
 */

public final class CSVWriter extends BaseTools {
	protected static final int NITROGEN = 0;
	protected static final int H_NITROGEN = 1;
	protected static final int C_ALPHA = 2;
	protected static final int C_BETA = 3;
	protected static final int C_PRIME = 4;
	protected static final int OXYGEN = 5;
	
	public static final String E6 = "E6";
	public static final String E20 = "E20";
	public static final String VK = "VK";
	public static final String ISU = "ISU";
	
	private static final String OUTPUT = "output/";
	private static final String CSV = ".csv";
	private static final String LEARNING_SET_HEADER = "No.,Chain,Residue,Letter,Secondary,E6,E20,isUnstruct,Vkabat,VkabatCompletion,amber95,N,HN,CA,CB,CP,O,AvgCharge,isSwitch";
	
	//write mode parameters
	private static final String WRITE_TRIPLET[]			= { "-t", "-3" };

	private static final String WRITE_MODE_ASSIGNED		= "-a" ;
	private static final String WRITE_MODE_UNASSIGNED		= "-u" ;
	private static final String WRITE_MODE_BOTH			= "-b" ;
	private static final String WRITE_MODE_WEIGHTED		= "-w" ;
	private static final String WRITE_MODE_ALL			= "-a" ;
	
	private static final String FLIP_E6					= "-e6" ;
	private static final String FLIP_E20					= "-e20" ;
	private static final String FLIP_E22					= "-e22" ;
	private static final String FLIP_ISUNSTRUCT			= "-isu" ;
	
	private static final String NORMALIZE				= "-n" ;
	private static final String NO_SIMPLIFY				= "-ss" ;
	
	/**
	 * Writes the ProteinChains in the list to a file
	 * @param args
	 * @param saveFileName
	 * @param myChains
	 */
	public static void writeData(String args[], String saveFileName, ArrayList<ProteinChain> myChains) {
		for(int i = 1; i < args.length; ++i) {
			args[i] = args[i].toLowerCase();
			if(!args[i].startsWith(ARG_PREFIX)) { args[i] = ARG_PREFIX + args[i]; }
		}
		
		boolean triplet = stringArrayContainsPartial(args, WRITE_TRIPLET);
		
		boolean writeAll = stringArrayContainsPartial(args, WRITE_MODE_ALL);
		boolean writeBoth = stringArrayContainsPartial(args, WRITE_MODE_BOTH);
		boolean writeUnassigned = stringArrayContainsPartial(args, WRITE_MODE_UNASSIGNED);
		boolean writeAssigned = stringArrayContainsPartial(args, WRITE_MODE_ASSIGNED);
		boolean writeWeighted = stringArrayContainsPartial(args, WRITE_MODE_WEIGHTED);
		
		if(!writeAll && !writeBoth && !writeUnassigned && !writeAssigned && !writeWeighted) {
			writeBoth = true;
		}
		
		if(writeAll) {
			writeBoth = true;
			writeUnassigned = true;
			writeAssigned = true;
		}
		
		boolean simplifySecondary = !stringArrayContainsPartial(args, NO_SIMPLIFY);
		boolean flip_e6 = stringArrayContainsPartial(args, FLIP_E6);
		boolean flip_e20 = stringArrayContainsPartial(args, FLIP_E20);
		boolean flip_e22 = stringArrayContainsPartial(args, FLIP_E22);
		boolean flip_isu = stringArrayContainsPartial(args, FLIP_ISUNSTRUCT);
		boolean norm = stringArrayContainsPartial(args, NORMALIZE);
		
		String outFileName = "output/"+saveFileName;
		
		if(triplet) {
			outFileName += "-t";
		} else {
			outFileName += "-s";
		}
		
		AminoChain dataSetArray[] = null;
		
		if(triplet) {
			dataSetArray = new TripletChain[myChains.size()];
			
			for(int i = 0; i < myChains.size(); ++i) {
				dataSetArray[i] = myChains.get(i).generateTripletChain();
			}
		} else {
			dataSetArray = new ProteinChain[myChains.size()];
			myChains.toArray(dataSetArray);
		}
		
		if(writeBoth) {	writeDataSet(outFileName, dataSetArray, 'b', flip_e6, flip_e20, flip_e22, flip_isu, norm, simplifySecondary, false); }
		if(writeUnassigned) { writeDataSet(outFileName, dataSetArray, 'u', flip_e6, flip_e20, flip_e22, flip_isu, norm, simplifySecondary, false); }
		if(writeAssigned) { writeDataSet(outFileName, dataSetArray, 'a', flip_e6, flip_e20, flip_e22, flip_isu, norm, simplifySecondary, false); }
		if(writeWeighted) { writeDataSet(outFileName, dataSetArray, 'w', flip_e6, flip_e20, flip_e22, flip_isu, norm, simplifySecondary, false); }
	}
	
	/**
	 * TODO
	 * @param filename
	 * @param chains
	 * @param writeMode
	 * @param flip_e6
	 * @param flip_e20
	 * @param flip_e22
	 * @param flip_isu
	 * @param normalize
	 */
	private static void writeDataSet(String filename, AminoChain[] chains, char writeMode, boolean flip_e6, boolean flip_e20,
			boolean flip_e22, boolean flip_isu, boolean normalize, boolean simplifySecondary, boolean enableE22) {
		filename += writeMode;
		
		if(flip_e6)  { qp("Flipping E6");			filename += "-fe6"; }
		if(flip_e20) { qp("Flipping E20");			filename += "-fe20"; }
		if(flip_e22) { qp("Flipping E22");			filename += "-fe22"; }
		if(flip_isu) { qp("Flipping isUnstruct");	filename += "-fISU"; }
		if(normalize)		 { qp("Normalizing");	filename += "-n"; }
		
		if(!filename.endsWith(CSV)) { filename += CSV; }
		
		PrintWriter csvWriter = null;
		StringBuilder dataBuilder = new StringBuilder();
		
		try {
			csvWriter = new PrintWriter(filename, "UTF-8");
			csvWriter.println(LEARNING_SET_HEADER);
			
			//"Protein,Chain,Residue,E20,E6,isUnstruct,Vkabat,VkabatCompletion,N,HN,CA,CB,CP,O,AVG,isSwitch";
			for(AminoChain chain: chains) {
				for(int index = 0; index < chain.length(); ++index) {
					Tablizable aa = chain.getAmino(index);
					
					if(aa == null) { continue; }
					if(aa.isUnwritable()) { continue; }
					
					//if(ignoreUnassignedVK && aa.vKabatCompletion() == -1) { continue; }
					
					dataBuilder.setLength(0);
					dataBuilder.append((index+chain.firstIndex()+1) + "," + chain.getProteinName() + chain.getID() + ",");
					
					dataBuilder.append(aa.toCode() + ",");
					dataBuilder.append(aa.toChar() + ",");
					
					if(simplifySecondary && aa.secondary() != null) {
						dataBuilder.append(aa.secondary().simpleClassify() + ",");
					} else {
						dataBuilder.append(aa.secondary() + ",");
					}
					
					dataBuilder.append(Tablizable.processEntropy(aa.E6(), 6, flip_e6, normalize));
					dataBuilder.append(Tablizable.processEntropy(aa.E20(), 20, flip_e20, normalize));
					
					if(enableE22) {
						dataBuilder.append(Tablizable.processEntropy(aa.E22(), 22, flip_e22, normalize));
					}
					
					//Flip isUnstruct?
					//isUnstruct is always normalized
					if(flip_isu) {
						dataBuilder.append((1 - aa.isUnstruct()) + ",");
					} else {
						dataBuilder.append(aa.isUnstruct() + ",");
					}
					
					dataBuilder.append(aa.vKabat() + ",");
					dataBuilder.append(aa.vKabatCompletion() + ",");
					dataBuilder.append(aa.amber95() + ",");
					
					dataBuilder.append(aa.getStandardCharges());
					
					dataBuilder.append(aa.averageCharge() + ",");
					
					switch(writeMode) {
					case 'b':		dataBuilder.append(aa.isSwitch() ? 1 : 0);							break;
					case 'a':		dataBuilder.append(aa.switchType() == SwitchType.ASSINGED ? 1 : 0);	break;
					case 'u':		dataBuilder.append(aa.switchType() == SwitchType.UNASSIGNED ? 1 : 0);	break;
					case 'w':
						if(aa.switchType() == SwitchType.UNASSIGNED) {
							dataBuilder.append(Tablizable.DISORDERED_WEIGHT);
						} else if(aa.switchType() == SwitchType.ASSINGED) {
							dataBuilder.append(1);
						} else {
							dataBuilder.append(0);
						} break;
					}
					
					csvWriter.println(dataBuilder.toString());
				}
			}
			
			csvWriter.close();
		} catch (FileNotFoundException e) {
			qp("File Could Not Be Written");
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Writes a .csv file
	 * @param fileName
	 * @param lines
	 */
	public static void writeCSV(String fileName, String[] lines) {
		if(!fileName.endsWith(CSV)) { fileName += CSV; }
		
		fileName = "output/" + fileName;
		
		PrintWriter csvWriter = null;
		
		try {
			csvWriter = new PrintWriter(fileName, "UTF-8");
			csvWriter.println(Protein.csvHeader());
			
			for(String line: lines) {
				csvWriter.println(line);
			}
			
			csvWriter.close();
		} catch (FileNotFoundException e) {
			qp("File Could Not Be Written");
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
	

	
	/**
	 * Appends values to be written as a record in a CSV
	 * @param base: the base to append to
	 * @param vals: the values to append
	 * @return base with appended values
	 */
	@SuppressWarnings("unused")
	private static String csvAppend(String base, Object... vals) {
		for(Object obj: vals) {
			base += "," + obj;
		}
		
		return base;
	}
	
	/**
	 * Should write a CSV file of just chain secondary structures, along with the indexes and residue types
	 * 
	 * @param fileName: name of the output file
	 * @param cluster: cluster to write
	 */
	public static void writeSecondaryStructureComparisonCSV(String fileName, AlignedCluster cluster) {
		if(!fileName.startsWith(OUTPUT)) { fileName = OUTPUT + fileName; }
		if(!fileName.endsWith(CSV)) { fileName += CSV; }
		
		PrintWriter csvWriter = null;
		
		try {
			csvWriter = new PrintWriter(fileName, "UTF-8");
			csvWriter.println(cluster.toSecStructCSV());
			csvWriter.close();
		} catch (FileNotFoundException e) {
			qp("File Could Not Be Written");
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Writes a CSV that allows the comparison of residues and secondary structures
	 * @param fileName: name of the csv file
	 * @param chains: all protein chains to write to the csv
	 */
	public static void writeSwitchRecordCSV(String fileName, ProteinChain... chains) {
		if(!fileName.endsWith(CSV)) { fileName += CSV; }
		int longestChain = 0;
		String[] lines = null;
		
		StringBuilder headerBuilder = new StringBuilder();
		ArrayList<String> lineList = new ArrayList<String>();
		PrintWriter csvWriter = null;
		
		//index, residue, secondary
		
		//prep everything...
		for(ProteinChain p: chains) {
			headerBuilder.append(p.description + p.getID() + "-Index," );
			headerBuilder.append(p.description + p.getID() + "-Residue," );
			headerBuilder.append(p.description + p.getID() + "-Secondary," );
			headerBuilder.append(p.description + p.getID() + "-Is_Switch," );
			
			if(p != null) {
				if(longestChain < p.length()) { longestChain = p.length(); }
			}
		}
		
		//qp("[" + fileName + "] starts at index: " + firstIndex);
		
		//add the header
		
		
		StringBuilder lineBuilder = new StringBuilder();
		for(int index = 0; index < longestChain; ++index) {
			
			for(ProteinChain chain: chains) {
				if(chain == null) {
					lineBuilder.append("missing,missing,missing,");
					continue;
				}
				AminoAcid printMe = chain.getAmino(index);
				lineBuilder.append(index + ",");
				if(printMe == null) {
					lineBuilder.append("missing,missing,missing,");
				} else {
					lineBuilder.append(printMe.residueType().toChar() + ",");
					lineBuilder.append(printMe.secondary() + ",");
					lineBuilder.append(printMe.isSwitch() + ",");
				}
			}
			lineList.add(lineBuilder.toString());
			lineBuilder.setLength(0);
		}
		
		lines = new String[lineList.size()];
		lineList.toArray(lines);
		
		try {
			csvWriter = new PrintWriter("output/" + fileName, "UTF-8");
			
			for(String line: lines) {
				csvWriter.println(line);
			}
			
			csvWriter.close();
		} catch (FileNotFoundException e) {
			qp("File Could Not Be Written");
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * TODO: unfinished
	 * @param fileName
	 * @param clusters
	 */
	public static void writeSwitchRecordCSV(String fileName, ChainCluster... clusters) {
		if(!fileName.endsWith(CSV)) { fileName += CSV; }
		int longestChain = 0;
		String[] lines = null;
		
		StringBuilder headerBuilder = new StringBuilder();
		ArrayList<String> lineList = new ArrayList<String>();
		PrintWriter csvWriter = null;
		
		ProteinChain[][] allChains = new ProteinChain[clusters.length][];
		
		for(int index = 0; index < clusters.length; ++index) {
			
			ProteinChain temp[] = clusters[index].getAllChains();
			
			
			allChains[index] = temp;
			
			for(ProteinChain p: allChains[index]) {
				headerBuilder.append(p.description + p.getID() + "-Index," );
				headerBuilder.append(p.description + p.getID() + "-Residue," );
				headerBuilder.append(p.description + p.getID() + "-Secondary," );
				headerBuilder.append(p.description + p.getID() + "-Is_Switch," );
				
				if(p != null) { 
					if(longestChain < p.length()) { longestChain = p.length(); }
				}
			}
			headerBuilder.append("LEFT_BLANK," );
		}
		
		//add the header
		lineList.add(headerBuilder.toString());
		
		StringBuilder lineBuilder = new StringBuilder();
		for(int index = 0; index < longestChain; ++index) {
			for(ProteinChain[] chains: allChains) {
				for(ProteinChain chain: chains) {
					if(chain == null) {
						lineBuilder.append("missing,missing,missing,");
						continue;
					}
					
					AminoAcid printMe = chain.getAmino(index);
					lineBuilder.append(index + ",");
					if(printMe == null) {
						lineBuilder.append("missing,missing,missing,");
					} else {
						lineBuilder.append(printMe.residueType().toChar() + ",");
						lineBuilder.append(printMe.secondary() + ",");
						lineBuilder.append(printMe.isSwitch() + ",");
					}
				}
				lineBuilder.append(",");
			}
			lineList.add(lineBuilder.toString());
			lineBuilder.setLength(0);
		}
		
		lines = new String[lineList.size()];
		lineList.toArray(lines);
		
		try {
			csvWriter = new PrintWriter("output/" + fileName, "UTF-8");
			
			for(String line: lines) {
				csvWriter.println(line);
			}
			
			csvWriter.close();
		} catch (FileNotFoundException e) {
			qp("File Could Not Be Written");
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
}
