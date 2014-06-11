package Modification;

import java.util.Arrays;

import weka.core.Instances;
import weka.datagenerators.classifiers.classification.RDG1;
import weka.datagenerators.classifiers.classification.RandomRBF;
import Xreducer_core.Utils;
import Xreducer_core.Utils_entropy;
import Xreducer_fuzzy.ITStyle_KleeneDienes;
import Xreducer_fuzzy.ITStyle_Lukasiewicz;
import Xreducer_fuzzy.ImplicatorTnormStyle;
import Xreducer_fuzzy.SStyle_Abs1lambda_VmaxVmin;
import Xreducer_fuzzy.SStyle_MaxMin;
import Xreducer_fuzzy.SimilarityStyle;

public class testUncertaintyforFRS {
	public SimilarityStyle m_sstyle = null;
	public ImplicatorTnormStyle m_itstyle = null;
	public Instances m_data = null;
	public testUncertaintyforFRS(Instances data,SimilarityStyle sstyle, ImplicatorTnormStyle itstyle) {
 		this.m_data = data;
		this.m_sstyle = sstyle;
		this.m_itstyle = itstyle;
	}
	public double getAccuracy(boolean[] B, boolean[] X,ImplicatorTnormStyle I, ImplicatorTnormStyle T){
		int N = X.length;
		int M = B.length;
		double[][] R = getSimilarityMatrix(B);
		double ansLower = 0;
		double ansUpper = 0;
		double[] TmpLower = new double[N];
		double[] TmpUpper = new double[N];
		for(int q=0;q<N;++q){
			double Lower = 1;
			double Upper = 0;
			for(int i=0;i<N;++i){
				double var = X[i]?1:0;
				double tmp1 = I.getfuzzyImplicatorValue(var, R[q][i]);
				Lower = Math.min(Lower, tmp1);
				double tmp2 = T.getfuzzyTnromValue(var, R[q][i]);
				Upper = Math.max(Upper, tmp2);
			}
			TmpLower[q]=Lower;
			TmpUpper[q]=Upper;
			ansLower+=Lower;
			ansUpper+=Upper;
		}
		//System.out.println("Lower"+Arrays.toString(TmpLower));
		//System.out.println("Upper"+Arrays.toString(TmpUpper));
		return ansLower/ansUpper;
	}
	public double getAccuracyCom(boolean[] B, boolean[] X,double alpha, double beta,ImplicatorTnormStyle I, ImplicatorTnormStyle T){
		int N = X.length;
		int M = B.length;
		double[][] R = getSimilarityMatrix(B);
		double ansLower = 0;
		double ansUpper = 0;
		double[] TmpLower = new double[N];
		double[] TmpUpper = new double[N];
		for(int q=0;q<N;++q){
			double Lower = 1;
			double Upper = 0;
			for(int i=0;i<N;++i){
				double var = X[i]?1:0;
				double tmp1 = I.getfuzzyImplicatorValue(var, R[q][i]);
				Lower = Math.min(Lower, tmp1);
				double tmp2 = T.getfuzzyTnromValue(var, R[q][i]);
				Upper = Math.max(Upper, tmp2);
			}
			TmpLower[q]=Lower;
			TmpUpper[q]=Upper;
			if(Lower>=alpha)
				ansLower++;
			if(Upper>=beta)
				ansUpper++;
		}
		//System.out.println("Lower"+Arrays.toString(TmpLower));
		//System.out.println("Upper"+Arrays.toString(TmpUpper));
		return ansLower/ansUpper;
	}
	public double getAccuracy(boolean[] B, double[] X,double alpha, double beta,ImplicatorTnormStyle I, ImplicatorTnormStyle T){
		int N = X.length;
		int M = B.length;
		double[][] R = getSimilarityMatrix(B);
		double ansLower = 0;
		double ansUpper = 0;
		double[] TmpLower = new double[N];
		double[] TmpUpper = new double[N];
		for(int q=0;q<N;++q){
			double Lower = 1;
			double Upper = 0;
			for(int i=0;i<N;++i){
				double tmp1 = I.getfuzzyImplicatorValue(X[i], R[q][i]);
				//if(q==0&&!B[1])
					//System.out.println(i+"----"+var+":"+R[q][i]+"="+tmp1);
				Lower = Math.min(Lower, tmp1);
				double tmp2 = T.getfuzzyTnromValue(X[i], R[q][i]);
				Upper = Math.max(Upper, tmp2);
				
					
			}
			TmpLower[q]=Lower;
			TmpUpper[q]=Upper;
			if(Lower>=alpha)
				ansLower+=Lower;
			if(Upper>=beta){
				ansUpper+=Upper;}
			//if(Lower>Upper)
				//System.out.println("Find:"+Lower+":"+Upper);
		}
		//if(alpha==0&&beta==1){
			//System.out.println("Lower"+Arrays.toString(TmpLower));
			//System.out.println("Upper"+Arrays.toString(TmpUpper));
		//}
		//if(ansLower/ansUpper>1)
			//System.out.println("Find:"+ansLower+":"+ansUpper);
		return ansLower/ansUpper;
	}
	public double getAccuracy(boolean[] B, boolean[] X,double alpha, double beta,ImplicatorTnormStyle I, ImplicatorTnormStyle T){
		int N = X.length;
		int M = B.length;
		double[][] R = getSimilarityMatrix(B);
		//System.out.println(Arrays.toString(R[3]));
		double ansLower = 0;
		double ansUpper = 0;
		double[] TmpLower = new double[N];
		double[] TmpUpper = new double[N];
		for(int q=0;q<N;++q){
			double Lower = 1;
			double Upper = 0;
			for(int i=0;i<N;++i){
				double var = X[i]?1:0;
				double tmp1 = I.getfuzzyImplicatorValue( var,R[q][i]);
				//if(q==0)
					//System.out.println(i+"----"+var+":"+R[q][i]+"="+tmp1);
				Lower = Math.min(Lower, tmp1);
				double tmp2 = T.getfuzzyTnromValue(var,R[q][i] );
				Upper = Math.max(Upper, tmp2);
				
					
			}
			TmpLower[q]=Lower;
			TmpUpper[q]=Upper;
			if(Lower>=alpha)
				ansLower+=Lower;
			if(Upper>=beta)
				ansUpper+=Upper;
			//if(Lower>=alpha&&Upper<beta)
				//System.out.println("Find:"+Lower+":"+Upper);
		}
		//if(alpha==0&&beta==1){
			//System.out.println("Lower"+Arrays.toString(TmpLower));
			//System.out.println("Upper"+Arrays.toString(TmpUpper));
		//}
		return ansLower/ansUpper;
	}
	public static boolean[][] getTransposeMatrix(boolean[][] R){
		boolean[][] newR = new boolean[R[0].length][R.length];
		for(int i=0;i<R.length;++i)
			for(int j=0;j<R[0].length;++j)
				newR[j][i]=R[i][j];
		return newR;
	}
	public double getAAsum(boolean[] B,double[] Xi, double alpha, double beta,ImplicatorTnormStyle I, ImplicatorTnormStyle T){
		int N = this.m_data.numInstances();
		int M = B.length;
		boolean[] D = Utils.Instances2DecBoolean(this.m_data);
		boolean[][] MRd = getTransposeMatrix(Utils_entropy.getEquivalenceClass(this.m_data,D));	 //J*U
		
		
		double[][] R = getSimilarityMatrix(B);
		double ansLower = 0;
		double ansUpper = 0;
		//double ansUpper = 0;
		for(int q=0;q<N;++q){
			double maxLower = 0;
			double maxUpper = 0;
			for(int j=0;j<MRd.length;++j){
				double Lower = 1;
				double Upper = 0;
				for(int i=0;i<N;++i){
					double var = MRd[j][i]?Xi[i]:0;
					double tmp1 = I.getfuzzyImplicatorValue(var, R[q][i]);
					Lower = Math.min(Lower, tmp1);
					double tmp2 = T.getfuzzyTnromValue(var, R[q][i]);
					 
					Upper = Math.max(Upper, tmp2);
				}
				if(Lower>=alpha)
					maxLower+=Lower;
				if(Upper>=beta)
					maxUpper+=Upper;
				//ansUpper+=Upper;
			}
			
			ansLower += maxLower;
			ansUpper += maxUpper;
		}
		
		return ansLower/ansUpper;
	}
	public double getAAsum(boolean[] B, double alpha, double beta,ImplicatorTnormStyle I, ImplicatorTnormStyle T){
		int N = this.m_data.numInstances();
		int M = B.length;
		boolean[] D = Utils.Instances2DecBoolean(this.m_data);
		boolean[][] MRd = getTransposeMatrix(Utils_entropy.getEquivalenceClass(this.m_data,D));	 //J*U
		
		
		double[][] R = getSimilarityMatrix(B);
		double ansLower = 0;
		double ansUpper = 0;
		//double ansUpper = 0;
		for(int q=0;q<N;++q){
			double maxLower = 0;
			double maxUpper = 0;
			for(int j=0;j<MRd.length;++j){
				double Lower = 1;
				double Upper = 0;
				for(int i=0;i<N;++i){
					double var = MRd[j][i]?1:0;
					double tmp1 = I.getfuzzyImplicatorValue(var, R[q][i]);
					Lower = Math.min(Lower, tmp1);
					double tmp2 = T.getfuzzyTnromValue(var, R[q][i]);
					 
					Upper = Math.max(Upper, tmp2);
				}
				if(Lower>=alpha)
					maxLower+=Lower;
				if(Upper>=beta)
					maxUpper+=Upper;
				//ansUpper+=Upper;
			}
			
			ansLower += maxLower;
			ansUpper += maxUpper;
		}
		
		return ansLower/ansUpper;
	}
	public double getAAsum_Com(boolean[] B,double alpha, double beta,ImplicatorTnormStyle I, ImplicatorTnormStyle T){
		int N = this.m_data.numInstances();
		int M = B.length;
		boolean[] D = Utils.Instances2DecBoolean(this.m_data);
		boolean[][] MRd = getTransposeMatrix(Utils_entropy.getEquivalenceClass(this.m_data,D));	 //J*U
		
		
		double[][] R = getSimilarityMatrix(B);
		double ansLower = 0;
		double ansUpper = 0;
		//double ansUpper = 0;
		for(int q=0;q<N;++q){
			double maxLower = 0;
			double maxUpper = 0;
			for(int j=0;j<MRd.length;++j){
				double Lower = 1;
				double Upper = 0;
				for(int i=0;i<N;++i){
					double var = MRd[j][i]?1:0;
					double tmp1 = I.getfuzzyImplicatorValue(var, R[q][i]);
					Lower = Math.min(Lower, tmp1);
					double tmp2 = T.getfuzzyTnromValue(var, R[q][i]);
					 
					Upper = Math.max(Upper, tmp2);
				}
				if(Lower>=alpha)
					maxLower++;
				if(Upper>=beta)
					maxUpper++;
				//ansUpper+=Upper;
			}
			
			ansLower += maxLower;
			ansUpper += maxUpper;
		}
		
		return ansLower/ansUpper;
	}
	public double getAAsum(boolean[] B,ImplicatorTnormStyle I, ImplicatorTnormStyle T){
		int N = this.m_data.numInstances();
		int M = B.length;
		boolean[] D = Utils.Instances2DecBoolean(this.m_data);
		boolean[][] MRd = getTransposeMatrix(Utils_entropy.getEquivalenceClass(this.m_data,D));	 //J*U
		
		
		double[][] R = getSimilarityMatrix(B);
		double ansLower = 0;
		double ansUpper = 0;
		for(int j=0;j<MRd.length;++j){
			for(int q=0;q<N;++q){
				double Lower = 1;
				double Upper = 0;
				for(int i=0;i<N;++i){
					double var = MRd[j][i]?1:0;
					double tmp1 = I.getfuzzyImplicatorValue(var, R[q][i]);
					Lower = Math.min(Lower, tmp1);
					double tmp2 = T.getfuzzyTnromValue(var, R[q][i]);
			 
					Upper = Math.max(Upper, tmp2);
				}
				ansLower+=Lower;
				ansUpper+=Upper;
			}
		}
		
		return ansLower/ansUpper;
	}
	public double getAAsup(boolean[] B,ImplicatorTnormStyle I, ImplicatorTnormStyle T){
		int N = this.m_data.numInstances();
		int M = B.length;
		boolean[] D = Utils.Instances2DecBoolean(this.m_data);
		boolean[][] MRd = getTransposeMatrix(Utils_entropy.getEquivalenceClass(this.m_data,D));	 //J*U
		
		
		double[][] R = getSimilarityMatrix(B);
		double ansLower = 0;
		double ansUpper = 0;
		//double ansUpper = 0;
		for(int q=0;q<N;++q){
			double maxLower = 0;
			double maxUpper = 0;
			for(int j=0;j<MRd.length;++j){
				double Lower = 1;
				double Upper = 0;
				for(int i=0;i<N;++i){
					double var = MRd[j][i]?1:0;
					double tmp1 = I.getfuzzyImplicatorValue(var, R[q][i]);
					Lower = Math.min(Lower, tmp1);
					double tmp2 = T.getfuzzyTnromValue(var, R[q][i]);
					 
					Upper = Math.max(Upper, tmp2);
				}
				if(maxLower<Lower)
					maxLower=Lower;
				if(maxUpper<Upper)
					maxUpper=Upper;
				//ansUpper+=Upper;
			}
			ansLower += maxLower;
			ansUpper += maxUpper;
		}
		
		return ansLower/ansUpper;
	}
	
