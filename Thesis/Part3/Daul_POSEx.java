package Part3;

import weka.core.Instances;

public class Daul_POSEx extends FSmethod {

	public double alpha = -1;

	public Daul_POSEx(Instances data, String algname, boolean[] labeltag, double alpha) {
		super(data, algname, labeltag);
		// TODO Auto-generated constructor stub
		// TODO Auto-generated constructor stub
		this.alpha = alpha;
		//this.alpha = this.m_data.numClasses();

		this.m_selectAtt = this.getSelectedAtt();
	}

	@Override
	public boolean[] getReduceAtt() {
		// TODO Auto-generated method stub
long time_start = Utils.getCurrenttime();	
		
		int nAttrNoDes = this.m_data.numAttributes()-1;
	 
		boolean[] Red = new boolean[nAttrNoDes+1];
	 
		boolean[] A = Utils.Instances2FullBoolean(this.m_data);
		boolean[] D = Utils.Instances2DecBoolean(this.m_data);
		
	
		int nAttr = this.m_data.numAttributes()-1;
		int cnt = 0;
		boolean[] isTest = new boolean[nAttr];
		while(cnt<nAttr){
			double bestValue = -1;
			int bestInd = -1;
			for(int i=0;i<nAttr;++i){
				boolean[] tmpRed = Red.clone();
				tmpRed[i]=true;
				if(!isTest[i]){
					double posD = Utils_entropy.getCA(m_data, D, tmpRed, labeltag);
					if(posD>bestValue){
						bestValue = posD;
						bestInd = i;
					}
				}
			}
			boolean[] tmpRed = new boolean[nAttr+1];
			tmpRed[bestInd] = true;
			double posRed = getDaulPos(Red,tmpRed);
			double com1 = this.alpha*bestValue;
			System.out.println(com1+":"+posRed);
			if(com1>posRed)
				Red[bestInd]=true;
			isTest[bestInd]=true;
			cnt++;
		}
		
		
		
		
		
		
		this.m_useTime = (Utils.getCurrenttime() - time_start)/(double)1000;
		this.m_numRed = Utils.booleanSelectedNum(Red);
		
		return Red;
	}
	private double getDaulPos(boolean[] x, boolean[] y) {
		// TODO Auto-generated method stub
		double ans = Utils_entropy.getGranulation(m_data, x, y)+Utils_entropy.getGranulation(m_data, y, x);
		return ans/(double)2;
	}

	@Override
	public boolean isStopping(boolean[] Red) {
		// TODO Auto-generated method stub
		return false;
	}

}
