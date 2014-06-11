package FCBFandRS;

 
import java.io.FileReader;
import java.util.Arrays;

import Xreducer_core.Utils;
import Xreducer_struct.oneAlgorithm.xStyle;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.ReplaceMissingValues;

public class RSmethod extends FSmethod {
	public xStyle rough_type; //0为条件熵 1为正域
	public int bin = -1;
	
	public RSmethod(Instances data, int bin,  xStyle rough_type) throws Exception {
		super(data);
		// TODO Auto-generated constructor stub
		this.rough_type = rough_type;
		this.bin = bin;
		String lg = "";
		if(bin==-1){
			lg = "-MDL";
		}
		else{
			lg = "-"+bin+"Bin";
		}
	 
		switch(rough_type){
		case conditionentropy:
		case positive_RSAR:
		{
		
		//离散化
		if(this.bin == -1){
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
			unsd.setBins(this.bin);
			//unsd.setUseEqualFrequency(true); // If set to true, equal-frequency binning will be used instead of equal-width binning.
			try {
				unsd.setInputFormat(this.m_data);
				this.m_data = Filter.useFilter(this.m_data , unsd);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		break;
		}
		default:break;
		}

		switch(this.rough_type){
		case conditionentropy:{
			this.algname =  "RSentropy"+lg+"算法";
		break;}
		case positive_RSAR:{
			this.algname = "RSpositive"+lg+"算法";
			break;}
		case fuzzyPositive_Boundary:{
			this.algname = "B-FRFS"+lg+"算法";
			break;}
		case fuzzyPositive_Low:{
			this.algname = "L-FRFS"+lg+"算法";
			break;}
		default:break;
		}
		
		
		
		this.m_selectAtt = getSelectedAtt();
		
	}
	public int[] getAttrList(){
		
		int NumAttr = this.m_data.numAttributes();
		int[] ans = new int[NumAttr];
		double rbest = -1000000;
		boolean[] newB = new boolean[NumAttr];
		boolean[] tempB = new boolean[NumAttr]; 
		boolean[] D = Utils.Instances2DecBoolean(this.m_data);
		int cnt = 0;
		do{
			tempB = newB.clone();
			rbest =-1;
			int ind = -1;
			for(int i=0;i<NumAttr-1;++i){ //去掉决策属性
				if(!newB[i]){
					newB[i]=true;
					double rRandx = Utils.getEvaluateValue(this.rough_type,this.m_data,D,newB);
					if(Utils.isAllFalse(tempB)||rRandx>=rbest){
						tempB = newB.clone();
						rbest = rRandx;
						ind = i;
					}
					newB[i]=false;
				}
			}
			newB = tempB.clone();
			ans[cnt++] = ind;
		}
		while(!Utils.isAllTrue(newB));	
		ans[ans.length-1] = ans.length-1;
		return ans;
		
	}
	@Override
	public boolean[] getReduceAtt() {
		int NumAttr = this.m_data.numAttributes();
	 
		// TODO Auto-generated method stub
		long time_start = Utils.getCurrenttime();	
		
		boolean[] newB = new boolean[NumAttr];
		boolean[] tempB = new boolean[NumAttr];
		boolean[] D = Utils.Instances2DecBoolean(this.m_data);
		boolean[] A = Utils.Instances2FullBoolean(this.m_data);
		double ha = Utils.getEvaluateValue(this.rough_type,this.m_data,D,A);
		double rbest = -1000000;
		 
 
		do{
			tempB = newB.clone();
			rbest = -1000000; 
			for(int i=0;i<NumAttr-1;++i){ //去掉决策属性
				if(!newB[i]){
					newB[i]=true;
					double rRandx = Utils.getEvaluateValue(this.rough_type,this.m_data,D,newB);
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



	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		String fn = "C:/Users/Eric/Desktop/2011秋冬/Code/Xreducer/data/Data/vote.arff";
		//String fn = "C:/Users/Eric/Desktop/2012春/Modification.NO3/Data/current data/Balance.arff";
		Instances m_data = new Instances(new FileReader(fn));
		m_data.setClassIndex(m_data.numAttributes()-1); 
	 
		//System.out.println(m_data.numInstances());
		
		// Replace missing values   //被均值代替
//		ReplaceMissingValues m_ReplaceMissingValues = new ReplaceMissingValues();
//		m_ReplaceMissingValues.setInputFormat(m_data);
//		m_data = Filter.useFilter(m_data, m_ReplaceMissingValues);
		
		
		 
		 RSmethod mg = new RSmethod(m_data,-1, xStyle.positive_RSAR);
		//MStyle_FCBF mg = new MStyle_FCBF(m_data, true, -1, -1);
		 mg.getInformation();
	}

}
