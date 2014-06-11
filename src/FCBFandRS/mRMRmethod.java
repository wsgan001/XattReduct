package FCBFandRS;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Random;
import java.util.Vector;

import Xreducer_core.Utils;
import Xreducer_struct.oneAlgorithm.xStyle;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.trees.J48;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Remove;
import weka.filters.unsupervised.attribute.ReplaceMissingValues;

public class mRMRmethod extends FSmethod {
	public xStyle rough_type; //0为条件熵 1为正域
	public xStyle sc_type;
	public int bin = -1;
	public double H_A = -1.0;
	public double a = -1.0;
	public double b = -1.0;
	public double c = -1.0;
	public double d = -1.0;
	public Vector<Double> sc = new Vector <Double>(); 
	public mRMRmethod(Instances data, int bin,  xStyle rough_type, double a, double b, double c, double d) {
		super(data);
		
		
		this.rough_type = rough_type;
		this.sc_type = xStyle.conditionentropy;
		this.bin = bin;
		this.a = a;
		this.b = b;
		this.c = c;
		this.d = d;
		
		String lg = "";
		if(bin==-1){
			lg = "-MDL";
		}
		else{
			lg = "-"+bin+"Bin";
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
		
		this.algname = "mRMR"+lg+"算法";
		boolean[] A = Utils.Instances2FullBoolean(this.m_data);
		boolean[] D = Utils.Instances2DecBoolean(this.m_data);
		this.H_A = Utils.getEvaluateValue(this.sc_type,this.m_data,D,A) ;
		//System.out.println(H_A);
		
		this.m_selectAtt = getSelectedAtt();
		// TODO Auto-generated constructor stub
	}
	public int[] getAttrList(){
		int NumAttr = this.m_data.numAttributes();
		int[] ans = new int[NumAttr];
		
		boolean[] Sm = new boolean[NumAttr];
		boolean[] Sm_1 = new boolean[NumAttr];
		boolean[] Smtmp = new boolean[NumAttr];
		boolean[] D = Utils.Instances2DecBoolean(this.m_data);
		
		
		double rbest = -1000000;

		double Mj = -1;
		int cnt=0;
		do{
			Smtmp = Sm.clone();
			Sm_1 = Sm.clone();
			rbest = -100000;
			int ind = -1;
			for(int i=0;i<NumAttr-1;++i){ //去掉决策属性
				if(!Sm[i]){
					Sm[i]=true;
					boolean[] tmp = new boolean[NumAttr];
					tmp[i]=true;
					double aa = this.a==0?0:this.a*Utils.getEvaluateValue(this.rough_type,this.m_data,D,tmp);
					double bb = this.b==0?0:this.b*this.sumRedunt(Sm_1, tmp);
					double cc = this.c==0?0:this.c*Utils.getEvaluateValue(this.rough_type,this.m_data,D,Sm);
					double dd = this.d==0?0:this.d*Utils.getEvaluateValue(this.rough_type,this.m_data,tmp,Sm_1);

					
					double Mrelevance = aa - bb;    //已知Sm-1 -> MAX_(xj\in S-Sm-1)[I(xj;c)- 1/m-1*\sum_(xi\in Sm-1)I(xj;xi)] ->Sm
					double Mdepedenance = cc - dd;  //已知Sm-1 -> MAX_(xj\in S-Sm-1)|I(Sm-1;c)-I(Sm-1+xj;c)| ->Sm
					double rRandx = Mrelevance + Mdepedenance;
					//System.out.println(rRandx);
					if(Utils.isAllFalse(Smtmp)||rRandx>rbest){
						Smtmp = Sm.clone();
						rbest = rRandx;
						ind = i;
					}
					Sm[i]=false;
				}
			}
			Sm = Smtmp.clone();
			ans[cnt++]= ind;
		}
		while(!Utils.isAllTrue(Smtmp));	
		ans[ans.length-1] = ans.length-1;
		return ans;
		
	}
	@Override
	public boolean[] getReduceAtt() {
		// TODO Auto-generated method stub
		int NumAttr = this.m_data.numAttributes();
		 
		// TODO Auto-generated method stub
		long time_start = Utils.getCurrenttime();	
		
		boolean[] Sm = new boolean[NumAttr];
		boolean[] Sm_1 = new boolean[NumAttr];
		boolean[] Smtmp = new boolean[NumAttr];
		boolean[] D = Utils.Instances2DecBoolean(this.m_data);
		
		
		double rbest = -1000000;

		double Mj = -1;
		do{
			Smtmp = Sm.clone();
			Sm_1 = Sm.clone();
			rbest = -100000;
			for(int i=0;i<NumAttr-1;++i){ //去掉决策属性
				if(!Sm[i]){
					Sm[i]=true;
					boolean[] tmp = new boolean[NumAttr];
					tmp[i]=true;
					double aa = this.a==0?0:this.a*Utils.getEvaluateValue(this.rough_type,this.m_data,D,tmp);
					double bb = this.b==0?0:this.b*this.sumRedunt(Sm_1, tmp);
					double Mjtmp = Utils.getEvaluateValue(this.rough_type,this.m_data,D,Sm);
					double cc = this.c==0?0:this.c*Mjtmp;
					double dd = this.d==0?0:this.d*Utils.getEvaluateValue(this.rough_type,this.m_data,tmp,Sm_1);

					
					double Mrelevance = aa - bb;    //已知Sm-1 -> MAX_(xj\in S-Sm-1)[I(xj;c)- 1/m-1*\sum_(xi\in Sm-1)I(xj;xi)] ->Sm
					double Mdepedenance = cc - dd;  //已知Sm-1 -> MAX_(xj\in S-Sm-1)|I(Sm-1;c)-I(Sm-1+xj;c)| ->Sm
					double rRandx = Mrelevance + Mdepedenance;
					//System.out.println(rRandx);
					if(Utils.isAllFalse(Smtmp)||rRandx>rbest){
						Smtmp = Sm.clone();
						rbest = rRandx;
						Mj = Mjtmp;
					}
					Sm[i]=false;
				}
			}
			double tmp = Utils.getEvaluateValue(this.sc_type,this.m_data,D,Smtmp);
			 sc.add( tmp);
			Sm = Smtmp.clone();
			this.m_process+=Arrays.toString(Utils.boolean2select(Sm))+"=>"+(rbest>0?rbest:-rbest)+"\n";
			//this.m_process+=Arrays.toString(Utils.boolean2select(Sm))+"=>"+Mj+"\n";
			 
		}
		while(!StopCriterion(Sm));	
		
		
		this.m_useTime = (Utils.getCurrenttime() - time_start)/(double)1000;
		this.m_numRed = Utils.booleanSelectedNum(Sm);
		return Sm;
	}
	public double sumRedunt (boolean[] Sm_1, boolean[] tmpj){
		double sum = 0;
		int m = Sm_1.length-1;
		for(int i=0 ;i<m; ++i){
			if(Sm_1[i]){
				boolean[] tmpi = new boolean[m+1];
				tmpi[i]=true;
				sum += Utils.getEvaluateValue(this.rough_type,this.m_data,tmpi,tmpj);
			}
		}
		
		return sum/(double)(m-1);
	}
	public boolean StopCriterion(boolean[] Sm){
		boolean[] D = Utils.Instances2DecBoolean(this.m_data);
		double sc = Utils.getEvaluateValue(this.sc_type,this.m_data,D,Sm);

		//System.out.println(H_A);
		if(Math.abs(sc-this.H_A)<0.01){
			return true;
		}
		else
			return false;
	}
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		String fn = "C:/Users/Eric/Desktop/2011秋冬/Code/Xreducer/data/Data/labor.arff";
	 
		Instances m_data = new Instances(new FileReader(fn));
		m_data.setClassIndex(m_data.numAttributes()-1); 
	 
		
		// Replace missing values   //被均值代替
		ReplaceMissingValues m_ReplaceMissingValues = new ReplaceMissingValues();
		m_ReplaceMissingValues.setInputFormat(m_data);
		m_data = Filter.useFilter(m_data, m_ReplaceMissingValues);
		
		int cnt = 10;
		int [] rednum  =new int [cnt];
		double [] ac = new double [cnt];
		double j=0;
		for(int i=0;i<cnt;i++)
		{
			
			mRMRmethod mg0 = new mRMRmethod(m_data,-1 ,xStyle.conditionentropy,0,0,1,j);
			j=j+0.1;
			rednum[i] = mg0.getSelectedAtt().length;
	    	Evaluation eval = new Evaluation(m_data);
		    eval.setPriors(m_data);
	    	eval.crossValidateModel(new NaiveBayes(), m_data, 10, new Random(i));
	    	ac[i] = eval.pctCorrect();
		}
		 
		//System.out.println(Arrays.toString(mg0));
		System.out.println(Arrays.toString(ac));
		System.out.println(Arrays.toString(rednum));
		//mg.getInformation();
	}

}
