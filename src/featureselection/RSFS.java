package featureselection;

import java.util.Arrays;

import featureselection.SemiroughFS.evaluate_struct;
import weka.core.Instances;


public class RSFS {

	public static evaluate_struct entropybasedreduct(Instances alldataset,Instances ldataset) throws Exception{
		int atrnum=ldataset.numAttributes()-1;
		boolean[] D = new boolean[ldataset.numAttributes()];
		Arrays.fill(D, false);
		D[ldataset.numAttributes()-1]=true;
		boolean[] A = new boolean[
		                          ldataset.numAttributes()];
		Arrays.fill(A, true);
		A[ldataset.numAttributes()-1]=false;		
		double fullentropy=Utils_entropy.getConditionalEntorpy(ldataset,D,A);
		boolean[] selectatr=new boolean[ldataset.numAttributes()];
		Arrays.fill(selectatr,false);
		double curentropy=-1;int selectatrnum=0;
		while(true){
			double minentropy=-1; int sel=-1;
			for(int i=0;i<atrnum;i++){
				if(!selectatr[i]){
					selectatr[i]=true;
					double newsig=Utils_entropy.getConditionalEntorpy(ldataset,D,selectatr);
					selectatr[i]=false;
					if(newsig < minentropy || minentropy<0){
						minentropy=newsig;
						sel=i;
					}
				}
			}
			curentropy=minentropy;
			selectatr[sel]=true;
			selectatrnum++;
			if( fullentropy - curentropy > -1e-8){
				break;
			}
		}
		
		for (int i = 0; i < selectatr.length; i++) {
			if (selectatr[i]) {
				selectatr[i] = false;
				double newentropy = Utils_entropy.getConditionalEntorpy(ldataset, D,
						selectatr);
				if( fullentropy - newentropy > -1e-8){
					selectatrnum--;
				} else {
					selectatr[i] = true;
				}
			}
		}
		
		int[] finalatt=new int[selectatrnum]; int pos=0;
		for(int i=0;i<selectatr.length;i++){
			if(selectatr[i]){
				finalatt[pos]=i;
				pos++;
			}
		}
		evaluate_struct evaluatevalue = SemiroughFS.myevevaluate(alldataset, finalatt, true);
		return evaluatevalue;
	}
	
	
	public static  evaluate_struct posbasedreduct(Instances alldataset,Instances ldataset) throws Exception{
		int atrnum=ldataset.numAttributes()-1;
		boolean[] D = new boolean[ldataset.numAttributes()];
		Arrays.fill(D, false);
		D[ldataset.numAttributes()-1]=true;
		boolean[] A = new boolean[ldataset.numAttributes()];
		Arrays.fill(A, true);
		A[ldataset.numAttributes()-1]=false;		
		double fullR=Utils_entropy.getGranulation(ldataset,D,A);
		boolean[] selectatr=new boolean[ldataset.numAttributes()];
		Arrays.fill(selectatr,false);
		double cureR=-1;int selectatrnum=0;
		while(true){
			double maxsig=-1; int sel=-1;
			for(int i=0;i<atrnum;i++){
				if(!selectatr[i]){
					selectatr[i]=true;
					double newsig=Utils_entropy.getGranulation(ldataset,D,selectatr);
					selectatr[i]=false;
					if(newsig>maxsig){
						maxsig=newsig;
						sel=i;
					}
				}
			}
			cureR=maxsig;
			selectatr[sel]=true;
			selectatrnum++;
			if( Math.abs(fullR - cureR) < 1e-6){
				break;
			}
		}
		
		for (int i = 0; i < selectatr.length; i++) {
			if (selectatr[i]) {
				selectatr[i] = false;
				double newR = Utils_entropy.getGranulation(ldataset, D,
						selectatr);
				if (Math.abs(fullR - newR) < 1e-6) {
					selectatrnum--;
				} else {
					selectatr[i] = true;
				}
			}
		}
		
		int[] finalatt=new int[selectatrnum]; int pos=0;
		for(int i=0;i<selectatr.length;i++){
			if(selectatr[i]){
				finalatt[pos]=i;
				pos++;
			}
		}
		evaluate_struct evaluatevalue = SemiroughFS.myevevaluate(alldataset, finalatt, true);
		return evaluatevalue;
	}

	
}
