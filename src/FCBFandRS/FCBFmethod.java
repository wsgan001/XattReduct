package FCBFandRS;

import java.io.FileReader;
import java.util.Arrays;

import Xreducer_core.Utils;
import Xreducer_core.Utils_entropy;
import Xreducer_fuzzy.MStyle_FCBF;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.ReplaceMissingValues;

public class FCBFmethod extends FSmethod {

 
	public double lambda = 0.0;
	public int bin = -1;
	
	
	public FCBFmethod(Instances data, int bin, double lambda) {
		super(data);
		this.lambda = lambda;
		this.bin = bin;
		String lg = "";
		if(bin==-1){
			lg = "-MDL";
		}
		else{
			lg = "-"+bin+"Bin";
		}
		this.algname =  "FCBF("+(this.lambda==-1.0?"log":(int)this.lambda)+")"+lg+"算法";
		
		// Replace missing values   //被均值代替
		ReplaceMissingValues m_ReplaceMissingValues = new ReplaceMissingValues();
		try {
			m_ReplaceMissingValues.setInputFormat(m_data);
			m_data = Filter.useFilter(m_data, m_ReplaceMissingValues);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
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
		
		
		
		this.m_selectAtt = getSelectedAtt();
		// TODO Auto-generated constructor stub
	}

	public double getMeausureValue(boolean[] D, boolean[] B){
		return Utils_entropy.getSU(this.m_data, D, B);
	}
	 
	public int[] getAttrList(){
		int N = this.m_data.numAttributes();
		boolean[] B = new boolean[N];
		boolean[] D = Utils.Instances2DecBoolean(this.m_data);
		int[] cfbf_index = new int[N-1]; //不包括决策属性
		double[] cfbf_value = new double[N-1];

		
		for(int k=0;k<N-1;++k){
			B[k]=true;
			cfbf_value[k]=this.getMeausureValue(D, B);
			cfbf_index[k]=k;
			B[k]=false;
		}

		//排序
		double temp;
		int tempindex;
		for(int i=0;i<N-1;++i){/* 冒泡法排序 */ 
			for(int j=0;j< N-i-2;++j){
				if(cfbf_value[j]<cfbf_value[j+1]) {
					//交换cfbf_value
					temp = cfbf_value[j];
					cfbf_value[j] = cfbf_value[j + 1];
					cfbf_value[j + 1] = temp;
					//交换cfbf_index
					tempindex = cfbf_index[j];
					cfbf_index[j] = cfbf_index[j + 1];
					cfbf_index[j + 1] = tempindex;
				}
			}
		}
		
		
		int[] ans = new int[N];
		for(int i=0;i<N-1;++i){
			ans[i] = cfbf_index[i];
		}
		ans[ans.length-1] = ans.length-1;
		return ans;
	}
	public boolean[] getReduceAtt(){
		int U = this.m_data.numInstances();
		int N = this.m_data.numAttributes();
		boolean[] newB = new boolean[N];
		boolean[] B = new boolean[N];
		boolean[] A = Utils.Instances2FullBoolean(this.m_data);
		boolean[] D = Utils.Instances2DecBoolean(this.m_data);
		int[] cfbf_index = new int[N-1]; //不包括决策属性
		double[] cfbf_value = new double[N-1];
		
		long time_start = Utils.getCurrenttime();		
		
		
		for(int k=0;k<N-1;++k){
			B[k]=true;
			cfbf_value[k]=this.getMeausureValue(D, B);
			cfbf_index[k]=k;
			B[k]=false;
		}

		//排序
		double temp;
		int tempindex;
		for(int i=0;i<N-1;++i){/* 冒泡法排序 */ 
			for(int j=0;j< N-i-2;++j){
				if(cfbf_value[j]<cfbf_value[j+1]) {
					//交换cfbf_value
					temp = cfbf_value[j];
					cfbf_value[j] = cfbf_value[j + 1];
					cfbf_value[j + 1] = temp;
					//交换cfbf_index
					tempindex = cfbf_index[j];
					cfbf_index[j] = cfbf_index[j + 1];
					cfbf_index[j + 1] = tempindex;
				}
			}
		}
		if(this.lambda==-1)
		{
			int log = (int) (N/Utils.log2(N));
			this.lambda = cfbf_value[log];
		}
		//System.out.println(Arrays.toString(cfbf_value));
		//int log = (int) (N/Utils.log2(N));
		//System.out.println(log);
		// for(int i=0;i<cfbf_value.length;i++){
			// if(cfbf_value[i]>=this.lambda)
			// System.out.println((cfbf_index[i]+1)+":"+cfbf_value[i]);
		 //}
		newB[cfbf_index[0]]=true;
	 
 
		for(int k =1;k!=(N-1) && cfbf_value[k]>=this.lambda;k++){
			boolean[] X = new boolean[N];
			X[cfbf_index[k]]=true;//newone
			boolean isRud = false;
			for(int i=0;i<k;++i){
				if(newB[cfbf_index[i]]){
					boolean[] Y = new boolean[N];
					Y[cfbf_index[i]]=true;//oldone			 
					//if(this.getMeausureValue(X, Y)>=this.m_lambda*cfbf_value[cnt]){//不相关	
					if(this.getMeausureValue(X, Y)>=cfbf_value[k]){//不相关
						isRud = true;
						break;
					}		
				}
			}
			if(!isRud){
				newB[cfbf_index[k]]=true;
				//System.out.println(Arrays.toString(Utils.boolean2select(newB))+"->"+cfbf_index[cfbf_index[k]]+":"+cfbf_value[cfbf_index[k]]);
			}
		}
		 
		
		this.m_useTime = (Utils.getCurrenttime() - time_start)/(double)1000;
		this.m_numRed = Utils.booleanSelectedNum(newB);
		//System.out.println(this.algname+"Success!");
		return newB;
	}
	
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		String fn = "C:/Users/Eric/Desktop/2011秋冬/Code/Xreducer/data/Data/ionosphere.arff";
		Instances m_data = new Instances(new FileReader(fn));
		m_data.setClassIndex(m_data.numAttributes()-1); 
	 
		
		// Replace missing values   //被均值代替
		ReplaceMissingValues m_ReplaceMissingValues = new ReplaceMissingValues();
		m_ReplaceMissingValues.setInputFormat(m_data);
		m_data = Filter.useFilter(m_data, m_ReplaceMissingValues);
		
		
		 
		FCBFmethod mg = new FCBFmethod(m_data, -1, -1);
		//MStyle_FCBF mg = new MStyle_FCBF(m_data, true, -1, -1);
		mg.getInformation();
	}

}
