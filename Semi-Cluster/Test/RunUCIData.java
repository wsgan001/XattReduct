package Test;

import java.io.FileReader;
import java.util.Vector;

import myUtils.xFile;
 
import weka.clusterers.SimpleKMeans;
import weka.core.Instances;
import weka.core.SelectedTag; 
import Part1.MethodGenerateClustering; 
import Test.ConMatrix.LinkType;

public class RunUCIData {

	public static void Run(String path,String dataname) throws Exception{
		
		Instances data = new Instances(new FileReader(path+dataname+".arff"));
		data.setClassIndex(data.numAttributes()-1);
		Instances dataforcluster = new Instances(new FileReader(path+dataname+".arff"));
		dataforcluster.deleteAttributeAt(dataforcluster.numAttributes()-1);
		int rnd = 2;
		double per = 0.05;
		int ensize = 10;
		int type =0;//0 È«²¿ÊôÐÔ
		boolean[] labeled =ClusterEnsemble.getRandomLabeled(per,data.numInstances(),rnd);
		//String p = "D:\\TETST\\constraint.txt";
		//writeLabeled2file(labeled);
		MethodGenerateClustering mc = new MethodGenerateClustering(data,dataforcluster,labeled,ensize,rnd++,type,0.5);
		ClusterEnsemble ce1 =new ClusterEnsemble(mc,new ConMatrix(LinkType.AVERAGE),false);
		ClusterEnsemble ce2 =new ClusterEnsemble(mc,new ConMatrix(LinkType.MEAN),false);
		
		double[] tmpdclass = data.attributeToDoubleArray(data.numAttributes()-1);
		int[] res0 = new int[data.numInstances()];
		for(int i=0;i<tmpdclass.length;++i)
		{
			res0[i]=(int)tmpdclass[i];
		}
		
		
		int[] res1=ce1.fr.cluster;
		int[] res2=ce2.fr.cluster;
		
		SimpleKMeans km = new SimpleKMeans(); 
		km.setMaxIterations(100);
		km.setNumClusters(data.numClasses());
		km.setDontReplaceMissingValues(true);
		km.setSeed(rnd++);
		km.setPreserveInstancesOrder(true);
		km.buildClusterer(dataforcluster);
		int[] res3=km.getAssignments();
		
		HierarchicalClusterer hc = new HierarchicalClusterer();
		hc.setNumClusters(data.numClasses());
		hc.buildClusterer(dataforcluster);
		int[] res4=hc.m_nClusterNr;;
	
		  
		
		String str="";
		str+=ints2str(res0);
		str+=ints2str(res1);
		str+=ints2str(res2);
		str+=ints2str(res3);
		str+=ints2str(res4);
		
		
		xFile.writeNewFile(path+"\\RunTMP\\", dataname+".part1", str);
		System.out.println("Done:"+dataname);
		 
	}
	private static String ints2str(int[] res0) {
		// TODO Auto-generated method stub
		String str="";
		for(int i=0;i<res0.length;++i){
			str+=res0[i]+" ";
		}
		return str+"\n";
	}
	private static void writeLabeled2file( boolean[] labeled) {
		// TODO Auto-generated method stub
		int cnt =0;
		for(int i=0;i<labeled.length;++i)
			if(labeled[i])
				cnt++;
		String str = cnt+"\n";
		for(int i=0;i<labeled.length;++i){
			str = str+(labeled[i]?"1":"0")+"\n";
		}
		
		xFile.writeNewFile("C:\\Users\\Eric\\Desktop\\2012Çï¶¬\\NO.4\\data\\UCI\\RunTMP\\", "constraint.txt", str);	
	}
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		String path = "C:\\Users\\Eric\\Desktop\\2012Çï¶¬\\NO.4\\data\\UCI\\";
		Vector<String> ns= new Vector<String>();
		
		//ns.add("eclipse") ;
		//ns.add("half-rings") ;
		//ns.add("subcompound") ;
		//ns.add("three-rings") ;
		
		
		
		
		//String name = "half-rings";
		//String name = "subcompound";
		//String name = "three-rings";
		
		//ns.add("wine") ;
		//ns.add("ionosphere") ;
		//ns.add("glass") ;
		//ns.add("diabetes") ;
		//ns.add("clean1") ;
		//ns.add("ecoli") ;
		
		//String name = "wine";
		//String name = "ionosphere";
		//String name = "glass";
		//String name = "diabetes";
		//String name = "clean1";
		//String name = "ecoli";
		
		//ns.add("libras") ;
		//ns.add("Musk2") ;
		//ns.add("olitos") ;
		//ns.add("sonar") ;
		//ns.add("waveform") ;
		//ns.add("wdbc") ;
		//ns.add("new-thyroid") ;
		//ns.add("yeast") ;
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
			Run(path,ns.elementAt(i));
		}
		
	}

}
