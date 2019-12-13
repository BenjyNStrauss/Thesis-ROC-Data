package bioUI;

import java.io.IOException;
import java.util.ArrayList;
import java.util.BitSet;

import analysis.RegressionManager;
import analysis.ResultsFusionModule;
import analysis.stats.ROCRecord;
import analysis.stats.RegressionResult;
import bio.AlignedCluster;
import bio.AminoAcid;
import bio.DataSource;
import bio.ProteinChain;
import bio.ResidueType;
import bio.SecondaryStructure;
import bio.tools.BioLookup;
import bio.tools.CSVWriter;
import bio.tools.ClusterReader;
import bio.tools.FASTA;
import bio.tools.SequenceAligner;
import util.BaseTools;
import util.LaTeXTableConverter;

/**
 * A class used to quickly test functions in other classes
 * 
 * @author Benjy Strauss
 *
 */

@SuppressWarnings("unused")
public class QuickMain extends BaseTools {
	
	private static final String[] SEQUENCE_BANK = {
			"SNAMNSQLTLRALERGDLRFIHNLNNNRNIMSYWFEEPYESFDELEELYNKHIHDNAERRFVVEDAQKNLIGLVELIEINYIHRSAEFQIIIAPEHQGKGFARTLINRAL",
			"__MNSQLTLRALERGDLRFIHNLNNNRNIMSYWFEEPYESFDELEEIYNKHIHDNAERRFLIGLVELIEINYIHRSAEFQIIIAPEHQGKGFARTLINRAL",
			"SNAMNSQLTLRALERGDLRFIHNLNNNRNIMSYWFEEPYESFDELEELYNKHIHDNAERRFVVEDAQKNLIGLVEIHRSAEFQIIIAPEHQGKGFARTLINRAL",
			"FFGFRRESESNAMNSQLTLRALERGDLRFIHNLNNNRNIMSYWFEEPYESFDELEELYNKHIHDNAERRFVVEDAQKNLIGLVELIEINYIHRSAEFQIIIAPEHQGKGFARTLINRAL",
			"SNAMNSQLTLRALERGDLRFIHNLNNNRNIMSYWFEEPYEEEEEELYNKHIHDNAERRFVVEDAQKNLIGLVELIEINYIHRSAEFQIIIAPEHQGKGFARTLINRAL",
			"SNAMNSQLTLRALERGDLRFIHNLNNNRNIMSYWFEEPYESFNKHIHDNAERRFVVEDAQKNLIGLVELIEINYIHRSAEFQIIIAPEHQGKGFARTLINRAL",
	};
	
	private static final String TEST_NAME = "FAKE";
	private static final String _1TIQ_SEQ = "MSVKMKKCSREDLQTLQQLSIETFNDTFKEQNSPENMKAYLESAFNTEQLEKELSNMSSQFFFIYFDHEIAGYVKVNIDDAQSEEMGAESLEIERIYIKNSFQKHGLGKHLLNKAIEIALERNKKNIWLGVWEKNENAIAFYKKMGFVQTGAHSFYMGDEEQTDLIMAKTLILEHHHHHH";
	private static final String _4E2A_SEQ = "MGSSHHHHHHSSGLVPRGSHMASMTGGQQMGRGSMSQVEIRKVNQDELSLLQKIAIQTFRETFAFDNTAEQLQNFFDEAYTLSVLKLELDDKESETYFILMSGKAAGFLKVNWGSSQTEQVLEDAFEIQRLYILKAYQGLGLGKQLFEFALERAQISGLSWVWLGVWEKNVKAQLLYAKYGFEQFSKHSFFVGNKVDTDWLLKKSLS";
	private static final String _2FL4_SEQ = "GMEIHFEKVTSDNRKAVENLQVFAEQQAFIESMAENLKESDQFPEWESAGIYDGNQLIGYAMYGRWQDGRVWLDRFLIDQRFQGQGYGKAACRLLMLKLIEKYQTNKLYLSVYDTNSSAIRLYQQLGFVFNGELDTNGERVMEWTHQNK";
	private static ArrayList<ProteinChain> myChains;

