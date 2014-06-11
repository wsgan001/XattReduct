package Part2;

import myUtils.ClusterUtils;

public class ClusterEvaluationRes {
	public double ac = -1;
	public double nmi = -1;
	public int[] clusters = null;
	public int[] classes = null;
	public ClusterEvaluationRes(int[] classes, int[] clusters){
		this.classes = classes;
		this.clusters = clusters;
		this.Evalution();
	}
	public void Evalution(){
		this.ac = ClusterUtils.getAC_ByBipartiteGraph(this.classes, this.clusters);
		this.nmi = ClusterUtils.NMI(this.classes, this.clusters);
	}
}
