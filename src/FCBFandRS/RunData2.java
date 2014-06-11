package FCBFandRS;

import java.io.FileReader;
import java.util.Arrays;
import java.util.Random;
import java.util.Vector;

import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.functions.LibSVM;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Remove;
import weka.filters.unsupervised.attribute.ReplaceMissingValues;

import ResultsOperator.Vector2LaTex;
import Xreducer_core.Utils;
import Xreducer_struct.oneAlgorithm.xStyle;

public class RunData2 {

	public static void main(String[] args) throws Exception {
		String path = "C:/Users/Eric/Desktop/2011秋冬/Code/Xreducer/data/Data/";
	 
		int bin = -1; // -1->MDL n->n个断点
		Vector<String> datas = new Vector<String>();
		
		datas.add("labor");
		//datas.add("ionosphere");
		//datas.add("wine");
		//datas.add("vote");
		//datas.add("wdbc");
		//datas.add("soybean"); 
 
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
			
			int cnt = 11;
			int [] rednum  =new int [cnt];
			double [] ac = new double [cnt];
			Vector<int[]> selattr = new Vector<int[]>();
			
			
			
			int rand = 1000;
			int randh = -1;
			
			

			
			
		    double j=0;
			for(int k=0;k<cnt;k++)
			{
				
				mRMRmethod mgx = new mRMRmethod(m_data,-1 ,xStyle.ClassDependet,0,0,1,j);
				j=j+0.1;
				rednum[k] = mgx.getSelectedAtt().length;
				selattr.add(mgx.getSelectedAtt());
			}
			Random rnd = new Random(1);
			for(int h=0;h<rand;++h){
				System.out.println();
				System.out.println("xx:"+h);
				
				for(int k=0;k<cnt;++k){
				int [] reAttr = Utils.seletatt2removeAtt(selattr.get(k));
				Remove m_removeFilterx = new Remove();
				m_removeFilterx.setAttributeIndicesArray(reAttr);
				m_removeFilterx.setInvertSelection(false);
		    	m_removeFilterx.setInputFormat(m_data);   
		    	Instances newDatax = Filter.useFilter(m_data, m_removeFilterx);
		    	newDatax.setClassIndex( newDatax.numAttributes() - 1 ); //重新设置决策属性索引
		    	Evaluation eval = new Evaluation(m_data);
			    eval.setPriors(m_data);
	 
		    	eval.crossValidateModel(new LibSVM(), newDatax, 10, new Random(h));
		    	ac[k] = eval.pctCorrect()/100.00;
				}
				System.out.println();
				System.out.println(Arrays.toString(rednum));
				System.out.println(Arrays.toString(ac));
				
				
				
				if(rednum[0]!=rednum[1] && ac[0]<ac[1])
				{
					System.out.println();
					System.out.println("randh:"+h);
					break;
				}
				if(rednum[0]!=rednum[2] && ac[0]<ac[2])
				{
					System.out.println();
					System.out.println("randh:"+h);
					break;
				}
				if(rednum[0]!=rednum[3] && ac[0]<ac[3])
				{
					System.out.println();
					System.out.println("randh:"+h);
					break;
				}
			}
			
			
			
			
			
			
			 
			//System.out.println(Arrays.toString(mg0));
			str += "descriptor  "+datas.get(i)+"_num"+"\t"+datas.get(i)+"_ac"+"\r\n";
			for(int k=0;k<cnt;k++)
			{
				str += rednum[k]+"\t"+ac[k]+"\r\n";
			}
		 
			
 
		}
		System.out.println(str);
	 
	}
}