	public static void main(String[] args) throws Exception  {
		BioLookup.setDataSource(DataSource.UNIPROT);
		
		filterPrint("output/test-uniprot-v4-ta-logreg-roc-log.csv");
		//analyze_isu_sa();
		//processROC();
		//buildLatexTables();
		
		System.exit(0);
	}
	
	private static void filterPrint(String file) {
		ArrayList<String> filteredLines = new ArrayList<String>();
		String[] lines = BaseTools.getFileLines(file);
		for(String line: lines) {
			if(line.contains(",VK ,")) {
				filteredLines.add(line);
			}
		}
		BaseTools.writeFileLines("output/vkabat-filtered.csv", filteredLines);
	}
	
	private static void analyze_isu_sa() {
		setDebug(true);
		Stats.loadData("output/test-uniprot-v4-sa-logreg.csv");
		Stats.rocFit(0.00001);
		Stats.loadData("output/test-uniprot-v4-ta-logreg.csv");
		Stats.rocFit(0.00001);
	}
	
	private static void adjustROCRecords() { 
		ROCRecord.organizeROCRecordFile("output/affect/test-uniprot-v4-sa-logreg-roc-log.csv", "output/affect/test-uniprot-v4-sa-newlog.csv");
		ROCRecord.organizeROCRecordFile("output/affect/test-uniprot-v4-sb-logreg-roc-log.csv", "output/affect/test-uniprot-v4-sb-newlog.csv");
		ROCRecord.organizeROCRecordFile("output/affect/test-uniprot-v4-su-logreg-roc-log.csv", "output/affect/test-uniprot-v4-su-newlog.csv");
		ROCRecord.organizeROCRecordFile("output/affect/test-uniprot-v4-ta-logreg-roc-log.csv", "output/affect/test-uniprot-v4-ta-newlog.csv");
		ROCRecord.organizeROCRecordFile("output/affect/test-uniprot-v4-tb-logreg-roc-log.csv", "output/affect/test-uniprot-v4-tb-newlog.csv");
		ROCRecord.organizeROCRecordFile("output/affect/test-uniprot-v4-tu-logreg-roc-log.csv", "output/affect/test-uniprot-v4-tu-newlog.csv");
	}
	
	/**
	 * 
	 */
	private static void buildLatexTables() {
		RegressionResult.setReadMode(SwitchWriteMode.ASSIGNED);
		LaTeXTableConverter.convertModelResultsToTable("regression/models/test-uniprot-v4-sa_model-results.txt", "output/thesis-tables-sa.txt");
		RegressionResult.setReadMode(SwitchWriteMode.BOTH);
		LaTeXTableConverter.convertModelResultsToTable("regression/models/test-uniprot-v4-sb_model-results.txt", "output/thesis-tables-sb.txt");
		RegressionResult.setReadMode(SwitchWriteMode.UNASSIGNED);
		LaTeXTableConverter.convertModelResultsToTable("regression/models/test-uniprot-v4-su_model-results.txt", "output/thesis-tables-su.txt");
		RegressionResult.setTripletMode(true);
		RegressionResult.setReadMode(SwitchWriteMode.ASSIGNED);
		LaTeXTableConverter.convertModelResultsToTable("regression/models/test-uniprot-v4-ta_model-results.txt", "output/thesis-tables-ta.txt");
		RegressionResult.setReadMode(SwitchWriteMode.BOTH);
		LaTeXTableConverter.convertModelResultsToTable("regression/models/test-uniprot-v4-tb_model-results.txt", "output/thesis-tables-tb.txt");
		RegressionResult.setReadMode(SwitchWriteMode.UNASSIGNED);
		LaTeXTableConverter.convertModelResultsToTable("regression/models/test-uniprot-v4-tu_model-results.txt", "output/thesis-tables-tu.txt");
	}
	
	private static void processROC() throws Exception {
		quickRegression();
		quickFusion();
		quickROC();		
	}
	
