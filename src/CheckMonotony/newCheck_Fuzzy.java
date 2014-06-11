package CheckMonotony;

import java.util.Arrays;
import java.util.Random;

import Xreducer_core.Utils;
import Xreducer_core.Utils_entropy;
import Xreducer_fuzzy.SStyle_Abs1lambda_Vmax;
import Xreducer_fuzzy.SStyle_MaxMin;
import Xreducer_fuzzy.SimilarityStyle;

import weka.core.Instances;

public class newCheck_Fuzzy {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		int runtime = 100000;
		int simruntime =1000000;
		int n = 4;
		int a = 3;
		int c = 2;
		double alpha = 0.33;
		Run(runtime,simruntime,n,a,c,alpha);
		
		/*Instances data = CheckMonotony.generateInstances(n,1,c,1,3378);
		System.out.println(data);
		data = CheckMonotony.generateInstances(n,1,c,1,102);
		System.out.println(data);
		data = CheckMonotony.generateInstances(n,1,c,1,1520);
		System.out.println(data);
		
		double[][][] sb = new double[3][4][4];
		double[][] sb0={
				{1,0.650,0.474,0.883},
				{0.650,1,0.124,0.766},
				{0.474,0.124,1.0,0.358},
				{0.883,0.766,0.358,1}
		};
		
		double[][] sb1={
				{1,0.0,0.892,0.0},
				{0.0,1,0.0,0.919},
				{0.892,0.0,1.0,0.0},
				{0.0,0.919,0.0,1}
		};
		
		double[][] sb2={
				{1,0.608,0.216,0.059},
				{0.608,1,0.608,0.451},
				{0.216,0.608,1.0,0.843},
				{0.059,0.451,0.843,1}
		};
		sb[0] = sb0.clone();
		sb[1] = sb1.clone();
		sb[2] = sb2.clone();
		double [][] allres = newcheckdata(sb,2);
		 for(int k=0;k<allres.length;++k){
			 System.out.println(Arrays.toString(allres[k]));
		 }*/
		 
	}
	
	public static void Run(int runtime, int simruntime, int n, int a, int c, double alpha) throws Exception{
		for(int i=0;i<runtime;++i){
			if(newcheckdataFuzzy(n,a,c,i)&&newcheckdataCovering(n,a,c,i)){
				int[] simInd = new int[a];
				for(int k=0;k<a;++k){
					simInd[k]=-1;
				}
				double[][][] sb = getSim(n,a,c,i);
				double[][][] finalSim = new double[a][n][n];
				for(int k = 0;k<sb.length;k++){
					for(int q=0;q<simruntime;++q){
						double [][] gSim = gerenatedSim(n,c,q);
						if(ArraysIsSimilarity(gSim,sb[k],alpha)){
							simInd[k]=q;
							finalSim[k] = gSim.clone();
							break;
						}
					}
				}
				boolean tag = true;
				for(int k=0;k<a;++k){
					if(simInd[k]==-1)
					{
						tag= false;
						break;
					}
				}
				if(tag){
					
					double [][] allres = newcheckdata(finalSim,c);
					 if(isAllresMatch(allres)){
						 System.out.println(i+":"+Arrays.toString(simInd));
						 for(int k=0;k<allres.length;++k){
							 System.out.println(Arrays.toString(allres[k]));
						 }
					 }
					//System.out.println("Find");
					//break;
				}
				//System.out.println(i+":"+Arrays.toString(simInd));
			}
		}
		

		System.out.println("Done");
	}
	public static boolean isAllresMatch(double[][] xx){
		int k=xx[0].length;
		boolean tag1 = false;
		boolean tag2 = false;
		if(xx[1][k-1]==xx[1][k-2] && xx[3][k-1]==xx[3][k-2]){
			tag1 =  true;
		}
		
	
		if((xx[0][k-1]>xx[0][k-2]&&xx[0][0]>xx[0][1])&&(xx[2][k-1]>xx[2][k-2]&&xx[2][0]>xx[2][1])){
			tag2= true;
		}
		return tag1&&tag2;
	}
	
	
	
	
	public static boolean ArraysIsSimilarity(double[][] x, double[][] y,double alpha){
		boolean tag = true;
		for(int i=0;i<x.length;++i){
			for(int j = i+1;j<x.length;++j){
				if(Math.abs((x[i][j]-y[i][j]))>alpha){
					tag = false;
					break;
				}
			}
		}
		return tag;
	}
	public static double[][] gerenatedSim(int n, int cls, int ind) throws Exception{
		double[][] ans = new double[n][n];
		SimilarityStyle xfs = new SStyle_MaxMin();
		//SimilarityStyle xfs = new SStyle_Abs1lambda_Vmax(4);
		Instances data = CheckMonotony.generateInstances(n,1,cls,1,ind); //1.样本 2.属性 3.离散连续 
					double[] Vas = data.attributeToDoubleArray(0);			
					double[] vasStatistics = Utils.getStatisticsValue(Vas);
					for(int i=0;i<n;++i){
						for(int q=i;q<n;++q) {
							xfs.SimilaritySetting(vasStatistics[1],vasStatistics[2],vasStatistics[5]);
							double x1 = data.instance(i).value(0);
							double x2 = data.instance(q).value(0);
							double simValue = xfs.getSimilarityValue(x1,x2);
							ans[i][q] = simValue;
							ans[q][i] = ans[i][q] ;
						}}			
		
	    return ans;
	}
	
	
	
	public static double[][][] getSim(int n,int a,int cls, int ind){
		double[][][] Sb = new double[a][n][n];
		Random rd = new Random(ind);
		for(int k=0;k<a;++k){
			Sb[k] = generateSbFuzzyArray(n,rd);
		}
		return Sb;
	}
	public static double[][] newcheckdata(double[][][] Sb, int cls){
		int a = Sb.length;
		int n = Sb[0].length;
		double[] res1 = new double[a];
		double[] resNew1 = new double[a];



		double[][] Dj = generateDjArray(n,cls);
		double[][] Sd = generateSdArray(n,cls);
		boolean[] Btag = new boolean[a];
		
		
		double[][] bsetS = new double[n][n];
		for(int k=0;k<a;++k){
			int bestind = -1;
			double bestvas = 10000000;
			if(k==0){
				for(int i=0;i<a;++i){
					//double vas = New_Fuzzy(Sb[i],Dj);
					double vas = Element_Fuzzy(Sb[i],Sd);
					if(bestvas>vas){
						bestvas = vas;
						bestind = i;
					}
				}
				bsetS = Sb[bestind].clone();
				Btag[bestind] = true;
				res1[k] = bestvas;
			}
			else{
				for(int i=0;i<a;++i){
					if(Btag[i]!=true){
					//double vas = New_Fuzzy(Sb[i],Dj);
						double vas = Element_Fuzzy(SumIn(bsetS,Sb[i]),Sd);
						if(bestvas>vas){
							bestvas = vas;
							bestind = i;
						
						}
					}
				}
				bsetS = SumIn(bsetS,Sb[bestind]).clone();
				Btag[bestind] = true;
				res1[k] = bestvas;
			}
		}
		
		
		Btag = new boolean[a];
		bsetS = new double[n][n];
		for(int k=0;k<a;++k){
			int bestind = -1;
			double bestvas = 10000000;
			if(k==0){
				for(int i=0;i<a;++i){
					//double vas = New_Fuzzy(Sb[i],Dj);
					double vas = New_Fuzzy(Sb[i],Dj);
					if(bestvas>vas){
						bestvas = vas;
						bestind = i;
					}
				}
				bsetS = Sb[bestind].clone();
				Btag[bestind] = true;
				resNew1[k] = bestvas;
			}
			else{
				for(int i=0;i<a;++i){
					if(Btag[i]!=true){
					//double vas = New_Fuzzy(Sb[i],Dj);
						double vas = New_Fuzzy(SumIn(bsetS,Sb[i]),Dj);
						if(bestvas>vas){
							bestvas = vas;
							bestind = i;
						
						}
					}
				}
				bsetS = SumIn(bsetS,Sb[bestind]).clone();
				Btag[bestind] = true;
				resNew1[k] = bestvas;
			}
		}
		
		double[] res2 = new double[a];
		double[] resNew2 = new double[a];

		for(int k=0;k<a;++k){
			Sb[k] = generateSbCoveringArray( Sb[k],0.3);
		}
		 Dj = generateDjArray(n,cls);
		 Sd = generateSdArray(n,cls);
		Btag = new boolean[a];
		
		
		 bsetS = new double[n][n];
		for(int k=0;k<a;++k){
			int bestind = -1;
			double bestvas = 10000000;
			if(k==0){
				for(int i=0;i<a;++i){
					//double vas = New_Fuzzy(Sb[i],Dj);
					double vas = Element_Covering(Sb[i],Sd);
					if(bestvas>vas){
						bestvas = vas;
						bestind = i;
					}
				}
				bsetS = Sb[bestind].clone();
				Btag[bestind] = true;
				res2[k] = bestvas;
			}
			else{
				for(int i=0;i<a;++i){
					if(Btag[i]!=true){
					//double vas = New_Fuzzy(Sb[i],Dj);
						double vas = Element_Covering(SumIn(bsetS,Sb[i]),Sd);
						if(bestvas>vas){
							bestvas = vas;
							bestind = i;
						
						}
					}
				}
				bsetS = SumIn(bsetS,Sb[bestind]).clone();
				Btag[bestind] = true;
				res2[k] = bestvas;
			}
		}
		
		
		Btag = new boolean[a];
		bsetS = new double[n][n];
		for(int k=0;k<a;++k){
			int bestind = -1;
			double bestvas = 10000000;
			if(k==0){
				for(int i=0;i<a;++i){
					//double vas = New_Fuzzy(Sb[i],Dj);
					double vas = New_Covering(Sb[i],Dj);
					if(bestvas>vas){
						bestvas = vas;
						bestind = i;
					}
				}
				bsetS = Sb[bestind].clone();
				Btag[bestind] = true;
				resNew2[k] = bestvas;
			}
			else{
				for(int i=0;i<a;++i){
					if(Btag[i]!=true){
					//double vas = New_Fuzzy(Sb[i],Dj);
						double vas = New_Covering(SumIn(bsetS,Sb[i]),Dj);
						if(bestvas>vas){
							bestvas = vas;
							bestind = i;
						
						}
					}
				}
				bsetS = SumIn(bsetS,Sb[bestind]).clone();
				Btag[bestind] = true;
				resNew2[k] = bestvas;
			}
		}
		double[][] allres = new double[4][a];
		allres[0]=res1.clone();
		allres[1]=resNew1.clone();
		allres[2]=res2.clone();
		allres[3]=resNew2.clone();
		
		return allres;
		 
	
	}
	
	
 
	
	public static boolean newcheckdataFuzzy(int n,int a,int cls, int ind){
		double[] res = new double[a];
		double[] resNew = new double[a];
		Random rd = new Random(ind);
		double[][][] Sb = new double[a][n][n];
		for(int k=0;k<a;++k){
			Sb[k] = generateSbFuzzyArray(n,rd);
		}
		double[][] Dj = generateDjArray(n,cls);
		double[][] Sd = generateSdArray(n,cls);
		boolean[] Btag = new boolean[a];
		
		
		double[][] bsetS = new double[n][n];
		for(int k=0;k<a;++k){
			int bestind = -1;
			double bestvas = 10000000;
			if(k==0){
				for(int i=0;i<a;++i){
					//double vas = New_Fuzzy(Sb[i],Dj);
					double vas = Element_Fuzzy(Sb[i],Sd);
					if(bestvas>vas){
						bestvas = vas;
						bestind = i;
					}
				}
				bsetS = Sb[bestind].clone();
				Btag[bestind] = true;
				res[k] = bestvas;
			}
			else{
				for(int i=0;i<a;++i){
					if(Btag[i]!=true){
					//double vas = New_Fuzzy(Sb[i],Dj);
						double vas = Element_Fuzzy(SumIn(bsetS,Sb[i]),Sd);
						if(bestvas>vas){
							bestvas = vas;
							bestind = i;
						
						}
					}
				}
				bsetS = SumIn(bsetS,Sb[bestind]).clone();
				Btag[bestind] = true;
				res[k] = bestvas;
			}
		}
		
		
		Btag = new boolean[a];
		bsetS = new double[n][n];
		for(int k=0;k<a;++k){
			int bestind = -1;
			double bestvas = 10000000;
			if(k==0){
				for(int i=0;i<a;++i){
					//double vas = New_Fuzzy(Sb[i],Dj);
					double vas = New_Fuzzy(Sb[i],Sd);
					if(bestvas>vas){
						bestvas = vas;
						bestind = i;
					}
				}
				bsetS = Sb[bestind].clone();
				Btag[bestind] = true;
				resNew[k] = bestvas;
			}
			else{
				for(int i=0;i<a;++i){
					if(Btag[i]!=true){
					//double vas = New_Fuzzy(Sb[i],Dj);
						double vas = New_Fuzzy(SumIn(bsetS,Sb[i]),Dj);
						if(bestvas>vas){
							bestvas = vas;
							bestind = i;
						
						}
					}
				}
				bsetS = SumIn(bsetS,Sb[bestind]).clone();
				Btag[bestind] = true;
				resNew[k] = bestvas;
			}
		}
		
		/*if(checkM(res)&&checkMnew(resNew)){
			System.out.println(Arrays.toString(res));
			System.out.println(Arrays.toString(resNew));
			for(int k=0;k<a;++k){
				System.out.println("Sim:"+(k+1));
				for(int i=0;i<n;++i){
					String str = "";
					for(int q=0;q<n;++q){
						str += Sb[k][i][q]+" ";
					}
					System.out.println(str);
				}
			}
		}*/
		
		return (checkM(res)&&checkMnew(resNew));
	
	}
	
	public static boolean newcheckdataCovering(int n,int a,int cls, int ind){
		double[] res = new double[a];
		double[] resNew = new double[a];
		Random rd = new Random(ind);
		double[][][] Sb = new double[a][n][n];
		for(int k=0;k<a;++k){
			Sb[k] = generateSbCoveringArray(generateSbFuzzyArray(n,rd),0.3);
		}
		double[][] Dj = generateDjArray(n,cls);
		double[][] Sd = generateSdArray(n,cls);
		boolean[] Btag = new boolean[a];
		
		
		double[][] bsetS = new double[n][n];
		for(int k=0;k<a;++k){
			int bestind = -1;
			double bestvas = 10000000;
			if(k==0){
				for(int i=0;i<a;++i){
					//double vas = New_Fuzzy(Sb[i],Dj);
					double vas = Element_Fuzzy(Sb[i],Sd);
					if(bestvas>vas){
						bestvas = vas;
						bestind = i;
					}
				}
				bsetS = Sb[bestind].clone();
				Btag[bestind] = true;
				res[k] = bestvas;
			}
			else{
				for(int i=0;i<a;++i){
					if(Btag[i]!=true){
					//double vas = New_Fuzzy(Sb[i],Dj);
						double vas = Element_Fuzzy(SumIn(bsetS,Sb[i]),Sd);
						if(bestvas>vas){
							bestvas = vas;
							bestind = i;
						
						}
					}
				}
				bsetS = SumIn(bsetS,Sb[bestind]).clone();
				Btag[bestind] = true;
				res[k] = bestvas;
			}
		}
		
		
		Btag = new boolean[a];
		bsetS = new double[n][n];
		for(int k=0;k<a;++k){
			int bestind = -1;
			double bestvas = 10000000;
			if(k==0){
				for(int i=0;i<a;++i){
					//double vas = New_Fuzzy(Sb[i],Dj);
					double vas = New_Covering(Sb[i],Sd);
					if(bestvas>vas){
						bestvas = vas;
						bestind = i;
					}
				}
				bsetS = Sb[bestind].clone();
				Btag[bestind] = true;
				resNew[k] = bestvas;
			}
			else{
				for(int i=0;i<a;++i){
					if(Btag[i]!=true){
					//double vas = New_Fuzzy(Sb[i],Dj);
						double vas = New_Covering(SumIn(bsetS,Sb[i]),Dj);
						if(bestvas>vas){
							bestvas = vas;
							bestind = i;
						
						}
					}
				}
				bsetS = SumIn(bsetS,Sb[bestind]).clone();
				Btag[bestind] = true;
				resNew[k] = bestvas;
			}
		}
		
		/*if(checkM(res)&&checkMnew(resNew)){
			System.out.println(Arrays.toString(res));
			System.out.println(Arrays.toString(resNew));
			for(int k=0;k<a;++k){
				System.out.println("Sim:"+(k+1));
				for(int i=0;i<n;++i){				
					 
						String str = "";
						for(int q=0;q<n;++q){
							str += Sb[k][i][q]+" ";
						}
						System.out.println(str);
					
				}
			}
		}*/
		return (checkM(res)&&checkMnew(resNew));
	
	}
	
	
	private static boolean checkMnew(double[] xx) {
		// TODO Auto-generated method stub
		int k = xx.length;
		/*boolean tag = false;
		for(int i=0;i<k-1;++i){
			if(xx[i]<xx[i+1]){
				tag = true;
				break;
			}
		}
		return tag;*/
		if(xx[k-1]==xx[k-2]){
			return true;
		}
		return false;
	}
	static boolean checkM(double[] xx){
		int k = xx.length;
		/*boolean tag = false;
		for(int i=0;i<k-1;++i){
			if(xx[i]<xx[i+1]){
				tag = true;
				break;
			}
		}
		return tag;*/
		if(xx[k-1]>xx[k-2]&&xx[0]>xx[1]){
			return true;
		}
		return false;
	}
	public static double[][] generateSdArray(int N,int cls){
		double[][] ans = new double [N][N];
		int[] dlabel = new int[N];
		int k = N/cls;
		int ind = 0;
		for(int i=0;i<cls;++i){
			for(int j=0;j<k;++j){
				dlabel[ind++] = i;
			}
		}
		while(ind!=N){
			dlabel[ind++] = cls-1;
		}
	
		for(int i=0;i<N;++i){
			for(int j=i;j<N;++j){
				if(i==j){
					ans[i][j]=1;
				}
				else{
					if(dlabel[i]==dlabel[j]){
						ans[i][j]=1;
						ans[j][i]=1;
					}
					
				}
			}
		}
		return ans;
	}
	public static double[][] generateDjArray(int N,int cls){
		double[][] ans = new double [cls][N];
		int[] dlabel = new int[N];
		int k = N/cls;
		int ind = 0;
		for(int i=0;i<cls;++i){
			for(int j=0;j<k;++j){
				dlabel[ind++] = i;
			}
		}
		while(ind!=N){
			dlabel[ind++] = cls-1;
		}
		ind = 0;
		for(int i=0;i<cls;++i){
			for(int j=0;j<k;++j){ 
					ans[i][ind++]=1;
			}		
			if(i==cls-1){
				while(ind!=N){
					ans[i][ind++] = 1;
				}
			}
		}
		return ans;
	}
	
	public static double[][] generateSbFuzzyArray(int N, Random rnd){
		double[][] ans = new double [N][N];
		for(int i=0;i<N;++i){
			for(int j=i;j<N;++j){
				if(i==j){
					ans[i][j]=1;
				}
				else{
					ans[i][j]=rnd.nextDouble();    //fuzzy
					//ans[i][j]=rnd.nextBoolean()==true?1:0;   //covering
					ans[j][i]=ans[i][j];
				}
			}
		}
		return ans;
	}
	public static double[][] generateSbCoveringArray(double[][] FuzzySim,double alpha){
		double[][] ans = new double [FuzzySim.length][FuzzySim.length];
		for(int i=0;i<FuzzySim.length;++i){
			for(int j=i;j<FuzzySim.length;++j){
				if(i==j){
					ans[i][j]=1;
				}
				else{
					ans[i][j]=FuzzySim[i][j]>=alpha?1:0;    
					ans[j][i]=ans[i][j];
				}
			}
		}
		return ans;
	}
	static double New_Fuzzy(double[][] Sb, double[][] Sd){
		double ans = 0;	

		
		int U = Sb.length;
		int Ud = Sd.length;
		for(int i=0;i<U;++i){
			for(int j=0;j<Ud;++j){
				double part1 = doubleArrays2Sum(ArraysIntersection(Sb[i],Sd[j])); //|Xib ^ Dj|
				double part2 = doubleArrays2Sum(Sb[i]);//|Sb|
				//System.out.println(part1+":"+part2);
				if(part2!=0)
				ans += part1*log2(part1/part2);
			}
		}
		
		
		
		return -ans/(double)U;
	}
	
	static double Element_Fuzzy(double[][] Sb, double[][] Sd){
		double ans = 0;	
		int U = Sb.length;

		
		for(int i=0;i<U;++i){

				double part1 = doubleArrays2Sum(ArraysIntersection(Sb[i],Sd[i])); //|Xib ^ Dj|
				double part2 = doubleArrays2Sum(Sb[i]);//|Sb|
				//System.out.println(part1+":"+part2);
				if(part2!=0)
				ans += log2(part1/part2);
			
		}
		
		
		if(ans==0)
			return 0.0;
		else	return -ans/(double)U;	
	}
	static double New_Covering(double[][] Sb,double[][] Dj){
		double ans = 0;
		int U = Sb.length;
	


		int Ud = Dj.length;
		for(int i=0;i<U;++i){
			for(int j=0;j<Ud;++j){
				double part1 = doubleArrays2Sum(ArraysIntersection(Sb[i],Dj[j])); //|Sb ^ Dj|
				double part2 = doubleArrays2Sum(Sb[i]);//|Sb|
				if(part2!=0)
				ans += part1*log2(part1/part2);
			}
		}
				
		return -ans/(double)U;
	}
	static double Element_Covering( double[][] Sb,double[][] Sd){
		double ans = 0;
		int U = Sd.length;	
		for(int i=0;i<U;++i){

				double part1 = doubleArrays2Sum(ArraysIntersection(Sb[i],Sd[i])); //|Sb ^ Sd|
				double part2 = doubleArrays2Sum(Sb[i]);//|Sb|
				if(part2!=0)
				ans += log2(part1/part2);			
		}
			
		return -ans/(double)U;
	}
	
	private static double doubleArrays2Sum (double[] X){
		int U = X.length;
		double ans = 0;
		
		for(int i=0;i<U;++i){
			ans += X[i];
		}
		return ans;
	}
	private  static double log2(double x){
		if (x!=0)
			return Math.log(x) / Math.log((double)2);
			else return 0.0;
	}
	private static double[] ArraysIntersection(double[] X, double[] Y){
		int U = X.length;
		double[] ans = new double[U];
		for(int i=0;i<U;++i){
			ans[i] = Math.min(X[i], Y[i]);
		}
		
		return ans;
	}
	static double[][] SumIn(double[][] sb1,double[][] sb2){
		double[][] res = new double[sb1.length][sb1.length];
		for(int i=0;i<sb1.length;++i){
			for(int j=0;j<sb1.length;++j){
				res[i][j]=Math.min(sb1[i][j], sb2[i][j]);
			}
		}
		return res;
	}
	static int FindMinind(double[] ans){
		double res = 1000000;
		int ind = -1;
		for(int i=0;i<ans.length;++i){
			if(ans[i]<res){
				res = ans[i];
				ind = i;
			}
		}
		return ind;
	}
	static double FindMin(double[] ans){
		double res = 100000;
		for(int i=0;i<ans.length;++i){
			if(ans[i]<res){
				res = ans[i];
			}
		}
		return res;
	}

	
}
