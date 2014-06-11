package Test;

import helpLib.Hungarian;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Vector;

import cluster.eva;

import weka.core.Instances;

public class EvaluateAlg {
	public static void Run(String path,String dataname) throws Exception{
		Instances data = new Instances(new FileReader(path+dataname+".arff"));
		data.setClassIndex(data.numAttributes()-1);
		System.out.println(dataname+": numIns="+data.numInstances()+"  numAttr="+(data.numAttributes()-1)+"  numCls="+data.numClasses());
		
		Vector<int[]> res1=loadRes(path+"\\RunTMP\\"+dataname+".part1");
		Vector<int[]> res2=loadRes(path+"\\RunTMP\\"+dataname+".part2");
		Vector<int[]> res3=loadMatRes(path+"\\RunTMP\\"+dataname+".part3");
		
		double[] ac= new double[9];
		double[] nmi= new double[9];
		int k=data.numClasses();
		int[] classes = res1.elementAt(0);
		int[] swcs=res1.elementAt(1);
		int[] swcm=res1.elementAt(2);
		int[] kmeans=res1.elementAt(3);
		int[] hc=res1.elementAt(4);
		int[] mpk=res2.elementAt(0);
		int[] pk=res2.elementAt(1);
		int[] cspa=res3.elementAt(0);
		int[] hgpa=res3.elementAt(1);
		int[] mcla=res3.elementAt(2);
		
		
		ac[0]=getAC_ByBipartiteGraph(classes,swcs);
		ac[1]=getAC_ByBipartiteGraph(classes,swcm);
		ac[2]=getAC_ByBipartiteGraph(classes,mpk);
		ac[3]=getAC_ByBipartiteGraph(classes,pk);
		ac[4]=getAC_ByBipartiteGraph(classes,kmeans);
		ac[5]=getAC_ByBipartiteGraph(classes,hc);
		ac[6]=getAC_ByBipartiteGraph(classes,cspa);
		ac[7]=getAC_ByBipartiteGraph(classes,hgpa);
		ac[8]=getAC_ByBipartiteGraph(classes,mcla);
		
		nmi[0]=NMI(classes,swcs);
		nmi[1]=NMI(classes,swcm);
		nmi[2]=NMI(classes,mpk);
		nmi[3]=NMI(classes,pk);
		nmi[4]=NMI(classes,kmeans);
		nmi[5]=NMI(classes,hc);
		nmi[6]=NMI(classes,cspa);
		nmi[7]=NMI(classes,hgpa);
		nmi[8]=NMI(classes,mcla);
		
		System.out.println("AC:"+Arrays.toString(ac));
		System.out.println("NMI:"+Arrays.toString(nmi)+"\n\n\n");
		
	}
	public static double getAC_ByBipartiteGraph(int[] classes,int[] cluster){ //一一对应
		int[] tmpclasses = classes.clone();
		Arrays.sort(tmpclasses);
		int K = tmpclasses[tmpclasses.length-1]+1;
		int count=0;
		double[][] costMat=new double[K][K];
		for(int i=0;i<K;i++){
			for(int j=0;j<K;j++){
				costMat[i][j]=classes.length;
				for(int k=0;k<classes.length;k++){					
					if(classes[k]==i&&cluster[k]==j){
						costMat[i][j]-=1;
					}
				}
			}
		}
		Hungarian assignment = new Hungarian(costMat);
		for(int i=0;i<K;i++){ 
			count+=(classes.length-costMat[i][assignment.sol(i)]);
		}
		return (double)count/(double)classes.length;
	}
	public static void Alignment_ByBipartiteGraph(int[] classes,int[] cluster,int K){ //一一对应

		double[][] costMat=new double[K][K]; 
		for(int i=0;i<K;i++){
			for(int j=0;j<K;j++){
				costMat[i][j]=classes.length;
				for(int k=0;k<classes.length;k++){					
					if(classes[k]==i&&cluster[k]==j){
						costMat[i][j]-=1;
					}
				}
			}
		}
		Hungarian assignment = new Hungarian(costMat);
		for(int i=0;i<cluster.length;i++){
			cluster[i]=assignment.sol(cluster[i]);
		}
	 
	}
	public static int FindKcluster(int []X){
		int maxV = -1;
		for(int i=0;i<X.length;++i){
			if(maxV<X[i])
				maxV = X[i];
		}
		return maxV+1;
	}
	public static double NMI(int[] classes, int[] cluster){
		int k_classes = FindKcluster(classes);
		int k_cluster = FindKcluster(cluster);
		return eva.NMI(classes,k_classes, cluster,k_cluster);
	}
	public static Vector<int[]> loadMatRes(String string) throws IOException {
		// TODO Auto-generated method stub
		
		File file=new File(string);
        Vector<int[] > res = new Vector<int[] >();
        BufferedReader br=new BufferedReader(new FileReader(file));
        String temp=null;
         
        temp=br.readLine();
        
        while(temp!=null){
        	String[] xx=temp.split("	");
        	if(xx.length>1){
        	int[] arr=new int[xx.length];
        	for(int i=0;i<xx.length;i++)
        	{      
        		arr[i]=Integer.parseInt( xx[i]);
             }
        	res.add(arr);}
        	 
        	temp=br.readLine();
        	
        } 
        return res;
	}
	static Vector<int[]> loadRes(String string) throws IOException {
		// TODO Auto-generated method stub
		 
		
		File file=new File(string);
        Vector<int[] > res = new Vector<int[] >();
        BufferedReader br=new BufferedReader(new FileReader(file));
        String temp=null;
         
        temp=br.readLine();
        
        while(temp!=null){
        	String[] xx=temp.split(" ");
        	if(xx.length>1){
        	int[] arr=new int[xx.length];
        	for(int i=0;i<xx.length;i++)
        	{      
        		arr[i]=Integer.parseInt( xx[i]);
             }
        	res.add(arr);}
        	 
        	temp=br.readLine();
        	
        } 
        return res;
        
		 
	}
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		String path = "C:\\Users\\Eric\\Desktop\\2012秋冬\\NO.4\\data\\UCI\\";
		//String name = "eclipse";
		String name = "half-rings";
		//String name = "subcompound";
		//String name = "three-rings";
		
		
		//String name = "wine";
		//String name = "ionosphere";
		//String name = "clean1";
		//String name = "glass";
		//String name = "diabetes";
		//String name = "ecoli";
		//String name = "libras";
		
		
		//String name = "Musk2";
		//String name = "olitos";
		//String name = "sonar";
		//String name = "waveform";
		//String name = "wdbc";
		
		
		//String name = "new-thyroid";
		//String name = "yeast";
		//String name = "iris";
		
		
		//Run(path,name);
		//Run2(path,name);
		
		
		
