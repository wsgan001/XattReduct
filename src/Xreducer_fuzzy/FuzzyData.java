package Xreducer_fuzzy;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import myUtils.xFigure;
import myUtils.xMath;

import org.apache.commons.math.stat.descriptive.moment.Mean;
import org.apache.commons.math.stat.descriptive.moment.StandardDeviation;
import org.apache.commons.math.stat.inference.TestUtils;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;

import Xreducer_core.Utils;
 


import weka.classifiers.Classifier;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.functions.LibSVM;
import weka.classifiers.functions.RBFNetwork;
import weka.classifiers.lazy.IBk;
import weka.classifiers.rules.JRip;
import weka.classifiers.rules.PART;
import weka.classifiers.trees.J48;
import weka.classifiers.trees.SimpleCart;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Remove;
import weka.filters.unsupervised.attribute.RemoveUseless;
import weka.filters.unsupervised.attribute.ReplaceMissingValues;

public class FuzzyData {

	
	
	private int m_runTime = 10;
	private int m_numFold = 10;
	private double m_singificantlevel = 0.05;
	
	private File m_datafile = null;
	private String m_dataname = "";
	private Instances m_data = null;
	private ImplicatorTnormStyle m_itStyle = null;
	private SimilarityStyle m_sStyle = null;
	private int m_comRedAlg = 11;
	private int m_comClaAlg = 2;

	private Vector<int[] > m_seletAtts = new Vector<int[] >();
	private int[] m_numRedAtts = new int[m_comRedAlg+1];
	private double[] m_timeRed = new double[m_comRedAlg+1];
	private Vector<double[][] > m_ACs = new Vector<double[][] > ();
	private Vector<double[][] > m_TestRes = new Vector<double[][] > ();
	private MeasureStyle[] m_mStyles = new MeasureStyle[m_comRedAlg];
	private Classifier[] m_classifiers = new Classifier[m_comClaAlg];
	private String m_processAll = "";
	
