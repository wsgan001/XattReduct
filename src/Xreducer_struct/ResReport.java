package Xreducer_struct;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Vector;

import myUtils.xMath;

public class ResReport {
	
	public Vector<ResData> records = null;
	public int clnum = 0;
	public int renum = 0;
	public int datanum = 0;
	

	public Vector<Double> avgNumRed = new Vector<Double>();
	public Vector<Double> avgRuntime = new Vector<Double>();
	public Vector<Vector<ResNode>> ClaDetail = new Vector<Vector<ResNode>>(); //clnum
	
	public ResReport(Vector<ResData> records){
		this.records = records;
		this.clnum = records.elementAt(0).claAlgnames.size();
		this.renum =  records.elementAt(0).redAlgnames.size();
		this.datanum = records.size();
		this.claculateAVG();
	}

	private void claculateAVG() {
		// TODO Auto-generated method stub
		for(int r=0;r<renum;++r){
			int tmpNumRed = 0;
			double tmpRuntime = 0;
			for(int d=0;d<datanum;++d){
				tmpNumRed += this.records.elementAt(d).numRed.elementAt(r);
				tmpRuntime += this.records.elementAt(d).timeRed.elementAt(r);
			}
			avgNumRed.add(xMath.getDoubleRound(2, (double)tmpNumRed/(double)datanum));
			avgRuntime.add(xMath.getDoubleRound(2, (double)tmpRuntime/(double)datanum));
		}
		for(int c=0;c<clnum;++c){
			Vector<ResNode> oneCla = new Vector<ResNode>();
			Vector<int[]> bwlt =new Vector<int[]>(); 
			double[] sumACtmp = new double[renum];
			for(int j=0;j<renum;++j){
				int[] tmp = new int[4];		// B W L T
				Arrays.fill(tmp, 0);
				bwlt.add(tmp);
				sumACtmp[j]=0.0;
			}
			for(int d=0;d<datanum;++d){
				for(int j=0;j<renum;++j){
					ResNode tmp = this.records.elementAt(d).resDetail.elementAt(j).elementAt(c);
					sumACtmp[j] += tmp.elvMean;
					int index = tmp.getWLT();
					bwlt.elementAt(j)[index]++;
				}
			}
			for(int j=0;j<renum;++j){
				ResNode tmp = new ResNode();
				tmp.elvMean = xMath.getDoubleRound(2, sumACtmp[j]/(double)datanum);
				String str = "";
				//for(int k=1;k<4;++k){
					//str += bwlt.elementAt(j)[k]+"/"; //ÎÄÕÂË³ÐòÎªlwt
				//}
				//str = str.substring(0, str.length()-1);
				str += bwlt.elementAt(j)[1]+"/";
				str += bwlt.elementAt(j)[2]+"/";
				str += bwlt.elementAt(j)[3]+"/";
				tmp.ltw = str;
				oneCla.add(tmp);
			}
			this.ClaDetail.add(oneCla);
		}

	}

	public String getLatexStr() {
		// TODO Auto-generated method stub
		String str = "";
		str += getDataInfoLatex();
		str += getNumRuntimeLatex();
		str += getACTable_pval();
		//str += getACTable_nopval();
		return str;
	}

	private String getACTable_nopval() {
		// TODO Auto-generated method stub
		boolean isResize = false;
		
		String str = "";
		String tip = "l";
		String tip2 = isResize?"":"%";
		String tip3 = "\t$DataSet$ &\n";
		for(int r=0;r<renum-1;++r){
			tip +="c";
			String rname = this.records.elementAt(0).redAlgnames.elementAt(r);
			tip3 += "\t$"+rname+"$ &\n";
		      
		}
		tip += "c"; 
		tip3 += "\t$"+this.records.elementAt(0).redAlgnames.elementAt(renum-1)+"$ \\\\\n\t\\midrule\n";

		String begin1 = "\\begin{table*}[htbp]\n"+
		               "\\centering\n"+
		               "%\\small\n"+
		               "\\caption{Accuracy of  algorithms with ";
	    String begin2 =" classifiers}\n"+
		               "\\label{tab:reduction:accresults:";
	    String begin3 ="}\n"+
		               tip2+"\\resizebox{\\textwidth}{!}{ %\n"+
		               "\t\\begin{tabular}{"+tip+"}\n"+
		               "\t\\toprule\n"+tip3;
	
		String end ="\t\\bottomrule\n"+
					"\t\\end{tabular}\n"+
					tip2+"}%\n"+
					"\\end{table*}\n";
		for(int c=0;c<clnum;++c){
			String claname = this.records.elementAt(0).claAlgnames.elementAt(c);
			String tmpcla = begin1+claname+begin2+claname.toLowerCase()+begin3;
			for(int d=0;d<this.datanum;++d){
				String tmpdata = "\t"+ this.records.elementAt(d).dataname;
				for(int r=0;r<renum-1;++r){
					tmpdata += "\t&\t" + this.records.elementAt(d).resDetail.elementAt(r).elementAt(c).elvMean + "$\\pm$" + 
							this.records.elementAt(d).resDetail.elementAt(r).elementAt(c).elvStd;
				}
				tmpdata += "\t&\t" + this.records.elementAt(d).resDetail.elementAt(renum-1).elementAt(c).elvMean+"\t\\\\\n";
				tmpcla += tmpdata;
			}
			tmpcla += "\t\\midrule\n\tAverage";
			for(int r=0;r<renum-1;++r){
				tmpcla += "\t&\t" + this.ClaDetail.elementAt(c).elementAt(r).elvMean;		     
			}
			tmpcla += "\t&\t" + this.ClaDetail.elementAt(c).elementAt(renum-1).elvMean+"\t\\\\\n";
			

			str += tmpcla+end+"\n\n\n";
		}
		
		return str;
	}

