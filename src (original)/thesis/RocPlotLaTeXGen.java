package thesis;

import java.io.PrintWriter;

import util.BaseTools;

/**
 * 
 * @author Benjy Strauss
 *
 */

public class RocPlotLaTeXGen extends BaseTools {
	
	private static final String[] PLOT_TYPES = { "sa", "sb", "su", "ta", "tb", "tu" };
	private static final String[] COMBOS = { "r", "6", "2", "u", "v", "6r", "6u", "6v", "2r", "2u", "2v",
			"ru", "rv", "uv", "6ru", "6rv", "6uv", "2ru", "6rv", "2uv", "ruv", "6ruv", "2ruv"};

	public static void main(String[] args) throws Exception {
		PrintWriter ROCPlotFigureWriter = new PrintWriter("output/roc-plots.tex");
		
		for(String str: PLOT_TYPES) {
			for(String str1: COMBOS) {
				
				if(str.startsWith("t") && same(str1)) {
					continue;
				}
				
				ROCPlotFigureWriter.write("\\begin{figure}[H]\n");
				ROCPlotFigureWriter.write("\\centering\n");
				ROCPlotFigureWriter.write("\\label{" + str + "-" + str1 + "}\n");
				ROCPlotFigureWriter.write("\\includegraphics{images/roc-plots/" + str + "-" + str1 + ".pdf}\n");
				ROCPlotFigureWriter.write("\\end{figure}\n\n");
			}
		}
		
		ROCPlotFigureWriter.close();
	}

	private static boolean same(String str1) {
		if(str1.contains("6") || str1.contains("2") || str1.contains("u")) {
			return false;
		} else return true;
	}

}
