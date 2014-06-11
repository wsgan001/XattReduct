package FCBFandRS;

import java.io.FileReader;
import java.util.Arrays;
import java.util.Random;
import java.util.Vector;

import weka.attributeSelection.AttributeSelection;
import weka.attributeSelection.Ranker;
import weka.attributeSelection.ReliefFAttributeEval;
import weka.classifiers.Evaluation;
import weka.classifiers.functions.LibSVM;
import weka.classifiers.trees.SimpleCart;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Remove;
import weka.filters.unsupervised.attribute.ReplaceMissingValues;
import Xreducer_core.Utils;
import Xreducer_struct.oneAlgorithm.xStyle;

public class RunData4 {
	public static void main(String[] args) throws Exception {
		String path = "C:/Users/Eric/Desktop/2011秋冬/Code/Xreducer/data/Data/";
	 
		int bin = -1; // -1->MDL n->n个断点
		Vector<String> datas = new Vector<String>();
		
		//datas.add("labor");
		//datas.add("ionosphere");
		//datas.add("wine");
		//datas.add("vote");
		datas.add("wdbc");
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
			
			mRMRmethod mgx = new mRMRmethod(m_data,-1 ,xStyle.ClassDependet,0,0,1,0.2);
			int [] xx = mgx.getAttrList();
			mRMRmethod mgx2 = new mRMRmethod(m_data,-1 ,xStyle.ClassDependet,1,1,0,0);
			int [] xx2 = mgx2.getAttrList();
				
			FCBFmethod mg3 = new FCBFmethod(m_data,bin,0);
			int [] xx3 = mg3.getAttrList();
			RSmethod mg4 = new RSmethod(m_data,bin, xStyle.ClassDependet);
			int [] xx4 = mg4.getAttrList();
			RSmethod mg5 = new RSmethod(m_data,bin, xStyle.positive_RSAR);
			int [] xx5 = mg5.getAttrList();
			
			AttributeSelection  AS = new AttributeSelection();
			AS.setEvaluator(new ReliefFAttributeEval());  
			AS.setSearch(new Ranker()); 
			AS.SelectAttributes(m_data);
			int [] xx6 = AS.selectedAttributes();

			
			double[]  res1 = evaluateAC(m_data,xx); //mdmr
			double[][] res = new double[6][res1.length];
			res[0]=res1;
			res[1]= evaluateAC(m_data,xx2); //mrmr
			res[2]= evaluateAC(m_data,xx3);  //fcbf
			res[3]= evaluateAC(m_data,xx4);  //rsIM
			res[4]= evaluateAC(m_data,xx5);  //rsPOS
			res[5]= evaluateAC(m_data,xx6);  //relieff
			showRes(datas.get(i),res);
		}
	}
	
	
	public static void showRes(String name,double[][] res){
		System.out.println();
		int n=res[0].length;
		System.out.println("descriptor  "+name+"_x");
		for(int i=0;i<n;++i){
			System.out.println(i+1);
		}
		System.out.println("descriptor  "+name+"_mdmr");
		for(int i=0;i<n;++i){
			System.out.println(res[0][i]);
		}
		System.out.println("descriptor  "+name+"_mrmr");
		for(int i=0;i<n;++i){
			System.out.println(res[1][i]);
		}
		System.out.println("descriptor  "+name+"_fcbf");
		for(int i=0;i<n;++i){
			System.out.println(res[2][i]);
		}
		System.out.println("descriptor  "+name+"_rsIM");
		for(int i=0;i<n;++i){
			System.out.println(res[3][i]);
		}
		System.out.println("descriptor  "+name+"_rsPOS");
		for(int i=0;i<n;++i){
			System.out.println(res[4][i]);
		}
		System.out.println("descriptor  "+name+"_relieff");
		for(int i=0;i<n;++i){
			System.out.println(res[5][i]);
		}
	}
	public static double getAVG(double[] ans){
		double sum = 0;
		for(int i=0;i<ans.length;++i){
			sum+=ans[i];
		}
		return sum/(double)ans.length;
	}
	public static double[] evaluateAC(Instances data,int[] seletatt) throws Exception{
	
		int N = seletatt.length;
		double[] ans = new double[N-1];
		for(int i=0;i<N-1;++i){
			int tmp = i+2;
			int[] tmpselet = new int[tmp];
			for(int k=0;k<tmp-1;++k){
				tmpselet[k]=seletatt[k];
			}
			tmpselet[tmpselet.length-1] = N-1;

			
			int [] reAttr = Utils.seletatt2removeAtt(tmpselet);
			Remove m_removeFilterx = new Remove();
			m_removeFilterx.setAttributeIndicesArray(reAttr);
			m_removeFilterx.setInvertSelection(false);
	    	m_removeFilterx.setInputFormat(data);   
	    	Instances newDatax = Filter.useFilter(data, m_removeFilterx);
	    	newDatax.setClassIndex( newDatax.numAttributes() - 1 ); //重新设置决策属性索引
	    	Evaluation eval = new Evaluation(newDatax);
		    eval.setPriors(newDatax);
		    int runtime = 10;
		    //SimpleCart cl = new SimpleCart();
		    LibSVM cl = new LibSVM();
		    
		    double[] ansx = Utils.multicrossValidateModel(cl, newDatax, runtime, 10 );
	    	 
	    	ans[i] = getAVG(ansx);
			
			
			
			
		}
		return ans;
	}
}