package Xreducer_struct;

import java.util.Vector;

import Xreducer_struct.oneFile.xNSstyle;



public class oneDataRecord {
	public String filepath = null;
	public String filename = null;
	public int ins = -1;
	public int att = -1;
	public int cla = -1;
	public int state = -1;
	public int progress = -1;
	public boolean ischoose = false;
	public boolean isexist = false;
	
	
	public int baseindex = -1;
	public double signifcantlevel = -1; 
	public int numReduce = -1;
	public int numRun = -1;
	public String trainclname = null;
	public int numFold = -1;
	//public Vector<oneAlgorithm> algs = null;
	public int numcomAlg = -1;
	public int NSindex = -1;
	public xNSstyle NSstyle = null;
	public int NSmaxIndex = -1;
	public String NSclname = null;
	//public double[][] NSRes = null;
	public int randomI = -1;

	
	
	//public String[] algnames = null;
	//public Vector<int[]> selectAtts =  null;
	//public double[] redTimes = null;
	//public double[][] TtestRes = null;
	
	/*********************algs****************************/
	public Vector<oneAlgRecord> algs;

}
