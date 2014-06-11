package Part3;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Vector;

import org.apache.commons.math.stat.descriptive.moment.Mean;

import myUtils.xMath;

import Part1.ClusterEnsemble;

import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.functions.LibSVM;
import weka.classifiers.rules.JRip;
import weka.classifiers.rules.PART;
import weka.classifiers.trees.J48;
import weka.classifiers.trees.SimpleCart;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.ReplaceMissingValues;

public class EvaluateAllData {
	public static void printTime(){
		 Date now = new Date();
		 DateFormat d3 = DateFormat.getTimeInstance();
	     String str3 = d3.format(now);
	     System.out.println(str3);
	}
	public static String getTime(){
		 Date now = new Date();
		 DateFormat d3 = DateFormat.getTimeInstance();
	     String str3 = d3.format(now);
	     return str3;
	}
	public static void EvaluateAllDataforTable(String saveFpath, Vector<String> fns, Vector<String> names, double semiMRMR, double daulPOS) throws Exception{
		 System.out.println("Begin at"+getTime());
		String[] dataNames = new String[names.size()];
		for(int i=0;i<names.size();++i)
			dataNames[i] = names.elementAt(i);
		String[] claAlgNames = {"NBC","C4.5","JRip","PART","CART"};
		String[] cluAlgNames = {"KModes","EM","Cobweb","FarthestFirst","CLOPE"};
 
   
		Vector<Vector<double[][]>> allres_part3_classifier = new Vector<Vector<double[][]>> ();
		Vector<Vector<double[][]>> allres_part4_classifier = new Vector<Vector<double[][]>> ();
		Vector<Vector<Vector<double[][]>>> allres_part3_cluster = new Vector<Vector<Vector<double[][]>>> ();
		Vector<Vector<Vector<double[][]>>> allres_part4_cluster = new Vector<Vector<Vector<double[][]>>> ();
		
		Vector<Vector<double[]>>  time_cla_part3 =new Vector<Vector<double[]>> ();
		Vector<Vector<double[]>>  time_clu_part3 =new Vector<Vector<double[]>> ();
		Vector<Vector<double[]>>  time_cla_part4 =new Vector<Vector<double[]>> ();
		Vector<Vector<double[]>>  time_clu_part4 =new Vector<Vector<double[]>> ();
		boolean[] isHaveStd_classifier = new boolean[claAlgNames.length];
		Arrays.fill(isHaveStd_classifier, true);
		boolean[] isHaveStd_cluster = new boolean[claAlgNames.length];
		Arrays.fill(isHaveStd_cluster, true);
		isHaveStd_cluster[4]=false;
		
		Vector<int[]> numRed_part3 = new Vector<int[]>();
		Vector<int[]> numRed_part4 = new Vector<int[]>();
		String selAlgName1= "SemiMRMR";
		String selAlgName2= "DualPOS";   
		
		for(int i=0;i<fns.size();++i){
			Instances data = new Instances(new FileReader(fns.elementAt(i)));
			data.setClassIndex(data.numAttributes()-1);
			
			// Replace missing values   //被均值代替
			ReplaceMissingValues m_ReplaceMissingValues = new ReplaceMissingValues();
			m_ReplaceMissingValues.setInputFormat(data);
			data = Filter.useFilter(data, m_ReplaceMissingValues);		
			//离散化
			weka.filters.supervised.attribute.Discretize sd = new weka.filters.supervised.attribute.Discretize();		 
			sd.setInputFormat(data);
			data = Filter.useFilter(data , sd);
			
			Instances datacluster = new Instances(data);
			datacluster.setClassIndex(-1);
			datacluster.deleteAttributeAt(datacluster.numAttributes()-1);
			
			double per = 0.5;
			boolean[] labels = ClusterEnsemble.getRandomLabeled(per,data.numInstances(),0);
			Semi_mRMR sm = new Semi_mRMR(data,selAlgName1,labels,semiMRMR);
			Daul_POS dp = new Daul_POS(data,selAlgName2,labels,daulPOS,0);
			
		
			int runtime = 5;
			int foldnum = 10;
			 Vector<ClassifierEX> cla = new Vector<ClassifierEX>();
			 cla.add(new ClassifierEX(new NaiveBayes(),claAlgNames[0],runtime,foldnum));
			 //cla.add(new ClassifierEX(new LibSVM(),"RBF-SVM",runtime,foldnum));
			 cla.add(new ClassifierEX(new J48(),claAlgNames[1],runtime,foldnum));
			 cla.add(new ClassifierEX(new JRip(),claAlgNames[2],runtime,foldnum));
			 cla.add(new ClassifierEX(new PART(),claAlgNames[3],runtime,foldnum));
			 cla.add(new ClassifierEX(new SimpleCart(),claAlgNames[4],runtime,foldnum));
	 	
			 Vector<ClustererEX> clu = new Vector<ClustererEX>();
			 int classNum = data.numClasses();
			 clu.add(new ClustererEX(0,cluAlgNames[0],runtime,classNum));
			 clu.add(new ClustererEX(1,cluAlgNames[1],runtime,classNum));
			 clu.add(new ClustererEX(2,cluAlgNames[2],runtime,classNum));
			 clu.add(new ClustererEX(3,cluAlgNames[3],runtime,classNum));
			 clu.add(new ClustererEX(4,cluAlgNames[4],1,classNum));
			 
			 EvaluateMethodforAdata alg_sm = new EvaluateMethodforAdata(data, datacluster,sm,cla,clu,true);
			 EvaluateMethodforAdata alg_dp = new EvaluateMethodforAdata(data, datacluster,dp,cla,clu,true);
			 
	
			 int[] anumRed_part3 = new int[2]; 
			 anumRed_part3[0] = sm.m_numRed;
			 anumRed_part3[1] = data.numAttributes()-1;
			 numRed_part3.add(anumRed_part3);
			 allres_part3_classifier.add(alg_sm.ClaTtestres);
			 allres_part3_cluster.add(alg_sm.res_clu);
			 time_cla_part3.add(alg_sm.RunTimeDetail_Classifier);
			 time_clu_part3.add(alg_sm.RunTimeDetail_Cluster);
			 
			 int[] anumRed_part4 = new int[2]; 
			 anumRed_part4[0] = dp.m_numRed;
			 anumRed_part4[1] = data.numAttributes()-1;
			 numRed_part4.add(anumRed_part4);
			 allres_part4_classifier.add(alg_dp.ClaTtestres);
			 allres_part4_cluster.add(alg_dp.res_clu);
			 time_cla_part4.add(alg_dp.RunTimeDetail_Classifier);
			 time_clu_part4.add(alg_dp.RunTimeDetail_Cluster);
			 
			 
			 
			 
			 System.out.println(dataNames[i]+":Done at"+getTime());
			 System.out.println(selAlgName1+":"+sm.m_numRed+" "+selAlgName2+":"+dp.m_numRed+" FullSet:"+(data.numAttributes()-1));
			 printClassifierRes(alg_sm.ClaTtestres,alg_dp.ClaTtestres);
			 printClusterRes(alg_sm.res_clu,alg_dp.res_clu);
			 System.out.println("----------------------------------------------------------------");
		}
		 
			 String title_part3_cla = "算法  "+selAlgName1+" 比较分类精度Accuracy " +"参数：$\\alpha$ = "+xMath.doubleFormat("0.0", semiMRMR);
			 String title_part3_clu_ac = "算法  "+selAlgName1+" 比较聚类精度Accuracy " +"参数：$\\alpha$ = "+xMath.doubleFormat("0.0", semiMRMR);
			 String title_part3_clu_nmi = "算法  "+selAlgName1+" 比较聚类归一化互信息NMI " +"参数：$\\alpha$ = "+xMath.doubleFormat("0.0", semiMRMR);
			 String part3 = "";
			 part3 +=ShowClassifierRes(title_part3_cla,selAlgName1,claAlgNames,dataNames,allres_part3_classifier,isHaveStd_classifier,numRed_part3);
			 //part3 +=ShowClusterRes(title_part3_clu_ac,title_part3_clu_nmi,selAlgName1,cluAlgNames,dataNames,allres_part3_cluster,isHaveStd_cluster,numRed_part3);
			 part3 +=ShowClusterRes(title_part3_clu_ac,title_part3_clu_nmi,selAlgName1,cluAlgNames,dataNames,allres_part3_cluster,isHaveStd_cluster);
			 
			 String title_part4_cla = "算法  "+selAlgName2+" 比较分类精度Accuracy";
			 String title_part4_clu_ac = "算法  "+selAlgName2+" 比较聚类精度Accuracy";
			 String title_part4_clu_nmi = "算法  "+selAlgName2+" 比较聚类归一化互信息NMI";
			 String part4 = "";
			 part4 +=ShowClassifierRes(title_part4_cla,selAlgName2,claAlgNames,dataNames,allres_part4_classifier,isHaveStd_classifier,numRed_part4);
			 //part4 +=ShowClusterRes(title_part4_clu_ac,title_part3_clu_nmi,selAlgName2,cluAlgNames,dataNames,allres_part4_cluster,isHaveStd_cluster,numRed_part4);
			 part4 +=ShowClusterRes(title_part4_clu_ac,title_part4_clu_nmi,selAlgName2,cluAlgNames,dataNames,allres_part4_cluster,isHaveStd_cluster);
			 
			 
			 //比较时间
			 String part_time = "";
			 String title_runtime_cla = "比较属性选择时间和各个分类算法所用时间";
			 String title_runtime_clu = "比较属性选择时间和各个聚类算法所用时间";
			 String[] sleAlgs = {"SM","DP","FF"};
	 
			 part_time+=EvaluateTimes.getLatexStrforTime(title_runtime_cla, dataNames,sleAlgs , claAlgNames, time_cla_part3, time_cla_part4);
			 part_time+=EvaluateTimes.getLatexStrforTime(title_runtime_clu, dataNames,sleAlgs , cluAlgNames, time_clu_part3, time_clu_part4);
			 
			 myUtils.xFile.writeNewFile(saveFpath, "latex_res_part3.tex", part3+part4);
			 myUtils.xFile.writeNewFile(saveFpath, "latex_res_part3EX.tex", part_time);
			 System.out.println("All Done at"+getTime());
	}
	public static void printClusterRes(Vector< Vector<double[][]>> res1,Vector< Vector<double[][]>> res2 ){
		 //classifer
		String str1_ac = "SemiMRMR-cluster-ac:";
		String str1_nmi = "SemiMRMR-cluster-nmi:";
		 //cluster
		 for(int c=0;c<res1.size();++c){
			 
			 double ac1 = res1.elementAt(c).elementAt(0)[0][0]*100;
			 double ac2 = res1.elementAt(c).elementAt(0)[1][0]*100;
			 double nmi1 = res1.elementAt(c).elementAt(1)[0][0];
			 double nmi2 = res1.elementAt(c).elementAt(1)[1][0];
			 str1_ac+=comAC(ac1,ac2)+" ";
			 str1_nmi+=comAC(nmi1,nmi2)+" "; 
		 }
		 String str2_ac = " DaulPOS-cluster-ac:";
			String str2_nmi = " DaulPOS-cluster-nmi:";
			 for(int c=0;c<res2.size();++c){
				 
				 double ac1 = res2.elementAt(c).elementAt(0)[0][0]*100;
				 double ac2 = res2.elementAt(c).elementAt(0)[1][0]*100;
				 double nmi1 = res2.elementAt(c).elementAt(1)[0][0];
				 double nmi2 = res2.elementAt(c).elementAt(1)[1][0];
				 str2_ac+=comAC(ac1,ac2)+" ";
				 str2_nmi+=comAC(nmi1,nmi2)+" "; 
			 }
		 String str1 = str1_ac+"  "+str1_nmi;
		 String str2 = str2_ac+"  "+str2_nmi;
		 System.out.println(str1+"\n"+str2);
	}
	public static void printClassifierRes(Vector<double[][]> res1,Vector<double[][]> res2 ){
		 //classifer
		String str1 = "SemiMRMR-classifier-ac:";
		  for(int c=0;c<res1.size();++c){
			 	double ac1 = res1.elementAt(c)[0][0]*100;
			 	double ac2 = res1.elementAt(c)[1][0]*100;	  
			 	str1+=comAC(ac1,ac2)+" ";
		 }
		 
		  String str2 = " DaulPOS-classifier-ac:";
		 for(int c=0;c<res2.size();++c){
			  
			 	double ac1 = res2.elementAt(c)[0][0]*100;
			 	double ac2 = res2.elementAt(c)[1][0]*100;
			 
			 	str2+=comAC(ac1,ac2)+" ";
		 }
		 System.out.println(str1+"\n"+str2);
	}
	public EvaluateAllData(Vector<String> fns, Vector<String> names, double semiMRMR, double daulPOS) throws Exception{
		System.out.println("Begin!");
		printTime();
		for(int i=0;i<fns.size();++i){
			System.out.println("-----------------");
			String strhead = names.elementAt(i)+":";
			//printTime();
			Instances data = new Instances(new FileReader(fns.elementAt(i)));
			data.setClassIndex(data.numAttributes()-1);
			strhead +=data.numAttributes()-1;
			System.out.println(strhead);
			// Replace missing values   //被均值代替
			ReplaceMissingValues m_ReplaceMissingValues = new ReplaceMissingValues();
			m_ReplaceMissingValues.setInputFormat(data);
			data = Filter.useFilter(data, m_ReplaceMissingValues);		
			//离散化
			weka.filters.supervised.attribute.Discretize sd = new weka.filters.supervised.attribute.Discretize();		 
			sd.setInputFormat(data);
			data = Filter.useFilter(data , sd);
			
			Instances datacluster = new Instances(data);
			datacluster.setClassIndex(-1);
			datacluster.deleteAttributeAt(datacluster.numAttributes()-1);
			
			
			double per = 0.5;
			boolean[] labels = ClusterEnsemble.getRandomLabeled(per,data.numInstances(),0);
			Semi_mRMR sm = new Semi_mRMR(data,"SemiMRMR",labels,semiMRMR);
			Daul_POS dp = new Daul_POS(data,"DualPOS",labels,daulPOS,0);
			
		
			int runtime = 5;
			int foldnum = 10;
			 Vector<ClassifierEX> cla = new Vector<ClassifierEX>();
			 /*cla.add(new ClassifierEX(new NaiveBayes(),"NBC",runtime,foldnum));
			 //cla.add(new ClassifierEX(new LibSVM(),"RBF-SVM",runtime,foldnum));
			 cla.add(new ClassifierEX(new J48(),"C4.5",runtime,foldnum));
			 cla.add(new ClassifierEX(new JRip(),"JRip",runtime,foldnum));
			 cla.add(new ClassifierEX(new PART(),"PART",runtime,foldnum));
			 cla.add(new ClassifierEX(new SimpleCart(),"CART",runtime,foldnum));*/
			 String[] cluAlgNames = {"KModes","EM","Cobweb","FarthestFirst","CLOPE"};
			 Vector<ClustererEX> clu = new Vector<ClustererEX>();
			 int classNum = data.numClasses();
			 clu.add(new ClustererEX(0,cluAlgNames[0],runtime,classNum));
			 clu.add(new ClustererEX(1,cluAlgNames[1],runtime,classNum));
			 clu.add(new ClustererEX(2,cluAlgNames[2],runtime,classNum));
			 clu.add(new ClustererEX(3,cluAlgNames[3],runtime,classNum));
			 clu.add(new ClustererEX(4,cluAlgNames[4],1,classNum));
			 
			 EvaluateMethodforAdata testadata1 = new EvaluateMethodforAdata(data, datacluster,sm,cla,clu,true);
			 EvaluateMethodforAdata testadata2 = new EvaluateMethodforAdata(data, datacluster,dp,cla,clu,true);
			 //String str_sm = sm.algname+":Num:"+sm.m_numRed+"::";
			 String str_sm_ac = "AC:";
			 String str_sm_nmi = "NMI:";
			 String str_dp_ac = "AC:";
			 String str_dp_nmi = "NMI:";
			 String str_sm_ac_detail = "AC:";
			 String str_sm_nmi_detail = "NMI:";
			 String str_dp_ac_detail = "AC:";
			 String str_dp_nmi_detail = "NMI:";
			 //classifer
			 /*for(int c=0;c<cla.size();++c){
				 	String name = cla.elementAt(c).algname;
				 	double ac1 = testadata1.ClaTtestres.elementAt(c)[0][0]*100;
				 	double ac2 = testadata1.ClaTtestres.elementAt(c)[1][0]*100;
				 	str_sm+=name+"("+xMath.doubleFormat("0.00", ac1)+","+xMath.doubleFormat("0.00", ac2)+"):";
				 	str_sm+=comAC(ac1,ac2)+" ";
			 }
			// str_sm +="Time:"+xMath.doubleFormat("0.0000", sm.m_useTime);
			 //String str_dp = dp.algname+":Num:"+dp.m_numRed+"::";
			 
			 for(int c=0;c<cla.size();++c){
				 	String name = cla.elementAt(c).algname;
				 	double ac1 = testadata2.ClaTtestres.elementAt(c)[0][0]*100;
				 	double ac2 = testadata2.ClaTtestres.elementAt(c)[1][0]*100;
				 	str_dp+=name+"("+xMath.doubleFormat("0.00", ac1)+","+xMath.doubleFormat("0.00", ac2)+"):";
				 	str_dp+=comAC(ac1,ac2)+" ";
			 }
			 //str_dp +="Time:"+xMath.doubleFormat("0.0000", dp.m_useTime);
			  */
			 //cluster
			 for(int c=0;c<clu.size();++c){
				 String name = clu.elementAt(c).algname;
				 double ac1 = testadata1.res_clu.elementAt(c).elementAt(0)[0][0]*100;
				 double ac2 = testadata1.res_clu.elementAt(c).elementAt(0)[1][0]*100;
				 double nmi1 = testadata1.res_clu.elementAt(c).elementAt(1)[0][0];
				 double nmi2 = testadata1.res_clu.elementAt(c).elementAt(1)[1][0];
				 str_sm_ac+=comAC(ac1,ac2)+" ";
				 str_sm_nmi+=comAC(nmi1,nmi2)+" ";
				 str_sm_ac_detail+=name+"("+xMath.doubleFormat("0.00", ac1)+","+xMath.doubleFormat("0.00", ac2)+"):";
				 str_sm_nmi_detail+=name+"("+xMath.doubleFormat("0.0000", nmi1)+","+xMath.doubleFormat("0.0000", nmi2)+"):";
			 }
			 for(int c=0;c<clu.size();++c){
				 String name = clu.elementAt(c).algname;
				 double ac1 = testadata2.res_clu.elementAt(c).elementAt(0)[0][0]*100;
				 double ac2 = testadata2.res_clu.elementAt(c).elementAt(0)[1][0]*100;
				 double nmi1 = testadata2.res_clu.elementAt(c).elementAt(1)[0][0];
				 double nmi2 = testadata2.res_clu.elementAt(c).elementAt(1)[1][0];
				 str_dp_ac+=comAC(ac1,ac2)+" ";
				 str_dp_nmi+=comAC(nmi1,nmi2)+" ";
				 str_dp_ac_detail+=name+"("+xMath.doubleFormat("0.00", ac1)+","+xMath.doubleFormat("0.00", ac2)+"):";
				 str_dp_nmi_detail+=name+"("+xMath.doubleFormat("0.0000", nmi1)+","+xMath.doubleFormat("0.0000", nmi2)+"):";
			 }
			 sm.getInformation();
			 dp.getInformation();
			 System.out.println(str_sm_ac+" "+str_sm_nmi);
			 System.out.println(str_dp_ac+" "+str_dp_nmi);
			 System.out.println(str_sm_ac_detail);
			 System.out.println(str_dp_ac_detail);
			 System.out.println(str_sm_nmi_detail);
			 System.out.println(str_dp_nmi_detail);
			 //printTime();
			 System.out.println("All Done!");
				printTime();
		}
	}
	private static String ShowClusterRes(String title_ac,
			String title_nmi, String selAlgName,
			String[] cluAlgNames, String[] dataNames,
			Vector<Vector<Vector<double[][]>>> allres_cluster,
			boolean[] isHaveStd_cluster, Vector<int[]> numRed_part) {
		Vector<double[][]> newRes_ac = new Vector<double[][]>();
		Vector<double[][]> newRes_nmi = new Vector<double[][]>();
		for(int i=0;i<allres_cluster.size();++i){
			double[][] aData_ac = new double[cluAlgNames.length][4];
			double[][] aData_nmi = new double[cluAlgNames.length][4];
			for(int r=0;r<cluAlgNames.length;++r){
				aData_ac[r][0] = allres_cluster.elementAt(i).elementAt(r).elementAt(0)[0][0]*100;
				aData_ac[r][1] = allres_cluster.elementAt(i).elementAt(r).elementAt(0)[0][1]*100;
				aData_ac[r][2] = allres_cluster.elementAt(i).elementAt(r).elementAt(0)[1][0]*100;
				aData_ac[r][3] = allres_cluster.elementAt(i).elementAt(r).elementAt(0)[1][1]*100;
				
				aData_nmi[r][0] = allres_cluster.elementAt(i).elementAt(r).elementAt(1)[0][0];
				aData_nmi[r][1] = allres_cluster.elementAt(i).elementAt(r).elementAt(1)[0][1];
				aData_nmi[r][2] = allres_cluster.elementAt(i).elementAt(r).elementAt(1)[1][0];
				aData_nmi[r][3] = allres_cluster.elementAt(i).elementAt(r).elementAt(1)[1][1];
			}
			newRes_ac.add(aData_ac);
			newRes_nmi.add(aData_nmi);
		}
		String str_ac = "%##############"+selAlgName.toUpperCase()+"--Cluster-AC###########\n";
		str_ac += getLatexStr(title_ac,selAlgName,dataNames,cluAlgNames,newRes_ac,isHaveStd_cluster,true,numRed_part)+"\n\n";
		
		String str_nmi = "%##############"+selAlgName.toUpperCase()+"--Cluster-NMI###########\n";
		str_nmi += getLatexStr(title_nmi,selAlgName,dataNames,cluAlgNames,newRes_nmi,isHaveStd_cluster,false,numRed_part)+"\n\n";
		//System.out.println(str_ac);
		//System.out.println(str_nmi);
		return str_ac+str_nmi;
		// TODO Auto-generated method stub
		
	}
	private static String  ShowClusterRes(String title_ac,
			String title_nmi, String selAlgName,
			String[] cluAlgNames, String[] dataNames,
			Vector<Vector<Vector<double[][]>>> allres_cluster,
			boolean[] isHaveStd_cluster) {
		Vector<double[][]> newRes_ac = new Vector<double[][]>();
		Vector<double[][]> newRes_nmi = new Vector<double[][]>();
		for(int i=0;i<allres_cluster.size();++i){
			double[][] aData_ac = new double[cluAlgNames.length][4];
			double[][] aData_nmi = new double[cluAlgNames.length][4];
			for(int r=0;r<cluAlgNames.length;++r){
				aData_ac[r][0] = allres_cluster.elementAt(i).elementAt(r).elementAt(0)[0][0]*100;
				aData_ac[r][1] = allres_cluster.elementAt(i).elementAt(r).elementAt(0)[0][1]*100;
				aData_ac[r][2] = allres_cluster.elementAt(i).elementAt(r).elementAt(0)[1][0]*100;
				aData_ac[r][3] = allres_cluster.elementAt(i).elementAt(r).elementAt(0)[1][1]*100;
				
				aData_nmi[r][0] = allres_cluster.elementAt(i).elementAt(r).elementAt(1)[0][0];
				aData_nmi[r][1] = allres_cluster.elementAt(i).elementAt(r).elementAt(1)[0][1];
				aData_nmi[r][2] = allres_cluster.elementAt(i).elementAt(r).elementAt(1)[1][0];
				aData_nmi[r][3] = allres_cluster.elementAt(i).elementAt(r).elementAt(1)[1][1];
			}
			newRes_ac.add(aData_ac);
			newRes_nmi.add(aData_nmi);
		}
		String str_ac = "%##############"+selAlgName.toUpperCase()+"--Cluster-AC###########\n";
		str_ac += getLatexStr(title_ac,selAlgName,dataNames,cluAlgNames,newRes_ac,isHaveStd_cluster,true)+"\n\n";
		
		String str_nmi = "%##############"+selAlgName.toUpperCase()+"--Cluster-NMI###########\n";
		str_nmi += getLatexStr(title_nmi,selAlgName,dataNames,cluAlgNames,newRes_nmi,isHaveStd_cluster,false)+"\n\n";
 
		return str_ac+str_nmi;
		// TODO Auto-generated method stub
		
	}
	