        Vector<String> ns= new Vector<String>();
		
		ns.add("eclipse") ;
		ns.add("half-rings") ;
		ns.add("subcompound") ;
		ns.add("three-rings") ;
		
		
		
		
		//String name = "half-rings";
		//String name = "subcompound";
		//String name = "three-rings";
		
		ns.add("wine") ;
		ns.add("ionosphere") ;
		ns.add("glass") ;
		ns.add("diabetes") ;
		ns.add("clean1") ;
		ns.add("ecoli") ;
		//String name = "wine";
		//String name = "ionosphere";
		//String name = "glass";
		//String name = "diabetes";
		//String name = "clean1";
		//String name = "ecoli";
		
		ns.add("libras") ;
		ns.add("Musk2") ;
		ns.add("olitos") ;
		ns.add("sonar") ;
		ns.add("waveform") ;
		ns.add("wdbc") ;
		ns.add("new-thyroid") ;
		ns.add("yeast") ;
		ns.add("iris") ;
		//String name = "libras";
		//String name = "Musk2";
		//String name = "olitos";
		//String name = "sonar";
		//String name = "waveform";
		//String name = "wdbc";
		
		//String name = "new-thyroid";
		//String name = "yeast";
		//String name = "iris";
		//Run(path,name);
		for(int i=0;i<ns.size();++i){
			//Run(path,ns.elementAt(i));
			//ShowDataInfo(path,ns.elementAt(i));
			ShowACRes(path,ns.elementAt(i));
			//ShowNMIRes(path,ns.elementAt(i));
		}
	}
	private static void ShowNMIRes(String path, String dataname) throws IOException {
		// TODO Auto-generated method stub
		Instances data = new Instances(new FileReader(path+dataname+".arff"));
		data.setClassIndex(data.numAttributes()-1);
		//System.out.println(dataname+": numIns="+data.numInstances()+"  numAttr="+(data.numAttributes()-1)+"  numCls="+data.numClasses());
		
		Vector<int[]> res1=loadRes(path+"\\RunTMP\\"+dataname+".part1");
		Vector<int[]> res2=loadRes(path+"\\RunTMP\\"+dataname+".part2");
		Vector<int[]> res3=loadMatRes(path+"\\RunTMP\\"+dataname+".part3");
		
		 
		double[] nmi= new double[9];
		int k=data.numClasses();
		int[] classes = res1.elementAt(0);
		int[] swcs=res1.elementAt(1);
		int[] swcm=res1.elementAt(2);
		int[] kmeans=res1.elementAt(3);
		int[] hc=res1.elementAt(4);
		int[] mpk=res2.elementAt(0);
		int[] pk=res2.elementAt(1);
		int[] cspa=res3.elementAt(0);
		int[] hgpa=res3.elementAt(1);
		int[] mcla=res3.elementAt(2);
		
	 
		nmi[0]=NMI(classes,swcs);
		nmi[1]=NMI(classes,swcm);
		nmi[2]=NMI(classes,mpk);
		nmi[3]=NMI(classes,pk);
		nmi[4]=NMI(classes,kmeans);
		nmi[5]=NMI(classes,hc);
		nmi[6]=NMI(classes,cspa);
		nmi[7]=NMI(classes,hgpa);
		nmi[8]=NMI(classes,mcla);
		String str=dataname;
		for(int i=0;i<nmi.length;++i){
			str+=" & "+doubleFormat("0.000",nmi[i]);
		}
		str+="\\\\";
		System.out.println(str);
	}
	private static void ShowACRes(String path, String dataname) throws IOException {
		// TODO Auto-generated method stub
		Instances data = new Instances(new FileReader(path+dataname+".arff"));
		data.setClassIndex(data.numAttributes()-1);
		//System.out.println(dataname+": numIns="+data.numInstances()+"  numAttr="+(data.numAttributes()-1)+"  numCls="+data.numClasses());
		
		Vector<int[]> res1=loadRes(path+"\\RunTMP\\"+dataname+".part1");
		Vector<int[]> res2=loadRes(path+"\\RunTMP\\"+dataname+".part2");
		Vector<int[]> res3=loadMatRes(path+"\\RunTMP\\"+dataname+".part3");
		
		double[] ac= new double[9];
		double[] nmi= new double[9];
		int k=data.numClasses();
		int[] classes = res1.elementAt(0);
		int[] swcs=res1.elementAt(1);
		int[] swcm=res1.elementAt(2);
		int[] kmeans=res1.elementAt(3);
		int[] hc=res1.elementAt(4);
		int[] mpk=res2.elementAt(0);
		int[] pk=res2.elementAt(1);
		int[] cspa=res3.elementAt(0);
		int[] hgpa=res3.elementAt(1);
		int[] mcla=res3.elementAt(2);
		
		
		ac[0]=getAC_ByBipartiteGraph(classes,swcs);
		ac[1]=getAC_ByBipartiteGraph(classes,swcm);
		ac[2]=getAC_ByBipartiteGraph(classes,mpk);
		ac[3]=getAC_ByBipartiteGraph(classes,pk);
		ac[4]=getAC_ByBipartiteGraph(classes,kmeans);
		ac[5]=getAC_ByBipartiteGraph(classes,hc);
		ac[6]=getAC_ByBipartiteGraph(classes,cspa);
		ac[7]=getAC_ByBipartiteGraph(classes,hgpa);
		ac[8]=getAC_ByBipartiteGraph(classes,mcla);
		
		String str=dataname;
		for(int i=0;i<ac.length;++i){
			str+=" & "+doubleFormat("0.00",ac[i]*100);
		}
		str+="\\\\";
		System.out.println(str);
	}
	private static String doubleFormat(String str, double d) {
		// TODO Auto-generated method stub
		return new DecimalFormat(str).format(d);
	}
	private static void ShowDataInfo(String path, String elementAt) throws FileNotFoundException, IOException {
		// TODO Auto-generated method stub
		Instances data = new Instances(new FileReader(path+elementAt+".arff"));
		data.setClassIndex(data.numAttributes()-1);
		//System.out.println(dataname+": numIns="+data.numInstances()+"  numAttr="+(data.numAttributes()-1)+"  numCls="+data.numClasses());
		System.out.println(elementAt+" & "+data.numInstances()+" & "+(data.numAttributes()-1)+" & "+data.numClasses()+"\\\\");
	}
	private static void Run2(String path, String name) throws IOException {
		// TODO Auto-generated method stub
		Instances data = new Instances(new FileReader(path+name+".arff"));
		data.setClassIndex(data.numAttributes()-1);
		System.out.println(name+": numIns="+data.numInstances()+"  numAttr="+(data.numAttributes()-1)+"  numCls="+data.numClasses());
		
		Vector<int[]> res1=loadRes(path+"\\RunTMP\\"+name+".part1");
		Vector<int[]> res2=loadRes(path+"\\RunTMP\\"+name+".part2");
 
		
		double[] ac= new double[9];
		double[] nmi= new double[9];
		int k=data.numClasses();
		int[] classes = res1.elementAt(0);
		int[] swcs=res1.elementAt(1);
		int[] swcm=res1.elementAt(2);
		int[] kmeans=res1.elementAt(3);
		int[] hc=res1.elementAt(4);
		int[] mpk=res2.elementAt(0);
		int[] pk=res2.elementAt(1);
 
		
		
		ac[0]=getAC_ByBipartiteGraph(classes,swcs);
		ac[1]=getAC_ByBipartiteGraph(classes,swcm);
		ac[2]=getAC_ByBipartiteGraph(classes,mpk);
		ac[3]=getAC_ByBipartiteGraph(classes,pk);
		ac[4]=getAC_ByBipartiteGraph(classes,kmeans);
		ac[5]=getAC_ByBipartiteGraph(classes,hc); 
		
		nmi[0]=NMI(classes,swcs);
		nmi[1]=NMI(classes,swcm);
		nmi[2]=NMI(classes,mpk);
		nmi[3]=NMI(classes,pk);
		nmi[4]=NMI(classes,kmeans);
		nmi[5]=NMI(classes,hc);
 
		
		System.out.println("AC:"+Arrays.toString(ac));
		System.out.println("NMI:"+Arrays.toString(nmi));
	}

}
