package Xreducer_core;

import java.io.IOException;
import java.util.Vector;

import weka.classifiers.Classifier;
import weka.core.Instances;
import Xreducer_struct.oneAlgorithm;
import Xreducer_struct.oneAlgorithm.xCategory;
import Xreducer_struct.oneAlgorithm.xStyle;
import Xreducer_struct.oneFile;
import Xreducer_struct.oneFile.xNSstyle;

public class nibblePolicyMethod {

	public double[][] NSRes = null;

	public oneAlgorithm alg = null;
	public xNSstyle style ;
	public int numFold = 0;
	public Classifier NScl = null;
	public int randomI = 0;
	public oneFile afile = null;
	public Instances OriginalData = null;
	public Instances DiscretizeData = null;
	public int Maxnum = 0;
	public int plotNum = 3;
	
	public nibblePolicyMethod(oneFile afile) throws IOException, Exception{
		this.afile = afile;
		int baseindex = afile.NSindex;
		this.alg = afile.algs.get(baseindex);
		this.style = afile.NSstyle;
		this.numFold = afile.numFold;
		this.NScl = afile.NScl;
		this.randomI = afile.randomI;
		this.afile = afile;
		wekaDiscretizeMethod dm = new wekaDiscretizeMethod(this.afile.filepath,true);
		this.OriginalData = dm.getOriginalData();
		this.DiscretizeData = dm.getDiscretizeData();
		this.Maxnum = (afile.att-1)>afile.NSmaxIndex?afile.NSmaxIndex:(afile.att-1);
	}
	
	public boolean Run() throws Exception{
		
		xCategory category = this.alg.category;
		ReduceMethod rm = null;
		switch(category){
		case Wekaalg:{ //weka algorithm
			rm = new  wekaReduceMethod(this.afile,this.alg);
			break;
		}

		case Roughsetalg:{ //roughset algorithm
			rm = new  roughSetMethod(this.afile,this.alg);
			break;
		}
		case FCBFalg:{ //roughset algorithm
			rm = new  FCBFReduceMethod(this.afile,this.alg);
			break;
		}
		default: break;
		}
		boolean[] B = new boolean[this.afile.att];
		int currentNum = 0;
		Vector res = new Vector();
		while(currentNum<this.Maxnum){
			rm.reLoadDataset(B);
			boolean[] newB = rm.getOneReduction(B);			
			double[] anstemp = NSevaluationfunction(this.style,B,newB);
			currentNum = Utils.booleanSelectedNum(newB);
			double[] ans = new double[anstemp.length+1];
			for(int i=0;i<anstemp.length;++i){
				ans[i]=anstemp[i];
			}
			ans[anstemp.length] = currentNum;
			B = newB.clone();
			res.add(ans);
		}
		this.NSRes = new double[this.plotNum+1][res.size()];
		for(int i=0;i<res.size();++i){
			double[] ans = (double[]) res.get(i);
			for(int j=0;j<ans.length;++j){
				this.NSRes[j][i]=ans[j];
			}
			
		}
		this.NSRes[2][0] = 0;
		
		return true;
	}
	
	
	public double[] NSevaluationfunction(xNSstyle style,boolean[] oldB, boolean[] newB) throws Exception{
		double[] res = null;
		xStyle nsStyle = null;
		switch(style){
		case NS_IG:{
			nsStyle = xStyle.IG;
			break;
		}
		case NS_SU:{
			nsStyle = xStyle.SU;
			break;
		}
		default:break;
		}
		res = new double[3];
		Instances newData = wekaDiscretizeMethod.getSelectedInstances(this.OriginalData,newB);
        //ÑµÁ·Ô¤²â×¼È·ÂÊ
		res[0] = Utils.onecrossValidateModel(this.NScl, newData, this.numFold, this.randomI);
		boolean[] D = Utils.Instances2DecBoolean(this.OriginalData);

		boolean[] addB = Utils.boolsSubtract(newB, oldB);
		//res[1] = Utils_entropy.getEvaluateValue(nsStyle, this.DiscretizeData, D, newB);
		//System.out.println(Utils.boolean2select(oldB).length+":"+Utils.boolean2select(addB).length);
		res[1] = Utils.getEvaluateValue(nsStyle, this.DiscretizeData, D, addB);
		res[2] = Utils.getEvaluateValue(nsStyle, this.DiscretizeData, oldB, addB);
		//System.out.println(res[1]+":"+res[2]);
		this.plotNum = 3;
		
		return res;
	}
	public double[][] getNSRes(){
		return this.NSRes;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
