package Xreducer_core;

import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;

import weka.core.Instances;
import Xreducer_struct.oneAlgorithm;
import Xreducer_struct.oneAlgorithm.xCategory;
import Xreducer_struct.oneAlgorithm.xStyle;
import Xreducer_struct.oneFile;
import Xreducer_struct.oneFile.xNSstyle;

public class NibbleAlgMethod {
	public Instances OriginalData = null;
	public Instances DiscretizeData = null;
	public xStyle style ;
	public oneAlgorithm alg = null;
	public oneAlgorithm reduceAlg = null;
	private double gamma = -1;
	public int Maxnum = 0;
	public int NumAtt = 0;
	public oneFile afile = null;
	
	
	public NibbleAlgMethod(oneFile afile, oneAlgorithm alg, oneAlgorithm reduceAlg) throws Exception{

		this.afile = afile;
		this.alg = alg;
		this.reduceAlg = reduceAlg;
		this.gamma = alg.alpha;
		this.style = alg.style;
		wekaDiscretizeMethod dm = new wekaDiscretizeMethod(afile.filepath,true);
		this.OriginalData = dm.getOriginalData();
		this.DiscretizeData = dm.getDiscretizeData();
		this.Maxnum = (int)((afile.att-1)*this.gamma);
		this.NumAtt = afile.att;
	}
	//‘ººÚµƒ∆¿π¿∫Ø ˝
	public boolean NSevaluationfunction(Instances data, boolean[] oldB, boolean[] newB, int ith) throws Exception{
		boolean[] D = Utils.Instances2DecBoolean(data);
		double H_oldB = Utils.getEvaluateValue(xStyle.informationEntropy, data, null, oldB);
		double H_D = Utils.getEvaluateValue(xStyle.informationEntropy, data, null, D);
		boolean[] addB = Utils.boolsSubtract(newB, oldB);
		double addB2D = Utils.getEvaluateValue(this.style, data, D, addB);
		double addB2oldB = Utils.getEvaluateValue(this.style, data, oldB, addB);
		double k = 0.0;
		k = Utils.booleanSelectedNum(oldB); //SU(old,new)/|old|>=SU(d,new)
		k =  H_oldB;
		//System.out.println(ith+":"+":"+addB2oldB/k+":-----"+addB2oldB+"##"+addB2D);
		//System.out.println(Utils.boolean2select(oldB).length+":"+Utils.boolean2select(addB).length);
		if((double)(addB2oldB/k)>=addB2D) 
			return true;	
		else return false;
	}
	public boolean Run() throws Exception{
		xCategory category = this.reduceAlg.category;
		ReduceMethod rm = null;
		switch(category){
		case Wekaalg:{ //weka algorithm
			rm = new  wekaReduceMethod(this.afile,this.reduceAlg);
			break;
		}

		case Roughsetalg:{ //roughset algorithm
			rm = new  roughSetMethod(this.afile,this.reduceAlg);
			break;
		}
		case FCBFalg:{ //roughset algorithm
			rm = new  FCBFReduceMethod(this.afile,this.reduceAlg);
			break;
		}
		default: break;
		}
		
		long t_start=System.currentTimeMillis();
		long t_end=0;
		Date now = new Date();
		this.alg.startTime = DateFormat.getTimeInstance().format(now); 
		
		boolean[] B = new boolean[this.NumAtt];
		B = rm.getOneReduction(B);	
		int currentNum = 0;
		int ith = 0;
		do{
			ith++;
			rm.reLoadDataset(B);
			boolean[] newB = rm.getOneReduction(B);			
			if (NSevaluationfunction(rm.getData(),B,newB,ith)){//ºÏ≤‚»ﬂ”‡
				break;
			}
			B = newB.clone();
			currentNum = Utils.booleanSelectedNum(B);
		}
		while(currentNum<this.Maxnum);
		
		
		t_end=System.currentTimeMillis();
		this.alg.redTime = (t_end-t_start)/(double)(1000*this.alg.numReduce);
		
		t_start=System.currentTimeMillis();
		t_end=0;
		this.alg.selectedAtt = Utils.boolean2select(B);
		Instances newData = wekaDiscretizeMethod.getSelectedInstances(this.OriginalData,B);
        //—µ¡∑‘§≤‚◊º»∑¬ 
		this.alg.ACs = Utils.multicrossValidateModel(this.alg.cl, newData, this.alg.numRun,this.alg.numFold );
		
		
		t_end=System.currentTimeMillis();
		this.alg.trainTime = (t_end-t_start)/1000.0;
		this.alg.endTime = DateFormat.getTimeInstance().format(now); 
		return true;

	}
	public oneAlgorithm getAlg(){
		return this.alg;		
	}
}
