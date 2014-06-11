package Part34EX;

import java.io.FileReader;
import java.text.DateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Vector;

import org.apache.commons.math.stat.descriptive.moment.Mean;

import myUtils.xMath; 
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.ReplaceMissingValues;
import Part1.ClusterEnsemble;
 
import Part3.Daul_POS;
 
import Part3.EvaluateTimes;
import Part3.SemiClustererEX;
import Part3.Semi_mRMR;
 
public class EvaluatePart1234ForALLdata {
	
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
	
	public static void EvaluateAllDataforTable(String saveFpath, Vector<String> fns, Vector<String> names, double semiMRMR, double daulPOS, double per) throws Exception{
		 System.out.println("Begin at"+getTime());
		String[] dataNames = new String[names.size()];
		for(int i=0;i<names.size();++i)
			dataNames[i] = names.elementAt(i);
		
		String[] cluAlgNames = {"W-Voting","S-W-Voting","Semi-HC-Avg","Semi-HC-Mean"};

  

		Vector<Vector<Vector<double[][]>>> allres_cluster = new Vector<Vector<Vector<double[][]>>> ();
		Vector<Vector<double[]>>  time_clu =new Vector<Vector<double[]>> ();
		Vector<int[]> numRed = new Vector<int[]>();
		String[] selAlgName= {"SemiMRMR","DualPOS"};
	  
		int runtime = 5;
	
		
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
			
			
			boolean[] labels = ClusterEnsemble.getRandomLabeled(per,data.numInstances(),0);
			Semi_mRMR sm = new Semi_mRMR(data,selAlgName[0],labels,semiMRMR);
			Daul_POS dp = new Daul_POS(data,selAlgName[1],labels,daulPOS,0);

	 	
			 Vector<SemiClustererEX> clu = new Vector<SemiClustererEX>();
			 int classNum = data.numClasses();
			 clu.add(new SemiClustererEX(0,cluAlgNames[0],runtime,classNum,labels));
			 clu.add(new SemiClustererEX(1,cluAlgNames[1],runtime,classNum,labels));
			 clu.add(new SemiClustererEX(2,cluAlgNames[2],runtime,classNum,labels));
			 clu.add(new SemiClustererEX(3,cluAlgNames[3],runtime,classNum,labels));
		 
			 
			 EvaluatePart1234ForAdata alg = new EvaluatePart1234ForAdata(data, datacluster,sm,dp,clu);
		
			 
	
			 int[] anumRed = new int[3]; 
			 anumRed[0] = sm.m_numRed;
			 anumRed[1] = dp.m_numRed;
			 anumRed[2] = data.numAttributes()-1;
			 numRed.add(anumRed);
			 
			 allres_cluster.add(alg.res_clu);
			 time_clu.add(alg.RunTimeDetail_Cluster);
			 

			 
			 System.out.println(dataNames[i]+":Done at"+getTime());
		}
		 
			 
		 
			 String title_part4_clu_ac = "算法  "+selAlgName[0]+"和"+selAlgName[1]+" 比较半监督聚类精度Accuracy";
			 String title_part4_clu_nmi = "算法  "+selAlgName[0]+"和"+selAlgName[1]+" 比较半监督聚类归一化互信息NMI";
			 String str = "";
			 
			
			 str +=ShowClusterRes(title_part4_clu_ac,title_part4_clu_nmi,selAlgName,cluAlgNames,dataNames,allres_cluster);

			 //比较时间
			 String part_time = "";
			 
			 String title_runtime_clu = "比较属性选择时间和各个半监督聚类算法所用时间";
			 String[] sleAlgs = {"SM","DP","FF"};
	 
			 part_time+=EvaluateTimes.getLatexStrforTimeforPart1234EX(title_runtime_clu, dataNames,sleAlgs , cluAlgNames, time_clu);
			 
