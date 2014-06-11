package Plus;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Vector;

import Xreducer_core.Utils;
import Xreducer_struct.oneAlgorithm.xStyle;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.ReplaceMissingValues;
import CheckMonotony.Entropy;
import CheckMonotony.Entropy.newEntropy;
import FCBFandRS.FSmethod;
import FCBFandRS.RSmethod;
import Modification.Dependence;

public class Focus extends FSmethod{
	public int E_index;  
	public int bin = -1;
 
	public Focus(Instances data, int bin,  int x) {
		super(data);
		this.E_index = x;
		this.bin = bin;
		String lg = "";
		if(bin==-1){
			lg = "-MDL";
		}
		else{
			lg = "-"+bin+"Bin";
		}
	 
		this.algname =  E_index+lg+"-Focus算法";
		
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
	}

	/**
	 * @param args
	 */

	@Override
	public boolean[] getReduceAtt() {
		int NumAttr = this.m_data.numAttributes();
		 
		// TODO Auto-generated method stub
		long time_start = Utils.getCurrenttime();	
		
		boolean[] newB = new boolean[NumAttr];
	 
		
		boolean[] A = Utils.Instances2FullBoolean(this.m_data);
		double ha = 0;
		ha =  Dependence.getEvalution(this.m_data, A, E_index);
		 
		
		 
		Vector<boolean[]> res = new Vector<boolean[]> ();
		for(int i=0;i<NumAttr-1;++i) //去掉决策属性
		//for(int i=5;i<6;++i) //去掉决策属性
		{
			int[] tmp = new int[i];
			subselect(0,1,tmp,i,ha,res);
			//System.out.println(i);
			if(!res.isEmpty())
				break;	
		}
		newB=res.elementAt(0).clone();
		this.m_useTime = (Utils.getCurrenttime() - time_start)/(double)1000;
		this.m_numRed = Utils.booleanSelectedNum(newB);
		return newB;
	}
	
	public void subselect(  int head, int index, int[] tmp, int k, double ha,Vector<boolean[]> res) { 
		 int N = this.m_data.numAttributes()-1;
		 if(res.isEmpty()){
			   for (int i=head; i<N+index-k; i++) 
			   { 
				    if(index<k)
				    { 
				    	tmp[index-1]=i; 
					    subselect(i+1, index+1, tmp, k,ha,res); 
				    } 
				    if(index==k) 
				    { 
				    	tmp[index-1]=i; 
				    	boolean[] tmpB =  intArrays2boolArrays(N+1,tmp);
				    	//System.out.println(Arrays.toString(tmpB));
							double htmp = Dependence.getEvalution(this.m_data, tmpB, E_index);
							if(htmp>=ha)
							{
								res.add(tmpB);
							}
					    //res.add(tmp);//这里加clone 否则add(tmp) 只是添加了引用
				    }			   
			  }
		  }		 
	}
	public static boolean[] intArrays2boolArrays(int N, int [] A){
		boolean[] Res = new boolean[N];
		for(int i=0;i<A.length;++i){
			Res[A[i]]=true;
		}
		return Res;
	}
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		String fn = "C:/Users/Eric/Desktop/2011秋冬/Code/Xreducer/data/Data/vote.arff";
		//String fn = "C:/Users/Eric/Desktop/2012春/Modification.NO3/Data/current data/Balance.arff";
		Instances m_data = new Instances(new FileReader(fn));
		m_data.setClassIndex(m_data.numAttributes()-1); 
	 
		//System.out.println(m_data.numInstances());
		 
		
		 
		Focus mg = new Focus(m_data,-1, 1);
		 
		 mg.getInformation();
	}
}
