package Part3;
 
import weka.classifiers.Classifier;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Remove;

public class ClassifierEX {

	public int runtime = -1;
	public int foldnum = -1;
	public Classifier cla = null;
	public Instances data = null;
	public int[] seletatts = null;
	public double[][] res = null;
	public String algname = null;
	public double seleTime = 0;
	public double fulltime = 0;
	public ClassifierEX(Classifier cla, String name, int runtime, int foldnum){
		this.runtime = runtime;
		this.foldnum = foldnum;
		res = new double[2][this.runtime];
		this.cla = cla;
		this.algname = name;
	}
	public double[][] getRes(Instances data, int[] seletatts) throws Exception{
		this.data = data;
		this.seletatts = seletatts.clone();
		return this.getEvalution();
	}
	private double[][] getEvalution() throws Exception {
		// TODO Auto-generated method stub
		long time_start = Utils.getCurrenttime();
		double[] selres = myEvolution(this.cla,this.data,this.seletatts,this.foldnum,this.runtime);
		this.seleTime = (Utils.getCurrenttime() - time_start)/(double)1000;
		time_start = Utils.getCurrenttime();
		double[] fullres = myEvolution(this.cla,this.data,this.foldnum,this.runtime);
		this.fulltime = (Utils.getCurrenttime() - time_start)/(double)1000;
		this.seleTime = this.seleTime /(double)this.runtime;
		this.fulltime = this.fulltime /(double)this.runtime;
		for(int i=0;i<this.runtime;++i){
			this.res[0][i] = selres[i];
			this.res[1][i] = fullres[i];
		}
		return this.res;
	}
	public static double[] myEvolution(Classifier cla2, Instances data2,
			int foldnum2, int runtime2) throws Exception {
		Instances newData = new Instances(data2);
		newData.setClassIndex( newData.numAttributes() - 1 ); //重新设置决策属性索引
		// TODO Auto-generated method stub
		return Utils.multicrossValidateModel(cla2, newData, runtime2, foldnum2 );
	}
	public static double[] myEvolution(Classifier cl,Instances data,int[] seletatts, int fold , int runtime) throws Exception{
		int [] reAttr = Utils.seletatt2removeAtt(seletatts);
		Remove m_removeFilter = new Remove();
		m_removeFilter.setAttributeIndicesArray(reAttr);
		m_removeFilter.setInvertSelection(false);
    	m_removeFilter.setInputFormat(data);   
    	Instances newData = Filter.useFilter(data, m_removeFilter);
    	newData.setClassIndex( newData.numAttributes() - 1 ); //重新设置决策属性索引
    	double[] ans = Utils.multicrossValidateModel(cl, newData, runtime, fold );
		return ans;
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
