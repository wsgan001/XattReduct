package myUtils;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Vector;

public class xMath {
	public static boolean isMonotony(double[] X){
		boolean stag = X[0]>=X[X.length-1];
		for(int i=0;i<X.length-1;++i){
			if(X[i]==X[i+1])
				continue;
			else if(stag!=(X[i]>X[i+1]))
				return false;
		}
		return true;
	}
	public static boolean isHasEqual(double[] X){
		for(int i=0;i<X.length-1;++i){
			if(X[i]==X[i+1])
				return true;
		}
		return false;
	}
	public static boolean isStrictMonotony(double[] X){
		if(X[0]==X[X.length-1])
			return false;
		boolean stag = X[0]>X[X.length-1];
		for(int i=0;i<X.length-1;++i){
			if(X[i]==X[i+1] || stag!=(X[i]>X[i+1]))
				return false;
		}
		return true;
	}
	public static int boolTrueNum(boolean[] X){
		int cnt = 0;
		for(int i=0;i<X.length;++i){
			if(X[i])
				cnt++;
		}
		return cnt;
	}
	public static double getDoubleRound(int i, double x){
		 
			BigDecimal mData = new BigDecimal(x).setScale(i, BigDecimal.ROUND_HALF_UP);
			return mData.doubleValue();


		 
		     
	}
	public static String doubleFormat(String str,double x){
		return new DecimalFormat(str).format(x);
	}
	 public static Vector<int[]> combination(int A,int B) {/*求解C(A,B)*/  //C^B_A
	    	Vector<int[]>  result= new Vector<int[]>();
	    	int[] tmp = new int[B];
	    	subselect(A, result, 0, 1, tmp, B);  
	    	return result;
	    	
	}
	public static void subselect(int A, Vector<int[]> res, int head, int index, int[] tmp, int k) { 
			
			   for (int i=head; i<A+index-k; i++) 
			   { 
				    if(index<k)
				    { 
				    	tmp[index-1]=i; 
					    subselect(A,res, i+1, index+1, tmp, k); 
				    } 
				    if(index==k) 
				    { 
					    tmp[index-1] = i; 
					    res.add(tmp.clone());//这里加clone 否则add(tmp) 只是添加了引用
				    }			   
			  }			 
	}
	   public static void main(String[] args) {
		   
		   System.out.println(new   DecimalFormat( "###0.00").format(0.05));
	   }
}
