package CheckMonotony;

import java.util.Arrays;
import java.util.Vector;

import Xreducer_core.Utils;
import Xreducer_core.Utils_entropy;
import Xreducer_fuzzy.SStyle_Abs1lambda_Vmax;
import Xreducer_fuzzy.SStyle_MaxMin;
import Xreducer_fuzzy.SimilarityStyle;
import weka.core.Instances;
import weka.filters.Filter;


public class Entropy {
	static public enum newEntropy{
		newCovering,
		newPartition,
		newFuzzy,
		elementCovering,
		elementPartition,
		elementFuzzy
	}
	public static double getMeasure(Instances data,newEntropy me, boolean[]D, boolean[] B, Vector<SimilarityStyle> xfss) throws Exception{
		weka.filters.supervised.attribute.Discretize sd = new weka.filters.supervised.attribute.Discretize();
		 
		sd.setInputFormat(data);
		Instances dataD = Filter.useFilter(data , sd);
		dataD.setClassIndex(data.numAttributes()-1);
		double ans = 0;
		switch(me){
		case newCovering:{
			ans =  New_Covering(data,D,B,xfss);
			break;
		}
		case newPartition:{
			ans =  New_Partition(dataD,D,B);
			break;
		}
		case newFuzzy:{
			ans =  New_Fuzzy_Version1(data,D,B,xfss);
			break;
		}
		case elementCovering:{
			ans =  Element_Covering(data,D,B,xfss);
			break;
		}
		case elementPartition:{
			ans =  Element_Partition(dataD,D,B);
			break;
		}
		case elementFuzzy:{
			ans =  Element_Fuzzy(data,D,B,xfss);
			break;
		}
		default:break;
		}
		return ans;
	}
	public static double getMeasure(Instances data,newEntropy me, boolean[]D, boolean[] B) throws Exception{
		weka.filters.supervised.attribute.Discretize sd = new weka.filters.supervised.attribute.Discretize();
		 
		sd.setInputFormat(data);
		Instances dataD = Filter.useFilter(data , sd);
		dataD.setClassIndex(data.numAttributes()-1);
		double ans = 0;
		switch(me){
		case newCovering:{
			ans =  New_Covering(data,D,B);
			break;
		}
		case newPartition:{
			ans =  New_Partition(dataD,D,B);
			break;
		}
		case newFuzzy:{
			ans =  New_Fuzzy_Version1(data,D,B);
			break;
		}
		case elementCovering:{
			ans =  Element_Covering(data,D,B);
			break;
		}
		case elementPartition:{
			ans =  Element_Partition(dataD,D,B);
			break;
		}
		case elementFuzzy:{
			ans =  Element_Fuzzy(data,D,B);
			break;
		}
		default:break;
		}
		return ans;
	}
	static double[][] Data2Sim_Covering (Instances data, boolean[] B){
		int U = data.numInstances();
		double[][] ans =  Data2Sim_Fuzzy(data,B);
		double lambda = 0.3;
		for(int i=0;i<U;++i){
			for(int j=0;j<U;++j){
				if(ans[i][j]>=lambda)
					ans[i][j] = 1;
				else
					ans[i][j] = 0;
			}
		}
	
		return ans;
	}
	static double[][] Data2Sim_Covering (Instances data, boolean[] B,Vector<SimilarityStyle> xfss){
		int U = data.numInstances();
		double[][] ans =  Data2Sim_Fuzzy(data,B,xfss);
		double lambda = 0.3;
		for(int i=0;i<U;++i){
			for(int j=0;j<U;++j){
				if(ans[i][j]>=lambda)
					ans[i][j] = 1;
				else
					ans[i][j] = 0;
			}
		}
	
		return ans;
	}
	
	static double[][] Data2Sim_Partition (Instances data, boolean[] B){
		int U = data.numInstances();
		double[][] ans = new double[U][U];
		for(int i=0;i<U;++i){
			Arrays.fill(ans[i], 1);}
		 for(int k=0;k<B.length;++k){
				if(B[k])
					for(int i=0;i<U;++i){
						for(int q=i;q<U;++q){
							ans[i][q] = Math.min(ans[i][q],data.instance(i).value(k)==data.instance(q).value(k)?1:0);
							ans[q][i] = ans[i][q]  ;}
				} 
					
		 }
		
		
		
		return ans;
	}
	
