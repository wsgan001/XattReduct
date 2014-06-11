package FCBFandRS;

import java.util.Arrays;

import weka.core.Instances;
import weka.filters.Filter;
import CheckMonotony.Entropy;
import CheckMonotony.Entropy.newEntropy;
import Xreducer_core.Utils;

public class coreRSmethod extends FSmethod {
	public newEntropy measure_type;  
	public int bin = -1;
	
	public coreRSmethod(Instances data, int bin,  newEntropy x){
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
			this.algname =  "newCoveringCore"+lg+"算法";
			break;
		}
		case newPartition:{
			this.algname =  "newPartitionCore"+lg+"算法";
			break;
		}
		case newFuzzy:{
			this.algname =  "newFuzzyCore"+lg+"算法";
			break;
		}
		case elementCovering:{
			this.algname =  "elementCoveringCore"+lg+"算法";
			break;
		}
		case elementPartition:{
			this.algname =  "elementPartitionCore"+lg+"算法";
			break;
		}
		case elementFuzzy:{
			this.algname =  "elementFuzzyCore"+lg+"算法";
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
		
		for(int i=0;i<NumAttr-1;++i){
			if(this.Sig(booleanSubtract(A,i), i)>0){
				newB[i]=true;
			}
		}
		
		
		
		
		double rbest = -1;
 
		try {
			do{
				tempB = newB.clone();
				rbest = -1;
				for(int i=0;i<NumAttr-1;++i){ //去掉决策属性
					if(!newB[i]){
						double currentV = this.Sig(newB,i);				 
						if(Utils.isAllFalse(tempB)||currentV>=rbest){
							newB[i]=true;
							tempB = newB.clone();
							rbest = currentV;
							newB[i]=false;
						}
						 
					}
				}
				newB = tempB.clone();
				this.m_process+=Arrays.toString(Utils.boolean2select(newB))+"=>"+(rbest>=0?rbest:-rbest)+"\n";
				System.out.println(Arrays.toString(Utils.boolean2select(newB))+"=>"+(rbest>=0?rbest:-rbest));
			}
			//while(rbest-ha>0.0000001);	
			while(Entropy.getMeasure(this.m_data,this.measure_type,D,newB)!=ha);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		this.m_useTime = (Utils.getCurrenttime() - time_start)/(double)1000;
		this.m_numRed = Utils.booleanSelectedNum(newB);
		return newB;
		 
	}
	public double Sig(boolean[] red, int a_ind){
		boolean[] D = Utils.Instances2DecBoolean(this.m_data);
		double part1=0;
		double part2=0;
		boolean[] newRed = red.clone();
		newRed[a_ind] = true;
		try {
			part1 = Entropy.getMeasure(this.m_data,this.measure_type,D,red);
			part2 = Entropy.getMeasure(this.m_data,this.measure_type,D,newRed);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return part1-part2;
	}
	
	public static boolean[] booleanSubtract(boolean[] A, int a_ind){
		boolean[] newA = A.clone();
		newA[a_ind]=false;
		return newA;
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
