package CheckMonotony;

import java.util.Arrays;
import java.util.Random;

import Xreducer_core.Utils_entropy;

import weka.core.Instances;

public class newCheck {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		for(int i=0;i<1000;++i){
			if(newcheckdata(4,i)){
				System.out.println("Find");
				}
		}
		
		System.out.println("Done");

	}
	public static boolean newcheckdata(int n, int ind){
		double[] res = new double[3];
		Random rd = new Random(ind);
		double[] ans1 = new double[3];
		int N = n;
		double[][] SD = new double[N][N];
		for(int i=0;i<N/2;++i){
			for(int j=0;j<N/2;++j){
				SD[i][j]=1;
				SD[j][i]=1;
				SD[i+N/2][j+N/2]=1;
				SD[j+N/2][i+N/2]=1;
			}
		}
		double[][][] SB = new double[3][N][N];
		for(int i=0;i<N;++i){
			for(int j=0;j<N;++j){
				if(i==j){
					SB[0][i][j]=1;
					}
					else{
						SB[0][i][j]=rd.nextBoolean()==true?1:0;
						SB[0][j][i]=SB[0][i][j];
					}
			}
		}		
		ans1[0] =  Element_Covering(SD,SB[0]);
		//System.out.println(ans1[0]);
		for(int i=0;i<N;++i){
			for(int j=i;j<N;++j){
				if(i==j){
				SB[1][i][j]=1;
				}
				else{
					SB[1][i][j]=rd.nextBoolean()==true?1:0;
					SB[1][j][i]=SB[1][i][j];
				}
			}
		}
		ans1[1] =  Element_Covering(SD,SB[1]);
		//System.out.println(ans1[1]);

		for(int i=0;i<N;++i){
			for(int j=i;j<N;++j){
				if(i==j){
				SB[2][i][j]=1;
				}
				else{
					SB[2][i][j]=rd.nextBoolean()==true?1:0;
					SB[2][j][i]=SB[2][i][j];
				}
			}
		}
		ans1[2] =  Element_Covering(SD,SB[2]);		
		//System.out.println(ans1[2]);
		//System.out.println(FindMax(ans1));
		int k = FindMinind(ans1);
		
		
		double[] ans2 = new double[2];
		double[][][] SB2 = new double[2][N][N];
		if(k==0){
			SB2[0]=SumIn(SB[0],SB[1]);
			SB2[1]=SumIn(SB[0],SB[2]);
		}
		else if(k==1){
			SB2[0]=SumIn(SB[1],SB[0]);
			SB2[1]=SumIn(SB[1],SB[2]);
		}
		else {
			SB2[0]=SumIn(SB[2],SB[0]);
			SB2[1]=SumIn(SB[2],SB[1]);
		}
		ans2[0]=Element_Covering(SD,SB2[0]);
		ans2[1]=Element_Covering(SD,SB2[1]);
		
		//System.out.println(FindMax(ans2));
		
		double[][] SB3= SumIn(SumIn(SB[0],SB[1]),SB[2]);
		double ans3 = Element_Covering(SD,SB3);
		
		res[0] = FindMin(ans1);
		res[1] = FindMin(ans2);
		res[2] = ans3;
		
		
		
		
		// new
		
		
		double [] ansNew = new double[3];
		ansNew[0]= New_Covering(SB[0]);
		ansNew[1]= New_Covering(SB[1]);
		ansNew[2]= New_Covering(SB[2]);
		int knew = FindMinind(ansNew);
		
		
		double[] ans2New = new double[2];
		double[][][] SB2new = new double[2][N][N];
		if(knew==0){
			SB2new[0]=SumIn(SB[0],SB[1]);
			SB2new[1]=SumIn(SB[0],SB[2]);
		}
		else if(knew==1){
			SB2new[0]=SumIn(SB[1],SB[0]);
			SB2new[1]=SumIn(SB[1],SB[2]);
		}
		else {
			SB2new[0]=SumIn(SB[2],SB[0]);
			SB2new[1]=SumIn(SB[2],SB[1]);
		}
		ans2New[0]=New_Covering(SB2new[0]);
		ans2New[1]=New_Covering(SB2new[1]);
		
		//System.out.println(FindMax(ans2));
		
		double[][] SB3new= SumIn(SumIn(SB[0],SB[1]),SB[2]);
		double ans3new = New_Covering(SB3new);
		double[] resNew = new double[3];
		resNew[0] = FindMin(ansNew);
		resNew[1] = FindMin(ans2New);
		resNew[2] = ans3new;
		
	
		
		
		
		//System.out.println(Arrays.toString(res));
		if(checkM(res)&&checkMnew(resNew)){
			System.out.println(Arrays.toString(res));
			System.out.println(Arrays.toString(resNew));
			System.out.println("Sim1:");
			for(int i=0;i<N;++i){
				
				System.out.println(Arrays.toString(SB[0][i]));
			}
			System.out.println("Sim2:");
			for(int i=0;i<N;++i){
				
				System.out.println(Arrays.toString(SB[1][i]));
			}
			System.out.println("Sim3:");
			for(int i=0;i<N;++i){
				
				System.out.println(Arrays.toString(SB[2][i]));
			}
		}
		return checkM(res);

		
	}
	private static boolean checkMnew(double[] xx) {
		// TODO Auto-generated method stub
		int k = xx.length;
		/*boolean tag = false;
		for(int i=0;i<k-1;++i){
			if(xx[i]<xx[i+1]){
				tag = true;
				break;
			}
		}
		return tag;*/
		if(xx[k-1]==xx[k-2]){
			return true;
		}
		return false;
	}
	static boolean checkM(double[] xx){
		int k = xx.length;
		/*boolean tag = false;
		for(int i=0;i<k-1;++i){
			if(xx[i]<xx[i+1]){
				tag = true;
				break;
			}
		}
		return tag;*/
		if(xx[k-1]>xx[k-2]&&xx[0]>xx[1]){
			return true;
		}
		return false;
	}
	static double[][] SumIn(double[][] sb1,double[][] sb2){
		double[][] res = new double[sb1.length][sb1.length];
		for(int i=0;i<sb1.length;++i){
			for(int j=0;j<sb1.length;++j){
				res[i][j]=Math.min(sb1[i][j], sb2[i][j]);
			}
		}
		return res;
	}
	static int FindMinind(double[] ans){
		double res = 1000000;
		int ind = -1;
		for(int i=0;i<ans.length;++i){
			if(ans[i]<res){
				res = ans[i];
				ind = i;
			}
		}
		return ind;
	}
	static double FindMin(double[] ans){
		double res = 100000;
		for(int i=0;i<ans.length;++i){
			if(ans[i]<res){
				res = ans[i];
			}
		}
		return res;
	}
	static double New_Covering(double[][] Sb){
		double ans = 0;
		int U = Sb.length;
		double[][] Dj = new double[2][U];
		for(int i=0;i<U/2;++i){
			Dj[0][i]=1;
		}
		for(int i=U/2;i<U;++i){
			Dj[1][i]=1;
		}

		int Ud = Dj.length;
		for(int i=0;i<U;++i){
			for(int j=0;j<Ud;++j){
				double part1 = doubleArrays2Sum(ArraysIntersection(Sb[i],Dj[j])); //|Sb ^ Dj|
				double part2 = doubleArrays2Sum(Sb[i]);//|Sb|
				if(part2!=0)
				ans += part1*log2(part1/part2);
			}
		}
				
		return -ans/(double)U;
	}
	static double Element_Covering( double[][] Sd,double[][] Sb){
		double ans = 0;
		int U = Sd.length;	
		for(int i=0;i<U;++i){

				double part1 = doubleArrays2Sum(ArraysIntersection(Sb[i],Sd[i])); //|Sb ^ Sd|
				double part2 = doubleArrays2Sum(Sb[i]);//|Sb|
				if(part2!=0)
				ans += log2(part1/part2);			
		}
			
		return -ans/(double)U;
	}
	private static double doubleArrays2Sum (double[] X){
		int U = X.length;
		double ans = 0;
		
		for(int i=0;i<U;++i){
			ans += X[i];
		}
		return ans;
	}
	private  static double log2(double x){
		if (x!=0)
			return Math.log(x) / Math.log((double)2);
			else return 0.0;
	}
	private static double[] ArraysIntersection(double[] X, double[] Y){
		int U = X.length;
		double[] ans = new double[U];
		for(int i=0;i<U;++i){
			ans[i] = Math.min(X[i], Y[i]);
		}
		
		return ans;
	}
}