	public static String ShowClassifierRes(String title, String selAlg, String[] algnames, String [] datanames, Vector<Vector<double[][]>> allres,boolean[] isHaveStd){
		Vector<double[][]> newRes = new Vector<double[][]>();
		for(int i=0;i<allres.size();++i){
			double[][] aData = new double[algnames.length][4];
			for(int r=0;r<algnames.length;++r){
				aData[r][0] = allres.elementAt(i).elementAt(r)[0][0]*100;
				aData[r][1] = allres.elementAt(i).elementAt(r)[0][1]*100;
				aData[r][2] = allres.elementAt(i).elementAt(r)[1][0]*100;
				aData[r][3] = allres.elementAt(i).elementAt(r)[1][1]*100;
			}
			newRes.add(aData);
		}
		String str = "%##############"+selAlg.toUpperCase()+"--Classifer-AC###########\n";
		str += getLatexStr(title,selAlg,datanames,algnames,newRes,isHaveStd,true)+"\n\n";
		return str;
	}
	public static String ShowClassifierRes(String title, String selAlg, String[] algnames, String [] datanames, Vector<Vector<double[][]>> allres,boolean[] isHaveStd,Vector<int[]> numRed){
		Vector<double[][]> newRes = new Vector<double[][]>();
		for(int i=0;i<allres.size();++i){
			double[][] aData = new double[algnames.length][4];
			for(int r=0;r<algnames.length;++r){
				aData[r][0] = allres.elementAt(i).elementAt(r)[0][0]*100;
				aData[r][1] = allres.elementAt(i).elementAt(r)[0][1]*100;
				aData[r][2] = allres.elementAt(i).elementAt(r)[1][0]*100;
				aData[r][3] = allres.elementAt(i).elementAt(r)[1][1]*100;
			}
			newRes.add(aData);
		}
		String str = "%##############"+selAlg.toUpperCase()+"--Classifer-AC###########\n";
		str += getLatexStr(title,selAlg,datanames,algnames,newRes,isHaveStd,true,numRed)+"\n\n";
	 
		return str;
	}
	private static String getLatexStr(String Title, String selAlg, String[] dataname, String[] algname,Vector<double[][]> Res ,boolean[] isHaveStd ,boolean isAC, Vector<int[]> numRed){
		String str = "";
		String strHead = getHeadStr(Title,algname,selAlg,isHaveStd,isAC,true);
		double[][] values = new double[algname.length*2][dataname.length];
		double[][] numRedall = new double[2][dataname.length];
		String format = isAC?"0.00":"0.000";
		for(int i = 0;i<dataname.length;++i){
			String aData =dataname[i]+"&"+numRed.elementAt(i)[0]+"&"+numRed.elementAt(i)[1];
			numRedall[0][i] = numRed.elementAt(i)[0];
			numRedall[1][i] = numRed.elementAt(i)[1];
			for(int j=0;j<Res.elementAt(i).length;++j){
				values[2*j][i] =  Res.elementAt(i)[j][0];
				values[2*j+1][i] =  Res.elementAt(i)[j][2];
				if(isHaveStd[j]){
					
					String com1 = xMath.doubleFormat(format, Res.elementAt(i)[j][0]);
					String std1 = xMath.doubleFormat(format, Res.elementAt(i)[j][1]);
					String com2 = xMath.doubleFormat(format, Res.elementAt(i)[j][2]);
					String std2 = xMath.doubleFormat(format, Res.elementAt(i)[j][3]);
					aData+="&"+com1+"$\\pm$"+std1+"&"+com2+"$\\pm$"+std2;
				}
				else
				{
					String com1 = xMath.doubleFormat(format, Res.elementAt(i)[j][0]);
					String com2 = xMath.doubleFormat(format, Res.elementAt(i)[j][2]);
					aData+="&"+com1+"&"+com2;
				}
			}
			str += aData+"\\\\\n";
		}
		
		str += "\\midrule\nAverage";
		Mean meanRednum = new Mean(); // 算术平均值
		double avgRednum1 = meanRednum.evaluate(numRedall[0]);
		double avgRednum2 = meanRednum.evaluate(numRedall[1]);
		str += "&"+xMath.doubleFormat(format, avgRednum1)+"&"+xMath.doubleFormat(format, avgRednum2);
		for(int j=0;j<algname.length;++j){
			Mean mean = new Mean(); // 算术平均值
			double avg1 = mean.evaluate(values[2*j]);
			double avg2 = mean.evaluate(values[2*j+1]);
			str += "&"+xMath.doubleFormat(format, avg1)+"&"+xMath.doubleFormat(format, avg2);
		}
		str += "\\\\\n";
		String strBottom = getBottomStr();
		return strHead+str+strBottom;
	}
	
