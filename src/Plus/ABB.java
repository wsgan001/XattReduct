package Plus;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import Xreducer_core.Utils;
import weka.core.Instances;
import weka.filters.Filter;
import FCBFandRS.FSmethod;
import Modification.Dependence;

public class ABB extends FSmethod {
	public int E_index;  
	public int bin = -1;
	public static boolean[] bestRed = null;
	public ABB(Instances data, int bin,  int x) {
		super(data);
		this.E_index = x;
		this.bin = bin;
		bestRed = Utils.Instances2FullBoolean(this.m_data);
		String lg = "";
		if(bin==-1){
			lg = "-MDL";
		}
		else{
			lg = "-"+bin+"Bin";
		}
	 
		this.algname =  E_index+lg+"-ABBÀ„∑®";
		
		//¿Î…¢ªØ
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

	@Override
	public boolean[] getReduceAtt() {
 
		// TODO Auto-generated method stub
		long time_start = Utils.getCurrenttime();	
		
		boolean[] newB = Utils.Instances2FullBoolean(this.m_data);
		
		
		boolean[] A = Utils.Instances2FullBoolean(this.m_data);
		double ha = 0;
		ha =  Dependence.getEvalution(this.m_data, A, E_index);

		subABB(newB,ha);
		
	 
		this.m_useTime = (Utils.getCurrenttime() - time_start)/(double)1000;
		this.m_numRed = Utils.booleanSelectedNum(bestRed);
		return bestRed;
	}
	public void subABB( boolean[] CurrentTmp, double ha) { 
		 if(Utils.isAllFalse(CurrentTmp))
		 	return;
		 
		 for(int i=0;i<CurrentTmp.length-1;++i){
			 boolean[] tmpB = CurrentTmp.clone();
			 if(CurrentTmp[i]){
				 tmpB[i]=false;
				 double htmp = Dependence.getEvalution(this.m_data, tmpB, E_index);
				 if(htmp>=ha)
					{
					 if(Utils.booleanSelectedNum(bestRed)>Utils.booleanSelectedNum(tmpB))
					 	{
						bestRed=tmpB.clone();
						}
					 subABB(tmpB,ha);
					 	
					}
			 }
			 
			 
		 }
		  
	}
	/**
	 * @param args
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	public static void main(String[] args) throws FileNotFoundException, IOException {
		// TODO Auto-generated method stub
		String fn = "C:/Users/Eric/Desktop/2011«Ô∂¨/Code/Xreducer/data/Data/vote.arff";
		//String fn = "C:/Users/Eric/Desktop/2012¥∫/Modification.NO3/Data/current data/Balance.arff";
		Instances m_data = new Instances(new FileReader(fn));
		m_data.setClassIndex(m_data.numAttributes()-1); 
	 
		//System.out.println(m_data.numInstances());
		 
		
		 
		ABB mg = new ABB(m_data,-1, 1);
		 
		 mg.getInformation();
	}

}
