package analysis.stats;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;

import bio.exceptions.InvalidDataFormatException;
import bioUI.SwitchWriteMode;

/**
 * 
 * @author Benjy Strauss
 *
 */

public class RegressionResult extends StatsObject {
	private static final long serialVersionUID = 1L;
	private static final String NEW_LINE = "\n";
	private static final String LINE_SPACING = "   ";
	static final String LATEX_TABLE_LINE_END = " \\\\ \\hline";
	
	private static final String FORMULA = "Formula: ";
	private static final String ACCURACY = "Accuracy: ";
	private static final String DEP_VARIABLE = "Dep. Variable:";
	private static final String OBSERVATIONS = "No. Observations:";
	private static final String MODEL = "Model:";
	private static final String DF_RESIDUALS = "Df Residuals:";
	private static final String METHOD = "Method:";
	private static final String DF_MODEL = "Df Model:";
	private static final String DATE = "Date:";
	private static final String PSEUDO_R_SQUARED = "Pseudo R-squ.:";
	private static final String TIME = "Time:";
	private static final String LOG_LIKELIHOOD = "Log-Likelihood:";
	private static final String CONVERGED = "converged:";
	private static final String LL_NULL = "LL-Null:";
	private static final String LLR = "LLR p-value:";
	
	private static SwitchWriteMode readMode;
	private static boolean tripletMode = false;
	
	private String formula;
	private double accuracy;
	
	private String dependantVariable;
	private String model;
	private String method;
	private Date date;
	private boolean converged;
	private int observations;
	private int df_residuals;
	private int df_model;
	private double pseudo_r_squared;
	private double log_likelihood;
	private double ll_null;
	private BigDecimal llr_p_value;
	
	private RegressionTableValue table[];
	
	/**
	 * 
	 * @param line
	 */
	public RegressionResult(String line) { this(line.split(NEW_LINE)); }
	
	/**
	 * 
	 * @param lines
	 */
	public RegressionResult(String[] lines) {
		int start = 0;
		String dateStr;
		
		for(int i = 0; i < lines.length; ++i) {
			if(lines[i].startsWith(FORMULA)) { start = i; break; }
		}
		
		if(lines[start+0].startsWith(FORMULA)) { 
			formula = lines[start+0].substring(FORMULA.length());
		} else { throw new InvalidDataFormatException(); }
		
		if(lines[start+1].startsWith(ACCURACY)) { 
			accuracy = Double.parseDouble(lines[start+1].substring(ACCURACY.length()));
		} else { throw new InvalidDataFormatException(); }
		
		if(lines[start+4].startsWith(DEP_VARIABLE)) { 
			String line04 = lines[start+4].substring(DEP_VARIABLE.length()).trim();
			dependantVariable = line04.substring(0, line04.indexOf(LINE_SPACING));
			line04 = line04.substring(line04.indexOf(LINE_SPACING) + LINE_SPACING.length() + OBSERVATIONS.length());
			line04 = line04.trim();
			observations = Integer.parseInt(line04);
		} else { throw new InvalidDataFormatException(); }
		
		if(lines[start+5].startsWith(MODEL)) { 
			String line05 = lines[start+5].substring(MODEL.length()).trim();
			model = line05.substring(0, line05.indexOf(LINE_SPACING));
			line05 = line05.substring(line05.indexOf(LINE_SPACING) + LINE_SPACING.length() + DF_RESIDUALS.length());
			line05 = line05.trim();
			df_residuals = Integer.parseInt(line05);
		} else { throw new InvalidDataFormatException(); }
		
		if(lines[start+6].startsWith(METHOD)) { 
			String line06 = lines[start+6].substring(METHOD.length());
			line06 = line06.trim();
			method = line06.substring(0, line06.indexOf(LINE_SPACING));
			line06 = line06.substring(line06.indexOf(LINE_SPACING) + LINE_SPACING.length() + DF_MODEL.length());
			line06 = line06.trim();
			df_model = Integer.parseInt(line06);
		} else { throw new InvalidDataFormatException(); }
		
		if(lines[start+7].startsWith(DATE)) { 
			String line07 = lines[start+7].substring(DATE.length()).trim();
			dateStr = line07.substring(0, line07.indexOf(LINE_SPACING));
			line07 = line07.substring(line07.indexOf(LINE_SPACING) + LINE_SPACING.length() + PSEUDO_R_SQUARED.length());
			line07 = line07.trim();
			pseudo_r_squared = Double.parseDouble(line07);
		} else { throw new InvalidDataFormatException(); }
		
		if(lines[start+8].startsWith(TIME)) { 
			String line08 = lines[start+8].substring(TIME.length()).trim();
			date = parseDate(dateStr, line08.substring(0, line08.indexOf(LINE_SPACING)));
			line08 = line08.substring(line08.indexOf(LINE_SPACING) + LINE_SPACING.length() + LOG_LIKELIHOOD.length());
			line08 = line08.trim();
			log_likelihood = Double.parseDouble(line08);
		} else { throw new InvalidDataFormatException(); }
		
		if(lines[start+9].startsWith(CONVERGED)) { 
			String line09 = lines[start+9].substring(CONVERGED.length()).trim();
			converged = Boolean.parseBoolean(line09.substring(0, line09.indexOf(LINE_SPACING)));
			line09 = line09.substring(line09.indexOf(LINE_SPACING) + LINE_SPACING.length() + LOG_LIKELIHOOD.length());
			line09 = line09.trim();
			ll_null = Double.parseDouble(line09);
		} else { throw new InvalidDataFormatException(); }
		
		if(lines[start+10].trim().startsWith(LLR)) { 
			String line10 = lines[start+10].trim().substring(LLR.length()).trim();
			llr_p_value = new BigDecimal(line10);
		} else { throw new InvalidDataFormatException(); }
		
		ArrayList<RegressionTableValue> values = new ArrayList<RegressionTableValue>();
		for(int index = start+14; index < lines.length; ++index) {
			if(lines[index].startsWith("=====")) { break; }
			values.add(new RegressionTableValue(lines[index]));
		}
		
		table = new RegressionTableValue[values.size()];
		values.toArray(table);
	}
	
