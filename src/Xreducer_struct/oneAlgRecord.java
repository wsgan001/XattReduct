package Xreducer_struct;

import Xreducer_struct.oneAlgorithm.xCategory;
import Xreducer_struct.oneAlgorithm.xStyle;

public class oneAlgRecord {
	public xCategory alg_category;
	public xStyle alg_style;
	public int alg_ID; //���
	public String alg_algname = null; //�㷨����
	public double alg_alpha = -1; //�㷨����
	public String alg_eval = null;
	public String alg_search = null;
	public boolean alg_flag = false;
	
	
	public int alg_numReduce = -1;
	public int alg_numRun = -1;
	public String alg_clname = null;
	public int alg_numFold = -1;
	
	
	//public String startTime = "";
	//public String endTime = "";
	//public double redTime = 0.0; //Լ��ʱ��
	//public double trainTime = 0.0; //ѵ��ʱ��
	//public int[] selectedAtt = null; //��ѡ����
	//public double[] ACs = null;
}