	private static String getLatexStr(String Title, String selAlg, String[] dataname, String[] algname,Vector<double[][]> Res ,boolean[] isHaveStd ,boolean isAC){
		String str = "";
		String strHead = getHeadStr(Title,algname,selAlg,isHaveStd,isAC);
		double[][] values = new double[algname.length*2][dataname.length];
	
		String format = isAC?"0.00":"0.000";
		for(int i = 0;i<dataname.length;++i){
			String aData =dataname[i];
			for(int j=0;j<Res.elementAt(i).length;++j){
				values[2*j][i] =  Res.elementAt(i)[j][0];
				values[2*j+1][i] =  Res.elementAt(i)[j][2];
				if(isHaveStd[j]){
					
					String com1 = xMath.doubleFormat(format, Res.elementAt(i)[j][0]);
					String std1 = xMath.doubleFormat(format, Res.elementAt(i)[j][1]);
					String com2 = xMath.doubleFormat(format, Res.elementAt(i)[j][2]);
					String std2 = xMath.doubleFormat(format, Res.elementAt(i)[j][3]);
					aData+="&"+com1+"$\\pm$"+std1+"&"+com2+"$\\pm$"+std2;
				}
				else
				{
					String com1 = xMath.doubleFormat(format, Res.elementAt(i)[j][0]);
					String com2 = xMath.doubleFormat(format, Res.elementAt(i)[j][2]);
					aData+="&"+com1+"&"+com2;
				}
			}
			str += aData+"\\\\\n";
		}
		
		str += "\\midrule\nAverage";
		
		for(int j=0;j<algname.length;++j){
			Mean mean = new Mean(); // 算术平均值
			double avg1 = mean.evaluate(values[2*j]);
			double avg2 = mean.evaluate(values[2*j+1]);
			str += "&"+xMath.doubleFormat(format, avg1)+"&"+xMath.doubleFormat(format, avg2);
		}
		str += "\\\\\n";
		String strBottom = getBottomStr();
		return strHead+str+strBottom;
	}
	
	
	private static String getBottomStr() {
		// TODO Auto-generated method stub
		String str = "";
		str += "\\bottomrule\n"
			+"\\end{tabular}\n"
			+"}%\n"
			+"\\end{table*}\n";
		return str;
	}
	private static String getHeadStr(String title, String[] algname, String selAlg, boolean[] isHaveStd ,boolean isAC) {
		// TODO Auto-generated method stub
		String str ="";
		str += "\\begin{table*}[htbp]\n"
		         +"\\centering\\caption{"+title+"}\n"
		         +"\\label{}\n"
		         +"\\resizebox{\\textwidth}{!}{ %\n"
		         +"\\begin{tabular}{lcccccccccc}\n"
		         +"\\toprule"
		         +"\\multirow{3}{*}{\\centering $Datasets$}\n"
		         +"&\\multicolumn{2}{c}{$"+algname[0]+"$}\n"
		         +"&\\multicolumn{2}{c}{$"+algname[1]+"$}\n"
		         +"&\\multicolumn{2}{c}{$"+algname[2]+"$}\n"
		         +"&\\multicolumn{2}{c}{$"+algname[3]+"$}\n"
		         +"&\\multicolumn{2}{c}{$"+algname[4]+"$}\\\\\n"
		         +"\\cmidrule{2-11}\n";
		String strtmp1 = "";
		String strtmp2 = "";
		String Com = isAC?"ACC":"NMI";
		for(int i=0;i<isHaveStd.length;++i){
			strtmp1 += "&$"+selAlg+"$&"+"$FullFeature$";
			
			if(isHaveStd[i]){
				strtmp2 +=  "&"+Com+"$\\pm$Std&"+Com+"$\\pm$Std";
			}
			else
			{
				strtmp2 +=  "&"+Com+"&"+Com;
			}
			 
		}
		strtmp1 += "\\\\\n";
		str += strtmp1 +"\\cmidrule{2-11}\n";
		strtmp2 += "\\\\\n";
		str += strtmp2 ;
		return str+"\\midrule\n";
	}
	private static String getHeadStr(String title, String[] algname, String selAlg, boolean[] isHaveStd ,boolean isAC,boolean ishaveRedNum) {
		// TODO Auto-generated method stub
		String str ="";
		str += "\\begin{table*}[htbp]\n"
		         +"\\centering\\caption{"+title+"}\n"
		         +"\\label{}\n"
		         +"\\resizebox{\\textwidth}{!}{ %\n"
		         +"\\begin{tabular}{lcccccccccccc}\n"
		         +"\\toprule\n"
		         +"\\multirow{3}{*}{\\centering $Datasets$}\n"
		         +"&\\multicolumn{2}{c}{$\\#Features$}\n"
		         +"&\\multicolumn{2}{c}{$"+algname[0]+"$}\n"
		         +"&\\multicolumn{2}{c}{$"+algname[1]+"$}\n"
		         +"&\\multicolumn{2}{c}{$"+algname[2]+"$}\n"
		         +"&\\multicolumn{2}{c}{$"+algname[3]+"$}\n"
		         +"&\\multicolumn{2}{c}{$"+algname[4]+"$}\\\\\n"
		         +"\\cmidrule{2-13}\n";
		String strtmp1 = "&\\multirow{2}{*}{\\centering $"+selAlg+"$}& \\multirow{2}{*}{\\centering $ FullFeature$}";
		String strtmp2 = "&&";
		String Com = isAC?"ACC":"NMI";
		for(int i=0;i<isHaveStd.length;++i){
			strtmp1 += "&$"+selAlg+"$&"+"$FullFeature$";
			
			if(isHaveStd[i]){
				strtmp2 +=  "&"+Com+"$\\pm$Std&"+Com+"$\\pm$Std";
			}
			else
			{
				strtmp2 +=  "&"+Com+"&"+Com;
			}
			 
		}
		strtmp1 += "\\\\\n";
		str += strtmp1 +"\\cmidrule{4-13}\n";
		strtmp2 += "\\\\\n";
		str += strtmp2 ;
		return str+"\\midrule\n";
	}
	static String comAC(double a, double b){
		if(a>b)
			return "√";
		else if(Math.abs(a-b)<0.1)
			return "-";
		else if(Math.abs(a-b)<2)
			return "o";
		else return "x";
	}
	String comNMI(double a, double b){
		if(a>b)
			return "√";
		else if(Math.abs(a-b)<0.01)
			return "-";
		else if(Math.abs(a-b)<0.2)
			return "o";
		else return "x";
	}
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		double semiMRMR = 0.1;
		double daulPOS = 1;
		String path = "C:/Users/Eric/Desktop/2012秋冬/毕业设计/DataSet/newFS/";
		String postfix = ".arff";
		Vector<String> names = new Vector<String>();
		//names.add("Arrhythmia");  //1h
		
