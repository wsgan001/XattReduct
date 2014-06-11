package Modification;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;

import Xreducer_core.Utils;
import Xreducer_core.Utils_entropy;

import weka.core.Instances;

public class Dependence {

	public static double getUncertainty(Instances data, boolean[] B, int U_index){
		double ans = 0;
		switch(U_index){
		case 0:{
			ans = getConditionalEntropy(data,B,1);
			break;
		}
		case 1:{
			ans = getConditionalEntropy(data,B,2);
			break;
		}
		case 2:{
			ans = 1-getApproximationAccuracy(data,B,1,1);
			break;
		}
		case 3:{
			ans = 1-getApproximationAccuracy(data,B,1,2);
			break;
		}
		case 4:{
			ans = 1-getApproximationAccuracy(data,B,1,3);
			break;
		}
		case 5:{
			ans = 1-getApproximationAccuracy(data,B,1,4);
			break;
		}
		case 6:{
			ans = 1-getApproximationAccuracy(data,B,2,1);
			break;
		}
		case 7:{
			ans = 1-getApproximationAccuracy(data,B,2,2);
			break;
		}
		case 8:{
			ans = 1-getApproximationAccuracy(data,B,2,3);
			break;
		}
		case 9:{
			ans = 1-getApproximationAccuracy(data,B,2,4);
			break;
		}
		case 10:{
			ans = getConditionalEntropy_element(data,B,1);
			break;
		}
		case 11:{
			ans = getSU(data,B,1);
			break;
		}
		default:break;
		}
		return ans;
	}
	public static double getEvalution(Instances data, boolean[] B, int E_index){
		double ans = 0;
		switch(E_index){
		case 0:{
			ans = -getConditionalEntropy(data,B,1);
			break;
		}
		case 1:{
			ans = -getConditionalEntropy(data,B,2);
			break;
		}
		case 2:{
			ans = getDependennceDegree(data,B,1,1);
			break;
		}
		case 3:{
			ans = getDependennceDegree(data,B,1,2);
			break;
		}
		case 4:{
			ans = getDependennceDegree(data,B,1,3);
			break;
		}
		case 5:{
			ans = getDependennceDegree(data,B,1,4);
			break;
		}
		case 6:{
			ans = getDependennceDegree(data,B,2,1);
			break;
		}
		case 7:{
			ans = getDependennceDegree(data,B,2,2);
			break;
		}
		case 8:{
			ans = getDependennceDegree(data,B,2,3);
			break;
		}
		case 9:{
			ans = getDependennceDegree(data,B,2,4);
			break;
		}
		case 10:{
			ans = -getConditionalEntropy_element(data,B,1);
			break;
		}
		case 11:{
			ans = getSU(data,B,1);
			break;
		}
		default:break;
		}
		return ans;
	}
	
