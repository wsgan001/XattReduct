package FCBFandRS;

import java.util.Vector;

import ResultsOperator.Vector2LaTex;
import Xreducer_core.Utils;
 

public class RunData {
	
	public static void main(String[] args) throws Exception {
		String path = "C:/Users/Eric/Desktop/2011Çï¶¬/Code/Xreducer/data/Data/";
		//String path = "C:/Users/Eric/Desktop/2012´º/Paper.new.NO3/";
		String sp = "C:/Users/Eric/Desktop/2012´º/Paper.new.NO3/results/experiment/";
		int bin = -1; // -1->MDL n->n¸ö¶Ïµã
		Vector<String> datas = new Vector<String>();
		
	    //no.3
		datas.add("colic");		
		datas.add("flag");	
		datas.add("ionosphere");
		datas.add("labor");
		datas.add("libras");	
		datas.add("olitos");	
		datas.add("sonar");	
		datas.add("wdbc");	
		datas.add("wine");	
		datas.add("heart");
		
		
		//datas.add("ionosphere");		
		//datas.add("wdbc");
		
		
		
		//datas.add("libras");
		//datas.add("labor");
		/*datas.add("wine");
		datas.add("vote");
		datas.add("soybean");		
		datas.add("audiology");		
		datas.add("waveform-5000");
		datas.add("olitos");
		datas.add("sonar");
		datas.add("flag");
		datas.add("colic");
		datas.add("credit"); 	
		datas.add("cleveland");
		datas.add("clean1");	
		datas.add("diabetes");		
		datas.add("heart");
		datas.add("wpbc-33");
		datas.add("diabetes");
		datas.add("ecoli");
		datas.add("glass");
		/*datas.add("web");
		datas.add("lung-Michigan");		
		datas.add("promoters");
		datas.add("Musk2");
		datas.add("MLL-train"); 
		datas.add("hillvalley");
		datas.add("handwritten");
		datas.add("multifeat");
		datas.add("Isolet_test");
		datas.add("secom"); 
		
		datas.add("arrhythmia");
		datas.add("dermatology");
		datas.add("hepatitis");
		datas.add("ozone");
		datas.add("libras");
		
		datas.add("CNS");
		datas.add("Colon-Cancer");
		datas.add("Leukemia");		
		datas.add("complete-9classes");
		datas.add("reduced-2classes");
		datas.add("DLBCLTumor");
		datas.add("tumors-C");*/ 
		String latexall = "";
		for(int i=0;i<datas.size();++i){
			String fn = path+datas.get(i)+".arff";	
			ComRedAlg ra = new ComRedAlg(fn,sp,bin);
			latexall+=ra.latexString;
			System.out.println("\n\n"+ra.getDataInfor()+ra.resString);
			System.out.println("Success!+At "+Utils.getCurrentDatatime()+"\n");
		}
		Vector2LaTex st = new Vector2LaTex(sp,latexall);
		st.latex2pdf_allData();
	}
}
