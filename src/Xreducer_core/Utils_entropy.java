package Xreducer_core;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;

import Xreducer_gui.resultFrame;
import Xreducer_struct.oneAlgorithm.xStyle;
import weka.core.Instances;
import weka.filters.Filter;

public class Utils_entropy {
	
	//��ȼ���Xi
	//boolean[]X ������������
	//�ݲ�����missingֵ
	public static boolean[][] getEquivalenceClass(Instances dataset, boolean[] X){

		int U = dataset.numInstances();
		//��ȼ���,����ȼ۾���
		int[][] X_matrix = new int[U][U];
		int N = 0; //ͳ�Ƶȼ���ĸ���
		for(int i=0;i<U;++i){
			if(X_matrix[i][i]==0){
				N++;
				X_matrix[i][i] = 1;
				for(int j=i+1;j<U;++j){
					boolean flag = true;
					for(int k=0;k<X.length;++k){
						if(X[k]&&dataset.instance(i).value(k)!=dataset.instance(j).value(k)){
							flag = false;
							break;
						}
					}
					if (flag){
						X_matrix[i][j] = 1;
						X_matrix[j][j] = 2;
					}
				}
			}
		}
		//���ɵȼ���Xi
		boolean[][] Xi = new boolean[U][N];
		int n=0;
		for (int i=0;i<U;++i){
			if(X_matrix[i][i]==1){
				for (int j=0;j<U;++j){
					if(X_matrix[i][j]==1)
					Xi[j][n] = true ;
				}
				n++;
			}
		}
		return Xi;
	}
	
	//�õ����������Ӽ�B�����ȣ�granulation��(����)POS_D(B)  POS_bD
	//����B������������ pos=(U pos_By)/|U|

