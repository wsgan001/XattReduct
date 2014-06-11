package Part2;

import java.text.DateFormat;
import java.util.Date;
import java.util.Vector;

import Part1.Ttest;
import Part1.showFigureLatex;
import Part1Ex.EvaluateMethodPart1Ex;
import PartAll.getDatas;

public class RunDataPart2 {

	public static void Run(boolean isTest,int numPic) throws Exception {
		  
		int runtimes = 5;
		boolean isTtest = true;
		int basernd = 0;
		double[] baseParameters={0.5};//1. per
		/*选择权重最大做基准 对齐投票*/
		String exppathjgp = "C:/Users/Eric/Desktop/2012秋冬/毕业设计/Paper/资料/Part2/实验/fig/";
		String path = "/Chapters/Part2/ex/";
		String exppatheps = "C:/Users/Eric/Desktop/2012秋冬/毕业设计/Paper/资料/Part2/实验/latex"+path;
		String latesavePath = "C:/Users/Eric/Desktop/2012秋冬/毕业设计/Paper/资料/Part2/实验/latex/";

		Vector<String[]> datainfo = getDatas.getClusterDatas(isTest);
		String str = getDatas.getTableinfoLatex(datainfo); //显示数据描述
	
		Vector<Vector<double[][]>> res = EvaluateMethodPart2.getFigures(datainfo,exppathjgp,exppatheps,runtimes,basernd,baseParameters);//获得eps jpg
	 
	    String[] keys = {"SINGLE","COMPLETE","AVERAGE","MEAN","BestHCM"};
		str += TtestPart2.getTableLatex(res,datainfo,isTtest,runtimes,basernd,baseParameters,keys);//显示比较表格latex
		str+=showFigureLatex.getAllPiclatexPart2(numPic, datainfo,"."+path);//获得pic的latex
		myUtils.xFile.writeNewFile(latesavePath, "latex_res_part2.tex", str);
		System.out.println("Done");
		
	 
		 Date now = new Date();
		 DateFormat d3 = DateFormat.getTimeInstance();
	      String str3 = d3.format(now);
	      System.out.println(str3);
	}	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
