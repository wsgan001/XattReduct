package myUtils;

import java.io.File;
import java.io.FileReader;
import java.util.Random;
import java.util.Vector;

 
import weka.core.Instances;
import weka.filters.Filter;

public class xData {

	
	
	public static String getDataTable(Instances data){
		int n=data.numInstances();
		int m=data.numAttributes();
		String str = "";
		for(int i=0;i<n;++i){
			for(int j=0;j<m-1;++j){
				if(data.instance(i).isMissing(j)){
					str += "* ";
				}
				else{
					str += (int)data.instance(i).value(j)+" ";
					//str += data.instance(i).attribute(j).toString()+" ";
				}
				
			}
			str += data.classAttribute().value((int)data.instance(i).classValue());
			str +="\n";
		}
		return str;
	}
	
	public static String getDataTable_test(Instances data){
		int n=data.numInstances();
		int m=data.numAttributes();
		String str = "";
		for(int i=0;i<n;++i){
			for(int j=0;j<m-1;++j){
				if(data.instance(i).isMissing(j)){
					str += "* & ";
				}
				else{
					str += (int)data.instance(i).value(j)+" & ";
					//str += data.instance(i).attribute(j).toString()+" ";
				}
				
			}
			str += ((int)data.instance(i).classValue()==1?"A":"B")+"\\\\";
			//str += data.classAttribute().value((int)data.instance(i).classValue());
			str +="\n";
		}
		return str;
	}
 
		
	 

}
