package Part3;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import Part1.ClusterEnsemble;

import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.ReplaceMissingValues;

public class TestFS {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		// Replace missing values   //被均值代替
		String fn = "C:/Users/Eric/Desktop/2012秋冬/毕业设计/DataSet/newFS/Audiology.arff";
		//String fn = "C:/Users/Eric/Desktop/2012秋冬/毕业设计/DataSet/Voting.arff";
		Instances data = new Instances(new FileReader(fn));
		data.setClassIndex(data.numAttributes()-1);
		ReplaceMissingValues m_ReplaceMissingValues = new ReplaceMissingValues();
		try {
			m_ReplaceMissingValues.setInputFormat(data);
			data = Filter.useFilter(data, m_ReplaceMissingValues);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		weka.filters.supervised.attribute.Discretize sd = new weka.filters.supervised.attribute.Discretize();		 
		sd.setInputFormat(data);
		data = Filter.useFilter(data , sd);
		//wekaDiscretizeMethod wm = new wekaDiscretizeMethod();
		boolean[] labels = ClusterEnsemble.getRandomLabeled(0.5,data.numInstances(),0);
		Semi_mRMR sm = new Semi_mRMR(data,"SemiMRMR",labels,0.1);
		sm.getInformation();
		
		
		 Daul_POS dp = new Daul_POS(data,"DualPOS",labels,1,0);
		//Daul_POSEx dp = new Daul_POSEx(data,"DualPOS",labels,1);
		 dp.getInformation();
	}

}
