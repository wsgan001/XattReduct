package CheckMonotony;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Random;
import java.util.Vector;

import CheckMonotony.Entropy.newEntropy;
import Xreducer_core.Utils;
import Xreducer_fuzzy.SStyle_Abs1lambda_Vmax;
import Xreducer_fuzzy.SStyle_MaxMin;
import Xreducer_fuzzy.SimilarityStyle;

import weka.core.Instances;

public class InconsistentCheck {
	public static void main(String[] args) throws Exception{
		//test2(newEntropy.elementFuzzy);
		testAll(1000,newEntropy.newCovering);
	}
	public static void testAll(int runtime, newEntropy measure_type) throws Exception{
		String fn = "C:/Users/Eric/Desktop/2012春/Paper.new.NO3/ex2.arff";
		//String fn = "C:/Users/Eric/Desktop/2011秋冬/Code/Xreducer/data/Data/libras.arff";
		//String fn = "C:/Users/Eric/Desktop/2011秋冬/Code/Xreducer/data/Data/wdbc.arff";
		//String fn = "C:/Users/Eric/Desktop/2011秋冬/Code/Xreducer/data/Data/ionosphere.arff";
		Instances originaldata = new Instances(new FileReader(fn));
		originaldata.setClassIndex(originaldata.numAttributes()-1); //设置决策属性索引
		
		int R = 10;
		double ratestep = 0.01;
		double[] ans = new double[R];
		double[] ansrate = new double[R];
		double originalrate = getInconsistentRate(Data2Matrix(originaldata));
		
		//SimilarityStyle xfs = new SStyle_MaxMin();
		//SimilarityStyle xfs = new SStyle_Abs1lambda_Vmax(4);
		
		Vector<SimilarityStyle> xfss = new Vector<SimilarityStyle>(originaldata.numAttributes());
		for(int k=0;k<originaldata.numAttributes()-1;++k){
			
			SimilarityStyle xfs = new SStyle_MaxMin();
			double[] Vas = originaldata.attributeToDoubleArray(k);			
			double[] vasStatistics = Utils.getStatisticsValue(Vas);
			xfs.SimilaritySetting(vasStatistics[1],vasStatistics[2],vasStatistics[5]);
			
			xfss.add(xfs);
		}
		
		
		ans[0] = getAllFeatureEntropy(originaldata,measure_type,xfss);
		ansrate[0] = originalrate;
		
		
		//int k=880;
		for(int k=0;k<runtime;++k){
			//System.out.println(k);
			Instances data = new Instances(originaldata);
			double currentrate = originalrate;
			for(int i=1;i<R;++i){
				currentrate = currentrate+ ratestep;
				data = getInconsistentData(data,currentrate,new Random(i+k));
				//System.out.println(i);
				//System.out.println(data);
				ans[i] = getAllFeatureEntropy(data,measure_type,xfss);
				ansrate[i] = currentrate;
			}
			//if(isMath(ans)){
				//System.out.println(k);
				//System.out.println(Arrays.toString(ans));
				for(int q = 0;q<ans.length;++q)
				{
					System.out.println(ans[q]);
				}
				/*System.out.println("###");
				for(int q = 0;q<ans.length;++q)
				{
					System.out.println(ansrate[q]);
				}
				//System.out.println(Arrays.toString(ansrate));
				
				Instances datax = new Instances(originaldata);
				double currentratex = originalrate;
				System.out.println("0");
				boolean[] A = Utils.Instances2FullBoolean(data);
				double[][] Mx =  Entropy.Data2Sim_Fuzzy(datax,A);
				//for(int q=0;q<Mx.length;++q){
					//System.out.println(Arrays.toString(Mx[q]));
				//}
				for(int i=1;i<R;++i){
					currentratex = currentratex+ ratestep;
					datax = getInconsistentData(datax,currentratex,new Random(i+k));
					//System.out.println(i);
					//System.out.println(datax);
					//Mx =  Entropy.Data2Sim_Fuzzy(datax,A);
					//for(int q=0;q<Mx.length;++q){
						//System.out.println(Arrays.toString(Mx[q]));
					//}
				}*/
				
				
				break;
			//}
		}
		
	}
	private static boolean isMath(double[] ans) {
		// TODO Auto-generated method stub
		boolean tag = false;
		for(int i=0;i<ans.length-1;++i){
			if(ans[i]>ans[i+1])
			{
				tag = true;
				break;
			}
		}
		return tag;
	}
	public static void test2(newEntropy measure_type) throws Exception{
		String fn = "C:/Users/Eric/Desktop/2012春/Paper.new.NO3/ex2.arff";
		//String fn = "C:/Users/Eric/Desktop/2011秋冬/Code/Xreducer/data/Data/ionosphere.arff";
		Instances data = new Instances(new FileReader(fn));
		data.setClassIndex(data.numAttributes()-1); //设置决策属性索引
		
		int R = 10;
		double ratestep = 0.1;
		double[] ans = new double[R];
		double[] ansrate = new double[R];
		double originalrate = getInconsistentRate(Data2Matrix(data));
		ans[0] = getAllFeatureEntropy(data,measure_type);
		ansrate[0] = originalrate;
		double currentrate = originalrate;
		for(int i=1;i<R;++i){
			currentrate = currentrate+ ratestep;
			data = getInconsistentData(data,currentrate,new Random(i));
			//System.out.println(i);
			//System.out.println(data);
			ans[i] = getAllFeatureEntropy(data,measure_type);
			ansrate[i] = currentrate;
		}
		System.out.println(Arrays.toString(ans));
		System.out.println(Arrays.toString(ansrate));
	}
	public static double getAllFeatureEntropy (Instances data,newEntropy measure_type, Vector<SimilarityStyle> xfss) throws Exception{
		boolean[] D = Utils.Instances2DecBoolean(data);
		boolean[] A = Utils.Instances2FullBoolean(data);
		double ha = Entropy.getMeasure(data,measure_type,D,A,xfss);
		return ha;
	}
	
