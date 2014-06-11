package Part1Ex;

import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;
import java.util.Vector;

import Part1.ClusterEnsemble;
import Part1.MethodGenerateClustering;
import Part1.xFigurePart1;

import myUtils.ClusterUtils;
import weka.clusterers.SimpleKMeans;
import weka.core.Instances;


public class EvaluateMethodPart1Ex {
	public Instances data = null;
	public Instances dataforcluster = null;
	public String dataname = null;
	public String exppatheps = null;
	public String exppathjpg = null;
	public double kmodes_ac = 0;
	public double kmodes_nmi = 0;
	public double[] kmodes_ac_values = null;
	public double[] kmodes_nmi_values = null;
	public int baseRnd = 0;
	public int runtimes = 10;
	public double baseAlpha = -1;
	public int baseensize = -1;
	public double baseper = -1;
	public Vector<double[][]> xydata_ac = new Vector<double[][]> ();
	public Vector<double[][]> xydata_nmi =  new Vector<double[][]> ();
 
	public String[] keys = null;
	public Vector<double[]> parameter_s = null;
 
	public Vector<double[][] > base_ac = new Vector<double[][] >();
	public Vector<double[][] > base_nmi = new Vector<double[][] >();
	public int[] p_inds = new int[3];
	 
	public double bestValue = -1;
	public int bestValue_ind = -1;
	public EvaluateMethodPart1Ex(String pth, String dataName, String expjpg, String expeps,int runtimes,int baseRnd , double[] baseParameters) throws Exception{
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
		  this.baseAlpha = baseParameters[0];
		  this.baseensize = (int)baseParameters[1];
		  this.baseper = baseParameters[2];
 
		  this.keys = new String[5];
		  keys[0]="w_kmodes";
		  keys[1]="s_w_kmodes";
		  keys[2]="random_w_kmodes";
		  keys[3]="random_s_w_kmodes";
		  keys[4]="single_kmodes";
		  parameter_s = new Vector<double[]>();
		  double[] alpha = {0,0.1,0.2,0.3,0.4,0.5,0.6,0.7,0.8,0.9,1};
		  parameter_s.add(alpha);
		  double[] ensize = {5,13,15,18,20,25,30};
		  parameter_s.add(ensize);
		  double[] per = {0,0.05,0.1,0.15,0.2,0.25,0.3,0.35,0.4,0.45,0.5};
		  parameter_s.add(per);
		  kmodes_ac_values = new double[runtimes];
		  kmodes_nmi_values = new double[runtimes];
		  getKmodesResults(baseRnd);
	}
	private void getKmodesResults(int rnd) throws Exception {
		kmodes_ac = 0;
		kmodes_nmi = 0;
		// TODO Auto-generated method stub
		Random Rnd = new Random(rnd);
 
		
		for(int r=0;r<runtimes;++r){
			SimpleKMeans km = new SimpleKMeans(); 
			km.setMaxIterations(100);
			km.setNumClusters(data.numClasses());
			km.setDontReplaceMissingValues(true);
			km.setSeed(Rnd.nextInt());
			km.setPreserveInstancesOrder(true);
			km.buildClusterer(dataforcluster);
			  int[] res = km.getAssignments();
			  //System.out.println(Arrays.toString(res));
			  kmodes_ac_values[r] = ClusterUtils.CA(this.data, res);
			  kmodes_ac+=kmodes_ac_values[r];
			  kmodes_nmi_values[r]=ClusterUtils.NMI(this.data, res);
			  kmodes_nmi+= kmodes_nmi_values[r];
		}
		//System.out.println(Arrays.toString(ans_ac));
		//System.out.println(Arrays.toString(ans_nmi));
		 kmodes_ac =  kmodes_ac/(double)runtimes;
		 kmodes_nmi =  kmodes_nmi/(double)runtimes;
	}

	
	public void evaluateAlpha() throws Exception{
		  int ensize = this.baseensize;
		  double per = this.baseper;
		  int rndbase = baseRnd;
		 
		  double[][] ans_ac = new double[parameter_s.elementAt(0).length][this.keys.length];
		  double[][] ans_nmi = new double[parameter_s.elementAt(0).length][this.keys.length];
		  for(int a=0;a<parameter_s.elementAt(0).length;++a){
			  double avg0=0,avg0_s=0,avg1=0,avg1_s=0;
			  double avg0_nmi=0,avg0_s_nmi=0,avg1_nmi=0,avg1_s_nmi=0;
			  double[][] baseAns_ac = new double[this.keys.length-1][runtimes];
			  double[][] baseAns_nmi = new double[this.keys.length-1][runtimes];
			  for(int r=0;r<runtimes;++r){
				  boolean[] labeled =ClusterEnsemble.getRandomLabeled(per,data.numInstances(),r);
				  MethodGenerateClustering mc0 = new MethodGenerateClustering(data,dataforcluster,labeled,ensize,rndbase++,0,parameter_s.elementAt(0)[a]);
				  MethodGenerateClustering mc1 = new MethodGenerateClustering(data,dataforcluster,labeled,ensize,rndbase++,1,parameter_s.elementAt(0)[a]);
				       Random rd = new Random(rndbase);
				  	   ClusterEnsemble ce0 =new ClusterEnsemble(mc0,new MCF_w_kmodes(rd.nextInt()),false);
				       ClusterEnsemble ce0_s =new ClusterEnsemble(mc0,new MCF_s_w_kmodes(rd.nextInt()),false);
				       ClusterEnsemble ce1 =new ClusterEnsemble(mc1,new MCF_w_kmodes(rd.nextInt()),false);
				       ClusterEnsemble ce1_s =new ClusterEnsemble(mc1,new MCF_s_w_kmodes(rd.nextInt()),false);
						  if(parameter_s.elementAt(0)[a]==this.baseAlpha){
							  baseAns_ac[0][r] = ce0.fr.ac;
							  baseAns_ac[1][r] = ce0_s.fr.ac;
						      baseAns_ac[2][r] = ce1.fr.ac;
							  baseAns_ac[3][r] = ce1_s.fr.ac;
							  baseAns_nmi[0][r] = ce0.fr.nmi;
							  baseAns_nmi[1][r] = ce0_s.fr.nmi;
							  baseAns_nmi[2][r] = ce1.fr.nmi;
							  baseAns_nmi[3][r] = ce1_s.fr.nmi;
						  }
				       avg0+=ce0.fr.ac;
				       avg0_s+=ce0_s.fr.ac;
				       avg1+=ce1.fr.ac;
				       avg1_s+=ce1_s.fr.ac;
				       avg0_nmi+=ce0.fr.nmi;
				       avg0_s_nmi+=ce0_s.fr.nmi;
				       avg1_nmi+=ce1.fr.nmi;
				       avg1_s_nmi+=ce1_s.fr.nmi;
			  }
			  
			  ans_ac[a][0]=avg0/(double)runtimes;
		       ans_ac[a][1]=avg0_s/(double)runtimes;
		       ans_ac[a][2]=avg1/(double)runtimes;
		       ans_ac[a][3]=avg1_s/(double)runtimes;
		       ans_ac[a][4]=this.kmodes_ac;
		       ans_nmi[a][0]=avg0_nmi/(double)runtimes;
		       ans_nmi[a][1]=avg0_s_nmi/(double)runtimes;
		       ans_nmi[a][2]=avg1_nmi/(double)runtimes;
		       ans_nmi[a][3]=avg1_s_nmi/(double)runtimes;
		       ans_nmi[a][4]=this.kmodes_nmi;
		       if(parameter_s.elementAt(0)[a]==this.baseAlpha){
		    	   this.base_ac.add(baseAns_ac);
		    	   this.base_nmi.add(baseAns_nmi);
		    	   this.p_inds[0] = a;
		    	   double tmp = ans_ac[a][0]+ans_ac[a][1]+ans_ac[a][2]+ans_ac[a][3];
		    	   if(tmp >this.bestValue){
		    		   this.bestValue = tmp;
		    		   this.bestValue_ind = 0;
		    		   }
		    	   
		    		   
		       }
		      
		  }
		 
		
		  this.xydata_ac.add(ans_ac);
		  this.xydata_nmi.add(ans_nmi);
		
	}
	public void evaluateEnsembleSize() throws Exception{

		  int rndbase = baseRnd;
		  
		  double[][] ans_ac = new double[parameter_s.elementAt(1).length][this.keys.length];
		  double[][] ans_nmi = new double[parameter_s.elementAt(1).length][this.keys.length];
		  for(int a=0;a<parameter_s.elementAt(1).length;++a){
			  double avg0=0,avg0_s=0,avg1=0,avg1_s=0;
			  double avg0_nmi=0,avg0_s_nmi=0,avg1_nmi=0,avg1_s_nmi=0;
			  double[][] baseAns_ac = new double[this.keys.length-1][runtimes];
			  double[][] baseAns_nmi = new double[this.keys.length-1][runtimes];
			  for(int r=0;r<runtimes;++r){
				  boolean[] labeled =ClusterEnsemble.getRandomLabeled(this.baseper,data.numInstances(),r);
				  MethodGenerateClustering mc0 = new MethodGenerateClustering(data,dataforcluster,labeled,(int)parameter_s.elementAt(1)[a],rndbase++,0,this.baseAlpha);
				  MethodGenerateClustering mc1 = new MethodGenerateClustering(data,dataforcluster,labeled,(int)parameter_s.elementAt(1)[a],rndbase++,1,this.baseAlpha);
				  Random rd = new Random(rndbase);
				   ClusterEnsemble ce0 =new ClusterEnsemble(mc0,new MCF_w_kmodes(rd.nextInt()),false);
			       ClusterEnsemble ce0_s =new ClusterEnsemble(mc0,new MCF_s_w_kmodes(rd.nextInt()),false);
			       ClusterEnsemble ce1 =new ClusterEnsemble(mc1,new MCF_w_kmodes(rd.nextInt()),false);
			       ClusterEnsemble ce1_s =new ClusterEnsemble(mc1,new MCF_s_w_kmodes(rd.nextInt()),false);
				       if((int)parameter_s.elementAt(1)[a]==this.baseensize){
							  baseAns_ac[0][r] = ce0.fr.ac;
							  baseAns_ac[1][r] = ce0_s.fr.ac;
						      baseAns_ac[2][r] = ce1.fr.ac;
							  baseAns_ac[3][r] = ce1_s.fr.ac;
							  baseAns_nmi[0][r] = ce0.fr.nmi;
							  baseAns_nmi[1][r] = ce0_s.fr.nmi;
							  baseAns_nmi[2][r] = ce1.fr.nmi;
							  baseAns_nmi[3][r] = ce1_s.fr.nmi;
						  }
				       avg0+=ce0.fr.ac;
				       avg0_s+=ce0_s.fr.ac;
				       avg1+=ce1.fr.ac;
				       avg1_s+=ce1_s.fr.ac;
				       avg0_nmi+=ce0.fr.nmi;
				       avg0_s_nmi+=ce0_s.fr.nmi;
				       avg1_nmi+=ce1.fr.nmi;
				       avg1_s_nmi+=ce1_s.fr.nmi;
			  }
			  
			  ans_ac[a][0]=avg0/(double)runtimes;
		       ans_ac[a][1]=avg0_s/(double)runtimes;
		       ans_ac[a][2]=avg1/(double)runtimes;
		       ans_ac[a][3]=avg1_s/(double)runtimes;
		       ans_ac[a][4]=this.kmodes_ac;
		       ans_nmi[a][0]=avg0_nmi/(double)runtimes;
		       ans_nmi[a][1]=avg0_s_nmi/(double)runtimes;
		       ans_nmi[a][2]=avg1_nmi/(double)runtimes;
		       ans_nmi[a][3]=avg1_s_nmi/(double)runtimes;
		       ans_nmi[a][4]=this.kmodes_nmi;
		       //System.out.println( ans_ac[a][0]);
		       if((int)parameter_s.elementAt(1)[a]==this.baseensize){
		    	   this.base_ac.add(baseAns_ac);
		    	   this.base_nmi.add(baseAns_nmi);
		    	   this.p_inds[1] = a;
		    	   double tmp = ans_ac[a][0]+ans_ac[a][1]+ans_ac[a][2]+ans_ac[a][3];
		    	   if(tmp >this.bestValue){
		    		   this.bestValue = tmp;
		    		   this.bestValue_ind = 1;
		    		   }
		    	   
		    		   
		       }
		  }
		
		  this.xydata_ac.add(ans_ac);
		  this.xydata_nmi.add(ans_nmi);
		  
	}
	public void evaluatePer() throws Exception{
		  
		
 
		  int rndbase = baseRnd;
		  double[][] ans_ac = new double[parameter_s.elementAt(2).length][this.keys.length];
		  double[][] ans_nmi = new double[parameter_s.elementAt(2).length][this.keys.length];
		  for(int a=0;a<parameter_s.elementAt(2).length;++a){
			  double avg0=0,avg0_s=0,avg1=0,avg1_s=0;
			  double avg0_nmi=0,avg0_s_nmi=0,avg1_nmi=0,avg1_s_nmi=0;
			  double[][] baseAns_ac = new double[this.keys.length-1][runtimes];
			  double[][] baseAns_nmi = new double[this.keys.length-1][runtimes];
			  for(int r=0;r<runtimes;++r){
				  boolean[] labeled =ClusterEnsemble.getRandomLabeled(parameter_s.elementAt(2)[a],data.numInstances(),r);
				  MethodGenerateClustering mc0 = new MethodGenerateClustering(data,dataforcluster,labeled,this.baseensize,rndbase++,0,this.baseAlpha);
				  MethodGenerateClustering mc1 = new MethodGenerateClustering(data,dataforcluster,labeled,this.baseensize,rndbase++,1,this.baseAlpha);
				   Random rd = new Random(rndbase);
				   ClusterEnsemble ce0 =new ClusterEnsemble(mc0,new MCF_w_kmodes(rd.nextInt()),false);
			       ClusterEnsemble ce0_s =new ClusterEnsemble(mc0,new MCF_s_w_kmodes(rd.nextInt()),false);
			       ClusterEnsemble ce1 =new ClusterEnsemble(mc1,new MCF_w_kmodes(rd.nextInt()),false);
			       ClusterEnsemble ce1_s =new ClusterEnsemble(mc1,new MCF_s_w_kmodes(rd.nextInt()),false);
				       if(parameter_s.elementAt(2)[a]==this.baseper){
							  baseAns_ac[0][r] = ce0.fr.ac;
							  baseAns_ac[1][r] = ce0_s.fr.ac;
						      baseAns_ac[2][r] = ce1.fr.ac;
							  baseAns_ac[3][r] = ce1_s.fr.ac;
							  baseAns_nmi[0][r] = ce0.fr.nmi;
							  baseAns_nmi[1][r] = ce0_s.fr.nmi;
							  baseAns_nmi[2][r] = ce1.fr.nmi;
							  baseAns_nmi[3][r] = ce1_s.fr.nmi;
						  }
				       avg0+=ce0.fr.ac;
				       avg0_s+=ce0_s.fr.ac;
				       avg1+=ce1.fr.ac;
				       avg1_s+=ce1_s.fr.ac;
				       avg0_nmi+=ce0.fr.nmi;
				       avg0_s_nmi+=ce0_s.fr.nmi;
				       avg1_nmi+=ce1.fr.nmi;
				       avg1_s_nmi+=ce1_s.fr.nmi;
				       if(parameter_s.elementAt(2)[a]==this.baseper){
				    	   this.base_ac.add(baseAns_ac);
				    	   this.base_nmi.add(baseAns_nmi);
				    	   this.p_inds[2] = a;
				    	   double tmp = ans_ac[a][0]+ans_ac[a][1]+ans_ac[a][2]+ans_ac[a][3];
				    	   if(tmp >this.bestValue){
				    		   this.bestValue = tmp;
				    		   this.bestValue_ind = 2;
				    		   }
				    	   
				    		   
				       }
			  }
			  
			  ans_ac[a][0]=avg0/(double)runtimes;
		       ans_ac[a][1]=avg0_s/(double)runtimes;
		       ans_ac[a][2]=avg1/(double)runtimes;
		       ans_ac[a][3]=avg1_s/(double)runtimes;
		       ans_ac[a][4]=this.kmodes_ac;
		       ans_nmi[a][0]=avg0_nmi/(double)runtimes;
		       ans_nmi[a][1]=avg0_s_nmi/(double)runtimes;
		       ans_nmi[a][2]=avg1_nmi/(double)runtimes;
		       ans_nmi[a][3]=avg1_s_nmi/(double)runtimes;
		       ans_nmi[a][4]=this.kmodes_nmi;
		       //System.out.println( ans_ac[a][0]);
		  }
		  
 
		  this.xydata_ac.add(ans_ac);
		  this.xydata_nmi.add(ans_nmi);
		
	 
		  
	}

	
 
