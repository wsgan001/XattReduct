package Xreducer_fuzzy;

import java.io.FileReader;
import java.util.Arrays;
import java.util.Random;

import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.ReplaceMissingValues;
import Xreducer_core.Utils;
import Xreducer_core.Utils_fuzzy;
import Xreducer_core.Utils_fuzzy.xFuzzySimilarity;

public class FuzzyEX {
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		//String fn = "C:/Users/Eric/Desktop/2011Çï¶¬/Code/Xreducer/data/Data/cleveland.arff";
		String fn = "C:/Users/Eric/Desktop/2011Çï¶¬/Code/Xreducer/data/fuzzy/ex.arff";
		ImplicatorTnormStyle itstyle = new ITStyle_Lukasiewicz(); 
		SimilarityStyle sstyle = new SStyle_MaxMin();
		 Random rnd = new Random(4);
			int cnt = 100000;
			double lambda = 1.0;
			for(int i=0;i<cnt;++i){
				
				
				Xreducer_core.Utils_fuzzy.randomdataset(fn,6,4,rnd);
				Instances m_data = new Instances(new FileReader(fn));
				m_data.setClassIndex(m_data.numAttributes()-1); 
	
				
				MStyle_FuzzySU mg = new MStyle_FuzzySU(m_data,sstyle, itstyle, lambda);

				int U = m_data.numInstances();
				int N = m_data.numAttributes();
				boolean[] D=Utils.Instances2DecBoolean(m_data);
				boolean[] P=new boolean[N];			
				double[] sus = new double[N-1];
				for(int k=0;k<N-1;++k){
					P[k]=true;
					sus[k] = mg.getMeausureValue(D, P);
					P[k]=false;
				}
				int[] rankIndex = new int[N-1];
				for(int k=0;k<N-1;++k){
					rankIndex [k] = k;
				}
				double[] restemp = sus.clone();
				double temp;
				int tempindex;
				for(int ii=0;ii<N-1;++ii){//Ã°ÅÝ·¨ÅÅÐò 
					for(int ji=0;ji< N-ii-2;++ji){
						if(restemp[ji]<restemp[ji+1]) {
							//½»»»restemp
							temp = restemp[ji];
							restemp[ji] = restemp[ji + 1];
							restemp[ji + 1] = temp;
							//½»»»entropyRankindex
							tempindex = rankIndex[ji];
							rankIndex[ji] = rankIndex[ji + 1];
							rankIndex[ji + 1] = tempindex;
						}
					}
				}
				System.out.println(i);
				boolean[] P1=new boolean[N];
				boolean[] P2=new boolean[N];	
				boolean[] P3=new boolean[N];
				P1[rankIndex[0]]=true;
				P2[rankIndex[1]]=true;
				P3[rankIndex[2]]=true;
				boolean flag1 =  mg.getMeausureValue(P1,P2)>=lambda* mg.getMeausureValue(D,P2);
				if(!flag1)
					continue;
				boolean flag2 =  mg.getMeausureValue(P1,P3)<lambda* mg.getMeausureValue(D,P3);
				if(!flag2)
					continue;
				P3[rankIndex[0]]=true;
				boolean flag3 = mg.getConditionalEntropy(D, P3)==0;
				if(flag3)
				{
					
					System.out.println(Arrays.toString(rankIndex));
					System.out.println(Arrays.toString(restemp));
					break;
				}
				
				
			}
		
	}
}
