package bio.tools.isUnstruct;

import java.util.List;

/**
 * Designed for Java implementation of "IsUnstruct"
 * @translator Benjy Strauss
 * pattern -> String
 */

public class PredDel {
	private static final int K_MAX = 5;
	@SuppressWarnings("unused")
	private static final int PT_SIZE = 100;
	private static final int GPT = 171;
	private static final int CH_PATTERN = 4;
	public static final int E_PATTERN = 50;
	private static final double wac_elong[] = {0.0137229,0.0224214,0.0389551,0.0545193,0.0887486,0.0688628,0.0141371,0.0346998,0.0794769,0.0742804,0.0553257,0.0608747,0.0369777,0.0424005,0.0654720,0.0569914,0.0240030,0.0493192,0.0585566,0.0461315,0.0000341};
	@SuppressWarnings("unused")
	private byte int1;
	
	private static final double wa_init = 0.0064994;
	private static final double wa_fs = 0.0037950;
	
	public static final String pt_all[] = { "HHHH", "ENLYFQ", "GSHM", "GPGSM", "DDDDK", "EGGHHHHH", "VPRGS", "ASMTGGQQMGR",
			"LEAHHH", "SNAM", "GGGGSGG", "GPLGS", "WSHPQFEK", "GSSGSSG", "TSLYKKAG", "HIEGRH",
			"EQKLISEEDLN", "HHHHHGGS", "PPPPP", "EDEREE", "AAALEHHHHHH", "GKTNFFEK", "GSRHHHH",
			"HHHHHHSSGLEVLFQGP", "GGGGG", "SSTSS", "PPAPP", "APAPA", "ENLYFQGHM", "IDPFT", "KKKKK",
			"KSCDK", "EGKPIPN", "SSSVD", "MGRGS", "DCGCKPCI", "APAATGA", "KKKAA", "KKTSS", "GGSGGGGSGGG",
			"HHHHSSG", "PTTENLYFQGAM", "SSSSTQ", "EAQEEEE", "EEEEEEE", "AQAQE", "EPEEPA",
			"DDDDEDD", "QQQQQG", "PPAAP", "VKPEVKP", "PAATS", "SHMAS", "RRGKKK", "RGEGGFG",
			"HHHHHHSQDP", "GPSSG", "GGAKRH", "APEDP", "PSPPP", "PSVSPS", "HHHHHMA", "DSVISS",
			"EPTESS", "AAVGGAA", "SPSPG", "GHMA", "RSEED", "FPSPESS", "PAPPP", "QQQQQQQ",
			"IKSHHNVGGLP", "EEEED", "YKDDD", "LDNGED", "PPPRPK", "GHHHHH", "NLREDGE", "PSPPSP",
			"YFQSM", "GGLNDIFEAQKIEWH", "NGDTPS", "GRGHHHH", "GAMDP", "EAEED", "EDDEDED", "DDDKD",
			"QQREEG", "DIPESQ", "KKGKS", "RGEET", "GTHHHHH", "AAHHHHHHH", "ESSSS", "DAPDI", "ASIGQA",
			"TTTATT", "NNNNN", "RRRGR", "GQRKRR", "SMAEG", "SKKKK", "RPQLDS", "TSAETP", "RGRPRG",
			"DHSPAP", "AAPPA", "GGGSSSG", "QQQQQP", "AGAVAGG", "SMAAGG", "KKSKK", "REEEE", "SEFGSS",
			"EKKTE", "EKKNS", "GGGDD", "GEKHHHHH", "PPAPAG", "DLVPR", "RGSMAS", "GSETMA", "EEEKKKE",
			"RGGGGSG", "KKPKNK", "RSVRSN", "DEEDE", "AGEGPA", "SHHHHHH", "EDDESD", "NSSSS", "CGYSD",
			"SGSGGGS", "MEEEE", "GLVPR", "EKKKS", "GVPRG", "TDNGNS", "GMDELYK", "DEGHHH", "GGSRS",
			"GAHHHHH", "AMADIGS", "HHLHHHG", "GGKKKK", "SDEEDSS", "EEEEG", "SGDDDD", "AQSTSA",
			"PPPPQ", "GSMTD", "GGGGSGGGGS", "EEDDD", "KKEKK", "STTST", "AELAAATA", "VDHHHH",
			"STSHHHHH", "HHHHHHHHHSSGHIDDDDKHM", "SDGKDD", "GSHMLEDP", "KSGYKD", "DEDSD", "DSDEE",
			"GGHNSS", "KSASS", "GSHGM", "MASPA", "SAWSHPQF", "GSEED", "ENLYFQGS" };

	
	double ac_elong[] = {  1.0980,  0.0899,  1.0183,  0.9935,  0.6880,  0.7298,  1.5734,  1.0513,  0.0289, -0.1192, -0.0228, -0.6032, -0.4020, -0.2612, -0.4826, -0.3638, -0.0110, -0.2658, -0.3660, -0.5851,  0.0000};
	double unt = 1;
	double a_init = 3.4848;
	double fs_n = -4.3152, fs_c = -4.2289;
	int PATTERN_T = 2;
	int gpt = GPT;
	
