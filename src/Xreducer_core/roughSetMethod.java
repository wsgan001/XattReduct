package Xreducer_core;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Arrays;
import java.util.Date;

import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Remove;
import Xreducer_core.Utils_fuzzy.xFuzzySimilarity;
import Xreducer_struct.oneAlgorithm;
import Xreducer_struct.oneFile;
import Xreducer_struct.oneAlgorithm.xCategory;
import Xreducer_struct.oneAlgorithm.xStyle;

public class roughSetMethod extends ReduceMethod{

	public oneAlgorithm alg = null;
	public int NumAttr = 0; //
	public xStyle rough_type; //0为条件熵 1为正域
	private double E_A = 0;
	private double gamma = -1;
	private int[] rankIndex ; //条件熵索引由小到大排序,粒度由大到小排序
	public Instances OriginalData = null;
	private boolean isrecall = false;
	public Instances Data = null;
	public oneFile afile = null;
	
	public roughSetMethod(oneFile afile, oneAlgorithm alg) throws IOException, Exception{
		this.afile = afile;
		this.alg = alg;
		this.NumAttr = this.afile.att;
		this.rough_type = this.alg.style;;
		this.gamma = this.alg.alpha;
		this.isrecall = this.alg.flag;
		this.rankIndex = new int[NumAttr-1]; //不包括决策属性

		wekaDiscretizeMethod dm = new wekaDiscretizeMethod(afile.filepath,true);	

		this.OriginalData = dm.getOriginalData();
		switch(this.rough_type){
		case conditionentropy:
		case positive_RSAR:
		
		{
			this.Data = dm.getDiscretizeData();
			sortEntropy();
			boolean[] A1 = Utils.Instances2FullBoolean(this.Data);//A为条件属性全集
			boolean[] D1 = Utils.Instances2DecBoolean(this.Data);
			this.E_A = Utils.getEvaluateValue(this.rough_type,this.Data,D1,A1);
			
			break;
		}
		case fuzzyCEntorpy_FHFS:{
			this.Data = dm.getOriginalData();
			sortEntropy();
			boolean[] A1 = Utils.Instances2FullBoolean(this.Data);//A为条件属性全集
			boolean[] D1 = Utils.Instances2DecBoolean(this.Data);
			this.E_A = Utils.getEvaluateValue(this.rough_type,this.Data,D1,A1);
			
			break;
		}
		case fuzzyEntorpy_EFRFS:
		case positive_DMRSAR:	
		case fuzzyPositive_Low:
		case fuzzyPositive_Boundary:
		
		{
			this.Data = dm.getOriginalData();
			boolean[] A2 = Utils.Instances2FullBoolean(this.Data);//A为条件属性全集
			boolean[] D2 = Utils.Instances2DecBoolean(this.Data);
			this.E_A = Utils.getEvaluateValue(this.rough_type,this.Data,D2,A2);
			break;
		}
		case fuzzyset_FRFS:			
			this.Data = dm.getOriginalData();
			//sortEntropy();
			break;
		default:break;
		}

	}
	
