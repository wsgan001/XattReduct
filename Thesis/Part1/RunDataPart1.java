package Part1;

import java.text.DateFormat;
import java.util.Date;
import java.util.Vector;

import PartAll.getDatas;

public class RunDataPart1 {

	public static void Run(boolean isTest,int numPic) throws Exception {
  
		int runtimes = 10;
		boolean isTtest = true;
		int basernd = 0;
		double[] baseParameters={0.6,15,0.15};//1.alpha 2.ensize 3 per
		/*选择权重最大做基准 对齐投票*/
		String exppathjgp = "C:/Users/Eric/Desktop/2012秋冬/毕业设计/Paper/资料/Part1/实验/fig/";
		String path = "/Chapters/Part1/ex/";
		String exppatheps = "C:/Users/Eric/Desktop/2012秋冬/毕业设计/Paper/资料/Part1/实验/latex"+path;
		String latesavePath = "C:/Users/Eric/Desktop/2012秋冬/毕业设计/Paper/资料/Part1/实验/latex/";

	 
		Vector<String[]> datainfo = getDatas.getClusterDatas(isTest);
		String str = getDatas.getTableinfoLatex(datainfo); //显示数据描述
	
		Vector<Vector<double[][]>> res = EvaluateMethod.getFigures(datainfo,exppathjgp,exppatheps,runtimes,basernd,baseParameters);//获得eps jpg
		 String[] keys = {"w\\_voting","s\\_w\\_voting","random\\_w\\_voting","random\\_s\\_w\\_voting","single\\_kmodes"};
		str += Ttest.getTableLatex(res,datainfo,isTtest,runtimes,basernd,baseParameters,keys);//显示比较表格latex
		str+=showFigureLatex.getAllPiclatex(numPic, datainfo,"."+path);//获得pic的latex
		myUtils.xFile.writeNewFile(latesavePath, "latex_res_part1.tex", str);
		System.out.println("Done");
		
	 
		 Date now = new Date();
		 DateFormat d3 = DateFormat.getTimeInstance();
	      String str3 = d3.format(now);
	      System.out.println(str3);
	}
	
}
