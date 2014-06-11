package Xreducer_core;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Random;

import org.apache.commons.math.stat.descriptive.moment.Mean;
import org.apache.commons.math.stat.descriptive.moment.StandardDeviation;
import org.apache.commons.math.stat.inference.TestUtils;

import Xreducer_struct.oneAlgorithm.xStyle;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.functions.LibSVM;
import weka.classifiers.functions.RBFNetwork;
import weka.classifiers.meta.Bagging;
import weka.classifiers.rules.PART;
import weka.classifiers.trees.J48;
import weka.classifiers.trees.RandomForest;
import weka.classifiers.trees.SimpleCart;
import weka.core.Instances;
import weka.core.Range;

public class AttributeBaggingMethod {
	public int m_ensemblesize = 0;
	public Classifier[] m_classifiers = null;
	public boolean[][] m_selectedAttribute = null; //最后一位为true 决策属性
	public double[] m_weigted = null;
	public Instances m_traindata = null;
	public int m_numAtt = 0;
	public int m_numIns = 0;
	public int m_numCla = 0;
	public int m_seed = 0;
	public xABweightstyle m_style = null;
	
	public enum xABweightstyle{
		noweight("noweight"),
		accuracy("accuracy"),
		su("su"),
		susingle("susingle");
		
		private String tag=null;
		private xABweightstyle(String tag){
			this.tag = tag;
		}
		public String getValue(){
			return tag;
		}
		public static xABweightstyle xABweightstyle(String str){
			if(str.equals("noweight")){
				return xABweightstyle.noweight;
				}
			if(str.equals("accuracy")){
				return xABweightstyle.accuracy;}
			if(str.equals("su")){
				return xABweightstyle.su;}
			if(str.equals("susingle")){
				return xABweightstyle.susingle;}
			else return xABweightstyle.noweight;
		}
	}
	
	public AttributeBaggingMethod(Instances traindata, int Ensemblesize,Classifier cl,xABweightstyle abs,int seed) throws Exception{
		//过滤属性空间后 对相同对象的处理 wekaDiscretizeMethod.getSelectedInstances?
		this.m_traindata = traindata;
		this.m_numAtt = m_traindata.numAttributes();
		this.m_numCla = m_traindata.numClasses();
		this.m_ensemblesize = Ensemblesize;
		//for(int i=0;i<Ensemblesize;++i){
			//m_classifiers[i] = Classifier.makeCopy(cl);
			//m_classifiers[i] = new NaiveBayes();
		//}
		this.m_style = abs;
		this.m_seed = seed;
		this.m_classifiers = Classifier.makeCopies(cl, this.m_ensemblesize);
		this.m_selectedAttribute = getSelectedAttributes(this.m_ensemblesize,this.m_numAtt);
		classifierBuild ();
		this.m_weigted = getWeigtedValues(this.m_selectedAttribute);

	}