	private static void quickRegression() {
		RegressionManager.logistic_regression("learn-uniprot-v4-sa.csv", "test-uniprot-v4-sa.csv");
		RegressionManager.logistic_regression("learn-uniprot-v4-sb.csv", "test-uniprot-v4-sb.csv");
		RegressionManager.logistic_regression("learn-uniprot-v4-su.csv", "test-uniprot-v4-su.csv");
		RegressionManager.logistic_regression("learn-uniprot-v4-ta.csv", "test-uniprot-v4-ta.csv");
		RegressionManager.logistic_regression("learn-uniprot-v4-tb.csv", "test-uniprot-v4-tb.csv");
		RegressionManager.logistic_regression("learn-uniprot-v4-tu.csv", "test-uniprot-v4-tu.csv");
	}
	
	private static void quickROC() {
		Stats.loadData("output/test-uniprot-v4-sa-logreg.csv");
		Stats.rocFit();
		Stats.loadData("output/test-uniprot-v4-sb-logreg.csv");
		Stats.rocFit();
		Stats.loadData("output/test-uniprot-v4-su-logreg.csv");
		Stats.rocFit();
		Stats.loadData("output/test-uniprot-v4-ta-logreg.csv");
		Stats.rocFit();
		Stats.loadData("output/test-uniprot-v4-tb-logreg.csv");
		Stats.rocFit();
		Stats.loadData("output/test-uniprot-v4-tu-logreg.csv");
		Stats.rocFit();
	}
	
	private static void quickFusion() throws IOException {
		ResultsFusionModule.CSV_Fusion("test-uniprot-v4-sa.csv", "test-uniprot-v4-sa_exact_pred.csv", "output/test-uniprot-v4-sa-logreg.csv");
		ResultsFusionModule.CSV_Fusion("test-uniprot-v4-sb.csv", "test-uniprot-v4-sb_exact_pred.csv", "output/test-uniprot-v4-sb-logreg.csv");
		ResultsFusionModule.CSV_Fusion("test-uniprot-v4-su.csv", "test-uniprot-v4-su_exact_pred.csv", "output/test-uniprot-v4-su-logreg.csv");
		ResultsFusionModule.CSV_Fusion("test-uniprot-v4-ta.csv", "test-uniprot-v4-ta_exact_pred.csv", "output/test-uniprot-v4-ta-logreg.csv");
		ResultsFusionModule.CSV_Fusion("test-uniprot-v4-tb.csv", "test-uniprot-v4-tb_exact_pred.csv", "output/test-uniprot-v4-tb-logreg.csv");
		ResultsFusionModule.CSV_Fusion("test-uniprot-v4-tu.csv", "test-uniprot-v4-tu_exact_pred.csv", "output/test-uniprot-v4-tu-logreg.csv");
	}
	
	private static void alignmentAlgorithmTest() {
		ProteinChain nonStandardChain = new ProteinChain("DEBU", 'G', "FOOOLERTU");
		for(int i = 0; i < nonStandardChain.length(); ++i) {
			AminoAcid aa = nonStandardChain.getAmino(i);
			
			aa.setSecondaryStructure(SecondaryStructure.EXTENDED_STRAND);
			BioLookup.assignNetAmber95(aa);
			BioLookup.assignChargeValues(aa);
		}
		
		for(int i = 0; i < nonStandardChain.length(); ++i) {
			AminoAcid aa = nonStandardChain.getAmino(i);
			qp("A95: " + aa.toChar() + ": " + aa.amber95());
			qp("Avg: " + aa.toChar() + ": " + aa.averageCharge());
		}
	}
	
	private static void makeOverlays()  { 
		String[] meta = {"", ""};
		Stats.loadData("output/exact_test-uniprot-sb.csv");
		Stats.rocFit(meta);
		Stats.loadData("output/exact_test-uniprot-sa.csv");
		Stats.rocFit(meta);
		Stats.loadData("output/exact_test-uniprot-su.csv");
		Stats.rocFit(meta);
		Stats.loadData("output/exact_test-uniprot-tb.csv");
		Stats.rocFit(meta);
		Stats.loadData("output/exact_test-uniprot-ta.csv");
		Stats.rocFit(meta);
		Stats.loadData("output/exact_test-uniprot-tu.csv");
		Stats.rocFit(meta);
	}
	
