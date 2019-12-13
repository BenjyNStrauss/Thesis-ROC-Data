package bio.tools.isUnstruct;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

/**
 * 
 * @translator Benjy Strauss
 *
 */

public class SEQ_AC {
	ACIDP[] ac;
	int num, size;
	
	public String info;
	public int calc_pattern;
	int length() { return ac.length; }
	
	SEQ_AC() {
		ac = null; num=size=0; info = null; calc_pattern=0;
	}
 
	//ACIDP& operator[](int i) {return(ac[i]);}
  
	void Init(int n) {
		//qp(n);
		int i;
		ac = new ACIDP[n];
		num=0;
		size=n;
		for(i=0; i<n; i++) { ac[i] = new ACIDP(); }
	}
	
  	void ReInit() {
  		int i;
  		for(i=0; i<num; i++) { ac[i].reinit(); }
  		num=0;
  	}
  	
  	void LoadFasta(String seq_fasta) {
  		int i, n;
  		
  		calc_pattern=0;
  		info = "(no_name)";
  	
  		//if the fasta is raw
  		if(seq_fasta.charAt(0) == '>') {
  			info = seq_fasta.substring(1, seq_fasta.indexOf('\n'));
  	  		seq_fasta = seq_fasta.substring(seq_fasta.indexOf('\n'));
  		}
  		
  		seq_fasta = seq_fasta.replace("\n", "");
  		
  		if(seq_fasta.length() > size) { Init(seq_fasta.length()); }
  		num = seq_fasta.length();
  		
  		for(i=0,n=0; i < seq_fasta.length(); i++) {
  			if(seq_fasta.charAt(i) <= 0x20) { continue; }
  			
  			ac[n].reinit();
  			ac[n].ac = seq_fasta.charAt(i);
  			ac[n].ind_ac = Num_Ac(seq_fasta.charAt(i));
  			n++;
  		}
  		
  	}
  	
  	int Num_Ac(char c) {
		int ind[] = {
			20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20,
			20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20,
			//   A   B   C   D   E   F   G   H   I   J   K   L   M   N   O   P   Q   R   S   T   U   V   W   X   Y   Z
			20,  8, 20,  0, 15, 14,  2,  9, 16,  3, 20, 18,  4,  1, 13, 20, 19, 12, 17, 11, 10, 20,  5,  6, 20,  7, 20, 20, 20, 20, 20, 20,
			20,  8, 20,  0, 15, 14,  2,  9, 16,  3, 20, 18,  4,  1, 13, 20, 19, 12, 17, 11, 10, 20,  5,  6, 20,  7, 20, 20, 20, 20, 20, 20,
			20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20,
			20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20,
			20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20,
			20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20};
		return(ind[c]);
	}
  	
  	//in progress
  	void LoadFastaFile(String name) {
  		try{
  			BufferedReader reader = new BufferedReader(new FileReader(name));
  			
  			String s = "";
  			while(reader.ready()) {
  				s += reader.readLine() + "\n";
  			}
  			
  			LoadFasta(s);
  			reader.close();	
  		} catch (IOException IOE) {
  			
  		}
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