	public void classifierBuild () throws Exception{
		for(int i=0;i<this.m_ensemblesize;++i){
			Instances newData = wekaDiscretizeMethod.getSelectedInstances(this.m_traindata,this.m_selectedAttribute[i]);
			m_classifiers[i].buildClassifier(newData);
		}
	}
    public boolean[][] getSelectedAttributes(int es, int att){
    	boolean[][] sa = new boolean[es][att];
    	int max = Math.min(es, m_numAtt-1);
    	Random rnd = new Random(this.m_seed);
    	for(int i=0;i<es;++i){
    		for(int k=0;k<max;++k){
    			sa[i][k] = rnd.nextBoolean();
    		}
    		sa[i][this.m_numAtt-1] = true;
    	}
    	return sa;
    }
    public double[] getWeigtedValues(boolean[][] sa) throws Exception {
		// TODO Auto-generated method stub
    	double[] weigts = new double[sa.length];
    	boolean[] D = Utils.Instances2DecBoolean(m_traindata);
    	switch(this.m_style){
    	case noweight:{
    		for(int i=0;i<sa.length;++i){
    			weigts[i] = 1.0;
    		}
    		break;
    	}
    	case accuracy:{ 		
    		for(int i=0;i<sa.length;++i){  			
    			Instances newData = wekaDiscretizeMethod.getSelectedInstances(this.m_traindata,sa[i]);
    			Evaluation ev = new Evaluation(newData);
    			ev.evaluateModel(this.m_classifiers[i], newData);
    			weigts[i] = 1-ev.errorRate();
    			//double[] ans = classifyInstances(newData);
        		//for(int j=0;j<m_traindata.numInstances();++j){
            		//double ans = m_classifiers[i].classifyInstance(newData.instance(j));
            		//resLabel[j][labeli] += this.m_weigted[i];
        		//}

    		}
    		break;
    	}
    	case su:{
        	for(int i=0;i<sa.length;++i){
        		boolean[] B = sa[i].clone();
        		B[this.m_numAtt-1] = false;
        		//Instances newData = wekaDiscretizeMethod.getSelectedInstances(this.m_traindata,sa[i]);
        		weigts[i]=Utils.getEvaluateValue(xStyle.SU, this.m_traindata, D, B);
        	}
    		break;
    	}
    	case susingle:{
        	for(int i=0;i<sa.length;++i){
        		boolean[] B = new boolean[this.m_traindata.numAttributes()];
        		double sum = 0.0;
        		for(int j=0;j<sa[i].length-1;++j){
        			if(sa[i][j]){
        				B[j]=true;
        				sum += Utils.getEvaluateValue(xStyle.SU, this.m_traindata, D, B);
        				B[j]=false;
        			}
        			
        		}
        		weigts[i]=sum/(double)(Utils.booleanSelectedNum(sa[i])-1);
        	}
    		break;
    	}
    	default:break;
    	}

		return weigts;
	}
    public double[] classifyInstances (Instances testData) throws Exception{
    	double[][] resLabel = new double[testData.numInstances()][testData.numClasses()];
    	double[] ans = new double[testData.numInstances()];
    	for(int i=0;i<this.m_ensemblesize;++i){
    		Instances newData = wekaDiscretizeMethod.getSelectedInstances(testData,this.m_selectedAttribute[i]);
    		for(int j=0;j<testData.numInstances();++j){
        		int labeli = (int)m_classifiers[i].classifyInstance(newData.instance(j));
        		resLabel[j][labeli] += this.m_weigted[i];
    		}
    	}
    	for(int j=0;j<testData.numInstances();++j){
    		ans[j] = getMaxIndex(resLabel[j]);
    	}
		return ans;
    	
    }
	private double getMaxIndex(double[] ds) {
		// TODO Auto-generated method stub
		int maxindex = 0;
		double maxvalue = -1.0;
		for(int i=0;i<ds.length;++i){
			if(maxvalue < ds[i]){
				maxindex = i;
				maxvalue = ds[i];
			}
		}
		return maxindex;
	}
	public double getAC(Instances testData) throws Exception{
		double[] ans = classifyInstances(testData);
		int correctnum = 0;
    	for(int j=0;j<testData.numInstances();++j){
    		if(ans[j]==testData.instance(j).classValue())
    			correctnum ++;
    	}
		return (double)correctnum/(double)testData.numInstances();
	}
	public static double getAC(Classifier cl,Instances testData) throws Exception{
		int correctnum = 0;
    	for(int j=0;j<testData.numInstances();++j){
    		if(cl.classifyInstance(testData.instance(j))==testData.instance(j).classValue())
    			correctnum ++;
    	}
		return (double)correctnum/(double)testData.numInstances();
	}
	static public double[] onecrossValidateModel(Instances data,  Classifier cl,  int numFolds, int randomI) throws Exception{ 	
		data = new Instances(data);
	    data.randomize(new Random(randomI));
	    if (data.classAttribute().isNominal()) {
	      data.stratify(numFolds);
	    }
	    // Do the folds
	    double d = 0.0;
	    long traintime = 0;
	    long testtime = 0;
	    for (int i = 0; i < numFolds; i++) {
	      Instances train = data.trainCV(numFolds, i, new Random(randomI));
	      long temp = Utils.getCurrenttime();
	      cl.buildClassifier(train);
	      traintime += Utils.getCurrenttime()-temp;
	      Instances test = data.testCV(numFolds, i);
	      temp = Utils.getCurrenttime();
	      d += getAC(cl,test);
	      testtime += Utils.getCurrenttime()-temp;
	    }
	    //System.out.println(traintime+":"+testtime);
	    double[] ans = new double[3];
	    ans[0]= d/(double)numFolds;
	    ans[1]=traintime/(double)numFolds;
	    ans[2]=testtime/(double)numFolds;
	    return ans;

	}
	static public double[] onecrossValidateModel(Instances data, int Ensemblesize,Classifier cl,xABweightstyle abs, int numFolds, int randomI) throws Exception{ 	
		data = new Instances(data);
	    data.randomize(new Random(randomI));
	    if (data.classAttribute().isNominal()) {
	      data.stratify(numFolds);
	    }
	    // Do the folds
	    double d = 0.0;
	    long traintime = 0;
	    long testtime = 0;
	    for (int i = 0; i < numFolds; i++) {
	      Instances train = data.trainCV(numFolds, i, new Random(randomI));
	      long temp = Utils.getCurrenttime();
	      AttributeBaggingMethod ab = new AttributeBaggingMethod(train,Ensemblesize,cl,abs,randomI);
	      traintime += Utils.getCurrenttime()-temp;
	      Instances test = data.testCV(numFolds, i);
	      temp = Utils.getCurrenttime();
	      d += ab.getAC(test);
	      testtime += Utils.getCurrenttime()-temp;
	    }
	    //System.out.println(traintime+":"+testtime);
	    double[] ans = new double[3];
	    ans[0]= d/(double)numFolds;
	    ans[1]=traintime/(double)numFolds;
	    ans[2]=testtime/(double)numFolds;
	    return ans;

	}
	public static double[][] MypairedTtest(double[][] X, int baseindex , double singificantlevel) throws Exception{
		int N = X.length;
		//int M = X[0].length;
		double[][] res = new double[N][4];
		Mean mean = new Mean(); // 算术平均值
		//Variance variance = new Variance(); // 方差
		StandardDeviation sd = new StandardDeviation();// 标准方差
		//计算均值方差写入res[0] res[1]中
		for(int i=0;i<N;++i){
			res[i][0] = mean.evaluate(X[i]);
			res[i][1] = sd.evaluate(X[i]);
		}
		//DecimalFormat df = new DecimalFormat("0.000000");

		//进行paried t-test
		for(int i=0;i<N;++i){
			double pval;
			boolean H;
			if(i!=baseindex){
					pval = TestUtils.pairedTTest(X[baseindex], X[i]);
					H = TestUtils.pairedTTest(X[baseindex], X[i], singificantlevel);
					if(!H){//没有显著差异，平局
						res[i][2]=3;
						res[i][3]=pval;
					}
					else{//有显著差异, 均值大的获胜
						res[i][2]=res[baseindex][0]>=res[i][0]?1:2;
						res[i][3]=pval;
					}
					//String num = df.format(pval);
					//System.out.println(num);
			}
		}
		res[baseindex][2]=-1;
		res[baseindex][3]=-1;
		return res;
	}
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		/*String fn = "C:/Users/Eric/Desktop/2011秋冬/GestureRecognition/paper/gr.arff";
		Instances data = new Instances(new FileReader(fn));
		data.setClassIndex(data.numAttributes()-1);
		int runtimes = 10;
		int ensemblesize = 20;
		//ABSU
		double[] resx = new double[runtimes];
		 System.out.println("ABSU算法"+"ensemblesize="+ensemblesize);
		Classifier clx = new weka.classifiers.trees.REPTree();
		double traintimex = 0.0;
		double testtimex = 0.0;
		for(int i=0;i<runtimes;++i){
			double[] temp = new double[3];
			temp = onecrossValidateModel(data,ensemblesize,clx,xABweightstyle.su,10,i);
			resx[i] = temp[0];
			traintimex += temp[1];
			testtimex += temp[2];
			//System.out.println(i);
 
		}
		traintimex = traintimex/(double)runtimes;
		testtimex = testtimex/(double)runtimes;
		double meanx = new Mean().evaluate(resx);
		double stdx = new StandardDeviation().evaluate(resx);
		DecimalFormat dfx = new DecimalFormat( "0.0000" ); 
		//System.out.println(dfx.format(meanx)+":"+dfx.format(stdx)+":"+dfx.format(traintimex)+":"+dfx.format(testtimex));
		//System.out.println(Arrays.toString(resx));
		System.out.println("平均识别率："+dfx.format(meanx)+" 方差："+dfx.format(stdx)+" 训练时间:"+dfx.format(traintimex)+" 预测时间:"+dfx.format(testtimex));
		System.out.println("运行10次算法各个识别率");
		System.out.println(Arrays.toString(resx));
		System.out.println("运行结束。"); */
		
		 
		/*Classifier cl = new RBFNetwork();//svm
		System.out.println("RBFNetwork算法");
		double[] res = new double[runtimes];
		double traintime = 0.0;
		double testtime = 0.0;
		for(int i=0;i<runtimes;++i){
			double[] temp = new double[3];
			temp = onecrossValidateModel(data,cl,10,i);
			res[i] = temp[0];
			traintime += temp[1];
			testtime += temp[2];
			//System.out.println(i);
		}
		traintime = traintime/(double)runtimes;
		testtime = testtime/(double)runtimes;
		double mean = new Mean().evaluate(res);
		double std = new StandardDeviation().evaluate(res);
		DecimalFormat df = new DecimalFormat( "0.0000" ); 
		System.out.println("平均识别率："+df.format(mean)+" 方差："+df.format(std)+" 训练时间:"+df.format(traintime)+" 预测时间:"+df.format(testtime));
		System.out.println("运行10次算法各个识别率");
		System.out.println(Arrays.toString(res));
		System.out.println("运行结束。"); */
		/*double [][] res = {
				{0.9814999999999999,0.9844999999999999,0.9824999999999999,0.9804999999999999,0.9785,0.9824999999999999,0.976,0.9795,0.974,0.9744999999999999}	,
				{0.9819999999999999, 0.9834999999999999, 0.9809999999999999, 0.9800000000000001, 0.9770000000000001, 0.9800000000000001, 0.9754999999999999, 0.9785, 0.9724999999999999, 0.9739999999999999},
				{0.9625, 0.96, 0.9665000000000001, 0.9615, 0.9629999999999999, 0.9719999999999999, 0.9675, 0.9644999999999999, 0.9624999999999998, 0.9625},
				{0.9640000000000001, 0.9644999999999999, 0.9625, 0.962, 0.9569999999999999, 0.9635, 0.9644999999999998, 0.9650000000000001, 0.9609999999999997, 0.9605},
				{0.9145, 0.9130000000000003, 0.9155000000000001, 0.916, 0.9129999999999999, 0.914, 0.9165000000000001, 0.9185000000000001, 0.9119999999999999, 0.915},
				{0.9630000000000001, 0.9530000000000001, 0.958, 0.9595, 0.9574999999999999, 0.9645000000000001, 0.9630000000000001, 0.9545, 0.96, 0.9555},
				{0.96, 0.9635000000000001, 0.9664999999999999, 0.9594999999999999, 0.9664999999999999, 0.968, 0.9674999999999999, 0.9620000000000001, 0.9620000000000001, 0.967},
				{0.9364999999999999, 0.9454999999999998, 0.95, 0.9455, 0.9514999999999999, 0.9534999999999998, 0.9550000000000001, 0.9545, 0.951, 0.9564999999999999},
				};*/
		
