package FCBFandRS;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;
import java.util.Random;
import java.util.Vector;

import myUtils.xMath;

import org.apache.commons.math.stat.descriptive.moment.Mean;
import org.apache.commons.math.stat.descriptive.moment.StandardDeviation;
import org.apache.commons.math.stat.inference.TestUtils;

import weka.attributeSelection.CfsSubsetEval;
import weka.attributeSelection.ConsistencySubsetEval;
import weka.attributeSelection.GreedyStepwise;
import weka.attributeSelection.Ranker;
import weka.attributeSelection.ReliefFAttributeEval;
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
import weka.core.SelectedTag;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Remove;
import weka.filters.unsupervised.attribute.ReplaceMissingValues;
import CheckMonotony.InconsistentCheck;
import CheckMonotony.Entropy.newEntropy;
import Modification.RSxmethod;
import Plus.ABB;
import Plus.Focus;
import Plus.RoughGASearch;
import Plus.xRandomSearch;
import ResultsOperator.Vector2LaTex;
import Xreducer_core.Utils;
import Xreducer_fuzzy.ITStyle_Lukasiewicz;
import Xreducer_fuzzy.ImplicatorTnormStyle;
import Xreducer_fuzzy.MStyle_FuzzySU;
import Xreducer_fuzzy.SStyle_MaxMin;
import Xreducer_fuzzy.SimilarityStyle;
import Xreducer_struct.ResData;
import Xreducer_struct.ResNode;
import Xreducer_struct.oneAlgorithm.xStyle;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;

public class ComRedAlg {

	private int m_runTime = 10;
	private int m_numFold = 10;
	private double m_singificantlevel = 0.05;
	
	private File m_datafile = null;
	private String m_dataname = "";
	private Instances m_data = null;
	
	private Vector<Classifier> m_classifiers = new Vector<Classifier>();
	private Vector<FSmethod> m_reduceAlg = new Vector<FSmethod>();
	private Vector<int[]> m_seletAtts = new Vector<int[]>();
	private Vector<Integer> m_numRedAtts = new Vector<Integer>();
	private Vector<Double> m_timeRed = new Vector<Double>();
	private Vector<double[][] > m_ACs = new Vector<double[][] > ();
	private Vector<double[][] > m_TestRes = new Vector<double[][] > ();
	private String m_processAll = "";
	public Vector<String> claAlgnames = new Vector<String>();
	public Vector<String> redAlgnames = new Vector<String>();
	public String latexString = "";
	private String savepath ="";
	public String resString ="";
	private ResData allresData = new ResData();
	
