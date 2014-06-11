package Plus;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Random;
import java.util.Vector;

import Xreducer_core.Utils;
import weka.core.Instances;
import weka.filters.Filter;
import FCBFandRS.FSmethod;
import Modification.Dependence;

public class RoughGASearch extends FSmethod {

	public double pc = -1;
	public double pm = -1;
	public int popsize = 0; //种群个数
	public int MaxIteration = 0; //最大迭代次数
	public int E_index = 0;
	public int bin = -1;
	public double ha = -1;
	public Random rd = null;
	public Vector<boolean[]> pop = null;
	public int[] fitness = null;
	public int sumfitness = -1;
	public int oldfitness = -1;
	public int sc = 20;
	public int tmpsc = -1;
	public RoughGASearch(Instances data, int bin,  int x, int N, double pc, double pm, int MaxIteration) {
		super(data);
		// TODO Auto-generated constructor stub
		
		this.E_index = x;
		this.bin = bin;
		this.popsize = N;
		this.pc = pc;
		this.pm = pm;
		this.MaxIteration = MaxIteration;
		rd = new Random(N);
		tmpsc = this.sc;
		
		String lg = "";
		if(bin==-1){
			lg = "-MDL";
		}
		else{
			lg = "-"+bin+"Bin";
		}
	 
		this.algname =  E_index+lg+"-GA算法";
		//离散化
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

		boolean[] A = Utils.Instances2FullBoolean(this.m_data);
		this.ha =  Dependence.getEvalution(this.m_data, A, E_index);
		
		this.m_selectAtt = getSelectedAtt();
	}

