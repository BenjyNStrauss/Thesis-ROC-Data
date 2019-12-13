package bio.adapters;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import util.Defective;

/**
 * 
 * @author Benjy Strauss
 *
 *
 *To cull general protein sequences:

bin/Cull_for_UserSEQ.pl  -i file  -p 40

Required parameters are:
           -i input_sequence_file
              It can be a file of sequences in fasta format or output from
              BLAST/PSI-BLAST running. If PSI-BLAST output is used, the
              sequences will be taken from the Sbjct: lines from the hits.
           -p maxpc
              percent sequence identity threshold, the valid range is 5-100.
           -l minlen-maxlen (option)
              sequence length range, default is 20-10000
 *
 */

@Defective
public abstract class JPiscesAdapter {
	
	public static void cull(String fileName, int percent) {
		//"/bin/sh", "-c", command
		//String[] args = {"/bin/sh", "-c", "ls"};
		String[] args = {"PISCES/bin/Cull_for_UserSEQ.pl", "-i", fileName, "-p", ""+percent};
		ProcessBuilder builder = new ProcessBuilder(args);
		Process proc = null;
		
		System.out.println("flag0");
		
		try {
			proc = builder.start();
			
			InputStream is = proc.getInputStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(is));

			String line = null;
			while ((line = reader.readLine()) != null) {
			   System.out.println(line);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	
}
