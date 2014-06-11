package Part34EX;

import java.io.FileReader;
import java.util.Vector;

import myUtils.xMath;

import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.ReplaceMissingValues;
import Part1.ClusterEnsemble;
import Part3.Daul_POS;
import Part3.Semi_mRMR;

public class EvaluatePart4ex {
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		// Replace missing values   //被均值代替
		String fn = "C:/Users/Eric/Desktop/2012秋冬/毕业设计/DataSet/newFS/Heart-statlog.arff";
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
		 
		
		
		 Daul_POS dp = new Daul_POS(data,"DualPOS",labels,1,0);

		 Vector<double[]> pro = dp.processValues;
		 for(int i=0;i<pro.size();++i){
			 String ind = Integer.toString((int)pro.elementAt(i)[0]) ;
			 String IncRate = xMath.doubleFormat("0.0000", pro.elementAt(i)[1]);
			 String Rel = xMath.doubleFormat("0.0000", pro.elementAt(i)[2]);
			 String Rud = xMath.doubleFormat("0.0000", pro.elementAt(i)[3]);
			 String isSel = pro.elementAt(i)[4]==1?"√":"x";
			 System.out.println(ind+"--"+IncRate+"--"+Rel+"--"+Rud+"--"+isSel);
		 }
	}

}
