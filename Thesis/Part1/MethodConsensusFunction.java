package Part1;

import java.util.Vector;

import Test.ConMatrix.LinkType;

import weka.core.Instances;

public abstract   class MethodConsensusFunction {

	public abstract  finalClustererResult getFinalCluster(Vector<oneClustererResult> clusterers, int k) throws Exception; 

}
