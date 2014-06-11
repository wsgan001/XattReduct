package ResultsOperator;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Vector;

import Xreducer_core.Utils;
 

import de.nixosoft.jlr.JLRConverter;
import de.nixosoft.jlr.JLRGenerator;


public class Vector2LaTex {

 

	public Vector<String> rowName = null;
	public Vector<String> columnName = null;
	public Vector<String[]> resultdata = null;
	public int row = 0;
	public int column = 0;
	public String savePath ="";
	public String title="";
	public String Latex ="";
	public String AllLatex ="";
	public String dataname ="";
	
	public Vector2LaTex(String savePath, Vector<String> rowName , Vector<String> columnName, Vector<String[]> resultdata, String title, String dataname){
		this.rowName = rowName;
		this.columnName = columnName;
		this.resultdata = resultdata;
		row = rowName.size();
		column = columnName.size()+2;
		this.savePath = savePath;
		this.title = title;
		this.dataname = dataname;
		this.table2Latex_oneData();
		this.latex2pdf_oneData();
	}
	public Vector2LaTex(String savePath, String alllatex){
		this.savePath=savePath;
		this.Latex=alllatex;
	}
	
	public Vector2LaTex(String savePath, String alllatex,String singlelatex){
		this.savePath=savePath;
		this.Latex=singlelatex;
		this.AllLatex=alllatex;
	}
	public void table2Latex_oneData(){
		
		String latex="\\begin{table*}[htbp]\n\\centering\n" +
		         "\\caption{\\large $"+this.title+"$ }\n";
		if(this.columnName.size()>6){
			latex+="\\resizebox{\\textwidth}{!}{\n"; //分类算法大于6个 调整表格宽度
		}
		latex+="\\begin{tabular}{lcc";
		
		for(int i=0;i<this.column-2;++i){
			latex+="l";
		}
		latex+="}\n\\toprule\n   & $RedNum$ & $RedTime$ & ";
		for(int i=0;i<this.column-3;++i){
			latex+="$"+this.columnName.get(i)+"$ & ";
		}
		latex+="$"+this.columnName.get(this.column-3)+"$ \\\\\n\\midrule\n";
		
		for(int i=0;i<this.row;++i){
		    latex+="$"+this.rowName.get(i)+"$ & ";
			for(int j=0;j<this.column-1;++j){
				latex+="$"+this.resultdata.get(i)[j]+"$ & ";
			}
			latex+="$"+this.resultdata.get(i)[this.column-1]+"$ \\\\\n";
		}

		
		latex+="\\midrule\n\\bottomrule\n\\end{tabular}\n";		
		if(this.columnName.size()>6){
			latex+="}\n"; //分类算法大于6个 调整表格宽度
		}
		latex+="\\end{table*}\n\n";
		this.Latex = latex;
	
	}
	
