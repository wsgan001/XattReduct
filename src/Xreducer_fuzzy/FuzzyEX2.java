package Xreducer_fuzzy;

import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Random;

import weka.core.Instances;
import Xreducer_core.Utils;

public class FuzzyEX2 {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		// TODO Auto-generated method stub
		//String fn = "C:/Users/Eric/Desktop/2011秋冬/Code/Xreducer/data/Data/cleveland.arff";
		String fn = "C:/Users/Eric/Desktop/2011秋冬/Code/Xreducer/data/fuzzy/ex2.arff";
		ImplicatorTnormStyle itstyle = new ITStyle_Lukasiewicz(); 
		SimilarityStyle sstyle = new SStyle_MaxMin();
		 Random rnd = new Random(4);
			int cnt = 100000;
			double lambda = 1.0;
			for(int i=0;i<cnt;++i){
				
				
				Xreducer_core.Utils_fuzzy.randomdataset2(fn,25,10,3,rnd);
				Instances m_data = new Instances(new FileReader(fn));
				m_data.setClassIndex(m_data.numAttributes()-1); 
	
				System.out.println(i);
				MStyle_FuzzySU mg = new MStyle_FuzzySU(m_data,sstyle, itstyle, lambda);

				int U = m_data.numInstances();
				int N = m_data.numAttributes();
				 
				
				int[] selectattr = mg.m_selectAtt;
				int[] susort_index = mg.SUsort_index;
				double[] susort_value = mg.SUsort_value;
				double[] su_value = mg.SU_value;
				if(!intArraysIsContains(susort_index,selectattr))
				{
					System.out.println(Arrays.toString(susort_index));
					System.out.println(Arrays.toString(susort_value));
					System.out.println(Arrays.toString(su_value));
					break;
				}
				
				
	}


	}
	public static boolean intArraysIsContains(int[] intstring, int [] subintstring){ //检查intstring前面是否包含subintstring
		int N=intstring.length;
		boolean[] A1 = new boolean[N];
		boolean[] A2 = new boolean[N];
		for(int i=0;i<subintstring.length-1;++i){
			A1[intstring[i]]=true;
			A2[subintstring[i]]=true;
		}
		for(int i=0;i<N;++i){
			if((A1[i]&&!A2[i])||(A2[i]&&!A1[i]))
				return false;
		}
		return true;
		
	}
}
