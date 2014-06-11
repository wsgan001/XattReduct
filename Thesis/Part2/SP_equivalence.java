package Part2;

import java.util.Arrays;

import weka.core.Instances;

public class SP_equivalence extends SplitPartition {

	public SP_equivalence(Instances data, Instances data_cluster,
			boolean[] labeled) {
		super(data, data_cluster, labeled);
		// TODO Auto-generated constructor stub
	}

	@Override
	public int[] generatePatition() throws Exception {
		// TODO Auto-generated method stub
	 
		return this.splitpartitionWithLabel(getEquivalenceClass(this.data_cluster));
	}
	public  int[] getEquivalenceClass(Instances dataset){

		boolean [] X = new boolean[dataset.numAttributes()];
		Arrays.fill(X, true);
		int U = dataset.numInstances();
		//求等价类,先求等价矩阵
		int[][] X_matrix = new int[U][U];
		int N = 0; //统计等价类的个数
		for(int i=0;i<U;++i){
			if(X_matrix[i][i]==0){
				N++;
				X_matrix[i][i] = 1;
				for(int j=i+1;j<U;++j){
					boolean flag = true;
					for(int k=0;k<X.length;++k){
						if(X[k]&&dataset.instance(i).value(k)!=dataset.instance(j).value(k)){
							flag = false;
							break;
						}
					}
					if (flag){
						X_matrix[i][j] = 1;
						X_matrix[j][j] = 2;
					}
				}
			}
		}
		int [] p = new int[U];
		int n=0;
		for (int i=0;i<U;++i){
			if(X_matrix[i][i]==1){
				for (int j=0;j<U;++j){
					if(X_matrix[i][j]==1)
					   p[j]=n;
				}
				n++;
			}
		}
		return p;
	}
}
