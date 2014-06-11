package UFS;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import weka.clusterers.ClusterEvaluation;
import weka.clusterers.SimpleKMeans;
import weka.core.Instances;

public class EvaluationTest {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		String path = "C:/Users/Eric/Desktop/2011�ﶬ/Code/Xreducer/data/Data/wine.arff";
		Instances m_data = new Instances(new FileReader(path));
		//m_data.setClassIndex(m_data.numAttributes()-1); 
		
		
		
		SimpleKMeans km = new SimpleKMeans();
		km.buildClusterer(m_data);
		km.setNumClusters(2); //���þ���Ҫ�õ����������
		Instances  tempIns = km.getClusterCentroids();
        
         ClusterEvaluation ce = new ClusterEvaluation();
         ce.setClusterer(km);
        // m_data.setClassIndex(m_data.numAttributes()-1); 
         ce.evaluateClusterer(m_data);
         
        
         System.out.println(ce.clusterResultsToString());
	}

}
