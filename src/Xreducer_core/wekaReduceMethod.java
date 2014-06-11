package Xreducer_core;

import java.io.IOException;
import java.text.DateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Vector;

import Xreducer_struct.oneAlgorithm;
import Xreducer_struct.oneFile;

import weka.attributeSelection.ASEvaluation;
import weka.attributeSelection.ASSearch;
import weka.attributeSelection.AttributeSelection;
import weka.classifiers.Classifier;
import weka.core.Instances;

public class wekaReduceMethod extends ReduceMethod{


	
	public oneAlgorithm alg = null;
	private AttributeSelection  AS=null;
	public Instances OriginalData = null;
	public Instances DiscretizeData = null;
	public oneFile afile = null;
	
	public wekaReduceMethod(oneFile afile, oneAlgorithm alg) throws IOException, Exception{
		this.afile = afile;
		this.alg = alg;
		wekaDiscretizeMethod dm = new wekaDiscretizeMethod(afile.filepath,true);
		this.OriginalData = dm.getOriginalData();
		this.DiscretizeData = dm.getDiscretizeData();
		if(this.alg.eval!=null && this.alg.search!=null){
			AS = new AttributeSelection();
			AS.setEvaluator(this.alg.eval);  
			AS.setSearch(this.alg.search); 
		}
	}
	public boolean[] getOneReduction(boolean[] B) throws Exception {
		
		if(Utils.isAllTrue(B))
			return B;
		Instances newData = wekaDiscretizeMethod.getUnSelectedInstances(this.OriginalData, B);
		//System.out.println(this.dataset.numAttributes()+"$$$$$"+newData.numAttributes());
		boolean[] newB = B.clone();
		AS.SelectAttributes(newData);   
		int[] selectedAtt = AS.selectedAttributes();
		for(int i=0;i<selectedAtt.length-1;++i){ //weka算法selectedAtt 包括决策属性 
			newB[selectedAtt[i]]=true;
		}
		//System.out.println(Arrays.toString(selectedAtt));
		return newB;
	}
	public void reLoadDataset(boolean[] B){
		this.OriginalData = wekaDiscretizeMethod.filterDataSet(this.OriginalData,B);
	}
	public Instances getData() {
		// TODO Auto-generated method stub
		return this.OriginalData;
	}
	public boolean Run() throws Exception{
		
		
		long t_start=System.currentTimeMillis();
		long t_end=0;
		Date now = new Date();
		this.alg.startTime = DateFormat.getTimeInstance().format(now); 
		
		AS.SelectAttributes(this.OriginalData);   
		this.alg.selectedAtt = AS.selectedAttributes();
		for (int i = 0;i<this.alg.numReduce-1;++i){
			AS.SelectAttributes(this.OriginalData);   
			AS.selectedAttributes();
		}
		t_end=System.currentTimeMillis();
		this.alg.redTime = (t_end-t_start)/(double)(1000*this.alg.numReduce);
		
		t_start=System.currentTimeMillis();
		t_end=0;
		Instances newData = AS.reduceDimensionality(this.OriginalData);
		//训练
		this.alg.ACs = Utils.multicrossValidateModel(this.alg.cl, newData, this.alg.numRun,this.alg.numFold );	 
		t_end=System.currentTimeMillis();
		this.alg.trainTime = (t_end-t_start)/1000.0;
		//全部结束
		this.alg.endTime = DateFormat.getTimeInstance().format(now); 
		return true;
	}

	public oneAlgorithm getAlg(){
			return this.alg;		
	}

	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
