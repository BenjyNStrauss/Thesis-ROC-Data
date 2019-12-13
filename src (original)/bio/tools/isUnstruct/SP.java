package bio.tools.isUnstruct;

import util.BadArraySizeException;

/**
 * This is basically 24 doubles...
 * @translator Benjy Strauss
 *
 */
public class SP {
	double ac_elong[] = new double[21];
	double a_init;
	double fs_n, fs_c;
	
	public SP(double... vals) {
		if(vals.length != 24) { throw new BadArraySizeException(); }
		for(int i = 0; i < 21; ++i) { ac_elong[i] = vals[i]; }
		a_init = vals[21];
		fs_n = vals[22];
		fs_c = vals[23];
	}
};