	/**
	 * 0 - Max Sw-0.02/t^2
	 * 1 - Max Sw50+auc (change T only)
	 * 2 - (Average by position) Max Sw-0.02/t^2
	 * 3 - (Average by position) Max Sw50+auc (change T only)
	 * 4 - Null
	 * 
	 * @param pat
	 * @param k
	 */
	void SetPoten(int pat, int k) {
		SP sp[][] = {
					
		  //         CYS,     MET,     PHE,     ILE,     LEU,     VAL,     TRP,     TYR,     ALA,     GLY,     THR,     SER,     GLN,     ASN,     GLU,     ASP,     HIS,    ARG,      LYS,    PRO,      UNK    a_init     fs_n     fs_c
		 {new SP(  1.8445, -0.1274,  1.4886,  1.6735,  0.9861,  1.2418,  2.7407,  1.4576,  0.1118, -0.3110, -0.0091, -0.9648, -0.6926, -0.4590, -0.8077, -0.5130, -0.6628, -0.3163, -0.4954, -0.8322,  0.0000,  5.4604, -6.4977, -6.4968),   // 0 - éØ‚®¨†´Ï≠Æ• Sw-0.02/t^2
		  new SP(  1.2544, -0.0866,  1.0124,  1.1381,  0.6706,  0.8445,  1.8639,  0.9913,  0.0760, -0.2115, -0.0062, -0.6562, -0.4710, -0.3122, -0.5493, -0.3489, -0.4508, -0.2151, -0.3369, -0.5660,  0.0000,  3.7136, -4.4191, -4.4185),   // 1 - éØ‚®¨†´Ï≠Æ• Sw50 && auc ØÆ´„Á•≠Æ ÆØ‚®¨®ß†Ê®•© ‚•¨Ø•‡†‚„‡Î
		  new SP(  2.4188, -0.2387,  1.6671,  2.0799,  1.2732,  1.4630,  3.1954,  1.5785, -0.0639, -0.5250, -0.1529, -1.1092, -0.7109, -0.7780, -0.9353, -0.8225, -1.1264, -0.3427, -0.5974, -1.1507,  0.0000,  5.2510,  2.5911,  0.9429),   // 2 - éÊ•≠™® „·‡•§≠Ò≠≠Î• ØÆ ØÆß®Ê®Ô¨. éØ‚®¨†´Ï≠Æ• Sw-0.02/t^2
		  new SP(  1.6725, -0.1650,  1.1527,  1.4381,  0.8803,  1.0116,  2.2094,  1.0914, -0.0442, -0.3630, -0.1057, -0.7670, -0.4915, -0.5379, -0.6467, -0.5687, -0.7788, -0.2370, -0.4131, -0.7956,  0.0000,  3.6308,  1.7916,  0.6520),   // 3 - éÊ•≠™® „·‡•§≠Ò≠≠Î• ØÆ ØÆß®Ê®Ô¨. éØ‚®¨†´Ï≠Æ• Sw50 && auc ØÆ´„Á•≠Æ ÆØ‚®¨®ß†Ê®•© ‚•¨Ø•‡†‚„‡Î
		  new SP(  0.0000,  0.0000,  0.0000,  0.0000,  0.0000,  0.0000,  0.0000,  0.0000,  0.0000,  0.0000,  0.0000,  0.0000,  0.0000,  0.0000,  0.0000,  0.0000,  0.0000,  0.0000,  0.0000,  0.0000,  0.0000,  0,       0,       0.    )},  // 4 - ç„´•¢Î• ØÆ‚•≠Ê®†´Î

		 {new SP(  1.7855, -0.1737,  1.4041,  1.6278,  0.9456,  1.2003,  2.6406,  1.4318,  0.1009, -0.3203, -0.0192, -0.9396, -0.6972, -0.4438, -0.7666, -0.5132, -0.0362, -0.3542, -0.4853, -0.8504,  0.0000,  5.4041, -6.4273, -6.4466),   // 0 - éØ‚®¨†´Ï≠Æ• Sw-0.02/t^2
		  new SP(  1.1684, -0.1137,  0.9188,  1.0652,  0.6188,  0.7854,  1.7279,  0.9369,  0.0660, -0.2096, -0.0126, -0.6148, -0.4562, -0.2904, -0.5016, -0.3358, -0.0237, -0.2318, -0.3176, -0.5565,  0.0000,  3.5363, -4.2058, -4.2185),   // 1 - éØ‚®¨†´Ï≠Æ• Sw50 && auc ØÆ´„Á•≠Æ ÆØ‚®¨®ß†Ê®•© ‚•¨Ø•‡†‚„‡Î
		  new SP(  2.3572, -0.1967,  1.5534,  1.9840,  1.1677,  1.3736,  3.0945,  1.5248, -0.0741, -0.5569, -0.1807, -1.0801, -0.6953, -0.7213, -0.9058, -0.8194, -0.0933, -0.3896, -0.5842, -1.1795,  0.0000,  5.3091,  3.0649,  1.1571),   // 2 - éÊ•≠™® „·‡•§≠Ò≠≠Î• ØÆ ØÆß®Ê®Ô¨. éØ‚®¨†´Ï≠Æ• Sw-0.02/t^2
		  new SP(  1.5956, -0.1331,  1.0515,  1.3430,  0.7904,  0.9298,  2.0947,  1.0322, -0.0502, -0.3770, -0.1223, -0.7311, -0.4707, -0.4883, -0.6132, -0.5547, -0.0632, -0.2637, -0.3955, -0.7984,  0.0000,  3.5938,  2.0747,  0.7833),   // 3 - éÊ•≠™® „·‡•§≠Ò≠≠Î• ØÆ ØÆß®Ê®Ô¨. éØ‚®¨†´Ï≠Æ• Sw50 && auc ØÆ´„Á•≠Æ ÆØ‚®¨®ß†Ê®•© ‚•¨Ø•‡†‚„‡Î
		  new SP(  0.0000,  0.0000,  0.0000,  0.0000,  0.0000,  0.0000,  0.0000,  0.0000,  0.0000,  0.0000,  0.0000,  0.0000,  0.0000,  0.0000,  0.0000,  0.0000,  0.0000,  0.0000,  0.0000,  0.0000,  0.0000,  0,       0,       0.    )},  // 4 - ç„´•¢Î• ØÆ‚•≠Ê®†´Î

		 {new SP(  1.6559,  0.1355,  1.5356,  1.4983,  1.0375,  1.1006,  2.3728,  1.5854,  0.0436, -0.1797, -0.0344, -0.9096, -0.6062, -0.3939, -0.7278, -0.5486, -0.0166, -0.4009, -0.5519, -0.8824,  0.0000,  5.2553, -6.5076, -6.3774),   // 0 - éØ‚®¨†´Ï≠Æ• Sw-0.02/t^2
		  new SP(  1.0980,  0.0899,  1.0183,  0.9935,  0.6880,  0.7298,  1.5734,  1.0513,  0.0289, -0.1192, -0.0228, -0.6032, -0.4020, -0.2612, -0.4826, -0.3638, -0.0110, -0.2658, -0.3660, -0.5851,  0.0000,  3.4848, -4.3152, -4.2289),   // 1 - éØ‚®¨†´Ï≠Æ• Sw50 && auc ØÆ´„Á•≠Æ ÆØ‚®¨®ß†Ê®•© ‚•¨Ø•‡†‚„‡Î
		  new SP(  2.1239,  0.0697,  1.7384,  1.8623,  1.2839,  1.2617,  2.8732,  1.7961, -0.0715, -0.4354, -0.1518, -1.0900, -0.6191, -0.5098, -0.8548, -0.8930, -0.0795, -0.5002, -0.6018, -1.1907,  0.0000,  5.0979,  2.7147,  0.8467),   // 2 - éÊ•≠™® „·‡•§≠Ò≠≠Î• ØÆ ØÆß®Ê®Ô¨. éØ‚®¨†´Ï≠Æ• Sw-0.02/t^2
		  new SP(  1.3666,  0.0448,  1.1186,  1.1983,  0.8261,  0.8118,  1.8487,  1.1557, -0.0460, -0.2802, -0.0977, -0.7013, -0.3984, -0.3280, -0.5500, -0.5746, -0.0512, -0.3218, -0.3872, -0.7661,  0.0000,  3.2802,  1.7467,  0.5448),   // 3 - éÊ•≠™® „·‡•§≠Ò≠≠Î• ØÆ ØÆß®Ê®Ô¨. éØ‚®¨†´Ï≠Æ• Sw50 && auc ØÆ´„Á•≠Æ ÆØ‚®¨®ß†Ê®•© ‚•¨Ø•‡†‚„‡Î
		  new SP(  0.0000,  0.0000,  0.0000,  0.0000,  0.0000,  0.0000,  0.0000,  0.0000,  0.0000,  0.0000,  0.0000,  0.0000,  0.0000,  0.0000,  0.0000,  0.0000,  0.0000,  0.0000,  0.0000,  0.0000,  0.0000,  0,       0,       0.    )}}; // 4 - ç„´•¢Î• ØÆ‚•≠Ê®†´Î

		 // 0 - éØ‚®¨†´Ï≠Æ• Sw-0.02/t^2
		 // 1 - éØ‚®¨†´Ï≠Æ• Sw50 && auc ØÆ´„Á•≠Æ ÆØ‚®¨®ß†Ê®•© ‚•¨Ø•‡†‚„‡Î
		 // 2 - éÊ•≠™® „·‡•§≠Ò≠≠Î• ØÆ ØÆß®Ê®Ô¨. éØ‚®¨†´Ï≠Æ• Sw-0.02/t^2
		 // 3 - éÊ•≠™® „·‡•§≠Ò≠≠Î• ØÆ ØÆß®Ê®Ô¨. éØ‚®¨†´Ï≠Æ• Sw50 && auc ØÆ´„Á•≠Æ ÆØ‚®¨®ß†Ê®•© ‚•¨Ø•‡†‚„‡Î
		 // 4 - ç„´•¢Î• ØÆ‚•≠Ê®†´Î

		int i;
		if(pat>2 || pat<0) pat=2;
		PATTERN_T = pat;
		if(pat==0) gpt=0;
		if(pat==1) gpt=1;
		if(pat==2) gpt=GPT;
		if(k<0 || k>=K_MAX) k=K_MAX-1;
		for(i=0; i<=20; i++) ac_elong[i] = sp[pat][k].ac_elong[i];
		a_init = sp[pat][k].a_init;
		fs_n   = sp[pat][k].fs_n;
		fs_c   = sp[pat][k].fs_c;
	}
	
