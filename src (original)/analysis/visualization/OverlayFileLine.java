package analysis.visualization;

/**
 * A wrapper class for a StringBuilder which allows for extra data to be held
 * 
 * @author Benjy Strauss
 *
 */

public class OverlayFileLine extends VisualizationObject {
	private StringBuilder lineBuilder;
	private double entropyThreshold;
	private double vkThreshold;
	
	public OverlayFileLine(double entropyThreshold, double vkThreshold) { 
		lineBuilder = new StringBuilder();
		this.entropyThreshold = entropyThreshold;
		this.vkThreshold = vkThreshold;
	}
	
	/** Empty the internal StringBuilder by setting the length to zero */
	public void clear() { lineBuilder.setLength(0); }
	
	/** Append a char to the internal StringBuilder */
	public void append(char ch) { lineBuilder.append(ch); }
	/** Append a String to the internal StringBuilder */
	public void append(String str) { lineBuilder.append(str); }
	
	/** @return: the entropy/isUnstruct threshold (aka interval) */
	public double entropyThreshold() { return entropyThreshold; }
	/** @return: the vkabat threshold (aka interval) */
	public double vkThreshold() { return vkThreshold; }
	
	/** Call toString() on the internal StringBuilder */
	public String toString() { return lineBuilder.toString(); }
	
	/**
	 * Return a portion of the String in the internal StringBuilder without risking a StringIndexOutOfBounds Exception
	 * @param start: the string index to start at (anything less than zero will be treated as zero)
	 * @param end: the string index to end at
	 * @return 
	 */
	public String toString(int start, int end) { 
		if(start < 0) { start = 0; }
		if(start > lineBuilder.length()) { return ""; }
		
		if(end > lineBuilder.length()) {
			return lineBuilder.toString().substring(start);
		} else {
			return lineBuilder.toString().substring(start, end);
		}
	}
}