	public void sortEntropy(){
		
		boolean[] A = Utils.Instances2FullBoolean(this.Data);//A为条件属性全集
		boolean[] D = Utils.Instances2DecBoolean(this.Data);		
		
				
		for(int i=0;i<NumAttr-1;++i){
			rankIndex[i]=i;
		}
		boolean[] Btemp = new boolean[NumAttr];
		double[] restemp = new double[NumAttr-1];
		for(int i=0;i<NumAttr-1;++i){ //不包括决策属性
				Btemp[i]=true;
				restemp[i]=Utils.getEvaluateValue(this.rough_type,this.Data,D,Btemp);
				Btemp[i]=false;
		}
				
		switch(this.rough_type){
		case conditionentropy:
		case fuzzyCEntorpy_FHFS:{

			//冒泡由小到大排序restemp，索引放入rankIndex
			double temp;
			int tempindex;
			for(int i=0;i<NumAttr-1;++i){/* 冒泡法排序 */ 
				for(int j=0;j< NumAttr-i-2;++j){
					if(restemp[j]>restemp[j+1]) {
						//交换restemp
						temp = restemp[j];
						restemp[j] = restemp[j + 1];
						restemp[j + 1] = temp;
						//交换entropyRankindex
						tempindex = rankIndex[j];
						this.rankIndex[j] = this.rankIndex[j + 1];
						this.rankIndex[j + 1] = tempindex;
					}
				}
			}
			break;
		}
		case positive_RSAR:
		case positive_DMRSAR:
		case fuzzyset_FRFS:
		case fuzzyEntorpy_EFRFS:
		case fuzzyPositive_Boundary:
		case fuzzyPositive_Low:{

			//冒泡由大到小排序restemp，索引放入rankIndex
			//System.out.println(rough_type.getValue());
			//System.out.println(Arrays.toString(restemp));
			double temp;
			int tempindex;
			for(int i=0;i<NumAttr-1;++i){/* 冒泡法排序 */ 
				for(int j=0;j< NumAttr-i-2;++j){
					if(restemp[j]<restemp[j+1]) {
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
			
			break;
		}
		default:break;
		}
		//System.out.println(Arrays.toString(restemp));
	}
	public boolean[] getOneReduction_QuickReduct_Modification(boolean[] B){
		int readyRedAttribute = 0;
		for (int i=0;i<NumAttr-1;++i){
			if (!B[i]) {
				readyRedAttribute++;
			}
		}
		if(readyRedAttribute==0) //全部属性都选择了
			return B;
		boolean[] newB = new boolean[NumAttr];
		boolean[] tempB = new boolean[NumAttr];
		boolean[] D = Utils.Instances2DecBoolean(this.Data);
		double gama = this.gamma*this.E_A;
		double rbest = 0.0;
		double rprev = 0.0;
		do{
			tempB = newB.clone();
			rprev = rbest;
			for(int i=0;i<NumAttr-1;++i){ //去掉决策属性
				if(!newB[i]&&!B[i]){
					newB[i]=true;
					double rRandx = Utils.getEvaluateValue(this.rough_type,this.Data,D,newB);
					double rT = Utils.getEvaluateValue(this.rough_type,this.Data,D,tempB);
					if(Utils.isAllFalse(tempB)||rRandx>rT){
						tempB = newB.clone();
						rbest = rRandx;
					}
					newB[i]=false;
				}
			}
			newB = tempB.clone();
			System.out.println(Arrays.toString(Utils.boolean2select(newB))+"=>"+Utils.getEvaluateValue(this.rough_type,this.Data,D,newB));
		}
		while(rbest!=rprev);	
		
		//在已经选中的属性中 回溯遍
		for (int i=0; i<NumAttr-1; ++i){
			if(newB[i] && this.isrecall){
				newB[i]=false;
				double tempRes = Utils.getEvaluateValue(this.rough_type,this.Data,D,newB);
				if(tempRes==-1.0||Math.abs(tempRes-this.E_A)>gama)
				{
					newB[i]=true;
				}
			}
		}
		
		return Utils.boolsAdd(B, newB);
	}
	public boolean[] getOneReduction_QuickReduct(boolean[] B){
		int readyRedAttribute = 0;
		for (int i=0;i<NumAttr-1;++i){
			if (!B[i]) {
				readyRedAttribute++;
			}
		}
		if(readyRedAttribute==0) //全部属性都选择了
			return B;
		boolean[] newB = new boolean[NumAttr];
		boolean[] tempB = new boolean[NumAttr];
		boolean[] D = Utils.Instances2DecBoolean(this.Data);
		double gama = this.gamma*this.E_A;
		
		do{
			tempB = newB.clone();
			for(int i=0;i<NumAttr-1;++i){ //去掉决策属性
				if(!newB[i]&&!B[i]){
					newB[i]=true;
					if(Utils.isAllFalse(tempB)||Utils.getEvaluateValue(this.rough_type,this.Data,D,newB)>Utils.getEvaluateValue(this.rough_type,this.Data,D,tempB)){
						tempB = newB.clone();
					}
					newB[i]=false;
				}
			}
			newB = tempB.clone();
			System.out.println(Arrays.toString(Utils.boolean2select(newB))+"=>"+Utils.getEvaluateValue(this.rough_type,this.Data,D,newB));
		}
		while(Utils.getEvaluateValue(this.rough_type,this.Data,D,newB)!=this.E_A);	
		
		//在已经选中的属性中 回溯遍
		for (int i=0; i<NumAttr-1; ++i){
			if(newB[i] && this.isrecall){
				newB[i]=false;
				double tempRes = Utils.getEvaluateValue(this.rough_type,this.Data,D,newB);
				if(tempRes==-1.0||Math.abs(tempRes-this.E_A)>gama)
				{
					newB[i]=true;
				}
			}
		}
		
		return Utils.boolsAdd(B, newB);
	}
	
	//条件熵约简 在B[]为假的属性子集中，得到一次约简的属性约简，与全集A的条件属性差不超过alpha终止
	//B 包含决策属性
	public boolean[] getOneReduction(boolean[] B){
		int readyRedAttribute = 0;
		for (int i=0;i<NumAttr-1;++i){
			if (!B[i]) {
				readyRedAttribute++;
			}
		}
		if(readyRedAttribute==0) //全部属性都选择了
			return B;
		boolean[] newB = new boolean[NumAttr];
		boolean[] D = Utils.Instances2DecBoolean(this.Data);
		double gama = this.gamma*this.E_A;
		//计算不在B(B[i]=false)中单个属性的e,选出最重要的单个属性minH_index 条件熵最小值  正域最大值
		int E_index = -1;
		for (int i=0;i<NumAttr-1;++i){
			//找到不在B中rankIndex最靠前的属性
			if(!B[rankIndex[i]]){
				E_index = rankIndex[i];
				break;
			}
		}

		//从单个E_index开始找到接近H(d|A)
		newB[E_index]=true;
		
		for (int i=0; i<readyRedAttribute; ++i){ 
			double last_value = Utils.getEvaluateValue(this.rough_type,this.Data,D,newB);
			if(Math.abs(last_value-this.E_A)<=gama){ //newB达到结束条件
				break;
			}
			else{//否则找一个差值最大的
				E_index = -1;
				double maxD_value = -1.0;
				double temp = 0.0;
				for (int k=0;k<NumAttr-1;++k){
					if (!newB[k]&&!B[k]) {
						newB[k]=true;  //将第K个暂时加入约简中
						temp = Math.abs(last_value-Utils.getEvaluateValue(this.rough_type,this.Data,D,newB));
						//if(newB[26]&&(k==4||k==33)){
							//System.out.println(Arrays.toString(Utils.boolean2select(newB)));
							//System.out.println(k+":"+Utils.getEvaluateValue(this.rough_type,this.Data,D,newB)+"%%"+E_index);
							
						//}
						
						if(Math.abs(temp-maxD_value)>0.00000001 && temp>maxD_value){ 
							//可能出现由于熵计算顺序不同产生的误差
							maxD_value = temp;
							E_index = k;
							
						}
						newB[k]=false;
					}
				}
				if(i!=readyRedAttribute-1)//所有属性都加到B中
				{
					newB[E_index]=true;
				}
			}
			System.out.println(Arrays.toString(Utils.boolean2select(newB))+"=>"+Utils.getEvaluateValue(this.rough_type,this.Data,D,newB));
		}
		
		//在已经选中的属性中 回溯遍
		for (int i=0; i<NumAttr-1; ++i){
			if(newB[i] && this.isrecall){
				newB[i]=false;
				double tempRes = Utils.getEvaluateValue(this.rough_type,this.Data,D,newB);
				if(tempRes==-1.0||Math.abs(tempRes-this.E_A)>gama)
				{
					newB[i]=true;
				}
			}
		}
		
		
		
		return Utils.boolsAdd(B, newB);
	}
	public void reLoadDataset(boolean[] B){
		this.Data = wekaDiscretizeMethod.filterDataSet(this.Data,B);
	}
	public Instances getData() {
		// TODO Auto-generated method stub
		return this.Data;
	}
	public boolean Run() throws Exception{
		long t_start=System.currentTimeMillis();
		long t_end=0;
		Date now = new Date();
		this.alg.startTime = DateFormat.getTimeInstance().format(now); 
		
		boolean[] B = new boolean [this.NumAttr];
		
		//sortEntropy();
		System.out.println(this.alg.algname);
		switch(this.rough_type){
		case conditionentropy:
		case positive_RSAR:
		case fuzzyCEntorpy_FHFS:
		{
			B = getOneReduction(B); //得到一次约简		
			break;
		}

		case positive_DMRSAR:	
		case fuzzyPositive_Low:
		case fuzzyPositive_Boundary:
		{
			B  = getOneReduction_QuickReduct(B);
			break;
		}
		case fuzzyset_FRFS:	
		case fuzzyEntorpy_EFRFS:{
			B  = getOneReduction_QuickReduct_Modification(B);
			break;}
		default:break;
		}

		
		t_end=System.currentTimeMillis();
		this.alg.redTime = (t_end-t_start)/(double)(1000*this.alg.numReduce);
		
		t_start=System.currentTimeMillis();
		t_end=0;
		this.alg.selectedAtt = Utils.boolean2select(B);
		Instances newData = wekaDiscretizeMethod.getSelectedInstances(this.OriginalData,B);
        //训练预测准确率
		this.alg.ACs = Utils.multicrossValidateModel(this.alg.cl, newData, this.alg.numRun,this.alg.numFold );
		
		
		t_end=System.currentTimeMillis();
		this.alg.trainTime = (t_end-t_start)/1000.0;
		this.alg.endTime = DateFormat.getTimeInstance().format(now); 
		return true;
	}
	
	public oneAlgorithm getAlg(){
		return this.alg;		
	}
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
    	
		  oneAlgorithm oneAlg = new oneAlgorithm();
 		  oneAlg.category = xCategory.Roughsetalg;
 		  oneAlg.style = xStyle.conditionentropy;
		  oneAlg.flag = false; 
		  oneAlg.alpha = 0;
 
			String fn = "C:/Users/Eric/Desktop/2011秋冬/Code/Xreducer/data/Data/lung-Michigan.arff";
		 // String fn = "C:/Users/Eric/Desktop/2011秋冬/Code/Xreducer/data/Data/wdbc.arff";
		  //String fn = "C:/Users/Eric/Desktop/2011秋冬/Code/Xreducer/data/Data/credit.arff";
		 // String fn = "C:/Users/Eric/Desktop/2011秋冬/Code/Xreducer/data/shen/wine-shen.arff";
		   //String fn = "C:/Users/Eric/Desktop/2011秋冬/Code/Xreducer/data/fuzzy/fuzzy-ex.arff";
		 //String fn = "C:/Users/Eric/Desktop/2011秋冬/Code/Xreducer/data/derm.arff";
		  oneFile onef = new oneFile(new File(fn));
		  Instances dataset = new Instances(new FileReader(fn));
		  dataset.setClassIndex(dataset.numAttributes()-1); 
		  onef.ins = dataset.numInstances();
		  onef.att = dataset.numAttributes();
		  onef.cla = dataset.numClasses();
			
		  roughSetMethod rs = new roughSetMethod(onef,oneAlg);
		  
		  boolean[] B = new boolean [rs.NumAttr];
		  boolean[] rq = rs.getOneReduction(B);
		  //boolean[] rq = rs.getOneReduction(B);
		  /*int runtime = 1;
		  long t_start=System.currentTimeMillis();
		  long t_end=0;
		  boolean[] rq = null;
		  for(int k=0;k<runtime;++k)
			  rq = rs.getOneReduction_QuickReduct_Modification(B);
		  t_end=System.currentTimeMillis();
		  double rtime1 = (t_end-t_start)/(double)(1000*runtime);
		
		  t_start=System.currentTimeMillis();
		  boolean[] r = null;
		  for(int k=0;k<runtime;++k)
			  r = rs.getOneReduction(B);
		  t_end=System.currentTimeMillis();
		  double rtime2 = (t_end-t_start)/(double)(1000*runtime);*/
		  
		  //System.out.println(Arrays.toString(Utils.boolean2select(rq))+"--"+Utils.boolean2select(rq).length+"--"+rtime1);
		  //System.out.println(Arrays.toString(Utils.boolean2select(r))+"--"+Utils.boolean2select(r).length+"--"+rtime2);
		  System.out.println(Arrays.toString(Utils.boolean2select(rq))+"--"+Utils.boolean2select(rq).length);
		  System.out.println("Success~");
	}
}
