package Part2;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;
import java.util.Vector;

import myUtils.ClusterUtils;

import Part1.ClusterEnsemble;
import Part1.MethodGenerateClustering;
import Part1.xFigurePart1;
import Part1Ex.EvaluateMethodPart1Ex;
import Part1Ex.MCF_s_w_kmodes;
import Part1Ex.MCF_w_kmodes;
import Part2.MP_HierCluster.LinkType;

import weka.core.Instances;
import weka.core.SelectedTag;

public class EvaluateMethodPart2 {

	
	public Instances data = null;
	public Instances dataforcluster = null;
	public String dataname = null;
	public String exppatheps = null;
	public String exppathjpg = null;
	public double base_ac = 0;
	public double base_nmi = 0;
	public double[] base_ac_values = null;
	public double[] base_nmi_values = null;
	public int baseRnd = 0;
	public int runtimes = 10;
	
	public int[] classes = null;
	public double baseAlpha = -1;
	public double baseper = -1;
	public Vector<double[][]> xydata_ac = new Vector<double[][]> ();
	public Vector<double[][]> xydata_nmi =  new Vector<double[][]> ();
 
	public String[] keys = null;
	public Vector<double[]> parameter_s = null;
 
	public double[][]  Table_ac = null;
	public double[][] Table_nmi = null;
	
	public EvaluateMethodPart2(String pth, String dataName, String expjpg, String expeps,int runtimes,int baseRnd , double[] baseParameters) throws Exception{
		  Instances data = new Instances(new FileReader(pth));
		  data.setClassIndex(data.numAttributes()-1);
		  this.data = data;
		  Instances dataforcluster = new Instances(new FileReader(pth));
		  dataforcluster.deleteAttributeAt(dataforcluster.numAttributes()-1);
		  this.dataforcluster = dataforcluster;
		  this.dataname = dataName;
		  this.exppatheps = expeps;
		  this.exppathjpg = expjpg;
		  this.runtimes = runtimes;
		  this.baseRnd = baseRnd;
		  //this.baseAlpha = baseParameters[1];
		  this.baseper = baseParameters[0];

		  double[] tmpdclass = data.attributeToDoubleArray(data.numAttributes()-1);
			this.classes = new int[data.numInstances()];
			for(int i=0;i<tmpdclass.length;++i)
				this.classes[i]=(int)tmpdclass[i];
		  
			  this.keys = new String[5];
			  keys[0]="Single";
			  keys[1]="Complete";
			  keys[2]="Avarage";
			  keys[3]="Mean";
			  keys[4]="BestHCM";
		  
		  parameter_s = new Vector<double[]>();
		   
		  //parameter_s.add(alpha);
		  double[] per = {0,0.1,0.2,0.3,0.4,0.5,0.6,0.7,0.8,0.9,1};
		  //double[] per = {0,0.05,0.1,0.15,0.2,0.25,0.3,0.35,0.4,0.45,0.5};
		   ////double[] per = {0.5,0.6,0.7,0.8,0.9,1};
		  //double[] per = {0,0.1,0.2,0.3,0.4,0.5};
		  parameter_s.add(per);
		  base_ac_values = new double[runtimes];
		  base_nmi_values = new double[runtimes];
		  int fold = 5;
		  getCompareAlgResults(fold);
	}
	