	public void latex2pdf_oneData(){
		 File Directory = new File(System.getProperty("user.dir").replace("\\", "/")+"/latexTemplate");
	        File template = new File(Directory.getAbsolutePath() + File.separator+ "oneDataSet.tex");	        
	        File texfile = new File(Directory.getAbsolutePath() + File.separator + this.dataname +".tex");
	        
	        try {
	
	            HashMap<String, String> data = new HashMap<String, String>();
	            data.put("oneDataSet", this.Latex);
	            JLRConverter converter = new JLRConverter("::", ":::");
	            if (!converter.parse(template, texfile, data)) {
	                System.out.println(converter.getErrorMessage());
	            }
	            JLRGenerator pdfGen = new JLRGenerator();
	            pdfGen.deleteTempTexFile(true);
	            if (!pdfGen.generate(texfile, Directory, Directory)) { 
	                System.out.println(pdfGen.getErrorMessage());
	            }
	            copyFile(Directory+File.separator+this.dataname+".pdf",this.savePath+this.dataname+".pdf");
	            new File(Directory+File.separator+this.dataname+".pdf").delete();
	            new File(Directory+File.separator+this.dataname+".spl").delete(); 
	
	        } catch (IOException ex) {
	            System.err.println(ex.getMessage());
	        }
	}
	public void latex2pdf_allindepData(){
		String filename1 = "AllResultsIndep-"+Utils.getCurrentData();
		String filename2 = "AllResultsAll-"+Utils.getCurrentData();
		File Directory = new File(System.getProperty("user.dir").replace("\\", "/")+"/latexTemplate");
        File template1 = new File(Directory.getAbsolutePath() + File.separator+ "indepDataSet.tex");	        
        File texfile1 = new File(Directory.getAbsolutePath() + File.separator + filename1 +".tex");
        File template2 = new File(Directory.getAbsolutePath() + File.separator+ "allDataSet.tex");	        
        File texfile2 = new File(Directory.getAbsolutePath() + File.separator + filename2 +".tex");
        
        try {

            HashMap<String, String> data1 = new HashMap<String, String>();
            data1.put("indepDataSet", this.Latex);
            HashMap<String, String> data2 = new HashMap<String, String>();
            data2.put("allDataSet", this.AllLatex);
            JLRConverter converter = new JLRConverter("::", ":::");
           if (!converter.parse(template1, texfile1, data1)) {
                System.out.println(converter.getErrorMessage());
            }
            JLRGenerator pdfGen1 = new JLRGenerator();
            pdfGen1.deleteTempTexFile(true);
            if (!pdfGen1.generate(texfile1, Directory, Directory)) { 
                System.out.println(pdfGen1.getErrorMessage());
            }
            copyFile(Directory+File.separator+filename1+".pdf",this.savePath+filename1+".pdf");
            new File(Directory+File.separator+filename1+".pdf").delete();
            new File(Directory+File.separator+filename1+".spl").delete(); 
            
            if (!converter.parse(template2, texfile2, data2)) {
                System.out.println(converter.getErrorMessage());
            }
            JLRGenerator pdfGen2 = new JLRGenerator();
            pdfGen2.deleteTempTexFile(true);
            if (!pdfGen2.generate(texfile2, Directory, Directory)) { 
                System.out.println(pdfGen2.getErrorMessage());
            }
            copyFile(Directory+File.separator+filename2+".pdf",this.savePath+filename2+".pdf");
            new File(Directory+File.separator+filename2+".pdf").delete();
            new File(Directory+File.separator+filename2+".spl").delete();

        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }
}
	public void latex2pdf_allData(){
			String filename1 = "AllResultsIndep-"+Utils.getCurrentData();

			File Directory = new File(System.getProperty("user.dir").replace("\\", "/")+"/latexTemplate");
	        File template1 = new File(Directory.getAbsolutePath() + File.separator+ "indepDataSet.tex");	        
	        File texfile1 = new File(Directory.getAbsolutePath() + File.separator + filename1 +".tex");

	        
	        try {
	
	            HashMap<String, String> data = new HashMap<String, String>();
	            data.put("indepDataSet", this.Latex);
	            JLRConverter converter = new JLRConverter("::", ":::");
	           if (!converter.parse(template1, texfile1, data)) {
	                System.out.println(converter.getErrorMessage());
	            }
	            JLRGenerator pdfGen = new JLRGenerator();
	            pdfGen.deleteTempTexFile(true);
	            if (!pdfGen.generate(texfile1, Directory, Directory)) { 
	                System.out.println(pdfGen.getErrorMessage());
	            }
	            copyFile(Directory+File.separator+filename1+".pdf",this.savePath+filename1+".pdf");
	            new File(Directory+File.separator+filename1+".pdf").delete();
	            new File(Directory+File.separator+filename1+".spl").delete(); 
	
	        } catch (IOException ex) {
	            System.err.println(ex.getMessage());
	        }
	}
	public void copyFile(String oldPath, String newPath) { 
	       try { 
	           int bytesum = 0; 
	           int byteread = 0; 
	           File oldfile = new File(oldPath); 
	           if (oldfile.exists()) { //文件存在时 
	               InputStream inStream = new FileInputStream(oldPath); //读入原文件 
	               FileOutputStream fs = new FileOutputStream(newPath); 
	               byte[] buffer = new byte[100000]; 
	               while ( (byteread = inStream.read(buffer)) != -1) { 
	                   bytesum += byteread; //字节数 文件大小 
	                   //System.out.println(bytesum); 
	                   fs.write(buffer, 0, byteread); 
	               } 
	               inStream.close(); 
	           } 
	       } 
	       catch (Exception e) { 
	           System.out.println("复制单个文件操作出错"); 
	           e.printStackTrace(); }
	
	       } 
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String p = "C:/Users/Eric/Desktop/2012春/java-report/JLR/";
		String[] r={"xx","bb"};
		String[] c={"xxx","bbx","xxaaa"};
		String t="fefefef";
		Vector<String[]> d =new Vector<String[]>();
		String[] a1 = {"1","2","3"};
		String[] a2 = {"1a","2a","3a"};
		d.add(a1);
		d.add(a2);
		String n = "afeefe";
		//showTable tt = new showTable(p,r,c,d,t,n);
		 System.out.println(Utils.getCurrentData());
	}

}
