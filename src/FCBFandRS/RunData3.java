package FCBFandRS;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Random;
import java.util.Vector;

import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.ReplaceMissingValues;
import Xreducer_struct.oneAlgorithm.xStyle;

public class RunData3 {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		String path = "C:/Users/Eric/Desktop/2011秋冬/Code/Xreducer/data/Data/";
		 
		int bin = -1; // -1->MDL n->n个断点
		Vector<String> datas = new Vector<String>();
		
		datas.add("labor");	 
		datas.add("wine");
		datas.add("vote");  
		datas.add("ionosphere");
		datas.add("waveform-5000");
		datas.add("sonar");
		datas.add("flag");
		datas.add("colic");
		datas.add("credit"); 
 
		String str = "";
		for(int i=0;i<datas.size();++i){
			//str += datas.get(i)+"\r\n";
			String fn = path+datas.get(i)+".arff";	
			Instances m_data = new Instances(new FileReader(fn));
			m_data.setClassIndex(m_data.numAttributes()-1); 
		 
			
			// Replace missing values   //被均值代替
			ReplaceMissingValues m_ReplaceMissingValues = new ReplaceMissingValues();
			m_ReplaceMissingValues.setInputFormat(m_data);
			m_data = Filter.useFilter(m_data, m_ReplaceMissingValues);
			
			int cnt = 3;
			double j=0;
			Vector<double[]> h = new Vector<double[]> (3);
 
			for(int k=0;k<cnt;k++)
			{
				
				mRMRmethod mg0 = new mRMRmethod(m_data,-1 ,xStyle.ClassDependet,0,0,1,j);
				double[] tmp = new double[mg0.sc.size()];
				for(int q=0;q<mg0.sc.size();++q){
					tmp[q]=mg0.sc.elementAt(q);
				}
				h.add(tmp);
		    
		    	
		    	j=j+0.5;
			}
			 
			int n=-1;
			for(int k=0;k<cnt;k++)
			{
				n=Math.max(h.elementAt(k).length,n);
			}
			
			str += "descriptor  "+datas.get(i)+"_x"+"\r\n";
			for(int k=0;k<n;k++)
			{
				str +=  (k+1)+"\r\n";
			}
			str += "descriptor  "+datas.get(i)+"_h0"+"\r\n";
			for(int k=0;k<h.elementAt(0).length;k++)
			{
				str +=  h.elementAt(0)[k]+"\r\n";
			}
			str += "descriptor  "+datas.get(i)+"_h1"+"\r\n";
			for(int k=0;k<h.elementAt(1).length;k++)
			{
				str +=  h.elementAt(1)[k]+"\r\n";
			}
			str += "descriptor  "+datas.get(i)+"_h2"+"\r\n";
			for(int k=0;k<h.elementAt(2).length;k++)
			{
				str +=  h.elementAt(2)[k]+"\r\n";
			}
			
			
			
			//System.out.println(Arrays.toString(mg0));
			 
		 
			
 
		}
		System.out.println(str);
	}

}
