package Modification;

import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Vector;

import CheckMonotony.InconsistentCheck;

import myUtils.xFigure;
import myUtils.xMath;
import weka.core.Instances;

public class checkMon_2 {
	//验证不确定度随不完备率增加的变化
	

	public static Map<String,double[][]> calculate(String dataname, Instances originaldata, int[] e_indexs){
		int n = e_indexs.length;
		int R = 26;
		double ratestep = 0.02;
		double originalrate = InconsistentCheck.getInconsistentRate(InconsistentCheck.Data2Matrix(originaldata));
		boolean[] B = generateB(originaldata.numAttributes()-1);
		
		Map<String,double[][]> allRes = new HashMap<String,double[][]>();
		for(int i=0;i<n;++i){
			Instances data = new Instances(originaldata);
			double currentrate = originalrate;
			double[][] res = new double[2][R];
			String str = "descriptor "+dataname;
			switch(e_indexs[i]){
			case 0:{
				str += "_MCE";break;
			}
			case 10:{
				str += "_ECE";break;
			}
			case 2:{
				str += "_POS1";break;
			}
			case 3:{
				str += "_POS2";break;
			}
			default:break;
			}
			
			System.out.println(str);
			for(int k=0;k<R;++k){				
				
				data = InconsistentCheck.getInconsistentData(data,currentrate,new Random(k));
			
				res[0][k]=currentrate;
				res[1][k]=Dependence.getUncertainty(data, B, e_indexs[i]);
				res[1][k]=(res[1][k]==-0.0)?0:res[1][k];
				System.out.println(res[1][k]);
				currentrate = currentrate+ ratestep;
			}
			
			//System.out.println(xMath.isMonotony(res[1])+":"+ Arrays.toString(res[1]));
		 
			allRes.put(Integer.toString(e_indexs[i]), res);
		}
		return allRes;
	}
	public static void Run() throws IOException{
		String[] dataname =  {"example","Balance","Car","Monk_1","Monk_2","Monk_3","Tic-tac-toe"};
		String respath = "C:/Users/Eric/Desktop/2012春/Modification.NO3/结果/2.with inrate/R25 A/";
		int[] e_indexs_1 = {0};
		int[] e_indexs_2 = {10};
		int[] e_indexs_3 = {2,3};
		
		for(int d=0;d<dataname.length;++d){
			Instances originaldata = getData(dataname,d);
			String[] ts = new String[5];
			ts[0]=dataname[d];
			ts[1]="ConditionalEntropy";
			ts[2]="ClassificationAccuracy";
			ts[3]="UncertaintyMeasurement";
			ts[4]="Inrate";
			Map<String,double[][]> xydata1 = checkMon_2.calculate(dataname[d],originaldata, e_indexs_1);
			Map<String,double[][]> xydata2 = checkMon_2.calculate(dataname[d],originaldata, e_indexs_2);
			Map<String,double[][]> xydata3 = checkMon_2.calculate(dataname[d],originaldata, e_indexs_3);
			Vector<Map<String,double[][]>> allxy = new Vector<Map<String,double[][]>>();
	    	allxy.add(xydata1);
	    	allxy.add(xydata2);
	    	allxy.add(xydata3);
	    	
			xFigure.saveFigure(ts, respath, allxy);
			//System.out.println(dataname[d]+":Sucess!");
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
	private static boolean[] generateB(int m) {
		// TODO Auto-generated method stub
		boolean[] res = new boolean[m+1];
		Arrays.fill(res, false);
		for(int i=0;i<m;++i){
			res[i]=true;
		}
		return res;
	}

	public static void main(String[] args) throws IOException {
		Run();
	}
}
