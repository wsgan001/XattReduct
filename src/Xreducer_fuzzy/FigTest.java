package Xreducer_fuzzy;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Random;
import java.util.Vector;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;

import Xreducer_core.Utils;

import weka.classifiers.Classifier;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.functions.RBFNetwork;
import weka.classifiers.trees.J48;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Remove;
import weka.filters.unsupervised.attribute.ReplaceMissingValues;

public class FigTest {
	private int percent = 10;
	private File m_datafile = null;
	private String m_dataname = "";
	private Instances m_data = null;
	private ImplicatorTnormStyle m_itStyle = null;
	private SimilarityStyle m_sStyle = null;
	private int m_comRedAlg = 11;
	private int m_comClaAlg = 1;
	private MeasureStyle[] m_mStyles = new MeasureStyle[m_comRedAlg];
	private Classifier[] m_classifiers = new Classifier[m_comClaAlg];
	private double[][] TimeLine = new double[m_comRedAlg][percent];
	private double[][] ACLine = new double[m_comRedAlg][percent];
	
	
	
	public FigTest (String filepath,SimilarityStyle sstyle, ImplicatorTnormStyle itstyle) throws Exception{
		this.m_datafile = new File(filepath);
		this.m_itStyle = itstyle;
		this.m_sStyle = sstyle;
		 
		//this.m_classifiers[0] = new J48();
		//this.m_classifiers[0] = new RBFNetwork();
		this.m_classifiers[0] = new NaiveBayes();
		
		String dataname = new File(filepath).getName();
		int findex = dataname.lastIndexOf(".");
		this.m_dataname = dataname.substring(0, findex);
		
		
		this.m_data = new Instances(new FileReader(this.m_datafile));
		this.m_data.setClassIndex(this.m_data.numAttributes()-1); 
		// Replace missing values   //被均值代替
		ReplaceMissingValues m_ReplaceMissingValues = new ReplaceMissingValues();
		m_ReplaceMissingValues.setInputFormat(m_data);
		m_data = Filter.useFilter(m_data, m_ReplaceMissingValues);
		
		
		
		System.out.println(this.getDataInfor());
		
		int m_numFold = 4;
		for(int i=0;i<percent;++i){
			Instances newdata = this.getNewData(this.m_data,i);
			//tfs plus
			double al=1;
			for(int k=0;k<11;++k){
				this.m_mStyles[k] = new MStyle_FuzzySU(this.m_data, sstyle,itstyle,al);
				al+=0.2;
			}
			
			/*this.m_mStyles[0] = new MStyle_FuzzySU(newdata, this.m_sStyle,this.m_itStyle,1.0);
			this.m_mStyles[1] = new MStyle_FuzzySU(newdata, this.m_sStyle,this.m_itStyle,2.0);
			this.m_mStyles[2] = new MStyle_FuzzySU(newdata, this.m_sStyle,this.m_itStyle,3.0);
			this.m_mStyles[3] = new MStyle_Gamma(newdata, this.m_sStyle,this.m_itStyle);
			this.m_mStyles[4] = new MStyle_Boundary(newdata, this.m_sStyle,this.m_itStyle);
			this.m_mStyles[5] = new MStyle_FuzzyEntropy(newdata, this.m_sStyle,this.m_itStyle);
			this.m_mStyles[6] = new MStyle_ConditionalEntropy(newdata, this.m_sStyle,this.m_itStyle);*/
			
			//System.out.println(newdata.numInstances());
			/*this.m_mStyles[0] = new MStyle_FFCBF(newdata, this.m_sStyle,this.m_itStyle,-1);
			this.m_mStyles[1] = new MStyle_FFCBF(newdata, this.m_sStyle,this.m_itStyle,0);
			this.m_mStyles[2] = new MStyle_Gamma(newdata, this.m_sStyle,this.m_itStyle);
			this.m_mStyles[3] = new MStyle_Boundary(newdata, this.m_sStyle,this.m_itStyle);
			this.m_mStyles[4] = new MStyle_ConditionalEntropy(newdata, this.m_sStyle,this.m_itStyle);*/
			
			for(int j=0;j<this.m_comRedAlg;++j){
				TimeLine[j][i]=this.m_mStyles[j].m_useTime;
				int[] selectatt = this.m_mStyles[j].m_selectAtt;
				ACLine[j][i] = this.myEvolution(this.m_classifiers[0], newdata, selectatt, m_numFold);
			}
			
			System.out.println("Success "+i);
		}
		this.save2file();
		
	}
	
	private void save2file() throws DocumentException {
		// TODO Auto-generated method stub
		String str = "";
		str += this.showTimeLine();
		str += this.showACLine();
		writefile(this.m_dataname,str);
	}

	private String showACLine() {
		// TODO Auto-generated method stub
		String str = "descriptor  ";
		for(int i=0;i<this.m_comRedAlg;++i){
			str += this.m_dataname+"_"+"ac_"+i+"  ";
		}
		//System.out.println(str);
		str += "\n";
		for(int i=0;i<this.percent;++i){
			for(int j=0;j<this.m_comRedAlg;++j){
				str += this.ACLine[j][i]+" ";
			}
			str += "\n";
		}
		str += "\n";
		System.out.println(str);
		return str;
	}

