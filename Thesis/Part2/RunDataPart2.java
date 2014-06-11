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
		/*ѡ��Ȩ���������׼ ����ͶƱ*/
		String exppathjgp = "C:/Users/Eric/Desktop/2012�ﶬ/��ҵ���/Paper/����/Part2/ʵ��/fig/";
		String path = "/Chapters/Part2/ex/";
		String exppatheps = "C:/Users/Eric/Desktop/2012�ﶬ/��ҵ���/Paper/����/Part2/ʵ��/latex"+path;
		String latesavePath = "C:/Users/Eric/Desktop/2012�ﶬ/��ҵ���/Paper/����/Part2/ʵ��/latex/";

		Vector<String[]> datainfo = getDatas.getClusterDatas(isTest);
		String str = getDatas.getTableinfoLatex(datainfo); //��ʾ��������
	
		Vector<Vector<double[][]>> res = EvaluateMethodPart2.getFigures(datainfo,exppathjgp,exppatheps,runtimes,basernd,baseParameters);//���eps jpg
	 
	    String[] keys = {"SINGLE","COMPLETE","AVERAGE","MEAN","BestHCM"};
		str += TtestPart2.getTableLatex(res,datainfo,isTtest,runtimes,basernd,baseParameters,keys);//��ʾ�Ƚϱ��latex
		str+=showFigureLatex.getAllPiclatexPart2(numPic, datainfo,"."+path);//���pic��latex
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
