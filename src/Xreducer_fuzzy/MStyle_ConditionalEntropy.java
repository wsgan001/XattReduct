package Xreducer_fuzzy;

import java.io.FileReader;
import java.util.Arrays;



import Xreducer_core.Utils;

import weka.core.Instances;

public class MStyle_ConditionalEntropy extends MeasureStyle{

	public MStyle_ConditionalEntropy(Instances data,SimilarityStyle sstyle, ImplicatorTnormStyle itstyle) {
		super(data, sstyle,itstyle);
		// TODO Auto-generated constructor stub
		this.algname = "FCEFS算法";
		this.m_selectAtt = getSelectedAtt();
		
	}
	public double getMeausureValue(boolean[] D, boolean[] B){
		double ans = 0.0;
		int U = this.m_data.numInstances();
		int N = this.m_data.numAttributes();
		int classind = this.m_data.classIndex();
		for(int i=0;i<U;++i){
			double[] sumV = new double[U];
			Arrays.fill(sumV, 1);
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
			double part2 = Utils.getArraysSum(sumV);
			for(int q=0;q<U;++q)
				sumV[q] = this.m_itstyle.getfuzzyTnromValue(sumV[q],this.m_data.instance(i).value(classind)==this.m_data.instance(q).value(classind)?1:0);
			double part1 = Utils.getArraysSum(sumV);
			
			//System.out.println(i+":"+part1+"/"+part2+"="+part1/part2);
			if(part1!=0)		
				ans = ans+Utils.log2(part1/part2);
		}
		ans = -ans/(double)U;
		if(ans<0.00001)
			return 0.0;
		else	return ans;	
	}
	public boolean[] getReduceAtt(){
		/*int N = this.m_data.numAttributes();
		boolean[] newB = new boolean[N];
		boolean[] tempB = new boolean[N];
		boolean[] D = Utils.Instances2DecBoolean(this.m_data);	
		
		long time_start = Utils.getCurrenttime();
		double R_A = this.getMeausureValue(D, Utils.Instances2FullBoolean(m_data));
		do{
			tempB = newB.clone();
			double last_gamma_newB = 0.0;
			for(int i=0;i<N-1;++i){ //去掉决策属性
				if(!newB[i]){
					newB[i]=true;
					double gamma_newB = this.getMeausureValue(D, newB);
					double gamma_tempB = this.getMeausureValue(D, tempB);
					if(Utils.isAllFalse(tempB)||gamma_newB>gamma_tempB){
						last_gamma_newB = gamma_newB;
						tempB = newB.clone();
					}
					newB[i]=false;
				}
			}
			newB = tempB.clone();
			this.m_process += Arrays.toString(Utils.boolean2select(newB))+"=>"+last_gamma_newB+"\n";
			//System.out.println(this.m_process);
		}while(this.getMeausureValue(D,newB)<R_A);
		this.m_useTime = (Utils.getCurrenttime() - time_start)/(double)1000;
		this.m_numRed = Utils.booleanSelectedNum(newB);
		return newB;*/
		long time_start = Utils.getCurrenttime();
		boolean[] A = Utils.Instances2FullBoolean(this.m_data);//A为条件属性全集
		boolean[] D = Utils.Instances2DecBoolean(this.m_data);	
		int N = this.m_data.numAttributes();
		boolean[] newB = new boolean[N];
		double E_A = getMeausureValue(D,A);
		/*boolean[] Btemp = new boolean[N];
		double[] restemp = new double[N-1];
		int[] rankIndex = new int[N-1]; //不包括决策属性
		for(int i=0;i<N-1;++i){ //不包括决策属性
				Btemp[i]=true;
				restemp[i]=getMeausureValue(D,Btemp);
				Btemp[i]=false;
				rankIndex[i]=i;
		}
		

		//冒泡由小到大排序restemp，索引放入rankIndex
		for(int i=0;i<N-1;++i){ 
			double temp;
			int tempindex;
			for(int j=0;j< N-i-2;++j){
				if(restemp[j]>restemp[j+1]) {
					//交换restemp
					temp = restemp[j];
					restemp[j] = restemp[j + 1];
					restemp[j + 1] = temp;
					//交换entropyRankindex
					tempindex = rankIndex[j];
					rankIndex[j] = rankIndex[j + 1];
					rankIndex[j + 1] = tempindex;
				}
			}
		}

		//计算不在B(B[i]=false)中单个属性的e,选出最重要的单个属性minH_index 条件熵最小值  正域最大值
		newB[rankIndex[0]]=true;
		this.m_process += Arrays.toString(Utils.boolean2select(newB))+"=>"+restemp[0]+"\n";
		System.out.println(Arrays.toString(Utils.boolean2select(newB))+"=>"+restemp[0]);
		for (int i=0; i<N-1; ++i){ 
			double last_value = getMeausureValue(D,newB);
			int E_index = -1;
			double maxD_value = -1.0;
			double newB_temp = 0.0;
			double newB_value = 0.0;
			double temp = 0.0;
			if(Math.abs(last_value-E_A)<=0){ //newB达到结束条件
				break;
			}
			else{//否则找一个差值最大的				
				for (int k=0;k<N-1;++k){
					if (!newB[k]) {
						newB[k]=true;  //将第K个暂时加入约简中
						newB_temp = getMeausureValue(D,newB);
						temp = Math.abs(last_value - newB_temp);
						if(Math.abs(temp-maxD_value)>0.00000001 && temp > maxD_value){ 
							//可能出现由于熵计算顺序不同产生的误差
							newB_value = newB_temp;
							maxD_value = temp;
							E_index = k;
							
						}
						newB[k]=false;
					}
				}
				newB[E_index]=true;
				
			}
			this.m_process += Arrays.toString(Utils.boolean2select(newB))+"=>"+newB_value+"\n";
			System.out.println(Arrays.toString(Utils.boolean2select(newB))+"=>"+getMeausureValue(D,newB));
		}*/
		double nowValue = 10000;
		double temp = 0.0;
		int minIndex = -1;
		do{				
			minIndex = -1;
			//选一个最小的放进newB中
			for(int i=0;i<N-1;++i){
				if(!newB[i]){
					newB[i]=true;
					temp = getMeausureValue(D,newB);
					if(nowValue - temp > 0){
						nowValue = temp;
						minIndex = i;
					}
					newB[i]=false;
				}
			}
			if(minIndex == -1)
				break;
			newB[minIndex] = true;
			this.m_process += Arrays.toString(Utils.boolean2select(newB))+"=>"+nowValue+"\n";
			//System.out.println(Arrays.toString(Utils.boolean2select(newB))+"=>" + nowValue+"=>" +(minIndex+1));
		}
		while(true);
		
		this.m_useTime = (Utils.getCurrenttime() - time_start)/(double)1000;
		this.m_numRed = Utils.booleanSelectedNum(newB);
		//System.out.println(this.algname+"Success!");
		return newB;
	}

