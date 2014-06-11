package Modification;

import java.io.FileReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Vector;

import myUtils.xFigure;
import myUtils.xMath;

import weka.core.Instances;
import Xreducer_core.Utils_entropy;
import Xreducer_fuzzy.ITStyle_KleeneDienes;
import Xreducer_fuzzy.ITStyle_Lukasiewicz;
import Xreducer_fuzzy.ImplicatorTnormStyle;
import Xreducer_fuzzy.SStyle_Abs1lambda_VmaxVmin;
import Xreducer_fuzzy.SStyle_MaxMin;
import Xreducer_fuzzy.SimilarityStyle;

public class newTest2 {
	 
	public static void test1() throws Exception{ //±È½ÏI T
	
		//String path1 = "C:/Users/Eric/Desktop/2011Çï¶¬/Code/Xreducer/data/Data/cleveland.arff";
		//String path1 = "C:/Users/Eric/Desktop/2011Çï¶¬/Code/Xreducer/data/Data/colic.arff";
		//String path1 = "C:/Users/Eric/Desktop/2011Çï¶¬/Code/Xreducer/data/Data/credit.arff";
		//String path1 = "C:/Users/Eric/Desktop/2011Çï¶¬/Code/Xreducer/data/Data/ecoli.arff";
		//String path1 = "C:/Users/Eric/Desktop/2011Çï¶¬/Code/Xreducer/data/Data/glass.arff";
		//String path1 = "C:/Users/Eric/Desktop/2011Çï¶¬/Code/Xreducer/data/Data/heart.arff";
		//String path1 = "C:/Users/Eric/Desktop/2011Çï¶¬/Code/Xreducer/data/Data/hepatitis.arff";
		//String path1 = "C:/Users/Eric/Desktop/2011Çï¶¬/Code/Xreducer/data/Data/ionosphere.arff";
		String path1 = "C:/Users/Eric/Desktop/2011Çï¶¬/Code/Xreducer/data/Data/wine.arff";
		
		
		//String path4 = "C:/Users/Eric/Desktop/2011Çï¶¬/Code/Xreducer/data/Data/vote.arff";
		
		
		//Instances data = testUncertaintyforFRS.generateInstances(10,6,2,1,1);
		Instances data = new Instances(new FileReader(path1));
		data.setClassIndex(data.numAttributes()-1);

		
		//Instances data = testUncertaintyforFRS.generateInstances(100,20,3,1,0);
		SimilarityStyle sstyle = new SStyle_MaxMin();
		//SimilarityStyle sstyle = new SStyle_Abs1lambda_VmaxVmin(4);
		
		//ImplicatorTnormStyle s = new ITStyle_KleeneDienes(); 
		//ImplicatorTnormStyle itstyle = new ITStyle_Lukasiewicz();
		
		
		Map<String, double[][]> m = new HashMap<String, double[][]>();
		boolean X[] = new boolean[data.numInstances()];
		for(int k=0;k<data.numInstances()/4;++k)
			X[k]=true;
		
		Random rd = new Random(1);
		int[] Xtmp = new int[data.numInstances()];
		for(int k=0;k<data.numInstances();++k)
		{
			Xtmp[k]=k;
		}
		for(int k=1;k<data.numInstances();++k)
		{
			int compind = rd.nextInt(k);
			int tmp = Xtmp[compind];
			Xtmp[compind] = Xtmp[k];
			Xtmp[k]=tmp;
		}
		
		double Xi[] = new double[data.numInstances()];
		for(int k=0;k<data.numInstances()/4;++k)
		{
			Xi[Xtmp[k]]=rd.nextDouble();
		}
		
		double Xii[] = new double[data.numInstances()];
		for(int k=0;k<data.numInstances();++k)
		{
			Xii[k]=rd.nextDouble();
		}
		boolean B[] = new boolean[data.numAttributes()];
		for(int k=0;k<(data.numAttributes()-1)/2;++k){
			B[k]=true;
		}
		
		//System.out.println(Arrays.toString(Xi));
		
		ImplicatorTnormStyle Tl = new ITStyle_Lukasiewicz();
		ImplicatorTnormStyle Il = new ITStyle_Lukasiewicz();
		
		ImplicatorTnormStyle Tm = new ITStyle_KleeneDienes();
		ImplicatorTnormStyle Im_R = new ITStyle_KleeneDienes_R();

		
		ImplicatorTnormStyle Tp = new ITStyle_Probabilistic_R();
		ImplicatorTnormStyle Ip_R = new ITStyle_Probabilistic_R();

		
		
		
		testUncertaintyforFRS ts = new testUncertaintyforFRS(data,sstyle,Tl);
		int N = 20; 
		double[][] ans = new double[2][N];
		double alpha = 0;
		
		for(int k=0;k<N;++k){
			 
			ans[0][k] = alpha;
			//ans[1][k] = ts.getAccuracy(B, Xi,alpha,0,Il,Tl);
			//ans[1][k] = ts.getAccuracy(B, Xi,0,alpha,Il,Tl);
			//ans[1][k] =ts.getAAsum(B, Xii, alpha, 0, Il, Tl);
			ans[1][k] =ts.getAAsum(B, Xii,0, alpha,  Il, Tl);
			alpha = alpha + 0.05;
			
		}
		m.put("L", ans);
		
		testUncertaintyforFRS ts2 = new testUncertaintyforFRS(data,sstyle,Tm);
		 
		double[][] ans2 = new double[2][N];
		alpha = 0;
		for(int k=0;k<N;++k){
		 
			ans2[0][k] = alpha;
			//ans2[1][k] = ts2.getAccuracy(B, Xi,alpha,0,Im_R,Tm);
			//ans2[1][k] = ts2.getAccuracy(B, Xi,0,alpha,Im_R,Tm);
			//ans2[1][k] =ts2.getAAsum(B,Xii, alpha, 0,Im_R,Tm);
			ans2[1][k] =ts2.getAAsum(B,Xii, 0,alpha, Im_R,Tm);
			alpha = alpha + 0.05;
		}
		m.put("M", ans2);
		
		testUncertaintyforFRS ts3 = new testUncertaintyforFRS(data,sstyle,Tp);
		alpha = 0;
		double[][] ans3 = new double[2][N];
		for(int k=0;k<N;++k){
			 
			ans3[0][k] = alpha;
			
			//ans3[1][k] = ts3.getAccuracy(B, Xi,alpha,0,Ip_R,Tp);
			//ans3[1][k] = ts3.getAccuracy(B, Xi,0,alpha,Ip_R,Tp);
			//ans3[1][k] =ts3.getAAsum(B,Xii, alpha, 0,Ip_R,Tp);
			ans3[1][k] =ts3.getAAsum(B,Xii,0, alpha, Ip_R,Tp);
			alpha = alpha + 0.05;
		}
		m.put("P", ans3);
		
		
		
		/*boolean B[] = new boolean[data.numAttributes()];
		double[][] ans = new double[2][data.numAttributes()-1];
		for(int k=0;k<data.numAttributes()-1;++k){
			B[k]=true;
			ans[0][k] = k;
			ans[1][k] = ts.getAccuracy(B, X,0,0,Tl,Il);
		}
		m.put("Tl-Il", ans);
		 
		B = new boolean[data.numAttributes()];
		ans = new double[2][data.numAttributes()-1];
		for(int k=0;k<data.numAttributes()-1;++k){
			B[k]=true;
			ans[0][k] = k;
			ans[1][k] = ts.getAccuracy(B, X,0,0,Tm,Im_R);
		}
		m.put("Tm-Im_R", ans);
		 
		B = new boolean[data.numAttributes()];
		ans = new double[2][data.numAttributes()-1];
		for(int k=0;k<data.numAttributes()-1;++k){
			B[k]=true;
			ans[0][k] = k;
			ans[1][k] = ts.getAccuracy(B, X,0,0,Tm,Im_S);
		}
		m.put("Tm-Im_S", ans);
		
		B = new boolean[data.numAttributes()];
		ans = new double[2][data.numAttributes()-1];
		for(int k=0;k<data.numAttributes()-1;++k){
			B[k]=true;
			ans[0][k] = k;
			ans[1][k] = ts.getAccuracy(B, X,0,0,Tp,Ip_R);
		}
		m.put("Tp-Ip_R", ans);
		Arrays.fill(B, false);
		
		B = new boolean[data.numAttributes()];
		for(int k=0;k<data.numAttributes()-1;++k){
			B[k]=true;
			ans[0][k] = k;
			ans[1][k] = ts.getAccuracy(B, X,0,0,Tp,Ip_S);
		}
		m.put("Tp-Ip_S", ans);
		 
		*/
		

		
		
		String[] t = new String[3];	
		t[1]="Accuracy";
		t[2]="B";
		t[0]="Test4";
		xFigure.showFigure(t, m);
		for(int k=0;k<N;++k){
			System.out.println(ans[0][k]+" "+ans[1][k]+" "+ans2[1][k]+" "+ans3[1][k]);
		}
	}
	 
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		test1();
		}

}
