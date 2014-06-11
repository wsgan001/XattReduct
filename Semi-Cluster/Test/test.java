package Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import myUtils.xFile;

import Part1.MCF_w_voting;
import Part1.MethodGenerateClustering;
import Test.ConMatrix.LinkType;

import com.mathworks.toolbox.javabuilder.MWClassID;
import com.mathworks.toolbox.javabuilder.MWException;
import com.mathworks.toolbox.javabuilder.MWNumericArray;

import showFigure.*;
import weka.core.Instances;

public class test {

	public static void test1(Instances p, double[][] Relation, int[] Res) throws MWException{
		Object[] rhs = new Object[3];
		int n = p.numInstances();
		double[][] pData = new double[n][3];
		for(int i=0;i<n;++i){
			pData[i][0]=p.instance(i).value(0);
			pData[i][1]=p.instance(i).value(1);
			pData[i][2]=p.instance(i).value(2);
		}
		rhs[0] = new MWNumericArray(pData, MWClassID.SINGLE);
		double [][] Con = Relation2Constraints(Relation);
		rhs[1] = new MWNumericArray(Con, MWClassID.SINGLE);
	    rhs[2] = new MWNumericArray(Res, MWClassID.SINGLE);
	    showFigureclass sf1 = new showFigureclass();
	    showFigureclass sf2 = new showFigureclass();
	    showFigureclass sf3 = new showFigureclass();
		//sf.showFigure(rhs);
	    Object[] rhs1 = new Object[1];
	    rhs1[0] = new MWNumericArray(pData, MWClassID.SINGLE);
	    //sf1.showFigureOriginal(rhs1);
	    
	    Object[] rhs2 = new Object[2];
	    rhs2[0] = new MWNumericArray(pData, MWClassID.SINGLE);
	    rhs2[1] = new MWNumericArray(Con, MWClassID.SINGLE);
	   //sf2.showFigureRelation(rhs2);
	    
	    
	    Object[] rhs3 = new Object[2];
	    rhs3[0] = new MWNumericArray(pData, MWClassID.SINGLE);
	    rhs3[1] = new MWNumericArray(Res, MWClassID.SINGLE);
	     sf3.showFigureResult(rhs3);
		
	}
	private static double[][] Relation2Constraints(double[][] relation) {
		// TODO Auto-generated method stub
		int cnt = 0;
		for(int i=0;i<relation.length;++i){
			for(int j=i+1;j<relation[i].length;++j){
				if(relation[i][j]!=0)
					cnt++;
			}
		}
		
		double [][] con = new double[cnt][3];
		cnt = 0;
		for(int i=0;i<relation.length;++i){
			for(int j=i+1;j<relation[i].length;++j){
				if(relation[i][j]!=0){
					con[cnt][0]=i+1;
				    con[cnt][1]=j+1;
				    con[cnt++][2]=relation[i][j];}
			}
		}
		return con;
	}
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
	/*	try{
			Object[] rhs = new Object[2];
			double[][] a = {{1,2,1},{2,5,1},{3,2,2}};
			double[][] b = {{1,2,1},{2,3,-1}};
			MWNumericArray A1 = new MWNumericArray(a, MWClassID.SINGLE);
			MWNumericArray A2 = new MWNumericArray(b, MWClassID.SINGLE);
			rhs[0] = A1;
			rhs[1] = A2;
			showFigureclass sf = new showFigureclass();
			sf.showFigure(rhs);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}*/
		String dataname = "half-rings";
    	//String dataname = "Aggregation";
    	//String dataname = "Compound";
		//String dataname = "twomoons";
    	//String dataname = "Pathbased";
    	//String dataname = "Spiral";
    	//String dataname = "D31";
    	//String dataname = "R15";
    	//String dataname = "Flame";
    	//String dataname = "three-rings";
    	//String dataname = "eclipse";
    	//String dataname = "Sub-Compound";
    	
    	
    	String path = "C:\\Users\\Eric\\Desktop\\2012Çï¶¬\\NO.4\\data\\"+dataname+".arff";
    	Instances data = new Instances(new FileReader(path));
		data.setClassIndex(data.numAttributes()-1);
		Instances dataforcluster = new Instances(new FileReader(path));
		dataforcluster.deleteAttributeAt(dataforcluster.numAttributes()-1);
		int rnd = 2;
		double per = 0.2;
		int ensize = 10;
		int type =0;//0 È«²¿ÊôÐÔ
		boolean[] labeled =ClusterEnsemble.getRandomLabeled(per,data.numInstances(),rnd);
		//String p = "D:\\TETST\\constraint.txt";
		writeLabeled2file(labeled);
		MethodGenerateClustering mc = new MethodGenerateClustering(data,dataforcluster,labeled,ensize,rnd++,type,0.5);
		ClusterEnsemble ce =new ClusterEnsemble(mc,new ConMatrix(LinkType.AVERAGE),false);
		//ClusterEnsemble ce =new ClusterEnsemble(mc,new ConMatrixMerit(),false);
		
		
		
		int[] res = ce.fr.cluster;
		String copRes = "D:\\TETST\\result.txt";
		//int[] res = readRes2Int(copRes,data.numInstances());
		//int[] res = {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,2,2,2,2,2,2,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2};
		test1(data,mc.labeledRelation,res);
		
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
		
		xFile.writeNewFile("D:\\TETST\\", "constraint.txt", str);	
	}
	private static int[] readRes2Int(String p,int n) throws IOException {
		// TODO Auto-generated method stub
		File file=new File(p);
        int[] res = new int[n];
        BufferedReader br=new BufferedReader(new FileReader(file));
        String temp=null;
         
        temp=br.readLine();
        int cnt = 0;
        while(temp!=null){
        	 
        	res[cnt++]=Integer.parseInt(temp);
        	temp=br.readLine();
        	
        } 
        return res;
	}

}