		//names.add("Audiology");
		names.add("Colic");
		names.add("Credit");
		//names.add("Dermatology");
		names.add("Diabetes");
		
		names.add("Heart-cleveland");
		names.add("Heart-hungarian");
		names.add("Heart-statlog");
		
		
		names.add("Hepatitis");
		names.add("Ionosphere");
		//names.add("Labor");
		//names.add("Libras");
		//names.add("Lympho");
		//names.add("Musk1"); //20m
		names.add("Musk2"); //40m
		
		//names.add("Olitos");
		names.add("Promoters");
		//names.add("Sonar");
		//names.add("Soybean");
		names.add("SPECT");
		//names.add("Tumor");
		names.add("Voting");
		//names.add("Waveform");
		names.add("WDBC");
		//names.add("Wine"); 

 
		 
		String sfp = "C:/Users/Eric/Desktop/2012秋冬/毕业设计/Paper/资料/Part3/latex/";
		Vector<String> fns = new Vector<String>();
		for(int i=0;i<names.size();++i)
			fns.add(path+names.elementAt(i)+postfix);
		//EvaluateAllData tad = new EvaluateAllData(fns,names,semiMRMR, daulPOS);
		EvaluateAllDataforTable(sfp,fns,names,semiMRMR, daulPOS);
		
	}

}
