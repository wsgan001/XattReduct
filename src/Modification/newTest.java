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

public class newTest {
	public static void test5() throws Exception{ //±È½ÏI T
		Instances data = testUncertaintyforFRS.generateInstances(10,6,2,1,1);
		data.setClassIndex(data.numAttributes()-1);
		SimilarityStyle sstyle = new SStyle_MaxMin();
		boolean X[] = new boolean[data.numInstances()];
		Map<String, double[][]> m = new HashMap<String, double[][]>();
		for(int k=0;k<data.numInstances()/4;++k)
			X[k]=true;
		
		ImplicatorTnormStyle Tl = new ITStyle_Lukasiewicz();
		ImplicatorTnormStyle Il = new ITStyle_Lukasiewicz();
		
		ImplicatorTnormStyle Tm = new ITStyle_KleeneDienes_R();
		ImplicatorTnormStyle Im_R = new ITStyle_KleeneDienes_R();

		
		ImplicatorTnormStyle Tp = new ITStyle_Probabilistic_R();
		ImplicatorTnormStyle Ip_R = new ITStyle_Probabilistic_R();
		
		/*boolean B[] = new boolean[data.numAttributes()];
		for(int k=0;k<3;++k){
			B[k]=true;
		}
		testUncertaintyforFRS ts = new testUncertaintyforFRS(data,sstyle,Tl);
		double ans1 = ts.getAccuracy(B, X,0,0,Tl,Il);

		testUncertaintyforFRS ts2 = new testUncertaintyforFRS(data,sstyle,Tm);
		double ans2 = ts2.getAccuracy(B, X,0,0,Tm,Im_R);


		
		testUncertaintyforFRS ts3 = new testUncertaintyforFRS(data,sstyle,Tp);
		
		double ans3= ts3.getAccuracy(B, X,0,0,Tp,Ip_R);
		
		System.out.println("L:"+ans1);
		System.out.println("M:"+ans2);
		System.out.println("P:"+ans3);*/
		
		
		
		testUncertaintyforFRS ts = new testUncertaintyforFRS(data,sstyle,Tl);
		boolean B[] = new boolean[data.numAttributes()];
		double[][] ans = new double[2][data.numAttributes()-1];
		for(int k=0;k<data.numAttributes()-1;++k){
			B[k]=true;
			ans[0][k] = k;
			ans[1][k] = ts.getAccuracy(B, X,0,0,Tl,Il);
		}
		m.put("L", ans);
		
		testUncertaintyforFRS ts2 = new testUncertaintyforFRS(data,sstyle,Tm);
		B = new boolean[data.numAttributes()];
		double[][] ans2 = new double[2][data.numAttributes()-1];
		for(int k=0;k<data.numAttributes()-1;++k){
			B[k]=true;
			ans2[0][k] = k;
			ans2[1][k] = ts2.getAccuracy(B, X,0,0,Tm,Im_R);
		}
		m.put("M", ans2);
		
		testUncertaintyforFRS ts3 = new testUncertaintyforFRS(data,sstyle,Tp);
		B = new boolean[data.numAttributes()];
		double[][] ans3 = new double[2][data.numAttributes()-1];
		for(int k=0;k<data.numAttributes()-1;++k){
			B[k]=true;
			ans3[0][k] = k;
			ans3[1][k] = ts3.getAccuracy(B, X,0,0,Tp,Ip_R);
		}
		m.put("P", ans3);
		String[] t = new String[3];	
		t[1]="Accuracy";
		t[2]="B";
		t[0]="Test5";
		xFigure.showFigure(t, m);
	}