	private static void switchMarkTest01() throws Exception {
		ProteinChain _5BTRA = BioLookup.readChainFromFasta("5BTR",'A');
		_5BTRA = BioLookup.assignSecondaryDSSP(_5BTRA);
		
		AlignedCluster debugCluster = new AlignedCluster(_5BTRA);
		
		ProteinChain _5BTRB = BioLookup.readChainFromFasta("5BTR",'B');
		_5BTRB = BioLookup.assignSecondaryDSSP(_5BTRB);
		ProteinChain _5BTRC = BioLookup.readChainFromFasta("5BTR",'C');
		_5BTRC = BioLookup.assignSecondaryDSSP(_5BTRC);
		ProteinChain _4ZZHA = BioLookup.readChainFromFasta("4ZZH",'A');
		_4ZZHA = BioLookup.assignSecondaryDSSP(_4ZZHA);
		ProteinChain _4ZZIA = BioLookup.readChainFromFasta("4ZZI",'A');
		_4ZZIA = BioLookup.assignSecondaryDSSP(_4ZZIA);
		ProteinChain _4ZZJA = BioLookup.readChainFromFasta("4ZZJ",'A');
		_4ZZJA = BioLookup.assignSecondaryDSSP(_4ZZJA);
		
		debugCluster.addChain(_5BTRB);
		debugCluster.addChain(_5BTRC);
		debugCluster.addChain(_4ZZHA);
		debugCluster.addChain(_4ZZIA);
		debugCluster.addChain(_4ZZJA);
		
		String _5BTRA_sec = _5BTRA.toSecondarySequence().replaceAll(" ", "_").replaceAll("G", "H").replaceAll("I", "H");
		String _5BTRB_sec = _5BTRB.toSecondarySequence().replaceAll(" ", "_").replaceAll("G", "H").replaceAll("I", "H");
		String _5BTRC_sec = _5BTRC.toSecondarySequence().replaceAll(" ", "_").replaceAll("G", "H").replaceAll("I", "H");
		String _4ZZHA_sec = _4ZZHA.toSecondarySequence().replaceAll(" ", "_").replaceAll("G", "H").replaceAll("I", "H");
		String _4ZZIA_sec = _4ZZIA.toSecondarySequence().replaceAll(" ", "_").replaceAll("G", "H").replaceAll("I", "H");
		String _4ZZJA_sec = _4ZZJA.toSecondarySequence().replaceAll(" ", "_").replaceAll("G", "H").replaceAll("I", "H");
		
		_5BTRA_sec = _5BTRA_sec.replaceAll("B", "E").replaceAll("S", "_").replaceAll("T", "_");
		_5BTRB_sec = _5BTRB_sec.replaceAll("B", "E").replaceAll("S", "_").replaceAll("T", "_");
		_5BTRC_sec = _5BTRC_sec.replaceAll("B", "E").replaceAll("S", "_").replaceAll("T", "_");
		_4ZZHA_sec = _4ZZHA_sec.replaceAll("B", "E").replaceAll("S", "_").replaceAll("T", "_");
		_4ZZIA_sec = _4ZZIA_sec.replaceAll("B", "E").replaceAll("S", "_").replaceAll("T", "_");
		_4ZZJA_sec = _4ZZJA_sec.replaceAll("B", "E").replaceAll("S", "_").replaceAll("T", "_");
		
		//debugCluster.markDominant(1);
		debugCluster.verifySwitches(1);
		
		qp(_5BTRA.toSequence());
		qp(_5BTRA_sec);
		qp(_5BTRB_sec);
		qp(_5BTRC_sec);
		qp(_4ZZHA_sec);
		qp(_4ZZIA_sec);
		qp(_4ZZJA_sec);
		qp(_5BTRA.toSwitches());
		
		//qp(SwitchValidationModule.makeSwitchLinup(debugCluster));
	}
	