	public ComRedAlg(String filepath,String savepath,int bin) throws IOException, Exception{
		this.m_datafile = new File(filepath);
		this.savepath = savepath;
		
		//全部输入原始数据
		this.m_data = new Instances(new FileReader(this.m_datafile));
		this.m_data.setClassIndex(this.m_data.numAttributes()-1); 
		double originalrate = InconsistentCheck.getInconsistentRate(InconsistentCheck.Data2Matrix(m_data));
 		double inrate = 0.05;
 		if(originalrate==0.0)
 			this.m_data = InconsistentCheck.getInconsistentData(this.m_data,inrate,new Random(1));
		
		// Replace missing values   //被均值代替
		/*ReplaceMissingValues m_ReplaceMissingValues = new ReplaceMissingValues();
		m_ReplaceMissingValues.setInputFormat(m_data);
		m_data = Filter.useFilter(m_data, m_ReplaceMissingValues);*/
		
		String dataname = new File(filepath).getName();
		int findex = dataname.lastIndexOf(".");
		this.m_dataname = dataname.substring(0, findex);
		//显示数据集信息
		System.out.println(this.getDataInfor());
		
		//添加分类算法
		this.claAlgnames.add("NBC");
		this.m_classifiers.add(new NaiveBayes());
		//this.claAlgnames.add("RBF-SVM");
		//this.m_classifiers.add(new LibSVM());
		//this.claAlgnames.add("LineSVM");
		//LibSVM svm = new LibSVM();
		//svm.setKernelType(new SelectedTag( LibSVM.KERNELTYPE_LINEAR, LibSVM.TAGS_KERNELTYPE));
		//this.m_classifiers.add(svm);
		//this.claAlgnames.add("C4.5");
		//this.m_classifiers.add(new J48());
		// this.claAlgnames.add("JRip");
		 //this.m_classifiers.add(new JRip());
		 //this.claAlgnames.add("PART");
		//this.m_classifiers.add(new PART());
		 //int knn = this.m_data.numClasses();
		 //this.claAlgnames.add(knn+"NN");
		 //this.m_classifiers.add(new IBk(knn));
		 //this.claAlgnames.add("1NN");
		 //this.m_classifiers.add(new IBk(1));
		 //this.claAlgnames.add("RBFNetwork");
		 //this.m_classifiers.add(new RBFNetwork());
		// this.claAlgnames.add("CART");	
		 //this.m_classifiers.add(new SimpleCart());	
		/*for(int i=0;i<this.claAlgnames.size();++i){
			this.m_classifiers.add(Classifier.forName(this.claAlgnames.get(i),null));
		}*/
		
		
		
		//添加约简算法
		//this.redAlgnames.add("mRMR(0010.5)");
		//this.m_reduceAlg.add(new mRMRmethod(m_data,bin ,xStyle.ClassDependet,0,0,1,0.2));		
		//this.redAlgnames.add("mRMR(1100)");
		//this.m_reduceAlg.add(new mRMRmethod(m_data,bin ,xStyle.ClassDependet,1,1,0,0));
		//this.redAlgnames.add("mRMR(0011)");
		//this.m_reduceAlg.add(new mRMRmethod(m_data,-1 ,xStyle.conditionentropy,0,0,1,1));

		//this.redAlgnames.add("mRMR(0010)");
		//this.m_reduceAlg.add(new mRMRmethod(m_data,-1 ,xStyle.conditionentropy,0,0,1,0));
	

		 
		 //smc-b
 		this.redAlgnames.add("MCE");
 		this.m_reduceAlg.add(new RSxmethod(m_data,-1,0));
//		 
//		this.redAlgnames.add("ECE");
//		this.m_reduceAlg.add(new RSxmethod(m_data,-1,10));
//		 
//		this.redAlgnames.add("ARP^1");
//		this.m_reduceAlg.add(new RSxmethod(m_data,-1,2));
//		
//		 
//		this.redAlgnames.add("ARP^2");
//		this.m_reduceAlg.add(new RSxmethod(m_data,-1,3));
//		 
			
		 //kbs
		// this.redAlgnames.add("SetCover");
      	// this.m_reduceAlg.add(new RSxmethod(m_data,bin,0));
     	 //this.redAlgnames.add("FoucsM");
       	 //this.m_reduceAlg.add(new Focus(m_data,bin,0));
      	 //this.redAlgnames.add("ABB");
       	 //this.m_reduceAlg.add(new ABB(m_data,bin,0));
        // this.redAlgnames.add("LVF");
      	 //this.m_reduceAlg.add(new xRandomSearch(m_data,bin,0,1000));
      	// this.redAlgnames.add("GA");
      	// this.m_reduceAlg.add(new RoughGASearch(m_data,-1, 0, 30 ,0.7,0.05,50));
		 
		 
		 
		 
			/*this.redAlgnames.add("newCovering");
			this.m_reduceAlg.add(new newRSmethod(this.m_data,bin,newEntropy.newCovering));
			//this.redAlgnames.add("newPartition");
			//this.m_reduceAlg.add(new newRSmethod(this.m_data,bin,newEntropy.newPartition));
			this.redAlgnames.add("newFuzzy");
			this.m_reduceAlg.add(new newRSmethod(this.m_data,bin,newEntropy.newFuzzy));
			this.redAlgnames.add("elementCovering");
			this.m_reduceAlg.add(new newRSmethod(this.m_data,bin,newEntropy.elementCovering));
			//this.redAlgnames.add("elementPartition");
			//this.m_reduceAlg.add(new newRSmethod(this.m_data,bin,newEntropy.elementPartition));
			this.redAlgnames.add("elementFuzzy");
			this.m_reduceAlg.add(new newRSmethod(this.m_data,bin,newEntropy.elementFuzzy));*/
			
			/*this.redAlgnames.add("newCovering+Core");
			this.m_reduceAlg.add(new coreRSmethod(this.m_data,bin,newEntropy.newCovering));
			this.redAlgnames.add("newPartition+Core");
			this.m_reduceAlg.add(new coreRSmethod(this.m_data,bin,newEntropy.newPartition));
			this.redAlgnames.add("newFuzzy+Core");
			this.m_reduceAlg.add(new coreRSmethod(this.m_data,bin,newEntropy.newFuzzy));
			this.redAlgnames.add("elementCovering+Core");
			this.m_reduceAlg.add(new coreRSmethod(this.m_data,bin,newEntropy.elementCovering));
			this.redAlgnames.add("elementPartition+Core");
			this.m_reduceAlg.add(new coreRSmethod(this.m_data,bin,newEntropy.elementPartition));
			this.redAlgnames.add("elementFuzzy+Core");
			this.m_reduceAlg.add(new coreRSmethod(this.m_data,bin,newEntropy.elementFuzzy));*/
			
		//this.redAlgnames.add("FCBF(log)");
		//this.m_reduceAlg.add(new FCBFmethod(this.m_data,bin,-1));
		 this.redAlgnames.add("FCBF(0)");
		 this.m_reduceAlg.add(new FCBFmethod(this.m_data,bin,0));
		//this.redAlgnames.add("RSmi");
		//this.m_reduceAlg.add(new RSmethod(this.m_data,bin, xStyle.ClassDependet));
		//this.redAlgnames.add("RSpositive");
		//this.m_reduceAlg.add(new RSmethod(this.m_data,bin, xStyle.positive_RSAR));
		/*this.redAlgnames.add("FuzzyLow");
		this.m_reduceAlg.add(new RSmethod(this.m_data,bin, xStyle.fuzzyPositive_Low));
		this.redAlgnames.add("FuzzyBoundary");
		this.m_reduceAlg.add(new RSmethod(this.m_data,bin, xStyle.fuzzyPositive_Boundary));*/
		
		
		
		
		
		
		//this.redAlgnames.add("Consistency");
		//this.m_reduceAlg.add(new Wekamethod(m_data,"CSE算法",new ConsistencySubsetEval(),new GreedyStepwise()));
		//this.redAlgnames.add("CfsSubEval");
		//this.m_reduceAlg.add(new Wekamethod(m_data,"CfsSubEval算法",new CfsSubsetEval(),new GreedyStepwise()));
		//this.redAlgnames.add("ReliefF");
		//double tau = 1/Math.sqrt(m_singificantlevel*(double)this.m_data.numInstances())*0.125;
		//Ranker se = new Ranker();
		//se.setThreshold(tau);
		//this.m_reduceAlg.add(new Wekamethod(m_data,"ReliefF算法",new ReliefFAttributeEval(),se));
		this.redAlgnames.add("FullAttributes");
		this.m_reduceAlg.add(new Fullset(m_data));

		
		this.allresData.redAlgnames = this.redAlgnames;
		this.allresData.claAlgnames = this.claAlgnames;
		 
		
		//约简属性集---带决策属性;约简个数--不带决策属性;
		for(int i=0;i<this.m_reduceAlg.size();++i){
			this.m_seletAtts.add(this.m_reduceAlg.get(i).m_selectAtt);
			this.m_numRedAtts.add(this.m_reduceAlg.get(i).m_numRed);
			this.m_timeRed.add(this.m_reduceAlg.get(i).m_useTime);
			this.m_processAll += this.m_reduceAlg.get(i).m_process;
		}
		
		//训练 得出准确率
		for(int i=0;i<this.m_classifiers.size();++i){//一个分类算法 对应m不同的约简算法 结果ans[m][runtime] 包括全集
			double[][] ans = new double[this.m_reduceAlg.size()][this.m_runTime];
			for(int j=0;j<this.m_reduceAlg.size();++j){
				double[] tmp = this.myEvolution(this.m_classifiers.get(i), this.m_data, this.m_seletAtts.get(j), this.m_numFold, this.m_runTime);
				for(int k =0;k<this.m_runTime;++k)
					ans[j][k] = tmp[k];
			}
			this.m_ACs.add(ans);
		}
		//统计结果
		int baseindex = 0;
		for(int i=0;i<this.m_classifiers.size();++i){//一个分类算法 统计一个结果 mean std pvalue wlt
			this.m_TestRes.add(MypairedTtest(this.m_ACs.get(i),baseindex, this.m_singificantlevel));
		}
		
		
		
		
		
		this.allresData.seletAtt = this.m_seletAtts;
		
		
		 
		//保存结果
		saveRes2file();
		
		
	}
	
