package FCBFandRS;

import java.io.FileReader;
import java.util.Arrays;

import Xreducer_core.Utils;
import Xreducer_core.Utils_entropy;
import Xreducer_struct.oneAlgorithm.xStyle;

import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.ReplaceMissingValues;

public class FCBFandRSmethod extends FSmethod {

	public int bin = -1;
	
	public FCBFandRSmethod(Instances data, int bin) throws Exception {
		super(data);
		this.bin = bin;
		String lg = "";
		if(bin==-1){
			lg = "-MDL";
		}
		else{
			lg = "-"+bin+"Bin";
		}
		this.algname =  "FCBF&RS"+lg+"算法";
		
		
		
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
	@Override
	public boolean[] getReduceAtt() {
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
 
		newB[cfbf_index[0]]=true;
	 
		double H_A = Utils.getEvaluateValue(xStyle.conditionentropy,this.m_data, Utils.Instances2DecBoolean(this.m_data), Utils.Instances2FullBoolean(this.m_data));
		for(int k=0;k<N-1;++k){
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
				//判断条件熵是否相等
				double H_now = Utils.getEvaluateValue(xStyle.conditionentropy,this.m_data, Utils.Instances2DecBoolean(this.m_data), newB);
				this.m_process+=Arrays.toString(Utils.boolean2select(newB))+"=>"+(H_now>0?H_now:-H_now)+"\n";
				if(Math.abs(H_now-H_A)<0.0001){
					this.m_process+="xxxxxxx\n";
					break;
				}
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
		String fn = "C:/Users/Eric/Desktop/2011秋冬/Code/Xreducer/data/wine.arff";
		Instances m_data = new Instances(new FileReader(fn));
		m_data.setClassIndex(m_data.numAttributes()-1); 
	 
		
		// Replace missing values   //被均值代替
		ReplaceMissingValues m_ReplaceMissingValues = new ReplaceMissingValues();
		m_ReplaceMissingValues.setInputFormat(m_data);
		m_data = Filter.useFilter(m_data, m_ReplaceMissingValues);
		
		
		 
		//FCBFandRSmethod mg = new FCBFandRSmethod(m_data);
		//MStyle_FCBF mg = new MStyle_FCBF(m_data, true, -1, -1);
		//mg.getInformation();
	}

}
