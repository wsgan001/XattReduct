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
	//д���ļ�
	public static void writeTXTfile(String path, String fname, String str) throws DocumentException{
		 		   		
		 Document document = new Document();    
      
         // ���岽���ر��ĵ���    
         document.close();    
         
		   String Ostrtemp = "";
		   
		   
			File W_filename = new File(path+fname+".txt");
	    	RandomAccessFile mm =null;
	    	try {
	    		
	    		// PdfWriter.getInstance(document,new FileOutputStream(pdfname));  
	    		 //document.open();  
	    		mm = new RandomAccessFile(W_filename, "rw");//������
	    		BufferedReader input = new BufferedReader(new FileReader(W_filename));
	    		 
	    		input.close();
	    		 
	    		str = str + "\n";

	        	mm = new RandomAccessFile(W_filename, "rw");//������
	        	
	        	mm.write(str.getBytes());
	        	  //document.add(new Paragraph(str));  
	        }
	        catch (IOException e1) {
	            // TODO �Զ����� catch ��
	            e1.printStackTrace();
	        } finally {
	            if (mm != null) {
	                try {
	                    mm.close();
	                } catch (IOException e2) {
	                    // TODO �Զ����� catch ��
	                    e2.printStackTrace();
	                }
	            }
	        }
	}
	//д���ļ�
	public static void writefile(String path, String fname, String str) throws DocumentException{
		 		   		
		 Document document = new Document();    
      
         // ���岽���ر��ĵ���    
         document.close();    
         
		   String Ostrtemp = "";
		   
		   
			File W_filename = new File(path+fname);
	    	RandomAccessFile mm =null;
	    	try {
	    		
	    		// PdfWriter.getInstance(document,new FileOutputStream(pdfname));  
	    		 //document.open();  
	    		mm = new RandomAccessFile(W_filename, "rw");//������
	    		BufferedReader input = new BufferedReader(new FileReader(W_filename));
	    		 
	    		input.close();
	    		 
	    		str = str + "\n";

	        	mm = new RandomAccessFile(W_filename, "rw");//������
	        	
	        	mm.write(str.getBytes());
	        	  //document.add(new Paragraph(str));  
	        }
	        catch (IOException e1) {
	            // TODO �Զ����� catch ��
	            e1.printStackTrace();
	        } finally {
	            if (mm != null) {
	                try {
	                    mm.close();
	                } catch (IOException e2) {
	                    // TODO �Զ����� catch ��
	                    e2.printStackTrace();
	                }
	            }
	        }
	}
	public static void writeNewFile(String path, String fname, String str){
	try {
        //��һ��д�ļ��������캯���ĵڶ�������true��ʾ��׷�ӵ���ʽд�ļ� false ����д
        FileWriter writer = new FileWriter(path+fname, false);
        writer.write(str);
        writer.close();
    } catch (IOException e) {
        e.printStackTrace();
    }
	}
}