	public ResData getResDataInfo(){
		return this.allresData;
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
		for(int i=0;i<this.m_reduceAlg.size()-1;++i){
			str2 += this.m_reduceAlg.get(i).algname+":" +Arrays.toString(this.m_seletAtts.get(i))+"\n";
		}
		str += this.m_processAll+"\n\n\n"+str2+"\n\n\n"+this.getRes()+"\n\n\n"+getTTestRes()+"\n\n\n"+this.getLatexString();
		
		//System.out.println(str);
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
					String wlt = l=="#"?"    ":("$^"+l+"$");
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
		String title = "\t\t 约简个数 \t 约简时间 \t ";
		for(int i=0;i<this.claAlgnames.size();++i){
			title+=this.claAlgnames.get(i)+"算法 \t ";
		}
 
		str = datainf+title+ "\n";
		String[] redAlgnames = new String[this.m_reduceAlg.size()];
		for(int i=0;i<this.m_reduceAlg.size();++i){
			redAlgnames[i] = this.m_reduceAlg.get(i).algname;
		}
	 
		Vector<String[]> latex = new Vector<String[]>();
		for(int i=0;i<this.m_reduceAlg.size();++i){

			String tmp1 = "";
			String[] latexLine = new String[this.m_classifiers.size()+2];
			tmp1 += redAlgnames[i]+"\t "+this.m_numRedAtts.get(i)+" \t\t "+Utils.doubleFormat("0.00", this.m_timeRed.get(i))+"s  ";
			latexLine[0]=this.m_numRedAtts.get(i).toString();
			latexLine[1]=Utils.doubleFormat("0.00", this.m_timeRed.get(i))+"s";
			
			String tempstr = Utils.doubleFormat("0.00", this.m_timeRed.get(i));
			this.allresData.numRed.add(this.m_numRedAtts.get(i));
			double tmp = Double.parseDouble(tempstr);
			if(i==this.m_reduceAlg.size()-1){
				this.allresData.timeRed.add(tmp==0.00?0.01:tmp);
			}
			else{
				this.allresData.timeRed.add(tmp);
			}
			
			Vector<ResNode> line = new Vector<ResNode>();
			String tmp2 = "";
			for(int j=0;j<this.m_classifiers.size();++j){
				double[][] testresj = this.m_TestRes.get(j);
				double ac = testresj[i][0]*100;
				double std = testresj[i][1]*100;
				double pval = testresj[i][3]; //base = -1
				double wtl = testresj[i][2]; //base = 0
					 
				String t = "";
				String l="";
				int intt = (int)wtl;
				switch(intt){
				case 0: {t = "B";l="\\Large\\circ";break;}
				case 1: {t = "L";l="\\Large\\textcolor{red}{\\checkmark}";break;}
				case 2: {t = "W";l="\\Large\\mathbf{\\times}";break;}
				case 3: {t = "T";l="\\Large-";break;}
				default:break;
				}
				tmp2+="\t "+Utils.doubleFormat("0.00", testresj[i][0]*100)+"% "+t;
				//latexLine[j+2]=Utils.doubleFormat("0.00", testresj[i][0]*100)+"\\%\\; "+t;
				latexLine[j+2]=Utils.doubleFormat("0.00", testresj[i][0]*100)+"\\%\\; "+l;
				
				ResNode oneAlg = new ResNode();
				oneAlg.elvMean = xMath.getDoubleRound(2, ac);
				oneAlg.elvStd = xMath.getDoubleRound(2, std);
				oneAlg.pval = pval;
				 
				oneAlg.setWLT(intt);
				line.add(oneAlg);
				
			}
			
			str += tmp1 + tmp2 + "\n";
			this.allresData.resDetail.add(line);
			
			latex.add(latexLine);
		}
		
		
 
		Vector2LaTex st = new Vector2LaTex(this.savepath,this.redAlgnames,this.claAlgnames,latex,this.getDataLatexInfor(),this.m_dataname);

		
		this.latexString = st.Latex;
		this.resString = str;
		return str;
		
	}
	
