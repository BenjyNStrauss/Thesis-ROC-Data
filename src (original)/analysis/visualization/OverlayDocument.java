package analysis.visualization;

import javax.swing.text.DefaultStyledDocument;

/**
 * 
 * @author Benjy Strauss
 *
 */

public class OverlayDocument extends DefaultStyledDocument {
	private static final long serialVersionUID = 1L;
	
	public class OverlaySectionElement extends SectionElement {
		private static final long serialVersionUID = 1L;
		
	}
	
	public OverlaySectionElement getASectionElement() {
		OverlaySectionElement retVal = null;
		retVal = new OverlaySectionElement();
		return retVal;
	}
	
}
