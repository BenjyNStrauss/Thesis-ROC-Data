package analysis.stats;

import java.util.ArrayList;

/**
 * 
 * @author Benjy Strauss
 *
 */

public class RegressionTableValue extends StatsObject {
	private static final long serialVersionUID = 1L;
	private static final String SPACE = " ";
	
	private String name;
	private double coef;
	private double std_err;
	private double z_score;
	private double p_value;
	private double _0_025;
	private double _0_975;
	
	/**
	 * 
	 * @param line: the line to parse data from
	 */
	public RegressionTableValue(String line) {
		String[] values = line.split(SPACE);
		
		ArrayList<String> compactor = new ArrayList<String>();
		for(String str: values) {
			if(str.length() > 0) {
				compactor.add(str);
			}
		}
		
		values = new String[compactor.size()];
		compactor.toArray(values);
		
		_0_975 = Double.parseDouble(values[values.length-1]);
		_0_025 = Double.parseDouble(values[values.length-2]);
		p_value = Double.parseDouble(values[values.length-3]);
		z_score = Double.parseDouble(values[values.length-4]);
		std_err = Double.parseDouble(values[values.length-5]);
		coef = Double.parseDouble(values[values.length-6]);
		
		StringBuilder nameBuilder = new StringBuilder();
		for(int index = 0; index < values.length-6; ++index) {
			nameBuilder.append(values[index] + SPACE);
		}
		nameBuilder.setLength(nameBuilder.length()-1);
		name = nameBuilder.toString();
		name = name.replaceAll(", levels=SideChainIDs", " Type");
	}
	
	public String name() { return name; }
	public double coef() { return coef; }
	public double std_err() { return std_err; }
	public double z_score() { return z_score; }
	public double p_value() { return p_value; }
	public double _0_025() { return _0_025; }
	public double _0_975() { return _0_975; }
	
	/**
	 * 
	 */
	public String toString() {
		StringBuilder latexLineBuilder = new StringBuilder();
		latexLineBuilder.append(name + " & ");
		latexLineBuilder.append(coef + " & ");
		latexLineBuilder.append(std_err + " & ");
		latexLineBuilder.append(z_score + " & ");
		latexLineBuilder.append(p_value + RegressionResult.LATEX_TABLE_LINE_END);
		return latexLineBuilder.toString();
	}
}
