package bio;

import java.util.List;

import bio.exceptions.UnknownCodeException;
import bio.exceptions.UnrecognizedParameterException;

/**
 * Represents an Animo Acid residue type
 * This enum attempts to have all residue types represented, including modified and
 * uncommon residues, to allow precision where necessary.  Since different sources
 * only recognize certain residue types, the "couldBe()" method is used to determine if
 * two sources have the same information but just display it differently.
 * 
 * As I, the author, learn of new modified residue types, I will update this enum and the
 * methods it contains.
 * 
 * @author Benjy Strauss
 *
 */

public enum ResidueType {
	Alanine, Asparagine, AsparticAcid, Arginine, Cysteine, Glutamine, Glycine,
	GlutamicAcid, Histidine, Isoleucine, Lysine, Leucine, Phenylalanine, Methionine,
	Serine, Proline, Tryptophan, Threonine, Tyrosine, Valine,
	Selenocysteine, Pyrrolysine,
	OTHER, ANY, BAD,
	
	//more uncommon residues
	_2_Aminoadipic_Acid, _3_Aminoadipic_Acid, BetaAlanine, _2_Aminobutyric_Acid,
	Piperidinic_Acid, _6_Aminocaproic_Acid, _2_Aminoheptanoic_Acid, _2_Aminoisobutyric_Acid,
	_3_Aminoisobutyric_Acid, _2_Aminopimelic_Acid, _2_4_Diaminobutyric_Acid, Desmosine,
	_2_2_Diaminopimelic_Acid, _2_3_Diaminoproprionic_Acid, N_Ethylglycine, N_Ethylasparagine,
	Hydroxylysine, Allo_Hydroxylysine, _3_Hydroxyproline, _4_Hydroxyproline,
	Isodesmosine, Allo_Isoleucine, N_Methylglycine, N_Methylisoleucine,
	_6_N_Methyllysine, N_Methylvaline, Norvaline, Norleucine,
	Ornithine,
	
	Selenomethionine, //MSE
	N6_Accetyllysine;
	
	/**
	 * Determines what Amino Acid of the Proteinogenic 22 the character represents
	 * So far, only the Proteinogenic 22 amino acids have one-character representations
	 * @param c: the character to look up
	 * 
	 * @return: the corresponding instance of the enum
	 * @throws: UnrecognizedParameterException: if the character entered does not correspond
	 * to any known type of animo acid
	 */
	public static ResidueType letterLookup(char c) {
		c = Character.toUpperCase(c);
		
		switch(c) {
		case 'A': return Alanine;
		case 'B': break;
		case 'C': return Cysteine;
		case 'D': return AsparticAcid;
		case 'E': return GlutamicAcid;
		case 'F': return Phenylalanine;
		case 'G': return Glycine;
		case 'H': return Histidine;
		case 'I': return Isoleucine;
		case 'J': break;
		case 'K': return Lysine;
		case 'L': return Leucine;
		case 'M': return Methionine;
		case 'N': return Asparagine;
		case 'O': return Pyrrolysine;
		case 'P': return Proline;
		case 'Q': return Glutamine;
		case 'R': return Arginine;
		case 'S': return Serine;
		case 'T': return Threonine;
		case 'U': return Selenocysteine;
		case 'V': return Valine;
		case 'W': return Tryptophan;
		case 'X': return ANY;
		case 'Y': return Tyrosine;
		case 'Z': break;
		case '_': return null;
		default:
		}
		
		throw new UnrecognizedParameterException(c);
	}
	