	public static double getGranulation(Instances dataset,boolean[] D, boolean[] B){
		boolean[][] Xi = getEquivalenceClass(dataset, B); //�������Եĵȼ���
		boolean[][] Yj = getEquivalenceClass(dataset, D); //�������Եĵȼ���
		
		int N = Xi[0].length;
		int U = Xi.length;
		int M = Yj[0].length;		
		//��������
		//��POS(B) ,pos_sum Ϊ|POS(B)|
		int pos_sum = 0;
		for(int i=0;i<N;++i){
			for(int j=0;j<M;++j){
				int cnt = 0;
				for(int k=0;k<U;++k){
					if(Xi[k][i] && Yj[k][j]){
						cnt++; //Xi \in Yj �ĸ��� [x]_B\in Yj
					}
					if(Xi[k][i] && !Yj[k][j]){//������
						cnt=0;
						break;
					}
				}
				pos_sum = pos_sum + cnt;
			}
		}
		return (double)pos_sum/U;	
//		int tmpsum = 0;
//		boolean[] tmp = new boolean[U];
//		for(int i=0;i<N;++i){
//			for(int j=0;j<M;++j){
//				int cnt = 0;
//				for(int k=0;k<U;++k){
//					if(Xi[k][i] && Yj[k][j]){
//						cnt++; //Xi \in Yj �ĸ��� [x]_B\in Yj
//					}
//					if(Xi[k][i] && !Yj[k][j]){//������
//						cnt=0;
//						break;
//					}
//				}
//				for(int k=0;k<U;++k){
//					if(Xi[k][i])
//						tmp[k]=true;
//				}
// 
//			}
//		}
//		for(int k=0;k<U;++k){
//			if(tmp[k])
//				tmpsum++;
//		}
//		return (double)tmpsum/U;		
	}
	//POS_pX
	public static boolean[] getPOS_pX(Instances dataset,boolean[] X, boolean[] P){
		boolean[][] Xi = getEquivalenceClass(dataset, P); 
		boolean[][] Yj = getEquivalenceClass(dataset, X); 
		int N = Xi[0].length;
		int U = Xi.length;
		int M = Yj[0].length;
		
		boolean[] pos_px =  new boolean[U];
		for(int i=0;i<N;++i){
			for(int j=0;j<M;++j){
				boolean[] px = new boolean[U];
				for(int k=0;k<U;++k){
					if(Xi[k][i] && Yj[k][j]){
						px[k]=true;
					}
					if(Xi[k][i] && !Yj[k][j]){ //������
						Arrays.fill(px, false);
						break;
					}					
				}
				pos_px = Utils.boolsAdd(pos_px, px);
			}
		}
		return pos_px;
	}
	//BND_pQ
	public static boolean[] getBND_pX(Instances dataset,boolean[] X, boolean[] P){
		boolean[][] Xi = getEquivalenceClass(dataset, P); 
		boolean[][] Yj = getEquivalenceClass(dataset, X); 
		int N = Xi[0].length;
		int U = Xi.length;
		int M = Yj[0].length;
		
		boolean[] L_px =  new boolean[U];
		for(int i=0;i<N;++i){
			for(int j=0;j<M;++j){
				boolean[] px = new boolean[U];
				for(int k=0;k<U;++k){
					if(Xi[k][i] && Yj[k][j]){
						px[k]=true;
					}
					if(Xi[k][i] && !Yj[k][j]){ //������
						Arrays.fill(px, false);
						break;
					}					
				}
				L_px = Utils.boolsAdd(L_px, px);
			}
		}
		boolean[] U_px =  new boolean[U];
		for(int i=0;i<N;++i){
			for(int j=0;j<M;++j){
				for(int k=0;k<U;++k){
					if(Xi[k][i] && Yj[k][j]){
						U_px = Utils.boolsAdd(U_px, Utils.boolean2oneEquivalence(Xi, i));
						break;
					}			
				}
			}
		}
		return Utils.boolsSubtract(U_px, L_px);
	}
	public static double[] getPOS_pmean(Instances dataset,boolean[] pos,boolean[] X, boolean[] P){
		int N = X.length;
		double[] Pmean = new double[N];
		int numpos = Utils.booleanSelectedNum(pos);
		if(numpos>=1){
			for(int i=0;i<N;++i){
				if(P[i]){//��i������
					double[] Temppx = new double[numpos];
					int p = 0;
					for(int k=0;k<pos.length;++k){
						if(pos[k]){ //k��������
							//sumpx = sumpx + dataset.instance(k).value(i);
							//System.out.println(k+","+i+":"+dataset.instance(k).value(i));
							Temppx[p++] = dataset.instance(k).value(i);
						}
					}
					Pmean[i] = Utils.PosmeanOperater(Temppx);
				}
			}
		}
		return Pmean;
	}
	public static double getDistanceMeasure_pQ(Instances dataset,boolean[] Q, boolean[] P){
		double WpQ = 0.0;
		boolean[] pos =  getPOS_pX(dataset,Q,P);
		//System.out.println(Arrays.toString(pos));
		if(Utils.isAllFalse(pos))
			return 0.0;
		if(Utils.isAllTrue(pos))
			return 1.0;
		double RpQ = getGranulation(dataset,Q,P);
		double[] Pmean = getPOS_pmean(dataset,pos,Q,P);
		//System.out.println(Arrays.toString(Pmean));
		boolean[] BND_pQ = getBND_pX(dataset,Q,P);
		double delta = 0.0;
		int U = dataset.numInstances();
		int N = dataset.numAttributes();
		for(int i=0;i<U;++i){
			if(BND_pQ[i]){  // i\in BND
				double delta_k = 0.0;
				for(int k=0;k<N;++k){
					if(P[k]){
						double fa = Utils.DistanceMetrics(Pmean[k], dataset.instance(i).value(k));
						delta_k = delta_k + Math.pow(fa,2);
					}
				}
				//System.out.println(i+":"+Math.sqrt(delta_k));
				delta = delta + Math.sqrt(delta_k);
			}
		}
		WpQ = delta!=0.0?1.0/delta:0.0;
		return (WpQ+RpQ)/(double)2;
		//return WpQ;
	}
	//����Ϣ�� information entropy
	//H(X) boolean[]X ������������
	public static double getInformationEntorpy(Instances dataset, boolean[] X){
		boolean[][] Xi = getEquivalenceClass(dataset, X);

		
		int U = Xi.length;
		int N = Xi[0].length;
		double res_XIH = 0.0;
		for(int i=0;i<N;++i){
			int sum=0;
			for(int k=0;k<U;++k){
				if(Xi[k][i])
				sum++;
			}
			if(sum!=(double)0){
				//System.out.println(sum+"/"+U);
				res_XIH=res_XIH-(sum/(double)U)* (Math.log((double)sum/U) / Math.log((double)2));
				//System.out.println((Math.log((double)sum/U) / Math.log((double)2)));
				//System.out.println(-(sum/(double)U)*(Math.log((double)sum/U) / Math.log((double)2)));
				//System.out.println(res_XIH);
			}
			
		}
		//res_XIH=(double)res_XIH/(double)U;
		return res_XIH;
	}
	
