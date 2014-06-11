package featureselection;
//包含度的半监督粗糙集聚类特征提取_分类器测试

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Random;
import java.util.Vector;

//import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.ss.util.CellRangeAddress;
//import org.apache.poi.hssf.util.Region;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import UFS.DissimilarityForKmodes;
import UFS.EvaluatedUFS;
import UFS.SimpleKModes;

import weka.attributeSelection.AttributeSelection;
import weka.attributeSelection.GainRatioAttributeEval;
import weka.attributeSelection.InfoGainAttributeEval;
import weka.attributeSelection.Ranker;
import weka.attributeSelection.ReliefFAttributeEval;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.rules.DecisionTable;
import weka.classifiers.rules.OneR;
import weka.classifiers.rules.ZeroR;
import weka.classifiers.trees.J48;
import weka.classifiers.trees.RandomForest;
import weka.classifiers.trees.SimpleCart;
import weka.clusterers.CLOPE;
import weka.core.Attribute;
import weka.core.DistanceFunction;
import weka.core.Instance;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Remove;

public class CopyOfSemiroughsetfeatureselection_PCT {
	public enum Labeldis {
		pos, dismat,dismatall, dismatonline;
	}

	public enum Nodesim {
		min, max, mean;
	}

	public enum Middlekind {
		maxsim, maxscore, maxsimtoall;
	}
	static class Attwithscore implements Comparable{
		int attno;
		double score;
		public int compareTo(Object o1){
			if(this.score< ((Attwithscore)o1).score)
				return 1;
			else
				return -1;
		}
		public Attwithscore(){
			this.attno=0;
			this.score=0;
		}
		public Attwithscore(int pattno,double pscore){
			this.attno=pattno;
			this.score=pscore;
		}
	}

	static class evaluate_struct {
		double J48pct;
		double NBCpct;
		double CartRpct;
		double DTpct;
		double RandomForestpct;
		double Lem2pct;
	}

	static class cluster_re {
		double XB;
		double I;
	}

	static class Node {
		public int value;
		boolean isleaf;
		public Node leftChild;
		public Node rightChild;
		public int[] pos;
		public int[] atr;

		public Node(int value) {
			this.value = value;
			leftChild = null;
			rightChild = null;
			isleaf = false;
			pos = null;
			atr = null;
		}

		public Node(int value, boolean isleaf) {
			this.value = value;
			leftChild = null;
			rightChild = null;
			this.isleaf = isleaf;
			pos = null;
			atr = null;
		}

		public void setleftChild(Node lc) {
			leftChild = lc;
		}

		public void setrightChild(Node rc) {
			rightChild = rc;
		}
	}

	static int[] evabias = new int[6];
	static int curbias;
	static int curbase;
	static int ratiolength;
	static String dataname;
	static int method = 1;
	static double lpar = 1, upar = 1;
	static int RAND_TEST_NUM = 10;
	static DistanceFunction m_DistanceFunction;
	static int cursheetline = 0;
	public static Node root;

	static XSSFWorkbook book;
	static XSSFSheet sheet;
	static XSSFRow row;
	private Instances m_ClusterCentroids;

	private static double[][] similaritymat = null;

	static Vector<Node> topnodes = null;
	static Vector<Node> treenodes = null;

	static int rootlevel;

	static Node[] topK = null;
	static int[] selectatr;

	static Vector<Vector<Integer>> rem_selectatr = null;

	static double[][] posfuzzyforall;
	static double[][] dismatfuzzyforall;

	static int[][] posforall;
	static Vector posfortop;

	static int[][][] dismatforall;
	static int select_atr_num = 1;
	static int dataset_no = 1;
	static double unlabel_ratio = 0.8;
	static Labeldis e_dis;

	static double[] scoreforatt;
	static double EMPTY_VALUE = 0;
	static Nodesim node_sim = Nodesim.min;
	static Middlekind midkid = Middlekind.maxscore;

	public static int getlowappcount(Instances dataset, int a[], boolean[] X) {
		boolean[] Y = new boolean[dataset.numAttributes()];
		Arrays.fill(Y, false);
		for (int i = 0; i < a.length; i++) {
			Y[a[i]] = true;
		}
		boolean[][] Yj = getEquivalenceClass(dataset, Y);
		int U = Yj.length;
		int M = Yj[0].length;
		int lowappcount = 0;

		for (int i = 0; i < M; ++i) {
			int cnt = 0;
			for (int k = 0; k < U; ++k) {
				if (X[k] && Yj[k][i]) {
					cnt++; // Yj \in X 的个数
				}
				if (!X[k] && Yj[k][i]) {// 不属于
					cnt = 0;
					break;
				}
			}
			lowappcount = lowappcount + cnt;
		}
		return lowappcount;
	}

	public static int getupappcount(Instances dataset, int a[], boolean[] X) {
		boolean[] Y = new boolean[dataset.numAttributes()];
		Arrays.fill(Y, false);
		for (int i = 0; i < a.length; i++) {
			Y[a[i]] = true;
		}
		boolean[][] Yj = getEquivalenceClass(dataset, Y);
		int U = Yj.length;
		int M = Yj[0].length;
		int upappcount = 0;

		for (int i = 0; i < M; ++i) {
			int cnt = 0;
			boolean flag = false;
			for (int k = 0; k < U; ++k) {
				if (X[k] && Yj[k][i]) {
					flag = true;// 有交
				}
				if (Yj[k][i]) {// 统计等价类
					cnt++;
				}
			}
			if (flag) {
				upappcount = upappcount + cnt;
			}
		}
		return upappcount;
	}

	public static double getappaccury(Instances dataset, int a[]) {
		double appac = 0.0;
		boolean[] X = new boolean[dataset.numAttributes()];
		Arrays.fill(X, false);
		X[dataset.numAttributes() - 1] = true;
		boolean[][] Xi = getEquivalenceClass(dataset, X);
		int N = Xi[0].length;
		int U = Xi.length;
		boolean[] tmpX = new boolean[U];
		double lowsum = 0, upsum = 0;

		for (int i = 0; i < N; i++) {
			for (int j = 0; j < U; j++)
				tmpX[j] = Xi[j][i];
			double lowac = getlowappcount(dataset, a, tmpX);
			double upac = getupappcount(dataset, a, tmpX);
			lowsum += lowac;
			upsum += upac;
		}

		return lowsum / upsum;
	}

	public static void computescore(Instances l_data, Instances u_data,
			Labeldis e_dis) {
		scoreforatt = new double[u_data.numAttributes()];
		int[] a = new int[1];
		for (int i = 0; i < scoreforatt.length; i++) {
			a[0] = i;
			if(e_dis==Labeldis.dismatall){
				int[][] dismata =getdismatforalldata(l_data,u_data,a);
				double sum = 0, count = 0;
				for (int j = 0; j < dismata.length; j++) {
					for (int k = 0; k < dismata[0].length; k++) {
						sum += dismata[j][k];
						count++;
					}
				}
				if (count != 0) {
					scoreforatt[i] = sum / count;
				}else{
					scoreforatt[i]=0;
				}
				continue;
			}
			double lp = 0, up = 0;
			if (l_data.numInstances() > 0) {
				if (e_dis == Labeldis.pos) {
					int[] posa = posforall[a[0]];
					double sum = 0, count = 0;
					for (int j = 0; j < posa.length; j++) {
						sum += posa[j];
						count++;
					}
					if (count != 0) {
						lp = sum / count;
					} else {
						lp = 0;
					}
				} else if (e_dis == Labeldis.dismat
						|| e_dis == Labeldis.dismatonline) {
					int[][] ldismata = getdismat(l_data, a, true);
					double sum = 0, count = 0;
					for (int j = 0; j < ldismata.length; j++) {
						for (int k = 0; k < ldismata[0].length; k++) {
							sum += ldismata[j][k];
							count++;
						}
					}
					if (count != 0) {
						lp = sum / count;
					} else {
						lp = 0;
					}
				}
			}
			if (u_data.numInstances() > 0) {
				int[][] udismata = getdismat(u_data, a, false);
				double sum = 0, count = 0;
				for (int j = 0; j < udismata.length; j++) {
					for (int k = 0; k < udismata[0].length; k++) {
						sum += udismata[j][k];
						count++;
					}
				}
				if (count != 0) {
					up = sum / count;
				} else {
					up = 0;
				}
			}
			scoreforatt[i] = lp + up;
		}

	}