		/*double [][] res = {
		{0.9814999999999999,0.9844999999999999,0.9824999999999999,0.9804999999999999,0.9785,0.9824999999999999,0.976,0.9795,0.974,0.9744999999999999}	,
		{0.9819999999999999, 0.9834999999999999, 0.9809999999999999, 0.9800000000000001, 0.9770000000000001, 0.9800000000000001, 0.9754999999999999, 0.9785, 0.9724999999999999, 0.9739999999999999},
		{0.9725000000000001, 0.9749999999999999, 0.969, 0.975, 0.9685, 0.9650000000000001, 0.9674999999999999, 0.9720000000000001, 0.9775, 0.975},
		{0.9729999999999999, 0.9775, 0.97, 0.9734999999999999, 0.9690000000000001, 0.9620000000000001, 0.9704999999999998, 0.9734999999999999, 0.9760000000000002, 0.9734999999999999},
		{0.9810000000000001, 0.9824999999999999, 0.9804999999999999, 0.982, 0.9780000000000001, 0.9845, 0.9805000000000001, 0.9809999999999999, 0.977, 0.983},
		{0.9819999999999999, 0.983, 0.9809999999999999, 0.9814999999999999, 0.9770000000000001, 0.985, 0.9800000000000001, 0.9795, 0.9759999999999998, 0.9834999999999999}};*/
		double [][] res = {		
		{0.793, 0.7829999999999999, 0.771, 0.5825, 0.8205, 0.685, 0.8029999999999999, 0.827, 0.8185, 0.7745},
		{0.8074999999999999, 0.7935000000000001, 0.7825, 0.621, 0.8215, 0.6955, 0.8285, 0.825, 0.8265, 0.792},
		{0.8245000000000001, 0.8305, 0.8324999999999999, 0.8230000000000001, 0.829, 0.8305, 0.8315000000000001, 0.826, 0.8344999999999999, 0.835},
		{0.8314999999999999, 0.8285, 0.8409999999999999, 0.835, 0.8334999999999999, 0.834, 0.8385, 0.8295000000000001, 0.8354999999999999, 0.8334999999999999},
		{0.7274999999999999, 0.7280000000000001, 0.7249999999999999, 0.7265, 0.7249999999999999, 0.725, 0.7264999999999999, 0.7275, 0.7255, 0.7269999999999999},
		{0.8254999999999999, 0.8314999999999999, 0.8219999999999998, 0.8244999999999999, 0.8259999999999998, 0.8234999999999999, 0.8185, 0.8150000000000002, 0.8334999999999999, 0.8320000000000002},
		{0.8275, 0.8225, 0.825, 0.8285, 0.825, 0.8220000000000001, 0.834, 0.8215, 0.8375, 0.8344999999999999}	,
		{0.759, 0.7655000000000001, 0.7655000000000001, 0.7699999999999999, 0.7735000000000001, 0.7699999999999999, 0.7665, 0.7719999999999999, 0.7689999999999999, 0.7625}};
			
					
					
					
		double[][] ans = MypairedTtest(res,0,0.05);
		DecimalFormat df = new DecimalFormat( "0.0000" ); 
		for(int i=0;i<ans.length;++i){
			 
				System.out.println(df.format(ans[i][2])+"  :  "+df.format(ans[i][3]));
			//System.out.println(Arrays.toString(ans[i]));
		}
	}

}
