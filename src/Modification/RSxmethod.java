package Modification;

import java.io.FileReader;
import java.util.Arrays;

import Plus.Focus;
import Xreducer_core.Utils;
import Xreducer_fuzzy.ITStyle_Lukasiewicz;
import Xreducer_fuzzy.ImplicatorTnormStyle;
import Xreducer_fuzzy.MStyle_FuzzySU;
import Xreducer_fuzzy.SStyle_MaxMin;
import Xreducer_fuzzy.SimilarityStyle;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.ReplaceMissingValues;
import FCBFandRS.FSmethod;

public class RSxmethod extends FSmethod{

	public int E_index = -1;
	public RSxmethod(Instances data, int bin, int E_index) {
		super(data);
		this.E_index = E_index;
		String lg = null;
		
		if(bin==-1){
			lg = "-MDL";
		}
		else{
			lg = "-"+bin+"Bin";
		}
	 
		this.algname =  E_index+lg+"算法";
		
		//离散化
		if(bin == -1){
			 weka.filters.supervised.attribute.Discretize sd = new weka.filters.supervised.attribute.Discretize();
			try {
				sd.setInputFormat(this.m_data);
				this.m_data = Filter.useFilter(this.m_data , sd);
			} catch (Exception e) {
					// TODO Auto-generated catch block
				e.printStackTrace();
			}		 
		}
		else{
			weka.filters.unsupervised.attribute.Discretize unsd = new weka.filters.unsupervised.attribute.Discretize();
			unsd.setBins(bin);
			//unsd.setUseEqualFrequency(true); // If set to true, equal-frequency binning will be used instead of equal-width binning.
			try {
				unsd.setInputFormat(this.m_data);
				this.m_data = Filter.useFilter(this.m_data , unsd);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	 
		this.m_selectAtt = getSelectedAtt();
		
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param args
	 */
 
	@Override
	public boolean[] getReduceAtt() {
		// TODO Auto-generated method stub
		int NumAttr = this.m_data.numAttributes();
		 
		// TODO Auto-generated method stub
		long time_start = Utils.getCurrenttime();	
		
		boolean[] newB = new boolean[NumAttr];
		boolean[] tempB = new boolean[NumAttr];
		boolean[] D = Utils.Instances2DecBoolean(this.m_data);
		boolean[] A = Utils.Instances2FullBoolean(this.m_data);
		//double ha = Utils.getEvaluateValue(this.rough_type,this.m_data,D,A);
		double ha = Dependence.getEvalution(this.m_data, A, E_index);
		double rbest = -1000000;
		 
 
		do{
			tempB = newB.clone();
			rbest = -1000000; 
			for(int i=0;i<NumAttr-1;++i){ //去掉决策属性
				if(!newB[i]){
					newB[i]=true;
					double rRandx =  Dependence.getEvalution(this.m_data,newB, E_index);
					if(Utils.isAllFalse(tempB)||rRandx>=rbest){
						tempB = newB.clone();
						rbest = rRandx;
					}
					newB[i]=false;
				}
			}
			newB = tempB.clone();
			this.m_process+=Arrays.toString(Utils.boolean2select(newB))+"=>"+(rbest>=0?rbest:-rbest)+"\n";
		}
		//while(Math.abs(rbest-ha)>0.0001);	
		while(rbest!=ha);	
		
		this.m_useTime = (Utils.getCurrenttime() - time_start)/(double)1000;
		this.m_numRed = Utils.booleanSelectedNum(newB);
		return newB;
	}
	public static void main(String[] args) throws Exception {
		String fn = "C:/Users/Eric/Desktop/2012春/Modification.No4-kbs/kbs_ex.arff";
		//String fn = "C:/Users/Eric/Desktop/2012春/Modification.NO3/Data/current data/Balance.arff";
		Instances m_data = new Instances(new FileReader(fn));
		m_data.setClassIndex(m_data.numAttributes()-1); 
	 
		//System.out.println(m_data.numInstances());
//		
//		// Replace missing values   //被均值代替
//		ReplaceMissingValues m_ReplaceMissingValues = new ReplaceMissingValues();
//		m_ReplaceMissingValues.setInputFormat(m_data);
//		m_data = Filter.useFilter(m_data, m_ReplaceMissingValues);
		
		
		 
		RSxmethod mg = new RSxmethod(m_data,-1, 0);
		//MStyle_FCBF mg = new MStyle_FCBF(m_data, true, -1, -1);
		 mg.getInformation();
	}
}
