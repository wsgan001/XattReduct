package Part3;

 
 
 
import java.util.Vector;

import weka.core.Instances;

public class Daul_POS extends FSmethod {

	public double alpha = -1;
	public double lambda = -1;
	public Vector<double[]> processValues = null;
	public Daul_POS(Instances data, String algname, boolean[] labeltag, double alpha, double lambda) {
		super(data, algname, labeltag);
		// TODO Auto-generated constructor stub
		this.alpha = alpha;
		this.processValues = new Vector<double[]>();
		this.alpha = this.m_data.numClasses();
		this.lambda = lambda;
		this.m_selectAtt = this.getSelectedAtt();
	}
 
	@Override
	public boolean[] getReduceAtt() {
		// TODO Auto-generated method stub
		long time_start = Utils.getCurrenttime();	
		
		int nAttrNoDes = this.m_data.numAttributes()-1;
	 
		boolean[] Red = new boolean[nAttrNoDes+1];
	 
		boolean[] A = Utils.Instances2FullBoolean(this.m_data);
		boolean[] D = Utils.Instances2DecBoolean(this.m_data);
		
	
		int[] cfbf_index = new int[nAttrNoDes]; //不包括决策属性
		double[] cfbf_value = new double[nAttrNoDes];
		
		 		
		
		boolean[] tmp = new boolean[nAttrNoDes+1];
		for(int k=0;k<nAttrNoDes;++k){
			tmp[k]=true;
			//cfbf_value[k]=Utils_entropy.getGranulation(m_data, D, tmp,this.labeltag);
			cfbf_value[k]=getRankList(D,tmp,this.labeltag);
			cfbf_index[k]=k;
			tmp[k]=false;
		}

		//排序
		double temp;
		int tempindex;
		for(int i=0;i<nAttrNoDes;++i){/* 冒泡法排序 */ 
			for(int j=0;j< nAttrNoDes-i-1;++j){
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
			int log = (int) (nAttrNoDes/Utils.log2(nAttrNoDes));
			this.lambda = cfbf_value[log];
		}
		
		
		Red[cfbf_index[0]]=true;
		 
		 
		/*for(int k =1;k!=(nAttrNoDes) && cfbf_value[k]>=this.lambda;k++){
			boolean[] X = new boolean[nAttrNoDes+1];
			X[cfbf_index[k]]=true;//newone
			boolean isRud = false;
			for(int i=0;i<k;++i){
				if(Red[cfbf_index[i]]){
					boolean[] Y = new boolean[nAttrNoDes+1];
					Y[cfbf_index[i]]=true;//oldone			 
					//if(this.getMeausureValue(X, Y)>=this.m_lambda*cfbf_value[cnt]){//不相关	
					//System.out.println(this.getDaulPos(X, Y)+":"+cfbf_value[k]);
					if(this.getDaulPos(X, Y)>this.alpha*cfbf_value[k]){//不相关    X--ai Y--aj \in Red
						isRud = true;
						break;
					}		
				}
			}
			if(!isRud){
				Red[cfbf_index[k]]=true;
				//System.out.println(Arrays.toString(Utils.boolean2select(newB))+"->"+cfbf_index[cfbf_index[k]]+":"+cfbf_value[cfbf_index[k]]);
			}
		}*/
		
		for(int k =1;k!=(nAttrNoDes) && cfbf_value[k]>=this.lambda;k++){
			boolean[] tmpRed = Red.clone();
			tmpRed[cfbf_index[k]]=true;
			boolean[] X = new boolean[nAttrNoDes+1];
			X[cfbf_index[k]]=true;//newone
			double part1_rel = this.getDaulPos(D,tmpRed,this.labeltag)/(Utils.booleanSelectedNum(tmpRed)+1);
			double part2_rud = this.getDaulPos(X, Red)/(Utils.booleanSelectedNum(Red)+1);
			
			
			double[] values = new double[5];
			values[4] = -1;
			if(cfbf_value[k]*part1_rel>=part2_rud){
				Red[cfbf_index[k]]=true;
				values[4] = 1;
			}
			values[0] = cfbf_index[k];
			values[1] = cfbf_value[k];
			values[2] = cfbf_value[k]*part1_rel;
			values[3] = part2_rud;
			this.processValues.add(values);
			
			//System.out.println(cfbf_value[k]*part1_rel+":"+part2_rud);
		}
		
		this.m_useTime = (Utils.getCurrenttime() - time_start)/(double)1000;
		this.m_numRed = Utils.booleanSelectedNum(Red);
		
		return Red;
	}

	private double getRankList(boolean[] d, boolean[] tmp, boolean[] labeltag) {
		// TODO Auto-generated method stub
		//double ans = Utils_entropy.getGranulation(m_data, d, tmp, this.labeltag)+Utils_entropy.getGranulation(m_data, tmp, d,this.labeltag);
		//return ans/(double)2;
		int ind_i = 0;
		for(int i=0;i<tmp.length;++i){
			if(tmp[i]){
				ind_i = i;
				break;
			}
		}
		int ind_j = this.m_data.classIndex();
		return Utils_entropy.getConsistentRate(m_data,ind_i,ind_j,this.labeltag);
	}
	private double getDaulPos(boolean[] d, boolean[] a, boolean[] labeltag) {
		// TODO Auto-generated method stub
		double ans = Utils_entropy.getGranulation(m_data, d, a,labeltag)+Utils_entropy.getGranulation(m_data, a, d,labeltag);
		return ans/(double)2;
		
		//return Utils_entropy.getGranulation(m_data, d, a,labeltag);
	}
 
	private double getDaulPos(boolean[] d, boolean[] a) {
		// TODO Auto-generated method stub
		double ans = Utils_entropy.getGranulation(m_data, d, a)+Utils_entropy.getGranulation(m_data, a, d);
		return ans/(double)2;
	}

	@Override
	public boolean isStopping(boolean[] Red) {
		// TODO Auto-generated method stub
		return false;
	}

}
