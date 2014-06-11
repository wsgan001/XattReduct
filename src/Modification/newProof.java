package Modification;

import java.util.Arrays;

public class newProof {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		double[][] ans1 = new double[21][21];
		double[][] ans2 = new double[21][21];
		double[][] ans3 = new double[21][21];
		double[][] ansTmp = new double[21][21];
		
		for(int i=0;i<21;i++){
			for(int j=0;j<21;j++){
				double x = i*0.05;
				double y = j*0.05;
				ans1[i][j]=Math.max(x+y-1,0);
				ans2[i][j]=Math.min(x,y);
				ans3[i][j]=x*y;
				ansTmp[i][j]=ans2[i][j]-ans3[i][j];
			}
		}
		for(int i=0;i<ans1.length;++i){
			System.out.println(Arrays.toString(ansTmp[i]));
		}
	}

}