	public FuzzyData(String filepath,SimilarityStyle sstyle, ImplicatorTnormStyle itstyle) throws Exception{
		this.m_datafile = new File(filepath);
		this.m_itStyle = itstyle;
		this.m_sStyle = sstyle;
		 

		this.m_classifiers[0] = new NaiveBayes();		
		//this.m_classifiers[1] = new LibSVM(); //RBF-SVM
		//this.m_classifiers[2] = new J48();
		//this.m_classifiers[3] = new JRip();
		//this.m_classifiers[4] = new PART();
		//this.m_classifiers[5] = new IBk(1);
		this.m_classifiers[1] = new RBFNetwork();
		//this.m_classifiers[7] = new SimpleCart();
		
	
		String dataname = new File(filepath).getName();
		int findex = dataname.lastIndexOf(".");
		this.m_dataname = dataname.substring(0, findex);
		
		
		this.m_data = new Instances(new FileReader(this.m_datafile));
		this.m_data.setClassIndex(this.m_data.numAttributes()-1); 
		
		
		// Replace missing values   //被均值代替
		 ReplaceMissingValues m_ReplaceMissingValues = new ReplaceMissingValues();
		m_ReplaceMissingValues.setInputFormat(m_data);
		m_data = Filter.useFilter(m_data, m_ReplaceMissingValues); 

		 

		// Remove useless attributes
		//RemoveUseless m_AttFilter = new RemoveUseless();
		//m_AttFilter.setInputFormat(m_data);
		//m_data = Filter.useFilter(m_data, m_AttFilter);

		System.out.println(this.getDataInfor());
		
		//plus-tfs
		double al=1;
		for(int i=0;i<11;++i){
			this.m_mStyles[i] = new MStyle_FuzzySU(this.m_data, sstyle,itstyle,al);
			al+=0.2;
		}
		

		
		//this.m_mStyles[0] = new MStyle_FuzzySU(this.m_data, this.m_sStyle,this.m_itStyle,1.0);
		//this.m_mStyles[0] = new MStyle_FuzzySU(this.m_data, this.m_sStyle,this.m_itStyle,1.0);
		
	
//		this.m_mStyles[0] = new MStyle_FFCBF(this.m_data, this.m_sStyle,this.m_itStyle,-1);
//		this.m_mStyles[1] = new MStyle_FFCBF(this.m_data, this.m_sStyle,this.m_itStyle,0);
//		this.m_mStyles[2] = new MStyle_FCBF(this.m_data, true, -1, -1);
//		this.m_mStyles[3] = new MStyle_FCBF(this.m_data, true, -1, 0);
//		this.m_mStyles[4] = new MStyle_FCBF(this.m_data, false, 10, -1);
//		this.m_mStyles[5] = new MStyle_FCBF(this.m_data, false, 10, 0);
 		//this.m_mStyles[6] = new MStyle_Gamma(this.m_data, this.m_sStyle,this.m_itStyle);
//		this.m_mStyles[7] = new MStyle_Boundary(this.m_data, this.m_sStyle,this.m_itStyle);
		//this.m_mStyles[5] = new MStyle_FuzzyEntropy(this.m_data, this.m_sStyle,this.m_itStyle);
		//this.m_mStyles[8] = new MStyle_ConditionalEntropy(this.m_data, this.m_sStyle,this.m_itStyle);
		//约简属性集---带决策属性;约简个数--不带决策属性;
		for(int i=0;i<this.m_comRedAlg;++i){
			this.m_seletAtts.add(this.m_mStyles[i].m_selectAtt);
			this.m_numRedAtts[i] = this.m_mStyles[i].m_numRed;
			this.m_timeRed[i] = this.m_mStyles[i].m_useTime;
			m_processAll += this.m_mStyles[i].m_process;
		}
		this.m_seletAtts.add(Utils.getFullAtts_withDecAtt(this.m_data));
		this.m_numRedAtts[this.m_comRedAlg] = this.m_data.numAttributes()-1;
		this.m_timeRed[this.m_comRedAlg] = 0; //全集 约简时间为0
		//训练 得出准确率
		for(int i=0;i<this.m_comClaAlg;++i){//一个分类算法 对应m不同的约简算法 结果ans[m][runtime] 包括全集
			double[][] ans = new double[this.m_comRedAlg+1][this.m_runTime];
			for(int j=0;j<this.m_comRedAlg+1;++j){
				double[] tmp = this.myEvolution(this.m_classifiers[i], this.m_data, this.m_seletAtts.get(j), this.m_numFold, this.m_runTime);
				for(int k =0;k<this.m_runTime;++k)
					ans[j][k] = tmp[k];
			}
			this.m_ACs.add(ans);
		}
		//统计结果
		int baseindex = 0;
		for(int i=0;i<this.m_comClaAlg;++i){//一个分类算法 统计一个结果 mean std pvalue wlt
			this.m_TestRes.add(MypairedTtest(this.m_ACs.get(i),baseindex, this.m_singificantlevel));
		}
		//保存结果
		saveRes2file();
		double[][] ans1 = new double[2][this.m_comRedAlg];
		double[][] ans2 = new double[2][this.m_comRedAlg];
		double[][] ans3 = new double[2][this.m_comRedAlg];
		
		double[][] ans4 = new double[2][this.m_comRedAlg];
		al = 1;
		double[][] testres0 = this.m_TestRes.get(0);
		double[][] testres1 = this.m_TestRes.get(1);
		for(int i=0;i<this.m_comRedAlg;++i){
			ans1[0][i] = xMath.getDoubleRound(2, al);
			ans2[0][i] = xMath.getDoubleRound(2, al);
			ans3[0][i] = xMath.getDoubleRound(2, al);
			ans4[0][i] = xMath.getDoubleRound(2, al);
			ans1[1][i] =  this.m_mStyles[i].m_numRed;
			ans2[1][i] =  this.m_mStyles[i].m_useTime;
			ans3[1][i] =  testres0[i][0];
			ans4[1][i] =  testres1[i][0];
			 al += 0.2;
		}
		Vector<Map<String,double[][]>> v = new Vector<Map<String,double[][]>>();
		Map<String,double[][]> rednum = new HashMap<String,double[][]>();
		rednum.put("RedNum", ans1);
		Map<String,double[][]> time = new HashMap<String,double[][]>();
		time.put("time", ans2);
		Map<String,double[][]> ac = new HashMap<String,double[][]>();
		ac.put("NBC", ans3);
		ac.put("RBFnetwork", ans4);
		v.add(rednum);
		v.add(time);
		v.add(ac);
		String newfilename = "C:/Users/Eric/Desktop/2012春/Modification.NO2-tfs/newResult/";
		String[] titles = new String[4];
		titles[0] = this.m_dataname;
		titles[1] = "RedNum";
		titles[2] = "Time";
		titles[3] = "AC";
		xFigure.showFigure(titles, v);
		xFigure.saveFigure(titles, newfilename, v);
		
	}
	
