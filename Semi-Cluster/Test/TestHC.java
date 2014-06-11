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
	       for (int i = 0; i < n; i++) { // Ѱ����С�������ڵ�����
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
	       ds.union(ds.find(mx), ds.find(my)); // ����С�������ڵ�����ʵ������ϲ�
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
	       for (int i = 0; i < n; i++) { // ���¾������
	           r[i][mx] = v[i];
	           r[i][my] = vv[i];
	       }
	       cluster(r, n, dis); // �������࣬�ݹ�ֱ�����д�֮�����С��disֵ
	   }
	
	   /** *//**
	    *
	    * @param r
	     * @param cnum
	     *            denote the number of final clusters
	     */
	    public void cluster(double[][] r, int cnum) {
	        /**//*if(cc< cnum)
	            System.err.println("����������ʵ����");*/
	        while (cc > cnum) {// �������࣬ѭ��ֱ�������������cnum
	            int mx = 0, my = 0;
	            double vmin = MAX;
	            for (int i = 0; i < n; i++) { // Ѱ����С�������ڵ�����
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
	            ds.union(ds.find(mx), ds.find(my)); // ����С�������ڵ�����ʵ������ϲ�
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
	            for (int i = 0; i < n; i++) { // ���¾������
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

