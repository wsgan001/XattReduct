package Part2;

import java.util.Vector;

import weka.core.Instance;
import weka.core.Instances;
import weka.core.NormalizableDistance;
import weka.core.RevisionUtils;

public class DistanceWithLabel extends NormalizableDistance {
	public boolean[] labeltag = null;
	public int[] labels = null;
	public double alpha = 0.6;
 	 
	public DistanceWithLabel(boolean[] labeltag,int[] labels) {
		    super();
		    this.labeltag = labeltag;
		    this.labels =labels;
	
	}
	
	public DistanceWithLabel(Instances data) {
	    super(data);
	}
	
	@Override
	public String getRevision() {
		// TODO Auto-generated method stub
		 return RevisionUtils.extract("$Revision: 0.01 $");
	}
	
	 
	
	@Override
	public String globalInfo() {
		// TODO Auto-generated method stub
		return "";
	}
	
	@Override
	protected double updateDistance(double currDist, double diff) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	
	
	  /**
	   * Calculates the distance between two instances.
	   * 
	   * @param first 	the first instance
	   * @param second 	the second instance
	   * @return 		the distance between the two given instances
	   */
	  public double distance(Instance first, Instance second) {
	    double result=0;
	    int N = first.numAttributes();
	    for(int i=0;i<N;++i){
	    	if(Double.isNaN(first.value(i))||Double.isNaN(second.value(i))||first.value(i)==second.value(i))
	    		result=result+1;
	    }
	    
	    return  result/(double)first.numAttributes();
	  }
	  
	  public double distance(Instance first, Instance second, int i, int j) {
		    int N = first.numAttributes();
		    int dis = 0;
	    	for(int x=0;x<N;++x){
				    if(Double.isNaN(first.value(x))||Double.isNaN(second.value(x))||first.value(x)==second.value(x))
				    	dis++;
				    }
		    double result=0;

		    if(this.labeltag[i]&&this.labeltag[j]){
		    	if(this.labels[i]==this.labels[j])
		    		result = -(double)first.numAttributes();
		    	else
		    		result = (double)first.numAttributes();
		    }
		    else{

		    	 result =  dis;
		    }
		   
		    return  result;
		  }

	public double Semi_distance(double[][] fDistance, Vector<Integer> cluster1,
			Vector<Integer> cluster2) {
		// TODO Auto-generated method stub
		 int sim = 0;
		 int dissim = 0;
		 for (int i = 0; i < cluster1.size(); i++) {
		        int i1 = cluster1.elementAt(i);
		        for (int j = 0; j < cluster2.size(); j++) {
		          int i2 = cluster2.elementAt(j);
		          if(this.labeltag[i1]&&this.labeltag[i2]){
		        	  if(this.labels[i2]==this.labels[i2])
		        		  sim++;
		        	  else
		        		  dissim++;
		          }
		          
		        }
		 }
		 if(dissim==0)
			 return 1;
		return (double)sim/(double)dissim;
	}
	
	/**
	 * @param args
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */

}
