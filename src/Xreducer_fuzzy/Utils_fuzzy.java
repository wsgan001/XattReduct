package Xreducer_fuzzy;

import java.util.Arrays;

import weka.core.Instances;
import Xreducer_core.Utils;


public class Utils_fuzzy {


	public static double getFuzzyConditionEntropy(Instances dataset,boolean[] D, boolean[] P,SimilarityStyle xfs, ImplicatorTnormStyle its){
		double ans = 0.0;
		int U = dataset.numInstances();
		int N = dataset.numAttributes();
		int classind = dataset.classIndex();
		for(int i=0;i<U;++i){
			double[] sumV = new double[U];
			Arrays.fill(sumV, 1);
			for(int k=0;k<N;++k){
				if(P[k]&&dataset.attribute(k).isNumeric()){
					double[] Vas = dataset.attributeToDoubleArray(k);			
					double[] vasStatistics = Utils.getStatisticsValue(Vas);
					for(int q=0;q<U;++q) {
						xfs.SimilaritySetting(vasStatistics[1],vasStatistics[2],vasStatistics[5]);
						sumV[q] = its.getfuzzyTnromValue(sumV[q],xfs.getSimilarityValue(dataset.instance(i).value(k),dataset.instance(q).value(k)));
					}
				}
				else if(P[k]&&!dataset.attribute(k).isNumeric()){
					for(int q=0;q<U;++q)
						sumV[q] = its.getfuzzyTnromValue(sumV[q],dataset.instance(i).value(k)==dataset.instance(q).value(k)?1:0);
				} 
			}
			double part2 = Utils.getArraysSum(sumV);
			for(int q=0;q<U;++q)
				sumV[q] = its.getfuzzyTnromValue(sumV[q],dataset.instance(i).value(classind)==dataset.instance(q).value(classind)?1:0);
			double part1 = Utils.getArraysSum(sumV);
			//System.out.println(part1+":"+part2);
			if(part1!=0)		
				ans = ans+Utils.log2(part1/part2);
		}
		if(ans==0)
			return 0.0;
		else	return -ans/(double)U;	
	}
	public static double getFuzzySU(Instances dataset, boolean[] D, boolean[] P,SimilarityStyle xfs, ImplicatorTnormStyle its){
		double fHp = 0.0;
		double fHd = 0.0;
		double fHdp = 0.0;
		int U = dataset.numInstances();
		int N = dataset.numAttributes();
		int classind = dataset.classIndex();
		for(int i=0;i<U;++i){
			double[] sumV = new double[U];
			Arrays.fill(sumV, 1);
			double[] sumD = new double[U];
			Arrays.fill(sumD, 1);
			for(int k=0;k<N;++k){
				if(P[k]&&dataset.attribute(k).isNumeric()){
					double[] Vas = dataset.attributeToDoubleArray(k);			
					double[] vasStatistics = Utils.getStatisticsValue(Vas);
					for(int q=0;q<U;++q) {
						xfs.SimilaritySetting(vasStatistics[1],vasStatistics[2],vasStatistics[5]);
						sumV[q] = its.getfuzzyTnromValue(sumV[q],xfs.getSimilarityValue(dataset.instance(i).value(k),dataset.instance(q).value(k)));
					}
				}
				else if(P[k]&&!dataset.attribute(k).isNumeric()){
					for(int q=0;q<U;++q)
						sumV[q] = its.getfuzzyTnromValue(sumV[q],dataset.instance(i).value(k)==dataset.instance(q).value(k)?1:0);
				} 
			}
			double partp = Utils.getArraysSum(sumV);
			for(int q=0;q<U;++q){
				sumD[q] = dataset.instance(i).value(classind)==dataset.instance(q).value(classind)?1:0;
				sumV[q] = its.getfuzzyTnromValue(sumV[q],sumD[q]);
				}
			double partpd = Utils.getArraysSum(sumV);
			double partd = Utils.getArraysSum(sumD);
				fHd += Utils.log2(partd/(double)U);
				fHp += Utils.log2(partp/(double)U);
			if(partp!=0)
				fHdp += Utils.log2(partpd/partp);
		}
		//System.out.println(ans);	
		double ans = 2.0*(fHd-fHdp)/(fHd+fHp);
		if(ans<0.0000000001)
			return 0.0;
		return ans;
	}

	public static double[] getStatisticsValue(double[] Vas){
		//System.out.println("####");
		double[] ans = new double[6];//0.sum 1.max 2.min 3.mean 4var 5.std
		int cnt = 1;
		ans[0]=Vas[0];
		ans[1]=Vas[0];
		ans[2]=Vas[0];
		for(int i=1;i<Vas.length;++i){
			ans[0] += Vas[i];
			ans[1] = Vas[i]>ans[1]?Vas[i]:ans[1];
			ans[2] = Vas[i]<ans[2]?Vas[i]:ans[2];
			cnt++;
			
		}
		ans[3] = ans[0]/(double)cnt;
		for(int i=0;i<Vas.length;++i){
			ans[4] += (Vas[i]-ans[3])*(Vas[i]-ans[3]);
		}
		ans[4] = ans[4]/(double)(cnt-1);
		ans[5] = Math.sqrt(ans[4]);
		return ans;
	}
}
