package Part1;

 
import java.util.Arrays;
import java.util.Random;
import java.util.Vector;

import myUtils.ClusterUtils;


import weka.clusterers.EM;
import weka.clusterers.SimpleKMeans;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Remove;

public class MethodGenerateClustering {

	public Vector<int[]> clustersRes = null;
	public Instances data = null;
	public Instances datacluster = null;
	public boolean[] labeledIndex = null;
	public Random Rnd = null;
	public int cluster_baseind = -1;
	public Vector<oneClustererResult> res = null;
	public double alpha = -1;
	public double[] SquaredError = null;
	public int[] classes = null;
	public double[][] labeledRelation = null;
	public Vector<int[]> getMultiKmodesResults(Instances data,Instances dataforcluster, int ensemblesize) throws Exception{
		Vector<int[]> cls = new Vector<int[]>();
		int k = data.numClasses();
		
		for(int i=0;i<ensemblesize;++i){
			
			int tmpk = Rnd.nextInt(ensemblesize*k);
			tmpk=tmpk<=k?(tmpk+k):tmpk;
			SimpleKMeans km = new SimpleKMeans(); 
			km.setMaxIterations(100);
			km.setNumClusters(tmpk);
			km.setDontReplaceMissingValues(true);
			km.setSeed(Rnd.nextInt());
			km.setPreserveInstancesOrder(true);
			km.buildClusterer(dataforcluster);
			SquaredError[i] = km.getSquaredError();
			
			
			
			
			/*EM em = new EM();
			em.setMaxIterations(100);
			em.setNumClusters(k);
			//em.setSeed(Rnd.nextInt());
			em.buildClusterer(dataforcluster);
			int[] res2 = new int[dataforcluster.numInstances()];
			for(int r=0;r<dataforcluster.numInstances();++r){
				res2[r]=em.clusterInstance(dataforcluster.instance(r));
			}*/
	        
			//System.out.println(Arrays.toString(km.getAssignments()));
			cls.add(km.getAssignments());
			//cls.add(res2);

		}
		return cls;

	}
	private Vector<int[]> getMultiKmodesResultswithRandomSelectFeature(
			Instances data, Instances dataforcluster, int ensemblesize) throws Exception {
		// TODO Auto-generated method stub
		Vector<int[]> cls = new Vector<int[]>();

		for(int i=0;i<ensemblesize;++i){
			SimpleKMeans km = new SimpleKMeans(); 
			km.setMaxIterations(100);
			km.setNumClusters(data.numClasses());
			km.setDontReplaceMissingValues(true);
			km.setSeed(Rnd.nextInt());
			Instances newData = getNewRandomData(dataforcluster,Rnd.nextInt());
			km.setPreserveInstancesOrder(true);
			km.buildClusterer(newData);
			SquaredError[i] = km.getSquaredError();
			cls.add(km.getAssignments());

		}
		return cls;
	}
	
	public static double[][] labeled2relation(boolean[] labeled,Instances data) {
		// TODO Auto-generated method stub
		double[][] res = new double[labeled.length][labeled.length];
		for(int i=0;i<labeled.length;++i){
			for(int j=i+1;j<labeled.length;++j){
				if(labeled[i]&&labeled[j]){
					if(data.instance(i).classValue()==data.instance(j).classValue())
						res[i][j]=1;
					else
						res[i][j]=-1;
				}
			}
		}
		return res;
	}
	
	
	public MethodGenerateClustering(Instances data,Instances dataforcluster,  boolean[] labeledIndex, int ensemblesize, int Rnd, int methodind, double alpha) throws Exception
	{
		this.alpha = alpha;
		this.Rnd = new Random(Rnd);
		SquaredError = new double[ensemblesize];
		this.data = data;
		this.datacluster = new Instances(dataforcluster);
		this.labeledIndex = labeledIndex;
		
		this.labeledRelation = labeled2relation(labeledIndex,data).clone();
		double[] tmpdclass = data.attributeToDoubleArray(data.numAttributes()-1);
		classes = new int[data.numInstances()];
		for(int i=0;i<tmpdclass.length;++i)
			classes[i]=(int)tmpdclass[i];

		switch(methodind){
			case 0:{
				this.clustersRes = getMultiKmodesResults(data,dataforcluster,ensemblesize);
				break;
			}
			case 1:{
				this.clustersRes = getMultiKmodesResultswithRandomSelectFeature(data,dataforcluster,ensemblesize);
				break;
			}
			default:break;
		}
		res = getClusterers();
	}
	public static Instances getNewRandomData(Instances data, int rnd) throws Exception{
		 
		Random rd = new Random(rnd);
		boolean[] resB = new boolean[data.numAttributes()];
		for(int r=0;r<data.numAttributes();++r)
			resB[r]=rd.nextBoolean();
		int cnt = 0;
		for (int i=0;i<resB.length-1;++i){
			if(resB[i]){
				cnt++;
			}
		}
		int[] removeind = new int[resB.length-1-cnt];
		int j = 0;
		for(int i=0;i<resB.length-1;++i){
			if(!resB[i]){
				removeind[j++]=i;
			}	
		}
		
		Remove m_removeFilter = new Remove();
		m_removeFilter.setAttributeIndicesArray(removeind);
		m_removeFilter.setInvertSelection(false);
    	m_removeFilter.setInputFormat(data);   
    	Instances newData = Filter.useFilter(data, m_removeFilter);
    	return newData;
	}
	