	//��������conditional entropy
	//H(X|Y) boolean[]X,Y ������������
	public static double getConditionalEntorpy(Instances dataset, boolean[] X, boolean[] Y){
		boolean[][] Xi = getEquivalenceClass(dataset, X);	
		boolean[][] Yj = getEquivalenceClass(dataset, Y);

		
		int U = Xi.length;
		int N = Xi[0].length;
		int M = Yj[0].length;
		//����������
		double res_entropy = 0.0;
		for (int i=0;i<N;++i){			
			for(int j=0; j<M;++j){			
				int XandYnum=0;
				int Ynum=0;
				for(int k=0;k<U;++k){
					if(Xi[k][i]&Yj[k][j]){
						//System.out.println("##"+k+":"+i+":"+j);
						XandYnum++;}
					if(Yj[k][j])
						
						Ynum++;
				}
				//System.out.println(XandYnum+":"+Ynum);
				if(XandYnum !=0)
					res_entropy=res_entropy-XandYnum* (Math.log((double)XandYnum/Ynum) / Math.log((double)2));
					//ע���(double) ����part1_sum/part2_sum = 0 ��Ϊ������
			}
		}
		
		res_entropy=(double)res_entropy/U;
		return res_entropy;			
	}

	//��joint entropy
	//H(X,Y)
	public static double getJointEntropy(Instances dataset, boolean[] X, boolean[] Y){
		double Hy = getInformationEntorpy(dataset, Y);
		double Hxy = getConditionalEntorpy(dataset, X, Y);
		return Hy+Hxy;
	}
	
	//��CA(Class-Attribute) mutual information
	//CA(X:Y)
	public static double getCA(Instances dataset, boolean[] X, boolean[] Y){
		boolean[][] Xi = getEquivalenceClass(dataset, X);	
		boolean[][] Yj = getEquivalenceClass(dataset, Y);
		
		int U = Xi.length;
		int N = Xi[0].length;
		int M = Yj[0].length;
		//����������
		double res_CA = 0.0;
		for (int i=0;i<N;++i){				
			for(int j=0; j<M;++j){
				int XandYnum=0;
				int Ynum=0;
				int Xnum=0;
				for(int k=0;k<U;++k){
					if(Xi[k][i]&Yj[k][j])
						XandYnum++;
					if(Yj[k][j])
						Ynum++;
					if(Xi[k][i])
						Xnum++;
				}
				if(XandYnum !=0)
					res_CA=res_CA+XandYnum* (Math.log((XandYnum*U*1.0)/(Ynum*Xnum*1.0)) / Math.log((double)2));
					//ע���(double) ����part1_sum/part2_sum = 0 ��Ϊ������
			}
		}
		
		res_CA=(double)res_CA/U;
		return res_CA;	
	}
	public static double ClassDependent(Instances dataset, boolean[] X, boolean[] Y) {
		// TODO Auto-generated method stub
		boolean[][] Xi = getEquivalenceClass(dataset, X);	
		boolean[][] Yj = getEquivalenceClass(dataset, Y);

		
		int U = Xi.length;
		int N = Xi[0].length;
		int M = Yj[0].length;
		//���㻥��Ϣ
		double res_entropy = 0.0;
		for (int i=0;i<N;++i){				
			for(int j=0; j<M;++j){
				int XandYnum=0;
				int Ynum=0;
				int Xnum=0;
				for(int k=0;k<U;++k){
					if(Xi[k][i]&Yj[k][j])
						XandYnum++;
					if(Xi[k][i])
						Xnum++;
					if(Yj[k][j])
						Ynum++;
				}
				if(XandYnum !=0)
					res_entropy=res_entropy+XandYnum* (Math.log((double)(XandYnum*U)/(double)(Ynum*Xnum)) / Math.log((double)2));
					//ע���(double) ����part1_sum/part2_sum = 0 ��Ϊ������ ���Ӹ���
			}
		}
		
		res_entropy=(double)res_entropy/U;
		//System.out.println(res_entropy);
		return res_entropy;	

	}
	//��CAIR Class-Attribute Interdependence Redundancy
	//CAIR(X,Y)=CA(X,Y)/H(X,Y)
	public static double getCAIR(Instances dataset, boolean[] X, boolean[] Y){
		double Hx_y = getJointEntropy(dataset,X,Y);
		double CA = getCA(dataset,X,Y);
		return CA/Hx_y;
	}
	