	public static void test4() throws Exception{ //±È½ÏI T
	
		String path1 = "C:/Users/Eric/Desktop/2011Çï¶¬/Code/Xreducer/data/Data/cleveland.arff";
		//String path1 = "C:/Users/Eric/Desktop/2011Çï¶¬/Code/Xreducer/data/Data/colic.arff";
		//String path1 = "C:/Users/Eric/Desktop/2011Çï¶¬/Code/Xreducer/data/Data/credit.arff";
		//String path1 = "C:/Users/Eric/Desktop/2011Çï¶¬/Code/Xreducer/data/Data/ecoli.arff";
		//String path1 = "C:/Users/Eric/Desktop/2011Çï¶¬/Code/Xreducer/data/Data/glass.arff";
		//String path1 = "C:/Users/Eric/Desktop/2011Çï¶¬/Code/Xreducer/data/Data/heart.arff";
		//String path1 = "C:/Users/Eric/Desktop/2011Çï¶¬/Code/Xreducer/data/Data/hepatitis.arff";
		//String path1 = "C:/Users/Eric/Desktop/2011Çï¶¬/Code/Xreducer/data/Data/ionosphere.arff";
		//String path1 = "C:/Users/Eric/Desktop/2011Çï¶¬/Code/Xreducer/data/Data/wine.arff";
		
		
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
		
		
		
		//System.out.println(Arrays.toString(Xi));
		
		ImplicatorTnormStyle Tl = new ITStyle_Lukasiewicz();
		ImplicatorTnormStyle Il = new ITStyle_Lukasiewicz();
		
		ImplicatorTnormStyle Tm = new ITStyle_KleeneDienes();
		ImplicatorTnormStyle Im_R = new ITStyle_KleeneDienes_R();

		
		ImplicatorTnormStyle Tp = new ITStyle_Probabilistic_R();
		ImplicatorTnormStyle Ip_R = new ITStyle_Probabilistic_R();

		
		
		
		testUncertaintyforFRS ts = new testUncertaintyforFRS(data,sstyle,Tl);
		boolean B[] = new boolean[data.numAttributes()];
		double[][] ans = new double[2][data.numAttributes()-1];
		for(int k=0;k<data.numAttributes()-1;++k){
			B[k]=true;
			ans[0][k] = k;
			//ans[1][k] = ts.getAccuracy(B, X,0,0,Il,Tl);
			//ans[1][k] =ts.getAAsum(B, 0, 0, Il, Tl);
			
			//ans[1][k] = ts.getAccuracy(B, Xi,0,0,Il,Tl);
			ans[1][k] =ts.getAAsum(B, Xi, 0, 0, Il, Tl);
			
			//ans[1][k] =ts.getAAsum(B, Xii, 0, 0, Il, Tl);
		}
		m.put("L", ans);
		
		testUncertaintyforFRS ts2 = new testUncertaintyforFRS(data,sstyle,Tm);
		B = new boolean[data.numAttributes()];
		double[][] ans2 = new double[2][data.numAttributes()-1];
		for(int k=0;k<data.numAttributes()-1;++k){
			B[k]=true;
			ans2[0][k] = k;
			//ans2[1][k] = ts2.getAccuracy(B, X,0,0,Im_R,Tm);
			//ans2[1][k] =ts2.getAAsum(B, 0, 0,Im_R,Tm);
			
			//ans2[1][k] = ts2.getAccuracy(B, Xi,0,0,Im_R,Tm);
			ans2[1][k] =ts2.getAAsum(B,Xii, 0, 0,Im_R,Tm);
		}
		m.put("M", ans2);
		
		testUncertaintyforFRS ts3 = new testUncertaintyforFRS(data,sstyle,Tp);
		B = new boolean[data.numAttributes()];
		double[][] ans3 = new double[2][data.numAttributes()-1];
		for(int k=0;k<data.numAttributes()-1;++k){
			B[k]=true;
			ans3[0][k] = k;
			//ans3[1][k] = ts3.getAccuracy(B, X,0,0,Ip_R,Tp);
			//ans3[1][k] =ts3.getAAsum(B, 0, 0,Ip_R,Tp);
			
			//ans3[1][k] = ts3.getAccuracy(B, Xi,0,0,Ip_R,Tp);
			ans3[1][k] =ts3.getAAsum(B,Xii, 0, 0,Ip_R,Tp);
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
		for(int k=0;k<data.numAttributes()-1;++k){
			System.out.println(k+1+" "+ans[1][k]+" "+ans2[1][k]+" "+ans3[1][k]);
		}
	}
	public static void test3() throws Exception{ //¼ì²âacc acccom Çø·ÖÁ¦

		
		
		SimilarityStyle sstyle = new SStyle_MaxMin();
		//SimilarityStyle sstyle = new SStyle_Abs1lambda_VmaxVmin(4);
		
		ImplicatorTnormStyle s = new ITStyle_KleeneDienes(); 
		//ImplicatorTnormStyle itstyle = new ITStyle_Lukasiewicz();
		int U = 6;
		boolean X[] = new boolean[U];

		
		for(int k=0;k<U/2;++k)    //X=u/2
			X[k]=true;
		
		
		int testp = 9;
		int R=1000;
		for(int i=0;i<R;++i){
			Instances data = testUncertaintyforFRS.generateInstances(U,5,2,1,i);
			testUncertaintyforFRS ts = new testUncertaintyforFRS(data,sstyle,s);
			boolean tag=true;
			double p=0.1;
			for(int c=0;c<testp;c++){
				double[][] ans1 = new double[2][data.numAttributes()-1];
				double[][] ans2 = new double[2][data.numAttributes()-1];
				boolean B[] = new boolean[data.numAttributes()];
				for(int k=0;k<data.numAttributes()-1;++k){
					B[k]=true;
					
					ans1[0][k] = k;
					ans1[1][k] = ts.getAccuracyCom(B, X,p,p,s,s);
					ans2[0][k] = k;
					ans2[1][k] = ts.getAccuracy(B, X,p,p,s,s);
				}
				//m.put("accCom", ans1);
				//m.put("acc", ans2);
				if(!xMath.isHasEqual(ans1[1])||!xMath.isStrictMonotony(ans2[1]))
				{
					tag=false;
					break;
				}
				p=p+0.1;
			}
			if(tag){
				String[] t = new String[3];
	
				t[1]="Accuracy";
				t[2]="B";
				Map<String, double[][]> m = new HashMap<String, double[][]>();
				p=0.1;
				for(int c=0;c<testp;c++){

					t[0]="P="+p;
					double[][] ans1 = new double[2][data.numAttributes()-1];
					double[][] ans2 = new double[2][data.numAttributes()-1];
					boolean B[] = new boolean[data.numAttributes()];
					for(int k=0;k<data.numAttributes()-1;++k){
						B[k]=true;
						
						ans1[0][k] = k;
						ans1[1][k] = ts.getAccuracyCom(B, X,p,p,s,s);
						ans2[0][k] = k;
						ans2[1][k] = ts.getAccuracy(B, X,p,p,s,s);
					}
					m.put("accCom", ans1);
					m.put("acc", ans2);
					System.out.println("p="+p);
					System.out.println("descriptor y_accCom_"+c);
					for(int ss=0;ss<ans1[1].length;++ss)
						System.out.println(ans1[1][ss]);
					System.out.println("descriptor y_acc_"+c);
					for(int ss=0;ss<ans2[1].length;++ss)
						System.out.println(ans2[1][ss]);
					
					//System.out.println(Arrays.toString(ans1[1]));
					//System.out.println(Arrays.toString(ans2[1]));
					p=p+0.1;
					//xFigure.showFigure(t, m);
				}
				System.out.println(data);
				break;
			}
			//System.out.println(i);
		}
				
				
				

		
	
	}
	
	public static void test2() throws Exception{ //¼ì²âB
		//String path1 = "C:/Users/Eric/Desktop/2011Çï¶¬/Code/Xreducer/data/Data/cleveland.arff";
		//String path1 = "C:/Users/Eric/Desktop/2011Çï¶¬/Code/Xreducer/data/Data/colic.arff";
		//String path1 = "C:/Users/Eric/Desktop/2011Çï¶¬/Code/Xreducer/data/Data/credit.arff";
		//String path1 = "C:/Users/Eric/Desktop/2011Çï¶¬/Code/Xreducer/data/Data/ecoli.arff";
		//String path1 = "C:/Users/Eric/Desktop/2011Çï¶¬/Code/Xreducer/data/Data/glass.arff";
		//String path1 = "C:/Users/Eric/Desktop/2011Çï¶¬/Code/Xreducer/data/Data/heart.arff";
		//String path1 = "C:/Users/Eric/Desktop/2011Çï¶¬/Code/Xreducer/data/Data/hepatitis.arff";
		//String path1 = "C:/Users/Eric/Desktop/2011Çï¶¬/Code/Xreducer/data/Data/ionosphere.arff";
		String path1 = "C:/Users/Eric/Desktop/2011Çï¶¬/Code/Xreducer/data/Data/wine.arff";
		
		
		String path4 = "C:/Users/Eric/Desktop/2011Çï¶¬/Code/Xreducer/data/Data/vote.arff";
		Instances data = new Instances(new FileReader(path1));
		//data.setClassIndex(data.numAttributes()-1); 
		
		//Instances data = testUncertaintyforFRS.generateInstances(1000,10,3,1,0);
		SimilarityStyle sstyle = new SStyle_MaxMin();
		//SimilarityStyle sstyle = new SStyle_Abs1lambda_VmaxVmin(4);
		
		ImplicatorTnormStyle tnorm = new ITStyle_KleeneDienes(); 
		//ImplicatorTnormStyle itstyle = new ITStyle_Lukasiewicz();
		testUncertaintyforFRS ts = new testUncertaintyforFRS(data,sstyle,tnorm);
		
		
		ImplicatorTnormStyle Tl = new ITStyle_Lukasiewicz();
		ImplicatorTnormStyle Il = new ITStyle_Lukasiewicz();
		
		ImplicatorTnormStyle Tm = new ITStyle_KleeneDienes();
		ImplicatorTnormStyle Im_R = new ITStyle_KleeneDienes();
		ImplicatorTnormStyle Im_S = new ITStyle_KleeneDienes_R();
		
		ImplicatorTnormStyle Tp = new ITStyle_Probabilistic_R();
		ImplicatorTnormStyle Ip_R = new ITStyle_Probabilistic_R();
		ImplicatorTnormStyle Ip_S = new ITStyle_Probabilistic_S();
		
		
		
		boolean X[] = new boolean[data.numInstances()];

		for(int k=0;k<data.numInstances()/2;++k)   //X=u/2
			X[k]=true;
		
		String[] t = new String[3];
		t[0]="Test";
		t[1]="Accuracy";
		t[2]="B";
		Map<String, double[][]> m = new HashMap<String, double[][]>();
		int cnt=1;
		Vector<double[]> l = new Vector<double[]> ();
		for(double a=0;a<=1;a+=0.5){
			for(double b=0;b<=1;b+=0.5){
				double[][] ans = new double[2][data.numAttributes()-1];
				boolean B[] = new boolean[data.numAttributes()];
				for(int k=0;k<data.numAttributes()-1;++k){
					B[k]=true;
					
					ans[0][k] = k;
					ans[1][k] = ts.getAccuracy(B, X,a,b,Tm,Im_S);
					
				}
				
				m.put("a="+a+",b="+b, ans);
				
				//System.out.println("a="+a+",b="+b+Arrays.toString(ans[1]));
				l.add(ans[1]);
				//String str = ""+cnt++;
				//for(int s=0;s<ans[1].length;s++)
				//str += " "+ans[1][s];
				//System.out.println(str);
					
				
			}
		}
		
		showLines(l);
		xFigure.showFigure(t, m);
		
		
	}
	public static void showLines(Vector<double[]> l){
		double [][] ans = new double [l.elementAt(0).length][l.size()];
		
		for(int i=0;i<l.size();++i){
			for(int j=0;j<l.elementAt(i).length;++j){
				ans[j][i]=l.elementAt(i)[j];
			}
		}
		int cnt=1;
		for(int j=0;j<ans.length;++j){
			String str = ""+cnt++;
			for(int s=0;s<ans[1].length;s++)
			str += " "+ans[j][s];
			System.out.println(str);
		}

	}
	public static void test1() throws Exception{ //¼ì²â alpha beta ×ö3Î¬Í¼
		
		String path1 = "C:/Users/Eric/Desktop/2011Çï¶¬/Code/Xreducer/data/Data/cleveland.arff";
		//String path1 = "C:/Users/Eric/Desktop/2011Çï¶¬/Code/Xreducer/data/Data/colic.arff";
		//String path1 = "C:/Users/Eric/Desktop/2011Çï¶¬/Code/Xreducer/data/Data/credit.arff";
		//String path1 = "C:/Users/Eric/Desktop/2011Çï¶¬/Code/Xreducer/data/Data/ecoli.arff";
		//String path1 = "C:/Users/Eric/Desktop/2011Çï¶¬/Code/Xreducer/data/Data/glass.arff";
		//String path1 = "C:/Users/Eric/Desktop/2011Çï¶¬/Code/Xreducer/data/Data/heart.arff";
		//String path1 = "C:/Users/Eric/Desktop/2011Çï¶¬/Code/Xreducer/data/Data/hepatitis.arff";
		//String path1 = "C:/Users/Eric/Desktop/2011Çï¶¬/Code/Xreducer/data/Data/ionosphere.arff";
		//String path1 = "C:/Users/Eric/Desktop/2011Çï¶¬/Code/Xreducer/data/Data/wine.arff";
		Instances data = new Instances(new FileReader(path1));
		data.setClassIndex(data.numAttributes()-1); 
		
			SimilarityStyle sstyle = new SStyle_MaxMin();
			//SimilarityStyle sstyle = new SStyle_Abs1lambda_VmaxVmin(4);
			
			ImplicatorTnormStyle itstyle = new ITStyle_KleeneDienes(); 
			//ImplicatorTnormStyle itstyle = new ITStyle_Lukasiewicz();
			testUncertaintyforFRS ts = new testUncertaintyforFRS(data,sstyle,itstyle);
			boolean B[] = new boolean[data.numAttributes()];
			boolean X[] = new boolean[data.numInstances()];
			for(int k=0;k<(data.numAttributes()-1)/2;++k)
				B[k]=true;
			for(int k=0;k<data.numInstances()/2;++k)  //X=u/2
				X[k]=true;
			double preans = 0;
			int R=20;
			double[][] ans = new double[R+1][R+1];
			for(int a=0;a<=R;a++){
				for(int b=0;b<=R;b++){
				double ansx = ts.getAccuracy(B,X,(double)a/(double)R,(double)b/(double)R,itstyle,itstyle);
				//double ans = ts.getLowerUpperAccuracy(B);
				//System.out.println(ansx);
				ans[a][b] = ansx;
				//if(preans>ans){
					//System.out.println("Not OK");
				//}
				//preans = ans;
				//System.out.println(a+":"+b+"="+ansx);
			}
			
			}
			
			for(int a=0;a<=R;a++){
				String str = "";
				for(int s=0;s<ans[a].length;s++)
				str += " "+ans[a][s];
				System.out.println(str);
			}
			 
		
	}
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		test4();
		}

}