	private static void switchMarkTest02() throws Exception {
		ProteinChain _5BTRA = BioLookup.readChainFromFasta("5BTR",'A');
		_5BTRA = BioLookup.assignSecondaryDSSP(_5BTRA);
		
		AlignedCluster debugCluster = new AlignedCluster(_5BTRA);
		
		ProteinChain _5BTRB = BioLookup.readChainFromFasta("5BTR",'B');
		_5BTRB = BioLookup.assignSecondaryDSSP(_5BTRB);
		ProteinChain _5BTRC = BioLookup.readChainFromFasta("5BTR",'C');
		_5BTRC = BioLookup.assignSecondaryDSSP(_5BTRC);
		ProteinChain _4ZZHA = BioLookup.readChainFromFasta("4ZZH",'A');
		_4ZZHA = BioLookup.assignSecondaryDSSP(_4ZZHA);
		ProteinChain _4ZZIA = BioLookup.readChainFromFasta("4ZZI",'A');
		_4ZZIA = BioLookup.assignSecondaryDSSP(_4ZZIA);
		ProteinChain _4ZZJA = BioLookup.readChainFromFasta("4ZZJ",'A');
		_4ZZJA = BioLookup.assignSecondaryDSSP(_4ZZJA);
		
		debugCluster.addChain(_5BTRB);
		debugCluster.addChain(_5BTRC);
		debugCluster.addChain(_4ZZHA);
		debugCluster.addChain(_4ZZIA);
		debugCluster.addChain(_4ZZJA);
		
		String _5BTRA_sec = _5BTRA.toSecondarySequence().replaceAll(" ", "_").replaceAll("G", "H").replaceAll("I", "H");
		String _5BTRB_sec = _5BTRB.toSecondarySequence().replaceAll(" ", "_").replaceAll("G", "H").replaceAll("I", "H");
		String _5BTRC_sec = _5BTRC.toSecondarySequence().replaceAll(" ", "_").replaceAll("G", "H").replaceAll("I", "H");
		String _4ZZHA_sec = _4ZZHA.toSecondarySequence().replaceAll(" ", "_").replaceAll("G", "H").replaceAll("I", "H");
		String _4ZZIA_sec = _4ZZIA.toSecondarySequence().replaceAll(" ", "_").replaceAll("G", "H").replaceAll("I", "H");
		String _4ZZJA_sec = _4ZZJA.toSecondarySequence().replaceAll(" ", "_").replaceAll("G", "H").replaceAll("I", "H");
		
		_5BTRA_sec = _5BTRA_sec.replaceAll("B", "E").replaceAll("S", "_").replaceAll("T", "_");
		_5BTRB_sec = _5BTRB_sec.replaceAll("B", "E").replaceAll("S", "_").replaceAll("T", "_");
		_5BTRC_sec = _5BTRC_sec.replaceAll("B", "E").replaceAll("S", "_").replaceAll("T", "_");
		_4ZZHA_sec = _4ZZHA_sec.replaceAll("B", "E").replaceAll("S", "_").replaceAll("T", "_");
		_4ZZIA_sec = _4ZZIA_sec.replaceAll("B", "E").replaceAll("S", "_").replaceAll("T", "_");
		_4ZZJA_sec = _4ZZJA_sec.replaceAll("B", "E").replaceAll("S", "_").replaceAll("T", "_");
		
		debugCluster.verifySwitches(1);
		
		qp(_5BTRA.toSequence());
		qp(_5BTRA_sec);
		qp(_5BTRB_sec);
		qp(_5BTRC_sec);
		qp(_4ZZHA_sec);
		qp(_4ZZIA_sec);
		qp(_4ZZJA_sec);
		qp(_5BTRA.toSwitches());
	}
	