	public static double getConditionalEntropy_element(Instances data, boolean[] B, int T_index){
		double cnt = 0;
		boolean[][] T = getTrelation(data,B,T_index);
		boolean[] d = Utils.Instances2DecBoolean(data);
		boolean[][] D = getTrelation(data,d,T_index);;  //[U][U]
	
		int U = data.numInstances();
		for(int i=0;i<U;++i){
				double part1 = (double)getSetRank(ArraysIntersection(T[i],D[i])); //|T ^ D|
				double part2 = (double)getSetRank(T[i]);//|Sb|
				if(part2!=0)
					cnt += log2(part1/part2);
			
		}
		//double normalizing = U*log2(U);
		double normalizing = U;
		return -cnt/normalizing;
	}
	public static double getSU(Instances data, boolean[] B, int T_index){
		double cntCE = 0;
		double cntHB = 0;
		double cntHD = 0;
		boolean[][] T = getTrelation(data,B,T_index);
		boolean[] d = Utils.Instances2DecBoolean(data);
		boolean[][] Dj = SetTransposition(Utils_entropy.getEquivalenceClass(data,d));  //[U/d][U]

	
		int U = data.numInstances();
		int Ud = Dj.length;
		for(int i=0;i<U;++i){
			double part2 = (double)getSetRank(T[i]);//|Sb|			
			if(part2!=0){			
				for(int j=0;j<Ud;++j){
					double part1 = (double)getSetRank(ArraysIntersection(T[i],Dj[j])); //|T ^ Dj|
						cntCE -= part1*log2(part1/part2);
				}
				cntHB -= part2*log2(part2/U);	
			}	
		}
		for(int j=0;j<Ud;++j){
			double part3 = (double)getSetRank(Dj[j]);//|Di|
			if(part3!=0)
				cntHD -= part3*log2(part3/U);
		}
		return 1-2*(cntHD-(cntCE/(double)U))/(cntHB+cntHD);
		//return  cntCE/cntHD;
	}
	public static void showT(boolean[][] T,boolean[] B)
	{
		String tag="T_{";
		if(B[0]) tag+="P,";
		if(B[1]) tag+="M,";
		if(B[2]) tag+="S,";
		if(B[3]) tag+="X,";
		tag = tag.substring(0, tag.length()-1)+"}";
		for(int i=0;i<T.length;++i)
		{
			String str = tag+"(u_"+(i+1)+")=\\{";
			for(int j=0;j<T[i].length;++j)
				if(T[i][j])
					str+="u_"+(j+1)+",";
			str = str.substring(0, str.length()-1)+"\\}";
			System.out.println(str);
		}
		tag="\\{";
		if(B[0]) tag+="P,";
		if(B[1]) tag+="M,";
		if(B[2]) tag+="S,";
		if(B[3]) tag+="X,";
		tag = tag.substring(0, tag.length()-1)+"\\}";
		String h = "$H(d|"+tag+")=\\big[\\big]$";
		System.out.println(h);
	}
	public static double getConditionalEntropy(Instances data, boolean[] B, int T_index){
		double cnt = 0;
		boolean[][] T = getTrelation(data,B,T_index);
		boolean[] d = Utils.Instances2DecBoolean(data);
		boolean[][] Dj = SetTransposition(Utils_entropy.getEquivalenceClass(data,d));  //[U/d][U]
		//showT(T,B);
		
		
		
		int U = data.numInstances();
		int Ud = Dj.length;
		for(int i=0;i<U;++i){
			String str = "";
			double part2 = (double)getSetRank(T[i]);//|Sb|
			if(part2!=0)
				str = "\\frac{"+(int)part2+"}{"+U+"}*(";
			for(int j=0;j<Ud;++j){
				double part1 = (double)getSetRank(ArraysIntersection(T[i],Dj[j])); //|T ^ Dj|
				
				//System.out.println(i+":"+j+":"+part1+":"+part2);
				if(part2!=0&&part1!=0){
					cnt += part1*log2(part1/part2);
					 
					//str+="\\frac{"+(int)part1+"}{"+(int)part2+"}\\log\\frac{"+(int)part1+"}{"+(int)part2+"}+";	
				}
				
			}
			 
			//System.out.println(str.substring(0, str.length()-1)+")");
		}
		//double normalizing = U*log2(U);
		double normalizing = U;
		return -cnt/normalizing;
	}
	
	
	public static double getDependennceDegree(Instances data, boolean[] B, int T_index, int P_index){
		int U = data.numInstances();
		int rpos = Bottom_BD(data,B,T_index,P_index);
		return (double)rpos/(double)U;
	}
	public static double getApproximationAccuracy(Instances data, boolean[] B, int T_index, int P_index){
		boolean[][] T = getTrelation(data,B,T_index);
		boolean[] d = Utils.Instances2DecBoolean(data);
		boolean[][] Ud =  SetTransposition(Utils_entropy.getEquivalenceClass(data,d));  //[U/d][U]
		int cntB = 0;
		int cntT = 0;
		switch(P_index){
		case 1:{
			for(int j=0;j<Ud.length;++j){
				cntB = cntB + getSetRank(BottomX_1(T,Ud[j]));
				cntT = cntT + getSetRank(TopX_1(T,Ud[j]));
			}
			break;
			
		}
		case 2:{
			for(int j=0;j<Ud.length;++j){
				cntB = cntB + getSetRank(BottomX_2(T,Ud[j]));
				cntT = cntT + getSetRank(TopX_2(T,Ud[j]));
			}
			break;
		}
		case 3:{
			for(int j=0;j<Ud.length;++j){
				cntB = cntB + getSetRank(BottomX_3(T,Ud[j]));
				cntT = cntT + getSetRank(TopX_3(T,Ud[j]));
			}
			break;
		}
		case 4:{
			for(int j=0;j<Ud.length;++j){
				cntB = cntB + getSetRank(BottomX_4(T,Ud[j]));
				cntT = cntT + getSetRank(TopX_4(T,Ud[j]));
			}
			break;
		}
		default:break;
		}
		return (double)cntB/(double)cntT;
	}
	
