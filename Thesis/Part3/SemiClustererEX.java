package Part3;

import java.util.Vector;

import myUtils.ClusterUtils;

import org.apache.commons.math.stat.descriptive.moment.Mean;
import org.apache.commons.math.stat.descriptive.moment.StandardDeviation;

import Part1.ClusterEnsemble;
import Part1.MCF_s_w_voting;
import Part1.MCF_w_voting;
import Part1.MethodGenerateClustering;
import Part2.DistanceWithLabel;
import Part2.HierarchicalClusterer;
import Part2.MP_HierCluster;
import Part2.SP_equivalence;
import Part2.SplitMergeCluster;
import Part2.MP_HierCluster.LinkType;

 
import weka.core.Instances;
import weka.core.SelectedTag;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Remove;

public class SemiClustererEX {
	public Instances classdata = null;
	public Instances clusterdataFull = null;
	public Instances clusterdataSel1 = null;
	public Instances clusterdataSel2 = null;
	public int[] seletatts1 = null;
	public int[] seletatts2 = null;
	public double[][] res_ac = null;
	public double[][] res_nmi = null;
	public String algname = null;
	public int algIND = -1;
	public int classNum = -1;
	public double seleTime1 = 0;
	public double seleTime2 = 0;
	public double fulltime = 0;
	public boolean[] labeltag = null;
	public int runtime = -1;
	public int[] classes = null;
	public SemiClustererEX(int algInd, String cluName, int runtime,  int classNum, boolean[] labeltag){
		
		this.algIND = algInd;
		this.classNum = classNum;
		res_ac = new double[3][2];
		res_nmi = new double[3][2];
		this.runtime = runtime;
		this.algname = cluName;
		this.labeltag = labeltag;
		
		
	}
	public Vector<double[][]> getRes(Instances data, Instances datacluster, int[] seletatts1, int[] seletatts2) throws Exception{
		this.classdata = new Instances(data);
		this.clusterdataFull = new Instances(datacluster);
	 
		this.seletatts1 = new int[seletatts1.length-1];
		for(int i=0;i<seletatts1.length-1;++i)
			this.seletatts1[i]=seletatts1[i];
		int [] reAttr = Utils.seletatt2removeAtt(this.seletatts1);
		Remove m_removeFilter = new Remove();
		m_removeFilter.setAttributeIndicesArray(reAttr);
		m_removeFilter.setInvertSelection(false);
    	m_removeFilter.setInputFormat(datacluster);   
    	this.clusterdataSel1 = Filter.useFilter(datacluster, m_removeFilter);
    	
    	this.seletatts2 = new int[seletatts2.length-1];
		for(int i=0;i<seletatts2.length-1;++i)
			this.seletatts2[i]=seletatts2[i];
		int [] reAttr2 = Utils.seletatt2removeAtt(this.seletatts2);
		Remove m_removeFilter2 = new Remove();
		m_removeFilter2.setAttributeIndicesArray(reAttr2);
		m_removeFilter2.setInvertSelection(false);
    	m_removeFilter2.setInputFormat(datacluster);   
    	this.clusterdataSel2 = Filter.useFilter(datacluster, m_removeFilter2);
		
    	
    	  double[] tmpdclass = data.attributeToDoubleArray(data.numAttributes()-1);
			this.classes = new int[data.numInstances()];
			for(int i=0;i<tmpdclass.length;++i)
				this.classes[i]=(int)tmpdclass[i];
		return this.getEvalution();
	}
	
