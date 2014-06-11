package Test;
import java.util.*;    

	 public class TestHC {
		 public List[] clusterList;
	    DisjointSetsSlow ds;
	    public static final int MAX = Integer.MAX_VALUE;
	    public int n;
	    public int cc;
	
	   // private double ori[] = {1,2,5,7,9,10};
	
	   public  TestHC(int num) {
	       ds = new DisjointSetsSlow(num);
	       n = num;
	       cc = n;
	       clusterList = new ArrayList[num];
	       for (int i = 0; i < n; i++)
	           clusterList[i] = new ArrayList();
	   }
	
	   public List[] getClusterList() {
	       return clusterList;
	   }
	
	   public void setClusterList(List[] clusterList) {
	       this.clusterList = clusterList;
	   }
	
	   public void output() {
	       int ind = 1;
	       for (int i = 0; i < n; i++) {
	           clusterList[ds.find(i)].add(i);
	       }
	       for (int i = 0; i < n; i++) {
	           if (clusterList[i].size() != 0) {
	               System.out.print("cluster " + ind + " :");
	               for (int j = 0; j < clusterList[i].size(); j++) {
	                   System.out.print(clusterList[i].get(j) + " ");
	               }
	               System.out.println();
	               ind++;
	           }
	       }
	   }
	
	   /** *//**
	    * this method provides a hierachical way for clustering data.
	    *
	    * @param r
	    *            denote the distance matrix
	    * @param n
	    *            denote the sample num(distance matrix's row number)
	    * @param dis
	    *            denote the threshold to stop clustering
	    */
	   public void cluster(double[][] r, int n, double dis) {
	       int mx = 0, my = 0;
	       double vmin = MAX;
	       for (int i = 0; i < n; i++) { // 寻找最小距离所在的行列
	           for (int j = 0; j < n; j++) {
	               if (j > i) {
	                   if (vmin > r[i][j]) {
	                       vmin = r[i][j];
	                       mx = i;
	                       my = j;
	                   }
	               }
	           }
	       }
	       if (vmin > dis) {
	           return;
	       }
	       ds.union(ds.find(mx), ds.find(my)); // 将最小距离所在的行列实例聚类合并
	       double o1[] = r[mx];
	       double o2[] = r[my];
	       double v[] = new double[n];
	       double vv[] = new double[n];
	       for (int i = 0; i < n; i++) {
	           double tm = Math.min(o1[i], o2[i]);
	           if (tm != 0)
	               v[i] = tm;
	           else
	               v[i] = MAX;
	           vv[i] = MAX;
	       }
	       r[mx] = v;
	       r[my] = vv;
	       for (int i = 0; i < n; i++) { // 更新距离矩阵
	           r[i][mx] = v[i];
	           r[i][my] = vv[i];
	       }
	       cluster(r, n, dis); // 继续聚类，递归直至所有簇之间距离小于dis值
	   }
	
	   /** *//**
	    *
	    * @param r
	     * @param cnum
	     *            denote the number of final clusters
	     */
	    public void cluster(double[][] r, int cnum) {
	        /**//*if(cc< cnum)
	            System.err.println("聚类数大于实例数");*/
	        while (cc > cnum) {// 继续聚类，循环直至聚类个数等于cnum
	            int mx = 0, my = 0;
	            double vmin = MAX;
	            for (int i = 0; i < n; i++) { // 寻找最小距离所在的行列
	                for (int j = 0; j < n; j++) {
	                    if (j > i) {
	                        if (vmin > r[i][j]) {
	                            vmin = r[i][j];
	                            mx = i;
	                            my = j;
	                        }
	                    }
	                }
	            }
	            ds.union(ds.find(mx), ds.find(my)); // 将最小距离所在的行列实例聚类合并
	            double o1[] = r[mx];
	            double o2[] = r[my];
	            double v[] = new double[n];
	            double vv[] = new double[n];
	            for (int i = 0; i < n; i++) {
	                double tm = Math.min(o1[i], o2[i]);
	                if (tm != 0)
	                    v[i] = tm;
	                else
	                    v[i] = MAX;
	                vv[i] = MAX;
	            }
	            r[mx] = v;
	            r[my] = vv;
	            for (int i = 0; i < n; i++) { // 更新距离矩阵
	                r[i][mx] = v[i];
	                r[i][my] = vv[i];
	            }
	            cc--;
	        }
	    }
	
	    public static void main(String args[]) {
	        double[][] r = { { 0, 1, 4, 6, 8, 9 }, { 1, 0, 3, 5, 7, 8 },
	                { 4, 3, 0, 2, 4, 5 }, { 6, 5, 2, 0, 2, 3 },
	                { 8, 7, 4, 2, 0, 1 }, { 9, 8, 5, 3, 1, 0 } };
	        TestHC cl = new TestHC(6);
	      //cl.cluster(r, 6, 1);
	       cl.cluster(r, 3);
	       cl.output();
	   }
} 