	private void getCompareAlgResults(int fold) throws Exception {
		// TODO Auto-generated method stub
		int ninstances = this.data.numInstances();
		Vector<int []> res = new Vector<int[]>();
		HierarchicalClusterer hc1 = new HierarchicalClusterer(new DistanceWithLabel(new boolean[ninstances],new int[ninstances]));
		hc1.setNumClusters(this.data.numClasses());
		Instances newdata1 = new Instances(this.dataforcluster); 
		hc1.buildClusterer(newdata1);	
		int linktype1 = LinkType.SINGLE.ordinal();
		hc1.setLinkType(new SelectedTag(linktype1, MP_HierCluster.TAGS_LINK_TYPE));	 
		res.add( hc1.m_nClusterNr.clone());
		
		
		
		HierarchicalClusterer hc2 = new HierarchicalClusterer(new DistanceWithLabel(new boolean[ninstances],new int[ninstances]));
		hc2.setNumClusters(this.data.numClasses());
		Instances newdata2 = new Instances(this.dataforcluster); 
		hc2.buildClusterer(newdata2);	
		int linktype2 = LinkType.COMPLETE.ordinal();
		hc2.setLinkType(new SelectedTag(linktype2, MP_HierCluster.TAGS_LINK_TYPE));	 
		res.add( hc2.m_nClusterNr.clone());
		 
		
		HierarchicalClusterer hc3 = new HierarchicalClusterer(new DistanceWithLabel(new boolean[ninstances],new int[ninstances]));
		hc3.setNumClusters(this.data.numClasses());
		Instances newdata3 = new Instances(this.dataforcluster); 
		hc3.buildClusterer(newdata3);	
		int linktype3 = LinkType.AVERAGE.ordinal();
		hc2.setLinkType(new SelectedTag(linktype3, MP_HierCluster.TAGS_LINK_TYPE));	 
		res.add( hc3.m_nClusterNr.clone());
		
		HierarchicalClusterer hc4 = new HierarchicalClusterer(new DistanceWithLabel(new boolean[ninstances],new int[ninstances]));
		hc4.setNumClusters(this.data.numClasses());
		Instances newdata4 = new Instances(this.dataforcluster); 
		hc4.buildClusterer(newdata4);	
		int linktype4 = LinkType.AVERAGE.ordinal();
		hc4.setLinkType(new SelectedTag(linktype4, MP_HierCluster.TAGS_LINK_TYPE));	 
		res.add( hc4.m_nClusterNr.clone());
		
		double bestAC = -1;
		double bestNMI = -1;
		for(int i=0;i<res.size();++i){
			double tmpAC = ClusterUtils.getAC_ByBipartiteGraph(this.classes, res.elementAt(i));
			double tmpNMI = ClusterUtils.NMI(this.classes,  res.elementAt(i));
			bestAC = tmpAC>bestAC?tmpAC:bestAC;
			bestNMI = tmpNMI>bestNMI?tmpNMI:bestNMI;
		}
		
		
		this.base_ac = bestAC;
		this.base_nmi = bestNMI;
		for(int i=0;i<this.runtimes;++i){
			this.base_ac_values[i]=this.base_ac;
			this.base_nmi_values[i]=this.base_nmi;
		}
		
	}
	public static Vector<SplitMergeCluster> getClusters(){
		Vector<SplitMergeCluster> cls = new Vector<SplitMergeCluster>();
		 
		return null;
		
	}


