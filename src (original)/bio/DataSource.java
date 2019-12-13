package bio;

import java.util.List;

/**
 * 
 * @author Benjy Strauss
 *
 */

public enum DataSource {
	RCSB_PDB, GENBANK, UNIPROT, DSSP, OTHER;
	
	public static DataSource getDataSource(char src) {
		switch(src) {
		case 'D':
			qp("FASTAs will now come from DSSP.");
			return DSSP;
		case 'G':
			qp("FASTAs will now come from GenBank.");
			return GENBANK;
		case 'P':
		case 'R':
			qp("FASTAs will now come from RCSB-PDB.");
			return RCSB_PDB;
		case 'U':
			qp("FASTAs will now come from UniprotKB.");
			return UNIPROT;
		default:
			qp("Could not identify FASTA source.");
			return OTHER;
		}
	}
	
	/**
	 * 'qp' stands for quick-print
	 * @param arg0: the object to print
	 */
	private static final void qp(Object arg0) {
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
