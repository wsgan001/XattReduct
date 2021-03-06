package Part1Ex;

 
import java.util.Arrays;
import java.util.Vector;

 
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;

import Part1.MethodConsensusFunction;
import Part1.finalClustererResult;
import Part1.oneClustererResult;

public class MCF_w_kmodes extends MethodConsensusFunction {


	public int rnd = -1;
	public MCF_w_kmodes(int rnd){
		this.rnd = rnd;
	}
	@Override
	public finalClustererResult getFinalCluster(
			Vector<oneClustererResult> clusterers, int k) throws Exception   {
		// TODO Auto-generated method stub
		int ensize = clusterers.size();
		double [] weg = new double[ensize];
		for(int i=0;i<ensize;++i){
			weg[i]=clusterers.elementAt(i).f_weigth;
		}
		Instances newData = getNewData(clusterers);
		SimpleKModes km = new SimpleKModes();
		km.setMaxIterations(100);
		km.setDistanceFunction(new DissimilarityForKmodes_Weight());
	    km.setNumClusters(k);
	    km.setSeed(this.rnd);
	    km.setPreserveInstancesOrder(true);
		km.buildClusterer(newData);
	 
		finalClustererResult fc = new finalClustererResult();
		fc.cluster = km.getAssignments();
		return fc;

		
	}
	public  Instances getNewData(Vector<oneClustererResult> clusterers) {
		// TODO Auto-generated method stub
		int N = clusterers.elementAt(0).cluster.length;
		int M = clusterers.size();
		FastVector Allatt = new FastVector();
		for(int i=0;i<M;++i){
			FastVector att = getNominal(clusterers.elementAt(i).cluster);
			Attribute a = new Attribute("A"+(i+1), att);
			a.setWeight(clusterers.elementAt(i).f_weigth);
			Allatt.addElement(a);
		}
		Instances NewData = new Instances("NewData", Allatt , N);
		 
		for(int i=0;i<N;++i){
			Instance ins = new Instance(M);
			ins.setDataset(NewData);
			for(int k=0;k<M;++k){
				String s = Integer.toString(clusterers.elementAt(k).cluster[i]);
				ins.setValue(k, s);
			}
			NewData.add(ins);
		}
	
		return NewData;
	}
	private FastVector getNominal(int[] elementAt) {
		// TODO Auto-generated method stub
		FastVector att = new FastVector();
		int k = myUtils.ClusterUtils.FindKcluster(elementAt);
		for(int i=0;i<k;++i){
			att.addElement(Integer.toString(i));
		}
		return att;
	}
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		Vector<oneClustererResult> cls = new Vector<oneClustererResult> ();
		for(int i=0;i<5;++i){
			oneClustererResult a = new oneClustererResult();
			int[] res = {1,2,1,1,1,2,2,2,3,3,3,3,1,1,1,2,2,5,5,4,4,4};
			a.cluster = res;
			a.f_weigth = 0.8;
			cls.add(a);
		}
		MCF_w_kmodes km = new MCF_w_kmodes(2);
		System.out.println(Arrays.toString(km.getFinalCluster(cls,6).cluster));
		//Ttest.Run();
	}


}