	private static void switchMarkTest03() throws Exception {
		ProteinChain _5YGEA = BioLookup.readChainFromFasta("5YGE",'A');
		_5YGEA = BioLookup.assignSecondaryDSSP(_5YGEA);
		
		AlignedCluster debugCluster = new AlignedCluster(_5YGEA);
		
		ProteinChain _5YGEB = BioLookup.readChainFromFasta("5YGE",'B');
		_5YGEB = BioLookup.assignSecondaryDSSP(_5YGEB);
		
		debugCluster.addChain(_5YGEB);

		String _5YGEA_sec = _5YGEA.toSecondarySequence().replaceAll(" ", "_").replaceAll("G", "H").replaceAll("I", "H");
		String _5YGEB_sec = _5YGEB.toSecondarySequence().replaceAll(" ", "_").replaceAll("G", "H").replaceAll("I", "H");
		
		_5YGEA_sec = _5YGEA_sec.replaceAll("B", "E").replaceAll("S", "_").replaceAll("T", "_");
		_5YGEB_sec = _5YGEB_sec.replaceAll("B", "E").replaceAll("S", "_").replaceAll("T", "_");

		debugCluster.verifySwitches(1);
		
		qp(_5YGEA.toSequence());
		qp(_5YGEA_sec);
		qp(_5YGEB_sec);
		qp(_5YGEA.toSwitches());
	}
	
	private static void switchMarkTest04() throws Exception {
		ProteinChain _1TIQA = BioLookup.readChainFromFasta("1TIQ",'A');
		_1TIQA = BioLookup.assignSecondaryDSSP(_1TIQA);
		
		AlignedCluster debugCluster = new AlignedCluster(_1TIQA);
		
		ProteinChain _1TIQB = BioLookup.readChainFromFasta("1TIQ",'B');
		_1TIQB = BioLookup.assignSecondaryDSSP(_1TIQB);
		
		debugCluster.addChain(_1TIQB);

		String _1TIQA_sec = _1TIQA.toSecondarySequence().replaceAll(" ", "_").replaceAll("G", "H").replaceAll("I", "H");
		String _1TIQB_sec = _1TIQB.toSecondarySequence().replaceAll(" ", "_").replaceAll("G", "H").replaceAll("I", "H");
		
		_1TIQA_sec = _1TIQA_sec.replaceAll("B", "E").replaceAll("S", "_").replaceAll("T", "_");
		_1TIQB_sec = _1TIQB_sec.replaceAll("B", "E").replaceAll("S", "_").replaceAll("T", "_");

		debugCluster.verifySwitches(1);
		
		qp(_1TIQA.toSequence());
		qp(_1TIQA_sec);
		qp(_1TIQB_sec);
		qp(_1TIQA.toSwitches());
	}
	
	private static void superAlignerTest() throws Exception {
		ProteinChain _4IG9A = BioLookup.readChainFromFasta("4IG9",'A');
		ProteinChain _4IG9C = BioLookup.readChainFromFasta("4IG9",'C');
		
		qp(_4IG9A);
		qp(_4IG9C);
		
		_4IG9A = BioLookup.assignSecondary(_4IG9A);
		_4IG9C = BioLookup.assignSecondary(_4IG9C);
		
		
		SequenceAligner.superAlign(_4IG9A, _4IG9C);
	}
	
	private static void compareTest() throws Exception {
		ProteinChain _2FL4 = new ProteinChain("2FL4", 'A', _2FL4_SEQ);
		BioLookup.assignSecondary(_2FL4);
		CSVWriter.writeSwitchRecordCSV("2FL4-cluster", _2FL4);
	}
	
	private static void clusterTest() throws Exception {
		String[] data = ClusterReader.readClusters("clusters.txt")[7];
		//qp(data);
		
		ProteinChain _2B5GA = BioLookup.readChainFromFasta(data[0]);
		_2B5GA = BioLookup.assignSecondary(_2B5GA);
		
		AlignedCluster cluster = new AlignedCluster(_2B5GA);
		//cluster.getDominant().autoAssignCharges();
		
		for(int i = 1; i < data.length; ++i) {
			ProteinChain chain = BioLookup.readChainFromFasta(data[i]);
			BioLookup.assignSecondary(chain);
			cluster.addChain(chain);
		}
		
		cluster.markDominant();
		_2B5GA = cluster.getDominant();
		
		qp(cluster);
	}
	