	//��information gain(mutual information)
	//IG(X|Y)
	public static double getIG(Instances dataset, boolean[] X, boolean[] Y){
		double Hx = getInformationEntorpy(dataset, X);
		double Hxy = getConditionalEntorpy(dataset, X, Y);
		return Hx-Hxy;
	}
	//��symmetrical uncertainty
	//SU(X,Y)
	public static double getSU(Instances dataset, boolean[] X, boolean[] Y){	
		boolean[][] Xi = getEquivalenceClass(dataset, X);
		boolean[][] Yj = getEquivalenceClass(dataset, Y);
		int U = Xi.length;
		int N = Xi[0].length;
		double Hx = 0.0;
		for(int i=0;i<N;++i){
			int sum=0;
			for(int k=0;k<U;++k){
				if(Xi[k][i])
				sum++;
			}
			if(sum!=(double)0){
				Hx -= sum* (Math.log((double)sum/U) / Math.log((double)2));}
		}
		Hx=(double)Hx/(double)U;
		U = Yj.length;
		N = Yj[0].length;
		double Hy = 0.0;
		for(int i=0;i<N;++i){
			int sum=0;
			for(int k=0;k<U;++k){
				if(Yj[k][i])
				sum++;
			}
			if(sum!=(double)0){
				Hy -= sum* (Math.log((double)sum/U) / Math.log((double)2));}
		}
		Hy=(double)Hy/(double)U;
		U = Xi.length;
		N = Xi[0].length;
		int M = Yj[0].length;
		//����������
		double res_entropy = 0.0;
		for (int i=0;i<N;++i){				
			for(int j=0; j<M;++j){
				int XandYnum=0;
				int Ynum=0;
				for(int k=0;k<U;++k){
					if(Xi[k][i]&Yj[k][j])
						XandYnum++;
					if(Yj[k][j])
						Ynum++;
				}
				if(XandYnum !=0)
					res_entropy=res_entropy-XandYnum* (Math.log((double)XandYnum/Ynum) / Math.log((double)2));
					//ע���(double) ����part1_sum/part2_sum = 0 ��Ϊ������
			}
		}
		
		res_entropy=(double)res_entropy/U;

		double Hxy = res_entropy;
		return 2.0*(Hx-Hxy)/(Hx+Hy);
	}
	public static void main(String[] args) throws Exception, IOException {
		// TODO Auto-generated method stub
		//String fn = "C:/Users/Eric/Desktop/2011�ﶬ/Code/Xreducer/data/fuzzy/fuzzy-ex2.arff";
		String fn = "C:/Users/Eric/Desktop/2011�ﶬ/Code/Xreducer/data/Data/sonar.arff";
		Instances data = new Instances(new FileReader(fn));
		data.setClassIndex(data.numAttributes()-1); //���þ�����������
		
		
		
		weka.filters.supervised.attribute.Discretize sd = new weka.filters.supervised.attribute.Discretize();
		try {
			sd.setInputFormat(data);
			data = Filter.useFilter(data , sd);
		} catch (Exception e) {
				// TODO Auto-generated catch block
			e.printStackTrace();}
			
			
		int att = data.numAttributes(); //����������
		boolean[] D = Utils.Instances2DecBoolean(data);
		boolean[] A = Utils.Instances2FullBoolean(data);
		boolean[] B = new boolean[att];
		//B[1] = true; //a
		//B[2] = true; //a
		B[1] = true; //a
		//B[3] = true; //a
		//B[21] = true; //b
		//B[26] = true; //b
		//B[33] = true; //b
		//B[2] = true; //c
		//B[3] = true; //d
		//boolean[] p = getPOS_pX(data, D, B);
		//System.out.println(Arrays.toString(p));
		//boolean[] b = getBND_pX(data, D, B);
		//System.out.println(Arrays.toString(b));
		double a1 = getGranulation(data,D,B);
		//double b1 = getCA(data,D,B);
		System.out.println(a1);
		
		
	}
	public static void show(boolean[][] x){
		int n = x.length;
		int m = x[0].length;
		for(int i=0;i<n;++i){
			String p = "";
			for(int j=0;j<m;++j){
				if(x[i][j])
					p = p+"1";
				else
					p = p+"0";
			}
			System.out.println(i+":"+p);
		}
		
	}


		
}