	private String getACTable_pval() {
		// TODO Auto-generated method stub
		boolean isResize = false;
		
		String str = "";
		String tip = "lc";
		String tip2 = isResize?"":"%";
		String tip3 = "\t\\multirow{3}{*}{\\centering $Datasets$}\n";
		String tip4 = "\t&$Acc\\pm Std$";
		
		String rname = this.records.elementAt(0).redAlgnames.elementAt(0);
		tip3 += "\t&\\multicolumn{1}{c}{$"+rname+"$}\n";
		for(int r=1;r<renum;++r){
			tip +="cl";
			rname = this.records.elementAt(0).redAlgnames.elementAt(r);
			tip3 += "\t&\\multicolumn{2}{c}{$"+rname+"$}\n";
		     tip4 +="\t&$Acc\\pm Std$&$\\rho$-$Value$";
		}
		tip3 += "\t\\\\\n\t\\cmidrule{2-"+(renum*2)+"}\n";
		tip4 +="\t\\\\\n\t\\midrule\n";
		String begin1 = "\\begin{table*}[htbp]\n"+
		               "\\centering\n"+
		               "%\\small\n"+
		               "\\caption{Accuracy of  algorithms with ";
	    String begin2 =" classifiers}\n"+
		               "\\label{tab:reduction:acwithpval:";
	    String begin3 ="}\n"+
		               tip2+"\\resizebox{\\textwidth}{!}{ %\n"+
		               "\t\\begin{tabular}{"+tip+"}\n"+
		               "\t\\toprule\n"+tip3+tip4;
	
		String end ="\t\\bottomrule\n"+
					"\t\\end{tabular}\n"+
					tip2+"}%\n"+
					"\\end{table*}\n";
		for(int c=0;c<clnum;++c){
			String claname = this.records.elementAt(0).claAlgnames.elementAt(c);
			String tmpcla = begin1+claname+begin2+claname.toLowerCase()+begin3;
			
			 String part1="";
			 String part2="\t\\midrule\n\tAverage($Acc$) ";
			 String part3="\t\\midrule\n\tWin/Lose/Tie  &\\multicolumn{1}{c}{ }";
			
			 for(int d=0;d<datanum;++d){
				 String tmpdata = "\t"+ this.records.elementAt(d).dataname;
				 
					tmpdata += "&" + this.records.elementAt(d).resDetail.elementAt(0).elementAt(c).elvMean + "$\\pm$" + 
					this.records.elementAt(d).resDetail.elementAt(0).elementAt(c).elvStd;					 
					for(int r=1;r<renum;++r){
						double pval = this.records.elementAt(d).resDetail.elementAt(r).elementAt(c).pval;
						String strpval = Double.isNaN(pval)?"1.00":new  DecimalFormat( "##0.00").format(pval); 
;
						tmpdata += "&" + this.records.elementAt(d).resDetail.elementAt(r).elementAt(c).elvMean + "$\\pm$" + 
								this.records.elementAt(d).resDetail.elementAt(r).elementAt(c).elvStd+
								"&" +strpval+
								//"$^{"+this.records.elementAt(d).resDetail.elementAt(r).elementAt(c).ltw+"}$";
								"$"+this.records.elementAt(d).resDetail.elementAt(r).elementAt(c).ltw+"$";
					}
					tmpcla += tmpdata +"\\\\\n";
					
					 
			 }
				part2 += "&\\multicolumn{1}{c}{"+this.ClaDetail.elementAt(c).elementAt(0).elvMean+"}";
				for(int r=1;r<renum;++r){				 
					part2 += "&\\multicolumn{2}{c}{"+this.ClaDetail.elementAt(c).elementAt(r).elvMean+"}";
					part3 += "&\\multicolumn{2}{c}{$"+this.ClaDetail.elementAt(c).elementAt(r).ltw+"$}";
				}
			
			tmpcla += part1+part2+"\\\\\n"+part3+"\\\\\n";
			str += tmpcla+end+"\n\n\n";
		}
		
		//System.out.println(str);
		return str;
	}

