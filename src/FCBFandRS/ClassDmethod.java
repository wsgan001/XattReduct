package FCBFandRS;

import java.util.Arrays;

import Xreducer_core.Utils;
import Xreducer_struct.oneAlgorithm.xStyle;
import weka.core.Instances;
import weka.filters.Filter;

public class ClassDmethod extends FSmethod {
	public int bin = -1;
	
	public ClassDmethod(Instances data , int bin) {
		super(data);
		// TODO Auto-generated constructor stub
		
		this.bin = bin;
		String lg = "";
		if(bin==-1){
			lg = "-MDL";
		}
		else{
			lg = "-"+bin+"Bin";
		}
	 
 
		
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
		
		this.algname = "CDFS"+lg+"算法";
		
		this.m_selectAtt = getSelectedAtt();
	}

	@Override
	public boolean[] getReduceAtt() {
		// TODO Auto-generated method stub
		int NumAttr = this.m_data.numAttributes();
		 
		// TODO Auto-generated method stub
		long time_start = Utils.getCurrenttime();	
		
		boolean[] newB = new boolean[NumAttr];
		boolean[] tempB = new boolean[NumAttr];
		boolean[] D = Utils.Instances2DecBoolean(this.m_data);

		double rbest = -1000000;
		double rprev = 0.0;
 
		do{
			tempB = newB.clone();
			rprev = rbest;
			for(int i=0;i<NumAttr-1;++i){ //去掉决策属性
				if(!newB[i]){
					newB[i]=true;
					double rRandx = Utils.getEvaluateValue(xStyle.CAIR,this.m_data,D,newB);
					if(Utils.isAllFalse(tempB)||rRandx>rbest){
						tempB = newB.clone();
						rbest = rRandx;
					}
					newB[i]=false;
				}
			}
			newB = tempB.clone();
			this.m_process+=Arrays.toString(Utils.boolean2select(newB))+"=>"+(rbest>0?rbest:-rbest)+"\n";
		}
		while(rbest!=rprev);	
		
		
		this.m_useTime = (Utils.getCurrenttime() - time_start)/(double)1000;
		this.m_numRed = Utils.booleanSelectedNum(newB);
		return newB;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
