package Xreducer_fuzzy;

import java.io.FileReader;
import java.util.Arrays;

import org.apache.commons.math.stat.descriptive.moment.Mean;
import org.apache.commons.math.stat.descriptive.moment.StandardDeviation;
import org.apache.commons.math.stat.inference.TestUtils;

import weka.classifiers.Classifier;
import weka.classifiers.bayes.NaiveBayes;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Remove;
import weka.filters.unsupervised.attribute.ReplaceMissingValues;
import Xreducer_core.Utils;

public class testRes {
	public static double[][] MypairedTtest(double[][] X, int baseindex , double singificantlevel) throws Exception{
		int N = X.length;
		//int M = X[0].length;
		double[][] res = new double[N][4];
		Mean mean = new Mean(); // ����ƽ��ֵ
		//Variance variance = new Variance(); // ����
		StandardDeviation sd = new StandardDeviation();// ��׼����
		//�����ֵ����д��res[0] res[1]��
		for(int i=0;i<N;++i){
			res[i][0] = mean.evaluate(X[i]);
			res[i][1] = sd.evaluate(X[i]);
		}
		//DecimalFormat df = new DecimalFormat("0.000000");

		//����paried t-test
		for(int i=0;i<N;++i){
			double pval;
			boolean H;
			if(i!=baseindex){
					pval = TestUtils.pairedTTest(X[baseindex], X[i]);
					H = TestUtils.pairedTTest(X[baseindex], X[i], singificantlevel);
					if(!H){//û���������죬ƽ�� 3.tie
						res[i][2]=3;
						res[i][3]=pval;
					}
					else{//����������, ��ֵ��Ļ�ʤ 1.win 2.lose ? 1.lose 2.win
						res[i][2]=res[baseindex][0]>=res[i][0]?1:2;
						res[i][3]=pval;
					}
					//String num = df.format(pval);
					//System.out.println(num);
			}
		}
		res[baseindex][2]=0;
		res[baseindex][3]=-1;
		return res;
	}
	public static double[] myEvolution(Classifier cl,Instances data,int[] seletatts, int fold , int runtime) throws Exception{
		int [] reAttr = Utils.seletatt2removeAtt(seletatts);
		Remove m_removeFilter = new Remove();
		m_removeFilter.setAttributeIndicesArray(reAttr);
		m_removeFilter.setInvertSelection(false);
    	m_removeFilter.setInputFormat(data);   
    	Instances newData = Filter.useFilter(data, m_removeFilter);
    	newData.setClassIndex( newData.numAttributes() - 1 ); //�������þ�����������
    	double[] ans = Utils.multicrossValidateModel(cl, newData, runtime, fold );
		return ans;
	}
	public static void main(String[] args) throws Exception {
		String path = "C:/Users/Eric/Desktop/2011�ﶬ/Code/Xreducer/data/Data/";
		String fn = "cleveland";
		System.out.println(fn+"\n");
		fn = path+fn+".arff";
		Instances m_data = new Instances(new FileReader(fn));
		m_data.setClassIndex(m_data.numAttributes()-1); 
		// Replace missing values   //����ֵ����
		ReplaceMissingValues m_ReplaceMissingValues = new ReplaceMissingValues();
		m_ReplaceMissingValues.setInputFormat(m_data);
		m_data = Filter.useFilter(m_data, m_ReplaceMissingValues);
		
		int [][] m_seletAtts = {
				{0, 1, 2, 7, 8, 9, 10, 11, 12, 13},
				{0, 2, 7, 8, 9, 10, 11, 12, 13},
				{2, 7, 8, 9, 10, 11, 12, 13},
				{0, 2, 3, 4, 6, 7, 9, 11, 13},
				{0, 2, 3, 4, 6, 7, 9, 11, 13},
				{0, 2, 3, 4, 6, 7, 8, 9, 11, 12, 13},
				{0, 2, 3, 4, 6, 7, 9, 11, 12, 13},
									};
		Classifier cl = new NaiveBayes();
		double[][] ans = new double[8][10];
		
		
		//ѵ�� �ó�׼ȷ��
		for(int i=0;i<m_seletAtts.length;++i){//һ�������㷨 ��Ӧm��ͬ��Լ���㷨 ���ans[m][runtime] ����ȫ��			
			double[] tmp = myEvolution(cl, m_data, m_seletAtts[i], 10, 10);
			 for(int j=0;j<10;++j)
				 ans[i][j] = tmp[j];
		}
		
	}
}
