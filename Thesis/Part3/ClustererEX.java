package Part3;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Random;
import java.util.Vector;

import org.apache.commons.math.stat.descriptive.moment.Mean;
import org.apache.commons.math.stat.descriptive.moment.StandardDeviation;

import myUtils.ClusterUtils;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.clusterers.CLOPE;
import weka.clusterers.ClusterEvaluation;
import weka.clusterers.Clusterer;
import weka.clusterers.Cobweb;
import weka.clusterers.EM;
import weka.clusterers.FarthestFirst;
import weka.clusterers.OPTICS;
import weka.clusterers.SimpleKMeans;
import weka.core.FastVector;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Remove;

public class ClustererEX {

	public int runtime = -1;

 
	public Instances classdata = null;
	public Instances clusterdataFull = null;
	public Instances clusterdataSel = null;
	public int[] seletatts = null;
	public double[][] res_ac = null;
	public double[][] res_nmi = null;
	public String algname = null;
	public int algIND = -1;
	public int classNum = -1;
	public double seleTime = 0;
	public double fulltime = 0;
	public ClustererEX(int algInd, String cluName, int runtime,  int classNum){
		this.algIND = algInd;
		this.classNum = classNum;
		res_ac = new double[2][2];
		res_nmi = new double[2][2];
		this.runtime = runtime;
		this.algname = cluName;
		 
		
	}
	public Vector<double[][]> getRes(Instances data, Instances datacluster, int[] seletatts) throws Exception{
		this.classdata = new Instances(data);
		this.clusterdataFull = new Instances(datacluster);
	 
		this.seletatts = new int[seletatts.length-1];
		for(int i=0;i<seletatts.length-1;++i)
			this.seletatts[i]=seletatts[i];
		int [] reAttr = Utils.seletatt2removeAtt(this.seletatts);
		Remove m_removeFilter = new Remove();
		m_removeFilter.setAttributeIndicesArray(reAttr);
		m_removeFilter.setInvertSelection(false);
    	m_removeFilter.setInputFormat(datacluster);   
    	this.clusterdataSel = Filter.useFilter(datacluster, m_removeFilter);
		
		return this.getEvalution();
	}
	private Vector<double[][]>  getEvalution() throws Exception {
		// TODO Auto-generated method stub
		Vector<double[][]> ans = new Vector<double[][]> ();
		switch(this.algIND){
		case 0:{
			double[] acSel = new double[this.runtime];
			double[] acFull = new double[this.runtime];
			double[] nmiSel = new double[this.runtime];
			double[] nmiFull = new double[this.runtime];
			for(int i=0;i<this.runtime;++i){
				long time_start = Utils.getCurrenttime();
				SimpleKMeans clusel = new SimpleKMeans();
				clusel.setNumClusters(this.classNum);
				clusel.setMaxIterations(100);
				clusel.setSeed(i);
				clusel.setPreserveInstancesOrder(true);
				clusel.buildClusterer(this.clusterdataSel);
				int [] clustersResSel = clusel.getAssignments();
				this.seleTime += (Utils.getCurrenttime() - time_start)/(double)1000;
				time_start = Utils.getCurrenttime();
				SimpleKMeans clufull = new SimpleKMeans();
				clufull.setNumClusters(this.classNum);
				clufull.setMaxIterations(100);
				clufull.setSeed(i);
				clufull.setPreserveInstancesOrder(true);
				clufull.buildClusterer(clusterdataFull);
				int [] clustersResFull = clufull.getAssignments();
				this.fulltime += (Utils.getCurrenttime() - time_start)/(double)1000;
				
				acSel[i]=ClusterUtils.CA(classdata, clustersResSel);
				acFull[i]=ClusterUtils.CA(classdata, clustersResFull);
				nmiSel[i]=ClusterUtils.NMI(classdata, clustersResSel);
				nmiFull[i]=ClusterUtils.NMI(classdata, clustersResFull);
			}
			
			Mean mean = new Mean(); // 算术平均值
			//Variance variance = new Variance(); // 方差
			StandardDeviation sd = new StandardDeviation();// 标准方差
			//计算均值方差写入res[0] res[1]中
		 
			this.res_ac[0][0] = mean.evaluate(acSel);
			this.res_ac[0][1] = sd.evaluate(acSel);
			this.res_ac[1][0] = mean.evaluate(acFull);
			this.res_ac[1][1] = sd.evaluate(acFull);
			this.res_nmi[0][0] = mean.evaluate(nmiSel);
			this.res_nmi[0][1] = sd.evaluate(nmiSel);
			this.res_nmi[1][0] = mean.evaluate(nmiFull);
			this.res_nmi[1][1] = sd.evaluate(nmiFull);

			break;
		}
		case 1:{
			//this.clu = new EM();
			double[] acSel = new double[this.runtime];
			double[] acFull = new double[this.runtime];
			double[] nmiSel = new double[this.runtime];
			double[] nmiFull = new double[this.runtime];
			for(int i=0;i<this.runtime;++i){
				long time_start = Utils.getCurrenttime();	
				EM clusel = new EM();
				clusel.setNumClusters(this.classNum);
				clusel.setMaxIterations(100);
				clusel.setSeed(i);
				clusel.buildClusterer(this.clusterdataSel);			
				int[] clustersResSel = new int[this.clusterdataSel.numInstances()];
				for(int q=0;q<clustersResSel.length;++q){
					clustersResSel[q]= clusel.clusterInstance(this.clusterdataSel.instance(q));	
				 
				}
				this.seleTime += (Utils.getCurrenttime() - time_start)/(double)1000;
				time_start = Utils.getCurrenttime();
				
				EM clufull = new EM();
				clufull.setNumClusters(this.classNum);
				clufull.setMaxIterations(100);
				clufull.setSeed(i);
				clufull.buildClusterer(clusterdataFull);
				int[] clustersResFull = new int[this.clusterdataSel.numInstances()];
				for(int q=0;q<clustersResSel.length;++q){
					 
					clustersResFull[q]=clufull.clusterInstance(this.clusterdataFull.instance(q));
				}
				this.fulltime += (Utils.getCurrenttime() - time_start)/(double)1000;
				acSel[i]=ClusterUtils.CA(classdata, clustersResSel);
				acFull[i]=ClusterUtils.CA(classdata, clustersResFull);
				nmiSel[i]=ClusterUtils.NMI(classdata, clustersResSel);
				nmiFull[i]=ClusterUtils.NMI(classdata, clustersResFull);
			}
			
			Mean mean = new Mean(); // 算术平均值
			//Variance variance = new Variance(); // 方差
			StandardDeviation sd = new StandardDeviation();// 标准方差
			//计算均值方差写入res[0] res[1]中
		 
			this.res_ac[0][0] = mean.evaluate(acSel);
			this.res_ac[0][1] = sd.evaluate(acSel);
			this.res_ac[1][0] = mean.evaluate(acFull);
			this.res_ac[1][1] = sd.evaluate(acFull);
			this.res_nmi[0][0] = mean.evaluate(nmiSel);
			this.res_nmi[0][1] = sd.evaluate(nmiSel);
			this.res_nmi[1][0] = mean.evaluate(nmiFull);
			this.res_nmi[1][1] = sd.evaluate(nmiFull);

			break;
			
			
		}
		case 2:{
			//this.clu = new Cobweb();
			double[] acSel = new double[this.runtime];
			double[] acFull = new double[this.runtime];
			double[] nmiSel = new double[this.runtime];
			double[] nmiFull = new double[this.runtime];
			for(int r=0;r<this.runtime;++r){
				long time_start = Utils.getCurrenttime();
				Cobweb clusel = new Cobweb();		
				clusel.setSeed(r);	
				clusel.buildClusterer(this.clusterdataSel);
				int[] clustersResSel = new int[this.clusterdataSel.numInstances()];
				int maxSel = -1;
				for(int i=0;i<clustersResSel.length;++i){
					clustersResSel[i]= clusel.clusterInstance(this.clusterdataSel.instance(i));
					if(maxSel<clustersResSel[i])
						maxSel = clustersResSel[i];
				}
				int sepSel = maxSel/classNum;
				for(int i=0;i<clustersResSel.length;++i){
					clustersResSel[i]=clustersResSel[i]/sepSel;
				}
				this.seleTime += (Utils.getCurrenttime() - time_start)/(double)1000;
				time_start = Utils.getCurrenttime();
				Cobweb clufull = new Cobweb();
				clufull.setSeed(r);
				clufull.buildClusterer(this.clusterdataFull);
				int[] clustersResFull = new int[this.clusterdataSel.numInstances()];
				int maxFull = -1;
				for(int i=0;i<clustersResSel.length;++i){					
					clustersResFull[i]=clufull.clusterInstance(this.clusterdataFull.instance(i));
					if(maxFull<clustersResFull[i])
						maxFull = clustersResFull[i];
				}
				int sepFull = maxFull/classNum;
				for(int i=0;i<clustersResSel.length;++i){
					clustersResFull[i]=clustersResFull[i]/sepFull;
				}
				this.fulltime += (Utils.getCurrenttime() - time_start)/(double)1000;
				
				
				acSel[r]=ClusterUtils.CA(classdata, clustersResSel);
				acFull[r]=ClusterUtils.CA(classdata, clustersResFull);
				nmiSel[r]=ClusterUtils.NMI(classdata, clustersResSel);
				nmiFull[r]=ClusterUtils.NMI(classdata, clustersResFull);
				
			}

			Mean mean = new Mean(); // 算术平均值
			//Variance variance = new Variance(); // 方差
			StandardDeviation sd = new StandardDeviation();// 标准方差
			//计算均值方差写入res[0] res[1]中
		 
			this.res_ac[0][0] = mean.evaluate(acSel);
			this.res_ac[0][1] = sd.evaluate(acSel);
			this.res_ac[1][0] = mean.evaluate(acFull);
			this.res_ac[1][1] = sd.evaluate(acFull);
			this.res_nmi[0][0] = mean.evaluate(nmiSel);
			this.res_nmi[0][1] = sd.evaluate(nmiSel);
			this.res_nmi[1][0] = mean.evaluate(nmiFull);
			this.res_nmi[1][1] = sd.evaluate(nmiFull);

			break;
		}
		case 3:{
		
		 
			double[] acSel = new double[this.runtime];
			double[] acFull = new double[this.runtime];
			double[] nmiSel = new double[this.runtime];
			double[] nmiFull = new double[this.runtime];
			for(int i=0;i<this.runtime;++i){
				long time_start = Utils.getCurrenttime();
				FarthestFirst clusel = new FarthestFirst();
				clusel.setNumClusters(this.classNum);
				clusel.setSeed(i);
				clusel.buildClusterer(this.clusterdataSel);
				int[] clustersResSel = new int[this.clusterdataSel.numInstances()];
				for(int q=0;q<clustersResSel.length;++q){
					clustersResSel[q]= clusel.clusterInstance(this.clusterdataSel.instance(q));	
				}
				this.seleTime += (Utils.getCurrenttime() - time_start)/(double)1000;
				time_start = Utils.getCurrenttime();
				FarthestFirst clufull = new FarthestFirst();
				clufull.setNumClusters(this.classNum);	 
				clufull.setSeed(i);
				clufull.buildClusterer(clusterdataFull);
				int[] clustersResFull = new int[this.clusterdataSel.numInstances()];
				for(int q=0;q<clustersResSel.length;++q){
					clustersResFull[q]=clufull.clusterInstance(this.clusterdataFull.instance(q));
				}
				this.fulltime += (Utils.getCurrenttime() - time_start)/(double)1000;
				
				acSel[i]=ClusterUtils.CA(classdata, clustersResSel);
				acFull[i]=ClusterUtils.CA(classdata, clustersResFull);
				nmiSel[i]=ClusterUtils.NMI(classdata, clustersResSel);
				nmiFull[i]=ClusterUtils.NMI(classdata, clustersResFull);
			}
			
			Mean mean = new Mean(); // 算术平均值
			//Variance variance = new Variance(); // 方差
			StandardDeviation sd = new StandardDeviation();// 标准方差
			//计算均值方差写入res[0] res[1]中
		 
			this.res_ac[0][0] = mean.evaluate(acSel);
			this.res_ac[0][1] = sd.evaluate(acSel);
			this.res_ac[1][0] = mean.evaluate(acFull);
			this.res_ac[1][1] = sd.evaluate(acFull);
			this.res_nmi[0][0] = mean.evaluate(nmiSel);
			this.res_nmi[0][1] = sd.evaluate(nmiSel);
			this.res_nmi[1][0] = mean.evaluate(nmiFull);
			this.res_nmi[1][1] = sd.evaluate(nmiFull);

			break;
		

		}
		case 4:{
			long time_start = Utils.getCurrenttime();
			CLOPE clusel = new CLOPE();
			clusel.buildClusterer(this.clusterdataSel);
			int[] clustersResSel = new int[this.clusterdataSel.numInstances()];
			for(int i=0;i<clustersResSel.length;++i){
				clustersResSel[i]= clusel.clusterInstance(clusterdataSel.instance(i));
			}
			this.seleTime += (Utils.getCurrenttime() - time_start)/(double)1000;
			time_start = Utils.getCurrenttime();
			CLOPE clufull = new CLOPE();
			clufull.buildClusterer(this.clusterdataFull);
			int[] clustersResFull = new int[this.clusterdataSel.numInstances()];
			for(int i=0;i<clustersResSel.length;++i){
				clustersResFull[i]=clufull.clusterInstance(this.clusterdataFull.instance(i));
			}
			this.fulltime+= (Utils.getCurrenttime() - time_start)/(double)1000;
			
			this.res_ac[0][0] = ClusterUtils.CA(this.classdata, clustersResSel);
			this.res_ac[1][0] = ClusterUtils.CA(this.classdata, clustersResFull);
			this.res_nmi[0][0] = ClusterUtils.NMI(this.classdata, clustersResSel);
			this.res_nmi[1][0] = ClusterUtils.NMI(this.classdata, clustersResFull);
			//clusel.
			break;
			
			
			
			
			/*//this.clu = new OPTICS();
			OPTICS clusel = new OPTICS();
			OPTICS clufull = new OPTICS();
			clusel.setShowGUI(false);
			clufull.setShowGUI(false);
			clusel.buildClusterer(this.clusterdataSel);
			clufull.buildClusterer(this.clusterdataFull);
			int[] clustersResSel = new int[this.clusterdataSel.numInstances()];
			int[] clustersResFull = new int[this.clusterdataSel.numInstances()];
			for(int i=0;i<clustersResSel.length;++i){
				clustersResSel[i]= clusel.clusterInstance(clusterdataSel.instance(i));
				clustersResFull[i]=clufull.clusterInstance(this.clusterdataFull.instance(i));
			}
			this.res_ac[0][0] = ClusterUtils.CA(this.classdata, clustersResSel);
			this.res_ac[1][0] = ClusterUtils.CA(this.classdata, clustersResFull);
			this.res_nmi[0][0] = ClusterUtils.NMI(this.classdata, clustersResSel);
			this.res_nmi[1][0] = ClusterUtils.NMI(this.classdata, clustersResFull);
			//clusel.
			break;*/
		}
		default:break;
		}
		seleTime = seleTime/(double)this.runtime;
		fulltime = fulltime/(double)this.runtime;
		ans.add(this.res_ac);
		ans.add(this.res_nmi);
		return ans;
	}

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		String fn = "C:/Users/Eric/Desktop/2012秋冬/毕业设计/DataSet/newFS/Audiology.arff";
		Instances data = new Instances(new FileReader(fn));
		FarthestFirst clusel = new FarthestFirst();
		//clusel.setShowGUI(false);
	
	//	int classNum = 3;
		clusel.buildClusterer(data);
		 
		
		 
		int[] res = new int[data.numInstances()];
	 
		for(int i=0;i<res.length;++i){
			 res[i] = clusel.clusterInstance(data.instance(i));
			//System.out.println(f.elementAt(i));
			//if(max<res[i])
			//max = res[i];
		}
		
		System.out.println(Arrays.toString(res));
		
	}

}