	public String formula() { return formula; }
	public double accuracy() { return accuracy; }
	public String dependantVariable() { return dependantVariable; }
	public String model() { return model; }
	public String method() { return method; }
	public Date date() { return date; }
	public boolean converged() { return converged; }
	public int observations() { return observations; }
	public int df_residuals() { return df_residuals; }
	public int df_model() { return df_model; }
	public double pseudo_r_squared() { return pseudo_r_squared; }
	public double log_likelihood() { return log_likelihood; }
	public double ll_null() { return ll_null; }
	public BigDecimal llr_p_value() { return llr_p_value; }
	
	/**
	 * 
	 * @param arg
	 * @return
	 */
	@SuppressWarnings("deprecation")
	private static Date parseDate(String dateStr, String timeStr) {
		Date retVal = null;
		
		dateStr = dateStr.substring(dateStr.indexOf(",")+1).trim();
		String day = dateStr.substring(0, dateStr.indexOf(" ")).trim();
		dateStr = dateStr.substring(dateStr.indexOf(" ")).trim();
		String month = dateStr.substring(0, dateStr.indexOf(" ")).trim();
		String year = dateStr.substring(dateStr.indexOf(" ")).trim();

		int _month;
		switch(month.toLowerCase()) {
		case "jan": _month = 1; break;
		case "feb": _month = 2; break;
		case "mar": _month = 3; break;
		case "apr": _month = 4; break;
		case "may": _month = 5; break;
		case "jun": _month = 6; break;
		case "jul": _month = 7; break;
		case "aug": _month = 8; break;
		case "sep": _month = 9; break;
		case "oct": _month = 10; break;
		case "nov": _month = 11; break;
		case "dec": _month = 12; break;
		default: _month = 0;
		}
		
		int _day = Integer.parseInt(day);
		int _year = Integer.parseInt(year);
		_year -= 1900;
		
		String timeVals[] = timeStr.split(":");
		int time_vals[] = new int[3];
		time_vals[0] = Integer.parseInt(timeVals[0]);
		time_vals[1] = Integer.parseInt(timeVals[1]);
		time_vals[2] = Integer.parseInt(timeVals[2]);
		
		retVal = new Date(_year, _month, _day, time_vals[0], time_vals[1], time_vals[2]);
		
		return retVal;
	}
	
	private String getCaption(boolean fields) {
		StringBuilder retValBuilder = new StringBuilder();
		
		if(fields) {
			retValBuilder.append("Fields: ");
		} else {
			retValBuilder.append("Descriptors: ");
		}
		
		if(tripletMode) {
			retValBuilder.append("Triplet-");
		} else {
			retValBuilder.append("Single-");
		}
		
		if(readMode == null) {
			throw new NullPointerException();
		}
		
		switch(readMode) {
		case ASSIGNED:		retValBuilder.append("Assigned: ");		break;
		case BOTH:			retValBuilder.append("Both: ");			break;
		case UNASSIGNED:		retValBuilder.append("Unassigned: ");	break;
		case WEIGHTED:		retValBuilder.append("Weighted(" + readMode.DISORDERED_WEIGHT + "): "); break;
		}
		
		String metaFormula = formula.replaceAll(", levels=SideChainIDs", " Type");
		metaFormula = metaFormula.replaceAll("~", "-");
		
		retValBuilder.append(metaFormula);
		
		return retValBuilder.toString();
	}
	
