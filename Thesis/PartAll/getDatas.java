package PartAll;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Vector;

import weka.core.Instances;

public class getDatas {

	public static Vector<String[]> getClusterDatas(boolean isTest){
		String path = "C:/Users/Eric/Desktop/2012秋冬/毕业设计/DataSet/";
		String postfix = ".arff";
		Vector<String[] > datainfo = new Vector<String[] > ();
		if(!isTest){
			String[][] datas=getAllClusterData();
			for(int i=0;i<datas.length;++i){
				String[] str = {path+datas[i][0]+postfix,datas[i][0],datas[i][1]};
				datainfo.add(str);
			}
		}
		else
		{
			String[][] datas=getPartsClusterData();
			for(int i=0;i<datas.length;++i){
				String[] str = {path+datas[i][0]+postfix,datas[i][0],datas[i][1]};
				datainfo.add(str);
			}
		}
		return datainfo;
	}
	
	public static Vector<String[]> getFeatureSelectionDatas(){
		String path = "C:/Users/Eric/Desktop/2012秋冬/毕业设计/DataSet/newFS/";
		String postfix = ".arff";
		Vector<String[] > datainfo = new Vector<String[] > ();
		
			String[][] datas=getFeatureSelectionData();
			for(int i=0;i<datas.length;++i){
				String[] str = {path+datas[i][0]+postfix,datas[i][0],datas[i][1]};
				datainfo.add(str);
			}
		
		return datainfo;
	}
	private static String[][] getPartsClusterData() {
		// TODO Auto-generated method stub
		String[][] datas={
				{"Audiology","Standardized version of the original audiology database"},
				{"Balance","Balance scale weight \\& distance database"},
				{"Balloons","Data previously used in cognitive psychology experiment"},
				{"BreastCancer","Breast Cancer Data (Restricted Access)"},
				//{"CarEvaluation","Derived from simple hierarchical decision model"},
				//{"Chess","King-Rook versus King-Pawn on a7 "},
				{"Hayes","Hayes-Roth Data Set from topic: human subjects study"},
				{"Lenses","Database for fitting contact lenses"},
				{"Lympho","Lymphography domain data set"},
				{"Monks","A set of artificial domains over the same attribute space"},
				//{"Mushroom","Mushrooms described in terms of physical characteristics"},
				//{"Nursery","Derived from a hierarchical decision model "},
				{"Shuttle","Shuttle Landing Control Data Set"},
				{"Soybean","Large version of Michalski's famous soybean disease database"},
				{"SPECT","Single Proton Emission Computed Tomography (SPECT) images. "},
				{"TicTac","Binary classification task on tic-tac-toe game"},
				{"Trains","2 data formats (structured, one-instance-per-line)"},
				{"Tumor","Primary Tumor Data Set from Ljubljana Oncology Institute"},
				{"Voting","1984 United Stated Congressional Voting Records"}
				
		};
		return datas;
	}
	private static String[][] getAllClusterData() {
		// TODO Auto-generated method stub
		String[][] datas={
				{"Audiology","Standardized version of the original audiology database"},
				//{"Balance","Balance scale weight \\& distance database"},
				{"Balloons","Data previously used in cognitive psychology experiment"},
				{"BreastCancer","Breast Cancer Data (Restricted Access)"},
				{"CarEvaluation","Derived from simple hierarchical decision model"},
				{"Chess","King-Rook versus King-Pawn on a7."},
				{"Hayes","Hayes-Roth Data Set from topic: human subjects study."},
				{"Lenses","Database for fitting contact lenses."},
				{"Lympho","Lymphography domain data set."},
				{"Monks","A set of artificial domains over the same attribute space."},
				{"Mushroom","Mushrooms described in terms of physical characteristics."},
				{"Nursery","Derived from a hierarchical decision model."},
				{"Promoters","E. Coli promoter gene sequences (DNA)."},
				{"Shuttle","Shuttle Landing Control Data Set."},
				{"Soybean","Large version of Michalski's famous soybean disease database."},
				{"SPECT","Single Proton Emission Computed Tomography (SPECT) images. "},
				//{"TicTac","Binary classification task on tic-tac-toe game"},
				{"Trains","2 data formats (structured, one-instance-per-line)."},
				{"Tumor","Primary Tumor Data Set from Ljubljana Oncology Institute."},
				{"Voting","1984 United Stated Congressional Voting Records."}
				
		};
		return datas;
	}
	private static String[][] getFeatureSelectionData() {
		// TODO Auto-generated method stub
		String[][] datas={
				
				
				{"Colic","Horse Colic Data Set; Well documented attributes."},
				{"Credit","This data concerns credit card applications; Good mix of attributes."},
				{"Diabetes","From National Institute of Diabetes and Digestive and Kidney Diseases."},
				{"Heart-cleveland","Heart Disease Data Set."},
				{"Heart-hungarian","Heart Disease Data Set."},
				{"Heart-statlog","Heart Disease Data Set."},
				{"Hepatitis","From G.Gong: CMU; Includes cost data (donated by Peter Turney)."},
				{"Ionosphere","Classification of radar returns from the ionosphere."},
				{"Musk2","Musk (Version 2) Data Set."},
				{"Promoters","E. Coli promoter gene sequences (DNA)."},
				{"SPECT","Single Proton Emission Computed Tomography (SPECT) images."},
				{"Voting","1984 United Stated Congressional Voting Records."},
				{"WDBC","Breast Cancer Wisconsin (Diagnostic) Data Set."}

		};
		return datas;
	}
	public static String getTableinfoLatex(Vector<String[] > datainfo) throws FileNotFoundException, IOException {
		String str = "%##############DataINFO#############\n";
		str += getHeadlatex();
		for(int i=0;i<datainfo.size();++i){
			String dataname = datainfo.elementAt(i)[1];
			String dataabstract =  datainfo.elementAt(i)[2];
			Instances data = new Instances(new FileReader(datainfo.elementAt(i)[0]));
			 data.setClassIndex(data.numAttributes()-1);
			 
			 str += (dataname+" & "+data.numInstances()+" & "+(data.numAttributes()-1)+" & "+data.numClasses()+" & "+dataabstract+"\\\\\n");
			 
		}
		str += getBottomlatex();
		// TODO Auto-generated method stub
		 
		return str;
	}
	private static String getHeadlatex() {
		// TODO Auto-generated method stub
		String str = "";
		str+="\\begin{table}\n"+
		  "\\centering\n"+
		  "\\caption{数据集信息}\\label{tab:chapter3:datainfo}\n"+
		  "\\resizebox{\\textwidth}{!}{ %\n"+
		  "\\begin{tabular}{lcccl}\n"+
		     "\\toprule\n"+
		    " $DataSet$ & $\\#Instances$ & $\\#Attribute$ & $\\#Classes$ & $Abstract$\\\\\n"+
		    "\\midrule\n";
		return str;
	}
	private static String getBottomlatex() {
		// TODO Auto-generated method stub
		String str = "";
		str+="\\bottomrule\n"+
		   "\\end{tabular}\n"+
		   "}%\n"+
		  "\\end{table}\n";
		return str;
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