	public static double getConditionalEntorpy(Instances dataset, int[] a) {
		boolean[] X = new boolean[dataset.numAttributes()];
		Arrays.fill(X, false);
		X[dataset.numAttributes() - 1] = true;

		boolean[] Y = new boolean[dataset.numAttributes()];
		Arrays.fill(Y, false);
		for (int i = 0; i < a.length; i++) {
			Y[a[i]] = true;
		}

		boolean[][] Xi = getEquivalenceClass(dataset, X);
		boolean[][] Yj = getEquivalenceClass(dataset, Y);

		int U = Xi.length;
		int N = Xi[0].length;
		int M = Yj[0].length;
		// 计算条件熵
		double res_entropy = 0.0;
		for (int i = 0; i < N; ++i) {
			for (int j = 0; j < M; ++j) {
				int XandYnum = 0;
				int Ynum = 0;
				for (int k = 0; k < U; ++k) {
					if (Xi[k][i] & Yj[k][j]) {
						// System.out.println("##"+k+":"+i+":"+j);
						XandYnum++;
					}
					if (Yj[k][j])

						Ynum++;
				}
				// System.out.println(XandYnum+":"+Ynum);
				if (XandYnum != 0)
					res_entropy = res_entropy
							- XandYnum
							* (Math.log((double) XandYnum / Ynum) / Math
									.log((double) 2));
				// 注意加(double) 否则part1_sum/part2_sum = 0 熵为负无穷
			}
		}

		res_entropy = (double) res_entropy / U;
		return res_entropy;
	}

	public static boolean[][] getEquivalenceClass(Instances dataset, boolean[] X) {
		int U = dataset.numInstances();
		// 求等价类,先求等价矩阵
		int[][] X_matrix = new int[U][U];
		for (int i = 0; i < U; ++i) {
			for (int j = 0; j < U; ++j) {
				X_matrix[i][i] = 0;
			}
		}
		int N = 0; // 统计等价类的个数
		for (int i = 0; i < U; ++i) {
			if (X_matrix[i][i] == 0) {
				N++;
				X_matrix[i][i] = 1;
				for (int j = i + 1; j < U; ++j) {
					boolean flag = true;
					for (int k = 0; k < X.length; ++k) {
						if (X[k]
								&& dataset.instance(i).value(k) != dataset
										.instance(j).value(k)) {
							flag = false;
							break;
						}
					}
					if (flag) {
						X_matrix[i][j] = 1;
						X_matrix[j][j] = 2;
					}
				}
			}
		}
		// 生成等价类Xi
		boolean[][] Xi = new boolean[U][N];
		for (int i = 0; i < U; i++) {
			for (int j = 0; j < N; j++) {
				Xi[i][j] = false;
			}
		}
		int n = 0;
		for (int i = 0; i < U; ++i) {
			if (X_matrix[i][i] == 1) {
				for (int j = 0; j < U; ++j) {
					if (X_matrix[i][j] == 1)
						Xi[j][n] = true;
				}
				n++;
			}
		}
		return Xi;
	}

	public static int[] getpos(Instances dataset, int[] a) {
		// public static boolean[] getPOS_pX(Instances dataset,boolean[] X,
		// boolean[] P){
		if (dataset.numInstances() == 0) {
			return null;
		}
		boolean[] D = new boolean[dataset.numAttributes()];
		Arrays.fill(D, false);
		D[dataset.numAttributes() - 1] = true;

		boolean[] B = new boolean[dataset.numAttributes()];
		Arrays.fill(B, false);
		for (int i = 0; i < a.length; i++) {
			B[a[i]] = true;
		}

		boolean[][] Xi = getEquivalenceClass(dataset, B);
		boolean[][] Yj = getEquivalenceClass(dataset, D);
		int N = Xi[0].length;
		int U = Xi.length;
		int M = Yj[0].length;

		boolean[] pos_px = new boolean[U];
		Arrays.fill(pos_px, false);
		for (int i = 0; i < N; ++i) {
			for (int j = 0; j < M; ++j) {
				boolean[] px = new boolean[U];
				Arrays.fill(px, false);
				for (int k = 0; k < U; ++k) {
					if (Xi[k][i] && Yj[k][j]) {
						px[k] = true;
					}
					if (Xi[k][i] && !Yj[k][j]) { // 不属于
						Arrays.fill(px, false);
						break;
					}
				}
				pos_px = Utils.boolsAdd(pos_px, px);
			}
		}
		int[] pos_px_int = new int[U];
		for (int i = 0; i < U; i++) {
			if (pos_px[i]) {
				pos_px_int[i] = 1;
			} else {
				pos_px_int[i] = 0;
			}
		}
		return pos_px_int;
	}

	public static int[][] getdismat(Instances data, int[] a,
			boolean withdecision) {
		int[][] mat = new int[data.numInstances()][data.numInstances()];
		for (int i = 0; i < data.numInstances(); i++)
			for (int j = 0; j < data.numInstances(); j++)
				mat[i][j] = 0;

		if (withdecision) {
			int disatrnum = data.classIndex();
			if (disatrnum < 0) {
				System.out.printf("Wrong!");
			}
			for (int i = 0; i < data.numInstances(); i++) {
				for (int j = i; j < data.numInstances(); j++) {
					for (int k = 0; k < a.length; k++) {
						if (data.instance(i).value(a[k]) != data.instance(j)
								.value(a[k])
								&& data.instance(i).value(disatrnum) != data
										.instance(j).value(disatrnum)) {
							mat[i][j] = 1;
							mat[j][i] = 1;
						}
					}
				}
			}
		} else {
			for (int i = 0; i < data.numInstances(); i++) {
				for (int j = i; j < data.numInstances(); j++) {
					for (int k = 0; k < a.length; k++) {
						if (data.instance(i).value(a[k]) != data.instance(j)
								.value(a[k])) {
							mat[i][j] = 1;
							mat[j][i] = 1;
						}
					}
				}
			}
		}
		return mat;
	}

	public static void computepos(Instances ldata) {
		int artnum = ldata.numAttributes() - 1;
		int datanum = ldata.numInstances();
		posforall = new int[artnum][datanum];
		for (int i = 0; i < artnum; i++) {
			int[] a = new int[1];
			a[0] = i;
			posforall[i] = getpos(ldata, a);
		}

	}

	public static int[][] getdismatforalldata(Instances l_data, Instances u_data, int[] a){
		int numdata=l_data.numInstances()+u_data.numInstances();
		int[][] mat = new int[numdata][numdata];
		for (int i = 0; i < numdata; i++)
			for (int j = 0; j < numdata; j++)
				mat[i][j] = 0;
		int disatrnum = l_data.classIndex();
		if (disatrnum < 0) {
			System.out.printf("Wrong!");
		}
		//左上角 label-label
		for (int i = 0; i < l_data.numInstances(); i++) {
			for (int j = i; j < l_data.numInstances(); j++) {
				for (int k = 0; k < a.length; k++) {
					if (l_data.instance(i).value(a[k]) != l_data.instance(j)
							.value(a[k])
							&& l_data.instance(i).value(disatrnum) != l_data
									.instance(j).value(disatrnum)) {
						mat[i][j] = 1;
						mat[j][i] = 1;
					}
				}
			}
		}
		//右上角+左下角 label-unlabel
		for (int i = l_data.numInstances();i<numdata ; i++) {
			for (int j = 0; j < l_data.numInstances(); j++) {
				for (int k = 0; k < a.length; k++) {
					if (u_data.instance(i-l_data.numInstances()).value(a[k]) != l_data.instance(j)
							.value(a[k])) {
						mat[i][j] = 1;
						mat[j][i] = 1;
					}
				}
			}
		}
		//右下角 unlabel-unlabel
		for (int i = l_data.numInstances();i<numdata ; i++) {
			for (int j = i; j < numdata; j++) {
				for (int k = 0; k < a.length; k++) {
					if (u_data.instance(i-l_data.numInstances()).value(a[k]) != u_data.instance(j-l_data.numInstances())
							.value(a[k])) {
						mat[i][j] = 1;
						mat[j][i] = 1;
					}
				}
			}
		}
		return mat;
	}
	
