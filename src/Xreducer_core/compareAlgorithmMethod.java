package Xreducer_core;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;
import java.util.Vector;

import javax.swing.JScrollPane;
import javax.swing.JTable;

import org.apache.commons.math.stat.descriptive.moment.Mean;
import org.apache.commons.math.stat.descriptive.moment.StandardDeviation;
import org.apache.commons.math.stat.inference.TestUtils;

import weka.core.Instances;

import Xreducer_gui.loadFileFrame;
import Xreducer_gui.loadFileFrame.StateInfo;
import Xreducer_gui.mainWindows;
import Xreducer_struct.comFiles;
import Xreducer_struct.globalValue;
import Xreducer_struct.oneAlgorithm;
import Xreducer_struct.oneAlgorithm.xCategory;
import Xreducer_struct.oneFile;

public class compareAlgorithmMethod {

	
	private loadFileFrame lf = null;
	private comFiles comf = null;

	
	public compareAlgorithmMethod(comFiles comf, loadFileFrame lf ){
		this.comf = comf;
		this.lf = lf;
	}

	public void StartAlg() throws IOException, Exception{
		for(int i=0;i<this.comf.numFile;++i){
			//Date now = new Date();
			//String strT = DateFormat.getTimeInstance().format(now); 
			if(this.comf.onef.get(i).filepath!=null){
				this.lf.setState(i, StateInfo.COMPUTING);	
				int numcomAlg = this.comf.onef.get(i).numcomAlg;
				wekaDiscretizeMethod dm = new wekaDiscretizeMethod(this.comf.onef.get(i).filepath,true);				
				
				for(int j=0;j<numcomAlg;++j){
					xCategory category = this.comf.onef.get(i).algs.get(j).category;
					oneAlgorithm oneAl = this.comf.onef.get(i).algs.get(j);	
					switch(category){
					case Fullset:{ //full set								
						this.comf.onef.get(i).algs.get(j).ACs = Utils.multicrossValidateModel(oneAl.cl, dm.getOriginalData(), oneAl.numRun, oneAl.numFold );
						this.comf.onef.get(i).algs.get(j).redTime = 0.0;
						int K = dm.getOriginalData().numAttributes();
						int[] sel = new int[K];
						for(int k=0;k<K;++k){
							sel[k]=k;
						}
						this.comf.onef.get(i).algs.get(j).selectedAtt=sel;
						break;
					}
					case Wekaalg:{ //weka algorithm
						wekaReduceMethod wrm = new  wekaReduceMethod(this.comf.onef.get(i),oneAl);
						wrm.Run();
						//oneAlgorithm.copy(wrm.getAlg(),this.comf.onef.get(i).algs.get(j));
						break;
					}

					case Roughsetalg:{ //roughset algorithm
						//System.out.println(oneAl.algname);
						roughSetMethod rsm = new  roughSetMethod(this.comf.onef.get(i),oneAl);
						rsm.Run();
						//oneAlgorithm.copy(rsm.getAlg(),this.comf.onef.get(i).algs.get(j));
						break;
					}
					case FCBFalg:{ //roughset algorithm
						//System.out.println(oneAl.algname);
						FCBFReduceMethod fcbf = new  FCBFReduceMethod(this.comf.onef.get(i),oneAl );
						fcbf.Run();
						//oneAlgorithm.copy(fcbf.getAlg(),this.comf.onef.get(i).algs.get(j));
						break;
					}
					case NibbleRR:{ //roughset algorithm
						int nsindex = this.comf.onef.get(i).NSindex;
						NibbleAlgMethod nirr = new  NibbleAlgMethod(this.comf.onef.get(i),oneAl, this.comf.onef.get(i).algs.get(nsindex) );
						nirr.Run();
						//oneAlgorithm.copy(nirr.getAlg(),this.comf.onef.get(i).algs.get(j));
						break;
					}
					case RSandFCBFalg:{ //roughset algorithm
						RSandFCBFReduceMethod rsfcbf = new  RSandFCBFReduceMethod(this.comf.onef.get(i),oneAl );
						rsfcbf.Run();
						//oneAlgorithm.copy(nirr.getAlg(),this.comf.onef.get(i).algs.get(j));
						break;
					}
					default: break;
					}
					 
					int p = (int)(100*(double)j/(double)numcomAlg);
					this.lf.setProgress(i,  p);

				}
				if(comf.onef.get(i).NSindex!=-1){
					nibblePolicyMethod np = new nibblePolicyMethod(this.comf.onef.get(i));
					np.Run();
					this.comf.onef.get(i).NSRes = np.getNSRes();
					this.comf.onef.get(i).keys = oneFile.getKeys(np.style);	
				}
				else{
					double[][] temp = {{0.0,0.0},{0.0,0.0}};
					String[] stemp = {"No","No"};
					this.comf.onef.get(i).NSRes = temp;
					this.comf.onef.get(i).keys = stemp;	
				}

				resAnalysis(i); //分析第i个数据结果
				globalValue.gcomf = this.comf;  //计算结果赋值给全局变量
				this.lf.setState(i, StateInfo.COMPLETE);
				this.lf.setProgress(i,  100);

			}
		}
	}
	
	private void resAnalysis(int i) throws Exception {
		// TODO Auto-generated method stub
		int numcomAlg = this.comf.onef.get(i).numcomAlg;
		int numRun = this.comf.onef.get(i).numRun;
		String[] algnames = new String[numcomAlg];
		Vector<int[]> selectAtts =  new Vector<int[]>(numcomAlg);
		double[] redTimes = new double[numcomAlg];
		double[][] ACres = new double[numcomAlg][numRun];
		double[][] TtestRes = null;
		
		for(int k=0;k<numcomAlg;++k){
			algnames[k]=this.comf.onef.get(i).algs.get(k).algname;
			selectAtts.add(k,this.comf.onef.get(i).algs.get(k).selectedAtt);
			redTimes[k]=this.comf.onef.get(i).algs.get(k).redTime;
			ACres[k] = this.comf.onef.get(i).algs.get(k).ACs;
		}
		TtestRes = MypairedTtest(ACres,this.comf.onef.get(i).baseindex,this.comf.onef.get(i).signiifcantlevel);
		

		this.comf.onef.get(i).selectAtts = selectAtts;
		this.comf.onef.get(i).redTimes = redTimes;
		this.comf.onef.get(i).TtestRes = TtestRes;
	}

	public double[][] Vector2doule(Vector v){
		int M = ((double[])((Vector)v.get(0)).get(4)).length;
		double[][] res = new double[v.size()][M]; 
		for(int j=0;j<v.size();++j){
			res[j]=((double[])((Vector)v.get(j)).get(4));
		}
		return res;
		
	}

	public static double[][] MypairedTtest(double[][] X, int baseindex , double singificantlevel) throws Exception{
		int N = X.length;
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
		res[baseindex][2]=-1;
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
