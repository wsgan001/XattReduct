package Test;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Vector;

import weka.core.Instances;
import Part1.MethodGenerateClustering;
import Test.ConMatrix.LinkType;

public class RunEx_Ensize {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		String path = "C:\\Users\\Eric\\Desktop\\2012秋冬\\NO.4\\data\\UCI\\";
		//String name = "eclipse";
		//String name = "half-rings";
		//String name = "subcompound";
		//String name = "three-rings";
		
		
	

		
		Vector<String> ns= new Vector<String>();
		ns.add("wine") ;
		ns.add("glass") ;
		ns.add("clean1") ;	
		ns.add("ecoli") ;
		ns.add("libras") ;
		ns.add("wdbc") ;		
		ns.add("new-thyroid") ;
		ns.add("iris") ;
		 
		for(int i=0;i<ns.size();++i){
			Run(path,ns.elementAt(i));
		}
	}

	private static void Run(String path, String dataname) throws Exception {
		// TODO Auto-generated method stub
		Instances data = new Instances(new FileReader(path+dataname+".arff"));
		data.setClassIndex(data.numAttributes()-1);
		Instances dataforcluster = new Instances(new FileReader(path+dataname+".arff"));
		dataforcluster.deleteAttributeAt(dataforcluster.numAttributes()-1);
		int rnd = 2;
		//double[] per = {0.05,0.1,0.15,0.2,0.25,0.3,0.35,0.4,0.45,0.5,0.55,0.6};
		double per = 0.05;
		
		int[] ensize = {5,10,13,15,20,25};
		int type =0;//0 全部属性
		 

		double[] tmpdclass = data.attributeToDoubleArray(data.numAttributes()-1);
		int[] res0 = new int[data.numInstances()];
		for(int qq=0;qq<tmpdclass.length;++qq)
		{
			res0[qq]=(int)tmpdclass[qq];
		}
		String str=dataname+"\n";
		String str1="";
		String str2="";
		for(int i=0;i<ensize.length;++i){
			boolean[] labeled =ClusterEnsemble.getRandomLabeled(per,data.numInstances(),rnd);
			MethodGenerateClustering mc = new MethodGenerateClustering(data,dataforcluster,labeled,ensize[i],rnd++,type,0.5);
			ClusterEnsemble ce1 =new ClusterEnsemble(mc,new ConMatrix(LinkType.AVERAGE),false); 
			ClusterEnsemble ce2 =new ClusterEnsemble(mc,new ConMatrix(LinkType.MEAN),false);
			
			
			
			int[] res1=ce1.fr.cluster;
			int[] res2=ce2.fr.cluster;
			str1+=" "+EvaluateAlg.getAC_ByBipartiteGraph(res0,res1)*100;
			str2+=" "+EvaluateAlg.getAC_ByBipartiteGraph(res0,res2)*100;
		}
		str1+="\n";
		str2+="\n";
		Vector<int[]> res1=EvaluateAlg.loadMatRes(path+"\\RunTMP\\ensize\\"+dataname+".part3");

		int cnt = 0;
		String str3="";
		String str4="";
		String str5="";
		for(int p=0;p<res1.size();p=p+3){
			int[] resCSPA= res1.elementAt(p);
			int[] resHGPA= res1.elementAt(p+1);
			int[] resMCLA= res1.elementAt(p+2);
			 
			str3+=" "+EvaluateAlg.getAC_ByBipartiteGraph(res0,resCSPA)*100;
			str4+=" "+EvaluateAlg.getAC_ByBipartiteGraph(res0,resHGPA)*100;
			str5+=" "+EvaluateAlg.getAC_ByBipartiteGraph(res0,resMCLA)*100;
			
		}
		str3+="\n";
		str4+="\n";
		str5+="\n";
		str+=str1+str2+str3+str4+str5+"\n\n";
		System.out.println(str); 
		writeNewFile(path+"\\RunTMP\\ensize\\", dataname+".partEnsize.part1", str);
	}
	private static void writeNewFile(String string, String string2, String str) {
		// TODO Auto-generated method stub
		try {
	        //打开一个写文件器，构造函数的第二个参数true表示以追加的形式写文件 false 重新写
	        FileWriter writer = new FileWriter(string+string2, false);
	        writer.write(str);
	        writer.close();
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}
}