	static double[][] Data2Sim_Fuzzy (Instances data, boolean[] B){
		int U = data.numInstances();
		double[][] ans = new double[U][U];
		for(int i=0;i<U;++i){
			Arrays.fill(ans[i], 1);}
		SimilarityStyle xfs = new SStyle_MaxMin();
		//SimilarityStyle xfs = new SStyle_Abs1lambda_Vmax(4);
		
	    for(int k=0;k<B.length;++k){
				if(B[k]&&data.attribute(k).isNumeric()){
					double[] Vas = data.attributeToDoubleArray(k);			
					double[] vasStatistics = Utils.getStatisticsValue(Vas);
					for(int i=0;i<U;++i){
						for(int q=i+1;q<U;++q) {
							xfs.SimilaritySetting(vasStatistics[1],vasStatistics[2],vasStatistics[5]);
							double x1 = data.instance(i).value(k);
							double x2 = data.instance(q).value(k);
							double simValue = xfs.getSimilarityValue(x1,x2);
							ans[i][q] = Math.min(ans[i][q],simValue);
							ans[q][i] = ans[i][q] ;
						}}
				}
				else if(B[k]&&!data.attribute(k).isNumeric()){
					for(int i=0;i<U;++i){
						for(int q=i+1;q<U;++q){
							ans[i][q] = Math.min(ans[i][q],data.instance(i).value(k)==data.instance(q).value(k)?1:0);
							ans[q][i] = ans[i][q];}
				} 
				}
				
			
		}
			
		return ans;
	}
	static double[][] Data2Sim_Fuzzy (Instances data, boolean[] B,Vector<SimilarityStyle> xfss){
		int U = data.numInstances();
		double[][] ans = new double[U][U];
		for(int i=0;i<U;++i){
			Arrays.fill(ans[i], 1);}
		//SimilarityStyle xfs = new SStyle_MaxMin();
		//SimilarityStyle xfs = new SStyle_Abs1lambda_Vmax(4);
		
	    for(int k=0;k<B.length;++k){
				if(B[k]&&data.attribute(k).isNumeric()){
					//double[] Vas = data.attributeToDoubleArray(k);			
					//double[] vasStatistics = Utils.getStatisticsValue(Vas);
					for(int i=0;i<U;++i){
						for(int q=i+1;q<U;++q) {
							//xfs.SimilaritySetting(vasStatistics[1],vasStatistics[2],vasStatistics[5]);
							SimilarityStyle xfs = xfss.get(k);
							double x1 = data.instance(i).value(k);
							double x2 = data.instance(q).value(k);
							double simValue = xfs.getSimilarityValue(x1,x2);
							ans[i][q] = Math.min(ans[i][q],simValue);
							ans[q][i] = ans[i][q] ;
						}}
				}
				else if(B[k]&&!data.attribute(k).isNumeric()){
					for(int i=0;i<U;++i){
						for(int q=i+1;q<U;++q){
							ans[i][q] = Math.min(ans[i][q],data.instance(i).value(k)==data.instance(q).value(k)?1:0);
							ans[q][i] = ans[i][q];}
				} 
				}
				
			
		}
			
		return ans;
	}
	private static double[][] boolean2double(boolean[][] res) {
		// TODO Auto-generated method stub
		
		int U = res.length;
		int Ud = res[0].length;
		double[][] ans = new double[Ud][U];
		for(int i =0;i<U;++i){
			for(int j=0;j<Ud;++j){
				if(res[i][j])
					ans[j][i]=1;
				else
					ans[j][i]=0;
			}
		}
		
		return ans;
	}
	