	public static Vector<Vector<double[][]>>  getFigures(Vector<String[]> datainfo, String exppathjpg,String exppatheps,int times,int basernd,double[] baseParameters) throws Exception {
		Vector<Vector<double[][]>> res = new Vector<Vector<double[][]>> ();
		for(int i=0;i<datainfo.size();++i){
			EvaluateMethodPart1Ex em = new EvaluateMethodPart1Ex(datainfo.elementAt(i)[0],datainfo.elementAt(i)[1],exppathjpg,exppatheps,times, basernd, baseParameters);
		  em.evaluateAlpha();
		  //System.out.println(datanames[i]+"-Alpha:Done");
		  em.evaluateEnsembleSize();
		  //System.out.println(datanames[i]+"-Ensemble:Done");
		  em.evaluatePer();
		  //System.out.println(datanames[i]+"-Per:Done");
		 
		  em.saveFigure();
		  res.add(em.getComRes());
		}
		 return res;
	}
	private void saveFigure() throws IOException {
		// TODO Auto-generated method stub
		 String[] ps = {"Alpha","Ensemble Size","Label percent"};
		  for(int i=0;i<ps.length;++i)
		  {
			  if(i!=this.bestValue_ind){
				  		for(int j=0;j<keys.length-1;++j){
				  			this.xydata_ac.elementAt(i)[this.p_inds[i]][j]=this.xydata_ac.elementAt(this.bestValue_ind)[this.p_inds[this.bestValue_ind]][j];
				  		}
			  }
		  }
		 
		  for(int i=0;i<ps.length;++i)
		  {
			  Map<String,double[][]> xydata_ac = new LinkedHashMap<String,double[][]> ();
			  Map<String,double[][]>  xydata_nmi =new LinkedHashMap<String,double[][]> ();
			  for(int k=0;k<this.keys.length;++k){
				  double[][] aline_ac = new double[2][parameter_s.elementAt(i).length];
				  double[][] aline_nmi = new double[2][parameter_s.elementAt(i).length];
				  for(int a=0;a<parameter_s.elementAt(i).length;++a){
					  aline_ac[0][a]=i==1?(int)parameter_s.elementAt(i)[a]:parameter_s.elementAt(i)[a];
					  aline_ac[1][a]=this.xydata_ac.elementAt(i)[a][k];
					  aline_nmi[0][a]=i==1?(int)parameter_s.elementAt(i)[a]:parameter_s.elementAt(i)[a];
					  aline_nmi[1][a]=this.xydata_nmi.elementAt(i)[a][k];
				  }
				  xydata_ac.put(keys[k], aline_ac);
				  xydata_nmi.put(keys[k], aline_nmi);
				  //System.out.println("k"+k);
				 // System.out.println("keys[k]"+keys[k]);
				  //System.out.println(xydata_ac.size());
				 
			  }
			 
			  String alpha = i==0?"":"-alpha-"+this.baseAlpha;
			  String ensize =  i==1?"":"-ensize-"+this.baseensize;
			  String per = i==2?"":"-per-"+this.baseper;
			  String title = this.dataname+"-ensize-"+ensize+" per-"+per;
			  String[] title_ac={title,"AC",ps[i]};
			  String[] title_nmi={title,"NMI",ps[i]};
			
			  //xFigurePart1.showFigure(title_ac, xydata_ac);
			  //xFigurePart1.showFigure(title_nmi, xydata_nmi);
			  alpha = i==0?"alpha":"";
			  ensize = i==1?"ensize":"";
			  per = i==2?"per":"";
			  String filetitle_ac = this.dataname+"-"+alpha+ensize+per+"-AC";
			  String filetitle_nmi = this.dataname+"-"+alpha+ensize+per+"-NMI";
			  xFigurePart1.saveFigure(title_ac, this.exppathjpg+filetitle_ac, xydata_ac);
			  xFigurePart1.saveFigure(title_nmi, this.exppathjpg+filetitle_nmi, xydata_nmi);
			  String[] title_ac_eps={this.dataname,"AC",ps[i]};
			  String[] title_nmi_eps={this.dataname,"NMI",ps[i]};
			 
			  xFigurePart1.saveFigureAsEPS(title_ac_eps, this.exppatheps+filetitle_ac, xydata_ac);
			  xFigurePart1.saveFigureAsEPS(title_nmi_eps, this.exppatheps+filetitle_nmi, xydata_nmi);
		  }
	}
 
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		 
		//Ttest.Run();
	}
	public  Vector<double[][]> getComRes() {
		// TODO Auto-generated method stub
		Vector<double[][]> ans = new Vector<double[][]>();
		double[][][] res = new double [this.keys.length][2][this.runtimes];
		  for(int i=0;i<res.length-1;++i){
			  for(int j=0;j<this.runtimes;++j){
				  res[i][0][j]=this.base_ac.elementAt(this.bestValue_ind)[i][j];
				  res[i][1][j]=this.base_nmi.elementAt(this.bestValue_ind)[i][j];
			  }
			  ans.add(res[i]);
		  }
		  for(int j=0;j<this.runtimes;++j){
			  res[this.keys.length-1][0][j]=this.kmodes_ac_values[j];
			  res[this.keys.length-1][1][j]=this.kmodes_nmi_values[j];
		  }
		 ans.add(res[this.keys.length-1]);
		return ans;
	}
}
