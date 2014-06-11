package Modification;

import java.util.Vector;

import myUtils.xFile;

import FCBFandRS.ComRedAlg;
import ResultsOperator.Vector2LaTex;
import Xreducer_core.Utils;
import Xreducer_struct.ResData;
import Xreducer_struct.ResNode;
import Xreducer_struct.ResReport;

public class Rundata_Reduct {
	public static void main(String[] args) throws Exception {
		String path = "C:/Users/Eric/Desktop/2011秋冬/Code/Xreducer/data/Data/";
		//String path = "C:/Users/Eric/Desktop/2012春/Paper.new.NO3/";
		String sp = "C:/Users/Eric/Desktop/2012秋冬/两篇要修改的文章/smbc/";
		int bin = -1; // -1->MDL n->n个断点
		Vector<String> datas = new Vector<String>();
		//inrateData
		 datas.add("arrhythmia");
		//datas.add("audiology");	
		datas.add("cleveland");
		 datas.add("colic");
		datas.add("credit"); 	
		//datas.add("dermatology");
		datas.add("hepatitis");
		//datas.add("labor");
		 datas.add("soybean");
		datas.add("vote");
		
		//noinrateData
		//datas.add("clean1");
		//datas.add("diabetes");	
		//datas.add("ecoli");
		//datas.add("flag");	
		//datas.add("glass");
		//datas.add("heart");
		//datas.add("ionosphere");
		//datas.add("libras");	
		//datas.add("Musk2");
		//datas.add("olitos");
		//datas.add("promoters");
		//datas.add("sonar");
		//datas.add("waveform");
 		//datas.add("WDBC");
 		//datas.add("wine");
 		//datas.add("web");

	
		String latexall = "";
		String latexsingle = "";
		Vector<ResData> AllResData = new Vector<ResData>(datas.size());
		for(int i=0;i<datas.size();++i){
			String fn = path+datas.get(i)+".arff";	
			ComRedAlg ra = new ComRedAlg(fn,sp,bin);
			AllResData.add(ra.getResDataInfo());	
			latexsingle+=ra.latexString;
			
			System.out.println("\n\n"+ra.getDataInfor()+ra.resString);
			System.out.println("Success!+At "+Utils.getCurrentDatatime()+"\n");
		}
		
		ResReport rp =  new ResReport(AllResData);
		latexall = rp.getLatexStr();
		xFile.writefile(sp, "AllResultsAll-"+Utils.getCurrentData(), latexall);
		Vector2LaTex st = new Vector2LaTex(sp,latexall,latexsingle);
		st.latex2pdf_allindepData();
	}
}