	//写入文件
	public  void writefile(String dataname, String str) throws DocumentException{
		 		   		
		 Document document = new Document();    
      
         // 第五步：关闭文档。    
         document.close();    
         
		   String Ostrtemp = "";
		   String Ostr = "";
		   
			File W_filename = new File(this.savepath+dataname+".txt");
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
	public String getLatexString(){
		
		return this.latexString;
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
		String dn = this.m_dataname.substring(0,1).toUpperCase() + this.m_dataname.substring(1);
		String datainf ="数据集名称： "+dn+" 样本个数："+ins+" 属性个数："+att+"("+att_nominal+" Nominal/"+att_real+" Real) "+
				" 类别个数:"+cla+"\n\n";
		
		String datainf2 ="DataName： "+dn+" Ojbects："+ins+" Attributes："+att+"("+att_nominal+" Nominal/"+att_real+" Real) "+
		" Classes:"+cla+"\n\n";
		
		this.allresData.dataInfo = datainf2;
		this.allresData.dataname = dn;
		this.allresData.numObjects = ins;
		this.allresData.numAttributes = att-1;//文章里 数据信息不包括决策属性
		this.allresData.numClasses = cla;
		this.allresData.numNominalAttributes = att_nominal;
		this.allresData.numNumericAttributes = att_real;
		
		return datainf;
	}
	
	public String getDataLatexInfor(){
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
		String dn = this.m_dataname.substring(0,1).toUpperCase() + this.m_dataname.substring(1);
		String datainf =dn+"\\; Samples:\\,"+ins+"\\; Attriubtes:\\,"+att+"\\,(\\,"+att_nominal+"\\,Nominal/\\,"+att_real+"\\,Real\\,)"+
				"\\; Classes:\\,"+cla;
		return datainf;
	}	 
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