	public void evaluatePer() throws Exception{
		  
		
		 
		  int rndbase = baseRnd;
		  double[][] ans_ac = new double[parameter_s.elementAt(0).length][this.keys.length];
		  double[][] ans_nmi = new double[parameter_s.elementAt(0).length][this.keys.length];
		  for(int a=0;a<parameter_s.elementAt(0).length;++a){
			  double avg0=0,avg0_s=0,avg1=0,avg1_s=0;
			  double avg0_nmi=0,avg0_s_nmi=0,avg1_nmi=0,avg1_s_nmi=0;
			  double[][] baseAns_ac = new double[this.keys.length-1][runtimes];
			  double[][] baseAns_nmi = new double[this.keys.length-1][runtimes];
			  for(int r=0;r<runtimes;++r){
				  double curper = parameter_s.elementAt(0)[a];
				  boolean[] labeled1 =ClusterEnsemble.getRandomLabeled(curper,data.numInstances(),rndbase++);
				  boolean[] labeled2 =ClusterEnsemble.getRandomLabeled(curper,data.numInstances(),rndbase++);
				  boolean[] labeled3 =ClusterEnsemble.getRandomLabeled(curper,data.numInstances(),rndbase++);
				  boolean[] labeled4 =ClusterEnsemble.getRandomLabeled(curper,data.numInstances(),rndbase++);
				  /*int n = 4;
				  SP_kmodes sp1 = new SP_kmodes(data,dataforcluster,labeled1,rndbase++,n);
				  SP_kmodes sp2 = new SP_kmodes(data,dataforcluster,labeled2,rndbase++,n);
				  SP_kmodes sp3 = new SP_kmodes(data,dataforcluster,labeled3,rndbase++,n);
				  SP_kmodes sp4 = new SP_kmodes(data,dataforcluster,labeled4,rndbase++,n);*/
				  
				  SP_equivalence sp1 = new SP_equivalence(data,dataforcluster,labeled1);
				  SP_equivalence sp2 = new SP_equivalence(data,dataforcluster,labeled2);
				  SP_equivalence sp3 = new SP_equivalence(data,dataforcluster,labeled3);
				  SP_equivalence sp4 = new SP_equivalence(data,dataforcluster,labeled4);
				  
				MP_HierCluster mp1 = new MP_HierCluster(data,dataforcluster,labeled1,new DistanceWithLabel(labeled1,classes),LinkType.SINGLE);
				MP_HierCluster mp2 = new MP_HierCluster(data,dataforcluster,labeled2,new DistanceWithLabel(labeled2,classes),LinkType.COMPLETE);
				MP_HierCluster mp3 = new MP_HierCluster(data,dataforcluster,labeled3,new DistanceWithLabel(labeled3,classes),LinkType.AVERAGE);
				MP_HierCluster mp4 = new MP_HierCluster(data,dataforcluster,labeled4,new DistanceWithLabel(labeled4,classes),LinkType.MEAN);
					
				SplitMergeCluster smc_sig = new SplitMergeCluster(sp1,mp1,"SINGLE");
				SplitMergeCluster smc_avg = new SplitMergeCluster(sp2,mp2,"COMPLETE");
				SplitMergeCluster smc_com = new SplitMergeCluster(sp3,mp3,"AVERAGE");
				SplitMergeCluster smc_cen = new SplitMergeCluster(sp4,mp4,"MEAN");
					
					 
					
					
				       if(parameter_s.elementAt(0)[a]==this.baseper){
							  baseAns_ac[0][r] = smc_sig.getEvaluationRes().ac;
							  baseAns_ac[1][r] = smc_avg.getEvaluationRes().ac;
						      baseAns_ac[2][r] = smc_com.getEvaluationRes().ac;
							  baseAns_ac[3][r] = smc_cen.getEvaluationRes().ac;
							  baseAns_nmi[0][r] = smc_sig.getEvaluationRes().nmi;
							  baseAns_nmi[1][r] = smc_avg.getEvaluationRes().nmi;
							  baseAns_nmi[2][r] = smc_com.getEvaluationRes().nmi;
							  baseAns_nmi[3][r] = smc_cen.getEvaluationRes().nmi;
						  }
				       avg0+=smc_sig.getEvaluationRes().ac;
				       avg0_s+= smc_avg.getEvaluationRes().ac;
				       avg1+=smc_com.getEvaluationRes().ac;
				       avg1_s+=smc_cen.getEvaluationRes().ac;
				       avg0_nmi+=smc_sig.getEvaluationRes().nmi;
				       avg0_s_nmi+=smc_avg.getEvaluationRes().nmi;
				       avg1_nmi+=smc_com.getEvaluationRes().nmi;
				       avg1_s_nmi+=smc_cen.getEvaluationRes().nmi;
				       if(parameter_s.elementAt(0)[a]==this.baseper){
				    	   this.Table_ac = baseAns_ac;
				    	   this.Table_nmi = baseAns_nmi;	   
				       }
			  }
			  
			  ans_ac[a][0]=avg0/(double)runtimes;
		       ans_ac[a][1]=avg0_s/(double)runtimes;
		       ans_ac[a][2]=avg1/(double)runtimes;
		       ans_ac[a][3]=avg1_s/(double)runtimes;
		       ans_ac[a][4]=this.base_ac;
		       ans_nmi[a][0]=avg0_nmi/(double)runtimes;
		       ans_nmi[a][1]=avg0_s_nmi/(double)runtimes;
		       ans_nmi[a][2]=avg1_nmi/(double)runtimes;
		       ans_nmi[a][3]=avg1_s_nmi/(double)runtimes;
		       ans_nmi[a][4]=this.base_nmi;
		       //System.out.println( ans_ac[a][0]);
		  }
		  

		  this.xydata_ac.add(ans_ac);
		  this.xydata_nmi.add(ans_nmi);
		
	 
		  
	}

