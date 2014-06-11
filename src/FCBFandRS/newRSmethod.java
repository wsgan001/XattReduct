package FCBFandRS;

import java.util.Arrays;

import Xreducer_core.Utils;
import Xreducer_struct.oneAlgorithm.xStyle;
import weka.core.Instances;
import weka.filters.Filter;
import CheckMonotony.Entropy;
import CheckMonotony.Entropy.newEntropy;


public class newRSmethod extends FSmethod {
	public newEntropy measure_type;  
	public int bin = -1;
	
	public newRSmethod(Instances data, int bin,  newEntropy x){
		super(data);
		this.measure_type = x;
		this.bin = bin;
		String lg = "";
		switch(x){
		case newPartition:
		case elementPartition:
		{
			
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
			break;
		}
		default:break;
		}
		
		
		
		
		switch(x){
		case newCovering:{
			this.algname =  "newCovering"+lg+"算法";
			break;
		}
		case newPartition:{
			this.algname =  "newPartition"+lg+"算法";
			break;
		}
		case newFuzzy:{
			this.algname =  "newFuzzy"+lg+"算法";
			break;
		}
		case elementCovering:{
			this.algname =  "elementCovering"+lg+"算法";
			break;
		}
		case elementPartition:{
			this.algname =  "elementPartition"+lg+"算法";
			break;
		}
		case elementFuzzy:{
			this.algname =  "elementFuzzy"+lg+"算法";
			break;
		}
		default:break;
		}
		
		
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
		boolean[] A = Utils.Instances2FullBoolean(this.m_data);
		double ha = 0;
		try {
			ha = Entropy.getMeasure(this.m_data,this.measure_type,D,A);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		double rbest = 1000000;
 
		do{
			tempB = newB.clone();
			rbest = 1000000;
			for(int i=0;i<NumAttr-1;++i){ //去掉决策属性
				if(!newB[i]){
					newB[i]=true;
					double rRandx = 0;
					try {
						rRandx = Entropy.getMeasure(this.m_data,this.measure_type,D,newB);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					if(Utils.isAllFalse(tempB)||rRandx<=rbest){
						tempB = newB.clone();
						rbest = rRandx;
					}
					newB[i]=false;
				}
			}
			newB = tempB.clone();
			this.m_process+=Arrays.toString(Utils.boolean2select(newB))+"=>"+(rbest>=0?rbest:-rbest)+"\n";
			//System.out.println(Arrays.toString(Utils.boolean2select(newB))+"=>"+(rbest>=0?rbest:-rbest));
		}
		//while(rbest-ha>0.0000001);	
		while(rbest!=ha);
		
		this.m_useTime = (Utils.getCurrenttime() - time_start)/(double)1000;
		this.m_numRed = Utils.booleanSelectedNum(newB);
		return newB;
		 
	}

}