	public static double[][] MypairedTtest(double[][] X, int baseindex , double singificantlevel) throws Exception{
		int N = X.length;
		//int M = X[0].length;
		double[][] res = new double[N][4];
		Mean mean = new Mean(); // 算术平均值
		//Variance variance = new Variance(); // 方差
		StandardDeviation sd = new StandardDeviation();// 标准方差
		//计算均值方差写入res[0] res[1]中
		for(int i=0;i<N;++i){
			res[i][0] = mean.evaluate(X[i]);
			res[i][1] = sd.evaluate(X[i]);
		}
		//DecimalFormat df = new DecimalFormat("0.000000");

		//进行paried t-test
		for(int i=0;i<N;++i){
			double pval;
			boolean H;
			if(i!=baseindex){
					pval = TestUtils.pairedTTest(X[baseindex], X[i]);
					H = TestUtils.pairedTTest(X[baseindex], X[i], singificantlevel);
					if(!H){//没有显著差异，平局 3.tie
						res[i][2]=3;
						res[i][3]=pval;
					}
					else{//有显著差异, 均值大的获胜 1.win 2.lose
						res[i][2]=res[baseindex][0]>=res[i][0]?1:2;
						res[i][3]=pval;
					}
					//String num = df.format(pval);
					//System.out.println(num);
			}
		}
		res[baseindex][2]=0;
		res[baseindex][3]=-1;
		return res;
	}
	public void saveRes2file() throws DocumentException{
		String str = "";
		String str2 = "";
		for(int i=0;i<this.m_comRedAlg;++i){
			str2 += this.m_mStyles[i].algname+":" +Arrays.toString(this.m_seletAtts.get(i))+"\n";
		}
		str += this.m_processAll+"\n\n\n"+str2+"\n\n\n"+getRes()+"\n\n\n"+getTTestRes();
		writefile(this.m_dataname,str);
	}
	public double[] myEvolution(Classifier cl,Instances data,int[] seletatts, int fold , int runtime) throws Exception{
		int [] reAttr = Utils.seletatt2removeAtt(seletatts);
		Remove m_removeFilter = new Remove();
		m_removeFilter.setAttributeIndicesArray(reAttr);
		m_removeFilter.setInvertSelection(false);
    	m_removeFilter.setInputFormat(data);   
    	Instances newData = Filter.useFilter(data, m_removeFilter);
    	newData.setClassIndex( newData.numAttributes() - 1 ); //重新设置决策属性索引
    	double[] ans = Utils.multicrossValidateModel(cl, newData, runtime, fold );
		return ans;
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
	public String getTTestRes(){
		String str = "\n";
		for(int i=0;i<this.m_ACs.size();++i){
			double[][] tmp = this.m_ACs.get(i).clone();
			for(int p = 0;p<tmp.length;++p){
				for(int q =0;q<tmp[0].length;++q)
					str += Double.toString(tmp[p][q])+" ";
				str+="\n";
			}
			str+="\n\n";	
		}
		String latexstr = "";
		for(int i=0;i<this.m_TestRes.size();++i){
			double[][] tmp = this.m_TestRes.get(i).clone();
			for(int p = 0;p<tmp.length;++p){
				str += tmp[p][0]+" ";
				str += tmp[p][1]+" ";			
				String t = "";
				String l = "";
				int intt = (int)tmp[p][2];
				switch(intt){
				case 0: {t = "B";break;}
				case 1: {t = "L";l="-";break;}
				case 2: {t = "W";l="+";break;}
				case 3: {t = "T";l="#";break;}
				default:break;
				}
				str += t+" "+tmp[p][3]+"\n";
				if(p!=0){
					String wlt = l=="#"?"":("$^"+l+"$");
					String pval = Double.isNaN(tmp[p][3])?"1.00":Utils.doubleFormat("0.00", tmp[p][3])+wlt;
					latexstr += "&"+Utils.doubleFormat("0.00", tmp[p][0]*100)+"$\\pm$"+Utils.doubleFormat("0.00", tmp[p][1]*100)+"&"+pval+" ";
				}
					
				else
					latexstr += "&"+Utils.doubleFormat("0.00", tmp[p][0]*100)+"$\\pm$"+Utils.doubleFormat("0.00", tmp[p][1]*100)+" ";
			}
			str += "\n\n";
			latexstr +=  "\\\\\n";
		}
		return str+latexstr;
		
	}
	public String getRes(){
		String str = "";
		
		String datainf = this.getDataInfor();
		String title = "\t\t 约简个数 \t 约简时间 \t NaiveBayes算法 \t RBFSVM算法 \t C4.5算法 \t " +
				"JRip算法 \t PART算法 \t 1NN算法 \t RBFnewwork算法 \t CART算法\n";
		/*String title = "\t\t 约简个数 \t 约简时间 \t NaiveBayes算法 \t RBFNetwork算法 \n";*/
		str = datainf+title;
		String[] redAlgnames = new String[m_comRedAlg+1];
		for(int i=0;i<this.m_comRedAlg;++i){
			redAlgnames[i] = this.m_mStyles[i].algname;
		}
		redAlgnames[this.m_comRedAlg] = "FullSet全集";
		for(int i=0;i<this.m_comRedAlg+1;++i){
			String tmp1 = "";
			tmp1 += redAlgnames[i]+"\t "+this.m_numRedAtts[i]+" \t\t "+Utils.doubleFormat("0.00", this.m_timeRed[i])+"s  ";
			String tmp2 = "";
			for(int j=0;j<this.m_comClaAlg;++j){
				double[][] testresj = this.m_TestRes.get(j);
				String t = "";
				int intt = (int)testresj[i][2];
				switch(intt){
				case 0: {t = "B";break;}
				case 1: {t = "L";break;}
				case 2: {t = "W";break;}
				case 3: {t = "T";break;}
				default:break;
				}
				tmp2+="\t "+Utils.doubleFormat("0.00", testresj[i][0]*100)+"% "+t;
			}
			str += tmp1 + tmp2 + "\n";
		}
		return str;
		
	}
	
	//写入文件
	public  void writefile(String dataname, String str) throws DocumentException{
		 		   		
		 Document document = new Document();    
      
         // 第五步：关闭文档。    
         document.close();    
         
		   String Ostrtemp = "";
		   String Ostr = "";
		   
			//File W_filename = new File("C:/Users/Eric/Desktop/2012春/Paper.NO4/结果/NewRes/"+dataname+".txt");
		   File W_filename = new File("C:/Users/Eric/Desktop/2012春/Modification.NO2-tfs/newResult/"+dataname+".txt");
		   	//File W_filename = new File("C:/Users/Eric/Desktop/2011秋冬/Paper.NO4/结果/NewRes/"+dataname+".txt");
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
	/**1
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		//SimilarityStyle sstyle = new SStyle_MaxMin();
		
		//SimilarityStyle sstyle = new SStyle_Abs1lambda_VmaxVmin(4);
		
		//ImplicatorTnormStyle itstyle = new ITStyle_KleeneDienes();
		//String path = "C:/Users/Eric/Desktop/2011秋冬/Code/Xreducer/data/Data/acdata/";
		//String path = "C:/Users/Eric/Desktop/2011秋冬/Code/Xreducer/data/Data/";
		//Vector<String> datas = new Vector<String>();
		//datas.add("fuzzy-ex");
		/*datas.add("glass");
		datas.add("cleveland");
		datas.add("credit");
		datas.add("dermatology");
		datas.add("diabetes");
		datas.add("ecoli");
		datas.add("heart");
		
		datas.add("clean1");
		datas.add("colic");
		datas.add("flag");
		datas.add("labor");
		datas.add("hepatitis");

		
		datas.add("ionosphere");
		datas.add("wdbc");
		datas.add("wpbc_33");
		
		
		datas.add("CNS");
		datas.add("Leukemia");
		datas.add("Colon_Cancer");
		datas.add("web");
		datas.add("Reuters_test");
		datas.add("arrhythmia");
		datas.add("DLBCLTumor");
		datas.add("ORL_test");
		datas.add("lung-Michigan");
		datas.add("MLL_train");
		
		
		datas.add("cleveland");
		datas.add("colic");
		datas.add("credit");
		datas.add("dermatology");
		datas.add("diabetes");
		datas.add("flag");
		datas.add("heart");
		datas.add("ionosphere");
		datas.add("hepatitis");
		datas.add("CNS");
		datas.add("Colon_Cancer"); */
		Vector<String> datas = new Vector<String>();
		ImplicatorTnormStyle itstyle = new ITStyle_Lukasiewicz();
		/*SimilarityStyle sstyle = new SStyle_MaxMin(); */
	 
		SimilarityStyle sstyle = new SStyle_Abs1lambda_VmaxVmin(4);
		String path = "C:/Users/Eric/Desktop/2011秋冬/Code/Xreducer/data/Data/";
		//String path = "C:/Users/Eric/Desktop/2011秋冬/Code/Xreducer/data/Data/testdata/";
		/*datas.add("colic");
		datas.add("Colon-Cancer"); 
		datas.add("dermatology");
		datas.add("heart");
		datas.add("hepatitis");
		datas.add("ionosphere");
		datas.add("clean1");*/
		//datas.add("wine");
		//datas.add("waveform-5000");
	    /*datas.add("cleveland");
		datas.add("clean1");
		datas.add("colic");
		datas.add("credit");
		datas.add("dermatology");
		datas.add("diabetes");
		datas.add("flag");
		datas.add("heart");
		datas.add("ionosphere");
		datas.add("hepatitis");
		datas.add("waveform-5000");
		datas.add("labor");
		datas.add("olitos");
		datas.add("sonar");
		datas.add("wine");
		datas.add("wdbc");
		datas.add("wpbc_33");*/
		
		  datas.add("CNS");
		//datas.add("Colon_Cancer");  
		 datas.add("reduced-2classes");
		 datas.add("tumors-C"); 
		datas.add("waveform");
		//datas.add("vote"); 


		 
		
		

		
		for(int i=0;i<datas.size();++i){
			String fn = path+datas.get(i)+".arff";
			FuzzyData fd = new FuzzyData(fn,sstyle,itstyle);
			System.out.println("\n\n"+fd.getDataInfor()+fd.getRes());
			System.out.println("Success!+At "+Utils.getCurrentDatatime()+"\n");
		}
		
		//SimilarityStyle sstyle = new SStyle_MaxMin();
		//datas = new Vector<String>();
		/*datas.add("Musk2");
		datas.add("sonar");
		datas.add("waveform-5000"); 
		 
		for(int i=0;i<datas.size();++i){
			String fn = path+datas.get(i)+".arff";
			FuzzyData fd = new FuzzyData(fn,sstyle,itstyle);
			System.out.println("\n\n"+fd.getDataInfor()+fd.getRes());
			System.out.println("Success!+At "+Utils.getCurrentDatatime()+"\n");
		} */
		
	}


}
