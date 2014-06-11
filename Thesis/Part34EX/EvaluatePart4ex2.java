package Part34EX;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import myUtils.xFigure;

import Part3.Utils_entropy;
import PartAll.getDatas;

import featureselection.Utils;

import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.ReplaceMissingValues;

public class EvaluatePart4ex2 {

	public static void test(String fn,String name) throws Exception {
		// TODO Auto-generated method stub
		//String fn = "C:/Users/Eric/Desktop/2012秋冬/毕业设计/DataSet/newFS/Credit.arff";
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
		
		
		int M = data.numAttributes()-1;
		boolean[] D = Utils.Instances2DecBoolean(data);
		boolean[] B = new boolean[data.numAttributes()];
		double[][] line_IM = new double[2][M];
		double[][] line_SU = new double[2][M];
		double[][] line_Dpos = new double[2][M];
		for(int i=0;i<M;++i){
			B[i]=true;
			line_IM[0][i]=i;
			line_SU[0][i]=i;
			line_Dpos[0][i]=i;
			
			line_IM[1][i]=Utils_entropy.getCA(data, B, D);
			line_SU[1][i]=Utils_entropy.getSU(data, B, D);
			line_Dpos[1][i]=(Utils_entropy.getGranulation(data, B, D)+Utils_entropy.getGranulation(data, D, B))/(double)2;
		}
		Map<String,double[][]> xydata = new HashMap<String,double[][]> ();
		xydata.put("IM", line_IM);
		xydata.put("SU", line_SU);
		xydata.put("DaulPOS", line_Dpos);
		String[] titles = {name,"Measurement","|B|"};
		xFigure.showFigure(titles, xydata);
	}
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		 Vector<String[]> fns = getDatas.getFeatureSelectionDatas();
	
			for(int i=0;i<fns.size();++i){
				test(fns.elementAt(i)[0],fns.elementAt(i)[1]);
		
			}
	}

}
