package Xreducer_fuzzy;

import java.io.FileReader;
import java.util.Arrays;
 

import weka.core.Instances;
import Xreducer_core.Utils;
import Xreducer_core.Utils_entropy;

public class MStyle_Boundary extends MeasureStyle{
	
	public MStyle_Boundary(Instances data,SimilarityStyle sstyle, ImplicatorTnormStyle itstyle) {
		super(data, sstyle,itstyle);
		// TODO Auto-generated constructor stub
		this.algname = "B-FRFS算法";
		this.m_selectAtt = getSelectedAtt();
		
	}
	public double getMeausureValue(boolean[] D, boolean[] B){
		double ans = 0.0;
		int U = this.m_data.numInstances();
		int N = this.m_data.numAttributes();
		boolean[][] MRd = Utils_entropy.getEquivalenceClass(this.m_data,D);	
		int dM = MRd[0].length;
		double[][] vasStatistics = new double[Utils.booleanSelectedNum(B)][8];
		int vsIndex = 0;
		for(int k=0;k<N;++k){
			if(B[k]&&this.m_data.attribute(k).isNumeric()){
				vasStatistics[vsIndex][0] = 0;
				vasStatistics[vsIndex][1] = k;
				double[] Vas = this.m_data.attributeToDoubleArray(k);			
				double[] temp = Utils.getStatisticsValue(Vas);
				vasStatistics[vsIndex][2] = temp[0];
				vasStatistics[vsIndex][3] = temp[1];
				vasStatistics[vsIndex][4] = temp[2];
				vasStatistics[vsIndex][5] = temp[3];
				vasStatistics[vsIndex][6] = temp[4];
				vasStatistics[vsIndex][7] = temp[5];
				vsIndex++;
			}
			else if(B[k]&&!this.m_data.attribute(k).isNumeric()){			
				vasStatistics[vsIndex][0] = 1;
				vasStatistics[vsIndex][1] = k;
				vsIndex++;
			}
		}
		for(int i=0;i<U;++i){

			double[] sumV = new double[U];
			Arrays.fill(sumV, 1);
					
			for(int q=0;q<U;++q) {
				for(int k=0;k<vasStatistics.length;++k){
				if(vasStatistics[k][0]==0){
					int pIndex = (int)vasStatistics[k][1];
					this.m_sstyle.SimilaritySetting(vasStatistics[k][3],vasStatistics[k][4],vasStatistics[k][7]);
					double sd = this.m_sstyle.getSimilarityValue(this.m_data.instance(i).value(pIndex),this.m_data.instance(q).value(pIndex));
					sumV[q] = this.m_itstyle.getfuzzyTnromValue(sumV[q],sd);
				}
				else{
					int pIndex = (int)vasStatistics[k][1];
					sumV[q] = this.m_itstyle.getfuzzyTnromValue(sumV[q],this.m_data.instance(i).value(pIndex)==this.m_data.instance(q).value(pIndex)?1:0);			
				}
				}		
			}

			for(int d=0;d<dM;++d){ //对每个决策类
				double minUx = 1;
				double maxDx = -1.0;
				for(int q=0;q<U;++q) {
					minUx = Math.min(this.m_itstyle.getfuzzyImplicatorValue(sumV[q],MRd[q][d]?1:0),minUx);
					maxDx = Math.max(this.m_itstyle.getfuzzyTnromValue(sumV[q],MRd[q][d]?1:0),maxDx);
					
					
			    }
				//System.out.println(d+"&&"+(i+1)+"%%"+maxDx+"-"+minUx+"="+(maxDx-minUx));
				//if(maxDx-minUx<0){
						 //System.out.println(q);
						 //System.out.println(minUx+":"+maxDx);
					//}
					
				ans += Math.abs(maxDx-minUx);
			}
			
		}
		//System.out.println(ans+"/"+U+"*"+dM);
		return 1-ans/(double)(U*dM);
	}
	public boolean[] getReduceAtt(){
		int N = this.m_data.numAttributes();
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
			//System.out.println(Arrays.toString(Utils.boolean2select(newB))+"=>"+last_gamma_newB);
		}while(this.getMeausureValue(D,newB)<R_A);
		this.m_useTime = (Utils.getCurrenttime() - time_start)/(double)1000;
		this.m_numRed = Utils.booleanSelectedNum(newB);
		//System.out.println(this.algname+"Success!");
		return newB;
	}
	public String getInformation(){
		String str = "B-FRFS算法->所用时间:"+Utils.doubleFormat("0.0000", this.m_useTime)+"s  约简个数："+this.m_numRed+"\n"+this.m_process;
		str += "最终约简:"+Arrays.toString(this.m_selectAtt);
		System.out.println(str);
		return str;
		}
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		String fn = "C:/Users/Eric/Desktop/2011秋冬/Code/Xreducer/data/Data/ionosphere.arff";
		Instances m_data = new Instances(new FileReader(fn));
		m_data.setClassIndex(m_data.numAttributes()-1); 
		SimilarityStyle sstyle = new SStyle_MaxMin();
		ImplicatorTnormStyle itstyle = new ITStyle_Lukasiewicz(); 
		MStyle_Boundary mg = new MStyle_Boundary(m_data,sstyle, itstyle);
		boolean[] D=Utils.Instances2DecBoolean(m_data);
		boolean[] P = Utils.Instances2FullBoolean(m_data);
		//boolean[] P = new boolean[m_data.numAttributes()];
		//P[6]=true;
		double ans = mg.getMeausureValue(D, P);
		System.out.println(ans);
	}
}
