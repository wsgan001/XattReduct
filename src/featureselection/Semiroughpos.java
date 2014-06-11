package featureselection;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Random;
import java.util.Vector;

import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import featureselection.SemiroughFS.evaluate_struct;

import weka.attributeSelection.AttributeSelection;
import weka.attributeSelection.CfsSubsetEval;
import weka.attributeSelection.ConsistencySubsetEval;
import weka.attributeSelection.GainRatioAttributeEval;
import weka.attributeSelection.GreedyStepwise;
import weka.attributeSelection.Ranker;
import weka.clusterers.SimpleKMeans;
import weka.core.Instances;

public class Semiroughpos {
	static double sumofposforlabel;
	static int sumofdismatforunlabel;
	
//	static int[][] selectlabeldismat;
	static int[][] selectunlabeldismat;
	static double energy=1;
	static boolean unlabel_fill=true;
	static boolean ratioimprove=true;
	
	
	static XSSFWorkbook book;
	static XSSFSheet sheet;
	static XSSFRow row;
	static int method=1;
	
	
	public static double mygetGranulation(Instances ldataset,boolean[]D,boolean[] A){
		if(ldataset.numInstances()>0)
			return Utils_entropy.getGranulation(ldataset, D, A);
		else
			return 0;
	}	
	
	public static void computefulldismatvalue(Instances ldataset,Instances udataset){
//		fulldismatforlabel=new int[ldataset.numInstances()][ldataset.numInstances()];
		sumofposforlabel=0;
		sumofdismatforunlabel=0;
		int atrnum=ldataset.numAttributes()-1;
		boolean[] D = new boolean[ldataset.numAttributes()];
		Arrays.fill(D, false);
		D[ldataset.numAttributes()-1]=true;
		boolean[] A = new boolean[ldataset.numAttributes()];
		Arrays.fill(A, true);
		A[ldataset.numAttributes()-1]=false;		
		sumofposforlabel=mygetGranulation(ldataset,D,A);
		for(int i=0;i<udataset.numInstances();i++){
			for(int j=i+1;j<udataset.numInstances();j++){
				for(int k=0;k<atrnum;k++){
					if(udataset.instance(i).value(k)!=udataset.instance(j).value(k)){
						if(!unlabel_fill || udataset.instance(i).classValue()
								!=udataset.instance(j).classValue()){
							sumofdismatforunlabel++;
						}						
						break;
					}
				}
			}
		}
	}
	
	public static void addnewatttodismat(Instances dataset,int newatt,boolean islabeled){
		if(islabeled)
			return;
		for(int i=0;i<dataset.numInstances();i++){
			for(int j=i+1;j<dataset.numInstances();j++){
				if(dataset.instance(i).value(newatt)!=dataset.instance(j).value(newatt)){
					if(!islabeled){
						if(!unlabel_fill || dataset.instance(i).classValue()
								!=dataset.instance(j).classValue()){
							selectunlabeldismat[i][j]=1;
						}
					}
				}
			}
		}
		
	}
	
	public static int getattimporve(Instances dataset,int newatt,boolean islabeled){
		int improve=0;
		if(islabeled)
			return 0;
		for(int i=0;i<dataset.numInstances();i++){
			for(int j=i+1;j<dataset.numInstances();j++){
				if(dataset.instance(i).value(newatt)!=dataset.instance(j).value(newatt)){
					if(!islabeled && selectunlabeldismat[i][j]==0){
						if(!unlabel_fill || dataset.instance(i).classValue()
								!=dataset.instance(j).classValue()){
							improve++;
						}
					}
					
				}
			}
		}
		return improve;
	}
		
	public static evaluate_struct semirough_heuristic(Instances dataset,
			Instances ldataset, Instances udataset, double fileno) throws Exception{
		evaluate_struct evaluatevalue;
		int atrnum=ldataset.numAttributes()-1;
		double curpos=0;
		int curselectsumforunlabel=0;
		int selectedattnum=0;
		computefulldismatvalue(ldataset,udataset);
		selectunlabeldismat=new int[udataset.numInstances()][udataset.numInstances()];
		for(int i=0;i<selectunlabeldismat.length;i++)
			for(int j=0;j<selectunlabeldismat[0].length;j++)
				selectunlabeldismat[i][j]=0;
		boolean[] D = new boolean[ldataset.numAttributes()];
		Arrays.fill(D, false);
		D[ldataset.numAttributes()-1]=true;

		boolean[] selectatr=new boolean[ldataset.numAttributes()];
		Arrays.fill(selectatr,false);

		curpos=0;
		double newpos=0,maxpos=0;
		while( true ){
			if( Math.abs(sumofposforlabel - curpos) < 1e-6
					&&  ((double)curselectsumforunlabel) - (energy* ((double)sumofdismatforunlabel)) > -1e-5 )
			{
					break;			
			}
			double maximsigl=-1;double maxsigu=-1;int sel=-1;
			double maximprovel=-1;int maximproveu=-1;
			for(int i=0;i<atrnum;i++){
				if(selectatr[i])
					continue;
				selectatr[i]=true;
				newpos=mygetGranulation(ldataset,D,selectatr);
				double improvel=newpos-curpos;
				selectatr[i]=false;
				int improveu=getattimporve(udataset,i,false);
				double sigforl,sigforu;
				if(ratioimprove){
					if(sumofposforlabel>0){
						sigforl=(newpos) / ((double)sumofposforlabel);
					}else{
						sigforl=0;
					}
					if(sumofdismatforunlabel>0){
						sigforu=((double)(improveu+curselectsumforunlabel)) / ((double)sumofdismatforunlabel);
					}else{
						sigforu=0;
					}
				}else{
					sigforl=improvel;
					sigforu=improveu;
				}
				boolean flag=false;
				if(method==1){
					flag=(sigforl+ sigforu) > (maximsigl + maxsigu);
				}else{
					if(Math.abs(sumofposforlabel - curpos) >= 1e-6)
						flag=improvel > maximprovel;
					else
						flag=improveu > maximproveu;
				}
				if(flag){
					maximsigl=sigforl;
					maxsigu=sigforu;
					sel=i;
					maximprovel=improvel;
					maximproveu=improveu;
					maxpos=newpos;
				}
			}
			curpos=maxpos;
			addnewatttodismat(udataset,sel,false);
			curselectsumforunlabel+=maximproveu;
			selectatr[sel]=true;
			selectedattnum++;
		}
		
		
		for (int i = 0; i < selectatr.length; i++) {
			if (selectatr[i]) {
				selectatr[i] = false;
				double labelpos = mygetGranulation(ldataset,D,selectatr);
				int unlabelcount = SemiroughFS_heuristic.getdismatcount(udataset,selectatr,false);
				if(Math.abs(sumofposforlabel - labelpos) < 1e-6
						&&  ((double)unlabelcount) - (energy* ((double)sumofdismatforunlabel)) > -1e-5 ){
					selectedattnum--;
				} else {
					selectatr[i] = true;
				}
			}
		}
		
		int[] finalatt=new int[selectedattnum]; int pos=0;
		for(int i=0;i<selectatr.length;i++){
			if(selectatr[i]){
				finalatt[pos]=i;
				pos++;
			}
		}
		System.out.printf("select att: "+ selectedattnum+":");
		for(int i=0;i<finalatt.length;i++){
			System.out.printf( " " + (finalatt[i]+1));			
		}
		System.out.printf("\n");
		evaluatevalue = SemiroughFS.myevevaluate(dataset, finalatt, true);
		return evaluatevalue;
	}
	
	
	
	public static void main(String[] args) throws Exception {
		
		return;
	}

}