	/**
	 * 
	 * @return
	 */
	double Unt_Pot() {
		int j;
		double tunt;
		tunt=0;
		for(j=0; j<=20; j++) { tunt += wac_elong[j] * ac_elong[j] * ac_elong[j]; }
		tunt += wa_init * a_init * a_init;
		tunt += wa_fs   * fs_n   * fs_n;
		tunt += wa_fs   * fs_c   * fs_c;
		tunt = Math.sqrt(tunt);
		return(tunt);
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
	
	void Predict(SEQ_AC sa) {
		int i;
		//printf("Predict %s %d\n", sa.info, sa.calc_pattern);
		if(sa.calc_pattern==0) Pred_Pattern(sa);
		PredictNC(sa);
		PredictCN(sa);
		for(i=0; i<sa.length(); i++) { sa.ac[i].CalcP(); }
	}
	
	void Predict_sh(SEQ_AC sa) {
		int i;
		Pred_Pattern(sa);
		for(i=0; i< sa.length(); i++) {
			if(sa.ac[i].ept < 0.5) {
				sa.ac[i].p3d = 0.5;
				sa.ac[i].plp = 0.5;
			} else {
				sa.ac[i].p3d = 0;
				sa.ac[i].plp = 1;
			}
		}
	}
	
	void Pred_Pattern(SEQ_AC sa) {
		int im, l, i;
		
		for(i=0; i < sa.ac.length; i++) { sa.ac[i].ept=0; }
		sa.calc_pattern = 1;
		
		if(PATTERN_T == 0) {
			sa.calc_pattern = 1;
			return;
		}
		im = sa.ac.length;
		for(l=0; l<gpt; l++) for(i=0; i<im; i++) {
			Check_Pattern(sa, i, im, pt_all[l]);
		}
	}
	
	void PredictNC(SEQ_AC sa) {
		int i, ia;
		if(sa.length()<1) { return; }
		i = 0;
		ia = sa.ac[i].ind_ac;
		sa.ac[i].e3d_n = sa.ac[i].ept;
		sa.ac[i].elp_n = unt * (ac_elong[ia] + fs_n);
		for(i=1; i<sa.length(); i++) {
			ia  = sa.ac[i].ind_ac;
			sa.ac[i].e3d_n = E_Add(sa.ac[i-1].e3d_n,  sa.ac[i-1].elp_n + unt * a_init) + sa.ac[i].ept;
			sa.ac[i].elp_n = E_Add(sa.ac[i-1].elp_n,  sa.ac[i-1].e3d_n + unt * a_init) + unt * ac_elong[ia];
		}
	}
	
	void PredictCN(SEQ_AC sa) {
		int i, iap;
		if(sa.length()<1) { return; }
		i = sa.length()-1;
		sa.ac[i].e3d_c = 0;
		sa.ac[i].elp_c = unt * fs_c;
		for(i=sa.length()-2; i>=0; i--) {
			iap = sa.ac[i+1].ind_ac;

			sa.ac[i].e3d_c = E_Add(sa.ac[i+1].e3d_c + sa.ac[i+1].ept, sa.ac[i+1].elp_c + unt*(a_init + ac_elong[iap]));
			sa.ac[i].elp_c = E_Add(sa.ac[i+1].elp_c + unt * ac_elong[iap],    sa.ac[i+1].e3d_c + unt*a_init + sa.ac[i+1].ept);
		}
	}
	
	double E_Add(double a, double b) {
		double rz;
		if(b<a) return(E_Add(b,a));
		b-=a;
		if(b>100) return(a);
		rz = a - Math.log(1. + Math.exp(-b));
		return(rz);
	}

	void Check_Pattern(SEQ_AC sa, int i0, int im, String pt) {
		int i,j, k;
		if(im-i0 < pt.length())  { return; } // i=i0;i<im
		j=0;
		i=i0+j;
		if(sa.ac[i].ac != pt.charAt(j)) { return; }
		j++;
		i=i0+j;
		if(sa.ac[i].ac != pt.charAt(j)) { return; }
		j=pt.length()-1;
		i=i0+j;
		if(sa.ac[i].ac != pt.charAt(j)) { return; }
		j--; 
		i=i0+j;
		if(sa.ac[i].ac != pt.charAt(j)) { return; }
		for(k=0,i=i0,j=0; j<pt.length(); i++,j++) {
			if(pt.charAt(j) == 'X') continue;
			if(sa.ac[i].ac == pt.charAt(j)) {
				k++;
			} else { 
				k-=CH_PATTERN;
			}
			if(k<-pt.length()) { return; }
	  }
	//if(k<pt.n) return;
	if(k<=0) { return; }
	//for(i=i0,j=0; j<pt.n; i++,j++) sa[i].ept=E_PATTERN;
	im = i0+pt.length();
	//if(im <= 40) i0=0;
	//if(i0+40 >= sa.length()) im=sa.length();
	if(i0 < 40) { i0=0; }
	if(sa.length()-im < 40) { im=sa.length(); }
		for(i=i0; i<im; i++) { sa.ac[i].ept=E_PATTERN; }
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
