package Part2;

import java.util.Vector;

import weka.core.Instances;
import weka.core.NormalizableDistance;

public abstract class MergePartition {

	
	public boolean[] labeled = null;
	public Instances data_label = null;
	public Instances data_cluster = null;
	public int[] patition = null;
	public int[] labels = null;
	public DistanceWithLabel dis = null;
	
	public MergePartition(Instances data, Instances data_cluster, boolean[] labeled, DistanceWithLabel dis){
		this.data_label = data;
		double[] tmpdclass = data.attributeToDoubleArray(data.numAttributes()-1);
		labels = new int[data.numInstances()];
		for(int i=0;i<tmpdclass.length;++i)
			labels[i]=(int)tmpdclass[i];
		this.data_cluster = data_cluster;
		this.labeled = labeled;
		this.dis = dis;
	}
	public abstract int[] getFinalCluster(Vector<Integer>[] partitions)throws Exception;
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
