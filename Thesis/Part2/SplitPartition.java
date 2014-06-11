package Part2;

import java.util.Vector;

import myUtils.ClusterUtils;

import weka.core.Instances;

public abstract class SplitPartition {
	public boolean[] labeled = null;
	public Instances data_label = null;
	public Instances data_cluster = null;
	public int[] patition = null;
	public int[] labels = null;
	
	public SplitPartition(Instances data, Instances data_cluster, boolean[] labeled){
		this.data_label = data;
		double[] tmpdclass = data.attributeToDoubleArray(data.numAttributes()-1);
		labels = new int[data.numInstances()];
		for(int i=0;i<tmpdclass.length;++i)
			labels[i]=(int)tmpdclass[i];
		this.data_cluster = data_cluster;
		this.labeled = labeled;
	}
	public abstract int[] generatePatition()throws Exception;
	public Vector<Integer> [] getPatition() throws Exception{
		this.patition = this.generatePatition();
		int M = ClusterUtils.FindKcluster(this.patition);
		int N = this.patition.length;
		Vector<Integer> [] nClusterID = new Vector[M];
	    for (int i = 0; i < M; i++) {
	    	nClusterID[i] = new Vector<Integer>();
	    	for(int j=0;j<N;++j){
	    		if(this.patition[j]==i)
	    			 nClusterID[i].add(j);
	    	}
	      
	     
	    }
		return nClusterID;
	}
	public int[] splitpartitionWithLabel(int[] partition){
		int[] newlabels = this.labels.clone();
		for(int i=0;i<newlabels.length;++i){
			if(!this.labeled[i])
				newlabels[i]=-1;
		}
		return getEquivalenceClass(partition,newlabels);
	}
	public int[] getEquivalenceClass(int[] partition, int[] newlabels){

		int U = partition.length;
		//求等价类,先求等价矩阵
		int[][] X_matrix = new int[U][U];
		int N = 0; //统计等价类的个数
		for(int i=0;i<U;++i){
			if(X_matrix[i][i]==0){
				N++;
				X_matrix[i][i] = 1;
				for(int j=i+1;j<U;++j){						 
					if (partition[i]==partition[j]&&newlabels[i]==newlabels[j]){
						X_matrix[i][j] = 1;
						X_matrix[j][j] = 2;
					}
				}
			}
		}
 
		int [] p = new int[U];
		int n=0;
		for (int i=0;i<U;++i){
			if(X_matrix[i][i]==1){
				for (int j=0;j<U;++j){
					if(X_matrix[i][j]==1)
					   p[j]=n;
				}
				n++;
			}
		}
		return p;
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
