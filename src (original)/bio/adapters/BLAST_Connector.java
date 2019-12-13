package bio.adapters;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class BLAST_Connector {
	
	private static final String BLAST_URL = "https://www.ncbi.nlm.nih.gov/blast/Blast.cgi?CMD=Put&amp;QUERY=";
	private static final String BLAST_PARAMS = "format_type=Text&alignments=1000&descriptions=10000&hitlist_size=10000";
	
	public static void writeBlast(String seq) throws Exception {
		URL toBLAST = new URL(BLAST_URL + seq +"=blastp&FILTER=L&DATABASE=nr&" + BLAST_PARAMS);
        URLConnection yc = toBLAST.openConnection();
        BufferedReader in = new BufferedReader(new InputStreamReader(yc.getInputStream()));
        String inputLine;
        StringBuilder sb=new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            System.out.println(inputLine);
            sb.append(inputLine);
        }
        in.close();
	}
	
	//        blast_result_handle = NCBIWWW.qblast('blastp', 'nr', gi_number, \
	
	public static void main(String[] args)  {
		try {
			writeBlast("SNAMNSQLTLRALERGDLRFIHNLNNNRNIMSYWFEEPYESFDELEELYNKHIHDNAERRFVVEDAQKNLIGLVELIEINYIHRSAEFQIIIAPEHQGKGFARTLINRALDYSFTILNLHKIYLHVAVENPKAVHLYEECGFVEEGHLVEEFFINGRYQDVKRMYILQSKYLNRSE");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
