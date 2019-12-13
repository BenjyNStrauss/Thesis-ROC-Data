package analysis.visualization;

import java.awt.Color;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.rtf.RTFEditorKit;

import bio.ProteinChain;
import bio.exceptions.InvalidDataFormatException;
import util.BaseTools;

/**
 * A class that writes overlays for visualizing descriptors of ProteinChains
 * This class can either write in black and white as a .txt or in color as an .rtf
 * 		Note that the .txt version uses numbers 0x1 through 0xF (15), adding numbers when dots overlap
 * 		Whereas the .rtf version uses colored dots, combining colors when dots overlap
 * 
 * 		E6 is always YELLOW
 * 		E20 is always BLUE
 * 		isUnstruct/Lobanov-Galzitzakya is always RED
 * 		Vkabat is always GRAY
 * 			(if Vkabat overlaps with another dot, then the dot will be darker)
 * @author Benjy Strauss
 *
 */

public class OverlayWriter extends BaseTools {
	private static final int CONST = 1;
	
	private static final	double E6_MAX = Math.log(6)/Math.log(2);
	private static final	double E20_MAX = Math.log(20)/Math.log(2);
	
	/**
	 * Writes a ProteinChain as a descriptor overlay file (txt)
	 * @param chain: the ProteinChain to write
	 */
	public static void writeOverlay(ProteinChain chain) {
		writeOverlay(chain, true, 0.02, 0.05, 80, false);
	}
	
	/**
	 * Writes a ProteinChain as a descriptor overlay file (rtf)
	 * @param chain: the ProteinChain to write
	 */
	public static void writeOverlayRTF(ProteinChain chain) {
		writeOverlay(chain, true, 0.02, 0.05, 80, true);
	}
	
	
	/**
	 * Writes a ProteinChain as a descriptor overlay file
	 * 
	 * @param chain: the ProteinChain to write
	 * @param normalize: "true" will normalize E6 and E20 between 0 and 1, 
	 * 			"false" means that these values will not be normalized
	 * 			note that isUnstruct is always normalized and Vkabat never is
	 * @param entropyInterval: the interval for the E6, E20, and isUnstruct
	 * 			a lower interval makes the graph bigger, but gives more precision
	 * 			this MUST be greater than 0!
	 * @param vkThreshold: the interval for the Vkabat
	 * 			a lower interval makes the graph bigger, but gives more precision
	 * 			this MUST be greater than 0!
	 * @param lineWidth: how many residues on a line
	 */
	public static void writeOverlay(ProteinChain chain, boolean normalize, double entropyInterval, double vkInterval,
			int lineWidth, boolean rtf) {
		if(chain == null) { throw new NullPointerException("Cannot create overlay for nothing!"); }
		if(lineWidth < 1) { throw new InvalidDataFormatException("Cannot write lines shorter than one character!"); }
		if(entropyInterval <= 0) { throw new InvalidDataFormatException("Entropy threshold must be positive!"); }
		if(vkInterval <= 0) { throw new InvalidDataFormatException("Vkabat threshold must be positive!"); }
		OverlayFileLine[] lines = setupOverlay(chain, normalize, entropyInterval, vkInterval);
		if(rtf) {
			writeOverlayFileRTF(lines, lineWidth, chain, 4);
		} else {
			writeOverlayFile(lines, lineWidth, chain, 4);
		}
	}
	
