package bio.tools.isUnstruct;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.List;

/**
 * .ius not working
 * @translator Benjy Strauss
 *
 */

public class IsUnstruct extends PredDel {
	private static final String BLANK_120 = "";//"                                                                                                                        ";
	String release = "2.02";
	String file_name = "";
	int use_pattern=2, opt_type=1;      // for authors of IsUnstruct only
	String use_pattern_info[] = { "Patterns not used", "Only H6 pattern was used", "All patterns were used"};
	int long_disp=0, short_disp=0, file_save=1;
	
	private static final String replaceCharAtIndex(String str, int index, char newChar) {
		if(str.equals("")) { return str; }
		//qp(str.length());
		
		char[] strChars = str.toCharArray();
		strChars[index] = newChar;
		str = String.valueOf(strChars);
		
		//qp(str.length());
		return str;
	}
	
	/**
	 * 
	 * @param out_name
	 * @param inp_name
	 * @param ext
	 */
	protected String createOutName(String inp_name, String ext) {
		String out_name;
		
		int dotIndex = inp_name.indexOf('.');
		if(dotIndex != -1) {
			out_name = inp_name.substring(0, dotIndex);
		} else {
			out_name = inp_name;
		}
		
		if(ext.charAt(0) != '.') { out_name += "."; }
		out_name += ext;
		
		return out_name;
	}
	
