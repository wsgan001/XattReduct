package Xreducer_struct;

import java.io.File;
import java.util.Vector;

import weka.classifiers.Classifier;
import weka.classifiers.bayes.NaiveBayes;

public class oneFile {
	
	
	
	public oneFile(File path){
		 
		this.filepath = path;
		if(path!=null){
			String fne = path.getName();
			int eindex =fne.lastIndexOf('.');
			this.filename = fne.substring(0,eindex).toLowerCase();
			
		}
		this.algs = new Vector<oneAlgorithm>();
}
	
	
	public File filepath = null;
	public String filename = null;
	public int baseindex = (Integer) globalValue.baseindex; //»ù×¼Ëã·¨ÐòºÅ
	public double signiifcantlevel = 0.05; 
	public int numReduce = 3;
	public int numRun = 10;
	public Classifier cl = new NaiveBayes();
	public String clname = "NaiveBayes";
	public int numFold = 10;
	public Vector<oneAlgorithm> algs = null;
	public int numcomAlg = -1;
	
	
	
	
	public int NSindex = (Integer) globalValue.NSindex;
	public enum xNSstyle{
		NS_IG("NS_IG"),
		NS_SU("NS_SU"),
		NS_NONE("None");
		private String value=null;
		private xNSstyle(String tag){
			this.value = tag;
		}
		public String getValue(){
			return this.value;
		}
		public static xNSstyle getxNSstyle(String str){
			if(str.equals("NS_IG"))
				return xNSstyle.NS_IG;
			else if(str.equals("NS_SU"))
				return xNSstyle.NS_SU;
			else return xNSstyle.NS_NONE;
		}
	};
	public xNSstyle NSstyle;
	public int NSmaxIndex = 40;
	public Classifier NScl = new NaiveBayes();
	public String NSclname = "NaiveBayes";
	public double[][] NSRes = null;
	public int randomI = 0;
	public String[] keys = null;
	
	
	//public String[] algnames = null;
	public Vector<int[]> selectAtts =  null;
	public double[] redTimes = null;
	public double[][] TtestRes = null;
    
	public int ins = 0;
	public int att = 0;
	public int cla = 0;


	public static void copyOnefile(oneFile res, oneFile des){
		res = new oneFile(des.filepath);
		res.baseindex = des.baseindex;
		res.signiifcantlevel = des.signiifcantlevel;
		res.numRun = des.numRun;
		res.numReduce = des.numReduce;
		res.cl = des.cl;
		res.numFold = des.numFold;
		res.numcomAlg = des.numcomAlg;
		res.algs = new Vector<oneAlgorithm>();
		//res.algs.setSize(res.numcomAlg);
		for(int i=0;i<res.numcomAlg;++i){
			//oneAlgorithm.copy(res.algs.get(i), des.algs.get(i));
			res.algs.add(des.algs.get(i));
		}
		
		
		
		res.NSindex = des.NSindex;
		res.NSstyle = des.NSstyle;
		res.NSmaxIndex = des.NSmaxIndex;
		res.NScl = des.NScl;
		res.NSRes = des.NSRes;
		res.keys = des.keys;
		
		res.ins = des.ins;
		res.att = des.att;
		res.cla = des.cla;		
	}
	
	public static String[] getKeys(xNSstyle x){
		String[] res = null;
		switch(x){
		case NS_IG:{
			String[] keys = {"AC","reduct-IG","D-value-IG"};
			res = keys;
			
		}
		case NS_SU:{
			String[] keys = {"AC","reduct-SU","D-value-SU"};
			res = keys;
		}
		}
		return res;
	}

}