	/**
	 * Get's the character representing the amino acid
	 * Non-canonical bases are represented by the closest letter
	 * 
	 * @return the amino acid's letter representation
	 */
	public char toChar() {
		switch(this) {
		case BetaAlanine:
		case Alanine: 				return 'A';
		case Cysteine:				return 'C';
		case N_Ethylasparagine:
		case AsparticAcid:			return 'D';
		case GlutamicAcid:			return 'E';
		case Phenylalanine:			return 'F';
		case N_Ethylglycine:
		case N_Methylglycine:
		case Glycine:				return 'G';
		case Histidine:				return 'H';
		case Allo_Isoleucine:
		case N_Methylisoleucine:
		case Isoleucine:				return 'I';
		case Norleucine:
		case Leucine:				return 'L';
		case N6_Accetyllysine:
		case Hydroxylysine:
		case Allo_Hydroxylysine:
		case _6_N_Methyllysine:
		case Lysine:					return 'K';
		case Selenomethionine:
		case Methionine:				return 'M';
		case Asparagine:				return 'N';
		case Pyrrolysine:			return 'O';
		case _3_Hydroxyproline:
		case _4_Hydroxyproline:
		case Proline:				return 'P';
		case Glutamine:				return 'Q';
		case Arginine:				return 'R';
		case Serine:					return 'S';
		case Threonine:				return 'T';
		case Selenocysteine:			return 'U';
		case N_Methylvaline:
		case Norvaline:
		case Valine:					return 'V';
		case Tryptophan:				return 'W';
		case Tyrosine:				return 'Y';
		case BAD:					return '*';
		default:						return 'X';
		}
	}
	
	/**
	 * Obtains the Amino Acid's code as used by GenBank:
	 * http://www.insdc.org/files/feature_table.html#7.4.4
	 * But uses "Xaa" instead of "OTHER"
	 * 
	 * Also includes Selenoethionine from RCSB-PDB as Mse
	 * 
	 * @return: Genbank Amino Acid Code
	 */
	public String toCode() {
		switch(this) {
		case Alanine: 				return "Ala";
		case Arginine:				return "Arg";
		case Asparagine:				return "Asn";
		case AsparticAcid:			return "Asp";
		case Cysteine:				return "Cys";
		case GlutamicAcid:			return "Gln";
		case Glutamine:				return "Glu";
		case Glycine:				return "Gly";
		case Histidine:				return "His";
		case Isoleucine:				return "Ile";
		case Leucine:				return "Leu";
		case Lysine:					return "Lys";
		case Methionine:				return "Met";
		case Phenylalanine:			return "Phe";
		case Proline:				return "Pro";
		case Pyrrolysine:			return "Pyl";
		case Selenocysteine:			return "Sec";
		case Serine:					return "Ser";
		case Threonine:				return "Thr";
		case Tryptophan:				return "Trp";
		case Tyrosine:				return "Tyr";
		case Valine:					return "Val";
		
		case BAD:					return "BAD";
		
		case _2_Aminoadipic_Acid:						return "Aad";
		case _3_Aminoadipic_Acid:						return "bAad";
		case BetaAlanine:								return "bAla";
		case _2_Aminobutyric_Acid:						return "Abu";
		case Piperidinic_Acid:							return "4Abu";
		case _6_Aminocaproic_Acid:						return "Acp";
		case _2_Aminoheptanoic_Acid:						return "Ahe";
		case _2_Aminoisobutyric_Acid:					return "Aib";
		case _3_Aminoisobutyric_Acid:					return "bAib";
		case _2_Aminopimelic_Acid:						return "Apm";
		case _2_4_Diaminobutyric_Acid:					return "Dbu";
		case Desmosine:									return "Des";
		case _2_2_Diaminopimelic_Acid:					return "Dpm";
		case _2_3_Diaminoproprionic_Acid:				return "Dpr";
		case N_Ethylglycine:								return "EtGly";
		case N_Ethylasparagine:							return "EtAsn";
		case Hydroxylysine:								return "Hyl";
		case Allo_Hydroxylysine:							return "aHyl";
		case _3_Hydroxyproline:							return "3Hyp";
		case _4_Hydroxyproline:							return "4Hyp";
		case Isodesmosine:								return "Ide";
		case Allo_Isoleucine:							return "aIle";
		case N_Methylglycine:							return "MeGly";
		case N_Methylisoleucine:							return "MeIle";
		case _6_N_Methyllysine:							return "MeLys";
		case N_Methylvaline:								return "MeVal";
		case Norvaline:									return "Nva";
		case Norleucine:									return "Nle";
		case Ornithine:									return "Orn";
		
		case Selenomethionine:		return "Mse";
		case N6_Accetyllysine:		return "Xaa";
		case ANY:					return "Xaa";
		default:						return "OTHER";
		}
	}
	
