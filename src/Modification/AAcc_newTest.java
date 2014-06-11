package Modification;

import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import myUtils.xFigure;
import myUtils.xMath;

import weka.core.Instances;
import Xreducer_fuzzy.ITStyle_KleeneDienes;
import Xreducer_fuzzy.ITStyle_Lukasiewicz;
import Xreducer_fuzzy.ImplicatorTnormStyle;
import Xreducer_fuzzy.SStyle_MaxMin;
import Xreducer_fuzzy.SimilarityStyle;

public class AAcc_newTest {
	
	
	public static void test5() throws Exception{  //µ¥¸öB
		//String path1 = "C:/Users/Eric/Desktop/2011Çï¶¬/Code/Xreducer/data/Data/cleveland.arff";
		//String path1 = "C:/Users/Eric/Desktop/2011Çï¶¬/Code/Xreducer/data/Data/colic.arff";
		//String path1 = "C:/Users/Eric/Desktop/2011Çï¶¬/Code/Xreducer/data/Data/credit.arff";
		//String path1 = "C:/Users/Eric/Desktop/2011Çï¶¬/Code/Xreducer/data/Data/ecoli.arff";
		//String path1 = "C:/Users/Eric/Desktop/2011Çï¶¬/Code/Xreducer/data/Data/glass.arff";
		//String path1 = "C:/Users/Eric/Desktop/2011Çï¶¬/Code/Xreducer/data/Data/heart.arff";
		//String path1 = "C:/Users/Eric/Desktop/2011Çï¶¬/Code/Xreducer/data/Data/hepatitis.arff";
		//String path1 = "C:/Users/Eric/Desktop/2011Çï¶¬/Code/Xreducer/data/Data/ionosphere.arff";
		String path1 = "C:/Users/Eric/Desktop/2011Çï¶¬/Code/Xreducer/data/Data/wine.arff";
		
		
		 
		Instances data = new Instances(new FileReader(path1));
		//data.setClassIndex(data.numAttributes()-1); 
		
		//Instances data = testUncertaintyforFRS.generateInstances(1000,10,3,1,0);
		SimilarityStyle sstyle = new SStyle_MaxMin();
		//SimilarityStyle sstyle = new SStyle_Abs1lambda_VmaxVmin(4);
		
		ImplicatorTnormStyle tnorm = new ITStyle_KleeneDienes(); 
		//ImplicatorTnormStyle itstyle = new ITStyle_Lukasiewicz();
		testUncertaintyforFRS ts = new testUncertaintyforFRS(data,sstyle,tnorm);
		
		
		boolean X[] = new boolean[data.numInstances()];
		for(int i=0;i<data.numInstances()/2;++i)
			X[i]=true;
		 
		 
				double[][] ans = new double[6][data.numAttributes()-1];
				boolean B[] = new boolean[data.numAttributes()];
				for(int k=0;k<data.numAttributes()-1;++k){
					B[k]=true;					
					ans[0][k] = ts.getAccuracy(B,X,1,0,tnorm,tnorm);
					ans[1][k] = ts.getAccuracy(B,X,0,0,tnorm,tnorm);
					ans[2][k] = ts.getAccuracy(B,X,0,1,tnorm,tnorm);
					ans[3][k] = ts.getAAsum(B,1,0,tnorm,tnorm);
					ans[4][k] = ts.getAAsum(B,0,0,tnorm,tnorm);
					ans[5][k] = ts.getAAsum(B,0,1,tnorm,tnorm);
					B[k]=false;
				}
				
		for(int i=0;i<ans[0].length;++i)
			System.out.println((i+1)+" "+ans[0][i]+" "+ans[1][i]+" "+ans[2][i]+" "+ans[3][i]+" "+ans[4][i]+" "+ans[5][i]);
					
				
			
		
}
	
	
	
public static void test3() throws Exception{ //¼ì²âacc acccom Çø·ÖÁ¦

		
		
		SimilarityStyle sstyle = new SStyle_MaxMin();
		//SimilarityStyle sstyle = new SStyle_Abs1lambda_VmaxVmin(4);
		
		ImplicatorTnormStyle s = new ITStyle_KleeneDienes(); 
		//ImplicatorTnormStyle itstyle = new ITStyle_Lukasiewicz();
		int U = 6;
	
		
		int testp = 9;
		int R=1000;
		for(int i=30;i<R;++i){
			 
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
					ans1[1][k] = ts.getAAsum_Com(B, p,p,s,s);
					ans2[0][k] = k;
					ans2[1][k] = ts.getAAsum(B, p,p,s,s);
				}
				//m.put("accCom", ans1);
				//m.put("acc", ans2);
				if(!xMath.isHasEqual(ans1[1])||!xMath.isStrictMonotony(ans2[1]))
				//if(!xMath.isHasEqual(ans1[1]))
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
						ans1[1][k] = ts.getAAsum_Com(B,p,p,s,s);
						ans2[0][k] = k;
						ans2[1][k] = ts.getAAsum(B,p,p,s,s);
					}
					m.put("accCom", ans1);
					m.put("acc", ans2);
					//System.out.println("p="+p);
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
			System.out.println(i);
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
		
		
		 
		Instances data = new Instances(new FileReader(path1));
		//data.setClassIndex(data.numAttributes()-1); 
		
		//Instances data = testUncertaintyforFRS.generateInstances(1000,10,3,1,0);
		SimilarityStyle sstyle = new SStyle_MaxMin();
		//SimilarityStyle sstyle = new SStyle_Abs1lambda_VmaxVmin(4);
		
		ImplicatorTnormStyle tnorm = new ITStyle_KleeneDienes(); 
		//ImplicatorTnormStyle itstyle = new ITStyle_Lukasiewicz();
		testUncertaintyforFRS ts = new testUncertaintyforFRS(data,sstyle,tnorm);
		
		
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
					ans[1][k] = ts.getAAsum(B,a,b,tnorm,tnorm);
					 
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
			 
			for(int k=0;k<(data.numAttributes()-1)/2;++k)
				B[k]=true;
		 
			double preans = 0;
			int R=20;
			double[][] ans = new double[R+1][R+1];
			for(int a=0;a<=R;a++){
				for(int b=0;b<=R;b++){
				//double ansx = ts.getAccuracy(B,X,(double)a/(double)R,(double)b/(double)R,itstyle,itstyle);
				double ansx = ts.getAAsum(B,(double)a/(double)R,(double)b/(double)R,itstyle,itstyle);
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
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		test5();
	}

}
