package cluster;

import java.awt.Color;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Random;
import javax.imageio.*;


import weka.core.Instance;
import weka.core.Instances;
import UFS.DissimilarityForKmodes;
import UFS.SimpleKModes;

public class ClusterEvaluate {

	static String dataname;
	static class DatawithCluster implements Comparable{
		int dataid;
		int clusterno;
		public int compareTo(Object o1){
			if(this.clusterno > ((DatawithCluster)o1).clusterno)
				return 1;
			else if(this.clusterno < ((DatawithCluster)o1).clusterno)
				return -1;
			else
				return 0;
		}
		
		DatawithCluster(int dataid,int clusterno){
			this.dataid=dataid;
			this.clusterno=clusterno;
		}
	}
	
	static class VATframe extends Frame {
		static final int recsize=1; 
		int[][] colormat;
		int datanum;

		void setpaindata(double[][] dismat){
			datanum=dismat.length;
			setSize(datanum*recsize,datanum*recsize);
			colormat=new int[datanum][datanum];
			double dismatmaxvalue=-1;
			for(int i=0;i<datanum;i++){
				for(int j=i;j<datanum;j++){
					if(dismatmaxvalue<dismat[i][j])
						dismatmaxvalue=dismat[i][j];
				}
			}
			if(dismatmaxvalue==0)
				return;
			for(int i=0;i<datanum;i++){
				for(int j=i;j<datanum;j++){
					colormat[i][j] =(int)((255*dismat[i][j])/dismatmaxvalue);
					colormat[j][i] = colormat[i][j];
				}
			}
		}
		public VATframe(){
			super("VAT");
			this.datanum=datanum;
			setLocation(10,10);
			//setVisible(true);
		}
		public void paint(Graphics g){
			super.paint(g);
			for(int i=0;i<datanum;i++){
				for(int j=i;j<datanum;j++){
					Color col=new Color(colormat[i][j],0,0);
					g.setColor(col);
					g.fillRect(i*5,j*5,5,5);
					g.fillRect(j*5,i*5,5,5);
				}
			}
			try {
				Thread.sleep(1000);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		public void copytofile(String filename) throws IOException{
	        BufferedImage capture = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
	        Graphics2D graphics2D = capture.createGraphics();		
	        this.paint(graphics2D);
			ImageIO.write(capture, "png", new File("ans\\", filename+".png"));
		}
	}
	
	public static double getXB(Instances data, Instances centroids, int[] res) {
		Instances newdata=new Instances(data);
		newdata.setClassIndex(-1);
		newdata.deleteAttributeAt(newdata.numAttributes()-1);
		double XB = 0;
		DissimilarityForKmodes disfun = new DissimilarityForKmodes();
		for (int i = 0; i < newdata.numInstances(); i++) {
			double dis2 = disfun.distance(newdata.instance(i),
					centroids.instance(res[i]));
			dis2 = dis2 * dis2;
			XB += dis2;
		}
		double mindis=-1;
		for(int i=0;i<centroids.numInstances();i++){
			for(int j=i+1;j<centroids.numInstances();j++){
				double dis2 = disfun.distance(centroids.instance(i),
						centroids.instance(j));
				if(dis2<mindis || mindis<1){
					mindis=dis2;
				}
			}
		}
		XB/=(newdata.numAttributes()*mindis);
		return XB;
	}

	public static double getCA(Instances data, int[] res){
		double CA=0;
		double result = 0;
		double[] tmpdclass = data.attributeToDoubleArray(data.numAttributes()-1);
		int[] oclass = new int[data.numInstances()];
		for(int i=0;i<tmpdclass.length;++i)
		{
			oclass[i]=(int)tmpdclass[i];
		}
		int[] tmpclass = oclass.clone();
		int[] tmpclusters = res.clone();
		
		Arrays.sort(tmpclusters);
		Arrays.sort(tmpclass);
		int[][] M = new int[tmpclass[tmpclass.length-1]+1][tmpclusters[tmpclusters.length-1]+1];
		
		for(int i=0;i<res.length;++i)
		{
			M[oclass[i]][res[i]]++;
		}
	//	for(int i=0;i<M.length;++i)
	//	{
	//		System.out.println(Arrays.toString(M[i]));
	//	}
		for(int i=0;i<M.length;++i)
		{
			int maxindex = -1;
			for(int j=0;j<M[0].length-1;++j)
			{
				if(M[i][j]<M[i][j+1])
					maxindex = j+1;
			}
			M[i][0]=maxindex;
		}

		for(int i=0;i<oclass.length;++i)
		{
			if(M[oclass[i]][0]==res[i])
				result++;
		}
		CA=(double)result/(double)data.numInstances();
		return CA;
	}
	
	
	public static double getIk(Instances data, Instances centroids, int[] res)
			throws Exception {
		Instances newdata=new Instances(data);
		newdata.setClassIndex(-1);
		newdata.deleteAttributeAt(newdata.numAttributes()-1);
		double Ik = 0;
		DissimilarityForKmodes disfun = new DissimilarityForKmodes();
		double K = centroids.numInstances();
		double Dk = 0;
		for (int i = 0; i < centroids.numInstances(); i++) {
			for (int j = i + 1; j < centroids.numInstances(); j++) {
				double dis = disfun.distance(centroids.instance(i),
						centroids.instance(j));
				if (dis > Dk)
					Dk = dis;
			}
		}
		double E1 = 0;
		
		Instances one_centroid=new Instances(newdata,1);
		Instance tmp=new Instance(one_centroid.numAttributes());
		one_centroid.add(tmp);
		for(int i=0;i<one_centroid.numAttributes();i++){
			one_centroid.instance(0).setValue(i, newdata.meanOrMode(i));
		}
		for (int i = 0; i < newdata.numInstances(); i++) {
			double dis = disfun.distance(newdata.instance(i),
					one_centroid.instance(0));
			E1 += dis;
		}

		double Ek = 0;
		for (int i = 0; i < newdata.numInstances(); i++) {
			double dis = disfun.distance(newdata.instance(i),
					centroids.instance(res[i]));
			Ek += dis;
		}
		Ik = Math.pow(((1 / K) * (E1 / Ek) * Dk), 2);
		return Ik;
	}
	
	public static VATframe drawVat(Instances data, int[] res){
		Instances newdata=new Instances(data);
		newdata.setClassIndex(-1);
		newdata.deleteAttributeAt(newdata.numAttributes()-1);

		DatawithCluster[] dwc=new DatawithCluster[newdata.numInstances()];
		for(int i=0;i<dwc.length;i++){
			dwc[i]=new DatawithCluster(i,res[i]);
			//dwc[i].dataid=i;
			//dwc[i].clusterno=res[i];
		}
		Arrays.sort(dwc);
		DissimilarityForKmodes disfun = new DissimilarityForKmodes();
		double dismat[][]=new double[newdata.numInstances()][newdata.numInstances()];
		for(int i=0;i<dismat.length;i++){
			for(int j=0;j<dismat[0].length;j++){
				dismat[i][j]=disfun.distance(newdata.instance(dwc[i].dataid),newdata.instance(dwc[j].dataid));
				dismat[j][i]=dismat[i][j];
			}
		}
		VATframe vf=new VATframe();
		vf.setpaindata(dismat);
		vf.setVisible(true);
		return vf;
	}
	
	
	public static void main(String[] args) throws Exception {
		int method=1;
		
		for(method=1;method<6;method++){
		String type="";
		String dataname="soybean";
		VATframe vf=null;		
		File f = new File(
				"D:\\CODE\\eclipseworkspace\\SVN_lib\\XattReduct\\mydata\\" + dataname + ".arff");
		// set data begin
		Instances dataset = new Instances(new FileReader(f));
		for(int i=0;i<dataset.numAttributes();i++){
			dataset.deleteWithMissing(i);
		}
		dataset.setClassIndex(dataset.numAttributes()-1);
		int[] res=new int[dataset.numInstances()];
		
		if(method==1){
		for(int i=0;i<res.length;i++)
			res[i]=(int) dataset.instance(i).classValue();
			vf=drawVat(dataset,res);
			type="org";
		}
		
		Instances newdata=new Instances(dataset);
		newdata.setClassIndex(-1);
		newdata.deleteAttributeAt(newdata.numAttributes()-1);
		int par=5;
		
		if(method==2){
			Kmode kmode=Kmode.RunWithDataset(newdata, dataset.classAttribute().numValues(), new Random(par));
			vf=drawVat(dataset,kmode.getfinalcluster());
			type="kmode";
		}else if(method==3){
			SAKmode kmode=SAKmode.RunWithDataset(newdata, dataset.classAttribute().numValues(), new Random(par));
			vf=drawVat(dataset,kmode.getfinalcluster());
			type="saw";
		}else if(method==4){
			SAKmode2 kmode=SAKmode2.RunWithDataset(newdata, dataset.classAttribute().numValues(), new Random(par));
			vf=drawVat(dataset,kmode.getfinalcluster());
			type="sac";
		}else if(method==5){
			SAKmode3 kmode=SAKmode3.RunWithDataset(newdata, dataset.classAttribute().numValues(), new Random(par));
			vf=drawVat(dataset,kmode.getfinalcluster());
			type="sawc";
		}

		
		vf.copytofile(dataname+"_"+type);
//		Rectangle captureSize = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()); //È«ÆÁÄ»
//		Robot rob = new Robot();
//		BufferedImage capture=rob.createScreenCapture(captureSize);
	}
	}
}
