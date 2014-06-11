package CheckMonotony;

import java.util.Random;

import weka.core.Instances;
import weka.datagenerators.classifiers.classification.RandomRBF;

public class Sim2Data {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		double[][] sim1 = {
				{1.0, 1.0, 1.0, 0.0},
				{1.0, 1.0, 1.0, 1.0},
				{1.0, 1.0, 1.0, 1.0},
				{0.0, 1.0, 1.0, 1.0}
		};
		double[][] sim2 = {
				{1.0, 0.0, 0.0, 1.0},
				{0.0, 1.0, 1.0, 0.0},
				{0.0, 1.0, 1.0, 1.0},
				{1.0, 0.0, 1.0, 1.0}
		};
		double[][] sim3 = {
				{1.0, 0.0, 0.0, 0.0},
				{0.0, 1.0, 1.0, 1.0},
				{0.0, 1.0, 1.0, 0.0},
				{0.0, 1.0, 0.0, 1.0}
		};
 
		
		
		
		
		
		int N =10000;
		for(int i=0;i<N;++i){
			 if(checkone(sim3,i)){
				//System.out.println("Find");
				
			 }
			
			
			
			   
		}
	}
	public static boolean checkone(double [][] sim,int ind) throws Exception
	{		
		Instances data = CheckMonotony.generateInstances(4,1,2,1,ind);
		boolean[] B = {true,false};
		double[][] Sb = Entropy.Data2Sim_Covering(data,B);
		boolean tag = true;
		for(int i=0;i<sim.length;++i){
			for(int j=i+1;j<sim.length;++j){
				if(sim[i][j]!=Sb[i][j]){
					tag= false;
					break;
				}
			}
		}
		if(tag)
			System.out.println(data);
		return tag;
	}

}
