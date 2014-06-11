package Part2;

import java.util.Vector;

import weka.core.Instances;

public class SplitMergeCluster {

	public SplitPartition sp = null;
	public MergePartition mp = null;
	public boolean[] labeled = null;
	public Instances data_label = null;
	public Instances data_cluster = null;
	public ClusterEvaluationRes res = null;
	public String algname = null;
	public SplitMergeCluster(SplitPartition sp, MergePartition mp, String algname) throws Exception{
		this.algname = algname;
		Vector<Integer>[] partitions = sp.getPatition();
		int[] classes = sp.labels;
		int[] clusters = mp.getFinalCluster(partitions);
		this.res = new ClusterEvaluationRes(classes,clusters);
	}
	public ClusterEvaluationRes getEvaluationRes(){
		return this.res;
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
