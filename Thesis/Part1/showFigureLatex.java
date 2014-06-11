package Part1;

import java.text.DecimalFormat;
import java.util.Vector;

public class showFigureLatex {

	private static void showLatex(int cnt, String[] datanames, String type1, String type2 ){
		String head = "\\begin{figure}[pbt]\n";
		String bottom = "\\end{figure}";
		String path = "./Chapters/Part1/ex/";
		String label = "fig:chapter3:ex:"+type1.toLowerCase()+":"+type2.toLowerCase();
		String caption = "比较"+type1.toUpperCase()+"基于"+type2.toUpperCase();
		String width = doubleFormat("0.00", (double)1/(double)cnt);
		String ans = "";
		int cnttmp = 0;
		for(int i=0;i<datanames.length;++i){
			String captionx = datanames[i];
			String fname = datanames[i]+"-"+type1+"-"+type2;
			String labelx = "fig:chapter3:ex:"+datanames[i]+":"+type1.toLowerCase()+":"+type2.toLowerCase();
			ans+="\\begin{minipage}{"+width+"\\linewidth}\n"
			+"\\centering\n"
			+"\\includegraphics[width=1\\textwidth]{"+path+fname+".eps}\n"
			//+"\\caption{"+captionx+"}\n"
			//+"\\label{"+labelx+"}\n"
			+"\\end{minipage}%\n";
			
			cnttmp++;
			if(cnttmp==cnt){
				ans+="\\\\\n";
				cnttmp=0;
			}
		}
		ans+="\\caption{"+caption+"}\n";
		ans+="\\label{"+label+"}\n";
		System.out.println(head+ans+bottom);
	}
	private static String getPicLatex(int cnt, Vector<String[]> datainfo,String path, String type1, String type2 ){
		String head = "\\begin{figure}[pbt]\n";
		String bottom = "\\end{figure}";
	
		String label = "fig:chapter3:ex:"+type1.toLowerCase()+":"+type2.toLowerCase();
		String caption = "比较"+type1.toUpperCase()+"基于"+type2.toUpperCase();
		String width = doubleFormat("0.00", (double)1/(double)cnt);
		String ans = "";
		int cnttmp = 0;
		for(int i=0;i<datainfo.size();++i){
			String DataNames = datainfo.elementAt(i)[1];
			String captionx = DataNames;
			String fname = DataNames+"-"+type1+"-"+type2;
			String labelx = "fig:chapter3:ex:"+DataNames+":"+type1.toLowerCase()+":"+type2.toLowerCase();
			ans+="\\begin{minipage}{"+width+"\\linewidth}\n"
			+"\\centering\n"
			+"\\includegraphics[width=1\\textwidth]{"+path+fname+".eps}\n"
			//+"\\caption{"+captionx+"}\n"
			//+"\\label{"+labelx+"}\n"
			+"\\end{minipage}%\n";
			
			cnttmp++;
			if(cnttmp==cnt){
				ans+="\\\\\n";
				cnttmp=0;
			}
		}
		ans+="\\caption{"+caption+"}\n";
		ans+="\\label{"+label+"}\n";
		return head+ans+bottom;
		//System.out.println(head+ans+bottom);
	}
	private static String doubleFormat(String str,double x){
		return new DecimalFormat(str).format(x);
	}
	public static String getAllPiclatex(int n, Vector<String[]>  datainfo,String path){
		String str = "\n%##############Figures############\n";
		str+=getPicLatex(n,datainfo,path,"alpha","AC")+"\n";
		str+=getPicLatex(n,datainfo,path,"alpha","NMI")+"\n";
		str+=getPicLatex(n,datainfo,path,"ensize","AC")+"\n";
		str+=getPicLatex(n,datainfo,path,"ensize","NMI")+"\n";
		str+=getPicLatex(n,datainfo,path,"per","AC")+"\n";
		str+=getPicLatex(n,datainfo,path,"per","NMI")+"\n";
		return str;
	}
	public static String getAllPiclatexPart2(int n, Vector<String[]>  datainfo,String path){
		String str = "\n%##############Figures############\n";
		//str+=getPicLatex(n,datainfo,path,"alpha","AC")+"\n";
		//str+=getPicLatex(n,datainfo,path,"alpha","NMI")+"\n";
		//str+=getPicLatex(n,datainfo,path,"ensize","AC")+"\n";
		//str+=getPicLatex(n,datainfo,path,"ensize","NMI")+"\n";
		str+=getPicLatex(n,datainfo,path,"per","AC")+"\n";
		str+=getPicLatex(n,datainfo,path,"per","NMI")+"\n";
		return str;
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		int n = 3;
		// TODO Auto-generated method stub
		//String[] datanames = { "Balloons","Trains","CongressionalVoting"};
		/*String[] datanames = {"Audiology","Balance","Balloons","BreastCancer",
				"CarEvaluation","Chess","Hayes",
				"Lenses","Lympho","Monks","Mushroom",
				"Nursery","Promoters","Shuttle","Soybean",
				"SPECT","TicTac","Trains","Tumor","Voting"
		};*/
		String[] datanames = {"Ionosphere","Wine","Wpbc-34"};
		showLatex(n,datanames,"alpha","AC");
		showLatex(n,datanames,"alpha","NMI");
		showLatex(n,datanames,"ensize","AC");
		showLatex(n,datanames,"ensize","NMI");
		showLatex(n,datanames,"per","AC");
		showLatex(n,datanames,"per","NMI");
	}

}
