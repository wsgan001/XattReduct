package CheckMonotony;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Random;

import weka.core.Instances;
import weka.datagenerators.classifiers.classification.RDG1;
import weka.datagenerators.classifiers.classification.RandomRBF;
import weka.filters.Filter;
import CheckMonotony.Entropy.newEntropy;
import Xreducer_core.Utils;
import Xreducer_core.Utils_entropy;
import Xreducer_fuzzy.ITStyle_KleeneDienes;
import Xreducer_fuzzy.SStyle_MaxMin;
import Xreducer_fuzzy.Utils_fuzzy;

public class CheckMonotony {

	/**
	 * @param args
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	public static int[] generateRandomArrays(int n, int m,int rndint){
		int[] ans = new int[n];
		Random rnd = new Random(rndint);
		for(int i=0;i<n;++i){
			ans[i]=i;
		}
		for(int i=0;i<m;++i){
			int a = rnd.nextInt(n);
			int b = rnd.nextInt(n);
			int tmp = ans[a];
			ans[a] = ans[b];
			ans[b] = tmp;
		}
		return ans;
	}
	public static Instances generateInstances(int n,int m,int c,int caseind, int ind ) throws Exception{
		Instances data = null;
		if(caseind==0){
			RDG1 rdg =new RDG1();
			rdg.setNumAttributes(m);
			rdg.setNumExamples(n);
			rdg.setNumClasses(c);
			rdg.setSeed(ind);
			data = rdg.defineDataFormat(); //要先defineDataFormat
			data = rdg.generateExamples();
		}
		else{
			RandomRBF rbf = new RandomRBF();
			rbf.setNumAttributes(m);
			rbf.setNumExamples(n);
			rbf.setNumClasses(c);
			rbf.setSeed(ind);
			data = rbf.defineDataFormat(); //要先defineDataFormat
			data = rbf.generateExamples();
		}
		 
		
		data.setClassIndex(data.numAttributes()-1);
		return data;
	}
	public static double[] calculateH (Instances data , int rndint,newEntropy measure_type) throws Exception{

	
	 int numAttr = data.numAttributes()-1; //无决策属性
	 int[] indexR = generateRandomArrays(numAttr,30,rndint);
	 double[] ansValue = new double[numAttr];
	 boolean[] B = new boolean[numAttr+1];
	 boolean[] D = Utils.Instances2DecBoolean(data);
	 for(int i=0;i<numAttr;++i){
		 B[indexR[i]]=true;
		
		 
	 }
	boolean[] tempB = new boolean[numAttr+1];
	 double pbest = 100000;
		double[] ans = new double[numAttr];
		  for(int k=0;k<numAttr;++k){
			double rbest = 1000000;
			for(int i=0;i<numAttr;++i){ //去掉决策属性
				if(!B[i]){
					B[i]=true;
					double rRandx =  Entropy.getMeasure(data,measure_type,D,B);
					if(Utils.isAllFalse(tempB)||rRandx<rbest){
						tempB = B.clone();
						rbest = rRandx;
					}
					B[i]=false;
				}
			}

			ans[k] = rbest;
			//System.out.println(k);
			B = tempB.clone();
		     
			 
		}
	return ansValue;
	}
	public static boolean check1(Instances data , int rndint ,int caseint) throws Exception{
		weka.filters.supervised.attribute.Discretize sd = new weka.filters.supervised.attribute.Discretize();
		 
			sd.setInputFormat(data);
			Instances dataD = Filter.useFilter(data , sd);
			dataD.setClassIndex(data.numAttributes()-1);
		
		 int numAttr = data.numAttributes()-1; //无决策属性
		 int[] indexR = generateRandomArrays(numAttr,30,rndint);
		 double[] ansValue = new double[numAttr];
		 boolean[] B = new boolean[numAttr+1];
		 boolean[] D = Utils.Instances2DecBoolean(data);
		 for(int i=0;i<numAttr;++i){
			 B[indexR[i]]=true;
			 switch(caseint){
			 case 0:{
				 ansValue[i]=Entropy.New_Covering(data,D,B);
				 break;
			 }
			 case 1:{
				 ansValue[i]=Entropy.New_Partition(dataD,D,B);
				 break;
			 }
			 case 2:{
				 ansValue[i]=Entropy.New_Fuzzy_Version1(data,D,B);
				 break;
			 }
			 case 3:{
				 ansValue[i]=Entropy.Element_Covering(data,D,B);
				 break;
			 }
			 case 4:{
				 ansValue[i]=Entropy.Element_Partition(dataD,D,B);
				 break;
			 }
			 case 5:{
				 ansValue[i]=Entropy.Element_Fuzzy(data,D,B);
				 break;
			 }

			 default:break;
			 }
			
		 }
		 //System.out.println(Arrays.toString(ansValue));
		 boolean tag = true;
		 //for(int i=0;i<numAttr-1;++i){
			 if(ansValue[ansValue.length-3]<ansValue[ansValue.length-2]){
				 tag = false;
				
			 }
		 //}
		
		return tag;
	}
	public static boolean show(Instances data , int rndint ,int caseint) throws Exception{
		weka.filters.supervised.attribute.Discretize sd = new weka.filters.supervised.attribute.Discretize();
		 
			sd.setInputFormat(data);
			Instances dataD = Filter.useFilter(data , sd);
			dataD.setClassIndex(data.numAttributes()-1);
		
		 int numAttr = data.numAttributes()-1; //无决策属性
		 int[] indexR = generateRandomArrays(numAttr,30,rndint);
		 double[] ansValue = new double[numAttr];
		 boolean[] B = new boolean[numAttr+1];
		 boolean[] D = Utils.Instances2DecBoolean(data);
		 for(int i=0;i<numAttr;++i){
			 B[indexR[i]]=true;
			 switch(caseint){
			 case 0:{
				 ansValue[i]=Entropy.New_Covering(data,D,B);
				 break;
			 }
			 case 1:{
				 ansValue[i]=Entropy.New_Partition(dataD,D,B);
				 break;
			 }
			 case 2:{
				 ansValue[i]=Entropy.New_Fuzzy_Version1(data,D,B);
				 break;
			 }
			 case 3:{
				 ansValue[i]=Entropy.Element_Covering(data,D,B);
				 break;
			 }
			 case 4:{
				 ansValue[i]=Entropy.Element_Partition(dataD,D,B);
				 break;
			 }
			 case 5:{
				 ansValue[i]=Entropy.Element_Fuzzy(data,D,B);
				 break;
			 }

			 default:break;
			 }
			
		 }
		 System.out.println("######");
		 for(int k=0;k<ansValue.length;++k){
			 System.out.println((k+1)+" "+ansValue[k]);
		 }
		 
		 
		
		return true;
	}
	public static boolean check2(Instances data , int rndint) throws Exception{
		weka.filters.unsupervised.attribute.Discretize unsd = new weka.filters.unsupervised.attribute.Discretize();
		unsd.setBins(2);
		
		unsd.setInputFormat(data);
		Instances dataUD = Filter.useFilter(data , unsd);
		dataUD.setClassIndex(dataUD.numAttributes()-1);
	//System.out.println(dataUD);
		
		 int numAttr = data.numAttributes()-1; //无决策属性
		 int[] indexR = generateRandomArrays(numAttr,30,rndint);
		 double[] ansValue = new double[numAttr];
		 boolean[] B = new boolean[numAttr+1];
		 boolean[] D = Utils.Instances2DecBoolean(data);
		 for(int i=0;i<numAttr;++i){
			 B[indexR[i]]=true;
			
				 ansValue[i]=Entropy.New_Partition(dataUD,D,B);

			 
			
		 }
		 //System.out.println(Arrays.toString(ansValue));
		 boolean tag = false;
		  
			 if(ansValue[0]!=0 ){
				 tag = true;
				  
			 }
		  
		
		return tag;
	}
	public static boolean check3(Instances data,newEntropy measure_type) throws Exception{
		int NumAttr = data.numAttributes();
		boolean[] newB = new boolean[NumAttr];
		boolean[] tempB = new boolean[NumAttr];
		boolean[] D = Utils.Instances2DecBoolean(data);
		boolean[] A = Utils.Instances2FullBoolean(data);
		//double ha = Entropy.getMeasure(data,measure_type,D,A);
		//double pbest = 100000;
		double[] ans = new double[NumAttr-1];
		  for(int k=0;k<NumAttr-1;++k){
			double rbest = 1000000;
			for(int i=0;i<NumAttr-1;++i){ //去掉决策属性
				if(!newB[i]){
					newB[i]=true;
					double rRandx =  Entropy.getMeasure(data,measure_type,D,newB);
					if(Utils.isAllFalse(tempB)||rRandx<rbest){
						tempB = newB.clone();
						rbest = rRandx;
					}
					newB[i]=false;
				}
			}

			ans[k] = rbest;
			System.out.println(k);
			newB = tempB.clone();
		     
			 
		}

		boolean tag = true;
		for(int k=0;k<NumAttr-2;++k){
			//System.out.println((k+1)+" "+ans[k]);
			System.out.println(ans[k]);
			if(ans[k]<ans[k+1])
				tag =false;
		}
		//System.out.println((NumAttr-2+1)+" "+ans[NumAttr-2]);
		System.out.println(ans[NumAttr-2]);
		return tag;
	}
	public static boolean check1show(Instances data , int rndint ,int caseint) throws Exception{
		//weka.filters.supervised.attribute.Discretize sd = new weka.filters.supervised.attribute.Discretize();
		weka.filters.unsupervised.attribute.Discretize unsd = new weka.filters.unsupervised.attribute.Discretize();
		unsd.setBins(2);
			
			unsd.setInputFormat(data);
			Instances dataUD = Filter.useFilter(data , unsd);
			dataUD.setClassIndex(dataUD.numAttributes()-1);
			//System.out.println(data);
		//System.out.println(dataUD);
		 int numAttr = data.numAttributes()-1; //无决策属性
		 int[] indexR = generateRandomArrays(numAttr,30,rndint);
		 double[] ansValue = new double[numAttr];
		 boolean[] B = new boolean[numAttr+1];
		 boolean[] D = Utils.Instances2DecBoolean(data);
		 for(int i=0;i<numAttr;++i){
			 B[indexR[i]]=true;
			 switch(caseint){
			 case 0:{
				 ansValue[i]=Entropy.New_Covering(data,D,B);
				 break;
			 }
			 case 1:{
				 
				 ansValue[i]=Entropy.New_Partition(dataUD,D,B);
				 break;
			 }
			 case 2:{
				 ansValue[i]=Entropy.New_Fuzzy_Version1(data,D,B);
				 break;
			 }
			 case 3:{
				 ansValue[i]=Entropy.Element_Covering(data,D,B);
				 break;
			 }
			 case 4:{
				 ansValue[i]=Entropy.Element_Partition(dataUD,D,B);
				 break;
			 }
			 case 5:{
				 ansValue[i]=Entropy.Element_Fuzzy(data,D,B);
				 break;
			 }
			 case 6:{
				 ansValue[i]=Entropy.New_Covering_Version2(data,D,B);
				 break;
			 }
			 default:break;
			 }
			
		 }
		 //System.out.println(Arrays.toString(ansValue));
		 boolean tag = true;
		 for(int i=0;i<numAttr-1;++i){
			 if(ansValue[i]<ansValue[i+1]){
				 tag = false;
				 break;
			 }
		 }
		System.out.println((caseint)+"#");
		System.out.println(Arrays.toString(indexR));
		System.out.println(Arrays.toString(ansValue));
		return tag;
	}
	public static void CheckSimulatedata(newEntropy measure_type) throws Exception{
		
		 
		int runtime = 1000000;
		//for(int k=0;k<20;++k){
		Instances seldata = null;
		int selind = -1;
		for(int i=2;i<runtime;++i){
			Instances data = generateInstances(6,3,2,1,i); //1.样本 2.属性 3.离散连续
			//System.out.println(data);

			/*double[] xx1 = calculateH(data,i,0);
			double[] xx2 = calculateH(data,i,3);
			if(isMatch1(xx1)&&isMatch2(xx2)){
				System.out.println(i);
				selind=i;
				System.out.println(data);
				seldata = data;
				System.out.println("newC"+":"+Arrays.toString(xx1));
				System.out.println("elementC"+":"+Arrays.toString(xx2));
				break;
			}*/
			

