package Modification;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;

import Xreducer_core.Utils;

import weka.core.Instances;

public class CheckData {

	
	public static boolean[] getReduction(Instances m_data, int E_index){
		int NumAttr = m_data.numAttributes();
		String m_process = E_index+"\n";
		// TODO Auto-generated method stub
		long time_start = Utils.getCurrenttime();	
		
		boolean[] newB = new boolean[NumAttr];
		boolean[] tempB = new boolean[NumAttr];
		boolean[] D = Utils.Instances2DecBoolean(m_data);
		boolean[] A = Utils.Instances2FullBoolean(m_data);
		double ha = Dependence.getEvalution(m_data, A, E_index);
		double rbest = 0;
		
 
		do{
			tempB = newB.clone();
			rbest = -1000000; 
			for(int i=0;i<NumAttr-1;++i){ //去掉决策属性
				if(!newB[i]){
					newB[i]=true;
					double rRandx = Dependence.getEvalution(m_data, newB, E_index);
					if(Utils.isAllFalse(tempB)||rRandx>rbest){
						tempB = newB.clone();
						rbest = rRandx;
					}
					newB[i]=false;
				}
			}
			newB = tempB.clone();
			m_process+=Arrays.toString(Utils.boolean2select(newB))+"=>"+(rbest>=0?rbest:-rbest)+"\n";
		}
		//while(Math.abs(rbest-ha)>0.0001);	
		while(rbest!=ha);	
		
		double m_useTime = (Utils.getCurrenttime() - time_start)/(double)1000;
		int m_numRed = Utils.booleanSelectedNum(newB);
		System.out.println(m_process);
		System.out.println(Arrays.toString(Utils.boolean2select(newB))+":"+m_useTime+"s");
		return newB;
	}
	
	
	
	public static Instances getData (int index) throws IOException, IOException{
		String path = "C:/Users/Eric/Desktop/2012春/Modification.NO3/Data/current data/";
		String[] dataname = {"Balance","Car","Dermatology","Monk_1","Monk_2","Monk_3","Tic-tac-toe","Wobc","zoo"};
		System.out.println(dataname[index]);
		String datafn = path+dataname[index]+".arff";
		Instances data = new Instances(new FileReader(datafn));
		data.setClassIndex(data.numAttributes()-1); 
		return data;
	}
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		int datanum = 9;
		int evanum = 10;
		for(int i=0;i<datanum;++i){
			Instances data = getData(i);
			for(int j=0;j<evanum;++j)
				getReduction(data,j);
		}
	}

}
