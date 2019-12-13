package analysis.visualization;
import java.io.File;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Set;

import analysis.stats.ROCRecord;
import util.BaseTools;
import util.Coordinate;

/**
 * Load this up with ROCRecords and then call "makePlot()" to create ROC plots
 * This class creates one ROC plot for every set of descriptors
 * 
 * @author Benjy Strauss
 *
 */

public class ROCVisualizer extends BaseTools {
	
	private String folderName;
	private ArrayList<ROCRecord> rocBuffer;
	
	public ROCVisualizer(String filename) {
		rocBuffer = new ArrayList<ROCRecord>();
		
		folderName = filename.substring(0, filename.indexOf(".")-8);
		folderName = folderName.substring(folderName.indexOf("/")+1);
		folderName = OUTPUT + folderName + "-ROCPlot";
	}
	
	/**
	 * Add a ROCRecord(s) to the Visualizer
	 * @param records
	 */
	public void addRecords(ROCRecord... records) {
		for(ROCRecord rr: records) { rocBuffer.add(rr); }	
	}
	
	/**
	 * Make plots based on the ROCRecords in the Visualizer
	 * Plot x = False Positive Rate (FPR)
	 * Plot y = True Positive Rate (TPR)
	 * 
	 */
	public void makePlot() {
		makePlot(0.01);
	}
	
	/**
	 * Make plots based on the ROCRecords in the Visualizer
	 * Plot x = False Positive Rate (FPR)
	 * Plot y = True Positive Rate (TPR)
	 * @param threshold: 
	 */
	public void makePlot(double threshold) {
		dqp("resolution: " + threshold);
		File rocPlotFolder = new File(folderName);
		if(!rocPlotFolder.exists()) { rocPlotFolder.mkdir(); }
		
		Hashtable<String,ROCPlot> plots = new Hashtable<String,ROCPlot>();
		
		for(ROCRecord rr: rocBuffer) { 
			//dqp("Looking at: " + rr);
			String key = rr.toString();
			ROCPlot plot = plots.get(key);
			
			if(plot != null) {
				plot.addPoint(new Coordinate(rr.falsePositiveRate(), rr.truePositiveRate()));
			} else {
				plot = new ROCPlot(key);
				plot.addPoint(new Coordinate(rr.falsePositiveRate(), rr.truePositiveRate()));
				plots.put(key, plot);
			}
		}
		
		Set<String> keys = plots.keySet();
		for(String str: keys) {
			dqp("key is: " + str);
			
			ROCPlot plot = plots.get(str);
			//qp(plot.allPoints());
			plot.write(folderName, threshold*2);
		}
	}
	
	public String toString() {
		return "ROCVisualizer for " + folderName;
	}
}
