package Xreducer_struct;

import java.util.Arrays;
import java.util.Vector;

import myUtils.xMath;

public class ResData {
	public Vector<String> claAlgnames = new Vector<String>();
	public Vector<String> redAlgnames = new Vector<String>();
	public String dataInfo = "";
	public String dataname = "";
	public int numObjects = 0;
	public int numAttributes = 0; //不包括决策属性
	public int numNominalAttributes = 0;
	public int numNumericAttributes = 0;
	public int numClasses = 0;
	public Vector<int[]> seletAtt = new Vector<int[]>();
	

	public Vector<Vector<ResNode>> resDetail = new Vector<Vector<ResNode>>() ;
		
	public Vector<Integer> numRed =  new Vector<Integer>();
	public Vector<Double> timeRed =  new Vector<Double>();

}