	/**
	 * Note to coder: cases for duplicate return values are indented
	 * 
	 * Parses a 3-letter code into a residue type
	 * @param code: the string to parse
	 * @return: the residue type value
	 * @throws UnknownCodeException 
	 */
	public static ResidueType parseCode(String code) throws UnknownCodeException {
		code = code.toUpperCase();
		
		switch(code) {
		case "ALA":				return Alanine;
		case "ARG":				return Arginine;
		case "ASN":				return Asparagine;
		case "ASP":				return AsparticAcid;
		case "CYS":				return Cysteine;
		case "GLN":				return Glutamine;
		case "GLU":				return GlutamicAcid;
		case "GLY":				return Glycine;
		case "HIS":				return Histidine;
		case "ILE":				return Isoleucine;
		case "LEU":				return Leucine;
		case "LYS":				return Lysine;
		case "MET":				return Methionine;
		case "PHE":				return Phenylalanine;
		case "PRO":				return Proline;
		case "PYL":				return Pyrrolysine;
		case "SEC":				return Selenocysteine;
		case "SER":				return Serine;
		case "THR":				return Threonine;
		case "TRP":				return Tryptophan;
		case "TYR":				return Tyrosine;
		case "VAL":				return Valine;
		case "XAA":				return ANY;
		case "AAD":				return _2_Aminoadipic_Acid;
		case "BAAD":				return _3_Aminoadipic_Acid;
		case "BALA":				return BetaAlanine;
		case "ABU":				return _2_Aminobutyric_Acid;
		case "4ABU":				return Piperidinic_Acid;
		case "ACP":				return _6_Aminocaproic_Acid;
		case "AHE":				return _2_Aminoheptanoic_Acid;
		case "AIB":				return _2_Aminoisobutyric_Acid;
		case "BAIB":				return _3_Aminoisobutyric_Acid;
		case "APM":				return _2_Aminopimelic_Acid;
		case "DBU":				return _2_4_Diaminobutyric_Acid;
		case "DES":				return Desmosine;
		case "DPM":				return _2_2_Diaminopimelic_Acid;
		case "DPR":				return _2_3_Diaminoproprionic_Acid;
		case "ETGLY":			return N_Ethylglycine;
		case "ETASN":			return N_Ethylasparagine;
		case "HYL":				return Hydroxylysine;
		case "AHYL":				return Allo_Hydroxylysine;
		case "HY3":
		case "3HYP":				return _3_Hydroxyproline;
		case "HYP":
		case "4HYP":				return _4_Hydroxyproline;
		case "IDE":				return Isodesmosine;
		case "AILE":				return Allo_Isoleucine;
		case "MEGLY":			return N_Methylglycine;
		case "MEILE":			return N_Methylisoleucine;
		case "MELYS":			return _6_N_Methyllysine;
		case "MEVAL":			return N_Methylvaline;
		case "NVA":				return Norvaline;
		case "NLE":				return Norleucine;
		case "ORN":				return Ornithine;
		
		case "MSE":				return Selenomethionine;
		case "UNK":				return OTHER;
		//this isn't a biological term, just used for performance optimization.
		case "BAD":				return BAD;
		default:
			throw new UnknownCodeException(code);
		}
	}
	
	/**
	 * Tells whether the ResidueType is a member of the Standard 20 Amino Acids
	 * The Standard 20 are the Amino Acids coded for in DNA, not including
	 * Pyrrolysine and Selenocysteine
	 * 
	 * @return whether the amino acid is part of the Standard 20
	 */
	public boolean isStandard() {
		switch(this) {
		case Alanine: 				return true;
		case Arginine:				return true;
		case Asparagine:				return true;
		case AsparticAcid:			return true;
		case Cysteine:				return true;
		case GlutamicAcid:			return true;
		case Glutamine:				return true;
		case Glycine:				return true;
		case Histidine:				return true;
		case Isoleucine:				return true;
		case Leucine:				return true;
		case Lysine:					return true;
		case Methionine:				return true;
		case Phenylalanine:			return true;
		case Proline:				return true;
		case Serine:					return true;
		case Threonine:				return true;
		case Tryptophan:				return true;
		case Tyrosine:				return true;
		case Valine:					return true;
		default:						return false;
		}
	}
	
