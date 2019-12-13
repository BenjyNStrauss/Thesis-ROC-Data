package bio.tools.isUnstruct;

/**
 * 
 * @translator Benjy Strauss
 *
 */

public class ACIDP {
	double e3d_n, e3d_c, p3d, elp_n, elp_c, plp, ept;
	double n3d;
	int  ind_ac;
	char ac;
	
	ACIDP() { reinit(); }
	
	void reinit() {
		e3d_n = 0; e3d_c = 0; p3d = 0;
		elp_n = 0; elp_c = 0; plp = 0;
		ept = 0;
		n3d = 0;
		ind_ac = 20;
		ac = 'x';
	}
	
	void CalcP() {
		double e3d, elp, sp;
		e3d = e3d_n + e3d_c;
		elp = elp_n + elp_c;
		p3d = 1.;
		plp = Math.exp(-elp+e3d);
		sp = p3d+plp;
		p3d/=sp;
		plp/=sp;
	}
}
