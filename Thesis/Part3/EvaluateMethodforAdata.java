package Part3;

import java.util.Vector;

import org.apache.commons.math.stat.descriptive.moment.Mean;
import org.apache.commons.math.stat.descriptive.moment.StandardDeviation;
import org.apache.commons.math.stat.inference.TestUtils;

import weka.classifiers.Classifier;
import weka.clusterers.Clusterer;
import weka.core.Instances;

public class EvaluateMethodforAdata {

	public Instances data = null;
	public Instances datacluster = null;
	public FSmethod myMethod = null;
	public Vector<ClassifierEX> cla = null;
	public Vector<ClustererEX> clu = null;
	public boolean isTtest = false;
    public Vector<double[][]> res_cla = null;
    public Vector<String> name_cla = null;
    public Vector<Vector<double[][]>> res_clu = null;
    public Vector<String> name_clu = null;
    public Vector<double[][]> ClaTtestres = null; 
    public Vector<double[]> RunTimeDetail_Classifier = null;
    public Vector<double[]> RunTimeDetail_Cluster = null;
    
	public EvaluateMethodforAdata(Instances data, Instances datacluster, FSmethod myMethod, Vector<ClassifierEX> cla, Vector<ClustererEX> clu, boolean isTtest) throws Exception{
		// Replace missing values   //被均值代替
		/*ReplaceMissingValues m_ReplaceMissingValues = new ReplaceMissingValues();
		m_ReplaceMissingValues.setInputFormat(m_data);
		m_data = Filter.useFilter(m_data, m_ReplaceMissingValues);*/
		this.myMethod = myMethod;
		this.cla = cla;
		this.clu = clu;
		this.isTtest = isTtest;
		this.data = data;
		this.datacluster = datacluster;
		int n_Cla = this.cla.size();
		int n_Clu = this.clu.size();
		this.res_cla = new Vector<double[][]>();
		this.res_clu = new Vector<Vector<double[][]>>();
		this.name_cla = new Vector<String>();
		this.name_clu = new Vector<String>();
		for(int i=0;i<n_Cla;++i){
			res_cla.add(cla.elementAt(i).getRes(data, myMethod.getSelectedAtt()));
			name_cla.add(cla.elementAt(i).algname);
		}
		this.ClaTTest();
		for(int i=0;i<n_Clu;++i){
			res_clu.add(clu.elementAt(i).getRes(data, datacluster, myMethod.getSelectedAtt()));
			name_clu.add(clu.elementAt(i).algname);
		}
		this.ClaRunTime();
	 
	}
 
	private void ClaRunTime() {
		// TODO Auto-generated method stub
		this.RunTimeDetail_Classifier = new Vector<double[]>();
		this.RunTimeDetail_Cluster = new Vector<double[]>();
		    
		    
		int n_Cla = this.cla.size();
		int n_Clu = this.clu.size();
		double[] comRuntime = new double[2];
		comRuntime[0]=this.myMethod.m_useTime;
		this.RunTimeDetail_Classifier.add(comRuntime);
		this.RunTimeDetail_Cluster.add(comRuntime);
		for(int i=0;i<n_Cla;++i){
			double[] Runtime = new double[2];
			Runtime[0] = this.cla.elementAt(i).seleTime;
			Runtime[1] = this.cla.elementAt(i).fulltime;
			this.RunTimeDetail_Classifier.add(Runtime);
		}
		for(int i=0;i<n_Clu;++i){
			double[] Runtime = new double[2];
			Runtime[0] = this.clu.elementAt(i).seleTime;
			Runtime[1] = this.clu.elementAt(i).fulltime;
			this.RunTimeDetail_Cluster.add(Runtime);
		}
	}

	private void ClaTTest() throws Exception {
		// TODO Auto-generated method stub
		this.ClaTtestres = myTtest(this.res_cla);
	}
	private static Vector<double[][]> myTtest(Vector<double[][]> res) throws Exception {
		// TODO Auto-generated method stub
		 
		double singificantlevel = 0.05;
		 Vector<double[][]> ans = new  Vector<double[][]> ();
		 for(int i=0;i<res.size();++i){
			 ans.add(MypairedTtest(res.elementAt(i),1,singificantlevel));
		 }
		 
		  
		return ans;
	}
	private static double[][] MypairedTtest(double[][] X, int baseindex , double singificantlevel) throws Exception{
		// 0 -avg 1-std 2-wtl(win-2 lose-1 tie-3 base- 0) 3 pval
		
		int N = X.length;
		//System.out.println("##################");
		//for(int i=0;i<X.length;++i)
			//System.out.println(Arrays.toString(X[i]));
    	//int M = X[0].length;
		double[][] res = new double[N][4];
		Mean mean = new Mean(); // 算术平均值
		//Variance variance = new Variance(); // 方差
		StandardDeviation sd = new StandardDeviation();// 标准方差
		//计算均值方差写入res[0] res[1]中
		for(int i=0;i<N;++i){
			res[i][0] = mean.evaluate(X[i]);
			res[i][1] = sd.evaluate(X[i]);
		}
		//DecimalFormat df = new DecimalFormat("0.000000");

		//进行paried t-test
		for(int i=0;i<N;++i){
			double pval;
			boolean H;
			if(i!=baseindex){
					pval = TestUtils.pairedTTest(X[baseindex], X[i]);
					H = TestUtils.pairedTTest(X[baseindex], X[i], singificantlevel);
					if(!H){//没有显著差异，平局
						res[i][2]=3;
						res[i][3]=pval;
					}
					else{//有显著差异, 均值大的获胜
						res[i][2]=res[baseindex][0]>=res[i][0]?1:2;
						res[i][3]=pval;
					}
					//String num = df.format(pval);
					//System.out.println(num);
			}
		}
		res[baseindex][2]=0;
		res[baseindex][3]=-1;
		return res;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
