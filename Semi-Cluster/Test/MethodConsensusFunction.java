package Test;

import java.util.Vector;

import Part1.finalClustererResult;
import Part1.oneClustererResult;
import Test.ConMatrix.LinkType;

import weka.core.Instances;

public abstract   class MethodConsensusFunction {

	public LinkType lt=null;
	public MethodConsensusFunction(LinkType lt){
		this.lt = lt;
	}
	public abstract  finalClustererResult getFinalCluster(Vector<oneClustererResult> clusterers, int k) throws Exception;
	public abstract  finalClustererResult getFinalCluster(Vector<oneClustererResult> clusterers, double[][] LR, int k) throws Exception;
	public abstract finalClustererResult getFinalCluster(
			Vector<oneClustererResult> clusterers, double[][] LRx, LinkType lt, Instances data,int k)
			throws Exception ;

}
