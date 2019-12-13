package analysis.visualization;

import bio.exceptions.InvalidResolutionException;
import util.BaseTools;

/**
 * SuperClass for all objects that serve to visualize data
 * @author Benjy Strauss
 *
 */

public class VisualizationObject extends BaseTools {
	protected static final double DEFAULT_MINIMUM_ALLOWED_ROCPLOT_RESOLUTION = 0.01;
	protected static double minROCPlotResolution = DEFAULT_MINIMUM_ALLOWED_ROCPLOT_RESOLUTION;
	
	public String toString() {
		return "VisualizationObject (" + super.toString() + ")";
	}
	
	/**
	 * Set minimum ROC Plot Resolution
	 * The smaller this number, the larger the ROC Plots will be!
	 * A resolution of 0.00001 will result in ROC Plots >4GB in size!
	 * @param resolution
	 */
	public static void setMinROCPlotResolution(double resolution) {
		if(resolution <= 0 || resolution >= 1) {
			throw new InvalidResolutionException("Resolution must be between 0 and 1.");
		} else {
			minROCPlotResolution = resolution;
		}
	}
}
