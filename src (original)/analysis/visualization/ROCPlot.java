package analysis.visualization;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;

import bio.exceptions.InvalidResolutionException;
import bio.exceptions.PointValueOutOfRangeException;
import util.Coordinate;

/**
 * ROCPlot represents a plot of TPR (True Positive Rate) against FPR (False Positive Rate)
 * 		Dots on the plot '•' represent various thresholds
 * To use this class:
 * 		(1) Name it
 * 		(2) Add points with addPoint()
 * 		(3) Write it to a file with the write() method, specifying a folder to write it to with the prefix
 * 
 * @author Benjy Strauss
 *
 */

public class ROCPlot extends VisualizationObject {

	private static final int UN_INIT = -1;
	private static final String SEPARATOR = " |";
	private static final String DIAGONAL =  "↗︎" ;
	private static final String RULER_0_02 = "     0.0       0.1       0.2       0.3       0.4       0.5       0.6       0.7       0.8       0.9       1.0\n\n";
	
	private String name;
	private ArrayList<Coordinate> points;
	
	/**
	 * Constructor: Creates a new ROCPlot
	 * @param name: the name of the ROCPlot.  Used in writing the ROCPlot to a file
	 */
	public ROCPlot(String name) {
		this.name = name;
		points = new ArrayList<Coordinate>();
	}
	
	/**
	 * Adds a point(s) to the ROCPlot
	 * @param points: the point(s) to add to the plot
	 */
	public void addPoint(Coordinate... points) {
		for(Coordinate point: points) {
			if(point.x > 1 || point.x < 0 || point.y > 1 || point.y < 0) {
				throw new PointValueOutOfRangeException();
			} else {
				this.points.add(point);
			}
		}
	}
	
	/**
	 * Calculates the area under the curve
	 * @return: AUC value
	 */
	public double AUC() {
		double auc = 0;
		
		//add boundary points
		addPoint(new Coordinate(0,0));
		addPoint(new Coordinate(1,1));
		
		Coordinate.setCompareY();
		Collections.sort(points);
		Coordinate.setCompareX();
		Collections.sort(points);
		
		for(int k = 1; k < points.size(); ++k) {
			
			double x = points.get(k).x - points.get(k-1).x;
			double y = points.get(k).y - points.get(k-1).y;
			
			double base = x * points.get(k-1).y;
			auc += x*y/2;
			auc += base;
		}
		
		return auc;
	}
	
	/**
	 * Writes the ROCPlot to a file
	 * @param prefix: the folder to write the ROCPlot to
	 */
	public void write(String prefix) {
		write(prefix, 0.02);
	}
	
	/**
	 * Writes the ROCPlot to a file
	 * @param prefix: the folder to write the ROCPlot to
	 * @param resolution: so far the data only works for resolution of 0.02
	 */
	public void write(String prefix, double resolution) {
		//qp("flag:resolution: " + resolution);
		if(resolution <= 0 || resolution >= 1) {
			throw new InvalidResolutionException("Resolution must be between 0 and 1.");
		}
		
		if(resolution < minROCPlotResolution) {
			resolution = minROCPlotResolution;
		}
		
		String filename = prefix + "/rocplot-" + name.replaceAll(" ", "").replaceAll("\\+", "-") + ".txt";
		double plotSize = 1 / resolution;
		int size = (int) Math.round(plotSize);
		
		boolean map[][] = new boolean[size][size];
		
		//qp("## " + name + " ## " + points.size());
		for(Coordinate point: points) {
			int x_bracket = UN_INIT, y_bracket = UN_INIT;
			
			for(int offset = 0; offset < size; ++offset) {
				if(offset*resolution >= point.x) {
					x_bracket = offset;
					break;
				}
			}
			
			for(int offset = 0; offset < size; ++offset) {
				if(offset*resolution >= point.y) {
					y_bracket = offset;
					break;
				}
			}
			
			if(x_bracket == UN_INIT) { x_bracket = size - 1; }
			if(y_bracket == UN_INIT) { y_bracket = size - 1; }
			
			map[x_bracket][y_bracket] = true;
		}
		
		PrintWriter writer = null;
		
		try {
			writer = new PrintWriter(filename);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return;
		}
		
		writer.write("ROC plot for: " + name + "\n\n");
		String str = resolution + "";
		int max_len = str.length();
		StringBuilder intervalBuilder = new StringBuilder();
		
		for(int y = size - 1; y >= 0; --y) {
			intervalBuilder.setLength(0);
			String meta = y*resolution + "";
			
			if(meta.length() > max_len) {
				intervalBuilder.append(meta.substring(0, max_len));
			} else {
				intervalBuilder.append(meta);
			}
			
			for(int ii = intervalBuilder.length(); ii < max_len; ++ii) {
				intervalBuilder.append(" ");
			}
			
			intervalBuilder.append(SEPARATOR);
			
			for(int x = 0; x < size; ++x) {
				if(x == y) {
					intervalBuilder.append((map[x][y]) ? "• " : DIAGONAL+" ");
				} else {
					intervalBuilder.append((map[x][y]) ? "• " : "  ");
				}
			}
			
			intervalBuilder.append("\n");
			writer.write(intervalBuilder.toString());
		}
		
		intervalBuilder.setLength(0);
		for(int ii = 0; ii < max_len + SEPARATOR.length(); ++ii) { intervalBuilder.append(" "); }
		for(int ii = 0; ii < size+1; ++ii) { intervalBuilder.append("–|"); }
		intervalBuilder.setLength(intervalBuilder.length()-1);
		intervalBuilder.append("\n");
		writer.write(intervalBuilder.toString());
		
		if(resolution == 0.02) { writer.write(RULER_0_02); }
		
		writer.write("x and y axis do not use the same spacing, although the intervals are the same\n");
		writer.write("x = false positive rate\n");
		writer.write("y = true positive rate\n");
		writer.write(DIAGONAL + " = diagonal\n\n");
		
		writer.write("Area Under Curve (AUC) = " + AUC());
		writer.write("\n");
		writer.close();
	}
	
	/**
	 * Get a string representation of all of the points on the plot
	 * @return a string containing all of the points, separated by commas
	 */
	String allPoints() {
		StringBuilder builder = new StringBuilder();
		for(Coordinate point: points) {
			builder.append(point.toString() + ",");
		}
		return builder.toString();
	}
	
	/**
	 * Tells if the ROCPlot has the name given
	 * @param str: the name
	 * @return
	 */
	public boolean equals(String str) {
		return name.equals(str);
	}
	
	/**
	 * Tells if two ROCPlots have the same name
	 * @param rocPlot
	 * @return
	 */
	public boolean equals(ROCPlot rocPlot) {
		return name.equals(rocPlot.name);
	}
	
	public String toString() {
		return name;
	}
}
