package Part34EX;

import java.util.Vector;

import com.lowagie.text.DocumentException;

import myUtils.xFile;

public class Res2EPS {

	public static int[] findRange(double[] A, double[] Base){
		int Max = -1;
		int Min = 10000000;
		for(int i=0;i<A.length;++i){
			if((int)A[i]>Max)
				Max = (int)A[i];
			if((int)Base[i]>Max)
				Max = (int)Base[i];
			if((int)A[i]<Min)
				Min = (int)A[i];
			if((int)Base[i]<Min)
				Min = (int)Base[i];
		}
		int[] ans = new int[2];
		ans[0]=Min-2;
		ans[1]=Max+1;
		return ans;
	}
	public static double[] findXRange(double[] A){
		double Max = -1;
		double Min = 10000000;
		for(int i=0;i<A.length;++i){
			if(A[i]>Max)
				Max = A[i];
			 
			if(A[i]<Min)
				Min = A[i];
			 
		}
		double[] ans = new double[2];
		ans[0]=Min;
		ans[1]=Max;
		return ans;
	}
	private static String findTics(double[] elementAt, double[] elementAt2) {
		// TODO Auto-generated method stub
		int[] tmp = new int[(int)elementAt2[0]+1];
		for(int i=0;i<elementAt.length;++i){
			tmp[(int)elementAt[i]]++;
			tmp[(int)elementAt2[i]]++;
		}
		String str = "";
		for(int i=tmp.length-1;i>=0;--i){
			if(tmp[i]>0)
				str+=i+",";
		}
		
		return str.substring(0, str.length()-1);
	}
	public static void generateEpsForPart3ex(Vector<double[]> res, String xLabel,String path, String title, String saveposfix) throws DocumentException{
		String str_data = "";
		String dataName = title+"_data.dat";
		for(int i=0;i<res.elementAt(0).length;++i){
			str_data+=res.elementAt(0)[i]+" "+res.elementAt(1)[i]+" "+res.elementAt(2)[i]+" "+res.elementAt(3)[i]+" "+res.elementAt(4)[i]+"\n";
		}
		int[] yRange = findRange(res.elementAt(1),res.elementAt(2));
		double[] xRange = findXRange(res.elementAt(0));
		String yTics = findTics(res.elementAt(1),res.elementAt(2));
		xFile.writeNewFile(path, dataName, str_data);
		String str_gnuplot = getGNUplotStr(path,xLabel,title,dataName,yRange,xRange,yTics,saveposfix);
		xFile.writeNewFile(path, title+"_gunplot.plt", str_gnuplot);
	}

	private static String getGNUplotStr(String path, String xLabel,
			String title, String dataName,int[] yRange, double[] xRange,String ytics, String saveposfix) {
		// TODO Auto-generated method stub
		String str = "set term post eps color solid enh\n"
		+"cd '"+path+"'\n"
		+"set output '"+title+"_"+saveposfix+".eps'\n"
		+"#set grid\n"
		+"set lmargin 10\n"
		+"set key box font \"Times-Roman,15\"\n"
		+"set key left bottom\n"
		+"set format x \"\"  #Ã»ÓÐÖá±ê\n"
		+"set multiplot\n"
		+"set title \""+title+"\" font \"Times-Roman,35\"\n"
		+"set size 1, 0.5\n"
		+"set origin 0, 0.5\n"
		+"set yrange ["+yRange[0]+":"+yRange[1]+"]  \n "
		+"set xrange ["+xRange[0]+":"+xRange[1]+"]  \n"
		+"set ytics ("+ytics+") font \"Times-Roman,20\"\n"
		+"set xtics font \"Times-Roman,20\" \n"
		+"set bmargin 0\n"
		+"set ylabel \"#Features\" font \"Times-Roman,25\"\n"
		+"plot '"+dataName+"' using 1:2 w lp lw 3 lt 7 lc rgb \"red\" t \"SelectAttr\", \\\n"
		   +"'"+dataName+"'  using 1:3 w lp lt 0 lw 5 lc rgb \"blue\" t \"FullAttr\"\n";

		   str+="unset title\n"
			   +"set autoscale y\n"
			   +"set ytics 2\n"
			   +"set bmargin\n"
			   +"set xlabel '"+xLabel+"' font \"Times-Roman,25\" #Ï£À°×ÖÄ¸alpha\n"
			   +"set ylabel \"Accuracy(%)\" font \"Times-Roman,25\" \n"
		+"set format x\n"
		+"set size 1.0, 0.5\n"
		+"set origin 0.0, 0.0\n"
		+"set tmargin 0\n"

		+"plot '"+dataName+"' using 1:4 w lp lw 3 lt 7 lc rgb \"red\" t \"SelectAttr\", \\\n"
		+"'"+dataName+"'  using 1:5 w lp lt 0 lw 5 lc rgb \"blue\" t \"FullAttr\"\n"
		+"unset multiplot\n"
		+"set output\n"
		+"quit\n";

		return str;
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