	public String getInformation(){
		String str = "FCEFS算法->所用时间:" + Utils.doubleFormat("0.0000", this.m_useTime)+"s  约简个数："+this.m_numRed+"\n"+this.m_process;
		str += "最终约简:"+Arrays.toString(this.m_selectAtt);
		System.out.println(str);
		return str;
		}
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		//String fn = "C:/Users/Eric/Desktop/2011秋冬/Code/Xreducer/data/Data/audiology.arff";
		//String fn = "C:/Users/Eric/Desktop/2011秋冬/Code/Xreducer/data/Data/cleveland.arff";
		//String fn = "C:/Users/Eric/Desktop/2011秋冬/Code/Xreducer/data/Data/colic.arff";
		//String fn = "C:/Users/Eric/Desktop/2011秋冬/Code/Xreducer/data/Data/credit.arff";
		//String fn = "C:/Users/Eric/Desktop/2011秋冬/Code/Xreducer/data/Data/dermatology.arff";
		//String fn = "C:/Users/Eric/Desktop/2011秋冬/Code/Xreducer/data/Data/hepatitis.arff";
		//String fn = "C:/Users/Eric/Desktop/2011秋冬/Code/Xreducer/data/Data/labor.arff";
		String fn = "C:/Users/Eric/Desktop/2011秋冬/Code/Xreducer/data/Data/soybean.arff";
		
		//String fn = "C:/Users/Eric/Desktop/2011秋冬/Code/Xreducer/data/Data/wine.arff";
		//String fn = "C:/Users/Eric/Desktop/2011秋冬/Code/Xreducer/data/Data/wdbc.arff";
		Instances m_data = new Instances(new FileReader(fn));
		m_data.setClassIndex(m_data.numAttributes()-1); 
		
		//SimilarityStyle sstyle = new SStyle_Abs1lambda_VmaxVmin(4);
		SimilarityStyle sstyle = new SStyle_MaxMin();
		ImplicatorTnormStyle itstyle = new ITStyle_KleeneDienes();
		
		MStyle_ConditionalEntropy mg = new MStyle_ConditionalEntropy(m_data,sstyle, itstyle);
		//mg.getInformation();
		//System.out.println(Arrays.toString(mg.getSelectedAtt()));
		String str1 = Utils.doubleFormat("0.0000", mg.m_useTime)+"s & ";
		String str2 = (mg.m_selectAtt.length-1)+" & ";
		int[] ans = mg.m_selectAtt.clone();
		String str = "";
		for(int i=0;i<ans.length-1;++i){
			str += (ans[i]+1)+",";
		}
		String str3 = str.substring(0, str.length()-1)+" \\\\"; 
		System.out.println(str1+str2+str3);
	}
}
