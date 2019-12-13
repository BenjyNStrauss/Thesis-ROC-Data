package chem;

/**
 * Represents an element from 1 to 118
 * Also includes values for isotopes Deuterium, Tritium,
 * and for exotic atoms Positronium, Muonium, Tauonium
 * 
 * @author Benjy Strauss
 *
 */

public enum Element {
	H, He, Li, Be, B, C, N, O, F, Ne, Na, Mg, Al, Si, P, S, Cl, Ar,
	K, Ca, Sc, Ti, V, Cr, Mn, Fe, Co, Ni, Cu, Zn, Ga, Ge, As, Se, Br, Kr,
	Rb, Sr, Y, Zr, Nb, Mo, Tc, Ru, Rh, Pd, Ag, Cd, In, Sn, Sb, Te, I, Xe,
	Cs, Ba, La, Ce, Pr, Nd, Pm, Sm, Eu, Gd, Tb, Dy, Ho, Er, Tm, Yb, Lu, Hf, Ta, W, Re, Os, Ir, Pt, Au, Hg, Tl, Pb, Bi, Po, At, Rn,
	Fr, Ra, Ac, Th, Pa, U, Np, Pu, Am, Cm, Bk, Cf, Es, Fm, Md, No, Lr, Rf, Db, Sg, Bh, Hs, Mt, Ds, Rg, Cn, Nh, Fl, Mc, Lv, Ts, Og,
	D, T,// Deuterium and Tritium
	Ps, µ, τ, //Positronium, Muonium, Tauonium
	UNKNOWN;

