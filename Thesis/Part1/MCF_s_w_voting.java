package Part1;

import java.util.Arrays;
import java.util.Vector;

import cluster.eva;

public class MCF_s_w_voting extends MethodConsensusFunction {


	@Override
	public finalClustererResult getFinalCluster(
			Vector<oneClustererResult> clusterers, int k) {
		// TODO Auto-generated method stub
		 
		double threshold = (double)1/(double)clusterers.size();
		int N = clusterers.elementAt(0).cluster.length;
		int[] res  = new int[N];
		for(int i=0;i<N;++i){
			double[] voting = new double[k];
			for(int c=0;c<clusterers.size();++c){
				double  weg = clusterers.elementAt(c).f_weigth;
				if(weg>=threshold)
				voting[clusterers.elementAt(c).cluster[i]] += weg;
			}
			int votingLabel = 0;
			for(int v=1;v<voting.length;++v){
				if(voting[votingLabel]<voting[v]){
					votingLabel = v;
				}
			}
			res[i]=votingLabel;
		}
		finalClustererResult fc = new finalClustererResult();
		fc.cluster = res.clone();
		return fc;
	}

	

}