	public static double[] computesimilarityfordismatall(Instances l_data, Instances u_data,
			int[] a1, int[] a2) {
		double simvalue[]=new double[2];
		Instances instancesl = new Instances(l_data);
		Instances instancesu = new Instances(u_data);
		int[][] dismata1 = getdismatforalldata(l_data, u_data,a1);
		int[][] dismata2 = getdismatforalldata(l_data, u_data,a2);
		double sumintersection=0,suma1=0,suma2=0;
		for (int i = 0; i < dismata1.length; i++) {
			for (int j = 0; j < dismata2[0].length; j++) {
				if (1 == dismata1[i][j] && 1 == dismata2[i][j]) {
					sumintersection++;
				}
				if (1 == dismata1[i][j]) {
					suma1++;
				}if (1 == dismata2[i][j]) {
					suma2++;
				}
			}
		}
		double pa1=EMPTY_VALUE,pa2=EMPTY_VALUE;
		if (suma1 != 0)
			pa1=sumintersection / suma1;
		if (suma2 != 0)
			pa2=sumintersection / suma2;
		simvalue[0]=pa1;
		simvalue[1]=pa2;
		return simvalue;
	}
	
	public static double[] computesimilarity(Instances l_data, Instances u_data,
			int[] a1, int[] a2, Labeldis e_dis) {
		if(e_dis==Labeldis.dismatall)
			return computesimilarityfordismatall(l_data,u_data,a1,a2);
		
		Instances instancesl = new Instances(l_data);
		Instances instancesu = new Instances(u_data);

		double sumintersection = 0;
		double suma1 = 0,suma2=0;
		double[] labpar = new double[2];
		if (l_data.numInstances() > 0) {
			if (e_dis == Labeldis.pos) {
				int[] posa1, posa2;
				if (a1.length == 1 && a2.length == 1) {
					posa1 = posforall[a1[0]];
					posa2 = posforall[a2[0]];
				} else {
					System.out.printf("More than one atr...");
					posa1 = getpos(l_data, a1);
					posa2 = getpos(l_data, a2);
				}
				sumintersection = 0;
				suma1 = 0;
				suma2 = 0;
				for (int i = 0; i < posa1.length; i++) {
					if (1 == posa1[i] && 1 == posa2[i]) {
						sumintersection++;
					}
					if (posa1[i] == 1) {
						suma1++;
					}
					if (posa2[i] == 1) {
						suma2++;
					}
				}
				double pa1=EMPTY_VALUE,pa2=EMPTY_VALUE;
				if (suma1 != 0)
					pa1=sumintersection / suma1;
				if (suma2 != 0)
					pa2=sumintersection / suma2;
				labpar[0]=pa1;
				labpar[1]=pa2;
			} else if (e_dis == Labeldis.dismat
					|| e_dis == Labeldis.dismatonline) {
				sumintersection = 0;
				suma1 = 0;
				suma1 = 0;
				int[][] ldismata1, ldismata2;
				ldismata1 = getdismat(l_data, a1, true);
				ldismata2 = getdismat(l_data, a2, true);
				for (int i = 0; i < ldismata1.length; i++) {
					for (int j = 0; j < ldismata1[0].length; j++) {
						if (1 == ldismata1[i][j] && 1 == ldismata2[i][j]) {
							sumintersection++;
						}
						if (1 == ldismata1[i][j]) {
							suma1++;
						}
						if (1 == ldismata2[i][j]) {
							suma2++;
						}
					}
				}
				double pa1=EMPTY_VALUE,pa2=EMPTY_VALUE;
				if (suma1 != 0)
					pa1=sumintersection / suma1;
				if (suma2 != 0)
					pa2=sumintersection / suma2;
				labpar[0]=pa1;
				labpar[1]=pa2;
			}
		}
		sumintersection = 0;
		suma1 = 0;
		suma2 = 0;
		int[][] dismata1, dismata2;
		double []unlabelpar = new double[2];
		if (u_data.numInstances() > 0) {
			dismata1 = getdismat(u_data, a1, false);
			dismata2 = getdismat(u_data, a2, false);
			for (int i = 0; i < dismata1.length; i++) {
				for (int j = 0; j < dismata1[0].length; j++) {
					if (1 == dismata1[i][j] && 1 == dismata2[i][j]) {
						sumintersection++;
					}
					if (1 == dismata1[i][j]) {
						suma1++;
					}
					if (1 == dismata2[i][j]) {
						suma2++;
					}
				}
			}
			double pa1=EMPTY_VALUE,pa2=EMPTY_VALUE;
			if (suma1 != 0)
				pa1=sumintersection / suma1;
			if (suma2 != 0)
				pa2=sumintersection / suma2;
			unlabelpar[0]=pa1;
			unlabelpar[1]=pa2;
		}
		double[] re=new double[2];
		re[0]=lpar * labpar[0] + upar * unlabelpar[0];
		re[1]=lpar * labpar[1] + upar * unlabelpar[1];
		return re;
	}

	public static void computesimilarities(Instances l_dataset,
			Instances u_dataset, Labeldis e_dis) throws Exception {
		if ((l_dataset.numAttributes() - 1) != u_dataset.numAttributes()) {
			throw new Exception("Wrong data attributes number!");
		}
		similaritymat = new double[u_dataset.numAttributes()][u_dataset
				.numAttributes()];
		for (int i = 0; i < u_dataset.numAttributes(); i++) {
			for (int j = i; j < u_dataset.numAttributes(); j++) {
				int[] a1, a2;
				a1 = new int[1];
				a2 = new int[1];
				a1[0] = i;
				a2[0] = j;
				if (u_dataset.attribute(i).isNominal()
						&& u_dataset.attribute(j).isNominal()) {
					double[] simvalue=computesimilarity(l_dataset,
							u_dataset, a1, a2, e_dis);
					similaritymat[i][j] = simvalue[1];
					similaritymat[j][i] = simvalue[0];
				} else if (u_dataset.attribute(i).isNumeric()
						&& u_dataset.attribute(j).isNumeric()) {
					System.out.printf("no num data now!");
				}
			}
		}
	}

	public static void collectleaf(Node node, Vector v) {
		if (node.isleaf == true) {
			v.add((Object) node.value);
			return;
		}
		if (node.leftChild != null)
			collectleaf(node.leftChild, v);
		if (node.rightChild != null)
			collectleaf(node.rightChild, v);
		return;
	}

	public static double getdismatsimonline(Node n1, Node n2,
			Instances ldataset, Instances udataset) {
		Vector n1leafs = new Vector();
		Vector n2leafs = new Vector();
		collectleaf(n1, n1leafs);
		collectleaf(n2, n2leafs);
		int[] a1 = new int[n1leafs.size()];
		int[] a2 = new int[n2leafs.size()];
		for (int i = 0; i < a1.length; i++) {
			a1[i] = (new Integer(n1leafs.elementAt(i).toString())).intValue();
		}
		for (int i = 0; i < a2.length; i++) {
			a2[i] = (new Integer(n2leafs.elementAt(i).toString())).intValue();
		}
		double re[] = computesimilarity(ldataset, udataset, a1, a2,
				Labeldis.dismatonline);
		return Math.max(re[0], re[1]);
	}

	public static double getnodesimilarity(Node n1, Node n2) {
		Vector n1leafs = new Vector();
		Vector n2leafs = new Vector();
		collectleaf(n1, n1leafs);
		collectleaf(n2, n2leafs);
		double simvalue = 0;
		if (node_sim == Nodesim.min) {
			simvalue = -100;
			for (int i = 0; i < n1leafs.size(); i++) {
				for (int j = 0; j < n2leafs.size(); j++) {
					int h = (new Integer(n1leafs.elementAt(i).toString()))
							.intValue();
					int k = (new Integer(n2leafs.elementAt(j).toString()))
							.intValue();
					if (simvalue < Math.max(similaritymat[h][k],similaritymat[k][h])) {
						simvalue = Math.max(similaritymat[h][k],similaritymat[k][h]);
					}
				}
			}
		} else if (node_sim == Nodesim.max) {
			simvalue = 10000;
			for (int i = 0; i < n1leafs.size(); i++) {
				for (int j = 0; j < n2leafs.size(); j++) {
					int h = (new Integer(n1leafs.elementAt(i).toString()))
							.intValue();
					int k = (new Integer(n2leafs.elementAt(j).toString()))
							.intValue();
					if (simvalue > Math.max(similaritymat[h][k],similaritymat[k][h])) {
						simvalue = Math.max(similaritymat[h][k],similaritymat[k][h]);
					}
				}
			}
		} else if (node_sim == Nodesim.mean) {
			simvalue = 0;
			int count = 0;
			for (int i = 0; i < n1leafs.size(); i++) {
				for (int j = 0; j < n2leafs.size(); j++) {
					int h = (new Integer(n1leafs.elementAt(i).toString()))
							.intValue();
					int k = (new Integer(n2leafs.elementAt(j).toString()))
							.intValue();
					simvalue += Math.max(similaritymat[h][k],similaritymat[k][h]);
					count++;
				}
			}
			simvalue /= count;
		}
		return simvalue;
	}