			double[] xx2 = calculateH(data,i,measure_type);
			if(isMatch2(xx2)){
				System.out.println(i);
				selind=i;
				System.out.println(data);
				seldata = data;
				System.out.println("elementC"+":"+Arrays.toString(xx2));
				break;
			}
		}
		
		 
		/*weka.filters.unsupervised.attribute.Discretize unsd = new weka.filters.unsupervised.attribute.Discretize();
		unsd.setBins(2);
			
			unsd.setInputFormat(seldata);
			Instances dataUD = Filter.useFilter(seldata , unsd);
			dataUD.setClassIndex(dataUD.numAttributes()-1);
			System.out.println(seldata);
		System.out.println(dataUD);
		
		
		String fn = "C:/Users/Eric/Desktop/2012春/Paper.new.NO3/ex2.arff";
		Instances data = new Instances(new FileReader(fn));
		data.setClassIndex(data.numAttributes()-1); //设置决策属性索引
		 
		//if(!check1(data,9,5)&&!check1(data,9,3))
			
		{
			//System.out.println("No");
			//System.out.println(data);
			//break;
		}
		//System.out.println(data);
		for(int i=0;i<6;++i){
			check1show(data,9,i);
		}*/
		
		
		//}

		System.out.println("Done");
	}
	private static boolean isMatch2(double[] xx) {
		// TODO Auto-generated method stub
		
		int k = xx.length;
		/*double tmp = 100000;
		int tmpind = -1;
		for(int i=0;i<k;++i){
			if(tmp>xx[i]){
				tmp = xx[i];
				tmpind = i;
			}
		}
		if(tmp!=xx[k-1]){
			return true;
		}
		else return false;*/
		if(xx[k-1]>xx[k-2]&&xx[0]>xx[1]){
			return true;
		}
		return false;
	}
	private static boolean isMatch1(double[] xx) {
		// TODO Auto-generated method stub
		
		
		int k = xx.length;
		double tmp = 100000;
		int tmpind = -1;
		for(int i=0;i<k;++i){
			if(tmp>xx[i]){
				tmp = xx[i];
				tmpind = i;
			}
		}
		if(tmp==xx[k-1]&&tmpind!=k-1){
			return true;
		}
		else return false;
	}
	public static void checkUCIdata() throws Exception{
		// TODO Auto-generated method stub
		 String fn = "C:/Users/Eric/Desktop/2011秋冬/Code/Xreducer/data/Data/wdbc.arff";
		//String fn = "C:/Users/Eric/Desktop/2011秋冬/Code/Xreducer/data/Data/fuzzy-ex.arff";
	   //String fn = "C:/Users/Eric/Desktop/2012春/Paper.new.NO3/ex2.arff";
		Instances data = new Instances(new FileReader(fn));
		data.setClassIndex(data.numAttributes()-1); //设置决策属性索引
 
		boolean res = check3(data,newEntropy.elementFuzzy);
		//System.out.println(data.numAttributes());
		System.out.println(res);
	}
	
	
	public static void main(String[] args) throws Exception {
		//CheckSimulatedata(newEntropy.elementCovering);
		checkUCIdata();

		
	}

}
