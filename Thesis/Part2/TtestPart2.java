package Part2;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Vector;

import myUtils.ClusterUtils;

import org.apache.commons.math.stat.descriptive.moment.Mean;
import org.apache.commons.math.stat.descriptive.moment.StandardDeviation;
import org.apache.commons.math.stat.inference.TestUtils;
 
 
 

import weka.clusterers.SimpleKMeans;
import weka.core.Instances;

public class TtestPart2 {

	 
	public static String getTableLatex(Vector<Vector<double[][]>> res ,Vector<String[]> datainfo,boolean isTtest, int runtimes,int basernd, double[] baseParameters,String[] keys) throws Exception
	{
		 
		Vector<String> paths = new Vector<String> ();
		Vector<String> pathNames = new Vector<String> ();
		for(int i=0;i<datainfo.size();++i){
			paths.add(datainfo.elementAt(i)[0]);
			pathNames.add(datainfo.elementAt(i)[1]);
		}
		
		String str = testAllfile(res, paths,pathNames,runtimes,isTtest,basernd,baseParameters,keys);
		return str;
		
	}

	private static String testAllfile(Vector<Vector<double[][]>> res ,Vector<String> paths,Vector<String> showNames, int times,boolean ispval,int basernd,double[] baseParameters,String[] keys) throws Exception{
		Vector<Vector<double[][]>>allRes = new Vector<Vector<double[][]>>() ;
		for(int i=0;i<paths.size();++i){
			 //Vector<double[][]> res = getRess(paths.elementAt(i),showNames.elementAt(i),times,basernd,baseParameters);
			Vector<double[][]> Tres = myTtest(res.elementAt(i)); // 0-ac 1-nmi 
			allRes.add(Tres);
		}
		System.out.println();
		System.out.println();
		return getTtestLatex(allRes,showNames,ispval,baseParameters,keys);
	}
	private static String getTtestLatex(Vector<Vector<double[][]>> allRes,Vector<String> showNames,boolean isPavl,double[] baseParameters, String[] keys) {
		// TODO Auto-generated method stub
		String str = "";
		  int lines = allRes.elementAt(0).elementAt(0).length;
		  int datacnt = allRes.size();
		  double[][] ac_avg = new double[datacnt][lines];
		  double[][] ac_std = new double[datacnt][lines];
		  double[][] ac_lwt = new double[datacnt][lines];
		  double[][] ac_pavl = new double[datacnt][lines];		
		  double[][] nmi_avg = new double[datacnt][lines];
		  double[][] nmi_std = new double[datacnt][lines];
		  double[][] nmi_lwt = new double[datacnt][lines];
		  double[][] nmi_pavl = new double[datacnt][lines];	
		  for(int i=0;i<allRes.size();++i)
		  {
			  for(int j=0;j<lines;++j){
				  ac_avg[i][j]=allRes.elementAt(i).elementAt(0)[j][0];
				  ac_std[i][j]=allRes.elementAt(i).elementAt(0)[j][1];
				  ac_lwt[i][j]=allRes.elementAt(i).elementAt(0)[j][2];
				  ac_pavl[i][j]=allRes.elementAt(i).elementAt(0)[j][3];
				  nmi_avg[i][j]=allRes.elementAt(i).elementAt(1)[j][0];
				  nmi_std[i][j]=allRes.elementAt(i).elementAt(1)[j][1];
				  nmi_lwt[i][j]=allRes.elementAt(i).elementAt(1)[j][2];
				  nmi_pavl[i][j]=allRes.elementAt(i).elementAt(1)[j][3];
			  }
		  }
		  double[] all_ac_avg = new double[lines];
		  Vector<int[]> all_ac_lwt = new Vector<int[]>();
		  double[] all_nmi_avg = new double[lines];
		  Vector<int[]> all_nmi_lwt = new Vector<int[]>();
			  for(int j=0;j<lines;++j){
				  double tmp_ac = 0;
				  double tmp_nmi = 0;
				  for(int i=0;i<allRes.size();++i)
				  {
					  tmp_ac += ac_avg[i][j];
					  tmp_nmi += nmi_avg[i][j];
				  }
				  all_ac_avg[j]=tmp_ac/(double)datacnt;
				  all_nmi_avg[j]=tmp_nmi/(double)datacnt;
			  }
			  for(int j=0;j<lines-1;++j){
				  int tmp_ac_l = 0;
				  int tmp_ac_w = 0;
				  int tmp_ac_t = 0;
				  int tmp_nmi_l = 0;
				  int tmp_nmi_w = 0;
				  int tmp_nmi_t = 0;
				  for(int i=0;i<allRes.size();++i)
				  {
					  switch((int)ac_lwt[i][j]){
					  case 1:{tmp_ac_l++;break;}
					  case 2:{tmp_ac_w++;break;}
					  case 3:{tmp_ac_t++;break;}
					  default:break;
					  }
					  switch((int)nmi_lwt[i][j]){
					  case 1:{tmp_nmi_l++;break;}
					  case 2:{tmp_nmi_w++;break;}
					  case 3:{tmp_nmi_t++;break;}
					  default:break;
					  }
				  }
				  int[] tmp_ac = {tmp_ac_l,tmp_ac_w,tmp_ac_t};
				  int[] tmp_nmi = {tmp_nmi_l,tmp_nmi_w,tmp_nmi_t};
				  all_ac_lwt.add(tmp_ac);
				  all_nmi_lwt.add(tmp_nmi);
			  }
			  String head_ac = getHead(isPavl,0,baseParameters,keys);
			  String head_nmi = getHead(isPavl,1,baseParameters,keys); 
			  String bottom = getBottom();
			  //ac-with-pavl
			  String str_ac = "";
			  if(isPavl){
				  for(int i=0;i<allRes.size();++i)
				  {
					  str_ac+=showNames.elementAt(i);
					  for(int j=0;j<lines;++j){
						  String avg = doubleFormat("0.00", ac_avg[i][j]*100);
						  String std = doubleFormat("0.00", ac_std[i][j]*100);
						  String t = "";
							String l="";
							int intt = (int)ac_lwt[i][j];
							switch(intt){
							case 0: {t = "B";l="\\Large\\circ";break;}
							case 2: {t = "W";l="\\Large\\textcolor{red}{\\checkmark}";break;}
							case 1: {t = "L";l="\\Large$\\mathbf{\\times}$";break;}
							case 3: {t = "T";l="\\Large-";break;}
							default:break;
							}
						 	String pval = Double.isNaN(ac_pavl[i][j])?"1.00":doubleFormat("0.00", ac_pavl[i][j]);
						 	if(j!=lines-1)
						 		str_ac+= " & "+avg+"$\\pm$"+std+" & "+pval+l;
						 	else
						 		str_ac+= " & "+avg+"$\\pm$"+std+"\\\\";
					  }
					  str_ac+="\n";
				  }
				  String avg = "\\midrule\nAverage";
				  for(int j=0;j<lines-1;++j){
					  avg+="&\\multicolumn{2}{c}{ "+doubleFormat("0.00", all_ac_avg[j]*100)+" } ";
				  }
				  avg+="&\\multicolumn{1}{c}{ "+doubleFormat("0.00", all_ac_avg[lines-1]*100)+" } ";
				  avg+="\\\\\n";
				  String lwt = "\\midrule\nWin/Tie/Lose";
				  for(int j=0;j<lines-1;++j){
					  String l = all_ac_lwt.elementAt(j)[1]+"/"+all_ac_lwt.elementAt(j)[2]+"/"+all_ac_lwt.elementAt(j)[0];
					  lwt+="&\\multicolumn{2}{c}{ "+l+" } ";
				  }
				  lwt+="&\\multicolumn{1}{c}{}\\\\\n";
				  str_ac += avg+lwt;
			  }
			  else{
				  for(int i=0;i<allRes.size();++i)
				  {
					  str_ac+=showNames.elementAt(i);
					  for(int j=0;j<lines;++j){
						  String avg = doubleFormat("0.00", ac_avg[i][j]*100);
						  String std = doubleFormat("0.00", ac_std[i][j]*100);
						 	if(j!=lines-1)
						 		str_ac+= " & "+avg+"$\\pm$"+std;
						 	else
						 		str_ac+= " & "+avg+"$\\pm$"+std+" \\\\";
					  }
				  }
				  str_ac+="\n";
				  String avg = "\\midrule\nAverage";
				  for(int j=0;j<lines;++j){
					  avg+=" & "+doubleFormat("0.00", all_ac_avg[j]*100);
				  }
				  avg+="\\\\\n";
				  str_ac += avg;
			  }
			  //System.out.println("%##############AC##########");
			  //System.out.println(head_ac+str_ac+bottom);
			  str+="%##############AC##########\n";
			  str+=head_ac+str_ac+bottom+"\n";
			  
			  
			  String str_nmi = "";
			  if(isPavl){
				  for(int i=0;i<allRes.size();++i)
				  {
					  str_nmi+=showNames.elementAt(i);
					  for(int j=0;j<lines;++j){
						  String avg = doubleFormat("0.00", nmi_avg[i][j]);
						  String std = doubleFormat("0.00", nmi_std[i][j]);
						  String t = "";
							String l="";
							int intt = (int)nmi_lwt[i][j];
							switch(intt){
							case 0: {t = "B";l="\\Large\\circ";break;}
							case 2: {t = "W";l="\\Large\\textcolor{red}{\\checkmark}";break;}
							case 1: {t = "L";l="\\Large$\\mathbf{\\times}$";break;}
							case 3: {t = "T";l="\\Large-";break;}
							default:break;
							}
						 	String pval = Double.isNaN(nmi_pavl[i][j])?"1.00":doubleFormat("0.00", nmi_pavl[i][j]);
						 	if(j!=lines-1)
						 		str_nmi+= " & "+avg+"$\\pm$"+std+" & "+pval+l;
						 	else
						 		str_nmi+= " & "+avg+"$\\pm$"+std+"\\\\";
					  }
					  str_nmi+="\n";
				  }
				  String avg = "\\midrule\nAverage";
				  for(int j=0;j<lines-1;++j){
					  avg+="&\\multicolumn{2}{c}{ "+doubleFormat("0.00", all_nmi_avg[j])+" } ";
				  }
				  avg+="&\\multicolumn{1}{c}{ "+doubleFormat("0.00", all_nmi_avg[lines-1])+" } ";
				  avg+="\\\\\n";
				  String lwt = "\\midrule\nWin/Tie/Lose";
				  for(int j=0;j<lines-1;++j){
					  String l = all_nmi_lwt.elementAt(j)[1]+"/"+all_nmi_lwt.elementAt(j)[2]+"/"+all_nmi_lwt.elementAt(j)[0];
					  lwt+="&\\multicolumn{2}{c}{ "+l+" } ";
				  }
				  lwt+="&\\multicolumn{1}{c}{}\\\\\n";
				  str_nmi += avg+lwt;
			  }
			  else{
				  for(int i=0;i<allRes.size();++i)
				  {
					  str_nmi+=showNames.elementAt(i);
					  for(int j=0;j<lines;++j){
						  String avg = doubleFormat("0.00", nmi_avg[i][j]);
						  String std = doubleFormat("0.00", nmi_std[i][j]);
						 	if(j!=lines-1)
						 		str_nmi+= " & "+avg+"$\\pm$"+std;
						 	else
						 		str_nmi+= " & "+avg+"$\\pm$"+std+" \\\\";
					  }
				  }
				  str_nmi+="\n";
				  String avg = "\\midrule\nAverage";
				  for(int j=0;j<lines;++j){
					  avg+=" & "+doubleFormat("0.00", all_nmi_avg[j]);
				  }
				  avg+="\\\\\n";
				  str_nmi += avg;
			  }
			  //System.out.println("%##############NMI##########");
			  //System.out.println(head_nmi+str_nmi+bottom);
			  str+="%##############NMI##########\n";
			  str+=head_nmi+str_nmi+bottom+"\n";
			  return str;
		  
	}
	private static String getBottom() {
		// TODO Auto-generated method stub
		
			return "\\bottomrule\n\\end{tabular}\n}%\n\\end{table*}";

	}
	private static String getHead(boolean isPavl,int type, double[] p, String [] keys) {
		String avg = type==0?"ACC":"NMI";
		// TODO Auto-generated method stub
		String str = "";
		 
		String per = doubleFormat("0.00", p[0]);
		String title = "比较"+(type==0?"AC":"NMI")+":参数为 Labeled percent="+per;
		String label = "";
		
		str +="\\begin{table*}[htbp]\n\\centering\\caption{"+title+"}\n\\label{"+label+"}\n";
		if(isPavl){
			str +=" \\resizebox{\\textwidth}{!}{ %\n";
			str +="\\begin{tabular}{lccccccccc}\n\\toprule\n";
			str +="\\multirow{3}{*}{\\centering $Datasets$}\n";
			for(int i=0;i<keys.length-1;++i)
				str+="&\\multicolumn{2}{c}{$"+keys[i]+"$}\n";
			str+="&\\multicolumn{1}{c}{$"+keys[keys.length-1]+"$}\\\\\n";
			str+="\\cmidrule{2-10}\n";
			for(int i=0;i<keys.length-1;++i){
				str+="&"+avg+"$\\pm$Std&$\\rho$-Value";
			}
			str+="&"+avg+"$\\pm$Std\\\\\n\\midrule\n";
		}
		else{
			str +=" \\resizebox{\\textwidth}{!}{ %\n";
			str +="\\begin{tabular}{lccccc}\n\\toprule\n";
			str +="\\multirow{3}{*}{\\centering $Datasets$}\n";
			for(int i=0;i<keys.length-1;++i)
				str+="&\\multicolumn{1}{c}{$"+keys[i]+"$}\n";
			str+="&\\multicolumn{1}{c}{$"+keys[keys.length-1]+"$}\\\\\n";
			str+="\\cmidrule{2-6}\n";
			for(int i=0;i<keys.length-1;++i){
				str+="&"+avg+"$\\pm$STD";
			}
			str+="&"+avg+"$\\pm$Std\\\\\n\\midrule\n";
		}
		           
		              
		                
		               
		return str;
	}
	private static String doubleFormat(String str,double x){
		return new DecimalFormat(str).format(x);
	}
	private static Vector<double[][]> myTtest(Vector<double[][]> res) throws Exception {
		// TODO Auto-generated method stub
		int lines = res.size();
		int allvalues = res.elementAt(0)[0].length;
		double singificantlevel = 0.05;
		int baseind = res.size()-1;
		double [][] X_ac = new double[lines][allvalues];
		double [][] X_nmi = new double[lines][allvalues];
		for(int i=0;i<lines;++i){
			for(int j=0;j<allvalues;++j){
				X_ac[i][j] = res.elementAt(i)[0][j];
				X_nmi[i][j]=res.elementAt(i)[1][j];
			}
			
		}
		 Vector<double[][]> ans = new  Vector<double[][]> ();
		 ans.add(MypairedTtest(X_ac,baseind,singificantlevel));
		 ans.add(MypairedTtest(X_nmi,baseind,singificantlevel));
		return ans;
	}
	private static double[][] MypairedTtest(double[][] X, int baseindex , double singificantlevel) throws Exception{
		// 0 -avg 1-std 2-wtl(win-2 lose-1 tie-3 base- 0) 3 pval
		
		int N = X.length;
		//System.out.println("##################");
		//for(int i=0;i<X.length;++i)
			//System.out.println(Arrays.toString(X[i]));
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
		res[baseindex][2]=0;
		res[baseindex][3]=-1;
		return res;
	}

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		 
	}

}
