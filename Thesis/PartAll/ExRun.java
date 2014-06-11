package PartAll;

import java.text.DateFormat;
import java.util.Date;
import java.util.Vector;

import Part1.RunDataPart1;
import Part1Ex.RunDataPart1Ex;
import Part2.RunDataPart2;
import Part3.EvaluateAllData;
import Part34EX.EvaluatePart1234ForALLdata;


public class ExRun {
	public static void printTime(){
		 Date now = new Date();
		 DateFormat d3 = DateFormat.getTimeInstance();
	     String str3 = d3.format(now);
	     System.out.println(str3);
	}
	public static void RunPart1() throws Exception{
		boolean isTest = false;
		int numPic = 3;
		System.out.println("实验1 voting semi_w --> ac  nmi_w -->beta ac 一对一");
		printTime();
		RunDataPart1.Run(isTest,numPic);
		//printTime();
		//System.out.println("实验2 kmodes");
		//RunDataPart1Ex.Run(isTest,numPic);
		printTime();
	}
	public static void RunPart1exTest() throws Exception{
		boolean isTest = true;
		int numPic = 4;
		 
		System.out.println("实验2 kmodes");
		RunDataPart1Ex.Run(isTest,numPic);
		printTime();
	}
	public static void RunPart2Test() throws Exception{
		boolean isTest = true;
		int numPic = 3;
		printTime();
		RunDataPart2.Run(isTest, numPic);
		printTime();
		System.out.println("Done");
	}
	public static void RunPart34() throws Exception{
		 Vector<String[]> fns = getDatas.getFeatureSelectionDatas();
		 String sfp = "C:/Users/Eric/Desktop/2012秋冬/毕业设计/Paper/资料/Part3/latex/";
		 String datainfoLatexStr = getDatas.getTableinfoLatex(fns);
		 myUtils.xFile.writeNewFile(sfp, "latex_res_part3DATAINFO.tex", datainfoLatexStr);
		Vector<String> newfns = new Vector<String>();
		Vector<String> dataNames = new Vector<String>();
			for(int i=0;i<fns.size();++i){
				newfns.add(fns.elementAt(i)[0]);
				dataNames.add(fns.elementAt(i)[1]);
			}
			double semiMRMR = 0.1;
			double daulPOS = 1;	 
			//EvaluateAllData tad = new EvaluateAllData(fns,names,semiMRMR, daulPOS);
			EvaluateAllData.EvaluateAllDataforTable(sfp,newfns,dataNames,semiMRMR, daulPOS);
	}
	public static void RunPart1234() throws Exception{
		 Vector<String[]> fns = getDatas.getFeatureSelectionDatas();
		 String sfp = "C:/Users/Eric/Desktop/2012秋冬/毕业设计/Paper/资料/Part3/latex/";
		  
		Vector<String> newfns = new Vector<String>();
		Vector<String> dataNames = new Vector<String>();
			for(int i=0;i<fns.size();++i){
				newfns.add(fns.elementAt(i)[0]);
				dataNames.add(fns.elementAt(i)[1]);
			}
			double semiMRMR = 0.1;
			double daulPOS = 1;	 
			double per = 0.5;
			//EvaluateAllData tad = new EvaluateAllData(fns,names,semiMRMR, daulPOS);
			EvaluatePart1234ForALLdata.EvaluateAllDataforTable(sfp,newfns,dataNames,semiMRMR, daulPOS,per);
	}
	public static void main(String[] args) throws Exception {
		//RunPart2Test();
		//RunPart1();
		//RunPart34();
		RunPart1234();
	}
}
