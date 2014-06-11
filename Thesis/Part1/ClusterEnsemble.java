package Part1;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Random;
import java.util.Vector;

import Test.ConMatrix.LinkType;

import myUtils.ClusterUtils;

import cluster.eva;

import weka.clusterers.SimpleKMeans;
import weka.core.Instances;


public class ClusterEnsemble {
	public MethodGenerateClustering gc= null;
	public MethodConsensusFunction cf = null;
	public finalClustererResult fr = null;
	public boolean isConformity = true;
	public  ClusterEnsemble(MethodGenerateClustering gc, MethodConsensusFunction cf, boolean isConformity) throws Exception{
		this.isConformity = isConformity;
		
		this.gc = gc;
		this.cf = cf;
		fr = getFinalResult();
		fr.data = gc.data;
		Evaluate();
		 
	}
 
	public finalClustererResult getFinalResult() throws Exception
	{
		Vector<oneClustererResult> clusterers= gc.getClusterers();
		if(isConformity)
			Conformity(clusterers,gc.cluster_baseind);
		return cf.getFinalCluster(clusterers,gc.data.numClasses()); 
	}
	
	private void Conformity(Vector<oneClustererResult> clusterers, int baseInd) {
		// TODO Auto-generated method stub
		for(int i=0;i<clusterers.size();++i){
			if(i!=baseInd&&!myUtils.ClusterUtils.isAllSame(clusterers.elementAt(i).cluster)){
				myUtils.ClusterUtils.Alignment(clusterers.elementAt(baseInd).cluster,clusterers.elementAt(i).cluster);
			}
		}
	}
	public void Evaluate(){
		fr.ac=ClusterUtils.CA(fr.data, fr.cluster);	
		fr.nmi = ClusterUtils.NMI(fr.data, fr.cluster);
	}
	public static boolean[] getRandomLabeled(double per, int N,int rnd){
		boolean[] ans = new boolean[N];
		Arrays.fill(ans, false);
		for(int i=0;i<(int)N*per;++i){
			ans[i]=true;
		}
		Random rd = new Random(rnd);
		for(int i=0;i<N;++i){
			int pos = rd.nextInt(i+1);
			boolean tmp = ans[pos];
			ans[pos]=ans[i];
			ans[i]=tmp;
		}
		return ans;
	}

	

}
