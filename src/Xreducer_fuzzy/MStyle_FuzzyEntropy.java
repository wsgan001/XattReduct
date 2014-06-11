package Xreducer_fuzzy;

import java.io.FileReader;
import java.util.Arrays;

import org.apache.commons.math.stat.descriptive.summary.Sum;

import Xreducer_core.Utils;
import weka.core.Instances;

public class MStyle_FuzzyEntropy  extends MeasureStyle{

	public MStyle_FuzzyEntropy(Instances data, SimilarityStyle sstyle,
			ImplicatorTnormStyle itstyle) {
		super(data, sstyle, itstyle);
		// TODO Auto-generated constructor stub
		this.algname = "E-FRFS算法";
		this.m_selectAtt = getSelectedAtt();
		
	}
	public double getMeausureValue(boolean[] D, boolean[] B){
		double ans = 0.0;
		int U = this.m_data.numInstances();
		
 

		double[][] MRp = getFuzzySimilarityRelation(B);
		double[][] MRd = getFuzzySimilarityRelation(D);
		double[] Dj = new double [U];
		double cardinality = 0;
		for(int j=0;j<U;++j){
			Dj[j] = new Sum().evaluate(MRd[j]);
			cardinality += 1.0/Dj[j];
		}

		double Fall = 0;
		
		for(int i=0;i<U;++i){		
			double Fi = new Sum().evaluate(MRp[i]);
			double Hfi = 0.0;
			for(int j=0;j<U;++j){
				double FDij = Utils.MinMatricsSum(MRp,MRd,i,j);				 
				//System.out.println(i+":"+j+":"+(FDij));
				if(FDij!=0&&(Fi!=0&&Dj[j]!=0))		
					Hfi = Hfi-(Math.log(FDij/Fi) / Math.log((double)2))*(FDij/Fi)/Dj[j];
			}
			ans = ans + Fi*Hfi;
			Fall = Fall + Fi;
			
			//System.out.println(Arrays.toString(MRpd[i]));
			//System.out.println(part1+"/"+part2+"="+part1/part2);
			
		}
		ans /= Fall;
		  
		//return 1-ans/Utils.log2(cardinality);	
		return 1-ans/Utils.log2(cardinality);
	}
	public boolean[] getReduceAtt(){

		long time_start = Utils.getCurrenttime();
		boolean[] A = Utils.Instances2FullBoolean(this.m_data);//A为条件属性全集
		boolean[] D = Utils.Instances2DecBoolean(this.m_data);	
		int N = this.m_data.numAttributes();
		boolean[] newB = new boolean[N];
 

		double nowValue = -1;
		double temp = 0.0;
		int minIndex = -1;
		do{				
			minIndex = -1;
			//选一个最大的放进newB中
			for(int i=0;i<N-1;++i){
				if(!newB[i]){
					newB[i]=true;
					temp = getMeausureValue(D,newB);
					if(temp > nowValue){
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
	public  double[][] getFuzzySimilarityRelation_Numeric(int seletAtt){
		int N = this.m_data.numInstances();
		int M = this.m_data.numAttributes();
		double[][] MR = new double[N][N];
		double[] Vas = this.m_data.attributeToDoubleArray(seletAtt);			
		double[] vasStatistics = Utils.getStatisticsValue(Vas);
		this.m_sstyle.SimilaritySetting(vasStatistics[1],vasStatistics[2],vasStatistics[5]);
			for(int i=0;i<N;++i){
				MR[i][i]=1.0;
				for(int j=i+1;j<N;++j){
					MR[i][j] = this.m_sstyle.getSimilarityValue(this.m_data.instance(i).value(seletAtt),this.m_data.instance(j).value(seletAtt));
					MR[j][i] = MR[i][j];
				}
			}
		return MR;
	}
	public double[][] getFuzzySimilarityRelation_Nominal(int seletAtt){
		int N = this.m_data.numInstances();
		double[][] MR = new double[N][N];
		for(int i=0;i<N;++i){
			MR[i][i]=1.0;
			for(int j=i+1;j<N;++j){
				MR[i][j] = this.m_data.instance(i).value(seletAtt)==this.m_data.instance(j).value(seletAtt)?1:0; 
				MR[j][i] = MR[i][j];
			}
		}
		return MR;
		}
	public double[][] getFuzzySimilarityRelation(boolean[] P){
		int N = this.m_data.numInstances();
		int M = this.m_data.numAttributes();
		double[][] newMR = new double[N][N];
		for(int i=0;i<N;++i){
			Arrays.fill(newMR[i], 1);
		}
		for(int j=0;j<M;++j){
			double[][] tempMR =  new double[N][N];
			if(P[j]){
				if(this.m_data.attribute(j).isNumeric()){
					tempMR = getFuzzySimilarityRelation_Numeric(j);
				}
				else
					tempMR = getFuzzySimilarityRelation_Nominal(j);
				for(int k=0;k<N;++k){
					for(int q=0;q<N;++q){
						newMR[k][q] = this.m_itstyle.getfuzzyTnromValue(newMR[k][q], tempMR[k][q]);
					}
				}
			}			
		}
		return newMR;
	}
	public String getInformation(){
		String str = "E-FRFS算法->所用时间:" + Utils.doubleFormat("0.0000", this.m_useTime)+"s  约简个数："+this.m_numRed+"\n"+this.m_process;
		str += "最终约简:"+Arrays.toString(this.m_selectAtt);
		System.out.println(str);
		return str;
		}
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		String fn = "C:/Users/Eric/Desktop/2011秋冬/Code/Xreducer/data/Data/wine.arff";
		Instances m_data = new Instances(new FileReader(fn));
		m_data.setClassIndex(m_data.numAttributes()-1); 
		SimilarityStyle sstyle = new SStyle_MaxMin();
		ImplicatorTnormStyle itstyle = new ITStyle_Lukasiewicz(); 
		MStyle_FuzzyEntropy mg = new MStyle_FuzzyEntropy(m_data,sstyle, itstyle);
		//boolean[] D=Utils.Instances2DecBoolean(m_data);
		///boolean[] P = new boolean[m_data.numAttributes()];
		//P[6]=true;
		//double ans = mg.getMeausureValue(D, P);
		//System.out.println(ans);
	}

}
