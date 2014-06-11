package Xreducer_core;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Arrays;
import java.util.Date;

import weka.core.Instances;
import Xreducer_struct.oneAlgorithm;
import Xreducer_struct.oneFile;
import Xreducer_struct.oneAlgorithm.xCategory;
import Xreducer_struct.oneAlgorithm.xStyle;

public class RSandFCBFReduceMethod extends ReduceMethod{
	public oneAlgorithm alg = null;
	private double gamma = -1;
	private xStyle FCBF_type= null;
	private int[] rankIndex; //IR(i,c)由大到小排序 越大和决策属性相关度越高  越重要
	private double[] rankValue; //没有排序 按原来顺序排列
	private int NumAttr = 0; //包括决策属性
	private xStyle SCstyle = null;
	private double SCvalue = -1.0;
	
	public Instances OriginalData = null;
	public Instances Data = null;
	public oneFile afile = null;
	
	public RSandFCBFReduceMethod(oneFile afile, oneAlgorithm alg) throws IOException, Exception{
		this.afile = afile;
		this.alg = alg;
		this.NumAttr = this.afile.att;
		this.gamma = this.alg.alpha;



		this.FCBF_type = this.alg.style;	
		wekaDiscretizeMethod dm = new wekaDiscretizeMethod(afile.filepath,true);
		this.OriginalData = dm.getOriginalData();
		switch(this.FCBF_type){
		case conditionentropy:
		case SU:
		case IG:
			this.Data = dm.getDiscretizeData();break;
		case fuzzyCEntorpy_FHFS:
		case fuzzySU:
		case fuzzyIG:	
			this.Data = dm.getOriginalData();break;
		default:break;
		}
		this.rankIndex = new int[NumAttr-1]; //不包括决策属性
		for(int i=0;i<rankIndex.length;++i){
			this.rankIndex[i]=i;
		}
		this.rankValue = new double[NumAttr-1]; //不包括决策属性
		this.SCstyle = xStyle.fuzzyCEntorpy_FHFS;
		this.SCvalue = Utils.getEvaluateValue(SCstyle, this.Data, Utils.Instances2DecBoolean(Data), Utils.Instances2FullBoolean(Data));
		sortEvaluate();
	}
	public void reLoadDataset(boolean[] B){
		this.Data = wekaDiscretizeMethod.filterDataSet(this.Data,B);
	}
	public Instances getData() {
		// TODO Auto-generated method stub
		return this.Data;
	}
	public void sortEvaluate(){
		
			double[] restemp = new double[NumAttr-1]; //不包括决策属性
			boolean[] B = new boolean[NumAttr];
			boolean[] D = Utils.Instances2DecBoolean(this.Data);
			for(int i=0;i<NumAttr-1;++i){
				B[i]=true;
				restemp[i]=Utils.getEvaluateValue(this.FCBF_type, this.Data, D, B);
				B[i]=false;
			}
			this.rankValue = restemp.clone();
			switch(this.FCBF_type){
			case conditionentropy:
			case fuzzyCEntorpy_FHFS:	
			{
				//冒泡由大到小排序restemp，索引放入rankIndex
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
			case SU:
			case IG:
			case fuzzySU:
			case fuzzyIG:{	
			//冒泡由大到小排序restemp，索引放入rankIndex
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

	}
	
	public boolean[] getOneReduction(boolean[] B){
		
		boolean[] newB = new boolean[this.NumAttr];
		//System.out.println(Utils.booleanSelectedNum(B));
		int[] cfbf = new int[NumAttr-1]; //不包括决策属性
		int cnt = 0;
		for(int i=0;i<NumAttr-1;++i){
			if(!B[this.rankIndex[i]]){
				cfbf[i]=this.rankIndex[i];
				newB[cfbf[i]]=true;
				cnt++;
			}
		}
		boolean[] redB = new boolean[NumAttr];
		for(int j=0;j<cnt;++j){//从第二个开始
			if(newB[cfbf[j]]){				
				for(int i=0;i<cnt;++i){
					if(redB[cfbf[i]]){
						boolean[] X = new boolean[NumAttr];
						boolean[] Y = new boolean[NumAttr];
						X[cfbf[j]]=true;//newone
						Y[cfbf[i]]=true;//oldone
						//if(cfbf[j]==8&&cfbf[i]==2)
							System.out.println(cfbf[j]+":"+cfbf[i]+":"+Utils.getEvaluateValue(this.FCBF_type, this.Data,X,Y,4.0)+":"+this.rankValue[cfbf[j]]);
						//if(Utils.getEvaluateValue(this.FCBF_type, this.Data,X,Y,this.gamma)>=this.gamma*this.rankValue[cfbf[j]]){
						if(Utils.getEvaluateValue(this.FCBF_type, this.Data,X,Y,4.0)>=this.gamma*this.rankValue[cfbf[j]]){
							//相关
							newB[cfbf[j]]=false;
							break;
						}
					}
				}
				if(newB[cfbf[j]]){	
				    redB[cfbf[j]]=true;
				    System.out.println(Arrays.toString(Utils.boolean2select(redB)));
				    boolean[] D = Utils.Instances2DecBoolean(this.Data);
					if(Math.abs(Utils.getEvaluateValue(SCstyle, this.Data, D, redB,this.gamma)-this.SCvalue)<0.0001){
						System.out.println("xxx");
						return Utils.boolsAdd(B, redB);
					}
						
				}
				 
			}
		}
        //System.out.println(Utils.booleanSelectedNum(Utils.boolsAdd(B, newB)));
		return Utils.boolsAdd(B, newB);

	}
	public boolean Run() throws Exception{
		long t_start=System.currentTimeMillis();
		long t_end=0;
		Date now = new Date();
		this.alg.startTime = DateFormat.getTimeInstance().format(now); 
		
		boolean[] B = new boolean [this.NumAttr];
		
		//sortEvaluate();
		/*if(this.gamma==-1.0){
			int x =(int)(Math.log((double)2)/Math.log((double)(NumAttr-1))*(NumAttr-1));
			this.gamma = this.rankValue[this.rankIndex[x]];
		}*/
		
		B = getOneReduction(B); //得到一次约简
		
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
		  oneAlg.category = xCategory.RSandFCBFalg;
		  oneAlg.style = xStyle.fuzzySU;
		  oneAlg.flag = false; 
		  oneAlg.alpha = 2.0;
			//String fn = "C:/Users/Eric/Desktop/2011秋冬/Code/Xreducer/data/Data/wine.arff";
		 // String fn = "C:/Users/Eric/Desktop/2011秋冬/Code/Xreducer/data/Data/wdbc.arff";
		  String fn = "C:/Users/Eric/Desktop/2011秋冬/Code/Xreducer/data/Data/glass.arff";
		 // String fn = "C:/Users/Eric/Desktop/2011秋冬/Code/Xreducer/data/shen/wine-shen.arff";
		   //String fn = "C:/Users/Eric/Desktop/2011秋冬/Code/Xreducer/data/fuzzy/fuzzy-ex.arff";
		 //String fn = "C:/Users/Eric/Desktop/2011秋冬/Code/Xreducer/data/derm.arff";
		  oneFile onef = new oneFile(new File(fn));
		  Instances dataset = new Instances(new FileReader(fn));
		  dataset.setClassIndex(dataset.numAttributes()-1); 
		  onef.ins = dataset.numInstances();
		  onef.att = dataset.numAttributes();
		  onef.cla = dataset.numClasses();
			
		  RSandFCBFReduceMethod rs = new RSandFCBFReduceMethod(onef,oneAlg);
		  
		  boolean[] B = new boolean [rs.NumAttr];
		  boolean[] rq = rs.getOneReduction(B);
		  System.out.println(Arrays.toString(Utils.boolean2select(rq)));
	}

}