	private Vector<double[][]>  getEvalution() throws Exception {
		// TODO Auto-generated method stub
		Vector<double[][]> ans = new Vector<double[][]> ();
		switch(this.algIND){
		case 0:{
			double[] acSel1 = new double[this.runtime];
			double[] acSel2 = new double[this.runtime];
			double[] acFull = new double[this.runtime];
			double[] nmiSel1 = new double[this.runtime];
			double[] nmiSel2 = new double[this.runtime];
			double[] nmiFull = new double[this.runtime];
			for(int i=0;i<this.runtime;++i){
				long time_start = Utils.getCurrenttime();
				
				MethodGenerateClustering mc1 = new MethodGenerateClustering(this.classdata,this.clusterdataSel1,this.labeltag,15,i,0,0.6);
				ClusterEnsemble ce1 =new ClusterEnsemble(mc1,new MCF_w_voting(),true);
				 
 
				this.seleTime1 += (Utils.getCurrenttime() - time_start)/(double)1000;
				time_start = Utils.getCurrenttime();
				
				MethodGenerateClustering mc2 = new MethodGenerateClustering(this.classdata,this.clusterdataSel2,this.labeltag,15,i,0,0.6);
				ClusterEnsemble ce2 =new ClusterEnsemble(mc2,new MCF_w_voting(),true);
				
				this.seleTime2 += (Utils.getCurrenttime() - time_start)/(double)1000;
				time_start = Utils.getCurrenttime();
				
				MethodGenerateClustering mc3 = new MethodGenerateClustering(this.classdata,this.clusterdataFull,this.labeltag,15,i,0,0.6);
				ClusterEnsemble ce3 =new ClusterEnsemble(mc3,new MCF_w_voting(),true);
				
				this.fulltime += (Utils.getCurrenttime() - time_start)/(double)1000;
				
				acSel1[i]= ce1.fr.ac;
				acSel2[i]= ce2.fr.ac;
				acFull[i]= ce3.fr.ac;
				nmiSel1[i]= ce1.fr.nmi;
				nmiSel2[i]= ce2.fr.nmi;
				nmiFull[i]= ce3.fr.nmi;
			}
			
			Mean mean = new Mean(); // 算术平均值
			//Variance variance = new Variance(); // 方差
			StandardDeviation sd = new StandardDeviation();// 标准方差
			//计算均值方差写入res[0] res[1]中
		 
			this.res_ac[0][0] = mean.evaluate(acSel1);
			this.res_ac[0][1] = sd.evaluate(acSel1);
			this.res_ac[1][0] = mean.evaluate(acSel2);
			this.res_ac[1][1] = sd.evaluate(acSel2);
			this.res_ac[2][0] = mean.evaluate(acFull);
			this.res_ac[2][1] = sd.evaluate(acFull);
			this.res_nmi[0][0] = mean.evaluate(nmiSel1);
			this.res_nmi[0][1] = sd.evaluate(nmiSel1);
			this.res_nmi[1][0] = mean.evaluate(nmiSel2);
			this.res_nmi[1][1] = sd.evaluate(nmiSel2);
			this.res_nmi[2][0] = mean.evaluate(nmiFull);
			this.res_nmi[2][1] = sd.evaluate(nmiFull);

			break;
		}
		case 1:{
			
	 
			
			double[] acSel1 = new double[this.runtime];
			double[] acSel2 = new double[this.runtime];
			double[] acFull = new double[this.runtime];
			double[] nmiSel1 = new double[this.runtime];
			double[] nmiSel2 = new double[this.runtime];
			double[] nmiFull = new double[this.runtime];
			for(int i=0;i<this.runtime;++i){
				long time_start = Utils.getCurrenttime();
				
				MethodGenerateClustering mc1 = new MethodGenerateClustering(this.classdata,this.clusterdataSel1,this.labeltag,15,i,0,0.6);
				ClusterEnsemble ce1 =new ClusterEnsemble(mc1,new MCF_s_w_voting(),true);
				 
 
				this.seleTime1 += (Utils.getCurrenttime() - time_start)/(double)1000;
				time_start = Utils.getCurrenttime();
				
				MethodGenerateClustering mc2 = new MethodGenerateClustering(this.classdata,this.clusterdataSel2,this.labeltag,15,i,0,0.6);
				ClusterEnsemble ce2 =new ClusterEnsemble(mc2,new MCF_s_w_voting(),true);
				
				this.seleTime2 += (Utils.getCurrenttime() - time_start)/(double)1000;
				time_start = Utils.getCurrenttime();
				
				MethodGenerateClustering mc3 = new MethodGenerateClustering(this.classdata,this.clusterdataFull,this.labeltag,15,i,0,0.6);
				ClusterEnsemble ce3 =new ClusterEnsemble(mc3,new MCF_s_w_voting(),true);
				
				this.fulltime += (Utils.getCurrenttime() - time_start)/(double)1000;
				
				acSel1[i]= ce1.fr.ac;
				acSel2[i]= ce2.fr.ac;
				acFull[i]= ce3.fr.ac;
				nmiSel1[i]= ce1.fr.nmi;
				nmiSel2[i]= ce2.fr.nmi;
				nmiFull[i]= ce3.fr.nmi;
			}
			
			Mean mean = new Mean(); // 算术平均值
			//Variance variance = new Variance(); // 方差
			StandardDeviation sd = new StandardDeviation();// 标准方差
			//计算均值方差写入res[0] res[1]中
		 
			this.res_ac[0][0] = mean.evaluate(acSel1);
			this.res_ac[0][1] = sd.evaluate(acSel1);
			this.res_ac[1][0] = mean.evaluate(acSel2);
			this.res_ac[1][1] = sd.evaluate(acSel2);
			this.res_ac[2][0] = mean.evaluate(acFull);
			this.res_ac[2][1] = sd.evaluate(acFull);
			this.res_nmi[0][0] = mean.evaluate(nmiSel1);
			this.res_nmi[0][1] = sd.evaluate(nmiSel1);
			this.res_nmi[1][0] = mean.evaluate(nmiSel2);
			this.res_nmi[1][1] = sd.evaluate(nmiSel2);
			this.res_nmi[2][0] = mean.evaluate(nmiFull);
			this.res_nmi[2][1] = sd.evaluate(nmiFull);

			break;
			
			
		}
		case 2:{
			//this.clu = new Cobweb();
		 
			double acSel1 = -1;
			double acSel2 = -1;
			double acFull = -1;
			double nmiSel1 = -1;
			double nmiSel2 = -1;
			double nmiFull = -1;
		
				long time_start = Utils.getCurrenttime();
				
				 SP_equivalence sp1 = new SP_equivalence(this.classdata,this.clusterdataSel1,this.labeltag);
				 MP_HierCluster mp1 = new MP_HierCluster(this.classdata,this.clusterdataSel1,this.labeltag,new DistanceWithLabel(this.labeltag,classes),LinkType.AVERAGE);
				 SplitMergeCluster smc1 = new SplitMergeCluster(sp1,mp1,"AVERAGE");
				 
 
				this.seleTime1 += (Utils.getCurrenttime() - time_start)/(double)1000;
				time_start = Utils.getCurrenttime();
				
				 SP_equivalence sp2 = new SP_equivalence(this.classdata,this.clusterdataSel2,this.labeltag);
				 MP_HierCluster mp2 = new MP_HierCluster(this.classdata,this.clusterdataSel2,this.labeltag,new DistanceWithLabel(this.labeltag,classes),LinkType.AVERAGE);
				 SplitMergeCluster smc2 = new SplitMergeCluster(sp2,mp2,"AVERAGE");
				
				this.seleTime2 += (Utils.getCurrenttime() - time_start)/(double)1000;
				time_start = Utils.getCurrenttime();
				
				 SP_equivalence sp3 = new SP_equivalence(this.classdata,this.clusterdataFull,this.labeltag);
				 MP_HierCluster mp3 = new MP_HierCluster(this.classdata,this.clusterdataFull,this.labeltag,new DistanceWithLabel(this.labeltag,classes),LinkType.AVERAGE);
				 SplitMergeCluster smc3 = new SplitMergeCluster(sp3,mp3,"AVERAGE");
				
				this.fulltime += (Utils.getCurrenttime() - time_start)/(double)1000;
				
				acSel1= smc1.getEvaluationRes().ac;
				acSel2= smc2.getEvaluationRes().ac;
				acFull=  smc3.getEvaluationRes().ac;
				nmiSel1= smc1.getEvaluationRes().nmi;
				nmiSel2= smc2.getEvaluationRes().nmi;
				nmiFull= smc3.getEvaluationRes().nmi;
			
			
			Mean mean = new Mean(); // 算术平均值
			//Variance variance = new Variance(); // 方差
			StandardDeviation sd = new StandardDeviation();// 标准方差
			//计算均值方差写入res[0] res[1]中
		 
			this.res_ac[0][0] = acSel1;			 
			this.res_ac[1][0] = acSel2;			 
			this.res_ac[2][0] = acFull;			 
			this.res_nmi[0][0] = nmiSel1;		 
			this.res_nmi[1][0] = nmiSel2;
			this.res_nmi[2][0] = nmiFull;
		 

			break;
		}
		case 3:{
		
	 
			 
			double acSel1 = -1;
			double acSel2 = -1;
			double acFull = -1;
			double nmiSel1 = -1;
			double nmiSel2 = -1;
			double nmiFull = -1;
		
				long time_start = Utils.getCurrenttime();
				
				 SP_equivalence sp1 = new SP_equivalence(this.classdata,this.clusterdataSel1,this.labeltag);
				 MP_HierCluster mp1 = new MP_HierCluster(this.classdata,this.clusterdataSel1,this.labeltag,new DistanceWithLabel(this.labeltag,classes),LinkType.MEAN);
				 SplitMergeCluster smc1 = new SplitMergeCluster(sp1,mp1,"AVERAGE");
				 
 
				this.seleTime1 += (Utils.getCurrenttime() - time_start)/(double)1000;
				time_start = Utils.getCurrenttime();
				
				 SP_equivalence sp2 = new SP_equivalence(this.classdata,this.clusterdataSel2,this.labeltag);
				 MP_HierCluster mp2 = new MP_HierCluster(this.classdata,this.clusterdataSel2,this.labeltag,new DistanceWithLabel(this.labeltag,classes),LinkType.MEAN);
				 SplitMergeCluster smc2 = new SplitMergeCluster(sp2,mp2,"AVERAGE");
				
				this.seleTime2 += (Utils.getCurrenttime() - time_start)/(double)1000;
				time_start = Utils.getCurrenttime();
				
				 SP_equivalence sp3 = new SP_equivalence(this.classdata,this.clusterdataFull,this.labeltag);
				 MP_HierCluster mp3 = new MP_HierCluster(this.classdata,this.clusterdataFull,this.labeltag,new DistanceWithLabel(this.labeltag,classes),LinkType.MEAN);
				 SplitMergeCluster smc3 = new SplitMergeCluster(sp3,mp3,"AVERAGE");
				
				this.fulltime += (Utils.getCurrenttime() - time_start)/(double)1000;
				
				acSel1= smc1.getEvaluationRes().ac;
				acSel2= smc2.getEvaluationRes().ac;
				acFull=  smc3.getEvaluationRes().ac;
				nmiSel1= smc1.getEvaluationRes().nmi;
				nmiSel2= smc2.getEvaluationRes().nmi;
				nmiFull= smc3.getEvaluationRes().nmi;
			
			
			Mean mean = new Mean(); // 算术平均值
			//Variance variance = new Variance(); // 方差
			StandardDeviation sd = new StandardDeviation();// 标准方差
			//计算均值方差写入res[0] res[1]中
		 
			this.res_ac[0][0] = acSel1;			 
			this.res_ac[1][0] = acSel2;			 
			this.res_ac[2][0] = acFull;			 
			this.res_nmi[0][0] = nmiSel1;		 
			this.res_nmi[1][0] = nmiSel2;
			this.res_nmi[2][0] = nmiFull;
		 

			break;
			 
		

			}
		default:break;
		}
		
		this.seleTime1 = this.seleTime1/(double)this.runtime;
		this.seleTime2 = this.seleTime2/(double)this.runtime;
		this.fulltime = fulltime/(double)this.runtime;
		ans.add(this.res_ac);
		ans.add(this.res_nmi);
		return ans;
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