	public static Vector<Vector<double[][]>>  getFigures(Vector<String[]> datainfo, String exppathjpg,String exppatheps,int times,int basernd,double[] baseParameters) throws Exception {
		Vector<Vector<double[][]>> res = new Vector<Vector<double[][]>> ();
		for(int i=0;i<datainfo.size();++i){
			EvaluateMethodPart2 em = new EvaluateMethodPart2(datainfo.elementAt(i)[0],datainfo.elementAt(i)[1],exppathjpg,exppatheps,times, basernd, baseParameters);
		 
		  em.evaluatePer();
		  //System.out.println(datanames[i]+"-Per:Done");
		 
		  em.saveFigure();
		  res.add(em.getComRes());
		}
		 return res;
	}
	public  Vector<double[][]> getComRes() {
		// TODO Auto-generated method stub
		Vector<double[][]> ans = new Vector<double[][]>();
		double[][][] res = new double [this.keys.length][2][this.runtimes];
		  for(int i=0;i<res.length-1;++i){
			  for(int j=0;j<this.runtimes;++j){
				  res[i][0][j]=this.Table_ac[i][j];
				  res[i][1][j]=this.Table_nmi[i][j];
			  }
			  ans.add(res[i]);
		  }
		  for(int j=0;j<this.runtimes;++j){
			  res[this.keys.length-1][0][j]=this.base_ac_values[j];
			  res[this.keys.length-1][1][j]=this.base_nmi_values[j];
		  }
		 ans.add(res[this.keys.length-1]);
		return ans;
	}
	private void saveFigure() throws IOException {
		// TODO Auto-generated method stub
		 String[] ps = {"Label percent"};
		  for(int i=0;i<ps.length;++i)
		  {
			  Map<String,double[][]> xydata_ac = new LinkedHashMap<String,double[][]> ();
			  Map<String,double[][]>  xydata_nmi =new LinkedHashMap<String,double[][]> ();
			  for(int k=0;k<this.keys.length;++k){
				  double[][] aline_ac = new double[2][parameter_s.elementAt(i).length];
				  double[][] aline_nmi = new double[2][parameter_s.elementAt(i).length];
				  for(int a=0;a<parameter_s.elementAt(i).length;++a){
					  aline_ac[0][a]=parameter_s.elementAt(0)[a];
					  aline_ac[1][a]=this.xydata_ac.elementAt(i)[a][k];
					  aline_nmi[0][a]=parameter_s.elementAt(0)[a];
					  aline_nmi[1][a]=this.xydata_nmi.elementAt(i)[a][k];
				  }
				  xydata_ac.put(keys[k], aline_ac);
				  xydata_nmi.put(keys[k], aline_nmi);
				  //System.out.println("k"+k);
				 // System.out.println("keys[k]"+keys[k]);
				  //System.out.println(xydata_ac.size());
				 
			  }
			 
			  
			 
			  String per ="per";
			  String filetitle_ac = this.dataname+"-"+per+"-AC";
			  String filetitle_nmi = this.dataname+"-"+per+"-NMI";
	
			  String[] title_ac_eps={this.dataname,"AC",ps[i]};
			  String[] title_nmi_eps={this.dataname,"NMI",ps[i]};
			 
			  xFigurePart1.saveFigureAsEPS(title_ac_eps, this.exppatheps+filetitle_ac, xydata_ac);
			  xFigurePart1.saveFigureAsEPS(title_nmi_eps, this.exppatheps+filetitle_nmi, xydata_nmi);
		  }
	}


	public static void Test1() throws Exception{
		String datapath = "C:/Users/Eric/Desktop/2012Çï¶¬/±ÏÒµÉè¼Æ/DataSet/Soybean.arff";
		Instances data = new Instances(new FileReader(datapath));
		data.setClassIndex(data.numAttributes()-1);
		Instances dataforcluster = new Instances(new FileReader(datapath));
		dataforcluster.deleteAttributeAt(dataforcluster.numAttributes()-1);
		boolean[] labels = new boolean[data.numInstances()];
		//for(int i = 0;i<data.numInstances()/1;++i)
			//labels[i] = true;
		double[] tmpdclass = data.attributeToDoubleArray(data.numAttributes()-1);
		int[] classes = new int[data.numInstances()];
		for(int i=0;i<tmpdclass.length;++i)
			classes[i]=(int)tmpdclass[i];
		
		SP_equivalence sp = new SP_equivalence(data,dataforcluster,labels);
		//MP_HierCluster mp = new MP_HierCluster(data,dataforcluster,labels,new DistanceWithLabel(labels,classes));
		//SplitMergeCluster smc = new SplitMergeCluster(sp,mp,"testAlg");
		//double ac = smc.getEvaluationRes().ac;
		//double nmi = smc.getEvaluationRes().nmi;
		//System.out.println(ac+":"+nmi);
	}
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		Test1();
	}

}