	public static int middleof(Node root, Instances ldataset, Instances udataset) {
		Vector leafs = new Vector();
		collectleaf(root, leafs);
		int mid = -1;
		if (midkid == Middlekind.maxsim) {
			double maxsim = -100;
			for (int i = 0; i < leafs.size(); i++) {
				int curatr = (new Integer(leafs.elementAt(i).toString()))
						.intValue();
				double sum = 0;
				for (int j = 0; j < leafs.size(); j++) {
					if (j != i) {
						int tmpatr = (new Integer(leafs.elementAt(j).toString()))
								.intValue();
						//sum += Math.max(similaritymat[curatr][tmpatr], similaritymat[tmpatr][curatr]);
						sum += similaritymat[curatr][tmpatr];
					}
				}
				if (sum > maxsim) {
					maxsim = sum;
					mid = curatr;
				}
			}
		} else if (midkid == Middlekind.maxscore) {
			double maxscore = -100;
			for (int i = 0; i < leafs.size(); i++) {
				int curatr = (new Integer(leafs.elementAt(i).toString()))
						.intValue();
				if (maxscore < scoreforatt[curatr]) {
					maxscore = scoreforatt[curatr];
					mid = curatr;
				}
			}
		} else if (midkid == Middlekind.maxsimtoall) {
			double maxsim = -100;
			int[] a1 = new int[leafs.size()];
			int[] a2 = new int[1];
			for (int i = 0; i < leafs.size(); i++) {
				int curatr = (new Integer(leafs.elementAt(i).toString()))
						.intValue();
				a1[i] = curatr;
			}
			for (int i = 0; i < leafs.size(); i++) {
				int curatr = (new Integer(leafs.elementAt(i).toString()))
						.intValue();
				a2[0] = curatr;
				double sim = computesimilarity(ldataset, udataset, a1, a2,
						e_dis)[1];
				if (maxsim < sim) {
					maxsim = sim;
					mid = curatr;
				}
			}
		}
		return mid;
	}

	public static void hierarchicalcluster_remeber(int K_cluster,
			boolean isonline, Instances ldataset, Instances udataset) {
		int atrnum = similaritymat.length;
		topnodes = new Vector<Node>();
		treenodes = new Vector<Node>();
		for (int i = 0; i < atrnum; i++) {
			Node tmp = new Node(i, true);
			topnodes.add(tmp);
			treenodes.add(tmp);
		}
		int level = 0;
		rem_selectatr = new Vector<Vector<Integer>>();
		while (topnodes.size() > 1) {

			topK = new Node[topnodes.size()];
			for (int j = 0; j < topnodes.size(); j++) {
				topK[j] = topnodes.elementAt(j);
			}
			Vector<Integer> tmpv = new Vector<Integer>();
			for (int j = 0; j < topnodes.size(); j++) {
				tmpv.add(middleof(topK[j], ldataset, udataset));
			}
			rem_selectatr.add(tmpv);

			level++;
			double maxsimvalue = -100;
			Node nh = null, nk = null;
			int h = 0, k = 0;
			for (int i = 0; i < topnodes.size(); i++) {
				for (int j = i + 1; j < topnodes.size(); j++) {
					double simvalue;
					if (!isonline) {
						simvalue = getnodesimilarity(topnodes.elementAt(i),
								topnodes.elementAt(j));
					} else {
						simvalue = getdismatsimonline(topnodes.elementAt(i),
								topnodes.elementAt(j), ldataset, udataset);
					}
					if (simvalue > maxsimvalue) {
						maxsimvalue = simvalue;
						nh = topnodes.elementAt(i);
						nk = topnodes.elementAt(j);
						h = i;
						k = j;
					}
				}
			}
			Node tmp = new Node(level);
			tmp.setleftChild(topnodes.elementAt(h));
			tmp.setrightChild(topnodes.elementAt(k));
			topnodes.remove(nh);
			topnodes.remove(nk);
			topnodes.add(tmp);
			treenodes.add(tmp);
		}
		root = topnodes.elementAt(0);
		Vector<Integer> tmpv = new Vector<Integer>();
		tmpv.add(middleof(root, ldataset, udataset));
		rem_selectatr.add(tmpv);

		// rootlevel=level;
	}

	public static void displaytree(Node root) {
		if (root.isleaf)
			System.out.printf("*" + root.value + "*");
		else
			System.out.printf(" " + root.value + " ");

		if (root.leftChild != null) {
			System.out.printf("\nleft for " + root.value + " begin\n");
			displaytree(root.leftChild);
			System.out.printf("\nleft for " + root.value + " end\n");
		}
		if (root.rightChild != null) {
			System.out.printf("\nright for " + root.value + " begin\n");
			displaytree(root.rightChild);
			System.out.printf("\nright for " + root.value + " end\n");
		}
	}

	public static int[] atraddclass(int[] atrseq, int classIndex) {
		int[] artseq_new = new int[atrseq.length + 1];
		for (int i = 0; i < atrseq.length; i++) {
			artseq_new[i] = atrseq[i];
		}
		artseq_new[atrseq.length] = classIndex;
		return artseq_new;
	}

