package Part34EX;

import java.util.Vector;

import weka.core.Instances;
import Part3.ClassifierEX;
import Part3.ClustererEX;
import Part3.FSmethod;
import Part3.SemiClustererEX;

public class EvaluatePart1234ForAdata {
	public Instances data = null;
	public Instances datacluster = null;
	public FSmethod myMethod1 = null;
	public FSmethod myMethod2 = null;
	public Vector<SemiClustererEX> clu = null;
    public Vector<Vector<double[][]>> res_clu = null;
    public Vector<String> name_clu = null;
    public Vector<double[]> RunTimeDetail_Cluster = null;
    public EvaluatePart1234ForAdata(Instances data, Instances datacluster, FSmethod myMethod1, FSmethod myMethod2 , Vector<SemiClustererEX> clu) throws Exception{
    	this.myMethod1 = myMethod1;
    	this.myMethod2 = myMethod2;
		this.clu = clu;
		this.data = data;
		this.datacluster = datacluster;
		int n_Clu = this.clu.size();
		this.res_clu = new Vector<Vector<double[][]>>();
		this.name_clu = new Vector<String>();
		for(int i=0;i<n_Clu;++i){
			res_clu.add(clu.elementAt(i).getRes(data, datacluster, myMethod1.getSelectedAtt(), myMethod2.getSelectedAtt()));
			name_clu.add(clu.elementAt(i).algname);
		}
		this.ClaRunTime();
	 
    }
	private void ClaRunTime() {
		// TODO Auto-generated method stub

		this.RunTimeDetail_Cluster = new Vector<double[]>();
		int n_Clu = this.clu.size();
		double[] comRuntime = new double[3];
		comRuntime[0]=this.myMethod1.m_useTime;
		comRuntime[1]=this.myMethod2.m_useTime;	 
		this.RunTimeDetail_Cluster.add(comRuntime);
	 
		for(int i=0;i<n_Clu;++i){
			double[] Runtime = new double[3];
			Runtime[0] = this.clu.elementAt(i).seleTime1;
			Runtime[1] = this.clu.elementAt(i).seleTime2;
			Runtime[2] = this.clu.elementAt(i).fulltime;
			this.RunTimeDetail_Cluster.add(Runtime);
		}
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
