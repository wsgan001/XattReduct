package myUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;

public class xFile {
	//写入文件
	public static void writeTXTfile(String path, String fname, String str) throws DocumentException{
		 		   		
		 Document document = new Document();    
      
         // 第五步：关闭文档。    
         document.close();    
         
		   String Ostrtemp = "";
		   
		   
			File W_filename = new File(path+fname+".txt");
	    	RandomAccessFile mm =null;
	    	try {
	    		
	    		// PdfWriter.getInstance(document,new FileOutputStream(pdfname));  
	    		 //document.open();  
	    		mm = new RandomAccessFile(W_filename, "rw");//保存结果
	    		BufferedReader input = new BufferedReader(new FileReader(W_filename));
	    		 
	    		input.close();
	    		 
	    		str = str + "\n";

	        	mm = new RandomAccessFile(W_filename, "rw");//保存结果
	        	
	        	mm.write(str.getBytes());
	        	  //document.add(new Paragraph(str));  
	        }
	        catch (IOException e1) {
	            // TODO 自动生成 catch 块
	            e1.printStackTrace();
	        } finally {
	            if (mm != null) {
	                try {
	                    mm.close();
	                } catch (IOException e2) {
	                    // TODO 自动生成 catch 块
	                    e2.printStackTrace();
	                }
	            }
	        }
	}
	//写入文件
	public static void writefile(String path, String fname, String str) throws DocumentException{
		 		   		
		 Document document = new Document();    
      
         // 第五步：关闭文档。    
         document.close();    
         
		   String Ostrtemp = "";
		   
		   
			File W_filename = new File(path+fname);
	    	RandomAccessFile mm =null;
	    	try {
	    		
	    		// PdfWriter.getInstance(document,new FileOutputStream(pdfname));  
	    		 //document.open();  
	    		mm = new RandomAccessFile(W_filename, "rw");//保存结果
	    		BufferedReader input = new BufferedReader(new FileReader(W_filename));
	    		 
	    		input.close();
	    		 
	    		str = str + "\n";

	        	mm = new RandomAccessFile(W_filename, "rw");//保存结果
	        	
	        	mm.write(str.getBytes());
	        	  //document.add(new Paragraph(str));  
	        }
	        catch (IOException e1) {
	            // TODO 自动生成 catch 块
	            e1.printStackTrace();
	        } finally {
	            if (mm != null) {
	                try {
	                    mm.close();
	                } catch (IOException e2) {
	                    // TODO 自动生成 catch 块
	                    e2.printStackTrace();
	                }
	            }
	        }
	}
	public static void writeNewFile(String path, String fname, String str){
	try {
        //打开一个写文件器，构造函数的第二个参数true表示以追加的形式写文件 false 重新写
        FileWriter writer = new FileWriter(path+fname, false);
        writer.write(str);
        writer.close();
    } catch (IOException e) {
        e.printStackTrace();
    }
	}
}
