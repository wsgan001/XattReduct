package FCBFandRS;

import java.io.FileReader;

import Xreducer_core.Utils;

import weka.attributeSelection.ASEvaluation;
import weka.attributeSelection.ASSearch;
import weka.attributeSelection.AttributeSelection;
import weka.attributeSelection.ConsistencySubsetEval;
import weka.attributeSelection.GreedyStepwise;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.ReplaceMissingValues;

public class Wekamethod extends FSmethod {
	private AttributeSelection  AS = null;
	private ASEvaluation eval = null;
	private ASSearch search = null;
	
	public Wekamethod(Instances data,String algname,ASEvaluation eval,ASSearch search) {
		super(data);
		this.algname = algname;
		this.eval = eval;
		this.search = search;
	
		
		this.m_selectAtt = getSelectedAtt();
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean[] getReduceAtt() {
		// TODO Auto-generated method stub
		boolean[] newB = new boolean[this.m_data.numAttributes()];
		int[] selectedAtt = null;
		
		long time_start = Utils.getCurrenttime();
		AS = new AttributeSelection();
		try {
			AS.setEvaluator(eval);  
			AS.setSearch(search); 
			AS.SelectAttributes(this.m_data);
			selectedAtt = AS.selectedAttributes();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}   
		
		for(int i=0;i<selectedAtt.length-1;++i){ //weka算法selectedAtt 包括决策属性 
			newB[selectedAtt[i]]=true;
		}


		this.m_useTime = (Utils.getCurrenttime() - time_start)/(double)1000;
		this.m_numRed = Utils.booleanSelectedNum(newB);
		return newB;
	}

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		String fn = "C:/Users/Eric/Desktop/2011秋冬/Code/Xreducer/data/wine.arff";
		Instances m_data = new Instances(new FileReader(fn));
		m_data.setClassIndex(m_data.numAttributes()-1); 
	 
		
		// Replace missing values   //被均值代替
		ReplaceMissingValues m_ReplaceMissingValues = new ReplaceMissingValues();
		m_ReplaceMissingValues.setInputFormat(m_data);
		m_data = Filter.useFilter(m_data, m_ReplaceMissingValues);
		
		
		ConsistencySubsetEval eval = new ConsistencySubsetEval();
		String algname = "Consistency算法";
		GreedyStepwise search = new GreedyStepwise();
 	
		Wekamethod mg = new Wekamethod(m_data,algname,eval,search);
		//MStyle_FCBF mg = new MStyle_FCBF(m_data, true, -1, -1);
		mg.getInformation();
	}

}