	public static double getAllFeatureEntropy (Instances data,newEntropy measure_type) throws Exception{
		boolean[] D = Utils.Instances2DecBoolean(data);
		boolean[] A = Utils.Instances2FullBoolean(data);
		double ha = Entropy.getMeasure(data,measure_type,D,A);
		return ha;
	}
	
	
	public static Instances getInconsistentData(Instances olddata, double rate, Random rd){
		boolean[][] Matrix = Data2Matrix(olddata);
		Matrix = getInconsistentMatrix(Matrix,rate,rd);
		return Matrix2Data(olddata,Matrix);
		 
	}
	public static boolean[][] getInconsistentMatrix(boolean[][] oldM,double rate, Random rd){
		int N = oldM.length;
		int M = oldM[0].length;
		boolean[][] newM = oldM.clone();
		int ALL = N*M;
		double oldrate = getInconsistentRate(oldM);
		double newrate = rate-oldrate;
		int cnt = (int)(newrate*ALL);
		while(cnt!=0){
			int x = rd.nextInt(N);
			int y = rd.nextInt(M);
			if(!newM[x][y]){
				newM[x][y] = true;
				cnt--;
			}
		}	
		return newM;
	}
 
	public static double getInconsistentRate(boolean[][] Matix){
		int N = Matix.length;
		int M = Matix[0].length;
		int sum = 0;
		for(int i=0;i<N;++i){
			for(int j=0;j<M;++j){
				if(Matix[i][j])
					sum++;
			}
		}
		double res = (double)sum/((double)N*M);
		return res;
	}
	public static boolean[][] Data2Matrix(Instances data){
		int N = data.numInstances();
		int M = data.numAttributes()-1;
		boolean[][] ans = new boolean[N][M];
		for(int i=0;i<N;++i){
			for(int j=0;j<M;++j){
				if(data.instance(i).isMissing(j))
				{
					ans[i][j] = true;
				}
				else
				{
					ans[i][j] = false;
				}
			}
		}
		return ans;
	}
	
	public static Instances Matrix2Data(Instances data, boolean[][] Matrix){
		int N = Matrix.length;
		int M = Matrix[0].length;
		Instances newdata = data;
	 
		for(int i=0;i<N;++i){
			for(int j=0;j<M;++j){
				if(Matrix[i][j])
				{
					newdata.instance(i).setMissing(j);
				}
			}
		}
		return newdata;
	}
	
	
	public static void test() throws FileNotFoundException, IOException{
		//String fn = "C:/Users/Eric/Desktop/2012春/Paper.new.NO3/ex2.arff";
		String fn = "C:/Users/Eric/Desktop/2011秋冬/Code/Xreducer/data/Data/libras.arff";
		Instances data = new Instances(new FileReader(fn));
		data.setClassIndex(data.numAttributes()-1); //设置决策属性索引
		
		int N = data.numInstances();
		int M = data.numAttributes();
		int ALL = N*(M-1);
		
			
		//System.out.println(data);
		data.instance(0).setMissing(1);
		System.out.println(data);
		
		/*double[] Vas = data.attributeToDoubleArray(0);	
		System.out.println(Arrays.toString(Vas));
		double[] vasStatistics = Utils.getStatisticsValue(Vas);

		
		
		System.out.println(Arrays.toString(vasStatistics));
		SimilarityStyle xfs = new SStyle_Abs1lambda_Vmax(4);
		xfs.SimilaritySetting(vasStatistics[1],vasStatistics[2],vasStatistics[5]);
		double x1 = data.instance(0).value(0);
		double x2 = data.instance(1).value(0);
		double simValue = xfs.getSimilarityValue(x1,x2);
		System.out.println(simValue);	*/
		
	}
	
	
	
}