	private double[][] getSimilarityMatrix(boolean[] B) {
		// TODO Auto-generated method stub
		int U = this.m_data.numInstances();
		double[][] R = new double[U][U];
		for(int i=0;i<R.length;++i){
			Arrays.fill(R[i], 1.0);
		}
		
		
		double[][] vasStatistics = new double[Utils.booleanSelectedNum(B)][8];
		int vsIndex = 0;
		for(int k=0;k<B.length;++k){
			if(B[k]&&this.m_data.attribute(k).isNumeric()){
				vasStatistics[vsIndex][0] = 0;
				vasStatistics[vsIndex][1] = k;
				double[] Vas = this.m_data.attributeToDoubleArray(k);			
				double[] temp = Utils.getStatisticsValue(Vas);
				vasStatistics[vsIndex][2] = temp[0];
				vasStatistics[vsIndex][3] = temp[1];
				vasStatistics[vsIndex][4] = temp[2];
				vasStatistics[vsIndex][5] = temp[3];
				vasStatistics[vsIndex][6] = temp[4];
				vasStatistics[vsIndex][7] = temp[5];
				vsIndex++;
			}
			else if(B[k]&&!this.m_data.attribute(k).isNumeric()){			
				vasStatistics[vsIndex][0] = 1;
				vasStatistics[vsIndex][1] = k;
				vsIndex++;
			}
		}
		for(int k=0;k<vasStatistics.length;++k){
			if(vasStatistics[k][0]==0){
				int pIndex = (int)vasStatistics[k][1];
				this.m_sstyle.SimilaritySetting(vasStatistics[k][3],vasStatistics[k][4],vasStatistics[k][7]);
				for(int i=0;i<U;++i){
					R[i][i] = 1;
					for(int j=i+1;j<U;++j){
						double temp = this.m_sstyle.getSimilarityValue(this.m_data.instance(i).value(pIndex),this.m_data.instance(j).value(pIndex));
						R[i][j] = this.m_itstyle.getfuzzyTnromValue(R[i][j],temp);
					}
				} 			
			}
			else{
				int pIndex = (int)vasStatistics[k][1];
				
				for(int i=0;i<U;++i){
					R[i][i] = 1;
					for(int j=i+1;j<U;++j){
						double temp = this.m_data.instance(i).value(pIndex)==this.m_data.instance(j).value(pIndex)?1:0;
						R[i][j] = this.m_itstyle.getfuzzyTnromValue(R[i][j],temp);
					}
				}	
			}
	 
		}
		
	 
		for(int i=0;i<U;++i){
			for(int j=i;j<U;++j){
				R[j][i]=R[i][j];
			}
		}
		
		return R;
	}
	public static Instances generateInstances(int n,int m,int c,int caseind, int ind ) throws Exception{
		Instances data = null;
		if(caseind==0){
			RDG1 rdg =new RDG1();
			rdg.setNumAttributes(m);
			rdg.setNumExamples(n);
			rdg.setNumClasses(c);
			rdg.setSeed(ind);
			data = rdg.defineDataFormat(); //要先defineDataFormat
			data = rdg.generateExamples();
		}
		else{
			RandomRBF rbf = new RandomRBF();
			rbf.setNumAttributes(m);
			rbf.setNumExamples(n);
			rbf.setNumClasses(c);
			rbf.setSeed(ind);
			data = rbf.defineDataFormat(); //要先defineDataFormat
			data = rbf.generateExamples();
		}
		 
		
		data.setClassIndex(data.numAttributes()-1);
		return data;
	}
	public static void main(String[] args) throws Exception {
		//Q P for roughness
	 	int N = 2;
		for(int i=1;i<N;++i){
			Instances data = generateInstances(1000,25,5,1,i);
			//SimilarityStyle sstyle = new SStyle_MaxMin();
			SimilarityStyle sstyle = new SStyle_Abs1lambda_VmaxVmin(4);
			
			//ImplicatorTnormStyle itstyle = new ITStyle_KleeneDienes(); 
			ImplicatorTnormStyle itstyle = new ITStyle_Lukasiewicz();
			testUncertaintyforFRS ts = new testUncertaintyforFRS(data,sstyle,itstyle);
			boolean B[] = new boolean[data.numAttributes()];
			boolean[] D = Utils.Instances2DecBoolean(data);
			boolean[][] MRd = getTransposeMatrix(Utils_entropy.getEquivalenceClass(data,D));	 //J*U
			double preans = 0;
			for(int k=0;k<data.numAttributes()-1;++k){
				B[k]=true;
				
				//double ans = ts.getAccuracy(B, MRd[0]);
				//System.out.println(ans);
				//if(preans>ans){
				//	System.out.println("Not OK");
				//}
				//preans = ans;
			}
			//System.out.println(i);
		} 
		// A for roughness
	 	/*int N = 100;
		for(int i=0;i<N;++i){
			Instances data = generateInstances(1000,25,5,1,i);
			//SimilarityStyle sstyle = new SStyle_MaxMin();
			SimilarityStyle sstyle = new SStyle_Abs1lambda_VmaxVmin(4);
			
			ImplicatorTnormStyle itstyle = new ITStyle_KleeneDienes(); 
			//ImplicatorTnormStyle itstyle = new ITStyle_Lukasiewicz();
			testUncertaintyforFRS ts = new testUncertaintyforFRS(data,sstyle,itstyle);
			boolean B[] = new boolean[data.numAttributes()];
			boolean[] D = Utils.Instances2DecBoolean(data);
			 
			double preans = 0;
			for(int k=0;k<(data.numAttributes()-1)/2;++k){
				B[k]=true;
				
			}
			boolean[] X = new boolean[data.numInstances()];
			for(int u=0;u<data.numInstances()/50;++u){
				for(int x=u*(data.numInstances()/20);x<(u+1)*(data.numInstances()/20);++x)
					X[x]=true;
				
				double ans = ts.getLowerUpper(B, X);
				System.out.println(ans);
				if(preans>ans){
					System.out.println("Not OK");
				}
				preans = ans;
			}
			
		
		}
			*/
		
		
		/*
		int N = 100;
		for(int i=0;i<N;++i){
			Instances data = generateInstances(1000,25,5,1,i);
			//SimilarityStyle sstyle = new SStyle_MaxMin();
			SimilarityStyle sstyle = new SStyle_Abs1lambda_VmaxVmin(4);
			
			ImplicatorTnormStyle itstyle = new ITStyle_KleeneDienes(); 
			//ImplicatorTnormStyle itstyle = new ITStyle_Lukasiewicz();
			testUncertaintyforFRS ts = new testUncertaintyforFRS(data,sstyle,itstyle);
			boolean B[] = new boolean[data.numAttributes()];
			boolean[] D = Utils.Instances2DecBoolean(data);
			 
			double preans = 0;
			for(int k=0;k<(data.numAttributes()-1)/2;++k){
				B[k]=true;
				
			}
			boolean[] X = new boolean[data.numInstances()];
			for(int u=0;u<data.numInstances();++u){
				
					X[u]=true;
				
				double ans = ts.getLowerUpper(B, X);
				System.out.println(ans);
				if(preans>ans){
					System.out.println("Not OK");
				}
				preans = ans;
			}
			
		
		}*/
			//System.out.println(i);
		  
		
		/*
		int N = 1;
		for(int i=0;i<N;++i){
			Instances data = generateInstances(1000,25,5,0,i);
			SimilarityStyle sstyle = new SStyle_MaxMin();
			//SimilarityStyle sstyle = new SStyle_Abs1lambda_VmaxVmin(4);
			
			//ImplicatorTnormStyle itstyle = new ITStyle_KleeneDienes(); 
			ImplicatorTnormStyle itstyle = new ITStyle_Lukasiewicz();
			testUncertaintyforFRS ts = new testUncertaintyforFRS(data,sstyle,itstyle);
			boolean B[] = new boolean[data.numAttributes()];
			//boolean[] D = Utils.Instances2DecBoolean(data);
			//boolean[][] MRd = getTransposeMatrix(Utils_entropy.getEquivalenceClass(data,D));	 //J*U
			double preans = 0;
			for(int k=0;k<data.numAttributes()-1;++k){
				B[k]=true;
				
				
				double ans = ts.getAAsup(B);
				//double ans = ts.getLowerUpperAccuracy(B);
				System.out.println(ans);
				if(preans>ans){
					System.out.println("Not OK");
				}
				preans = ans;
			}
			System.out.println(i);
		}
		
		*/
		
		
		

		
		
		
		
}
}
