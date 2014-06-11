package Part2;

import java.util.Vector;

import weka.core.Instances;
import weka.core.NormalizableDistance;
import weka.core.SelectedTag;
import weka.core.Tag;

public class MP_HierCluster extends MergePartition {

	public MP_HierCluster(Instances data, Instances data_cluster,
			boolean[] labeled, DistanceWithLabel dis, LinkType lt) {
		super(data, data_cluster, labeled, dis);
		this.lt = lt;
		// TODO Auto-generated constructor stub
	}
	public static enum LinkType{SINGLE,COMPLETE,AVERAGE,MEAN,CENTROID,WARD,ADJCOMLPETE,NEIGHBOR_JOINING};
	public static Tag[] TAGS_LINK_TYPE = {
		    new Tag(0, "SINGLE"),
		    new Tag(1, "COMPLETE"),
		    new Tag(2, "AVERAGE"),
		    new Tag(3, "MEAN"),
		    new Tag(4, "CENTROID"),
		    new Tag(5, "WARD"),
		    new Tag(6,"ADJCOMLPETE")
		 
	};
	public LinkType lt = null;
	
	@Override
	public int[] getFinalCluster(Vector<Integer>[] partitions) throws Exception {
		// TODO Auto-generated method stub
		HierarchicalClustererEx hc = new HierarchicalClustererEx(dis);
		 
		hc.setInitCluster(partitions);
		hc.setNumClusters(this.data_label.numClasses());
		Instances newdata = new Instances(this.data_cluster);
		int  tmp = this.lt.ordinal();
		SelectedTag st = new SelectedTag(tmp, TAGS_LINK_TYPE);
		hc.setLinkType(st);
		hc.buildClusterer(newdata);
	 
	
		 
		return hc.m_nClusterNr;
		//return res;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
