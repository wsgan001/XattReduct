package Xreducer_fuzzy;

import java.io.FileReader;
import java.util.Arrays;

import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.ReplaceMissingValues;
import Xreducer_core.Utils;

public class MStyle_FFCBF extends MeasureStyle{
	public double m_lambda = 0.0;
	
public MStyle_FFCBF(Instances data, SimilarityStyle sstyle,
			ImplicatorTnormStyle itstyle, double lambda) {
	super(data, sstyle, itstyle);
	// TODO Auto-generated constructor stub
	this.m_lambda = lambda;
	this.algname =  "F-FCBF("+(this.m_lambda==-1.0?"log":(int)this.m_lambda)+")算法";
	this.m_selectAtt = getSelectedAtt();
	
}
public double getMeausureValue(boolean[] D, boolean[] B){
	double fHp = 0.0;
	double fHd = 0.0;
	double fHdp = 0.0;
	int U = this.m_data.numInstances();
	int N = this.m_data.numAttributes();
	int classind = this.m_data.classIndex();
	//System.out.println(Arrays.toString(B));
	double tmp = 1.0;
	for(int i=0;i<U;++i){
		double[] sumV = new double[U];
		Arrays.fill(sumV, 1);
		double[] sumD = new double[U];
		Arrays.fill(sumD, 1);
		for(int k=0;k<N;++k){
			if(B[k]&&this.m_data.attribute(k).isNumeric()){
				double[] Vas = this.m_data.attributeToDoubleArray(k);			
				double[] vasStatistics = Utils.getStatisticsValue(Vas);
				for(int q=0;q<U;++q) {
					this.m_sstyle.SimilaritySetting(vasStatistics[1],vasStatistics[2],vasStatistics[5]);
					sumV[q] = this.m_itstyle.getfuzzyTnromValue(sumV[q],this.m_sstyle.getSimilarityValue(this.m_data.instance(i).value(k),this.m_data.instance(q).value(k)));
				}
			}
			else if(B[k]&&!this.m_data.attribute(k).isNumeric()){
				for(int q=0;q<U;++q)
					sumV[q] = this.m_itstyle.getfuzzyTnromValue(sumV[q],this.m_data.instance(i).value(k)==this.m_data.instance(q).value(k)?1:0);
			} 
		}
		/*String str = "";
		for(int q=0;q<U;++q)
			if(sumV[q]!=0 && q!=i)
			str += Utils.doubleFormat("0.000", sumV[q])+" " ;
			else if(q==i)
				str += "1.0 ";
			else 
				str += "0.0 ";
		System.out.println(str);*/
		for(int k=0;k<N;++k){
			if(D[k]&&this.m_data.attribute(k).isNumeric()){
				double[] Vas = this.m_data.attributeToDoubleArray(k);			
				double[] vasStatistics = Utils.getStatisticsValue(Vas);
				for(int q=0;q<U;++q) {
					this.m_sstyle.SimilaritySetting(vasStatistics[1],vasStatistics[2],vasStatistics[5]);
					sumD[q] = this.m_itstyle.getfuzzyTnromValue(sumD[q],this.m_sstyle.getSimilarityValue(this.m_data.instance(i).value(k),this.m_data.instance(q).value(k)));
				}

			}
			else if(D[k]&&!this.m_data.attribute(k).isNumeric()){
				for(int q=0;q<U;++q)
					sumD[q] = this.m_itstyle.getfuzzyTnromValue(sumD[q],this.m_data.instance(i).value(k)==this.m_data.instance(q).value(k)?1:0);
			} 
		}
		double partp = Utils.getArraysSum(sumV);
		//System.out.println("sumd:"+partp);
		for(int q=0;q<U;++q){
			//sumD[q] = this.m_data.instance(i).value(classind)==this.m_data.instance(q).value(classind)?1:0;
			sumV[q] = this.m_itstyle.getfuzzyTnromValue(sumV[q],sumD[q]);
			}
		double partpd = Utils.getArraysSum(sumV);
		double partd = Utils.getArraysSum(sumD);
		//System.out.println("sumVD:"+Utils.doubleFormat("0.0", partpd)+"/"+Utils.doubleFormat("0.0", partp));
		//System.out.println("sumD:"+partd);
		
			//tmp *= partp;
			fHd += Utils.log2(partd/(double)U);
			fHp += Utils.log2(partp/(double)U);
		if(partp!=0)
			fHdp += Utils.log2(partpd/partp);
	}
	 	
	double ans = 2.0*(fHd-fHdp)/(fHd+fHp);
	if(ans<0.000001)
		return 0.0;
	//System.out.println(ans);	
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
	if(this.m_lambda==-1)
	{
		int log = (int) (N/Utils.log2(N));
		this.m_lambda = cfbf_value[log];
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
 
	int cnt = 1;
	while(cnt!=(N-1) && cfbf_value[cnt]>=this.m_lambda){
		boolean[] X = new boolean[N];
		X[cfbf_index[cnt]]=true;//newone
		boolean isRud = false;
		for(int i=0;i<cnt;++i){
			if(newB[cfbf_index[i]]){
				boolean[] Y = new boolean[N];
				Y[cfbf_index[i]]=true;//oldone			 
				//if(this.getMeausureValue(X, Y)>=this.m_lambda*cfbf_value[cnt]){//不相关	
				if(this.getMeausureValue(X, Y)>=cfbf_value[cnt]){//不相关
					isRud = true;
					break;
				}
				
			}
		}
		if(!isRud){
			newB[cfbf_index[cnt]]=true;
			//System.out.println(Arrays.toString(Utils.boolean2select(newB))+"->"+cfbf_index[cnt]);
		}
		 
		cnt++;
	}
	 
	
	this.m_useTime = (Utils.getCurrenttime() - time_start)/(double)1000;
	this.m_numRed = Utils.booleanSelectedNum(newB);
	//System.out.println(this.algname+"Success!");
	return newB;
}
public String getInformation(){
	String str = this.algname+"->所用时间:" + Utils.doubleFormat("0.0000", this.m_useTime)+"s  约简个数："+this.m_numRed+"\n"+this.m_process;
	str += "最终约简:"+Arrays.toString(this.m_selectAtt);
	System.out.println(str);
	return str;
	}

public static void main(String[] args) throws Exception {
	String fn = "C:/Users/Eric/Desktop/2011秋冬/Code/Xreducer/data/wine.arff";
	Instances m_data = new Instances(new FileReader(fn));
	m_data.setClassIndex(m_data.numAttributes()-1); 
	SimilarityStyle sstyle = new SStyle_MaxMin();
	
	// Replace missing values   //被均值代替
	ReplaceMissingValues m_ReplaceMissingValues = new ReplaceMissingValues();
	m_ReplaceMissingValues.setInputFormat(m_data);
	m_data = Filter.useFilter(m_data, m_ReplaceMissingValues);
	
	
	ImplicatorTnormStyle itstyle = new ITStyle_Lukasiewicz(); 
	MStyle_FFCBF mg = new MStyle_FFCBF(m_data,sstyle, itstyle, 0);
	mg.getInformation();
}

}
