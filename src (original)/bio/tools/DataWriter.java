package bio.tools;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import bio.AminoAcid;
import bio.ProteinChain;
import util.BaseTools;

/**
 * 
 * @author Benjy Strauss
 *
 */

public class DataWriter extends BaseTools {
	private static final int LEARN_OFFSET = 22;
	@SuppressWarnings("unused")
	private static final int TEST_OFFSET = 22;
	private static final int LINE_LENGTH = 60;
	private static final int VERBATIM_LATEX_LINES_PER_PAGE = 42;
	private static final String PAGE_BREAK = "\\end{verbatim} \\pagebreak \\begin{verbatim}";
	private static final String[] LINE_HEADERS = {
			"1st   : ",
			"2nd(d): ",
			"2nd(s): ",
			"Switch: "
	};
	
	/**
	 * Writes a summary of the protein chains to a text file
	 * @param chains: the chains to write to the file
	 * @param filename: the name of the file to write to
	 * @throws FileNotFoundException 
	 */
	public static void writeChainSummary(List<ProteinChain> chains, String filename, boolean latexMode) {
		ProteinChain[] chainArray = new ProteinChain[chains.size()];
		chains.toArray(chainArray);
		writeChainSummary(chainArray, filename, latexMode);
	}
	
	/**
	 * Writes a summary of the protein chains to a text file
	 * @param chains: the chains to write to the file
	 * @param filename: the name of the file to write to
	 * @throws FileNotFoundException 
	 */
	public static void writeChainSummary(ProteinChain[] chains, String filename, boolean latexMode) {
		ArrayList<String> fileLines = new ArrayList<String>();
		if(latexMode) { 
			fileLines.add("\n\\begin{verbatim}");
		} else {
			fileLines.add("Summary of cluster: " + filename);
			fileLines.add("* = secondary structure is unassigned");
			fileLines.add("1 = known switch (assigned), ? = possible switch (unassigned), 0 = no switch");
			fileLines.add("");
		}
		
		StringBuilder primarySequenceBuilder = new StringBuilder();
		primarySequenceBuilder.append(LINE_HEADERS[0]);
		
		StringBuilder secondarySequenceBuilder = new StringBuilder();
		secondarySequenceBuilder.append(LINE_HEADERS[1]);
		
		StringBuilder secondarySimpleBuilder = new StringBuilder();
		secondarySimpleBuilder.append(LINE_HEADERS[2]);
		
		StringBuilder switchBuilder = new StringBuilder();
		switchBuilder.append(LINE_HEADERS[3]);
		
		boolean fullLineFlag = false;
		
		for(ProteinChain chain: chains) {
			
			if(latexMode) {
				int roomOnPage = VERBATIM_LATEX_LINES_PER_PAGE - (fileLines.size() % VERBATIM_LATEX_LINES_PER_PAGE);
				if(roomOnPage < 7) {
					fileLines.add(PAGE_BREAK);
				}
			}
			
			fileLines.add(">" + chain.fullID());
			fileLines.add("");
			
			for(int index = chain.firstIndex(); index < chain.lastIndex(); ) {
				
				if(latexMode) {
					int roomOnPage = VERBATIM_LATEX_LINES_PER_PAGE - ((fileLines.size()-LEARN_OFFSET) % VERBATIM_LATEX_LINES_PER_PAGE);
					if(roomOnPage < 5) {
						fileLines.add(PAGE_BREAK);
					}
				}
				
				fileLines.add("[" + index + "]");
				
				while((primarySequenceBuilder.length() < (LINE_LENGTH + LINE_HEADERS[0].length())) && (index < chain.lastIndex())) {
					fullLineFlag = false;
					AminoAcid aa = chain.getAminoAt(index);
					if(aa != null) {
						primarySequenceBuilder.append(aa.residueType().toChar());
						if(aa.secondary() != null) {
							secondarySequenceBuilder.append(aa.secondary().toLetter());
							secondarySimpleBuilder.append(aa.secondary().simpleClassify().toChar());
						} else {
							secondarySequenceBuilder.append('!');
							secondarySimpleBuilder.append('!');
						}
						
						switch(aa.switchType()) {
						case ASSINGED:			switchBuilder.append('1');	break;
						case MISSING_RESIDUE:	switchBuilder.append('!');	break;
						case NONE:				switchBuilder.append('0');	break;
						case UNASSIGNED:			switchBuilder.append('?');	break;
						default:					switchBuilder.append('E');	break;
						}
						
					}
					++index;
				}
				
				fileLines.add(primarySequenceBuilder.toString());
				fileLines.add(secondarySequenceBuilder.toString());
				fileLines.add(secondarySimpleBuilder.toString());
				fileLines.add(switchBuilder.toString());
				fileLines.add("");
				
				primarySequenceBuilder.setLength(LINE_HEADERS[0].length());
				secondarySequenceBuilder.setLength(LINE_HEADERS[1].length());
				secondarySimpleBuilder.setLength(LINE_HEADERS[2].length());
				switchBuilder.setLength(LINE_HEADERS[3].length());
				fullLineFlag = true;
			}
			
			if(!fullLineFlag) {
				if(latexMode) { fileLines.add("\\begin{samepage} \\begin{verbatim}"); }
				fileLines.add(primarySequenceBuilder.toString());
				fileLines.add(secondarySequenceBuilder.toString());
				fileLines.add(secondarySimpleBuilder.toString());
				fileLines.add(switchBuilder.toString());
				if(latexMode) { fileLines.add("\\end{samepage} \\end{verbatim}"); }
				fileLines.add("");
				
				primarySequenceBuilder.setLength(LINE_HEADERS[0].length());
				secondarySequenceBuilder.setLength(LINE_HEADERS[1].length());
				secondarySimpleBuilder.setLength(LINE_HEADERS[2].length());
				switchBuilder.setLength(LINE_HEADERS[3].length());
			}
			fileLines.add("");
		}
		
		if(latexMode) {
			fileLines.add("\\end{verbatim}\n");
		}
		writeFileLines(filename, fileLines);
	}
	
}