	public static int Bottom_BD(Instances data, boolean[] B, int T_index, int P_index){
		boolean[][] T = getTrelation(data,B,T_index);
		boolean[] d = Utils.Instances2DecBoolean(data);
		boolean[][] Ud =  SetTransposition(Utils_entropy.getEquivalenceClass(data,d));  //[U/d][U]
		int cnt = 0;
		switch(P_index){
		case 1:{
			for(int j=0;j<Ud.length;++j){
				cnt = cnt + getSetRank(BottomX_1(T,Ud[j]));
			}
			break;
			
		}
		case 2:{
			for(int j=0;j<Ud.length;++j){
				cnt = cnt + getSetRank(BottomX_2(T,Ud[j]));
			}
			break;
		}
		case 3:{
			for(int j=0;j<Ud.length;++j){
				cnt = cnt + getSetRank(BottomX_3(T,Ud[j]));
			}
			break;
		}
		case 4:{
			for(int j=0;j<Ud.length;++j){
				cnt = cnt + getSetRank(BottomX_4(T,Ud[j]));
			}
			break;
		}
		default:break;
		}
		return cnt;
	}
	
	public static int Top_BD(Instances data, boolean[] B, int T_index, int P_index){
		boolean[][] T = getTrelation(data,B,T_index);
		boolean[] d = Utils.Instances2DecBoolean(data);
		int U = data.numInstances();

		
		boolean[][] Ud =  SetTransposition(Utils_entropy.getEquivalenceClass(data,d));  //[U/d][U]
		int cnt = 0;
		switch(P_index){
		case 1:{
			for(int j=0;j<Ud.length;++j){
				cnt = cnt + getSetRank(TopX_1(T,Ud[j]));
			}
			break;
			
		}
		case 2:{
			for(int j=0;j<Ud.length;++j){
				cnt = cnt + getSetRank(TopX_2(T,Ud[j]));
			}
			break;
		}
		case 3:{
			for(int j=0;j<Ud.length;++j){
				cnt = cnt + getSetRank(TopX_3(T,Ud[j]));
			}
			break;
		}
		case 4:{
			for(int j=0;j<Ud.length;++j){
				cnt = cnt + getSetRank(TopX_4(T,Ud[j]));
			}
			break;
		}
		default:break;
		}
		return cnt;
	}
	

	
	public static boolean[][] getTrelation(Instances data, boolean[] B, int T_index){
		int U = data.numInstances();
		boolean[][] Res = new boolean[U][U];
		for(int i=0;i<U;++i){
			for(int j=i;j<U;++j){
				Res[i][j]=true;
				Res[j][i]=true;
			}
		}
		switch(T_index){
		case 1:{
			for(int k=0;k<data.numAttributes();++k){
				if(B[k])
					for(int i=0;i<U;++i){
						for(int j=i;j<U;++j){

							if(Res[i][j]==true 
									&&data.instance(i).value(k)!=data.instance(j).value(k) //这里不能用data.instance(j).attribute(k)
									&&!data.instance(i).isMissing(k) 
									&&!data.instance(j).isMissing(k)){
								Res[i][j]=false;
								Res[j][i]=false;
							}
						}
					}
			}
			
			break;
		}
		case 2:{
			for(int k=0;k<data.numAttributes();++k){
				if(B[k])
					for(int i=0;i<U;++i){
						for(int j=0;j<U;++j){
							if(Res[i][j]==true 
									&&data.instance(i).value(k)!=data.instance(j).value(k)
									&&!data.instance(i).isMissing(k) ){
								Res[i][j]=false;
							}
						}
					}
			}
			break;
		}
		default:break;
		}
		return Res;
	}
	//第一种情况
	public static boolean[] BottomX_1(boolean[][] T, boolean[] X){
		int U = T.length;
		boolean[] Res = new boolean[U];
		for(int i=0;i<U;++i){
			if(isInclusion(T[i],X)){
				Res[i]=true;
			}
			else
				Res[i]=false;
		}
		return Res;
	}
	
	public static boolean[] TopX_1(boolean[][] T, boolean[] X){
		int U = T.length;
		boolean[] Res = new boolean[U];
		for(int i=0;i<U;++i){
			if(isIntersection(T[i],X)){
				Res[i]=true;
			}
			else
				Res[i]=false;
		}
		return Res;
	}
	