	/**
	 * Returns the E6 category of the residue
	 * Does not support non-standard residues
	 * @return: E6 category of this object
	 */
	public E6 e6() {
		switch(this) { 
		case Alanine:
		case Cysteine:
		case Isoleucine:
		case Leucine:
		case Methionine:
		case Valine:
			return E6.ALIPHATIC;//AVLIMC
		case Phenylalanine:
		case Tryptophan:
		case Tyrosine:
		case Histidine:
			return E6.AROMATIC;//"FWYH"
		case Asparagine:
		case Glutamine:
		case Serine:
		case Threonine:
			return E6.POLAR;//STNQ
		case Arginine:
		case Lysine:
			return E6.NEGATIVE;//KR
		case AsparticAcid:
		case GlutamicAcid:
			return E6.POSITIVE;//DE
		case Glycine:
		case Proline:
			return E6.SPECIAL;//GP
		default:
			return E6.UNACCOUNTED_FOR;
		}
	}
	
	/**
	 * Compares this object with another residue type
	 * Two different derivatives of the same amino acid will be marked as the same
	 * if you want derivatives to be marked differently, use equals()
	 * 
	 * @param other: the other Residue type
	 * @return: Whether this residue type and the parameter are equal
	 */
	public boolean couldBe(ResidueType other) {
		if(toChar() == other.toChar()) { return true; }
		if(this == ANY || other == ANY) { return true; }
		
		if(this == OTHER && !other.isStandard()) { return true; }
		if(!this.isStandard() && other == OTHER) { return true; }
		
		switch(this) {
		//not needed with Pyrrolysine
		case Cysteine:
		case Selenocysteine:
			if(other == Cysteine || other == Selenocysteine) { return true; }
			break;
		case Alanine:
		case BetaAlanine:
			if(other == Alanine || other == BetaAlanine) { return true; }
			break;
		case Glycine:
		case N_Ethylglycine:
		case N_Methylglycine:
			if(other == Glycine || other == N_Ethylglycine || other == N_Methylglycine) { return true; }
			break;
		case AsparticAcid:
		case N_Ethylasparagine:
			if(other == AsparticAcid || other == N_Ethylasparagine) { return true; }
			break;
		case Lysine:
		case Hydroxylysine:
		case Allo_Hydroxylysine:
		case _6_N_Methyllysine:
		case N6_Accetyllysine:
			if(other == Lysine || other == Hydroxylysine || other == Allo_Hydroxylysine 
			|| other == _6_N_Methyllysine || other == N6_Accetyllysine) { return true; }
			break;
		case Proline:
		case _3_Hydroxyproline:
		case _4_Hydroxyproline:
			if(other == Proline || other == _3_Hydroxyproline || other == _4_Hydroxyproline) { return true; }
			break;
		case Isoleucine:
		case Allo_Isoleucine:
		case N_Methylisoleucine:
			if(other == Isoleucine || other == Allo_Isoleucine || other == N_Methylisoleucine) { return true; }
			break;
		case Valine:
		case N_Methylvaline:
		case Norvaline:
			if(other == Valine || other == N_Methylvaline || other == Norvaline) { return true; }
			break;
		case Leucine:
		case Norleucine:
			if(other == Leucine || other == Norleucine) { return true; }
			break;
		case Methionine:
		case Selenomethionine:
			if(other == Methionine || other == Selenomethionine) { return true; }
			break;
		default:
		}
		
		return false;
	}
	
	/**
	 * Determines if two residue types are EXACTLY the same
	 * Different derivatives are not the same:
	 * For example, Methionine is NOT Selenomethionine
	 * If you want a method where derivatives would be marked the same, use couldBe()
	 * 
	 * @param other: the other Residue type
	 * @return: Whether this residue type and the parameter are equal
	 */
	public boolean equals(ResidueType other) {
		if(this == other) {
			return true;
		} else {
			return false;
		}
	}
	
	public String toString() {
		String retVal = super.toString();
		retVal = retVal.replaceAll("_", " ");
		return retVal.trim();
	}
	
	/**
	 * qp stands for quick-print
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

