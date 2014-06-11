package CheckMonotony;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;

import Xreducer_core.Utils;
import Xreducer_fuzzy.SStyle_MaxMin;
import Xreducer_fuzzy.SimilarityStyle;

import weka.core.Instances;

public class test {

	/**
	 * @param args
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	public static void main(String[] args) throws FileNotFoundException, IOException {
		// TODO Auto-generated method stub
		 //String fn = "C:/Users/Eric/Desktop/2011�ﶬ/Code/Xreducer/data/Data/wdbc.arff";
			//String fn = "C:/Users/Eric/Desktop/2011�ﶬ/Code/Xreducer/data/Data/fuzzy-ex.arff";
		   String fn = "C:/Users/Eric/Desktop/2012��/Paper.new.NO3/ex2.arff";
			Instances data = new Instances(new FileReader(fn));
			data.setClassIndex(data.numAttributes()-1); //���þ�����������
		
		
		
		boolean[] A = Utils.Instances2FullBoolean(data);
		boolean[] D = Utils.Instances2DecBoolean(data);
		boolean[] B = new boolean[data.numAttributes()];
		B[2]=true;
		B[1]=true;
		
		 
		//double ans = Entropy.New_Covering(data, D, A);
		//double ans = Entropy.Element_Covering(data, D, A);
		//double ans = Entropy.New_Fuzzy_Version1(data, D, A);
		double ans = Entropy.Element_Fuzzy(data, D, B);
		 System.out.println(ans);
	}

}