	public static Instances datawithselectatr(Instances inputdata,
			int[] select_art) throws Exception {

		Remove m_removeFilter = new Remove();
		int[] selectatr_with_class = null;

		inputdata.setClassIndex(inputdata.numAttributes() - 1);
		selectatr_with_class = atraddclass(select_art, inputdata.classIndex());
		Instances newData = null;
		try {
			m_removeFilter.setAttributeIndicesArray(selectatr_with_class);
			m_removeFilter.setInvertSelection(true);

			m_removeFilter.setInputFormat(inputdata);
			newData = Filter.useFilter(inputdata, m_removeFilter);
			newData.setClassIndex(newData.numAttributes() - 1);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return newData;
	}

	public static double getXB(Instances dataset, Instances centroids, int[] res) {
		double XB = 0;
		DissimilarityForKmodes disfun = new DissimilarityForKmodes();
		for (int i = 0; i < dataset.numInstances(); i++) {
			double dis2 = disfun.distance(dataset.instance(i),
					centroids.instance(res[i]));
			dis2 = dis2 * dis2;
			XB += dis2;
		}
		return XB;
	}

	public static int[] arryatrtobin(int[] arrayart, int len) {
		int[] binatr = new int[len];
		for (int i = 0; i < len; i++) {
			binatr[i] = 0;
		}
		for (int i = 0; i < arrayart.length; i++) {
			binatr[arrayart[i]] = 1;
		}
		return binatr;
	}

	public static double getIk(Instances dataset, Instances centroids, int[] res)
			throws Exception {
		double Ik = 0;
		DissimilarityForKmodes disfun = new DissimilarityForKmodes();
		double K = centroids.numInstances();
		double Dk = 0;
		for (int i = 0; i < centroids.numInstances(); i++) {
			for (int j = i + 1; j < centroids.numInstances(); j++) {
				double dis = disfun.distance(centroids.instance(i),
						centroids.instance(j));
				if (dis > Dk)
					Dk = dis;
			}
		}
		double E1 = 0;
		SimpleKModes skm = new SimpleKModes();
		//skm.setDistanceFunction(new DissimilarityForKmodes());
		skm.setNumClusters(1);
		skm.buildClusterer(dataset);
		Instances one_centroid = skm.getClusterCentroids();
		for (int i = 0; i < dataset.numInstances(); i++) {
			double dis = disfun.distance(dataset.instance(i),
					one_centroid.instance(0));
			E1 += dis;
		}

		double Ek = 0;
		for (int i = 0; i < dataset.numInstances(); i++) {
			double dis = disfun.distance(dataset.instance(i),
					centroids.instance(res[i]));
			Ek += dis;
		}
		Ik = Math.pow(((1 / K) * (E1 / Ek) * Dk), 2);
		return Ik;
	}

	public static double[] clustertest(Instances dataset) throws Exception {
		Instances newdata = new Instances(dataset);
		newdata.setClassIndex(-1);
		newdata.deleteAttributeAt(newdata.numAttributes() - 1);

		SimpleKModes skm = new SimpleKModes();
		skm.setNumClusters(dataset.classAttribute().numValues());
		skm.buildClusterer(newdata);
		Instances centroids = skm.getClusterCentroids();
		int[] res = new int[newdata.numInstances()];
		for (int i = 0; i < res.length; ++i) {
			res[i] = skm.clusterInstance(newdata.instance(i));
		}
		double XB = getXB(newdata, centroids, res);
		double IK = getIk(newdata, centroids, res);
		double CA = EvaluatedUFS.CA(dataset, res);
		double[] re = new double[3];
		re[0] = XB;
		re[1] = IK;
		re[2] = CA;
		return re;
	}

	public static evaluate_struct crosstest(Instances dataset) throws Exception {
		evaluate_struct allpct=new evaluate_struct();
		
		J48 classifier_j48 = new J48();
		Evaluation eval = new Evaluation(dataset);
		eval.crossValidateModel(classifier_j48, dataset, 5, new Random(1));
		allpct.J48pct=eval.pctCorrect();
		
		NaiveBayes classifier_nbc = new NaiveBayes();
		eval = new Evaluation(dataset);
		eval.crossValidateModel(classifier_nbc, dataset, 5, new Random(1));
		allpct.NBCpct=eval.pctCorrect();

		SimpleCart classifier_zero = new SimpleCart();
		eval = new Evaluation(dataset);
		eval.crossValidateModel(classifier_zero, dataset, 5, new Random(1));
		allpct.CartRpct=eval.pctCorrect();

		DecisionTable classifier_one = new DecisionTable();
		eval = new Evaluation(dataset);
		eval.crossValidateModel(classifier_one, dataset, 5, new Random(1));
		allpct.DTpct=eval.pctCorrect();
				
		RandomForest classifier_randf = new RandomForest();
		eval = new Evaluation(dataset);
		eval.crossValidateModel(classifier_randf, dataset, 5, new Random(1));
		allpct.RandomForestpct=eval.pctCorrect();

		Lem2 classifier_lem2 = new Lem2();
		eval = new Evaluation(dataset);
		eval.crossValidateModel(classifier_lem2, dataset, 5, new Random(1));
		allpct.Lem2pct=eval.pctCorrect();
				
		return allpct;
	}

	public static evaluate_struct myevevaluate(Instances dataset, int[] arts,
			boolean showmsg) throws Exception {
		int[] artpos = getpos(dataset, arts);
		evaluate_struct evaluatevalue = new evaluate_struct();
		Instances newData = datawithselectatr(dataset, arts);

		evaluatevalue = crosstest(newData);
		
		return evaluatevalue;
	}

    public static String getExcelColumnName(int celNum) {  
        int num = celNum + 1;//celNum是从0算起  
        String tem = "";  
        while(num > 0) {  
            int lo = (num - 1) % 26;//取余，A到Z是26进制，  
            tem = (char)(lo + 'A') + tem;  
            num = (num - 1) / 26;//取模  
        }  
        return tem;  
    }  
	public static void printevaluateset(Vector<evaluate_struct> ev,
			BufferedWriter output) throws IOException {
		int cellpos = 2;
		row = sheet.getRow(evabias[0] + curbase + curbias);
		for (int i = ev.size() - 1; i > -1; i--) {
			output.write(ev.elementAt(i).J48pct + "\t");
			row.createCell(cellpos).setCellValue(ev.elementAt(i).J48pct);
			row.getCell(cellpos).setCellStyle(row.getRowStyle());
			cellpos++;
		}
		int rownum=row.getRowNum()+1;
		String avgformulastr="AVERAGE("+getExcelColumnName(2)+rownum+":"+getExcelColumnName(cellpos-1)+rownum+")";
		row.createCell(cellpos).setCellFormula(avgformulastr);
		row.getCell(cellpos).setCellStyle(row.getRowStyle());

		output.write("\n");
		cellpos = 2;
		row = sheet.getRow(evabias[1] + curbase + curbias);
		for (int i = ev.size() - 1; i > -1; i--) {
			output.write(ev.elementAt(i).NBCpct + "\t");
			row.createCell(cellpos).setCellValue(ev.elementAt(i).NBCpct);
			row.getCell(cellpos).setCellStyle(row.getRowStyle());
			cellpos++;
		}
		rownum=row.getRowNum()+1;
		avgformulastr="AVERAGE("+getExcelColumnName(2)+rownum+":"+getExcelColumnName(cellpos-1)+rownum+")";
		row.createCell(cellpos).setCellFormula(avgformulastr);
		row.getCell(cellpos).setCellStyle(row.getRowStyle());
		
		output.write("\n");
		cellpos = 2;
		row = sheet.getRow(evabias[2] + curbase + curbias);
		for (int i = ev.size() - 1; i > -1; i--) {
			output.write(ev.elementAt(i).CartRpct + "\t");
			row.createCell(cellpos).setCellValue(ev.elementAt(i).CartRpct);
			row.getCell(cellpos).setCellStyle(row.getRowStyle());
			cellpos++;
		}
		rownum=row.getRowNum()+1;
		avgformulastr="AVERAGE("+getExcelColumnName(2)+rownum+":"+getExcelColumnName(cellpos-1)+rownum+")";
		row.createCell(cellpos).setCellFormula(avgformulastr);
		row.getCell(cellpos).setCellStyle(row.getRowStyle());

		output.write("\n");
		cellpos = 2;
		row = sheet.getRow(evabias[3] + curbase + curbias);
		for (int i = ev.size() - 1; i > -1; i--) {
			output.write(ev.elementAt(i).DTpct + "\t");
			row.createCell(cellpos).setCellValue(ev.elementAt(i).DTpct);
			row.getCell(cellpos).setCellStyle(row.getRowStyle());
			cellpos++;
		}
		rownum=row.getRowNum()+1;
		avgformulastr="AVERAGE("+getExcelColumnName(2)+rownum+":"+getExcelColumnName(cellpos-1)+rownum+")";
		row.createCell(cellpos).setCellFormula(avgformulastr);
		row.getCell(cellpos).setCellStyle(row.getRowStyle());

		output.write("\n");
		cellpos = 2;
		row = sheet.getRow(evabias[4] + curbase + curbias);
		for (int i = ev.size() - 1; i > -1; i--) {
			output.write(ev.elementAt(i).RandomForestpct + "\t");
			row.createCell(cellpos).setCellValue(ev.elementAt(i).RandomForestpct);
			row.getCell(cellpos).setCellStyle(row.getRowStyle());
			cellpos++;
		}rownum=row.getRowNum()+1;
		avgformulastr="AVERAGE("+getExcelColumnName(2)+rownum+":"+getExcelColumnName(cellpos-1)+rownum+")";
		row.createCell(cellpos).setCellFormula(avgformulastr);
		row.getCell(cellpos).setCellStyle(row.getRowStyle());

		output.write("\n");
		cellpos = 2;
		row = sheet.getRow(evabias[5] + curbase + curbias);
		for (int i = ev.size() - 1; i > -1; i--) {
			output.write(ev.elementAt(i).Lem2pct + "\t");
			row.createCell(cellpos).setCellValue(ev.elementAt(i).Lem2pct);
			row.getCell(cellpos).setCellStyle(row.getRowStyle());
			cellpos++;
		}
		rownum=row.getRowNum()+1;
		avgformulastr="AVERAGE("+getExcelColumnName(2)+rownum+":"+getExcelColumnName(cellpos-1)+rownum+")";
		row.createCell(cellpos).setCellFormula(avgformulastr);
		row.getCell(cellpos).setCellStyle(row.getRowStyle());

		output.write("\n");
		// for(int i=ev.size()-1;i>-1;i--){
		// output.write(ev.elementAt(i).CA+"\t");
		// }
		// output.write("\n");
	}

	public static evaluate_struct semiroughtest(Instances dataset,
			Instances ldataset, Instances udataset, double fileno,
			Labeldis e_dis) throws Exception {

		evaluate_struct evaluatevalue;
		Vector<evaluate_struct> evaluatevalueset = new Vector<evaluate_struct>();
		BufferedWriter output = null;
		boolean isonline = false;

		if (e_dis == Labeldis.dismatonline) {
			isonline = true;
			// e_dis=Labeldis.dismat;
		}

		try {
			String methodstr = "";
			if (e_dis == Labeldis.pos) {
				methodstr = new String("pos");
			} else if (e_dis == Labeldis.dismatonline) {
				methodstr = new String("onlinemat");
			} else if (e_dis == Labeldis.dismat) {
				methodstr = new String("mat");
			}else if (e_dis == Labeldis.dismatall) {
				methodstr = new String("matall");
			}
			File outfile = new File("result_labelratio" + methodstr + fileno
					+ ".txt");// 需要读取的文件路径
			if (outfile.exists()) {
				System.out.printf("文件存在\n");
			} else {
				System.out.printf("文件不存在\n");
				outfile.createNewFile();// 不存在则创建
			}
			output = new BufferedWriter(new FileWriter(outfile));
		} catch (Exception e) {
			e.printStackTrace();
		}

		output.write("unlable ratio:" + unlabel_ratio + "\n");

		if (e_dis == Labeldis.pos) {
			computepos(ldataset);
		}
		if (midkid == Middlekind.maxscore) {
			computescore(ldataset, udataset, e_dis);
		}

		computesimilarities(ldataset, udataset, e_dis);
		hierarchicalcluster_remeber(select_atr_num, isonline, ldataset,
				udataset);
		dataset.setClassIndex(dataset.numAttributes() - 1);

		output.write("Semi-supervised rough set method:" + "\n");
		for (int i = 0; i < rem_selectatr.size(); i++) {
			selectatr = new int[rem_selectatr.elementAt(i).size()];
			for (int j = 0; j < rem_selectatr.elementAt(i).size(); j++) {
				selectatr[j] = rem_selectatr.elementAt(i).elementAt(j);
			}

			evaluatevalue = myevevaluate(dataset, selectatr, true);
			evaluatevalueset.add(evaluatevalue);
			output.write(rem_selectatr.elementAt(i).size() + ": ");

			for (int j = 0; j < selectatr.length; j++) {
				output.write(selectatr[j] + " ");
			}
			output.write("\n");
			// output.write("pos: " + evaluatevalue.pos + "\n");
			// output.write("entropy: " + evaluatevalue.entropy + "\n");
			// output.write("pct: " + evaluatevalue.pct + "\n");
			// output.write("appac: " + evaluatevalue.appac + "\n");
		}
		printevaluateset(evaluatevalueset, output);

		evaluate_struct m = new evaluate_struct();
//		m.pos = 0;
//		m.entropy = 0;
//		m.pct = 0;
//		for (int i = 0; i < evaluatevalueset.size(); i++) {
//			m.pos += evaluatevalueset.elementAt(i).pos;
//			m.entropy += evaluatevalueset.elementAt(i).entropy;
//			m.pct += evaluatevalueset.elementAt(i).pct;
//		}
//		m.pos /= evaluatevalueset.size();
//		m.entropy /= evaluatevalueset.size();
//		m.pct /= evaluatevalueset.size();
		output.close();
		return m;
	}

	public static void semiroughrankertest(Instances dataset,
			Instances ldataset, Instances udataset, double fileno,
			Labeldis e_dis) throws Exception {
		evaluate_struct evaluatevalue;
		Vector<evaluate_struct> evaluatevalueset = new Vector<evaluate_struct>();
		computescore(ldataset, udataset, e_dis);
		Attwithscore[] attwithscore=new Attwithscore[udataset.numAttributes()];
		for(int i=0;i<attwithscore.length;i++){
			attwithscore[i]=new Attwithscore(i,scoreforatt[i]);
		}
		Arrays.sort(attwithscore);	
		for (select_atr_num = dataset.numAttributes() - 1; select_atr_num > 0; select_atr_num--) {
			int[] sersrankselect = new int[select_atr_num];
			for (int i = 0; i < select_atr_num; i++) {
				sersrankselect[i] = attwithscore[i].attno;
		}
		evaluatevalue = myevevaluate(dataset, sersrankselect, true);
		evaluatevalueset.add(evaluatevalue);
	}
		String methodstr="";
		if (e_dis == Labeldis.pos) {
			methodstr = new String("pos");
		} else if (e_dis == Labeldis.dismatonline) {
			methodstr = new String("onlinemat");
		} else if (e_dis == Labeldis.dismat) {
			methodstr = new String("mat");
		}else if (e_dis == Labeldis.dismatall) {
			methodstr = new String("matall");
		}
		File outfile = new File("result_labelratio" + methodstr + fileno
				+ ".txt");// 需要读取的文件路径
		if (outfile.exists()) {
			System.out.printf("文件存在\n");
		} else {
			System.out.printf("文件不存在\n");
			outfile.createNewFile();// 不存在则创建
		}
		
		BufferedWriter output = new BufferedWriter(new FileWriter(outfile));
		printevaluateset(evaluatevalueset, output);
	}	

	public static void comptest(Instances dataset) throws Exception {

		evaluate_struct evaluatevalue = null;
		Vector<evaluate_struct> evaluatevalueset = new Vector<evaluate_struct>();
		BufferedWriter output = null;
		try {
			File outfile = new File("result.txt");// 需要读取的文件路径
			if (outfile.exists()) {
				System.out.printf("文件存在\n");
			} else {
				System.out.printf("文件不存在\n");
				outfile.createNewFile();// 不存在则创建
			}
			output = new BufferedWriter(new FileWriter(outfile));
		} catch (Exception e) {
			e.printStackTrace();
		}

		// int atrnum=dataset.numAttributes();

		// System.out.printf("total:"+dataset.numInstances()+" labeled:"+
		// ldataset.numInstances()+" unlabeled:"+udataset.numInstances()+
		// " atr:"+atrnum+"\n");
		// set data end

		long startTime = System.currentTimeMillis();
		// random
		curbias = 0;
		evaluatevalueset.clear();
		output.write("\n" + "Random method:" + "\n");
		for (select_atr_num = dataset.numAttributes() - 1; select_atr_num > 0; select_atr_num--) {
			double rand_j = 0;
			double rand_n = 0;
			double rand_z = 0;
			double rand_o = 0;
			double rand_r = 0;
			double rand_l = 0;
			//double rand_CA = 0;
			for (int iter = 0; iter < RAND_TEST_NUM; iter++) {
				int[] randselect = new int[select_atr_num];
				Vector<Integer> atrv = new Vector<Integer>();
				for (int i = 0; i < dataset.numAttributes() - 1; i++) {
					atrv.add(i);
				}
				Random atrand = new Random(1);
				for (int i = 0; i < select_atr_num; i++) {
					int r = (int) atrand.nextInt(atrv.size());
					randselect[i] = atrv.elementAt(r);
					atrv.removeElementAt(r);
				}

				// Instances newData_rand = datawithselectatr(dataset,
				// randselect);
				// crosstest(newData_rand);

				evaluatevalue = myevevaluate(dataset, randselect, false);
				rand_j += evaluatevalue.J48pct;
				rand_n += evaluatevalue.NBCpct;
				rand_z += evaluatevalue.CartRpct;
				rand_o += evaluatevalue.DTpct;
				rand_r += evaluatevalue.RandomForestpct;
				rand_l += evaluatevalue.Lem2pct;
//				rand_CA += evaluatevalue.CA;
			}
			rand_j /= RAND_TEST_NUM;
			rand_n /= RAND_TEST_NUM;
			rand_z /= RAND_TEST_NUM;
			rand_o /= RAND_TEST_NUM;
			rand_r /= RAND_TEST_NUM;
			rand_l /= RAND_TEST_NUM;

			evaluatevalue = new evaluate_struct();
			evaluatevalue.J48pct = (int) rand_j;
			evaluatevalue.NBCpct = rand_n;
			evaluatevalue.CartRpct = rand_z;
			evaluatevalue.DTpct = rand_o;
			evaluatevalue.RandomForestpct = rand_r;
			evaluatevalue.Lem2pct = rand_l;

			evaluatevalueset.add(evaluatevalue);
			// output.write(select_atr_num + "\n");
			// output.write("pos: " + rand_pos + "\n");
			// output.write("entropy: " + rand_entropy + "\n");
			// output.write("pct: " + rand_pct + "\n");
		}
		printevaluateset(evaluatevalueset, output);

		// random2-signle
		curbias = 1;
		evaluatevalueset.clear();
		output.write("\n" + "Random method2:" + "\n");
		for (select_atr_num = dataset.numAttributes() - 1; select_atr_num > 0; select_atr_num--) {
			int[] randselect2 = new int[select_atr_num];
			Vector<Integer> atrv = new Vector<Integer>();
			for (int i = 0; i < dataset.numAttributes() - 1; i++) {
				atrv.add(i);
			}
			Random atrand = new Random(100);
			for (int i = 0; i < select_atr_num; i++) {
				int r = (int) atrand.nextInt(atrv.size());
				randselect2[i] = atrv.elementAt(r);
				atrv.removeElementAt(r);
			}
			// Instances newData_rand = datawithselectatr(dataset, randselect);
			// crosstest(newData_rand);
			evaluatevalue = myevevaluate(dataset, randselect2, false);
			evaluatevalueset.add(evaluatevalue);
		}
		printevaluateset(evaluatevalueset, output);

		// ranker
		curbias = 2;
		evaluatevalueset.clear();

		// InfoGainAttributeEval eval=new InfoGainAttributeEval();
		GainRatioAttributeEval eval = new GainRatioAttributeEval();
		Ranker search = new Ranker();

		// AttributeSelection attsel = new AttributeSelection();
		// attsel.setEvaluator(eval);
		// attsel.setSearch(search);
		// attsel.SelectAttributes(dataset);
		// int attarray[] =attsel.selectedAttributes();

		eval.buildEvaluator(dataset);
		int[] ranker_sort = search.search(eval, dataset);

		output.write("\n" + "Ranker method:" + "\n");
		for (select_atr_num = dataset.numAttributes() - 1; select_atr_num > 0; select_atr_num--) {
			int[] rankerselect = new int[select_atr_num];
			for (int i = 0; i < select_atr_num; i++) {
				rankerselect[i] = ranker_sort[i];
			}
			// Arrays.sort(rankerselect);
			evaluatevalue = myevevaluate(dataset, rankerselect, true);
			evaluatevalueset.add(evaluatevalue);
			// output.write(select_atr_num + "\n");
			// output.write("pos: " + evaluatevalue.pos + "\n");
			// output.write("entropy: " + evaluatevalue.entropy + "\n");
			// output.write("pct: " + evaluatevalue.pct + "\n");
		}
		printevaluateset(evaluatevalueset, output);

		// rspos
		int[] rs_sort = new int[dataset.numAttributes() - 1];
		boolean[] visited = new boolean[dataset.numAttributes() - 1];
		Arrays.fill(visited, false);
		int[] rs_score = new int[dataset.numAttributes() - 1];
		int[] rs_score_sort = new int[dataset.numAttributes() - 1];
		for (int i = 0; i < dataset.numAttributes() - 1; i++) {
			int[] a = new int[1];
			a[0] = i;
			int[] posa = getpos(dataset, a);
			int possum = 0;
			for (int j = 0; j < posa.length; j++)
				possum += posa[j];
			rs_score[i] = possum;
			rs_score_sort[i] = possum;
		}
		Arrays.sort(rs_score_sort);
		for (int i = 0; i < rs_score.length; i++) {
			for (int j = 0; j < rs_score_sort.length; j++) {
				if (!visited[j] && rs_score[i] == rs_score_sort[j]) {
					rs_sort[rs_score_sort.length - j - 1] = i;
					visited[j] = true;
					break;
				}
			}
		}
		curbias = 3;
		evaluatevalueset.clear();
		output.write("\n" + "RS rank method2:" + "\n");
		for (select_atr_num = dataset.numAttributes() - 1; select_atr_num > 0; select_atr_num--) {
			int[] rsrankselect = new int[select_atr_num];
			for (int i = 0; i < select_atr_num; i++) {
				rsrankselect[i] = rs_sort[i];
			}
			// Arrays.sort(rankerselect);
			evaluatevalue = myevevaluate(dataset, rsrankselect, true);
			evaluatevalueset.add(evaluatevalue);
			// output.write(select_atr_num + "\n");
			// output.write("pos: " + evaluatevalue.pos + "\n");
			// output.write("entropy: " + evaluatevalue.entropy + "\n");
			// output.write("pct: " + evaluatevalue.pct + "\n");
		}
		printevaluateset(evaluatevalueset, output);

		try {
			output.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return;
	}

	public static void main(String[] args) throws Exception {
		System.out.printf("Runing begin with PCT!\n");
		// set-up
		RAND_TEST_NUM = 1;
		lpar = 1;
		upar = 1;
		method = 2;
		e_dis = Labeldis.dismat;
		select_atr_num = 1;
		// int[] artpos=null;
		dataset_no = 1;
		unlabel_ratio = 0.8;
		EMPTY_VALUE = 0;
		node_sim = Nodesim.min;
		midkid = Middlekind.maxsim;
//		String[] datastr = { "zoo", "tic-tac-toe", "mushroom", "data1",
//				"data2", "soybean", "kr-vs-kp"};
		String[] datastr = {"data_100_20_5_10_10_500"};
		for (int dataset_iter = 0; dataset_iter < datastr.length; dataset_iter++) {
			dataset_no = dataset_iter + 1;
			book = new XSSFWorkbook();
			dataname = datastr[dataset_no - 1];
			File f = null;
			f = new File("E:\\code\\eclipse\\SVN_lib\\XattReduct\\mydata\\"
					+ dataname + ".arff");
			// testcode
			// f= new File("C:\\Users\\luosha865\\Desktop\\myzootest.arff");
			// set data begin
			Instances dataset = new Instances(new FileReader(f));
			for (int i = 0; i < dataset.numAttributes(); i++)
				dataset.deleteWithMissing(i);
			System.out.printf(dataset.numInstances() + "\n");
			Random random = new Random(0);
			dataset.randomize(random);
			dataset.setClassIndex(dataset.numAttributes() - 1);
			long st = System.currentTimeMillis();
			File outfile2 = null;
			BufferedWriter output2 = null;

			double[] test_unlabel_ratio = { 0, 0.2, 0.4, 0.6, 0.8, 1 };
			ratiolength = test_unlabel_ratio.length;
			sheet = book.createSheet(dataname);
			// Label label = new Label( 0 , 0 , " test " );
			// sheet.addCell(label);
			// jxl.write.Number number = new jxl.write.Number( 1 , 0 , 555.12541
			// );
			// sheet.addCell(number);
			cursheetline = 0;
			String[] evalstr = { "J48", "NBC", "Cart", "DecisionTable", "RandomForest", "Lem2" };

			XSSFCellStyle headstyle = book.createCellStyle();
			headstyle.setFillBackgroundColor(IndexedColors.BLUE.getIndex());
			headstyle.setFillForegroundColor(IndexedColors.BLUE.getIndex());
			headstyle.setFillPattern(IndexedColors.BLUE.getIndex());
			headstyle.setBorderBottom(CellStyle.BORDER_THIN);
			headstyle.setBorderTop(CellStyle.BORDER_THIN);

			XSSFCellStyle bordertop = book.createCellStyle();
			bordertop.setBorderTop(CellStyle.BORDER_THIN);

			XSSFCellStyle borderbottom = book.createCellStyle();
			borderbottom.setBorderBottom(CellStyle.BORDER_THIN);

			XSSFCellStyle bordertopbottom = book.createCellStyle();
			bordertopbottom.setBorderTop(CellStyle.BORDER_THIN);
			bordertopbottom.setBorderBottom(CellStyle.BORDER_THIN);

			// // sheet.addMergedRegion(new CellRangeAddress(2,3,0,0));
			for (int i = 0; i < evalstr.length; i++) {
				row = sheet.createRow(cursheetline);
				row.setRowStyle(headstyle);
				row.createCell(0).setCellValue(evalstr[i]);
				row.getCell(0).setCellStyle(row.getRowStyle());
				for (int j = 0; j < dataset.numAttributes()-1; j++) {
					row.createCell(j + 2).setCellValue(j+1);
					row.getCell(j + 2).setCellStyle(row.getRowStyle());
				}
				cursheetline++;

				evabias[i] = cursheetline;

				for (int j = 0; j < ratiolength; j++) {
					row = sheet.createRow(cursheetline + j);
					row.createCell(1).setCellValue(
							test_unlabel_ratio[j] * 100 + "%unlabel");
					row.getCell(1).setCellStyle(row.getRowStyle());
					if (j == 0) {
						row.setRowStyle(bordertop);
					} else if (j == ratiolength - 1) {
						row.setRowStyle(borderbottom);
					}

				}
				sheet.getRow(cursheetline).createCell(0).setCellValue("dismat");
				sheet.getRow(cursheetline).getCell(0)
						.setCellStyle(row.getRowStyle());
				sheet.addMergedRegion(new CellRangeAddress(cursheetline,
						cursheetline + ratiolength - 1, 0, 0));
				cursheetline += ratiolength;
				for (int j = 0; j < ratiolength; j++) {
					row = sheet.createRow(cursheetline + j);
					row.createCell(1).setCellValue(
							test_unlabel_ratio[j] * 100 + "%unlabel");
					row.getCell(1).setCellStyle(row.getRowStyle());
					if (j == 0) {
						row.setRowStyle(bordertop);
					} else if (j == ratiolength - 1) {
						row.setRowStyle(borderbottom);
					}

				}
				sheet.getRow(cursheetline).createCell(0).setCellValue("dismat_rsranker");
				sheet.getRow(cursheetline).getCell(0)
						.setCellStyle(row.getRowStyle());
				sheet.addMergedRegion(new CellRangeAddress(cursheetline,
						cursheetline + ratiolength - 1, 0, 0));
				cursheetline += ratiolength;
				
				
				for (int j = 0; j < ratiolength; j++) {
					row = sheet.createRow(cursheetline + j);
					row.createCell(1).setCellValue(
							test_unlabel_ratio[j] * 100 + "%unlabel");
					row.getCell(1).setCellStyle(row.getRowStyle());
					if (j == 0) {
						row.setRowStyle(bordertop);
					} else if (j == ratiolength - 1) {
						row.setRowStyle(borderbottom);
					}

				}
				sheet.getRow(cursheetline).createCell(0).setCellValue("dismatall");
				sheet.getRow(cursheetline).getCell(0)
						.setCellStyle(row.getRowStyle());
				sheet.addMergedRegion(new CellRangeAddress(cursheetline,
						cursheetline + ratiolength - 1, 0, 0));
				cursheetline += ratiolength;
				for (int j = 0; j < ratiolength; j++) {
					row = sheet.createRow(cursheetline + j);
					row.createCell(1).setCellValue(
							test_unlabel_ratio[j] * 100 + "%unlabel");
					row.getCell(1).setCellStyle(row.getRowStyle());
					if (j == 0) {
						row.setRowStyle(bordertop);
					} else if (j == ratiolength - 1) {
						row.setRowStyle(borderbottom);
					}

				}
				sheet.getRow(cursheetline).createCell(0).setCellValue("dismatall_rsranker");
				sheet.getRow(cursheetline).getCell(0)
						.setCellStyle(row.getRowStyle());
				sheet.addMergedRegion(new CellRangeAddress(cursheetline,
						cursheetline + ratiolength - 1, 0, 0));
				cursheetline += ratiolength;

				
				for (int j = 0; j < ratiolength; j++) {
					row = sheet.createRow(cursheetline + j);
					row.createCell(1).setCellValue(
							test_unlabel_ratio[j] * 100 + "%unlabel");
					row.getCell(1).setCellStyle(row.getRowStyle());
					if (j == 0) {
						row.setRowStyle(bordertop);
					} else if (j == ratiolength - 1) {
						row.setRowStyle(borderbottom);
					}

				}
				sheet.getRow(cursheetline).createCell(0).setCellValue("pos");
				sheet.getRow(cursheetline).getCell(0)
						.setCellStyle(row.getRowStyle());
				sheet.addMergedRegion(new CellRangeAddress(cursheetline,
						cursheetline + ratiolength - 1, 0, 0));
				cursheetline += ratiolength;
				
				for (int j = 0; j < ratiolength; j++) {
					row = sheet.createRow(cursheetline + j);
					row.createCell(1).setCellValue(
							test_unlabel_ratio[j] * 100 + "%unlabel");
					row.getCell(1).setCellStyle(row.getRowStyle());
					if (j == 0) {
						row.setRowStyle(bordertop);
					} else if (j == ratiolength - 1) {
						row.setRowStyle(borderbottom);
					}

				}
				sheet.getRow(cursheetline).createCell(0).setCellValue("pos_rsranker");
				sheet.getRow(cursheetline).getCell(0)
						.setCellStyle(row.getRowStyle());
				sheet.addMergedRegion(new CellRangeAddress(cursheetline,
						cursheetline + ratiolength - 1, 0, 0));
				cursheetline += ratiolength;
				row = sheet.createRow(cursheetline);
				row.setRowStyle(bordertopbottom);
				row.createCell(0).setCellValue("rand1");
				row.getCell(0).setCellStyle(row.getRowStyle());
				cursheetline++;
				row = sheet.createRow(cursheetline);
				row.setRowStyle(bordertopbottom);
				row.createCell(0).setCellValue("rand2");
				row.getCell(0).setCellStyle(row.getRowStyle());
				cursheetline++;
				row = sheet.createRow(cursheetline);
				row.setRowStyle(bordertopbottom);
				row.createCell(0).setCellValue("ranker");
				row.getCell(0).setCellStyle(row.getRowStyle());
				cursheetline++;
				row = sheet.createRow(cursheetline);
				row.setRowStyle(bordertopbottom);
				row.createCell(0).setCellValue("rsranker");
				row.getCell(0).setCellStyle(row.getRowStyle());
				cursheetline++;
				cursheetline++;
			}

			for (int i = 0; i < test_unlabel_ratio.length; i++) {
				curbias = i;
				unlabel_ratio = test_unlabel_ratio[i];
				int ulabelnum = (int) (dataset.numInstances() * unlabel_ratio);
				int labelnum = dataset.numInstances() - ulabelnum;
				Instances ldataset = null, udataset = null;
				ldataset = new Instances(dataset, labelnum);
				udataset = new Instances(dataset, ulabelnum);
				for (int j = 0; j < dataset.numInstances(); j++) {
					if (j < labelnum) {
						ldataset.add(dataset.instance(j));
					} else {
						udataset.add(dataset.instance(j));
					}
				}
				ldataset.setClassIndex(ldataset.numAttributes() - 1);
				udataset.setClassIndex(-1);
				udataset.deleteAttributeAt(udataset.numAttributes() - 1);
				e_dis = Labeldis.dismat;
				curbase = 0;
				semiroughtest(dataset, ldataset, udataset, unlabel_ratio, e_dis);
				curbase += ratiolength;
				semiroughrankertest(dataset, ldataset, udataset, unlabel_ratio, e_dis);
				
				e_dis = Labeldis.dismatall;
				curbase += ratiolength;
				semiroughtest(dataset, ldataset, udataset, unlabel_ratio, e_dis);
				curbase += ratiolength;
				semiroughrankertest(dataset, ldataset, udataset, unlabel_ratio, e_dis);
				
				e_dis = Labeldis.pos;
				curbase += ratiolength;
				semiroughtest(dataset, ldataset, udataset, unlabel_ratio, e_dis);
				curbase += ratiolength;
				semiroughrankertest(dataset, ldataset, udataset, unlabel_ratio, e_dis);
			}
			long et1 = System.currentTimeMillis();

			outfile2 = new File("time1.txt");
			outfile2.createNewFile();
			output2 = new BufferedWriter(new FileWriter(outfile2));
			output2.write("time:" + (et1 - st));
			output2.close();
			curbase += ratiolength;
			comptest(dataset);

			long et2 = System.currentTimeMillis();
			output2 = null;
			outfile2 = new File("time2.txt");
			outfile2.createNewFile();
			output2 = new BufferedWriter(new FileWriter(outfile2));
			output2.write("time:" + (et2 - st));
			output2.close();

			FileOutputStream fileOut;
			fileOut = new FileOutputStream("ans\\semiRSfs_"+dataname+".xlsx");
			book.write(fileOut);
			fileOut.close();
			System.out.println("写入成功，运行结束！");
		}
		/*
		 * test_unlabel_ratio=new double[100]; for(int
		 * i=0;i<test_unlabel_ratio.length;i++){ test_unlabel_ratio[i]=0.01*i; }
		 * Vector<evaluate_struct> v_matonline=new Vector<evaluate_struct>();
		 * Vector<evaluate_struct> v_mat=new Vector<evaluate_struct>();
		 * Vector<evaluate_struct> v_pos=new Vector<evaluate_struct>();
		 * 
		 * evaluate_struct re; for(int i=0;i<test_unlabel_ratio.length;i++){
		 * unlabel_ratio=test_unlabel_ratio[i]; e_dis=Labeldis.dismatonline;
		 * re=semiroughtest(unlabel_ratio,e_dis); v_matonline.add(re);
		 * e_dis=Labeldis.dismat; re=semiroughtest(unlabel_ratio,e_dis);
		 * v_mat.add(re); e_dis=Labeldis.pos;
		 * re=semiroughtest(unlabel_ratio,e_dis); v_pos.add(re); }
		 * BufferedWriter output2=null; File outfile2 = new
		 * File("ratio100.txt"); outfile2.createNewFile(); output2= new
		 * BufferedWriter(new FileWriter(outfile2));
		 * 
		 * printevaluateset(v_matonline,output2);
		 * printevaluateset(v_mat,output2); printevaluateset(v_pos,output2);
		 * output2.close();
		 */
		return;
	}
}
