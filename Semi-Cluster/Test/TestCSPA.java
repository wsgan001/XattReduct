package Test;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;

import weka.core.Instances;

import com.mathworks.toolbox.javabuilder.MWClassID;
import com.mathworks.toolbox.javabuilder.MWException;
import com.mathworks.toolbox.javabuilder.MWNumericArray;

import ClusterEnsembleJava.ClusterEnsembleJavaclass;

public class TestCSPA {
	 public static void main(String args[])  throws MWException, FileNotFoundException, IOException{
		ClusterEnsembleJavaclass a = new ClusterEnsembleJavaclass();
		String dataname = "half-rings";
    	//String dataname = "Aggregation";
    	//String dataname = "Compound";
		//String dataname = "twomoons";
    	//String dataname = "Pathbased";
    	//String dataname = "Spiral";
    	//String dataname = "D31";
    	//String dataname = "R15";
    	//String dataname = "Flame";
    	
    	
    	String path = "C:\\Users\\Eric\\Desktop\\2012Çï¶¬\\NO.4\\data\\"+dataname+".arff";
    	Instances data = new Instances(new FileReader(path));
		data.setClassIndex(data.numAttributes()-1);
		
		Object[] rhs = new Object[2];
		int n = data.numInstances();
		double[][] pData = new double[2][n];
		for(int i=0;i<n;++i){
			pData[0][i]=data.instance(i).value(0);
			pData[1][i]=data.instance(i).value(1);

		}
		rhs[0] = new MWNumericArray(pData, MWClassID.SINGLE);
		rhs[1] = new MWNumericArray(3, MWClassID.SINGLE);
		Object[] outRes = new Object[1];
		outRes = a.cspa(1,rhs);
	 
		
		MWNumericArray temp = (MWNumericArray)outRes[0]; 
		float[] weights=(float[])temp.toFloatArray();


		System.out.println(Arrays.toString(weights));
	}
	
}
