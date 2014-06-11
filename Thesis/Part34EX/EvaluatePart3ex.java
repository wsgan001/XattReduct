package Part34EX;

import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import myUtils.xFigure;

import org.apache.commons.math.stat.descriptive.moment.Mean;

import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.trees.SimpleCart;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.ReplaceMissingValues;

import Part1.ClusterEnsemble;
import Part3.ClassifierEX;
import Part3.Semi_mRMR;
import PartAll.getDatas;

public class EvaluatePart3ex {
	public static void test(String fn,String name) throws Exception {
		Instances data = new Instances(new FileReader(fn));
		data.setClassIndex(data.numAttributes()-1);
		ReplaceMissingValues m_ReplaceMissingValues = new ReplaceMissingValues();
		try {
			m_ReplaceMissingValues.setInputFormat(data);
			data = Filter.useFilter(data, m_ReplaceMissingValues);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		weka.filters.supervised.attribute.Discretize sd = new weka.filters.supervised.attribute.Discretize();		 
		sd.setInputFormat(data);
		data = Filter.useFilter(data , sd);
		
		//NaiveBayes cla = new NaiveBayes();
		SimpleCart cla = new SimpleCart();
		double[] fullres = ClassifierEX.myEvolution(cla,data,10,5);
		Mean avg1 = new Mean();
		double base = avg1.evaluate(fullres);
		int N = 11;
		double alpha = 0.05;
		double [][] numReds = new double[2][N];
		double [][] ACs = new double[2][N];
		double [][] ACfull = new double[2][N];
		
		double [] EPSalpha = new double[N];
		double [] EPSselNumRed = new double[N];
		double [] EPSfullNumRed = new double[N];
		double [] EPSselAC = new double[N];
		double [] EPSfullAC = new double[N];
		for(int i=0;i<N;++i){
			boolean[] labels = ClusterEnsemble.getRandomLabeled(0.5,data.numInstances(),0);
			Semi_mRMR sm = new Semi_mRMR(data,"SemiMRMR",labels,alpha);
			numReds[0][i]=alpha;
			numReds[1][i]=sm.m_numRed;
			ACs[0][i]=alpha;
			ACfull[0][i]=alpha;
			double[] selres =ClassifierEX.myEvolution(cla,data,sm.getSelectedAtt(),10,5);
			Mean avg2 = new Mean();
			ACs[1][i]=avg2.evaluate(selres);
			ACfull[1][i]=base;
			
			EPSalpha[i]=alpha;
			EPSselNumRed[i]=sm.m_numRed;
			EPSfullNumRed[i] = (data.numAttributes()-1);
			EPSselAC[i]=ACs[1][i]*100;
			EPSfullAC[i]=base*100;
			
			
			
			
			alpha += 0.025;
			
		}
		Vector<Map<String,double[][]>> xydatas = new Vector<Map<String,double[][]>>();
		Map<String,double[][]> xydata1 = new HashMap<String,double[][]> ();
		xydata1.put("NumRed", numReds);
		Map<String,double[][]> xydata2 = new HashMap<String,double[][]> ();
		xydata2.put("SeleSet", ACs);
		xydata2.put("FullSet", ACfull);
		xydatas.add(xydata1);
		xydatas.add(xydata2);
		String[] titles = {name,"AC/NUM","alpha"};
		xFigure.showFigure(titles, xydatas);
		
		//生成eps文件
		Vector<double[]> epsDatas = new Vector<double[]>();
		epsDatas.add(EPSalpha);
		epsDatas.add(EPSselNumRed);
		epsDatas.add(EPSfullNumRed);
		epsDatas.add(EPSselAC);
		epsDatas.add(EPSfullAC);
		String epsPath = "C:\\Users\\Eric\\Desktop\\2012秋冬\\毕业设计\\Paper\\资料\\Part3\\gnuplot\\part3ex\\";
		String xlabel = "{/Symbol a}";
		Res2EPS.generateEpsForPart3ex(epsDatas, xlabel, epsPath, name,"part3_eva_cart_alpha");
		
		
	}
	
	public static void main(String[] args) throws Exception {
		 Vector<String[]> fns = getDatas.getFeatureSelectionDatas();
	
			for(int i=0;i<fns.size();++i){
				test(fns.elementAt(i)[0],fns.elementAt(i)[1]);
		
			}
	}

}
