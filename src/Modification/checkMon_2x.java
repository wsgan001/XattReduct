package Modification;

import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Vector;

import myUtils.xData;
import myUtils.xFigure;
import myUtils.xMath;
import weka.core.Instances;
import weka.filters.Filter;
import CheckMonotony.CheckMonotony;
import CheckMonotony.InconsistentCheck;

public class checkMon_2x {
	//验证不确定度随不完备率增加的变化-->数据随机生成 属性为符号
	//
	//InconsistentCheck.getInconsistentData(originaldata,currentrate,new Random(i+k));

	public static Map<String,double[][]> calculate(Instances originaldata, int[] e_indexs){
		int n = e_indexs.length;
		int R = 25;
		double ratestep = 0.02;
		double originalrate = InconsistentCheck.getInconsistentRate(InconsistentCheck.Data2Matrix(originaldata));
		boolean[] B = generateB(originaldata.numAttributes()-1);
		
		Map<String,double[][]> allRes = new HashMap<String,double[][]>();
		Instances data = new Instances(originaldata);
		double currentrate = originalrate;
		System.out.println(xData.getDataTable(data));
		for(int k=0;k<R;++k){				
			currentrate = currentrate+ ratestep;
			data = InconsistentCheck.getInconsistentData(data,currentrate,new Random(k));
			System.out.println("Inrate"+":"+currentrate);
			System.out.println(xData.getDataTable(data));
		}
		
		
		for(int i=0;i<n;++i){
			data = new Instances(originaldata);
			currentrate = originalrate;
			double[][] res = new double[2][R];
			for(int k=0;k<R;++k){				
				currentrate = currentrate+ ratestep;
				data = InconsistentCheck.getInconsistentData(data,currentrate,new Random(k));
			
				
				res[0][k]=currentrate;
				res[1][k]=Dependence.getUncertainty(data, B, e_indexs[i]);
			}
			System.out.println(e_indexs[i]);
			System.out.println(xMath.isMonotony(res[1])+":"+ Arrays.toString(res[1]));
			allRes.put(Integer.toString(e_indexs[i]), res);
		}
		
		
		return allRes;
	}
	public static void Run() throws Exception{
		//String[] dataname =  {"Balance","Car","Dermatology","Monk_1","Monk_2","Monk_3","Tic-tac-toe","Wobc","zoo"};
		String respath = "C:/Users/Eric/Desktop/2012春/Modification.NO3/结果/2.with inrate/test/";
		int[] e_indexs_1 = {0};
		int[] e_indexs_2 = {10};
		int[] e_indexs_3 = {2,3};
		int[] e_indexs_x = {0,10,2,3};
		
		int Runtime = 17339;
		int bin = 4;
		for(int d=17338;d<Runtime;++d){
			Instances originaldata = CheckMonotony.generateInstances(25,5,2,1,d); //caseind=1
			//System.out.println(originaldata);
			if(bin == -1){
				 weka.filters.supervised.attribute.Discretize sd = new weka.filters.supervised.attribute.Discretize();
				try {
					sd.setInputFormat(originaldata);
					originaldata = Filter.useFilter(originaldata , sd);
				} catch (Exception e) {
						// TODO Auto-generated catch block
					e.printStackTrace();
				}		 
			}
			else{
			 
				weka.filters.unsupervised.attribute.Discretize unsd = new weka.filters.unsupervised.attribute.Discretize();
				unsd.setBins(bin);
				//unsd.setUseEqualFrequency(true); // If set to true, equal-frequency binning will be used instead of equal-width binning.
				try {
					unsd.setInputFormat(originaldata);
					originaldata = Filter.useFilter(originaldata , unsd);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
			//System.out.println(originaldata);
			Map<String,double[][]> xydatax = checkMon_2x.calculate(originaldata, e_indexs_x);
			if(checkLines(xydatax))
			{
				String[] ts = new String[5];
				ts[0]=Integer.toString(d);
				ts[1]="ConditionalEntropy";
				ts[2]="CE-Elementbased";
				ts[3]="ClassificationAccuracy";
				ts[4]="Inrate";
				Map<String,double[][]> xydata1 = checkMon_2x.calculate(originaldata, e_indexs_1);
				Map<String,double[][]> xydata2 = checkMon_2x.calculate(originaldata, e_indexs_2);
				Map<String,double[][]> xydata3 = checkMon_2x.calculate(originaldata, e_indexs_3);
				Vector<Map<String,double[][]>> allxy = new Vector<Map<String,double[][]>>();
		    	allxy.add(xydata1);
		    	allxy.add(xydata2);
		    	allxy.add(xydata3);
		    	
				xFigure.saveFigure(ts, respath, allxy);
				System.out.println(d+":Sucess!");
			}
			else
			{
				//System.out.println(d+":Done!");
			}
		}
	}
	
	
	private static boolean checkLines(Map<String, double[][]> xydatax) {
		// TODO Auto-generated method stub
		boolean[] line1 = abstractLines(xydatax.get("0")[1]);
		boolean tag0 = myUtils.xMath.isStrictMonotony(xydatax.get("0")[1]);
		boolean tag1 = myUtils.xMath.isMonotony(xydatax.get("10")[1]);
		boolean[] line3 = abstractLines(xydatax.get("2")[1]);
		boolean[] line4 = abstractLines(xydatax.get("3")[1]);
		//boolean tag2 = Dependence.isInclusion(line1, line3);
		//boolean tag3 = Dependence.isInclusion(line1, line4);
		
		boolean tag2 = myUtils.xMath.boolTrueNum(line3)>=14;
		boolean tag3 = myUtils.xMath.boolTrueNum(line4)>=14;	
	   if(tag0 && !tag1 && tag2 && tag3)
		{
			//System.out.println(Arrays.toString(xydatax.get("0")[1]));
			//System.out.println(Arrays.toString(line1));
			//System.out.println(Arrays.toString(xydatax.get("2")[1]));
			//System.out.println(Arrays.toString(line3));
			//System.out.println(Arrays.toString(xydatax.get("3")[1]));
			//System.out.println(Arrays.toString(line4));
		   //System.out.println(myUtils.xMath.boolTrueNum(line3)+":"+myUtils.xMath.boolTrueNum(line4));
			return true;
		}
		else
			return false;
	}
	private static boolean[] abstractLines(double[] line){
		boolean[] ans = new boolean[line.length-1];
		for(int i=0;i<line.length-1;++i){
			ans[i] = (line[i+1]-line[i])==0?true:false;
		}
		return ans;
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

	public static void main(String[] args) throws Exception {
		Run();
	}
}