	@Override
	public boolean[] getReduceAtt() {
		// TODO Auto-generated method stub
		long time_start = Utils.getCurrenttime();	
		//initialize pop
		pop = new Vector<boolean[]>();
		this.fitness = new int[this.popsize];
		for(int i=0;i<this.popsize;++i)
		{
			boolean[] tmpB = new boolean[this.m_data.numAttributes()];
			for(int k=0;k<this.m_data.numAttributes()-1;++k)
			{
				tmpB[k]=rd.nextBoolean();
			}
			pop.add(tmpB.clone());
		}
		//evaluate pop
		evaluatePop();
		//while(this.MaxIteration!=0&&!StopCriterion())
		while(!StopCriterion())
		{
			SelectPop();
			CrossoverPop();
			MutatePop();
			ModifyPop();
			evaluatePop();
			
			if(this.sumfitness==0)
				break;
			//System.out.println(MaxIteration);
			this.MaxIteration--;
		}
		
		this.m_useTime = (Utils.getCurrenttime() - time_start)/(double)1000;
		int best = getMaxfitenss();
		boolean[] red = this.pop.elementAt(best).clone();
		this.m_numRed = Utils.booleanSelectedNum(red);
		return red;
	}
	public void ModifyPop(){
		for(int i=0;i<this.popsize;++i)
		{
			boolean[] tmp = getReduct(this.pop.elementAt(i));
			this.pop.set(i, tmp);
		}
	}
	public boolean[] getReduct(boolean[] B)
	{
		 boolean[] newB = B.clone();
		 double rbest = Dependence.getEvalution(this.m_data,newB, E_index);
		boolean[] tempB = newB.clone();
		while(rbest!=ha){
				tempB = newB.clone();
				rbest = -1000000; 
				for(int i=0;i<this.m_data.numAttributes()-1;++i){ //去掉决策属性
					if(!newB[i]){
						newB[i]=true;
						double rRandx =  Dependence.getEvalution(this.m_data,newB, E_index);
						if(Utils.isAllFalse(tempB)||rRandx>=rbest){
							tempB = newB.clone();
							rbest = rRandx;
						}
						newB[i]=false;
					}
				}
				newB = tempB.clone();
			 
			}
			 
				
		 
		 return newB;
	}
	public void MutatePop(){
		for(int i=0;i<this.popsize;++i)
		{
			if(rd.nextDouble()<this.pm)//按pm概率选择变异
			{
				int pos = rd.nextInt(this.m_data.numAttributes()-1);
				this.pop.elementAt(i)[pos]=!this.pop.elementAt(i)[pos];
			}
		}
	}
	public void CrossoverPop(){
		for(int i=0;i<this.popsize/2;++i)
		{
			if(rd.nextDouble()<this.pc)//按pc概率选择交叉
			{
				boolean[][] tmp = getCross(this.pop.elementAt(i),this.pop.elementAt(i+this.popsize/2));
				this.pop.set(i, tmp[0].clone());
				this.pop.set(i+this.popsize/2, tmp[1].clone());
			}
		}
	}
	public boolean[][] getCross(boolean[] A, boolean[] B)
	{
		boolean[][] ans = new boolean[2][A.length];
		ans[0] = A.clone();
		ans[1] = B.clone();
		int pos = rd.nextInt(A.length-1);
		for(int i=pos;i<A.length-1;++i)//单点交叉
		{
			ans[0][i] = B[i];
			ans[1][i] = A[i];
		}
		return ans;
	}
	public void SelectPop(){
		Vector<boolean[]> newpop = new Vector<boolean[]>();
		int minfvalue = this.m_data.numAttributes()-1;
		int minfindex = -1;  //轮盘赌选择
		for(int i=0;i<this.popsize;++i)
		{
			int s = rws();
			if(minfvalue>this.fitness[s])
			{
				minfvalue = this.fitness[s];
				minfindex = s;
			}
			newpop.add(pop.elementAt(s).clone());
		}
		int maxind = getMaxfitenss(); //精英策略
		if(minfvalue<this.fitness[maxind])
		{
			newpop.set(minfindex, pop.elementAt(maxind).clone());
		}
		this.pop = (Vector<boolean[]>) newpop.clone();
	}
	public int rws()
	{
		double m=0;
		double r = rd.nextDouble();
		for(int i=0;i<this.popsize;++i)
		{
			m=m+(double)this.fitness[i]/(double)this.sumfitness;
			if(r<=m)
				return i;
		}
		return -1;
	}
	public int getMaxfitenss()
	{
		int max = 0;
		int maxindex = -1;
		for(int i=0;i<this.popsize;++i)
		{
			if(max<this.fitness[i])
			{
				max = this.fitness[i];
				maxindex = i;
			}
				
		}
		return maxindex;
	}
	public void evaluatePop(){
		this.sumfitness=0;
		for(int i=0;i<this.popsize;++i)
		{
			this.fitness[i]=0;
			for(int k=0;k<this.m_data.numAttributes()-1;++k)
			{
				if(!this.pop.elementAt(i)[k])
				{
					this.fitness[i]++;
					this.sumfitness++;
				}
			}
		}
	}
	public boolean StopCriterion(){  //若干代平均适应度没有变化 返回true
		int best = bestFitness(this.fitness);
		//int best = this.sumfitness;
		if(this.oldfitness==best)
		{
			this.tmpsc--;
		}
		else
		{
			this.tmpsc = this.sc;
		}
		this.oldfitness = best;
		if(this.tmpsc==0)//若干代平均适应度没有变化 返回true
		{
			return true;
		}
		else
			return false;
 
	}
	public int bestFitness(int[] F){
		int maxB = -1;
		for(int i=0;i<F.length;++i){
			if(F[i]>maxB)
				maxB=F[i];
		}
		return maxB;
	}
	
	public static void main(String[] args) throws FileNotFoundException, IOException {
		// TODO Auto-generated method stub
		String fn = "C:/Users/Eric/Desktop/2011秋冬/Code/Xreducer/data/Data/vote.arff";
		//String fn = "C:/Users/Eric/Desktop/2012春/Modification.NO3/Data/current data/Balance.arff";
		Instances m_data = new Instances(new FileReader(fn));
		m_data.setClassIndex(m_data.numAttributes()-1); 
	 
		//System.out.println(m_data.numInstances());
		 
		
		 
		RoughGASearch mg = new RoughGASearch(m_data,-1, 1, 20 ,1,0.05,100);
		 
		 mg.getInformation();
	}
}
