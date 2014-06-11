package Modification;

import java.io.FileReader;

import myUtils.xMath;

import org.apache.commons.math.stat.descriptive.moment.Mean;
import org.apache.commons.math.stat.descriptive.moment.StandardDeviation;

import weka.classifiers.Classifier;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.functions.LibSVM;
import weka.classifiers.trees.J48;
import weka.classifiers.trees.SimpleCart;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Remove;
import Xreducer_core.Utils;

public class testData {

	public static double[] myEvolution(Classifier cl,Instances data,int[] seletatts, int fold , int runtime) throws Exception{
		int [] reAttr = Utils.seletatt2removeAtt(seletatts);
		Remove m_removeFilter = new Remove();
		m_removeFilter.setAttributeIndicesArray(reAttr);
		m_removeFilter.setInvertSelection(false);
    	m_removeFilter.setInputFormat(data);   
    	Instances newData = Filter.useFilter(data, m_removeFilter);
    	newData.setClassIndex( newData.numAttributes() - 1 ); //重新设置决策属性索引
    	double[] ans = Utils.multicrossValidateModel(cl, newData, runtime, fold );
		return ans;
		
	}
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		String path1 = "C:/Users/Eric/Desktop/2011秋冬/Code/Xreducer/data/Data/cleveland.arff";
		String path2 = "C:/Users/Eric/Desktop/2011秋冬/Code/Xreducer/data/Data/credit.arff";
		String path3 = "C:/Users/Eric/Desktop/2011秋冬/Code/Xreducer/data/Data/hepatitis.arff";
		String path4 = "C:/Users/Eric/Desktop/2011秋冬/Code/Xreducer/data/Data/vote.arff";
		Instances data1 = new Instances(new FileReader(path1));
		data1.setClassIndex(data1.numAttributes()-1); 
		Instances data2 = new Instances(new FileReader(path2));
		data2.setClassIndex(data2.numAttributes()-1); 
		Instances data3 = new Instances(new FileReader(path3));
		data3.setClassIndex(data3.numAttributes()-1); 
		Instances data4 = new Instances(new FileReader(path4));
		data4.setClassIndex(data4.numAttributes()-1); 
		/*this.claAlgnames.add("NBC");
		this.m_classifiers.add(new NaiveBayes());
		this.claAlgnames.add("RBF-SVM");
		this.m_classifiers.add(new LibSVM());
		this.claAlgnames.add("C4.5");
		this.m_classifiers.add(new J48());
		 this.claAlgnames.add("CART");	
		 this.m_classifiers.add(new SimpleCart());*/	
		Mean mean = new Mean(); // 算术平均值
		//Variance variance = new Variance(); // 方差
		StandardDeviation sd = new StandardDeviation();// 标准方差
		int[] seletatts1 = {1, 2, 5, 6, 7, 8, 9, 10, 11, 12, 13};
		int[] seletatts21 = {0, 1, 2, 3, 5, 6, 7, 8, 9, 11, 12, 13, 14, 15};
		int[] seletatts22 = {0, 1, 2, 4, 5, 6, 7, 8, 9, 11, 12, 13, 14, 15};
		//int[] seletatts3 = {2, 4, 6, 7, 8, 13, 17, 18, 19};
		int[] seletatts3 = {1,2,3,4,5,6,8,10,12,13,16,17,19};
		
		int[] seletatts41 = {0,1,2,3,4,8,9,10,11,12,14,16};
		//int[] seletatts41 = {0, 1, 2, 3, 5, 6, 9, 10, 11, 12, 13, 15, 16};

		int[] seletatts42 = {0, 1, 2, 3, 5, 7, 9, 10, 11, 12, 13, 15, 16};
		int[] seletatts43 = {0, 1, 2, 3, 5, 8, 9, 10, 11, 12, 13, 15, 16};
		
		double []ans1 = myEvolution(new LibSVM(),data1,seletatts1,10,10);
		double []ans21 = myEvolution(new LibSVM(),data2,seletatts21,10,10);
		double []ans22 = myEvolution(new LibSVM(),data2,seletatts22,10,10);
		double []ans3 = myEvolution(new LibSVM(),data3,seletatts3,10,10);
		double []ans41 = myEvolution(new LibSVM(),data4,seletatts41,10,10);
		double []ans42 = myEvolution(new LibSVM(),data4,seletatts42,10,10);
		double []ans43 = myEvolution(new LibSVM(),data4,seletatts43,10,10);
		System.out.println();
		System.out.println("#####");
		 
			double avg = mean.evaluate(ans1);
			double std = sd.evaluate(ans1);
			
		 System.out.println(Utils.doubleFormat("00.00", avg*100)+"$\\pm$"+Utils.doubleFormat("0.00", std*100));
		 
		 System.out.println("#####");
			avg = mean.evaluate(ans21);
			std = sd.evaluate(ans21);
			
		 System.out.println(Utils.doubleFormat("00.00", avg*100)+"$\\pm$"+Utils.doubleFormat("0.00", std*100));
			avg = mean.evaluate(ans22);
			std = sd.evaluate(ans22);
			
		 System.out.println(Utils.doubleFormat("00.00", avg*100)+"$\\pm$"+Utils.doubleFormat("0.00", std*100));
		 System.out.println("#####");
			avg = mean.evaluate(ans3);
			std = sd.evaluate(ans3);
			
		 System.out.println(Utils.doubleFormat("00.00", avg*100)+"$\\pm$"+Utils.doubleFormat("0.00", std*100));
		 
		 System.out.println("#####");
			avg = mean.evaluate(ans41);
			std = sd.evaluate(ans41);
			
		 System.out.println(Utils.doubleFormat("00.00", avg*100)+"$\\pm$"+Utils.doubleFormat("0.00", std*100));
			avg = mean.evaluate(ans42);
			std = sd.evaluate(ans42);
			
		 System.out.println(Utils.doubleFormat("00.00", avg*100)+"$\\pm$"+Utils.doubleFormat("0.00", std*100));
			avg = mean.evaluate(ans43);
			std = sd.evaluate(ans43);
			
		 System.out.println(Utils.doubleFormat("00.00", avg*100)+"$\\pm$"+Utils.doubleFormat("0.00", std*100));
		 
	}

}
