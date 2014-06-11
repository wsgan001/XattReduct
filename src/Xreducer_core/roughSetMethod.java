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
	public xStyle rough_type; //0Ϊ������ 1Ϊ����
	private double E_A = 0;
	private double gamma = -1;
	private int[] rankIndex ; //������������С��������,�����ɴ�С����
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
		this.rankIndex = new int[NumAttr-1]; //��������������

		wekaDiscretizeMethod dm = new wekaDiscretizeMethod(afile.filepath,true);	

		this.OriginalData = dm.getOriginalData();
		switch(this.rough_type){
		case conditionentropy:
		case positive_RSAR:
		
		{
			this.Data = dm.getDiscretizeData();
			sortEntropy();
			boolean[] A1 = Utils.Instances2FullBoolean(this.Data);//AΪ��������ȫ��
			boolean[] D1 = Utils.Instances2DecBoolean(this.Data);
			this.E_A = Utils.getEvaluateValue(this.rough_type,this.Data,D1,A1);
			
			break;
		}
		case fuzzyCEntorpy_FHFS:{
			this.Data = dm.getOriginalData();
			sortEntropy();
			boolean[] A1 = Utils.Instances2FullBoolean(this.Data);//AΪ��������ȫ��
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
			boolean[] A2 = Utils.Instances2FullBoolean(this.Data);//AΪ��������ȫ��
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
		
		boolean[] A = Utils.Instances2FullBoolean(this.Data);//AΪ��������ȫ��
		boolean[] D = Utils.Instances2DecBoolean(this.Data);		
		
				
		for(int i=0;i<NumAttr-1;++i){
			rankIndex[i]=i;
		}
		boolean[] Btemp = new boolean[NumAttr];
		double[] restemp = new double[NumAttr-1];
		for(int i=0;i<NumAttr-1;++i){ //��������������
				Btemp[i]=true;
				restemp[i]=Utils.getEvaluateValue(this.rough_type,this.Data,D,Btemp);
				Btemp[i]=false;
		}
				
		switch(this.rough_type){
		case conditionentropy:
		case fuzzyCEntorpy_FHFS:{

			//ð����С��������restemp����������rankIndex
			double temp;
			int tempindex;
			for(int i=0;i<NumAttr-1;++i){/* ð�ݷ����� */ 
				for(int j=0;j< NumAttr-i-2;++j){
					if(restemp[j]>restemp[j+1]) {
						//����restemp
						temp = restemp[j];
						restemp[j] = restemp[j + 1];
						restemp[j + 1] = temp;
						//����entropyRankindex
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

			//ð���ɴ�С����restemp����������rankIndex
			//System.out.println(rough_type.getValue());
			//System.out.println(Arrays.toString(restemp));
			double temp;
			int tempindex;
			for(int i=0;i<NumAttr-1;++i){/* ð�ݷ����� */ 
				for(int j=0;j< NumAttr-i-2;++j){
					if(restemp[j]<restemp[j+1]) {
						//����restemp
						temp = restemp[j];
						restemp[j] = restemp[j + 1];
						restemp[j + 1] = temp;
						//����entropyRankindex
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
		if(readyRedAttribute==0) //ȫ�����Զ�ѡ����
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
			for(int i=0;i<NumAttr-1;++i){ //ȥ����������
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
		
		//���Ѿ�ѡ�е������� ���ݱ�
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
		if(readyRedAttribute==0) //ȫ�����Զ�ѡ����
			return B;
		boolean[] newB = new boolean[NumAttr];
		boolean[] tempB = new boolean[NumAttr];
		boolean[] D = Utils.Instances2DecBoolean(this.Data);
		double gama = this.gamma*this.E_A;
		
		do{
			tempB = newB.clone();
			for(int i=0;i<NumAttr-1;++i){ //ȥ����������
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
		
		//���Ѿ�ѡ�е������� ���ݱ�
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
	
	//������Լ�� ��B[]Ϊ�ٵ������Ӽ��У��õ�һ��Լ�������Լ����ȫ��A���������Բ����alpha��ֹ
	//B ������������
	public boolean[] getOneReduction(boolean[] B){
		int readyRedAttribute = 0;
		for (int i=0;i<NumAttr-1;++i){
			if (!B[i]) {
				readyRedAttribute++;
			}
		}
		if(readyRedAttribute==0) //ȫ�����Զ�ѡ����
			return B;
		boolean[] newB = new boolean[NumAttr];
		boolean[] D = Utils.Instances2DecBoolean(this.Data);
		double gama = this.gamma*this.E_A;
		//���㲻��B(B[i]=false)�е������Ե�e,ѡ������Ҫ�ĵ�������minH_index ��������Сֵ  �������ֵ
		int E_index = -1;
		for (int i=0;i<NumAttr-1;++i){
			//�ҵ�����B��rankIndex�ǰ������
			if(!B[rankIndex[i]]){
				E_index = rankIndex[i];
				break;
			}
		}

		//�ӵ���E_index��ʼ�ҵ��ӽ�H(d|A)
		newB[E_index]=true;
		
		for (int i=0; i<readyRedAttribute; ++i){ 
			double last_value = Utils.getEvaluateValue(this.rough_type,this.Data,D,newB);
			if(Math.abs(last_value-this.E_A)<=gama){ //newB�ﵽ��������
				break;
			}
			else{//������һ����ֵ����
				E_index = -1;
				double maxD_value = -1.0;
				double temp = 0.0;
				for (int k=0;k<NumAttr-1;++k){
					if (!newB[k]&&!B[k]) {
						newB[k]=true;  //����K����ʱ����Լ����
						temp = Math.abs(last_value-Utils.getEvaluateValue(this.rough_type,this.Data,D,newB));
						//if(newB[26]&&(k==4||k==33)){
							//System.out.println(Arrays.toString(Utils.boolean2select(newB)));
							//System.out.println(k+":"+Utils.getEvaluateValue(this.rough_type,this.Data,D,newB)+"%%"+E_index);
							
						//}
						
						if(Math.abs(temp-maxD_value)>0.00000001 && temp>maxD_value){ 
							//���ܳ��������ؼ���˳��ͬ���������
							maxD_value = temp;
							E_index = k;
							
						}
						newB[k]=false;
					}
				}
				if(i!=readyRedAttribute-1)//�������Զ��ӵ�B��
				{
					newB[E_index]=true;
				}
			}
			System.out.println(Arrays.toString(Utils.boolean2select(newB))+"=>"+Utils.getEvaluateValue(this.rough_type,this.Data,D,newB));
		}
		
		//���Ѿ�ѡ�е������� ���ݱ�
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
			B = getOneReduction(B); //�õ�һ��Լ��		
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
        //ѵ��Ԥ��׼ȷ��
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
 
			String fn = "C:/Users/Eric/Desktop/2011�ﶬ/Code/Xreducer/data/Data/lung-Michigan.arff";
		 // String fn = "C:/Users/Eric/Desktop/2011�ﶬ/Code/Xreducer/data/Data/wdbc.arff";
		  //String fn = "C:/Users/Eric/Desktop/2011�ﶬ/Code/Xreducer/data/Data/credit.arff";
		 // String fn = "C:/Users/Eric/Desktop/2011�ﶬ/Code/Xreducer/data/shen/wine-shen.arff";
		   //String fn = "C:/Users/Eric/Desktop/2011�ﶬ/Code/Xreducer/data/fuzzy/fuzzy-ex.arff";
		 //String fn = "C:/Users/Eric/Desktop/2011�ﶬ/Code/Xreducer/data/derm.arff";
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
