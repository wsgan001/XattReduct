package Test;

import java.util.Arrays;
import java.util.Vector;

import Part1.finalClustererResult;
import Part1.oneClustererResult;

import weka.core.Instances;
import weka.core.SelectedTag;
import weka.core.Tag;
  
 

public  class ConMatrix extends MethodConsensusFunction {

	 
	public static enum LinkType{SINGLE,COMPLETE,AVERAGE,MEAN,CENTROID,WARD,ADJCOMLPETE,NEIGHBOR_JOINING};
	public static Tag[] TAGS_LINK_TYPE = {
		    new Tag(0, "SINGLE"),
		    new Tag(1, "COMPLETE"),
		    new Tag(2, "AVERAGE"),
		    new Tag(3, "MEAN"),
		    new Tag(4, "CENTROID"),
		    new Tag(5, "WARD"),
		    new Tag(6,"ADJCOMLPETE")
		 
	};
	public LinkType lt = null;
	public ConMatrix(LinkType lt){
		super(lt); 
	}
	@Override
	public finalClustererResult getFinalCluster(
			Vector<oneClustererResult> clusterers, double[][] LRx, LinkType lt ,Instances data,int k) throws Exception {
		// TODO Auto-generated method stub
		 
		int N = clusterers.elementAt(0).cluster.length;
 
		int t = clusterers.size();
		
		double[][] matrix = new double[N][N];
		 
		for(int i=0;i<N;++i){
			for(int j=i;j<N;++j){
				double tmp=0;
				for(int q=0;q<t;++q){
					double  weg = clusterers.elementAt(q).f_weigth;
					if(clusterers.elementAt(q).cluster[i]==clusterers.elementAt(q).cluster[j])
						tmp+=weg;
						//tmp++;
				}
				matrix[i][j]=tmp;
				matrix[j][i]=tmp;
				
			}
		}
		double[][] LR = LRx.clone();
		for(int i=0;i<N;++i){
			for(int j=i;j<N;++j){
				if(LR[i][j]==-1){
					matrix[i][j]=2*t;
				}
				else if(LR[i][j]==1)
					matrix[i][j] = 0 ;
				else{
					matrix[i][j]=t*(1-matrix[i][j]);
				}
				
				
				/*if(matrix[i][j]==0||LR[i][j]==-1)
					matrix[i][j] = 1000000000;
				else if(LR[i][j]==1)
					matrix[i][j] = 0 ;
				else
					matrix[i][j]=1/matrix[i][j];*/
				matrix[j][i]=matrix[i][j];
			}
		}
	     for(int i=0;i<N;++i){
			matrix[i][i]=0;
		}
   /*    TestHC cl = new TestHC(N);
      //cl.cluster(r, 6, 1);
       cl.cluster(matrix, k);
       int ind = 0;
       for (int i = 0; i < cl.n; i++) {
    	   cl.clusterList[cl.ds.find(i)].add(i);
       }
       for (int i = 0; i < cl.n; i++) {
           if (cl.clusterList[i].size() != 0) {

               for (int j = 0; j < cl.clusterList[i].size(); j++) {
            	   res[(Integer) cl.clusterList[i].get(j)]=ind;
               }
             
               ind++;
           }
       }
	System.out.println(Arrays.toString(res));*/
		
	     DistanceBySim dis = new DistanceBySim(matrix);
	     HierarchicalClustererEx hc = new HierarchicalClustererEx(dis);
	     
	     //hc.setInitCluster(partitions);
			hc.setNumClusters(k);
			Instances newdata = new Instances(data);
			int  tmp = lt.ordinal();
			SelectedTag st = new SelectedTag(tmp, TAGS_LINK_TYPE);
			hc.setLinkType(st);
			hc.buildClusterer(newdata);
		 
		
			 
			int[] res = hc.m_nClusterNr.clone();
	     
	     
	     
	     
		
		finalClustererResult fc = new finalClustererResult();
		fc.cluster = res.clone();
		return fc;
		

	}

	@Override
	public finalClustererResult getFinalCluster(
			Vector<oneClustererResult> clusterers, double[][] LRx, int k)
			throws Exception {
		// TODO Auto-generated method stub
		double[][] LR = LRx.clone();
		int N = clusterers.elementAt(0).cluster.length;
		int[] res  = new int[N];
		int t = clusterers.size();
		
		
		for(int i=0;i<N;++i){
			for(int j=i+1;j<N;++j){
				LR[j][i]=LR[i][j];
			}
		}
		double[][] matrix = new double[N][N];
		 
		for(int i=0;i<N;++i){
			for(int j=i;j<N;++j){
				double tmp=0;
				for(int q=0;q<t;++q){
					double  weg = clusterers.elementAt(q).f_weigth;
					if(clusterers.elementAt(q).cluster[i]==clusterers.elementAt(q).cluster[j])
						tmp+=weg;
						//tmp++;
				}
				matrix[i][j]=tmp;
				matrix[j][i]=tmp;
				
			}
		}
	
		for(int i=0;i<N;++i){
			for(int j=i;j<N;++j){
				if(LR[i][j]==-1){
					matrix[i][j]=2*t;
				}
				else if(LR[i][j]==1)
					matrix[i][j] = 0 ;
				else{
					matrix[i][j]=t*(1-matrix[i][j]);
				}
				
				
				/*if(matrix[i][j]==0||LR[i][j]==-1)
					matrix[i][j] = 1000000000;
				else if(LR[i][j]==1)
					matrix[i][j] = 0 ;
				else
					matrix[i][j]=1/matrix[i][j];*/
				matrix[j][i]=matrix[i][j];
			}
		}
	     for(int i=0;i<N;++i){
			matrix[i][i]=0;
		}
       TestHC cl = new TestHC(N);
      //cl.cluster(r, 6, 1);
       cl.cluster(matrix, k);
       int ind = 0;
       for (int i = 0; i < cl.n; i++) {
    	   cl.clusterList[cl.ds.find(i)].add(i);
       }
       for (int i = 0; i < cl.n; i++) {
           if (cl.clusterList[i].size() != 0) {

               for (int j = 0; j < cl.clusterList[i].size(); j++) {
            	   res[(Integer) cl.clusterList[i].get(j)]=ind;
               }
             
               ind++;
           }
       }
	//System.out.println(Arrays.toString(res));
		
		
		finalClustererResult fc = new finalClustererResult();
		fc.cluster = res.clone();
		return fc;
		

	}

	 

	

	@Override
	public finalClustererResult getFinalCluster(
			Vector<oneClustererResult> clusterers, int k) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}


	

	 

}
