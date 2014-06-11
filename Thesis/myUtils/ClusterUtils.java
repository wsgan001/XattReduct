package myUtils;

import helpLib.Hungarian;

import java.util.Arrays;

import cluster.eva;

import weka.core.Instances;

public class ClusterUtils {
	public static void Alignment_MultiCluster2Classes(int[] base,int[] des){

		int k_base = FindKcluster(base);
		int k_des =  FindKcluster(des);
		
		int[][] M = new int[k_des][k_base];	
		for(int i=0;i<des.length;++i)
			M[des[i]][base[i]]++;		
		int[] Mind = new int[k_des];
		for(int i=0;i<M.length;++i)
		{
			int maxvalue = -1;
			 for(int j=0;j<M[i].length;++j){
				 if(M[i][j]>maxvalue)
				 {
					 maxvalue = M[i][j];
					 Mind[i] = j;
				 }
			 }
		}
		for(int i=0;i<des.length;++i)
			des[i]=Mind[des[i]];


	}
	public static int FindKcluster(int []X){
		int maxV = -1;
		for(int i=0;i<X.length;++i){
			if(maxV<X[i])
				maxV = X[i];
		}
		return maxV+1;
	}
	public static void Alignment(int[] classes,int[] cluster){

		
		int k_classes = FindKcluster(classes);
		int k_cluster =  FindKcluster(cluster);
		  if(k_classes!=k_cluster){
			Alignment_MultiCluster2Classes(classes,cluster);
		  }
		  else
		 {
		 	Alignment_ByBipartiteGraph(classes,cluster,k_classes);
		 }
	}
	public static void Alignment_ByBipartiteGraph(int[] classes,int[] cluster,int K){ //一一对应

		double[][] costMat=new double[K][K]; 
		for(int i=0;i<K;i++){
			for(int j=0;j<K;j++){
				costMat[i][j]=classes.length;
				for(int k=0;k<classes.length;k++){					
					if(classes[k]==i&&cluster[k]==j){
						costMat[i][j]-=1;
					}
				}
			}
		}
		Hungarian assignment = new Hungarian(costMat);
		for(int i=0;i<cluster.length;i++){
			cluster[i]=assignment.sol(cluster[i]);
		}
	 
	}
	public static double getAC_ByBipartiteGraph(int[] classes,int[] cluster){ //一一对应
		int[] tmpclasses = classes.clone();
		Arrays.sort(tmpclasses);
		int K = tmpclasses[tmpclasses.length-1]+1;
		int count=0;
		double[][] costMat=new double[K][K];
		for(int i=0;i<K;i++){
			for(int j=0;j<K;j++){
				costMat[i][j]=classes.length;
				for(int k=0;k<classes.length;k++){					
					if(classes[k]==i&&cluster[k]==j){
						costMat[i][j]-=1;
					}
				}
			}
		}
		Hungarian assignment = new Hungarian(costMat);
		for(int i=0;i<K;i++){ 
			count+=(classes.length-costMat[i][assignment.sol(i)]);
		}
		return (double)count/(double)classes.length;
	}
	private static double getAC_MultiToOne(int[] oclass, int[] clusters) {
		// TODO Auto-generated method stub
		double result = 0;
		int k_class = FindKcluster(oclass); //1对多
		int k_cluster =  FindKcluster(clusters);
		
		int[][] M = new int[k_cluster][k_class];
		
		for(int i=0;i<clusters.length;++i)
		{
			M[clusters[i]][oclass[i]]++;
		}

		for(int i=0;i<M.length;++i)
		{
			Arrays.sort(M[i]);
			result+=M[i][M[i].length-1];
		}
		return (double)result/(double)oclass.length;
	}
	
	public static double CA(Instances odata, int[] clusters)
	{
		
		double[] tmpdclass = odata.attributeToDoubleArray(odata.numAttributes()-1);
		int[] oclass = new int[odata.numInstances()];
		for(int i=0;i<tmpdclass.length;++i)
		{
			oclass[i]=(int)tmpdclass[i];
		}
		int k_base = FindKcluster(oclass);
		int k_des =  FindKcluster(clusters);
		if(k_base==k_des)
		//return getAC_MultiToOne(oclass, clusters); //多对1
			return getAC_ByBipartiteGraph(oclass, clusters); //1对1
		else
			return getAC_MultiToOne(oclass, clusters);
		
		
	}
	public static double SemiClusterWeight(int[] classes, int[] clusters,boolean[] labeled)
	{
		if(isAllFalse(labeled))
			return 1;
		return AccuracyRate(classes, clusters,labeled);//用聚类分对比例做Semi_w
		//return SemiNMI(classes, clusters,labeled);    //用NMI做Semi_w 容易出现0
		
	}

	private static double SemiNMI(int[] classes, int[] clusters, boolean[] labeled) {
		// TODO Auto-generated method stub
		int cnt = 0;
		for(int i=0;i<labeled.length;++i){
			if(labeled[i])
				cnt++;
		}
		int[] newClasses = new int[cnt];
		int[] newClusters = new int[cnt];
		cnt=0;
		for(int i=0;i<labeled.length;++i){
			if(labeled[i]){
				newClasses[cnt]=classes[i];
				newClusters[cnt++]=clusters[i];
			}
				 
		}	
		return NMI(newClasses, newClusters);

	}
	private static double AccuracyRate(int[] classes, int[] clusters,boolean[] labeled) {
		// TODO Auto-generated method stub
		//用聚类分对比例做Semi_w
		int all = 0;
		int right = 0;
		for(int i=0;i<labeled.length;++i){
			for(int j=0;j<labeled.length;++j){
				if(labeled[i]&&labeled[j]){
					all++;
					boolean cond1 = classes[i]==classes[j]&&clusters[i]==clusters[j];
					boolean cond2 = classes[i]!=classes[j]&&clusters[i]!=clusters[j];
					if(cond1||cond2)
						right++;
				}
			}
		}
		double ans = (double)right/(double)all;
		return ans;
	}
	private static boolean isAllFalse(boolean[] labeled) {
		// TODO Auto-generated method stub
		for(int i=0;i<labeled.length;++i)
			if(labeled[i])
				return false;
		return true;
	}
	public static boolean isAllSame(int[] elementAt) {
		// TODO Auto-generated method stub
		for(int i=0;i<elementAt.length-1;++i){
			if(elementAt[i]!=elementAt[i+1])
				return false;
		}
		return true;
	}
	public static double NMI(Instances data, int[] res){
		double[] tmpdclass = data.attributeToDoubleArray(data.numAttributes()-1);
		int[] oclass = new int[data.numInstances()];
		for(int i=0;i<tmpdclass.length;++i)
		{
			oclass[i]=(int)tmpdclass[i];
		}
		return NMI(oclass,res);
	}
	public static double NMI(int[] classes, int[] cluster){
		int k_classes = FindKcluster(classes);
		int k_cluster = FindKcluster(cluster);
		return eva.NMI(classes,k_classes, cluster,k_cluster);
	}
	
 


}