	private String getNumRuntimeLatex() {
		// TODO Auto-generated method stub
		String str = "";
		String tip = "l";
		String tip2 = "";
		String tip3 = "";
		for(int r=0;r<renum-1;++r){
			tip +="cr";
			tip2 += "\tSize&Time&\n";
			String rname = this.records.elementAt(0).redAlgnames.elementAt(r);
			tip3 += "\t\\multicolumn{2}{c}{$"+rname+"$} &\n";
		      
		}
		tip += "c"; 
		tip2 += "\tSize\\\\\n\t\\midrule\n"; 
		
		str ="\\begin{table*}[htbp]\n" +
				"\\centering\n" +
		  "%\\small\n" +
		  "%\\resizebox\n" +
		  "\\caption{Size of feature selection and using time}\n" +
		  "\\label{tab:reduction:sizetime}\n" +
		  "\t\\begin{tabular}{"+tip+"}\n" +
		  "\t\\toprule\n" +
		  "\t\\multirow{3}{*}{\\centering $Dataset$}&\n"+tip3+
		  "\t\\multicolumn{1}{c}{$FullFeatures$}\\\\\n"+
		  "\t\\cmidrule{2-"+(renum*2)+"}&\n"+tip2;
		for(int d=0;d<this.datanum;++d){
			String tmp = "\t"+this.records.elementAt(d).dataname;
			for(int r=0;r<renum-1;++r){
				tmp += "\t&\t"+this.records.elementAt(d).numRed.elementAt(r)+"\t&\t"+this.records.elementAt(d).timeRed.elementAt(r);
			}
			tmp += "\t&\t"+this.records.elementAt(d).numRed.elementAt(renum-1)+"\\\\\n";
			str += tmp;
		}

		str +="\t\\midrule\n\tAverage";
		for(int r=0;r<renum-1;++r){
			String tmp = "\t&\t"+this.avgNumRed.elementAt(r).toString()+"\t&\t"+this.avgRuntime.elementAt(r).toString();
			str += tmp;
		}
		str +="\t&\t"+this.avgNumRed.elementAt(renum-1).toString()+"\\\\\n";
		
		
		str += "\t\\bottomrule\n"+
	    		"\t\\end{tabular}\n"+
	    		"\\end{table*}\n\n\n";
		
		return str;
	}

	private String getDataInfoLatex() {
		// TODO Auto-generated method stub
		String str ="\\begin{table*}[htbp]\n"+
			      	"\\centering\n"+
			      	"%\\small\n"+
			      	"\\caption{Summary of the experiment datasets}\n"+
			      	"\\label{tab:reduction:datasetinfo}\n"+
			      	"%\\resizebox{0.5\\textwidth}{!}{\n"+
			      	"\t\\begin{tabular}{llllll}\n"+
			      	"\t\\toprule\n"+
			      	"\t\\multirow{3}{*}{\\centering $Dataset$} &\n " +
			      	"\t\\multirow{3}{*}{\\centering $Objects$} &\n " +
			      	"\t\\multicolumn{3}{c}{$Features$} & \n" +
			      	"\t\\multirow{3}{*}{\\centering $Classes$}\\\\\n"+
			      	"\t\\cmidrule{3-5}\n"+
			      	"\t&&$Total$&$Nominal$&$Numeric$\\\\\n"+
			      	"\t\\midrule\n";
		for(int d=0;d<this.datanum;++d){
		String tmp = "";
			tmp += "\t"+this.records.elementAt(d).dataname+"\t&\t";
			tmp += Integer.toString(this.records.elementAt(d).numObjects)+"\t&\t";
			tmp += Integer.toString(this.records.elementAt(d).numAttributes)+"\t&\t";
			tmp += Integer.toString(this.records.elementAt(d).numNominalAttributes)+"\t&\t";
			tmp += Integer.toString(this.records.elementAt(d).numNumericAttributes)+"\t&\t";
			tmp += Integer.toString(this.records.elementAt(d).numClasses)+"\t";
			tmp +="\\\\\n";
			str += tmp;
		}

		str+="\t\\bottomrule\n"+
			 "\t\\end{tabular}\n"+
			 "%}\n"+
			 "\\end{table*}\n\n\n";
		
		return str;
	}
}