	private static double[] ArraysIntersection(double[] X, double[] Y){
		int U = X.length;
		double[] ans = new double[U];
		for(int i=0;i<U;++i){
			ans[i] = Math.min(X[i], Y[i]);
		}
		
		return ans;
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
	
	static double New_Covering(Instances data, boolean[] d, boolean[] B){
		double ans = 0;
		double[][] Sb = Data2Sim_Covering(data,B);  //[U][U]
		double[][] Dj = boolean2double(Utils_entropy.getEquivalenceClass(data,d));  //[U/d][U]
	
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
	static double New_Covering(Instances data, boolean[] d, boolean[] B,Vector<SimilarityStyle> xfss){
		double ans = 0;
		double[][] Sb = Data2Sim_Covering(data,B,xfss);  //[U][U]
		double[][] Dj = boolean2double(Utils_entropy.getEquivalenceClass(data,d));  //[U/d][U]
	
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


	static double New_Partition(Instances data, boolean[] d, boolean[] B){
		double ans = 0;
		double[][] Bi = boolean2double(Utils_entropy.getEquivalenceClass(data,B));  //[U/B][U]
		double[][] Dj = boolean2double(Utils_entropy.getEquivalenceClass(data,d));  //[U/d][U]
		int U = data.numInstances();
		int Ub = Bi.length;
		int Ud = Dj.length;
		for(int i=0;i<Ub;++i){
			for(int j=0;j<Ud;++j){
				double part1 = doubleArrays2Sum(ArraysIntersection(Bi[i],Dj[j])); //|Bi ^ Dj|
				double part2 = doubleArrays2Sum(Bi[i]);//|Sb|
				if(part2!=0)
				ans += part2*part1*log2(part1/part2);
			}
		}
		if(ans==0)
			return 0;
		return -ans/(double)U;
	}

	static double New_Fuzzy_Version1(Instances data, boolean[] d, boolean[] B){
		double ans = 0;	
		double[][] Xib = Data2Sim_Fuzzy(data,B);  //[U][U]
		double[][] Dj = boolean2double(Utils_entropy.getEquivalenceClass(data,d));  //[U/d][U]
		
		int U = Xib.length;
		int Ud = Dj.length;
		for(int i=0;i<U;++i){
			for(int j=0;j<Ud;++j){
				double part1 = doubleArrays2Sum(ArraysIntersection(Xib[i],Dj[j])); //|Xib ^ Dj|
				double part2 = doubleArrays2Sum(Xib[i]);//|Sb|
				//System.out.println(part1+":"+part2);
				if(part2!=0)
				ans += part1*log2(part1/part2);
			}
		}
		
		
		
		return -ans/(double)U;
	}
	static double New_Fuzzy_Version1(Instances data, boolean[] d, boolean[] B,Vector<SimilarityStyle> xfss){
		double ans = 0;	
		double[][] Xib = Data2Sim_Fuzzy(data,B,xfss);  //[U][U]
		double[][] Dj = boolean2double(Utils_entropy.getEquivalenceClass(data,d));  //[U/d][U]
		
		int U = Xib.length;
		int Ud = Dj.length;
		for(int i=0;i<U;++i){
			for(int j=0;j<Ud;++j){
				double part1 = doubleArrays2Sum(ArraysIntersection(Xib[i],Dj[j])); //|Xib ^ Dj|
				double part2 = doubleArrays2Sum(Xib[i]);//|Sb|
				//System.out.println(part1+":"+part2);
				if(part2!=0)
				ans += part1*log2(part1/part2);
			}
		}
		
		
		
		return -ans/(double)U;
	}
	static double New_Fuzzy_Version2(Instances data, boolean[] d, boolean[] B){
		double ans = 0;	
		int U = data.numInstances();
		
		double[][] Xib = Data2Sim_Fuzzy(data,B);  //[U][U]
		double[][] Xid = Data2Sim_Fuzzy(data,d);  //[U][U]
		
 
		for(int i=0;i<U;++i){
				double part1 = doubleArrays2Sum(ArraysIntersection(Xib[i],Xid[i])); //|Xib ^ Xid|
				double part2 = doubleArrays2Sum(Xib[i]);//|Sb|
				//System.out.println(part1+":"+part2);
				if(part2!=0)
				ans += part1*log2(part1/part2);
			
		}
		
		
		return -ans/(double)U;
	}
	
	
	static double Element_Covering(Instances data, boolean[] d, boolean[] B){
		double ans = 0;
		int U = data.numInstances();
		
		double[][] Sb = Data2Sim_Covering(data,B);  //[U][U]
		double[][] Sd = Data2Sim_Covering(data,d);  //[U][U]
	
		for(int i=0;i<U;++i){

				double part1 = doubleArrays2Sum(ArraysIntersection(Sb[i],Sd[i])); //|Sb ^ Sd|
				double part2 = doubleArrays2Sum(Sb[i]);//|Sb|
				if(part2!=0)
				ans += log2(part1/part2);
			
		}
		
		
	
		return -ans/(double)U;
	}
	static double Element_Covering(Instances data, boolean[] d, boolean[] B,Vector<SimilarityStyle> xfss){
		double ans = 0;
		int U = data.numInstances();
		
		double[][] Sb = Data2Sim_Covering(data,B,xfss);  //[U][U]
		double[][] Sd = Data2Sim_Covering(data,d,xfss);  //[U][U]
	
		for(int i=0;i<U;++i){

				double part1 = doubleArrays2Sum(ArraysIntersection(Sb[i],Sd[i])); //|Sb ^ Sd|
				double part2 = doubleArrays2Sum(Sb[i]);//|Sb|
				if(part2!=0)
				ans += log2(part1/part2);
			
		}
		
		
	
		return -ans/(double)U;
	}
	static double Element_Partition(Instances data, boolean[] d, boolean[] B){
		double ans = 0;	
		int U = data.numInstances();
		double[][] Bi = boolean2double(Utils_entropy.getEquivalenceClass(data,B));  //[U/B][U]
		double[][] Dj = boolean2double(Utils_entropy.getEquivalenceClass(data,d));  //[U/d][U]
		 
		int Ub = Bi.length;
		int Ud = Dj.length;
		for(int i=0;i<Ub;++i){
			
			for(int j=0;j<Ud;++j){
				//System.out.println(Arrays.toString(Dj[j]));
				double part1 = doubleArrays2Sum(ArraysIntersection(Bi[i],Dj[j])); //|Bi ^ Dj|
				double part2 = doubleArrays2Sum(Bi[i]);//|Sb|
				//System.out.println(part1+":"+part2);
				if(part2!=0)
				ans += part1*log2(part1/part2);
			}
		}
		if(ans==0)
			return 0;
		return -ans/(double)U;
	}
	
	static double Element_Fuzzy(Instances data, boolean[] d, boolean[] B){
		double ans = 0;	
		int U = data.numInstances();
		double[][] Xib = Data2Sim_Fuzzy(data,B);  //[U][U]
		double[][] Xid = Data2Sim_Fuzzy(data,d);  //[U][U]
		
		for(int i=0;i<U;++i){

				double part1 = doubleArrays2Sum(ArraysIntersection(Xib[i],Xid[i])); //|Xib ^ Dj|
				double part2 = doubleArrays2Sum(Xib[i]);//|Sb|
				//System.out.println(part1+":"+part2);
				if(part2!=0)
				ans += log2(part1/part2);
			
		}
		
		
		if(ans==0)
			return 0.0;
		else	return -ans/(double)U;	
	}
	static double Element_Fuzzy(Instances data, boolean[] d, boolean[] B,Vector<SimilarityStyle> xfss){
		double ans = 0;	
		int U = data.numInstances();
		double[][] Xib = Data2Sim_Fuzzy(data,B,xfss);  //[U][U]
		double[][] Xid = Data2Sim_Fuzzy(data,d,xfss);  //[U][U]
		
		for(int i=0;i<U;++i){

				double part1 = doubleArrays2Sum(ArraysIntersection(Xib[i],Xid[i])); //|Xib ^ Dj|
				double part2 = doubleArrays2Sum(Xib[i]);//|Sb|
				//System.out.println(part1+":"+part2);
				if(part2!=0)
				ans += log2(part1/part2);
			
		}
		
		
		if(ans==0)
			return 0.0;
		else	return -ans/(double)U;	
	}
	public static double New_Covering_Version2(Instances data, boolean[] d,
			boolean[] b) {
		double ans = 0;
		double[][] Sb = Data2Sim_Covering(data,b);  //[U][U]
		double[][] Dj = Data2Sim_Covering(data,d);  //[U][U]
	
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

}
