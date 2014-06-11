package Modification;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import myUtils.xFigure;

import Xreducer_fuzzy.ITStyle_KleeneDienes;
import Xreducer_fuzzy.ITStyle_Lukasiewicz;
import Xreducer_fuzzy.ImplicatorTnormStyle;
import Xreducer_fuzzy.SStyle_MaxMin;
import Xreducer_fuzzy.SimilarityStyle;

import weka.core.Instances;






public class newTestLowerUpper {
	
	
	public static void test2() throws Exception
	{
		Random rnd = new Random(1);
		int U = 200;
		int R=1;
		double[] X = new double[U/2];
		for(int i=0;i<U/2;++i){
			X[i]=rnd.nextDouble();
		}
		for(int i=0;i<R;++i){
			Instances data = testUncertaintyforFRS.generateInstances(U,20,5,1,i);
			SimilarityStyle sstyle = new SStyle_MaxMin();
			ImplicatorTnormStyle Tm = new ITStyle_KleeneDienes();
			ImplicatorTnormStyle Im_S = new ITStyle_KleeneDienes();
			testUncertaintyforFRS ts = new testUncertaintyforFRS(data,sstyle,Tm);
			boolean B[] = new boolean[data.numAttributes()];
			Arrays.fill(B, true);
			B[B.length-1]=false;
			double ans = ts.getAccuracy(B, X,0,1,Tm,Im_S);
			if(ans>1)
				System.out.println("Find:"+ans);
		}
	}
	
	public static void test1() throws Exception
	{
		Random rnd = new Random(1);
		int U = 200;
		int R=1;
		double[] X = new double[U/2];
		for(int i=0;i<U/2;++i){
			X[i]=rnd.nextDouble();
		}
		for(int i=0;i<R;++i){
			Instances data = testUncertaintyforFRS.generateInstances(U,20,5,1,i);
			
			Map<String, double[][]> m = new HashMap<String, double[][]>();
			
			SimilarityStyle sstyle = new SStyle_MaxMin();
			
			ImplicatorTnormStyle Tl = new ITStyle_Lukasiewicz();
			ImplicatorTnormStyle Il = new ITStyle_Lukasiewicz();
			
			ImplicatorTnormStyle Tm = new ITStyle_KleeneDienes();
			ImplicatorTnormStyle Im_S = new ITStyle_KleeneDienes();
			ImplicatorTnormStyle Im_R = new ITStyle_KleeneDienes_R();
			
			ImplicatorTnormStyle Tp = new ITStyle_Probabilistic_R();
			ImplicatorTnormStyle Ip_R = new ITStyle_Probabilistic_R();
			ImplicatorTnormStyle Ip_S = new ITStyle_Probabilistic_S();
			
			testUncertaintyforFRS ts = new testUncertaintyforFRS(data,sstyle,Tm);
			boolean B[] = new boolean[data.numAttributes()];
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
			 
			 
			

			
			
			String[] t = new String[3];	
			t[1]="Accuracy";
			t[2]="B";
			t[0]="Test4";
			xFigure.showFigure(t, m);
			
		
		
		}
	}
	
	
	
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		test1();
	}
}
