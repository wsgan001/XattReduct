package Modification;

import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import myUtils.xFigure;

import weka.core.Instances;

public class checkMon_1 {
	//验证不确定度随属性增加的变化
	
	public static Map<String,double[][]> calculate(Instances data, int[] e_indexs){
		int n = e_indexs.length;
		Map<String,double[][]> allRes = new HashMap<String,double[][]>();
		for(int i=0;i<n;++i){
			int m = data.numAttributes()-1;
			double[][] res = new double[2][m];
			for(int k=0;k<m;++k){
				boolean[] B = generateB(m,k);
				res[0][k]=k+1;
				res[1][k]=Dependence.getUncertainty(data, B, e_indexs[i]);
			}
			allRes.put(Integer.toString(e_indexs[i]), res);
		}
		return allRes;
	}
	public static void Run() throws IOException{
		String[] dataname =  {"Balance","Car","Dermatology","Monk_1","Monk_2","Monk_3","Tic-tac-toe","Wobc","zoo"};
		String respath = "C:/Users/Eric/Desktop/2012春/Modification.NO3/结果/1.with att/";
		int[] e_indexs = {0,2,3,4,5};
		
		for(int d=0;d<dataname.length;++d){
			Instances data = getData(dataname,d);
			String[] ts = new String[3];
			ts[0]=dataname[d];
			ts[1]="UncertaintyMeasurement";
			ts[2]="Attributes";
			Map<String,double[][]> xydata = checkMon_1.calculate(data, e_indexs);
			xFigure.saveFigure(ts, respath, xydata);
			System.out.println(dataname[d]+":Sucess!");
		}
	}
	
	
	public static Instances getData (String[] dataname, int index) throws IOException, IOException{
		String path = "C:/Users/Eric/Desktop/2012春/Modification.NO3/Data/current data/";
		System.out.println(dataname[index]);
		String datafn = path+dataname[index]+".arff";
		Instances data = new Instances(new FileReader(datafn));
		data.setClassIndex(data.numAttributes()-1); 
		return data;
	}
	private static boolean[] generateB(int m, int k) {
		// TODO Auto-generated method stub
		boolean[] res = new boolean[m];
		Arrays.fill(res, false);
		for(int i=0;i<=k;++i){
			res[i]=true;
		}
		return res;
	}

	public static void main(String[] args) throws IOException {
		Run();
	}
}