	/**
	 * Creates the .iul
	 * @param sa
	 * @param inp_name
	 */
	void SaveLong(SEQ_AC sa, String inp_name) {
		String out_name = "";
		int i;
		PrintWriter out = null;
	
		char c, pt;
		out_name = createOutName(inp_name, "iul");
		try {
			out = new PrintWriter(out_name);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		out.printf("# IsUnstruct %s release / long format\n", release);
		out.printf("# %s\n", use_pattern_info[use_pattern]);
		if(opt_type!=1) { out.printf("# opt_type=%d. The prediction can be incorrect!\n", opt_type); }
		out.printf("# %s\n\n", sa.info);
	
		for(i=0; i < sa.ac.length; i++) {
			pt=' ';
			if(sa.ac[i].ept > (PredDel.E_PATTERN/2)) { pt='P'; }
			if(sa.ac[i].plp>0.5) { c='U'; }
			else { c='s'; }
			out.printf("%4d %c %c %c %5.3f\n", i+1, sa.ac[i].ac, c, pt, sa.ac[i].plp);
		}
		out.close();
	}
	
	/**
	 * creates the .ius
	 * @param sa
	 * @param inp_name
	 */
	void SaveShort(SEQ_AC sa, String inp_name) {
		String out_name = null;
		int i, i0, np;
		char c, q, cpt, tp;
		String seq = BLANK_120, qp = BLANK_120, prd = BLANK_120, pt = BLANK_120;
		out_name = createOutName(inp_name, "ius");
		
		PrintWriter out = null;
		try {
			out = new PrintWriter(out_name);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		out.printf("# IsUnstruct %s release / short format\n", release);
		out.printf("# %s\n", use_pattern_info[use_pattern]);
		if(opt_type!=1) {
			out.printf("# opt_type=%d. The prediction can be incorrect!\n", opt_type);
		}
		out.printf("# %s\n\n", sa.info);
		
		for(i0=0; i0 < sa.ac.length; i0 += 100) {
			np=0;
			for(i=i0; i<(i0+100) && i<sa.ac.length; i++) {
				if(i < sa.ac.length) {
					cpt = ' ';
					if(sa.ac[i].ept > (PredDel.E_PATTERN/2)) {
						np++;
						cpt='P';
					}
					c = sa.ac[i].ac;
					//qp("c: " + c);
					if(sa.ac[i].plp > 0.5) { 
						tp='U';
					} else {
						tp='s';
					}
					q='0';
					if(sa.ac[i].plp>=0.1  ) { q='1'; }
					if(sa.ac[i].plp>=0.2  ) { q='2'; }
					if(sa.ac[i].plp>=0.3  ) { q='3'; }
					if(sa.ac[i].plp>=0.4  ) { q='4'; }
					if(sa.ac[i].plp>=0.5  ) { q='5'; }
					if(sa.ac[i].plp>=0.6  ) { q='6'; }
					if(sa.ac[i].plp>=0.7  ) { q='7'; }
					if(sa.ac[i].plp>=0.8  ) { q='8'; }
					if(sa.ac[i].plp>=0.9  ) { q='9'; }
					//if(sa[i].plp>=0.995) q='U';
				} else {
					cpt= ' ';
					tp = ' ';
					c  = ' ';
					q  = ' ';
				}
				if((i%100)!=0 && (i%10)==0) {
					pt += ' ';
					prd += ' ';
					seq += ' ';
					qp += ' ';
				}
				pt += cpt;
				pt += (char) 0;
				prd += tp;
				prd += (char) 0;				
				seq += c;
				seq += (char) 0;
				qp += q;
				qp += (char) 0;
			}
			//qp("#" + seq.length());
			
			if(i0+100 <= sa.ac.length) {
				out.printf("sequence   %5d %-109s %4d\n",   i0+1, seq, i0+100);
				out.printf("state      %5d %-109s %4d\n",   i0+1, prd, i0+100);
				if(np>0) { out.printf("pattern    %5d %-109s %4d\n", i0+1, pt,  i0+100); }
				out.printf("probability%5d %-109s %4d\n\n", i0+1, qp,  i0+100);
			} else {
				out.printf("sequence   %5d %-109s %4d\n",   i0+1, seq, sa.ac.length);
				out.printf("state      %5d %-109s %4d\n",   i0+1, prd, sa.ac.length);
				if(np>0) { out.printf("pattern    %5d %-109s %4d\n", i0+1, pt, sa.ac.length); }
				out.printf("probability%5d %-109s %4d\n\n", i0+1, qp,  sa.ac.length);
			}
			
			pt = ""; prd = ""; seq = ""; qp = "";
		}
	out.close();
	}
	
	void SaveLong(SEQ_AC sa) {
		int i;
		char c, pt;
		System.out.printf("# IsUnstruct %s release / long format\n", release);
		System.out.printf("# %s\n", use_pattern_info[use_pattern]);
		if(opt_type!=1) { System.out.printf("# opt_type=%d. The prediction can be incorrect!\n", opt_type); }
		System.out.printf("# %s\n\n", sa.info);
		for(i=0; i < sa.ac.length; i++) {
			pt=' ';
			if(sa.ac[i].ept > (PredDel.E_PATTERN/2)) pt='P';
			if(sa.ac[i].plp>0.5) { c='U'; }
			else { c='s'; }
			System.out.printf("%4d %c %c %c %5.3f\n", i+1, sa.ac[i].ac, c, pt, sa.ac[i].plp);
		}
	}
	
	void SaveShort(SEQ_AC sa) {
		int i, j, i0, np;
		char c, q, cpt, tp;
		String seq = BLANK_120, qp = BLANK_120, prd = BLANK_120, pt = BLANK_120;
		System.out.printf("# IsUnstruct %s release / short format\n", release);
		System.out.printf("# %s\n", use_pattern_info[use_pattern]);
		if(opt_type!=1) { System.out.printf("# opt_type=%d. The prediction can be incorrect!\n", opt_type); }
		System.out.printf("# %s\n\n", sa.info);
	
		for(i0=0; i0 < sa.ac.length; i0+=100) {
			np=0;
			for(i=i0, j=0; i<(i0+100); i++) {
				if(i < sa.ac.length) {
					cpt = ' ';
					if(sa.ac[i].ept > (E_PATTERN/2)) {
						np++;
						cpt='P';
					}
					c = sa.ac[i].ac;
					if(sa.ac[i].plp>0.5) { tp='U'; }
					else { tp='s'; }
					q='0';
					if(sa.ac[i].plp>=0.1  ) q='1';
					if(sa.ac[i].plp>=0.2  ) q='2';
					if(sa.ac[i].plp>=0.3  ) q='3';
					if(sa.ac[i].plp>=0.4  ) q='4';
					if(sa.ac[i].plp>=0.5  ) q='5';
					if(sa.ac[i].plp>=0.6  ) q='6';
					if(sa.ac[i].plp>=0.7  ) q='7';
					if(sa.ac[i].plp>=0.8  ) q='8';
					if(sa.ac[i].plp>=0.9  ) q='9';
					//if(sa[i].plp>=0.995) q='U';
				} else {
					cpt= ' ';
					tp = ' ';
					c  = ' ';
					q  = ' ';
				}
				if((i%100)!=0 && (i%10)==0) {
					replaceCharAtIndex(pt, j, ' ');
					replaceCharAtIndex(prd, j, ' ');
					replaceCharAtIndex(seq, j, ' ');
					replaceCharAtIndex(qp, j, ' ');
					j++;
				}
				replaceCharAtIndex(pt, j, cpt);
				replaceCharAtIndex(pt, j+1, '-');
				replaceCharAtIndex(prd, j, tp);
				replaceCharAtIndex(prd, j+1, '-');
				replaceCharAtIndex(seq, j, c);
				replaceCharAtIndex(seq, j+1, '-');
				replaceCharAtIndex(qp, j, q);
				replaceCharAtIndex(qp, j+1, '-');
				j++;
		    }
			if(i0+100 <= sa.ac.length) {
				System.out.printf("sequence   %5d %-109s %4d\n",   i0+1, seq, i0+100);
				System.out.printf("state      %5d %-109s %4d\n",   i0+1, prd, i0+100);
				if(np>0) { System.out.printf("pattern    %5d %-109s %4d\n", i0+1, pt,  i0+100); }
				System.out.printf("probability%5d %-109s %4d\n\n", i0+1, qp,  i0+100);
		    } else{
		    	System.out.printf("sequence   %5d %-109s %4d\n",   i0+1, seq, sa.ac.length);
		    	System.out.printf("state      %5d %-109s %4d\n",   i0+1, prd, sa.ac.length);
		    	if(np>0) { System.out.printf("pattern    %5d %-109s %4d\n", i0+1, pt, sa.ac.length); }
		    	System.out.printf("probability%5d %-109s %4d\n\n", i0+1, qp,  sa.ac.length);
		    }
		}
	}

	void LoadKey(String[] argv) {
		int j;
		for(j=1; j< argv.length; j+=2) {
			if(argv[j].charAt(0) == '-') {
				switch(argv[j].charAt(1)) {
				case 'u': use_pattern = Integer.parseInt(argv[j+1]); break;
				case 'o': opt_type    = Integer.parseInt(argv[j+1]); break; // for authors of IsUnstruct only
				case 'l': long_disp   = Integer.parseInt(argv[j+1]); break;
				case 's': short_disp  = Integer.parseInt(argv[j+1]); break;
				case 'f': file_save   = Integer.parseInt(argv[j+1]); break;
				}
			} else {
				file_name = argv[j];
				break;
			}
		}
		SetPoten(use_pattern, opt_type);
	}
	
	public int main(String[] argv) {
		SEQ_AC sa = new SEQ_AC();
		LoadKey(argv);          // printf("LoadKey %s\n", file_name);
		if(file_name.equals("")) {
			System.out.printf("Usage: ./IsUnstruct [-use_pattern 0,1,2] [-long_disp 0,1] [-short_disp 0,1] [-file_save 0,1] <file_in_fasta_format>\n");
			System.out.printf("-use_pattern -- 0           Without patterns\n");
			System.out.printf("                1           With HHHHHH pattern only\n");
			System.out.printf("                2 (default) With all patterns\n");
			System.out.printf("-long_disp   -- 0 (default) Do not show long  output by the terminal\n");
			System.out.printf("             -- 1                  Show long  output by the terminal\n");
			System.out.printf("-short_disp  -- 0 (default) Do not show short output by the terminal\n");
			System.out.printf("             -- 1                  Show short output by the terminal\n");
			System.out.printf("-file_save   -- 0           Do not save output files\n");
			System.out.printf("             -- 1 (default)        Save output files\n");
			return(0);
		}
		
		sa.LoadFastaFile(file_name);  // printf("Load %s\n", file_name);
		Predict(sa);                  // printf("Predict\n");
		
		if(long_disp >0) SaveLong(sa);
		if(short_disp>0) SaveShort(sa);
		if(file_save>0) {
			SaveLong( sa, file_name);     // printf("SaveLong\n");
			SaveShort(sa, file_name);     // printf("SaveShort\n");
		}
		return(0);
	}
	
  	/**
	 * qp stands for quick-print
	 * mainly for use in debugging
	 * @param arg0: the object to print
	 */
	protected static void qp(Object arg0) {
		if(arg0 != null && arg0.getClass().isArray()) {
			Object[] i_arg0 = (Object[]) arg0;
			for(Object o: i_arg0) {
				System.out.println(o);
			}
		} else if(arg0 instanceof List) {
			List<?> l_arg0 = (List<?>) arg0;
			for(Object o: l_arg0) {
				System.out.println(o);
			}
		} else {
			System.out.println(arg0);
		}
	}
}