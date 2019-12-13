package bio.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import bio.AminoAcid;
import bio.ChainBuilder;
import bio.ProteinChain;
import util.BaseTools;

/**
 * Specialized BufferedReader for reading Vkabat CSVs
 * @author Benjy Strauss
 *
 */

public class VkabatReader extends BufferedReader {
	public static final String SECPRED_PATH = "scripts/secstructprediction/secpred/";
	
	private File targetFile;
	
	/**
	 * Create a new VkabatReader 
	 * @param path: file to read
	 * @throws FileNotFoundException: if file cannot be found
	 */
	public VkabatReader(String path) throws FileNotFoundException {
		super(new FileReader(path));
		targetFile = new File(path);
	}
	
	/**
	 * Create a new VkabatReader
	 * @param protein: name of the protein
	 * @param chain: chain of the protein
	 * @throws FileNotFoundException: if file cannot be found
	 */
	public VkabatReader(String protein, char chain) throws FileNotFoundException {
		super(new FileReader(generatePath(protein, chain)));
		targetFile = new File(generatePath(protein, chain));
	}
	
	/**
	 * 
	 * @return
	 * @throws IOException 
	 */
	public ProteinChain getVkabat(String protein, char chain) throws IOException {
		String input;
		ChainBuilder builder = new ChainBuilder();
		
		for(input = readLine(); ready(); ) {
			input = readLine();
			//clears out the first 3 fields
			input = input.substring(input.indexOf(",")+1);
			input = input.substring(input.indexOf(",")+1);
			char resType = input.charAt(0);
			input = input.substring(input.indexOf(",")+1);
			input = input.replaceAll(",", "");
			
			AminoAcid aa = new AminoAcid(resType);
			aa.setVkabat(calcVkabat(input));
			aa.setVkabatCompletion(input.length());
			builder.append(aa);
		}
		
		return builder.toChain(protein, chain, BioLookup.fastaType());
	}
	
	/**
	 * Calculate the Vkabat value for a single amino acid residue from an input string
	 * @param input
	 * @return
	 */
	public static double calcVkabat(String input) {
		input = input.toUpperCase();
		int H = 0, E = 0, O = 0;
		
		for(int index = 0; index < input.length(); ++index) {
			switch(input.charAt(index)) {
			case 'H':	++H;		break;
			case 'E':	++E;		break;
			default:		++O;
			}
		}
		
		double k = 0; //max = 3
		
		if(H > 0) { ++k; }
		if(E > 0) { ++k; }
		if(O > 0) { ++k; }
				
		int n1 = BaseTools.max(H, BaseTools.max(E, O)); // min = 5
		int N = input.length(); //max = 15
		
		double result = ( k / n1) * N; //max = 3/5*15 = 9
		
		return result;
	}

	/**
	 * 
	 * @param protein
	 * @param chain
	 * @return
	 * @throws FileNotFoundException
	 */
	protected static final String generatePath(String protein, char chain) {
		String filePath = protein + chain;
		filePath = filePath.toUpperCase();
		filePath = SECPRED_PATH + filePath + ".csv";
		
		return filePath;
	}
	
	/**
	 * Deletes the file that this class read frm
	 */
	public void cleanUp() {
		targetFile.delete();
	}
	
	/**
	 * 'qp' stands for quick-print
	 * mainly for use in debugging
	 * @param arg0: the object to print
	 */
	protected static void qp(Object arg0) {
		if(arg0 != null && arg0.getClass().isArray()) {
			Object[] i_arg0 = (Object[]) arg0;
			for(Object o: i_arg0) {
				System.out.println(o);
			}
		} else if(arg0 instanceof List) {
			List<?> l_arg0 = (List<?>) arg0;
			for(Object o: l_arg0) {
				System.out.println(o);
			}
		} else {
			System.out.println(arg0);
		}
	}
}