			 myUtils.xFile.writeNewFile(saveFpath, "latex_res_part1234acnmi.tex", str);
			 myUtils.xFile.writeNewFile(saveFpath, "latex_res_part1234time.tex", part_time);
			 System.out.println("All Done at"+getTime());
	}
 
	private static String  ShowClusterRes(String title_ac,
			String title_nmi, String[] selAlgName,
			String[] cluAlgNames, String[] dataNames,
			Vector<Vector<Vector<double[][]>>> allres_cluster) {
		Vector<double[][]> newRes_ac = new Vector<double[][]>();
		Vector<double[][]> newRes_nmi = new Vector<double[][]>();
		for(int i=0;i<allres_cluster.size();++i){
			double[][] aData_ac = new double[cluAlgNames.length][6];
			double[][] aData_nmi = new double[cluAlgNames.length][6];
			for(int r=0;r<cluAlgNames.length;++r){
				aData_ac[r][0] = allres_cluster.elementAt(i).elementAt(r).elementAt(0)[0][0]*100;
				aData_ac[r][1] = allres_cluster.elementAt(i).elementAt(r).elementAt(0)[0][1]*100;
				aData_ac[r][2] = allres_cluster.elementAt(i).elementAt(r).elementAt(0)[1][0]*100;
				aData_ac[r][3] = allres_cluster.elementAt(i).elementAt(r).elementAt(0)[1][1]*100;
				aData_ac[r][4] = allres_cluster.elementAt(i).elementAt(r).elementAt(0)[2][0]*100;
				aData_ac[r][5] = allres_cluster.elementAt(i).elementAt(r).elementAt(0)[2][1]*100;
				
				aData_nmi[r][0] = allres_cluster.elementAt(i).elementAt(r).elementAt(1)[0][0];
				aData_nmi[r][1] = allres_cluster.elementAt(i).elementAt(r).elementAt(1)[0][1];
				aData_nmi[r][2] = allres_cluster.elementAt(i).elementAt(r).elementAt(1)[1][0];
				aData_nmi[r][3] = allres_cluster.elementAt(i).elementAt(r).elementAt(1)[1][1];
				aData_nmi[r][4] = allres_cluster.elementAt(i).elementAt(r).elementAt(1)[2][0];
				aData_nmi[r][5] = allres_cluster.elementAt(i).elementAt(r).elementAt(1)[2][1];
			}
			newRes_ac.add(aData_ac);
			newRes_nmi.add(aData_nmi);
		}
		String str_ac = "%##############"+selAlgName[0].toUpperCase()+"--"+selAlgName[1].toUpperCase()+"--SemiCluster-AC###########\n";
		str_ac += getLatexStr(title_ac,selAlgName,dataNames,cluAlgNames,newRes_ac,true)+"\n\n";
		
		String str_nmi = "%##############"+selAlgName[0].toUpperCase()+"--"+selAlgName[1].toUpperCase()+"--SemiCluster-NMI###########\n";
		str_nmi += getLatexStr(title_nmi,selAlgName,dataNames,cluAlgNames,newRes_nmi,false)+"\n\n";
 
		return str_ac+str_nmi;
		// TODO Auto-generated method stub
		
	}
	
	private static String getLatexStr(String Title, String[] selAlg, String[] dataname, String[] algname,Vector<double[][]> Res ,boolean isAC){
		String str = "";
		String strHead = getHeadStr(Title,algname,selAlg[0],selAlg[1],isAC);
		double[][] values = new double[algname.length*3][dataname.length];
	
		String format = isAC?"0.00":"0.000";
		for(int i = 0;i<dataname.length;++i){
			String aData =dataname[i];
			for(int j=0;j<Res.elementAt(i).length-2;++j){
				values[3*j][i] =  Res.elementAt(i)[j][0];
				values[3*j+1][i] =  Res.elementAt(i)[j][2];
				values[3*j+2][i] =  Res.elementAt(i)[j][4];
					
					String com1 = xMath.doubleFormat(format, Res.elementAt(i)[j][0]);
					String std1 = xMath.doubleFormat(format, Res.elementAt(i)[j][1]);
					String com2 = xMath.doubleFormat(format, Res.elementAt(i)[j][2]);
					String std2 = xMath.doubleFormat(format, Res.elementAt(i)[j][3]);
					String com3 = xMath.doubleFormat(format, Res.elementAt(i)[j][4]);
					String std3 = xMath.doubleFormat(format, Res.elementAt(i)[j][5]);
					aData+="&"+com1+"$\\pm$"+std1+"&"+com2+"$\\pm$"+std2+"&"+com3+"$\\pm$"+std3;
				
			}
			for(int j=Res.elementAt(i).length-2;j<Res.elementAt(i).length;++j){
				values[3*j][i] =  Res.elementAt(i)[j][0];
				values[3*j+1][i] =  Res.elementAt(i)[j][2];
				values[3*j+2][i] =  Res.elementAt(i)[j][4];
					
					String com1 = xMath.doubleFormat(format, Res.elementAt(i)[j][0]);				
					String com2 = xMath.doubleFormat(format, Res.elementAt(i)[j][2]);			
					String com3 = xMath.doubleFormat(format, Res.elementAt(i)[j][4]);
					aData+="&"+com1+"&"+com2+"&"+com3;
				
			}
			str += aData+"\\\\\n";
		}
		
		str += "\\midrule\nAverage";
		
		for(int j=0;j<algname.length;++j){
			Mean mean = new Mean(); // 算术平均值
			double avg1 = mean.evaluate(values[3*j]);
			double avg2 = mean.evaluate(values[3*j+1]);
			double avg3 = mean.evaluate(values[3*j+2]);
			str += "&"+xMath.doubleFormat(format, avg1)+"&"+xMath.doubleFormat(format, avg2)+"&"+xMath.doubleFormat(format, avg3);
		}
		str += "\\\\\n";
		String strBottom = getBottomStr();
		return strHead+str+strBottom;
	}
	private static String getHeadStr(String title, String[] algname, String selAlg1, String selAlg2 ,boolean isAC) {
		// TODO Auto-generated method stub
		String str ="";
		str += "\\begin{table*}[htbp]\n"
		         +"\\centering\\caption{"+title+"}\n"
		         +"\\label{}\n"
		         +"\\resizebox{\\textwidth}{!}{ %\n"
		         +"\\begin{tabular}{lcccccccccccc}\n"
		         +"\\toprule"
		         +"\\multirow{3}{*}{\\centering $Datasets$}\n"
		         +"&\\multicolumn{3}{c}{$"+algname[0]+"$}\n"
		         +"&\\multicolumn{3}{c}{$"+algname[1]+"$}\n"
		         +"&\\multicolumn{3}{c}{$"+algname[2]+"$}\n"
		         +"&\\multicolumn{3}{c}{$"+algname[3]+"$}\\\\\n"
		         +"\\cmidrule{2-13}\n";
	 
		String strtmp1 = "";
		String strtmp2 = "";
		String Com = isAC?"ACC":"NMI";
		for(int i=0;i<algname.length-2;++i){
			strtmp1 +=  "&$"+selAlg1+"$&$"+selAlg2+"$&$FullAttr$";
			strtmp2 +=  "&"+Com+"$\\pm$Std &"+Com+"$\\pm$Std &"+Com+"$\\pm$Std";
		} 
		for(int i=algname.length-2;i<algname.length;++i){
			strtmp1 +=  "&$"+selAlg1+"$&$"+selAlg2+"$&$FullAttr$";
			strtmp2 +=  "&"+Com+"&"+Com+"&"+Com+"";
		} 
		strtmp1 += "\\\\\n";
		str += strtmp1+"\\cmidrule{2-13}\n";
		strtmp2 += "\\\\\n";
		str += strtmp2 ;
		return str+"\\midrule\n";
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
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		 
	}

}
