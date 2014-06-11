package Part3;

import java.util.Vector;

import org.apache.commons.math.stat.descriptive.moment.Mean;

import myUtils.xMath;

public class EvaluateTimes {

	public static String getLatexStrforTime(String title, String[] dataNames, String[] sleAlg, String[] algNames, Vector<Vector<double[]>> res1, Vector<Vector<double[]>> res2){
		String str = "";
		String strHead = getHead(title,sleAlg,algNames);
		String strBottom = getBottom();
		double [][] values = new double[algNames.length*3+2][dataNames.length];
		for(int i=0;i<res1.size();++i){
			String aData = dataNames[i];
			double time1 = res1.elementAt(i).elementAt(0)[0];
			double time2 = res2.elementAt(i).elementAt(0)[0];
			values[0][i] = time1;
			values[1][i] = time2;
			aData += "&"+xMath.doubleFormat("0.000", time1)+"&"+xMath.doubleFormat("0.000", time2);
			int n =  res1.elementAt(i).size();
			for(int j=1;j<n;++j){
				double t1 =  res1.elementAt(i).elementAt(j)[0];
				double t2 =  res2.elementAt(i).elementAt(j)[0];
				double t3 =  (res1.elementAt(i).elementAt(j)[1]+res2.elementAt(i).elementAt(j)[1])/(double)2;
				aData += "&"+xMath.doubleFormat("0.000", t1)+"&"+xMath.doubleFormat("0.000", t2)+"&"+xMath.doubleFormat("0.000", t3);
				values[3*(j-1)+2][i] = t1;
				values[3*(j-1)+3][i] = t2;
				values[3*(j-1)+4][i] = t3;
			}
			aData +="\\\\\n";
			str+=aData;
		}
		str += "\\midrule\nAverage";
		Mean meanRednum = new Mean(); // 算术平均值
		double avgtime1 = meanRednum.evaluate(values[0]);
		double avgtime2 = meanRednum.evaluate(values[1]);
		str += "&"+xMath.doubleFormat("0.000", avgtime1)+"&"+xMath.doubleFormat("0.000", avgtime2);
		for(int j=1;j< res1.elementAt(0).size();++j){
			Mean mean = new Mean(); // 算术平均值
			double avg1 = mean.evaluate(values[3*(j-1)+2]);
			double avg2 = mean.evaluate(values[3*(j-1)+3]);
			double avg3 = mean.evaluate(values[3*(j-1)+4]);
			str += "&"+xMath.doubleFormat("0.000", avg1)+"&"+xMath.doubleFormat("0.000", avg2)+"&"+xMath.doubleFormat("0.000", avg3);
		}
		str += "\\\\\n";
		return strHead+str+strBottom;
	}
	public static String getLatexStrforTimeforPart1234EX(String title, String[] dataNames, String[] sleAlg, String[] algNames, Vector<Vector<double[]>> res1){
		String str = "";
		String strHead = getHead2(title,sleAlg,algNames);
		String strBottom = getBottom();
		double [][] values = new double[algNames.length*3+2][dataNames.length];
		for(int i=0;i<res1.size();++i){
			String aData = dataNames[i];

			int n =  res1.elementAt(i).size();
			for(int j=1;j<n;++j){
				double t1 =  res1.elementAt(i).elementAt(j)[0];
				double t2 =  res1.elementAt(i).elementAt(j)[1];
				double t3 =  res1.elementAt(i).elementAt(j)[2];
				aData += "&"+xMath.doubleFormat("0.000", t1)+"&"+xMath.doubleFormat("0.000", t2)+"&"+xMath.doubleFormat("0.000", t3);
				values[3*(j-1)+2][i] = t1;
				values[3*(j-1)+3][i] = t2;
				values[3*(j-1)+4][i] = t3;
			}
			aData +="\\\\\n";
			str+=aData;
		}
		str += "\\midrule\nAverage";
		for(int j=1;j< res1.elementAt(0).size();++j){
			Mean mean = new Mean(); // 算术平均值
			double avg1 = mean.evaluate(values[3*(j-1)+2]);
			double avg2 = mean.evaluate(values[3*(j-1)+3]);
			double avg3 = mean.evaluate(values[3*(j-1)+4]);
			str += "&"+xMath.doubleFormat("0.000", avg1)+"&"+xMath.doubleFormat("0.000", avg2)+"&"+xMath.doubleFormat("0.000", avg3);
		}
		str += "\\\\\n";
		return strHead+str+strBottom;
	}
	private static String getHead2(String title, String[] sleAlg,
			String[] algNames) {
		// TODO Auto-generated method stub
		String str ="";
		
		str += "\\begin{table*}[htbp]\n"
		         +"\\centering\n\\caption{"+title+"}\n"
		         +"\\label{}\n"
		         +"\\resizebox{0.75\\textwidth}{!}{ %\n"
		         +"\\begin{tabular}{lccc|ccc|ccc|ccc}\n"
		         +"\\toprule\n"
		         +"\\multirow{3}{*}{\\centering $Datasets$}\n"
		         +"&\\multicolumn{3}{c}{$"+algNames[0]+"$}\n"
		         +"&\\multicolumn{3}{c}{$"+algNames[1]+"$}\n"
		         +"&\\multicolumn{3}{c}{$"+algNames[2]+"$}\n"
		         +"&\\multicolumn{3}{c}{$"+algNames[3]+"$}\\\\\n"
		         +"\\cmidrule{2-13}\n";
		String strtmp = "";
		 
		for(int i=0;i<algNames.length;++i){
			strtmp +="&$"+sleAlg[0]+"$&$"+sleAlg[1]+"$"+"&$"+sleAlg[2]+"$"; 
		}
		 
		str += strtmp+"\\\\\n";
		return str+"\\midrule\n";
	}
	private static String getBottom() {
		// TODO Auto-generated method stub
		String str = "";
		str += "\\bottomrule\n"
			+"\\end{tabular}\n"
			+"}%\n"
			+"\\end{table*}\n";
		return str;
	}
	private static String getHead(String title, String[] sleAlg,
			String[] algNames) {
		// TODO Auto-generated method stub
		String str ="";
		
		str += "\\begin{table*}[htbp]\n"
		         +"\\centering\\caption{"+title+"}\n"
		         +"\\label{}\n"
		         +"\\resizebox{\\textwidth}{!}{ %\n"
		         +"\\begin{tabular}{lcc|ccc|ccc|ccc|ccc|ccc}\n"
		         +"\\toprule\n"
		         +"\\multirow{3}{*}{\\centering $Datasets$}\n"
		         +"&\\multicolumn{2}{c}{$FeatureSelection$}\n"
		         +"&\\multicolumn{3}{c}{$"+algNames[0]+"$}\n"
		         +"&\\multicolumn{3}{c}{$"+algNames[1]+"$}\n"
		         +"&\\multicolumn{3}{c}{$"+algNames[2]+"$}\n"
		         +"&\\multicolumn{3}{c}{$"+algNames[3]+"$}\n"
		         +"&\\multicolumn{3}{c}{$"+algNames[4]+"$}\\\\\n"
		         +"\\cmidrule{2-18}\n";
		String strtmp = "&$"+sleAlg[0]+"$&$"+sleAlg[1]+"$";
		 
		for(int i=0;i<algNames.length;++i){
			strtmp +="&$"+sleAlg[0]+"$&$"+sleAlg[1]+"$"+"&$"+sleAlg[2]+"$"; 
		}
		 
		str += strtmp+"\\\\\n";
		return str+"\\midrule\n";
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
	}

}