	/**
	 * Sets up the data for an overlay
	 * 
	 * @param chain
	 * @param normalize
	 * @param entropyThreshold
	 * @param vkThreshold
	 * @return: a list of strings that can then be written to the overlay file
	 */
	private static OverlayFileLine[] setupOverlay(ProteinChain chain, boolean normalize, double entropyThreshold, double vkThreshold) {
		//qp("Setting up: " + chain.fullID() + " : " + chain.firstIndex());
		double isUnstruct[]	= new double[chain.length()];
		double e6[]			= new double[chain.length()];
		double e20[]			= new double[chain.length()];
		double vkabat[]		= new double[chain.length()];
		
		double maxEntropy = E20_MAX / entropyThreshold;
		double maxVK = 9 / vkThreshold;
		
		double rawBrackets = max(maxEntropy,maxVK);
		int brackets = (int) rawBrackets;
		
		OverlayMarkerStruct[][] chainData = new OverlayMarkerStruct[chain.length()][brackets];
		
		//set up the arrays
		for(int index = 0; index < chain.length(); ++index) {
			if(chain.getAmino(index) != null) {
				if(normalize) {
					e6[index]  = chain.getAmino(index).E6() / E6_MAX;
					e20[index] = chain.getAmino(index).E20() / E20_MAX;
				} else {
					e6[index]  = chain.getAmino(index).E6();
					e20[index] = chain.getAmino(index).E20();
				}
				
				isUnstruct[index] = chain.getAmino(index).isUnstruct();
				vkabat[index] = chain.getAmino(index).vKabat();
			} else {
				e6[index]			= Double.NaN;
				e20[index]			= Double.NaN;
				isUnstruct[index]	= Double.NaN;
				vkabat[index]		= Double.NaN;
			}
			
			for(int index2 = 0; index2 < brackets; index2++) {
				chainData[index][index2] = new OverlayMarkerStruct();
			}
		}
		
		boolean placed_e6 = false;
		boolean placed_e20 = false;
		boolean placed_isu = false;
		boolean placed_vk = false;
		
		//place the values
		for(int index = 0; index < chain.length(); ++index) {
			placed_e6 = false;
			placed_e20 = false;
			placed_isu = false;
			placed_vk = false;
			
			for(int row = 0; row <= brackets; ++row) {
				if(row * entropyThreshold > e6[index] && (!placed_e6)) {
					if(row > 0) {  placed_e6 = chainData[index][row-CONST].e6 = true; }
				}
				
				if(row * entropyThreshold > e20[index] && (!placed_e20)) {
					if(row > 0) {  placed_e20 = chainData[index][row-CONST].e20 = true; }
				}
				
				if(row * entropyThreshold > isUnstruct[index] && (!placed_isu)) {
					if(row > 0) {  placed_isu = chainData[index][row-CONST].isu = true; }
				}
				
				if(row * vkThreshold > vkabat[index] && (!placed_vk)) {
					if(row > 0) { placed_vk = chainData[index][row-CONST].vk = true; }
				}
			}
		}
		
		//turn the values into arrays of strings
		OverlayFileLine fileBuilders[] = new OverlayFileLine[brackets];
		for(int i = 0; i < brackets; ++i) { fileBuilders[i] = new OverlayFileLine(entropyThreshold*i, vkThreshold*i); }
		
		for(int index = 0; index < chain.length(); ++index) {
			for(int i = 0; i < brackets; ++i) {
				fileBuilders[i].append(chainData[index][i].getRepresentation());
			}
		}
		
		return fileBuilders;
	}
	
