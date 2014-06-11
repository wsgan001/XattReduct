package Part34EX;

import myUtils.xMath;

public class showFigureLatexPart34 {
	public static void showLatexPart3(int cnt, String[] datanames, String type1, String type2 ){
		String head = "\\begin{figure}[pbt]\n";
		String bottom = "\\end{figure}";
		String path = "./Chapters/Part3/ex/";
		String label = "fig:chapter5:ex:"+type1.toLowerCase()+":"+type2.toLowerCase();
		String caption = "比较算法semiMRMR的特征选择个数和"+type1+"的分类精度关于参数"+type2;
		String width = xMath.doubleFormat("0.00", (double)1/(double)cnt);
		String ans = "";
		int cnttmp = 0;
		for(int i=0;i<datanames.length;++i){
			String captionx = datanames[i];
			String fname = datanames[i]+"_part3_eva_"+type1+"_"+type2;
			String labelx = "fig:chapter5:ex:"+datanames[i]+":"+type1.toLowerCase()+":"+type2.toLowerCase();
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
	public static void showLatexPart4(int cnt, String[] datanames, String type1, String type2 ){
		String head = "\\begin{figure}[pbt]\n";
		String bottom = "\\end{figure}";
		String path = "./Chapters/Part4/ex/";
		String label = "fig:chapter6:ex:"+type1.toLowerCase()+":"+type2.toLowerCase();
		String caption = "比较算法daulPOS的特征选择个数和"+type1+"的分类精度关于参数"+type2;
		String width = xMath.doubleFormat("0.00", (double)1/(double)cnt);
		String ans = "";
		int cnttmp = 0;
		for(int i=0;i<datanames.length;++i){
			String captionx = datanames[i];
			String fname = datanames[i]+"_part4_eva_"+type1+"_"+type2;
			String labelx = "fig:chapter6:ex:"+datanames[i]+":"+type1.toLowerCase()+":"+type2.toLowerCase();
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
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String[] datasPart3 = {
				"Colic",
				"Credit",
				//"Diabetes",
				"Heart-cleveland",
				"Heart-hungarian",
				//"Heart-statlog",
				"Hepatitis",
				"Ionosphere",
				"Promoters",
				"SPECT",
				//"Voting",
				"WDBC"
		};
		String[] datasPart4 = {
				//"Colic",
				"Credit",
				//"Diabetes",
				"Heart-cleveland",
				"Heart-hungarian",
				//"Heart-statlog",
				"Hepatitis",
				//"Ionosphere",
				"Promoters",
				//"SPECT",
				//"Voting",
				"WDBC"
		};
		showLatexPart3(3,datasPart3,"CART","alpha");
		System.out.println();
		System.out.println();
		System.out.println();
		showLatexPart3(3,datasPart3,"CART","per");
		System.out.println();
		System.out.println();
		System.out.println();
		showLatexPart4(3,datasPart4,"CART","per");
	}

}
