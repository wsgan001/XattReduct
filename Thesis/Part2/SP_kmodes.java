package Part2;

import Part1.finalClustererResult;
import Part1Ex.DissimilarityForKmodes_Weight;
import Part1Ex.SimpleKModes;
import weka.core.Instances;

public class SP_kmodes extends SplitPartition {

	private int rnd = -1;
	private int n = -1;
	public SP_kmodes(Instances data, Instances data_cluster, boolean[] labeled, int rnd, int n) {
		super(data, data_cluster, labeled);
		this.rnd = rnd;
		this.n = n;
		// TODO Auto-generated constructor stub
	}

	@Override
	public int[] generatePatition() throws Exception {
		// TODO Auto-generated method stub
		
		 int k = this.n*this.data_label.numClasses();
		 
		k = k>this.data_label.numInstances()?this.data_label.numInstances():k;
		SimpleKModes km = new SimpleKModes();
		km.setMaxIterations(100);
	    km.setNumClusters(k);
	    km.setSeed(this.rnd);
	    km.setPreserveInstancesOrder(true);
		km.buildClusterer(this.data_cluster);
		return this.splitpartitionWithLabel(km.getAssignments());

	}

}
