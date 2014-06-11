package Xreducer_core;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Vector;

import weka.attributeSelection.CfsSubsetEval;
import weka.attributeSelection.ConsistencySubsetEval;
import weka.attributeSelection.GreedyStepwise;
import weka.attributeSelection.Ranker;
import weka.attributeSelection.ReliefFAttributeEval;
import weka.attributeSelection.WrapperSubsetEval;
import weka.classifiers.Classifier;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.functions.LibSVM;
import weka.classifiers.trees.J48;
import weka.classifiers.trees.SimpleCart;
import weka.core.Instances;
import Xreducer_gui.loadFileFrame;
import Xreducer_struct.comFiles;
import Xreducer_struct.globalValue;
import Xreducer_struct.oneAlgRecord;
import Xreducer_struct.oneAlgorithm;
import Xreducer_struct.oneAlgorithm.xTrainClassifier;
import Xreducer_struct.oneDataRecord;
import Xreducer_struct.oneFile;
import Xreducer_struct.oneAlgorithm.xCategory;
import Xreducer_struct.oneAlgorithm.xStyle;
import Xreducer_struct.oneFile.xNSstyle;

public class generateAlgMethod {

	public static void setGlobalcomf(Vector<oneDataRecord> dr){
		globalValue.gcomf = dataRecord2comf(dr);
	}
	
	
	public static comFiles dataRecord2comf(Vector<oneDataRecord> dr){
		int N = dr.size();
		File[] f = new File[N];
		for(int i=0;i<N;++i){
			f[i]=new File(dr.get(i).filepath);
		}
		comFiles cf = new comFiles(f);
		for(int i=0;i<N;++i){
			oneFile af = new oneFile(f[i]);
			oneDataRecord ad = dr.get(i);
			/************* oneDataRecord to oneFile*************/
			af.baseindex = ad.baseindex;
			af.signiifcantlevel = ad.signifcantlevel;
			af.numReduce = ad.numReduce;
			af.numRun = ad.numRun;
			af.clname = ad.trainclname;
			af.cl = xTrainClassifier.getClassifier(af.clname);
			af.numFold = ad.numFold;		
			int M = ad.numcomAlg;
			af.numcomAlg = M;
			/*************algs******************/			
			for(int j=0;j<M;++j){
				oneAlgorithm aa = new oneAlgorithm();
				oneAlgRecord ar = ad.algs.get(j);				
				aa.category = ar.alg_category;
				aa.style = ar.alg_style;
				aa.ID = ar.alg_ID;
				aa.algname = ar.alg_algname;
				aa.alpha = ar.alg_alpha;
				aa.flag = ar.alg_flag;			
				aa.numReduce = ar.alg_numReduce;
				aa.numRun = ar.alg_numRun;
				aa.clname =  ar.alg_clname;
				aa.cl = xTrainClassifier.getClassifier(aa.clname);
				aa.numFold = ar.alg_numFold;					
				aa.eval = oneAlgorithm.Str2Eval(ar.alg_eval,aa.cl);
				aa.evalname = ar.alg_eval;
				aa.search = oneAlgorithm.Str2Search(ar.alg_search,aa.alpha);
				aa.searchname = ar.alg_search;
				af.algs.add(aa);
			}
			af.NSstyle = ad.NSstyle;
			af.NSindex = ad.NSindex;
			af.NSmaxIndex = ad.NSmaxIndex;
			af.NSclname = ad.NSclname;
			af.cl = xTrainClassifier.getClassifier(af.NSclname);
			af.randomI = ad.randomI;
			
			af.ins = ad.ins;
			af.att = ad.att;
			af.cla = ad.cla;
			/*************end***********************/
			//oneFile.copyOnefile(cf.onef.get(i), af);
			cf.onef.add(af);
			
			
			//oneFile.copyOnefile(cf.onef.get(i), af);
		}
		return cf;
	}
	public static void setDefaultAlgs(File f) throws IOException{
		oneFile of = new oneFile(f);
		
		
		Instances dataset = new Instances(new FileReader(f));
		dataset.setClassIndex(dataset.numAttributes()-1); 
		of.ins = dataset.numInstances();
		of.att = dataset.numAttributes();
		of.cla = dataset.numClasses();
		 
   		of.NSstyle = xNSstyle.NS_SU;
   		of.NSindex = globalValue.NSindex;

   		//Classifier cl = new NaiveBayes();
   		//String clname = "NaiveBayes"; 
   		Classifier cl = new J48();
   		String clname = "J48"; 
   		//Classifier cl = new SimpleCart();
   		//String clname = "SimpleCart"; 
   		//Classifier cl = new LibSVM();
   		//String clname = "LibSVM"; 
   		int id = 0;
   		
	       		  oneAlgorithm oneAlg = new oneAlgorithm();
    		 	    
	       		  oneAlg.category = xCategory.RSandFCBFalg;
	       		  oneAlg.style = xStyle.fuzzySU;
	    		  oneAlg.cl = cl;
	    		  oneAlg.clname = clname;
	    		  oneAlg.ID = id++;
	    		  oneAlg.alpha = 4.0;
	    		  oneAlg.algname = "FuzzySU-(4)算法";
	    		  of.algs.add(oneAlg);
	    		  of.baseindex = oneAlg.ID;
	    		  
	    		  
		    		 
	    		 /* oneAlg = new oneAlgorithm();
	       		  oneAlg.category = xCategory.RSandFCBFalg;
	       		  oneAlg.style = xStyle.fuzzySU;
	    		  oneAlg.cl = cl;
	    		  oneAlg.clname = clname;
	    		  oneAlg.ID = id++;
	    		  oneAlg.alpha = 3.0;
	    		  oneAlg.algname = "FuzzySU-(3)算法";	 
	    		  of.algs.add(oneAlg);
	    		  
	    		  
	    		  oneAlg = new oneAlgorithm();
	       		  oneAlg.category = xCategory.RSandFCBFalg;
	       		  oneAlg.style = xStyle.fuzzySU;
	    		  oneAlg.cl = cl;
	    		  oneAlg.clname = clname;
	    		  oneAlg.ID = id++;
	    		  oneAlg.alpha = 2.0;
	    		  oneAlg.algname = "FuzzySU-(2)算法";	 
	    		  of.algs.add(oneAlg); */
	    		  //of.baseindex = oneAlg.ID;
	    		  
	    		  

	    		  
	    		  

   			  
	       		  /*oneAlg = new oneAlgorithm();
	    		  oneAlg.category = xCategory.Wekaalg;
	    		  oneAlg.style = xStyle.NONE;
	    		  oneAlg.cl = cl;
	    		  oneAlg.clname = clname;
	    		  oneAlg.ID = id++;
	    		  oneAlg.eval = new CfsSubsetEval();
	    		  oneAlg.evalname = "CfsSubsetEval";
	    		  oneAlg.search = new GreedyStepwise();
	    		  oneAlg.searchname = "GreedyStepwise";
	    		  oneAlg.algname = "CfsSubsetEval算法";	 
	    		  of.algs.add(oneAlg);	    		  

	       		  oneAlg = new oneAlgorithm();
	       		  oneAlg.category = xCategory.Wekaalg;
	       		  oneAlg.style = xStyle.NONE;
	    		  oneAlg.cl = cl;
	    		  oneAlg.clname = clname;
	    		  oneAlg.ID = id++;
	    		  oneAlg.eval = new ConsistencySubsetEval();
	    		  oneAlg.evalname = "ConsistencySubsetEval";
	    		  oneAlg.search = new GreedyStepwise();
	    		  oneAlg.searchname = "GreedyStepwise";
	    		  oneAlg.algname = "Consistency算法";	 
	    		  of.algs.add(oneAlg);    		  

	       		  oneAlg = new oneAlgorithm();
	       		  oneAlg.category = xCategory.Wekaalg;
	       		  oneAlg.style = xStyle.NONE;
	    		  oneAlg.cl = cl;
	    		  oneAlg.clname = clname;
	    		  oneAlg.ID = id++;
	    		  double tau = 1/Math.sqrt(of.signiifcantlevel*(double)of.ins)*0.125;
	    		  Ranker se = new Ranker();
	    		  se.setThreshold(tau);
	    		  oneAlg.alpha = tau;
	    		  oneAlg.eval = new ReliefFAttributeEval();
	    		  oneAlg.evalname = "ReliefFAttributeEval";
	    		  oneAlg.search = se;
	    		  oneAlg.searchname = "Ranker";
	    		  oneAlg.algname = "ReliefF算法";	 
	    		  of.algs.add(oneAlg);	    	*/	  
	  	   /* algnames[4] = "Wrapper算法"; 
	       		  oneAlg = new oneAlgorithm();
	       		  oneAlg.category = xCategory.Wekaalg;
	       		  oneAlg.style = xStyle.NONE;
	       		  oneAlg.cl = cl;
	    		  oneAlg.clname = clname;
	    		  oneAlg.ID = 4;
	    		  WrapperSubsetEval wa = new WrapperSubsetEval();
	    		  oneAlg.evalname = "WrapperSubsetEval";
	    		  wa.setClassifier(of.cl);
	    		  oneAlg.eval = wa;
	    		  oneAlg.search = new GreedyStepwise();
	    		  oneAlg.searchname = "GreedyStepwise";
	    		  oneAlg.algname = "Wrapper算法";	 
	    		  of.algs.add(oneAlg);	  
	    	
	       		  oneAlg = new oneAlgorithm();
	       		  oneAlg.category = xCategory.Roughsetalg;
	       		  oneAlg.style = xStyle.conditionentropy;
	    		  oneAlg.cl = cl;
	    		  oneAlg.clname = clname;
	    		  oneAlg.ID = id++;
	    		  oneAlg.flag = true;
	    		  oneAlg.alpha = 0;
	    		  oneAlg.algname = "条件熵";	 
	    		  of.algs.add(oneAlg);	

	     		  oneAlg = new oneAlgorithm();
	       		  oneAlg.category = xCategory.Roughsetalg;
	       		  oneAlg.style = xStyle.positive_RSAR;
	    		  oneAlg.cl = cl;
	    		  oneAlg.clname = clname;
	    		  oneAlg.ID = id++;
	    		  oneAlg.flag = true;
	    		  oneAlg.alpha = 0;
	    		  oneAlg.algname = "正域";	 
	    		  of.algs.add(oneAlg);

	     		  oneAlg = new oneAlgorithm();
	       		  oneAlg.category = xCategory.FCBFalg;
	       		  oneAlg.style = xStyle.SU;
	    		  oneAlg.cl = cl;
	    		  oneAlg.clname = clname;
	    		  oneAlg.ID = id++;
	    		  oneAlg.alpha = 0;
	    		  oneAlg.algname = "FCBF[SU](0)算法";	 
	    		  of.algs.add(oneAlg);	*/
	  	  	/* algnames[8] = "FCBF[SU](log)算法"; 
	     		  oneAlg = new oneAlgorithm();
	       		  oneAlg.category = xCategory.FCBFalg;
	       		  oneAlg.style = xStyle.SU;
	       		  	    		  oneAlg.cl = cl;
	    		  oneAlg.clname = clname;
	    		  oneAlg.ID = id++;
	    		  oneAlg.alpha = -1.0; //log
	    		  oneAlg.algname = "FCBF[SU](log)算法";	 
	    		  of.algs.add(oneAlg);	
	  	  	 algnames[9] = "FCBF[CAIR](0)算法"; 
	     		  oneAlg = new oneAlgorithm();
	       		  oneAlg.category = xCategory.FCBFalg;
	       		  oneAlg.style = xStyle.CAIR;
	       		  oneAlg.cl = cl;
	    		  oneAlg.clname = clname;
	    		  oneAlg.ID = id++;
	    		  oneAlg.alpha = 0;
	    		  oneAlg.algname = "FCBF[CAIR](0)算法";	 */
	    		  
	    		 /* oneAlg = new oneAlgorithm();
	       		  oneAlg.category = xCategory.NibbleRR;
	       		  oneAlg.style = xStyle.SU;
	       		  oneAlg.cl = cl;
	    		  oneAlg.clname = clname;
	    		  oneAlg.ID = id++;
	    		  oneAlg.alpha = 0.6;
	    		  oneAlg.algname = "NibbleRR算法";	 
	    		  of.algs.add(oneAlg);	*/
	    		  
	    		
	    		/*   oneAlg = new oneAlgorithm();
	       		  oneAlg.category = xCategory.Roughsetalg;
	       		  oneAlg.style = xStyle.fuzzyPositive_Low;
	    		  oneAlg.cl = cl;
	    		  oneAlg.clname = clname;
	    		  oneAlg.ID = id++;
	    		  oneAlg.alpha = 0;
	    		  oneAlg.flag = false; //不进行回溯
	    		  oneAlg.algname = "L-FRFS算法";	 
	    		  of.algs.add(oneAlg);
	    		  
	    		  oneAlg = new oneAlgorithm();
	       		  oneAlg.category = xCategory.Roughsetalg;
	       		  oneAlg.style = xStyle.fuzzyPositive_Boundary;
	    		  oneAlg.cl = cl;
	    		  oneAlg.clname = clname;
	    		  oneAlg.ID = id++;
	    		  oneAlg.alpha = 0;
	    		  oneAlg.flag = false; //不进行回溯
	    		  oneAlg.algname = "B-FRFS算法";	 
	    		  of.algs.add(oneAlg);*/
	    		   
	    		   oneAlg = new oneAlgorithm();
	       		  oneAlg.category = xCategory.Roughsetalg;
	       		  oneAlg.style = xStyle.fuzzyEntorpy_EFRFS;
	    		  oneAlg.cl = cl;
	    		  oneAlg.clname = clname;
	    		  oneAlg.ID = id++;
	    		  oneAlg.alpha = 0;
	    		  oneAlg.flag = false; //不进行回溯
	    		  oneAlg.algname = "E-FRFS算法";	 
	    		  of.algs.add(oneAlg);
	    		  
	    		  
	    		 /*oneAlg = new oneAlgorithm();
	       		  oneAlg.category = xCategory.Roughsetalg;
	       		  oneAlg.style = xStyle.fuzzyset_FRFS;
	    		  oneAlg.cl = cl;
	    		  oneAlg.clname = clname;
	    		  oneAlg.ID = id++;
	    		  oneAlg.alpha = 0;
	    		  oneAlg.flag = false; //不进行回溯
	    		  oneAlg.algname = "FRFS算法";	 
	    		  of.algs.add(oneAlg);*/
	    		  
	    		  
	    		  oneAlg = new oneAlgorithm();
	       		  oneAlg.category = xCategory.Roughsetalg;
	       		  oneAlg.style = xStyle.fuzzyCEntorpy_FHFS;
	    		  oneAlg.cl = cl;
	    		  oneAlg.clname = clname;
	    		  oneAlg.ID = id++;
	    		  oneAlg.alpha = 0;
	    		  oneAlg.flag = false; //不进行回溯
	    		  oneAlg.algname = "FCEFS算法";	 
	    		  of.algs.add(oneAlg);
	    		  //of.baseindex = oneAlg.ID;
	    		  

	    		  oneAlg = new oneAlgorithm();
	    		  oneAlg.category = xCategory.Fullset;
	    		  oneAlg.cl = cl;
	    		  oneAlg.clname = clname;
	    		  oneAlg.style = xStyle.NONE;
	    		  oneAlg.ID = id++;
	    		  oneAlg.algname = "全集";    		 
	    		  of.algs.add(oneAlg);  
	    		  
	    		  
	    		  
	    		  of.NSindex = -1;
	    		  of.numcomAlg = of.algs.size();
		globalValue.gcomf.onef.add(of);
		globalValue.gcomf.numFile++;
	}
	
	public static void deleteFile(int row){
		//int sindex = this.jt_datainfo.getSelectedRow();
		globalValue.gcomf.onef.remove(row);
		globalValue.gcomf.numFile--;
	}
	public static void deleteAlg(int fileindex,int row){
		//int sindex = this.jt_datainfo.getSelectedRow();
		globalValue.gcomf.onef.get(fileindex).algs.remove(row);
		globalValue.gcomf.onef.get(fileindex).numcomAlg--;
	}
	public static void deleteFile(int row,int count){
		//int sindex = this.jt_datainfo.getSelectedRow();
        for (int i = 0; i<count; i++) { 
            if (globalValue.gcomf.onef.size() > row) { 
            	globalValue.gcomf.onef.remove(row); 
            	globalValue.gcomf.numFile--;
            } 
        } 
	}
	
	public static void copyOneFile(comFiles res, int i, oneFile desfile){
		oneFile.copyOnefile(res.onef.get(i),desfile);
	}
}
