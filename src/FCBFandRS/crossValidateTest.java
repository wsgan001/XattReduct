package FCBFandRS;

import java.io.FileReader;
import java.util.Arrays;
import java.util.Random;

import org.apache.commons.math.stat.descriptive.moment.Mean;
import org.apache.commons.math.stat.descriptive.moment.StandardDeviation;

import Xreducer_core.Utils;

import weka.classifiers.Evaluation;
import weka.classifiers.trees.J48;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Remove;

public class crossValidateTest {

	public static void main(String[] args) throws Exception {
		String fn = "C:/Users/Eric/Desktop/2011秋冬/Code/Xreducer/data/Data/colic.arff";
		 
		Instances m_data = new Instances(new FileReader(fn));
		m_data.setClassIndex(m_data.numAttributes()-1); 
		
		
		 
		boolean[] red = Utils.Instances2DecBoolean(m_data);
		int[] B = Utils.boolean2select_wDec(red);
		//System.out.println(Arrays.toString(B));
		
		int [] reAttr = Utils.seletatt2removeAtt(B);
		//System.out.println(Arrays.toString(reAttr));
		
		Remove m_removeFilter = new Remove();
		m_removeFilter.setAttributeIndicesArray(reAttr);
		m_removeFilter.setInvertSelection(false);
    	m_removeFilter.setInputFormat(m_data);   
    	Instances newData = Filter.useFilter(m_data, m_removeFilter);
    	newData.setClassIndex( newData.numAttributes() - 1 ); //重新设置决策属性索引
    	
    	
    	Evaluation eval = new Evaluation(m_data);
	    eval.setPriors(m_data);
    	eval.crossValidateModel(new J48(), m_data, 10, new Random(1));
    	
    	System.out.println(eval.pctCorrect());
    	System.out.println(1-eval.errorRate());
    	System.out.println("AUC = "+ eval.areaUnderROC(0));
    	
    	
		 
		
    	//double[] ans = Utils.multicrossValidateModel(new J48(), newData, 10, 10 );
    	
    	
    	
    	
    	
    	
    	//System.out.println(Arrays.toString(ans));
    	
	}
}