	//第二种情况
	public static boolean[] BottomX_2(boolean[][] T, boolean[] X){
		int U = T.length;
		boolean[] Xc = getComplement(X);
		boolean[] Res = getComplement(TopX_2(T,Xc));
		return Res;
	}
	
	public static boolean[] TopX_2(boolean[][] T, boolean[] X){
		return TopX_4(T,X);
	}
	
	
	
	//第三种情况
	public static boolean[] BottomX_3(boolean[][] T, boolean[] X){

		return BottomX_4(T,X);
	}
	
	public static boolean[] TopX_3(boolean[][] T, boolean[] X){
	
		int U = T.length;
		boolean[] Xc = getComplement(X);
		boolean[] Res = getComplement(BottomX_3(T,Xc));
		return Res;
	}
	
	
	
	
	//第四种情况
	public static boolean[] BottomX_4(boolean[][] T, boolean[] X){
		int U = T.length;
		boolean[] Res = new boolean[U];
		Arrays.fill(Res, false);
		for(int i=0;i<U;++i){
			if(isInclusion(T[i],X)){
				Res = getSetAddition(Res,T[i]).clone();
			}
		}
		return Res;
	}
	
	public static boolean[] TopX_4(boolean[][] T, boolean[] X){
	
		int U = T.length;
		boolean[] Res = new boolean[U];
		Arrays.fill(Res, false);
		for(int i=0;i<U;++i){
			if(isIntersection(T[i],X)){
				Res = getSetAddition(Res,T[i]).clone();
			}
		}
		return Res;
	}	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static int getSetRank(boolean[] X){
		int cnt=0;
		for(int i=0;i<X.length;++i){
			if(X[i])
				cnt++;
		}
		return cnt;
	}
	
	public static boolean[] getSetAddition (boolean[] s1, boolean[] s2){
		boolean[] Res = s1.clone();
		for(int i=0;i<s2.length;++i){
			if(s2[i]){
				Res[i]=true;
			}
		}
		return Res;
	}
	
	
	
	public static boolean[] getComplement(boolean[] X){
		int U = X.length;
		boolean[] Res = new boolean[U];
		for(int i=0;i<U;++i){
			if(X[i]==false){
				Res[i]=true;
			}
			else
				Res[i]=false;
		}
		return Res;
		
	}
	
	
	
	
	public static boolean isInclusion(boolean[] Tx, boolean[] X){
		int U = Tx.length;
		boolean tag = true;	
		for(int j=0;j<U;++j){
			if(Tx[j]==true && X[j]!=true){
				tag = false;
				break;
			}
		}
		return tag;
	}
	public static boolean[] ArraysIntersection(boolean[] Tx, boolean[] X){
		int U = Tx.length;
		boolean[] Res = new boolean[U];
		for(int i=0;i<U;++i){
			if(Tx[i]==true && X[i]==true){
				Res[i] = true;
			}
			else
				Res[i]=false;
		}
		return Res;
	}
	public static boolean isIntersection(boolean[] Tx, boolean[] X){
		int U = Tx.length;
		boolean tag = false;	
		for(int j=0;j<U;++j){
			if(Tx[j]==true && X[j]==true){
				tag = true;
				break;
			}
		}
		return tag;
	}
	private  static double log2(double x){
		if (x!=0)
			return Math.log(x) / Math.log((double)2);
			else return 0.0;
	}
	public static boolean[][] SetTransposition(boolean[][] X){
		int M = X.length;
		int N = X[0].length;
		boolean[][] newX = new boolean[N][M];
		for(int i=0;i<N;++i){
			for(int j=0;j<M;++j)
				newX[i][j]=X[j][i];
		}
		return newX;
	}
	/**
	 * @param args
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	public static void main(String[] args) throws FileNotFoundException, IOException {
		// TODO Auto-generated method stub
		//String datafn = "C:/Users/Eric/Desktop/2012春/Modification.NO3/Data/current data/Dermatology.arff";
		String datafn = "C:/Users/Eric/Desktop/2012春/Modification.No4-kbs/kbs_ex.arff";
		Instances data = new Instances(new FileReader(datafn));
		data.setClassIndex(data.numAttributes()-1); 
		
		int m = data.numAttributes();
		boolean[] B = new boolean[m];
// 		B[0] = true;
		B[1] = true;
		B[2] = true;
		B[3] = true;
		
		double ans = getUncertainty( data,  B, 0);
		System.out.println(ans);
		
		
		
		
	}

}
