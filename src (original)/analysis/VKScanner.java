package analysis;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import util.BaseTools;

/**
 * 
 * @author Benjy Strauss
 *
 */

public class VKScanner extends BaseTools {
	
	private static final String VK_PATH = "scripts/secstructprediction/secpred";
	
	public static final String[] VKABAT_SOURCES = { "GOR,", "DPM", "GOR3", "PHD", "PREDATOR",
			"HNN", "MLRC", "SOPM", "JPRED", "PSIPRED", "JNET", "YASPIN", "SSPRO", "PROF", "DSC"};
	
	public static void main(String[] args) throws Exception {
		File vkFolder = new File(VK_PATH);
		StringBuilder reportBuilder = new StringBuilder();
		reportBuilder.append("VKabat Completion Report:\n\n");
		String fileList[] = vkFolder.list();
		
		BufferedReader reader;
		
		for(String filename: fileList) {
			if(!filename.startsWith(".")) {
				reader = new BufferedReader(new FileReader(VK_PATH + "/" + filename));
				String csvHeader = reader.readLine();
				String analysis = analyseLine(csvHeader);
				if(analysis.length() != 0) {
					reportBuilder.append(filename.substring(0,5) + ": " + analysis);
				}
				reader.close();
			}
		}
		
		qp(reportBuilder.toString());
	}

	private static String analyseLine(String csvHeader) {
		StringBuilder analysisBuilder = new StringBuilder();
		csvHeader = csvHeader.toUpperCase();
		
		for(String src: VKABAT_SOURCES) {
			if(!csvHeader.contains(src)) {
				analysisBuilder.append(src.replaceAll(",", "") + " ");
			}
		}
		
		if(analysisBuilder.length() != 0) {
			analysisBuilder.append("\n");
		}
		
		return analysisBuilder.toString();
	}
}