	//写入文件
	public  void writefile(String dataname, String str) throws DocumentException{
		 		   		
		 Document document = new Document();    
      
         // 第五步：关闭文档。    
         document.close();    
         
		   String Ostrtemp = "";
		   String Ostr = "";
		   	File W_filename = new File("C:/Users/Eric/Desktop/2012春/Modification.NO2-tfs/Revision/plus/FigRes/"+dataname+"_"+this.m_sStyle.getInfor()+"_"+this.m_itStyle.getInfor()+".txt");
           // String pdfname = "C:/Users/Eric/Desktop/2011秋冬/Paper.NO3/结果/NewRes/"+dataname+"_"+this.m_sStyle.getInfor()+"_"+this.m_itStyle.getInfor()+".pdf";
	    	RandomAccessFile mm =null;
	    	try {
	    		
	    		// PdfWriter.getInstance(document,new FileOutputStream(pdfname));  
	    		 //document.open();  
	    		mm = new RandomAccessFile(W_filename, "rw");//保存结果
	    		BufferedReader input = new BufferedReader(new FileReader(W_filename));
	    		while((Ostrtemp = input.readLine())!=null){
	    			Ostr =Ostr + Ostrtemp+"\n";
	    		   }
	    		input.close();
	    		str = Ostr+str;
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
	public double myEvolution(Classifier cl,Instances data,int[] seletatts, int fold ) throws Exception{
		int [] reAttr = Utils.seletatt2removeAtt(seletatts);
		Remove m_removeFilter = new Remove();
		m_removeFilter.setAttributeIndicesArray(reAttr);
		m_removeFilter.setInvertSelection(false);
    	m_removeFilter.setInputFormat(data);   
    	Instances newData = Filter.useFilter(data, m_removeFilter);
    	newData.setClassIndex( newData.numAttributes() - 1 ); //重新设置决策属性索引
    	double[] ans = Utils.multicrossValidateModel(cl, newData, 1, fold );
		return ans[0];
	}
	private String showTimeLine() {
		// TODO Auto-generated method stub
		String str = "descriptor  ";
		for(int i=0;i<this.m_comRedAlg;++i){
			str += this.m_dataname+"_"+"time_"+i+"  ";
		}
		//System.out.println(str);
		str += "\n";
		for(int i=0;i<this.percent;++i){
			for(int j=0;j<this.m_comRedAlg;++j){
				str += this.TimeLine[j][i]+" ";
			}
			str += "\n";
		}
		str += "\n";
		System.out.println(str);
		return str;
		
	}

	private Instances getNewData(Instances m_data2, int per) {
		// TODO Auto-generated method stub
		int numFolds = this.percent;
		Instances newdata =  new Instances(m_data2);
		newdata.randomize(new Random(per));
	    if (newdata.classAttribute().isNominal()) {
	    	newdata.stratify(numFolds);
	    }
	    Instances train = newdata.testCV(numFolds, 0);
	    for(int i=1;i<=per;++i){
	    	Instances tmpdata = newdata.testCV(numFolds, i);
	    	for(int j=0;j<tmpdata.numInstances();++j)
	    		train.add(tmpdata.instance(j));
	    }
	    //System.out.println(train.numInstances());
		return train;
	}

	public String getDataInfor(){
		int ins = this.m_data.numInstances();
		int att = this.m_data.numAttributes();
		int att_nominal = 0;
		int att_real = 0;
		int cla = this.m_data.numClasses();
		for(int i=0;i<att-1;++i){
			if(this.m_data.attribute(i).isNominal()){
				att_nominal++;
			}
			else
				att_real++;
		}
		
		String datainf ="数据集名称： "+this.m_dataname+" 样本个数："+ins+" 属性个数："+att+"("+att_nominal+" Nominal/"+att_real+" Real) "+
				" 类别个数:"+cla+"\n\n";
		return datainf;
	}
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		//String path = "C:/Users/Eric/Desktop/2011秋冬/Code/Xreducer/data/Data/acdata/";
		String path = "C:/Users/Eric/Desktop/2011秋冬/Code/Xreducer/data/Data/";
		Vector<String> datas = new Vector<String>();
		//datas.add("cleveland");
		datas.add("waveform");
		//datas.add("ionosphere");
		///datas.add("labor");
		//datas.add("clean1");
		//datas.add("waveform-5000");
		//datas.add("wine");
		ImplicatorTnormStyle itstyle = new ITStyle_Lukasiewicz();
		//SimilarityStyle sstyle = new SStyle_MaxMin();
		SimilarityStyle sstyle = new SStyle_Abs1lambda_VmaxVmin(4);
		for(int i=0;i<datas.size();++i){
			String fn = path+datas.get(i)+".arff";
			FigTest fd = new FigTest(fn,sstyle,itstyle);

		}
	}

}
