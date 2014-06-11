package Test;

import weka.core.Instance;
import weka.core.NormalizableDistance;

public class DistanceBySim extends NormalizableDistance {

	public double[][] dis = null;
	
	public DistanceBySim( double[][] dis ) {
	    super();
	    this.dis = dis;
	

}
	 public double distance(Instance first, Instance second, int i, int j) {
		 return dis[i][j];
	 }
	@Override
	public String getRevision() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String globalInfo() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected double updateDistance(double currDist, double diff) {
		// TODO Auto-generated method stub
		return 0;
	}

}