	public Element parseElement(String str) {
		str = str.toLowerCase().trim();
		
		switch(str) {
		case "h":
		case "hydrogen": 		return H;
		case "he":
		case "helium": 			return He;
		case "li":
		case "lithium":	 		return Li;
		case "be":
		case "beryllium": 		return Be;
		case "b":
		case "boron": 			return B;
		case "c":
		case "carbon": 			return C;
		case "n":
		case "nitrogen": 		return N;
		case "o":
		case "oxygen": 			return O;
		case "f":
		case "fluorine":			return F;
		case "ne":
		case "neon":				return Ne;
		case "na":
		case "sodium":			return Na;
		case "mg":
		case "magnesium":		return Mg;
		case "al":
		case "aluminum":			return Al;
		case "si":
		case "silicon":			return Si;
		case "p":
		case "phosphorus":		return P;
		case "cl":
		case "chlorine":			return Cl;
		case "ar":
		case "argon":			return Ar;
		case "k":
		case "potassium":		return K;
		case "ca":
		case "calcium":			return Ca;
		case "sc":
		case "scandium":			return Sc;
		case "ti":
		case "titanium":			return Ti;
		case "v":
		case "vanadium":			return V;
		case "cr":
		case "chromium":			return Cr;
		case "mn":
		case "manganese":		return Mn;
		case "fe":
		case "iron":				return Fe;
		case "co":
		case "cobalt":			return Co;
		case "ni":
		case "copper":			return Ni;
		case "zn":
		case "zinc":				return Zn;
		case "ga":
		case "gallium":			return Ga;
		case "ge":
		case "germanium":		return Ge;
		case "as":
		case "arsenic":			return As;
		case "se":
		case "selenium":			return Se;
		case "br":
		case "bromine":			return Br;
		case "kr":
		case "krypton":			return Kr;
		case "rb":
		case "rubidium":			return Rb;
		case "st":
		case "strontium":		return Sr;
		case "y":
		case "yttrium":			return Y;
		case "zi":
		case "zirconium":		return Zr;
		case "nb":
		case "niobium":			return Nb;
		case "mo":
		case "molybdenum	":		return Mo;
		case "tc":
		case "technetium	":		return Tc;
		case "ru":
		case "ruthenium":		return Ru;
		case "rh":
		case "rhodium":			return Rh;
		case "pd":
		case "palladium":		return Pd;
		case "ag":
		case "silver":			return Ag;
		case "cd":
		case "cadmium":			return Cd;
		case "in":
		case "indium":			return In;
		case "sn":
		case "tin":				return Sn;
		case "sb":
		case "antimony":			return Sb;
		case "te":
		case "tellurium":		return Te;
		case "i":
		case "iodine":			return I;
		case "xe":
		case "xenon":			return Xe;
		case "cs":
		case "caesium":			return Cs;
		case "ba":
		case "barium":			return Ba;
		case "la":
		case "lanthanum":		return La;
		case "ce":
		case "cerium":			return Ce;
		case "pr":
		case "praseodymium":		return Pr;
		case "nd":
		case "neodymium":		return Nd;
		case "pm":
		case "promethium":		return Pm;
		case "sm":
		case "samarium":			return Sm;
		case "eu":
		case "europium":			return Eu;
		case "gd":
		case "gadolinium":		return Gd;
		case "tb":
		case "terbium":			return Tb;
		case "dy":
		case "dysprosium":		return Dy;
		case "ho":
		case "holmium":			return Ho;
		case "er":
		case "erbium":			return Er;
		case "tm":
		case "thulium":			return Tm;
		case "yb":
		case "ytterbium":		return Yb;
		case "lu":
		case "lutetium":			return Lu;
		case "hf":
		case "hafnium":			return Hf;
		case "ta":
		case "tantalum":			return Ta;
		case "w":
		case "tungsten":			return W;
		case "re":
		case "rhenium":			return Re;
		case "os":
		case "osmium":			return Os;
		case "ir":
		case "iridium":			return Ir;
		case "pt":
		case "platinum":			return Pt;
		case "au":
		case "gold":				return Au;
		case "hg":
		case "mercury":			return Hg;
		case "tl":
		case "thallium":			return Tl;
		case "pb":
		case "lead":				return Pb;
		case "bi":
		case "bismuth":			return Bi;
		case "po":
		case "polonium":			return Po;
		case "at":
		case "astatine":			return At;
		case "rn":
		case "radon":			return Rn;
		case "fr":
		case "francium":			return Fr;
		case "ra":
		case "radium":			return Ra;
		case "ac":
		case "actinium":			return Ac;
		case "th":
		case "thorium":			return Th;
		case "pa":
		case "protactinium":		return Pa;
		case "u":
		case "uranium":			return U;
		case "np":
		case "neptunium":		return Np;
		case "pu":
		case "plutonium":		return Pu;
		case "am":
		case "americium":		return Am;
		case "cm":
		case "curium":			return Cm;
		case "bk":
		case "berkelium":		return Bk;
		case "cf":
		case "californium":		return Cf;
		case "es":
		case "einsteinium":		return Es;
		case "fm":
		case "fermium":			return Fm;
		case "md":
		case "mendelevium":		return Md;
		case "nobelium":			return No;
		case "lr":
		case "lawrencium":		return Lr;
		case "rf":
		case "rutherfordium":	return Rf;
		case "db":
		case "dubnium":			return Db;
		case "sg":
		case "seaborgium":		return Sg;
		case "bh":
		case "bohrium":			return Bh;
		case "hs":
		case "hassium":			return Hs;
		case "mt":
		case "meitnerium":		return Mt;
		case "ds":
		case "darmstadtium":		return Ds;
		case "rg":
		case "roentgenium":		return Rg;
		case "cn":
		case "copernicium":		return Cn;
		case "nh":
		case "nihonium":			return Nh;
		case "fl":
		case "flerovium":		return Fl;
		case "mc":
		case "moscovium":		return Mc;
		case "lv":
		case "livermorium":		return Lv;
		case "ts":
		case "tennessine":		return Ts;
		case "og":
		case "oganesson":		return Og;
		case "d":
		case "deuterium":		return D;
		case "t":
		case "tritium":			return T;
		case "ps":
		case "positronium":		return Ps;
		case "µ":
		case "muonium":			return µ;
		case "τ":
		case "tauonium":			return τ;
		default:					return UNKNOWN;
		}
	}
}