	public Vector<oneClustererResult> getClusterers() {
		// TODO Auto-generated method stub
		
		
		double[][] nmi = new double [clustersRes.size()][clustersRes.size()];
		for(int i=0;i<clustersRes.size();++i){
			if(!myUtils.ClusterUtils.isAllSame(clustersRes.elementAt(i)))
				for(int j=i+1;j<clustersRes.size();++j){
						nmi[i][j]=ClusterUtils.NMI(clustersRes.elementAt(i), clustersRes.elementAt(j));
						nmi[j][i]=nmi[i][j];
					}

		}
		Vector<oneClustererResult> res = new Vector<oneClustererResult>(); 
		for(int i=0;i<clustersRes.size();++i)
		{
			oneClustererResult aRes = new oneClustererResult();
			aRes.cluster=clustersRes.elementAt(i).clone();
			aRes.semi_weigth = this.getWeight(clustersRes.elementAt(i));
			double sum_nmi = 0;
			for(int j=0;j<clustersRes.size();++j){
				sum_nmi+=nmi[i][j];
			}
			//aRes.nmi_weigth = (double)1/sum_nmi; //1/beta
			aRes.nmi_weigth = sum_nmi; //beta
			aRes.squarederror_weigth = (double)1/this.SquaredError[i];//越小越好
			res.add(aRes);
		}
		NormalizedWeightAndBaseInd(res);
		
		return res;
		
		
	}
	private void NormalizedWeightAndBaseInd(Vector<oneClustererResult> res) {
		// TODO Auto-generated method stub

		double  sw_tmp = 0;
		double  nmi_tmp = 0;
		double  sq_tmp = 0;
		double avg = (double)1/(double)clustersRes.size();
		double bestValue = -1;
		for(int i=0;i<clustersRes.size();++i)
		{
			sw_tmp+=res.elementAt(i).semi_weigth;
			nmi_tmp+=res.elementAt(i).nmi_weigth;
			sq_tmp+=res.elementAt(i).squarederror_weigth;
			
		}
		for(int i=0;i<clustersRes.size();++i)
		{
			res.elementAt(i).semi_weigth =sw_tmp==0?avg: res.elementAt(i).semi_weigth/sw_tmp;
			res.elementAt(i).nmi_weigth =nmi_tmp==0?avg: res.elementAt(i).nmi_weigth/nmi_tmp; 
			res.elementAt(i).squarederror_weigth =sq_tmp==0?avg: res.elementAt(i).squarederror_weigth/sq_tmp;
		}
		
		for(int i=0;i<clustersRes.size();++i)
		{
			double part1 = res.elementAt(i).semi_weigth;
			double part2 = res.elementAt(i).nmi_weigth;
			//double part2 = res.elementAt(i).squarederror_weigth;
			//double weg = this.alpha*part1 + (1-this.alpha)*part2;
			//System.out.println(part2);
			double weg = part1;
			res.elementAt(i).f_weigth = weg;
			//double tmp = 0.6*part1 + 0.4*part2;
			if(weg>bestValue){
				this.cluster_baseind = i;
				bestValue = weg;
			}
		}
		
		///System.out.println(bestValue );
		

	}
	private double getWeight(int[] elementAt) {
		// TODO Auto-generated method stub
		if(myUtils.ClusterUtils.isAllSame(elementAt))
			return 0;
		return ClusterUtils.SemiClusterWeight(this.classes, elementAt, this.labeledIndex);
	}

}