	private static void chainAlignmentTest() throws Exception {
		ProteinChain _5BTRA = BioLookup.readChainFromFasta("5BTR",'A');
		ProteinChain _4ZZIA = BioLookup.readChainFromFasta("4ZZI",'A');
		
		int alignVal = SequenceAligner.alignSequence(_5BTRA, _4ZZIA);
		
		alignVal *= -1;
		
		_4ZZIA.insertBlanks(0, alignVal);
		
		qp(_5BTRA);
		qp(_4ZZIA);
	}
	
	private static void simpleAlignerTest() throws Exception {
		ProteinChain test00 = new ProteinChain(TEST_NAME, 'A', SEQUENCE_BANK[0]);
		ProteinChain test01 = new ProteinChain(TEST_NAME, 'A', SEQUENCE_BANK[1]);
		
		int alignVal = SequenceAligner.alignSequence(test00, test01);
		
		qp(SEQUENCE_BANK[0].length());
		qp(alignVal);
		
		alignVal = SequenceAligner.alignSequence(SEQUENCE_BANK[0], SEQUENCE_BANK[1]);
		qp(alignVal);
	}
	
	private static void fastaTest() throws Exception {
		ProteinChain full_5BTR = BioLookup.readChainPDB("5btr", 'a');
		qp(full_5BTR.firstIndex());
		qp(full_5BTR.toFasta());
	}
	
	private static void alignerCSVTest2() throws Exception {
		String[] data = ClusterReader.readClusters("misc/5BTRA-cluster.txt")[0];
		//qp(data);
		
		ProteinChain _5BTRA = BioLookup.readChainFromFasta(data[0]);
		_5BTRA = BioLookup.assignSecondary(_5BTRA);
		
		AlignedCluster cluster = new AlignedCluster(_5BTRA);
		//cluster.getDominant().autoAssignCharges();
		
		for(int i = 1; i < data.length; ++i) {
			ProteinChain chain = BioLookup.readChainFromFasta(data[i]);
			BioLookup.assignSecondary(chain);
			cluster.addChain(chain);
		}
		
		cluster.markDominant();
		_5BTRA = cluster.getDominant();
		
		qp(cluster);
		
		/*for(int i = _5BTRA.firstIndex(); i < _5BTRA.length(); ++i) {
			qp(i + "\t" + _5BTRA.getAmino(i).toChar() + "\t" + _5BTRA.getAmino(i).isSwitch());
		}
		
		//String dataSetArray[] = _5BTRA.t
		
		CSVWriter.writeDataSet("output/test", _5BTRA);*/
	}
	
	private static void alignerCSVTest() throws Exception {
		FASTA.verifyRCSB("4JJX",'A');
		FASTA.verifyRCSB("4YGO",'A');
		ProteinChain _4JJXA = BioLookup.readChainFromFasta("4JJX",'A');
		ProteinChain _4YGOA = BioLookup.readChainFromFasta("4YGO",'A');
		ProteinChain _4MHDA = BioLookup.readChainFromFasta("4MHD",'A');
		ProteinChain _6DAUA = BioLookup.readChainFromFasta("6DAU",'A');
		
		BioLookup.assignSecondary(_4JJXA);
		BioLookup.assignSecondary(_4YGOA);
		BioLookup.assignSecondary(_4MHDA);
		BioLookup.assignSecondary(_6DAUA);
		
		/*for(int i = _4MHDA.firstIndex(); i < _4MHDA.length(); ++i) {
			qp(i + "-" + _4MHDA.getAmino(i).toChar() + "-" + _4MHDA.getAmino(i).secondary());
		}*/
		
		AlignedCluster cluster = new AlignedCluster(_4JJXA);
		cluster.addChain(_4YGOA);
		cluster.addChain(_4MHDA);
		cluster.addChain(_6DAUA);
		
		qp(cluster.toSecStructCSV());
	}
	
	private static void alignerTest() throws Exception {
		FASTA.verifyRCSB("4JJX",'A');
		FASTA.verifyRCSB("6DAU",'A');
		ProteinChain _4JJXA = BioLookup.readChainFromFasta("4JJX",'A');
		ProteinChain _6DAUA = BioLookup.readChainFromFasta("6DAU",'A');
		
		int result = SequenceAligner.alignSequence(_4JJXA, _6DAUA);
		qp("result is " + result);
	}
}
