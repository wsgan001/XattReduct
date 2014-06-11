package Part1Ex;

import java.text.DateFormat;
import java.util.Date;
import java.util.Vector;
 
import Part1.Ttest;
import Part1.showFigureLatex;
import PartAll.getDatas;

public class RunDataPart1Ex {
	public static void Run(boolean isTest,int numPic) throws Exception {
		  
		int runtimes = 10;
		boolean isTtest = true;
		int basernd = 0;
		double[] baseParameters={0.6,15,0.15};//1.alpha 2.ensize 3 per
		/*ѡ��Ȩ���������׼ ����ͶƱ*/
		String exppathjgp = "C:/Users/Eric/Desktop/2012�ﶬ/��ҵ���/Paper/����/Part1/ʵ��/fig/";
		String path = "/Chapters/Part1/ex2/";
		String exppatheps = "C:/Users/Eric/Desktop/2012�ﶬ/��ҵ���/Paper/����/Part1/ʵ��/latex"+path;
		String latesavePath = "C:/Users/Eric/Desktop/2012�ﶬ/��ҵ���/Paper/����/Part1/ʵ��/latex/";

		Vector<String[]> datainfo = getDatas.getClusterDatas(isTest);
		String str = getDatas.getTableinfoLatex(datainfo); //��ʾ��������
	
		Vector<Vector<double[][]>> res = EvaluateMethodPart1Ex.getFigures(datainfo,exppathjgp,exppatheps,runtimes,basernd,baseParameters);//���eps jpg
	 
	    String[] keys = {"w\\_kmodes","s\\_w\\_kmodes","random\\_w\\_kmodes","random\\_s\\_w\\_kmodes","single\\_kmodes"};
		str += Ttest.getTableLatex(res,datainfo,isTtest,runtimes,basernd,baseParameters,keys);//��ʾ�Ƚϱ���latex
		str+=showFigureLatex.getAllPiclatex(numPic, datainfo,"."+path);//���pic��latex
		myUtils.xFile.writeNewFile(latesavePath, "latex_res_part1_ex.tex", str);
		System.out.println("Done");
		
	 
		 Date now = new Date();
		 DateFormat d3 = DateFormat.getTimeInstance();
	      String str3 = d3.format(now);
	      System.out.println(str3);
	}	
}