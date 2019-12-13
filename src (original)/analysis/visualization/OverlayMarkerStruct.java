package analysis.visualization;

import java.awt.Color;

import javax.swing.text.AttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

//import javafx.scene.paint.Color;

/**
 * 
 * @author Benjy Strauss
 *
 */

public class OverlayMarkerStruct extends VisualizationObject {
	
	public static final char E6_MARKER  = '1';
	public static final char E20_MARKER = '2';
	public static final char ISU_MARKER = '4';
	public static final char VK_MARKER  = '8';
	
	public static final char E6_E20_MARKER  = '3';
	public static final char E6_ISU_MARKER  = '5';
	public static final char E6_VK_MARKER   = '9';
	public static final char E20_ISU_MARKER = '6';
	public static final char E20_VK_MARKER  = 'A';
	public static final char ISU_VK_MARKER  = 'C';
	
	public static final char E6_E20_ISU_MARKER = '7';
	public static final char E6_E20_VK_MARKER  = 'B';
	public static final char E6_ISU_VK_MARKER  = 'D';
	public static final char E20_ISU_VK_MARKER = 'E';
	
	public static final char ALL_MARKER = 'F';
	//How dots appear on an RTF
	public static final String RTF_DOT = "•";
	//The monospaced font of choice; "Monospaced" does not work with the RTF writer
	public static final String MONOSPACED = "Courier";
	
	public boolean e6;
	public boolean e20;
	public boolean isu;
	public boolean vk;
	
	public OverlayMarkerStruct() { }
	
	/**
	 * Gets the hexadecimal representation of the data in the struct
	 * 	E6 = 1, E20 = 2, isUnstuct = 4, Vkabat = 8
	 * @return
	 */
	public char getRepresentation() {
		int subjectiveValue = 0;
		if(e6)  { subjectiveValue += 1; }
		if(e20) { subjectiveValue += 2; }		
		if(isu) { subjectiveValue += 4; }
		if(vk)  { subjectiveValue += 8; }
		
		String val = Integer.toHexString(subjectiveValue);
		val = val.toUpperCase();
		return val.charAt(0);
	}
	
	/**
	 * Gets the AttributeSet color based on the value of this object
	 * @return an AttributeSet containing the color of the character entered (in Courier font) 
	 */
	public AttributeSet getAttributes() {
		SimpleAttributeSet retVal = new SimpleAttributeSet();
		StyleConstants.setFontFamily(retVal, MONOSPACED); 
		
		switch(getRepresentation()) {
		case '0':	
		case '1':	StyleConstants.setForeground(retVal, new Color(255, 241, 11));	break; //yellow
		case '2':	StyleConstants.setForeground(retVal, new Color(23, 183, 250));	break; //blue
		case '3':	StyleConstants.setForeground(retVal, new Color(35, 255, 8));		break; //green
		case '4':	StyleConstants.setForeground(retVal, new Color(255, 0, 0));		break; //red
		case '5':	StyleConstants.setForeground(retVal, new Color(253, 169, 9));		break; //orange
		case '6':	StyleConstants.setForeground(retVal, new Color(107, 0, 255));		break; //purple
		case '7':	StyleConstants.setForeground(retVal, new Color(117, 77, 46));		break; //brown
		case '8':	StyleConstants.setForeground(retVal, new Color(118, 108, 112));	break; //gray
		case '9':	StyleConstants.setForeground(retVal, new Color(167, 158, 8));		break; //dark-yellow
		case 'A':	StyleConstants.setForeground(retVal, new Color(27, 97, 180));		break; //dark-blue
		case 'B':	StyleConstants.setForeground(retVal, new Color(21, 157, 4));		break; //dark-green
		case 'C':	StyleConstants.setForeground(retVal, new Color(156, 4, 5));		break; //dark-red
		case 'D':	StyleConstants.setForeground(retVal, new Color(169, 113, 6));		break; //dark-orange
		case 'E':	StyleConstants.setForeground(retVal, new Color(62, 0, 149));		break; //dark-purple
		case 'F':	StyleConstants.setForeground(retVal, Color.BLACK);  break;
		default:
		}
		
		return retVal;
	}
	
	/**
	 * Gets the RTF color for the character given
	 * @param value: the character used to determine the color
	 * @return an AttributeSet containing the color of the character entered (in Courier font) 
	 */
	public static AttributeSet getAttributes(char ch) {
		SimpleAttributeSet retVal = new SimpleAttributeSet();
		StyleConstants.setFontFamily(retVal, MONOSPACED);
		ch = Character.toUpperCase(ch);
		
		switch(ch) {
		case '0':	
		case '1':	StyleConstants.setForeground(retVal, new Color(255, 241, 11));	break; //yellow
		case '2':	StyleConstants.setForeground(retVal, new Color(23, 183, 250));	break; //blue
		case '3':	StyleConstants.setForeground(retVal, new Color(35, 255, 8));		break; //green
		case '4':	StyleConstants.setForeground(retVal, new Color(255, 0, 0));		break; //red
		case '5':	StyleConstants.setForeground(retVal, new Color(253, 169, 9));		break; //orange
		case '6':	StyleConstants.setForeground(retVal, new Color(107, 0, 255));		break; //purple
		case '7':	StyleConstants.setForeground(retVal, new Color(117, 77, 46));		break; //brown
		case '8':	StyleConstants.setForeground(retVal, new Color(118, 108, 112));	break; //gray
		case '9':	StyleConstants.setForeground(retVal, new Color(167, 158, 8));		break; //dark-yellow
		case 'A':	StyleConstants.setForeground(retVal, new Color(27, 97, 180));		break; //dark-blue
		case 'B':	StyleConstants.setForeground(retVal, new Color(21, 157, 4));		break; //dark-green
		case 'C':	StyleConstants.setForeground(retVal, new Color(156, 4, 5));		break; //dark-red
		case 'D':	StyleConstants.setForeground(retVal, new Color(169, 113, 6));		break; //dark-orange
		case 'E':	StyleConstants.setForeground(retVal, new Color(62, 0, 149));		break; //dark-purple
		case 'F':	StyleConstants.setForeground(retVal, Color.BLACK);  break;
		default:
		}
		
		return retVal;
	}
}