	/**
	 * 
	 * @return
	 */
	public String[] toLaTeX() {
		String text[] = null;
		ArrayList<String> textBuilder = new ArrayList<String>();
		textBuilder.add("\\begin{table}[H]");
		textBuilder.add("\\begin{center}");
		textBuilder.add("\t\\caption{" + getCaption(true) + "}");
		textBuilder.add("\\label{" + getCaption(true) + "}");
		textBuilder.add("\\begin{tabular}{ |l|c| }");
		textBuilder.add("\\hline");
		textBuilder.add("\\textbf{Field} & \\textbf{Value}" + LATEX_TABLE_LINE_END);
		textBuilder.add(ACCURACY + " & " + accuracy + LATEX_TABLE_LINE_END);
		textBuilder.add(DEP_VARIABLE + " & " + dependantVariable + LATEX_TABLE_LINE_END);
		textBuilder.add(OBSERVATIONS + "  & " + observations + LATEX_TABLE_LINE_END);
		textBuilder.add(MODEL + " & " + model + LATEX_TABLE_LINE_END);
		textBuilder.add(DF_RESIDUALS + " & " + df_residuals + LATEX_TABLE_LINE_END);
		textBuilder.add(METHOD + " & " + method + LATEX_TABLE_LINE_END);
		textBuilder.add(DF_MODEL + " & " + df_model + LATEX_TABLE_LINE_END);
		textBuilder.add(DATE + " & " + date + LATEX_TABLE_LINE_END);
		textBuilder.add(DEP_VARIABLE + " & " + dependantVariable + LATEX_TABLE_LINE_END);
		textBuilder.add(PSEUDO_R_SQUARED + " & " + pseudo_r_squared + LATEX_TABLE_LINE_END);
		textBuilder.add(LOG_LIKELIHOOD + " & " + log_likelihood + LATEX_TABLE_LINE_END);
		textBuilder.add(CONVERGED + " & " + converged + LATEX_TABLE_LINE_END);
		textBuilder.add(LL_NULL + " & " + ll_null + LATEX_TABLE_LINE_END);
		textBuilder.add(LLR + " & " + llr_p_value + LATEX_TABLE_LINE_END);
		textBuilder.add("\\end{tabular}");
		textBuilder.add("\\end{center}");
		textBuilder.add("\\end{table}");
		textBuilder.add("");
		textBuilder.add("\\begin{table}[H]");
		textBuilder.add("\\begin{center}");
		textBuilder.add("\t\\caption{" + getCaption(false) + "}");
		textBuilder.add("\\label{" + getCaption(false) + "}");
		textBuilder.add("\\begin{tabular}{ |l|c|c|c|c| }");
		textBuilder.add("\\hline");
		textBuilder.add("\\textbf{Name} & \\textbf{Beta} & \\textbf{Std Err} & \\textbf{Z-score} & \\textbf{P-value}" + LATEX_TABLE_LINE_END);
		
		for(RegressionTableValue row: table) {
			textBuilder.add(row.toString());
		}
		
		textBuilder.add("\\end{tabular}");
		textBuilder.add("\\end{center}");
		textBuilder.add("\\end{table}");
		textBuilder.add("");
		
		text = new String[textBuilder.size()];
		textBuilder.toArray(text);
		
		return text;
	}
	
	public static SwitchWriteMode getReadMode() { return readMode; }

	public static void setReadMode(SwitchWriteMode readMode) {
		RegressionResult.readMode = readMode;
	}
	
	public static boolean isTripletMode() {
		return tripletMode;
	}

	public static void setTripletMode(boolean tripletMode) {
		RegressionResult.tripletMode = tripletMode;
	}

	@SuppressWarnings("unused")
	private void debug_print() {
		qp(FORMULA + LINE_SPACING + formula);
		qp(ACCURACY + LINE_SPACING + accuracy);
		qp(DEP_VARIABLE + LINE_SPACING + dependantVariable);
		qp(OBSERVATIONS + LINE_SPACING + observations);
		qp(MODEL + LINE_SPACING + model);
		qp(DF_RESIDUALS + LINE_SPACING + df_residuals);
		qp(METHOD + LINE_SPACING + method);
		qp(DF_MODEL + LINE_SPACING + df_model);
		qp(DATE + LINE_SPACING + date);
		qp(PSEUDO_R_SQUARED + LINE_SPACING + pseudo_r_squared);
		qp(LOG_LIKELIHOOD + LINE_SPACING + log_likelihood);
		qp(CONVERGED + LINE_SPACING + converged);
		qp(LL_NULL + LINE_SPACING + ll_null);
		qp(LLR + LINE_SPACING + llr_p_value);
	}
	
	public String toString() {
		return "Regression result for: " + formula;
	}
}
