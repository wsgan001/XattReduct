package Plus;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Random;

import Xreducer_core.Utils;
import FCBFandRS.FSmethod;
import Modification.Dependence;
import weka.core.Instances;
import weka.filters.Filter;

public class xRandomSearch extends FSmethod{
	public int E_index;  
	public int bin = -1;
	public int MaxIT = -1;
	public boolean[] D = null;
	public xRandomSearch(Instances data, int bin,  int x ,int MaxIt) {
		super(data);
		this.E_index = x;
		this.bin = bin;
		this.MaxIT = MaxIt;
		String lg = "";
		if(bin==-1){
			lg = "-MDL";
		}
		else{
			lg = "-"+bin+"Bin";
		}
	 
		this.algname =  E_index+lg+"-RandomÀ„∑®";
		
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
	}
	public static void main(String[] args) throws FileNotFoundException, IOException {
		// TODO Auto-generated method stub
		String fn = "C:/Users/Eric/Desktop/2011«Ô∂¨/Code/Xreducer/data/Data/wine.arff";
		//String fn = "C:/Users/Eric/Desktop/2012¥∫/Modification.NO3/Data/current data/Balance.arff";
		Instances m_data = new Instances(new FileReader(fn));
		m_data.setClassIndex(m_data.numAttributes()-1); 
	 
		//System.out.println(m_data.numInstances());
		 
		
		 
		xRandomSearch mg = new xRandomSearch(m_data,-1, 1,10000);
		 
		 mg.getInformation();
	}
	@Override
	public boolean[] getReduceAtt() {
		// TODO Auto-generated method stub
		long time_start = Utils.getCurrenttime();	
		
		int cnt = this.MaxIT;
		Random rd = new Random(this.m_data.numAttributes());
		boolean[] A = Utils.Instances2FullBoolean(this.m_data);
		double ha =  Dependence.getEvalution(this.m_data, A, E_index);
		boolean[] red = A.clone();
		while(cnt!=0){
			boolean[] tmpB = getRandomAttr(this.m_data,rd);
			double tmpv = Dependence.getEvalution(this.m_data, tmpB, E_index);
			if(tmpv==ha &&  Utils.booleanSelectedNum(tmpB)<Utils.booleanSelectedNum(red))
			{
				red = tmpB.clone();
			}
			cnt--;
		}
		this.m_useTime = (Utils.getCurrenttime() - time_start)/(double)1000;
		this.m_numRed = Utils.booleanSelectedNum(red);
		return red;
	}
	private boolean[] getRandomAttr(Instances m_data,Random rd) {
		// TODO Auto-generated method stub
		int N = m_data.numAttributes();
		boolean[] ans = new boolean[N];
		
		for(int i=0;i<N-1;++i){
			ans[i] = rd.nextBoolean();
		}
		return ans;
	}

}