	/**
	 * Writes a black-and-white (.txt) overlay
	 * @param lines
	 * @param lineWidth
	 * @param chain
	 * @param decimalPlaces
	 */
	private static void writeOverlayFile(OverlayFileLine[] lines, int lineWidth, ProteinChain chain, int decimalPlaces) {
		String fileName = OUTPUT + chain.fullID() + "-overlay.txt";
		//This is a pun on Yu-Gi-Oh ZeXal's "Overlay" mechanic
		PrintWriter zexalWriter = null;
		
		DecimalFormat formatter = new DecimalFormat();
		formatter.setMaximumFractionDigits(decimalPlaces);
		formatter.setMinimumFractionDigits(decimalPlaces);

		try {
			zexalWriter = new PrintWriter(fileName);
			
			zexalWriter.write("Key:\n");
			zexalWriter.write("1 = E6\n");
			zexalWriter.write("2 = E20\n");
			zexalWriter.write("4 = isUnstruct\n");
			zexalWriter.write("8 = Vkabat\n\n");
			
			StringBuilder secondaryBuilder = new StringBuilder();
			
			for(int segmentStart = 0; segmentStart < chain.length(); segmentStart += lineWidth) {
				for(int line = lines.length-1; line >= 0; --line) {
					zexalWriter.write(formatter.format(lines[line].entropyThreshold()) + " | ");
					zexalWriter.write(lines[line].toString(segmentStart, segmentStart+lineWidth).replaceAll("0", " "));
					zexalWriter.write(" | " + formatter.format(lines[line].vkThreshold()) + "\n");
				}
				
				for(int ii = 0; ii < decimalPlaces+3; ++ii) { zexalWriter.write(" "); }
				for(int ii = 0; ii < lineWidth+4; ++ii) { zexalWriter.write("–"); }
				zexalWriter.write("\n");
				for(int ii = 0; ii < decimalPlaces+5; ++ii) { zexalWriter.write(" "); }
				for(int ii = segmentStart; ii < (segmentStart + lineWidth); ++ii) {
					if(ii < chain.length()) { 
						if(chain.getAmino(ii) != null) {
							zexalWriter.write(chain.getAmino(ii).toChar());
							if(chain.getAmino(ii).secondary() != null) {
								secondaryBuilder.append(chain.getAmino(ii).secondary().toLetter());
							} else {
								secondaryBuilder.append("?");
							}
						} else {
							zexalWriter.write("_");
						}
					}
				}
				
				zexalWriter.write("\n");
				for(int ii = 0; ii < decimalPlaces+5; ++ii) { zexalWriter.write(" "); }
				zexalWriter.write(secondaryBuilder.toString());
				secondaryBuilder.setLength(0);
				
				zexalWriter.write("\n(Residues " + segmentStart + " to " + (segmentStart + lineWidth) + ")\n\n");
			}
			
			zexalWriter.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Writes a color (.rtf) overlay
	 * Note that a monospaced font is always used
	 * @param lines
	 * @param lineWidth
	 * @param chain
	 * @param decimalPlaces
	 */
	private static void writeOverlayFileRTF(OverlayFileLine[] lines, int lineWidth, ProteinChain chain, int decimalPlaces) {
		String fileName = OUTPUT + chain.fullID() + "-overlay.rtf";
		//This is a pun on Yu-Gi-Oh ZeXal's "Overlay" mechanic
		
		RTFEditorKit kit = new RTFEditorKit();
		DefaultStyledDocument document = new DefaultStyledDocument();
		
		SimpleAttributeSet attrSet = new SimpleAttributeSet();
		StyleConstants.setFontFamily(attrSet, OverlayMarkerStruct.MONOSPACED); 
		
		FileOutputStream zexalOutputStream = null;
		
		DecimalFormat formatter = new DecimalFormat();
		formatter.setMaximumFractionDigits(decimalPlaces);
		formatter.setMinimumFractionDigits(decimalPlaces);
		
		addToDocument(document, "Key:\n", attrSet);
		addToDocument(document, "E6         = Yellow\n", attrSet);
		addToDocument(document, "E20        = Blue\n", attrSet);
		addToDocument(document, "isUnstruct = Red\n", attrSet);
		addToDocument(document, "Vkabat     = Gray\n\n", attrSet);
		
		for(int segmentStart = 0; segmentStart < chain.length(); segmentStart += lineWidth) {
			for(int line = lines.length-1; line >= 0; --line) {
				addToDocument(document, formatter.format(lines[line].entropyThreshold()) + " | ", attrSet);
				
				String str = lines[line].toString(segmentStart, segmentStart+lineWidth).replaceAll("0", " ");
				for(int ii = 0; ii < lineWidth; ++ii) {
					if(ii < str.length()) {
						char ch = str.charAt(ii);
						String output = (ch == ' ') ? " " : OverlayMarkerStruct.RTF_DOT;
						
						addToDocument(document, output, OverlayMarkerStruct.getAttributes(ch));
					} else {
						break;
					}
				}
				
				StyleConstants.setForeground(attrSet, Color.BLACK);
				addToDocument(document, " | " + formatter.format(lines[line].vkThreshold()) + "\n", attrSet);
			}
			
			//write the lines on the bottom
			for(int ii = 0; ii < decimalPlaces+3; ++ii) { addToDocument(document, " ", attrSet); }
			for(int ii = 0; ii < lineWidth+4; ++ii) { addToDocument(document, "-", attrSet); }
			addToDocument(document, "\n", attrSet);
			for(int ii = 0; ii < decimalPlaces+5; ++ii) { addToDocument(document, " ", attrSet); }
			for(int ii = segmentStart; ii < (segmentStart + lineWidth); ++ii) { 
				if(ii < chain.length()) {
					if(chain.getAmino(ii) != null) {
						addToDocument(document, ""+chain.getAmino(ii).toChar(), attrSet);
					} else {
						addToDocument(document, "_", attrSet);
					}
				}
			}
			
			addToDocument(document, "\n", attrSet);
			for(int ii = 0; ii < decimalPlaces+5; ++ii) { addToDocument(document, " ", attrSet); }
			for(int ii = segmentStart; ii < (segmentStart + lineWidth); ++ii) { 
				if(ii < chain.length()) {
					if(chain.getAmino(ii) != null) {
						if(chain.getAmino(ii).secondary() != null) {
							addToDocument(document, ""+chain.getAmino(ii).secondary().toLetter(), attrSet);
						} else {
							addToDocument(document, "?", attrSet);
						}
					} else {
						addToDocument(document, "?", attrSet);
					}
				}
			}
			
			addToDocument(document, "\n(Residues " + segmentStart + " to " + (segmentStart + lineWidth) + ")\n\n", attrSet);
		}
		
		try {
			zexalOutputStream = new FileOutputStream(fileName);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		try {
			kit.write(zexalOutputStream, document, 0, document.getLength());
		} catch (IOException | BadLocationException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Appends a line of text to a document
	 * @param doc: the document to append the text to
	 * @param text: the text to append
	 * @param attributes: the attributes to give the text
	 */
	public static final void addToDocument(DefaultStyledDocument doc, String text, AttributeSet attributes) {
		try {
			doc.insertString(doc.getLength(), text, attributes);
		} catch (BadLocationException e1) {
			e1.printStackTrace();
		}
	}
}
