package Modification;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Random;
import java.util.Vector;

import weka.classifiers.Classifier;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Remove;

public class SMCB2_PLUS {

	public int N = 0;
	
	public Vector<boolean[]> Matrixs = new Vector<boolean[]>();
	public Vector<boolean[]> Core = new Vector<boolean[]>();
	public Vector<boolean[]> Reduct = new Vector<boolean[]>();
	public Vector<boolean[]> FinalReduct = new Vector<boolean[]>();
	public Vector<Vector<boolean[]>> AllReduct = new Vector<Vector<boolean[]>> ();
	public Vector<Instances> allData = new Vector<Instances>();
	
	public Instances data = null;
	public Random rd = new Random(1);
	public SMCB2_PLUS(String fn) throws FileNotFoundException, IOException
	{
		data = new Instances(new FileReader(fn));
		data.setClassIndex(data.numAttributes()-1); 
		this.N = data.numAttributes()-1;
		System.out.println(data.numInstances());
		
		
		weka.filters.supervised.attribute.Discretize sd = new weka.filters.supervised.attribute.Discretize();
		 
		try {
			sd.setInputFormat(this.data);
			this.data = Filter.useFilter(this.data , sd);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//double MaxValue[] = new double[this.data.numAttributes()];
		//最高频率填missing
		/*for(int k=0;k<this.data.numAttributes();++k){
			int []xx = this.data.attributeStats(k).nominalCounts;
			int index = getMax(xx);
			String v = this.data.attribute(k).value(index);
			for(int i=0;i<this.data.numInstances();++i){
				if(this.data.instance(i).isMissing(k))
				{
					 
					this.data.instance(i).setValue(k, v);
				}
			}
		}*/
		//类内最高频率填missing
		int classNum = this.data.numClasses();
		int [][]maxindWithlabel = new int[classNum][this.data.numAttributes()-1];
		for(int k=0;k<classNum;++k){
			int [][]tmp = new int[100][this.data.numAttributes()-1];
			for(int i=0;i<this.data.numInstances();++i){
				if((int)this.data.instance(i).classValue()==k){
					for(int q=0;q<this.data.numAttributes()-1;++q){
						if(!this.data.instance(i).isMissing(q))
							//System.out.println((int)this.data.instance(i).value(q));
						tmp[(int)this.data.instance(i).value(q)][q]++;
					}
				}
					 
			}
			for(int q=0;q<this.data.numAttributes()-1;++q){
				int max = -1;
				int maxind = -1;
				for(int x=0;x<100;++x){
					if(tmp[x][q]>max){
						max = tmp[x][q];
						maxind = x;
					}
				}
				maxindWithlabel[k][q]=maxind;
			}
		}
		 
		for(int k=0;k<this.data.numAttributes();++k){
			for(int i=0;i<this.data.numInstances();++i){
				if(this.data.instance(i).isMissing(k))
				{
					int index = maxindWithlabel[(int)this.data.instance(i).classValue()][k];
					String v = this.data.attribute(k).value(index);
					this.data.instance(i).setValue(k, v);
				}
			}
		}
		

		for(int i=0;i<10;++i){
			allData.add(getPerData(0.9));
			allData.add(getPerData(0.8));
			allData.add(getPerData(0.7));
			allData.add(getPerData(0.6));
			allData.add(getPerData(0.5));
		}
		System.out.println(allData.size());
	 
	}
	public Instances getPerData(double per){
		int U = (int) (this.data.numInstances()*per);
		int pos[] = new int[this.data.numInstances()];
		for(int i=0;i<this.data.numInstances();++i){
			pos[i]=i;
		}
		for(int i=0;i<U;++i){
			int r = rd.nextInt(this.data.numInstances());
			while(r<i)
				r = rd.nextInt(this.data.numInstances());
			int tmp = pos[i];
			pos[i]=pos[r];
			pos[r]=tmp;
		}
		Instances test = new Instances(this.data, U);
		for(int i=0;i<U;++i)
			test.add(this.data.instance(pos[i]));
		return test;
	}
	public void getData2Matrix() {
		 
		int U = data.numInstances();
		for(int i=0;i<U;++i){
			for(int j=i+1;j<U;++j){
				if(data.instance(i).classValue()!=data.instance(j).classValue()){
					boolean[] A = new boolean[N];
					Arrays.fill(A, false);
					int cnt = 0;
					for(int k=0;k<N;++k)
					{
						if(data.instance(i).value(k)!=data.instance(j).value(k))
						{
							cnt++;
							A[k]=true;
						}
					}
					if(cnt==1)
						Core.add(A.clone());
					if(cnt>=1&&!hasMatrixs(A.clone()))
						Matrixs.add(A.clone());

				}
			}
		}
	}
	public int getMax(int[]A){
		int max = -1;
		int maxind = -1;
		for(int i=0;i<A.length;++i){
			if(A[i]>max){
				max = A[i];
				maxind = i;
			}
		}
		return maxind;
	}
	public boolean hasMatrixs(boolean[]A){
		for(int i=0;i<this.Matrixs.size();++i){
			if(isEqual(A,this.Matrixs.elementAt(i)))
				return true;
		}
		return false;
	}
	public boolean hasReduct(boolean[]A){
		for(int i=0;i<this.Reduct.size();++i){
			if(isEqual(A,this.Reduct.elementAt(i)))
				return true;
		}
		return false;
	}
	public boolean isEqual(boolean[]A,boolean[]B)
	{
		for(int i=0;i<A.length;++i)
		{
			if(A[i]!=B[i])
				return false;
		}
		return true;
	}
	public boolean isContains(boolean[]A,boolean[]B) //A包含B
	{
		for(int i=0;i<A.length;++i)
		{
			if(!A[i]&&B[i])
				return false;
		}
		return true;
	}
	public boolean isCap(boolean[]A,boolean[]B)
	{
		for(int i=0;i<A.length;++i)
		{
			if(B[i]&&A[i])
				return true;
		}
		return false;
	}
	public void deleteDuplicates(){
		Vector<boolean[]> tmpM = (Vector<boolean[]>)this.Matrixs.clone();
		boolean Tmp[] = new boolean[tmpM.size()];
		Arrays.fill(Tmp, true);
		for(int i=0;i<this.Matrixs.size();++i)
		{		 
			for(int j=0;j<this.Matrixs.size();++j){
				if(i!=j&&Tmp[j]&&isContains(this.Matrixs.elementAt(i),this.Matrixs.elementAt(j))){
					Tmp[i] = false;
					break;
				}
			}
		}
		Matrixs.clear();
		for(int i=0;i<tmpM.size();++i)
		{
			if(Tmp[i]){
			Matrixs.add(tmpM.elementAt(i));}
		}
		
	}
	
	public void findReduct(boolean[] curreduct, int pos)
	{
		boolean[] re=curreduct.clone();
		while(pos<this.Matrixs.size()){
			if(!isCap(re,Matrixs.elementAt(pos)))
			{
				break;
			}
			pos++;
		}
		if(pos>=this.Matrixs.size()){
			if(!hasReduct(re.clone()))
				Reduct.add(re.clone());
			return;
		}
		for(int i=0;i<this.N;i++)
		{
			boolean[] tmp = new boolean[this.N];
			tmp[i]=true;
			if(isCap(tmp,Matrixs.elementAt(pos)))
			{
				//re[i]=true;
				boolean[] nextre = re.clone();
				nextre[i]=true;
				findReduct(nextre,pos+1);
			}
		}
	}
	public void deleteDuplicatesReduct(){
		
		Vector<boolean[]> tmpM = (Vector<boolean[]>)this.Reduct.clone();
		boolean Tmp[] = new boolean[tmpM.size()];
		Arrays.fill(Tmp, true);
		for(int i=0;i<this.Reduct.size();++i)
		{		 
			for(int j=0;j<this.Reduct.size();++j){
				if(i!=j&&Tmp[j]&&isContains(this.Reduct.elementAt(i),this.Reduct.elementAt(j))){
					Tmp[i] = false;
					break;
				}
			}
		}
		
		Reduct.clear();
		for(int i=0;i<tmpM.size();++i)
		{
			if(Tmp[i]){
				Reduct.add(tmpM.elementAt(i));}
		}
		
	}
	public int[] getEvluation(){
		int[] res = new int[this.FinalReduct.size()];
		for(int i=0;i<this.FinalReduct.size();++i){
			int cnt = 0;
			for(int j=0;j<this.AllReduct.size();++j){
				for(int k=0;k<this.AllReduct.elementAt(j).size();++k){
					int []a = this.bool2intwithD(this.FinalReduct.elementAt(i));
					int []b = this.bool2intwithD(this.AllReduct.elementAt(j).elementAt(k));
					if(this.isEqual(this.FinalReduct.elementAt(i), this.AllReduct.elementAt(j).elementAt(k)))
					{
						cnt++;
						break;
					}
				}
			}
			res[i]=cnt;
		}
		return res;
	}
	public int[] bool2intwithD(boolean[] A){
		int cnt = 0;
		for(int i=0;i<A.length;++i)
		{
			if(A[i])
				cnt++;
		}
		int []res = new int[cnt+1];
		cnt = 0;
		for(int i=0;i<A.length;++i)
		{
			if(A[i])
				res[cnt++]=i;
		}
		res[cnt] = N;
		return res;
	}
	public void getReduct()
	{
		long t_start=System.currentTimeMillis();
		long t_end=0;
	 
		this.getData2Matrix();
		//System.out.println(this.Matrixs.size());
		this.deleteDuplicates();
		//System.out.println(this.Matrixs.size());
		boolean[] curreduct = new boolean[this.N];
		Arrays.fill(curreduct, false);

		this.findReduct(curreduct, 0);
		//System.out.println(this.Reduct.size());
		this.deleteDuplicatesReduct();
		//System.out.println(this.Reduct.size());
 
		//System.out.println("All==OK");
		/*for(int a=0;a<this.Reduct.size();++a)
		{
			System.out.println(Arrays.toString(bool2intwithD(this.Reduct.elementAt(a))));
		}*/
		  FinalReduct = (Vector<boolean[]>)this.Reduct.clone();
		  for(int i=0;i<this.allData.size();++i){
				this.Matrixs.clear();
				this.Core.clear();
				this.Reduct.clear();
				this.data = this.allData.elementAt(i);
				this.getData2Matrix();
				//System.out.println(this.Matrixs.size());
				this.deleteDuplicates();
				//System.out.println(this.Matrixs.size());
				curreduct = new boolean[this.N];
				Arrays.fill(curreduct, false);

				this.findReduct(curreduct, 0);
				//System.out.println(this.Reduct.size());
				this.deleteDuplicatesReduct();
				Vector<boolean[]> newone = (Vector<boolean[]>)this.Reduct.clone();
				this.AllReduct.add(newone);
				//System.out.println(i+"==OK");
				/*for(int a=0;a<this.Reduct.size();++a)
				{
					 
					System.out.println(Arrays.toString(bool2intwithD(this.Reduct.elementAt(a))));
				}*/
				
		  }
		  int[] res = getEvluation();
		  t_end=System.currentTimeMillis();
		  
			 double trainTime = (t_end-t_start)/1000.0;
			 System.out.println(trainTime+"s");
			 System.out.println(this.FinalReduct.size());
			for(int i=0;i<this.FinalReduct.size();++i)
			{
				//System.out.println(Arrays.toString(this.FinalReduct.elementAt(i))+"$\\rightarrow$"+res[i]);
				System.out.println(showReduct(this.FinalReduct.elementAt(i))+"："+res[i]+"\\\\");
			}

			//java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("hh:mm:ss");
			 // String now1 = sdf.format(new java.util.Date());
			 // System.out.println(now1);
			 
		 
	}
	public static String showReduct(boolean[] A){
		String str="";
		for(int i=0;i<A.length-1;++i){
			if(A[i])
			str+=(i+1)+", ";
		}

		return str;
	}
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		String path1 = "C:/Users/Eric/Desktop/2011秋冬/Code/Xreducer/data/Data/cleveland.arff";
		String path2 = "C:/Users/Eric/Desktop/2011秋冬/Code/Xreducer/data/Data/credit.arff";
		String path3 = "C:/Users/Eric/Desktop/2011秋冬/Code/Xreducer/data/Data/hepatitis.arff";
		String path4 = "C:/Users/Eric/Desktop/2011秋冬/Code/Xreducer/data/Data/vote.arff";
		SMCB2_PLUS test = new SMCB2_PLUS(path4);
		test.getReduct();
		

	 
		
	}

}
