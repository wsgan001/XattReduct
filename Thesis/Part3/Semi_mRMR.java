package Part3;

 
 
import weka.core.Instances;

public class Semi_mRMR extends FSmethod {

	public double alpha = -1;
	public double Had = -1;
	public double Ha = -1;
	public double NMad = -1;
	public Semi_mRMR(Instances data, String algname, boolean[] labeltag, double alpha) {
		super(data, algname, labeltag);
		this.alpha = alpha;
		// TODO Auto-generated constructor stub
		int nLabel = Utils.booleanSelectedNum(labeltag);
		boolean[] A = Utils.Instances2FullBoolean(m_data);
		boolean[] D = Utils.Instances2DecBoolean(m_data);
		this.Ha = Utils_entropy.getInformationEntorpy(m_data, A); 
		if(nLabel>0){
			this.Had = Utils_entropy.getConditionalEntorpy(m_data, D, A,this.labeltag);
			this.NMad = Utils_entropy.getCA(m_data,  D, A,this.labeltag);
		}
		this.m_selectAtt = this.getSelectedAtt();
		 
	}

	@Override
	public boolean[] getReduceAtt() {
		// TODO Auto-generated method stub
		long time_start = Utils.getCurrenttime();	
		
		int nAttrNoDes = this.m_data.numAttributes()-1;
	 
		boolean[] Red = new boolean[nAttrNoDes+1];
		double[] SU_L = new double[nAttrNoDes];
		for(int i=0;i<nAttrNoDes;++i){
			boolean[] ai = new boolean[nAttrNoDes+1];
			ai[i]=true;
			boolean[] d = Utils.Instances2DecBoolean(m_data);
			SU_L[i]=this.getMeasureValue(m_data, ai, d,this.labeltag);
		}
		double[][] SU_ALL = new double[nAttrNoDes][nAttrNoDes];
		for(int i=0;i<nAttrNoDes;++i){
			boolean[] ai = new boolean[nAttrNoDes+1];
			ai[i]=true;
			for(int j=0;j<nAttrNoDes;++j){
				boolean[] aj = new boolean[nAttrNoDes+1];
				aj[j]=true;
				SU_ALL[i][j]=this.getMeasureValue(m_data,ai,aj);
			}
		}
		int nLabel = Utils.booleanSelectedNum(labeltag);
		if(nLabel==0){//如果per为0即退化到无监督
			int redind = -1;
			double MaxH = -1000000;
			for(int i=0;i<nAttrNoDes;++i){
				boolean[] ai = new boolean[nAttrNoDes+1];
				ai[i]=true;
				double tmp = Utils_entropy.getInformationEntorpy(m_data, ai);
				if(tmp>MaxH){
					redind = i;
					MaxH = tmp;
				}
			}
			Red[redind]=true;
		}
		do{
			int seAttr = -1;
			double MaxValue = -1000000;
			for(int i=0;i<nAttrNoDes;++i){
				if(!Red[i]){
					boolean[] ai = new boolean[nAttrNoDes+1];
					ai[i]=true;
					double Red_part1 = SU_L[i];
					//double Rud_part1 = this.alpha*Utils_entropy.getSU(this.m_data,ai,Red,Utils.getUnLabel(this.labeltag));
					double Rud_part1 = this.getMeasureValue(this.m_data,ai,Red,Utils.getUnLabel(this.labeltag));
					int nRed = Utils.booleanSelectedNum(Red);
					double Rud_part2 = 0;
					if(nRed>0){
						for(int j=0;j<nAttrNoDes;++j){
							if(Red[j])
							Rud_part2+=SU_ALL[i][j];
						}
						//Rud_part2 = (1-this.alpha)*Rud_part2/(double)nRed;
						Rud_part2 = Rud_part2/(double)nRed;
					}
					double Fai=Red_part1-Rud_part1-Rud_part2;
					if(Fai>MaxValue){
						seAttr = i;
						MaxValue = Fai;
					}
				}
			}
			Red[seAttr] = true;
		}while(!isStopping(Red));
		
		this.m_useTime = (Utils.getCurrenttime() - time_start)/(double)1000;
		this.m_numRed = Utils.booleanSelectedNum(Red);
		
		return Red;
	}

	private double getMeasureValue(Instances m_data, boolean[] ai, boolean[] aj) {
		// TODO Auto-generated method stub
		//return Utils_entropy.getSU(this.m_data,ai,aj);
		return Utils_entropy.getCA(this.m_data,ai,aj);
	}

	private double getMeasureValue(Instances m_data, boolean[] ai,
			boolean[] red, boolean[] unLabel) {
		// TODO Auto-generated method stub
		return Utils_entropy.getCA(this.m_data,ai,red,Utils.getUnLabel(this.labeltag));
	}

	@Override
	public boolean isStopping(boolean[] Red) {
		// TODO Auto-generated method stub
		// System.out.println("##########");
		boolean tag1 = false;
		double p1 = 0;
		if(this.Had!=-1){
			boolean[] D = Utils.Instances2DecBoolean(m_data);
			p1 = Utils_entropy.getCA(m_data, D, Red, this.labeltag);
			tag1=Math.abs(p1-this.NMad)<this.alpha*this.NMad;
			// System.out.println(tag1+":"+p1+":"+this.NMad);
		}
		else
		{
			tag1 = true;
		}
		double p2 = Utils_entropy.getInformationEntorpy(m_data, Red);
		boolean tag2 =Math.abs(p2-this.Ha)<this.alpha*this.Ha;
		//boolean tag2 = p2==this.Ha;
		// System.out.println(tag2+":"+p2+":"+this.Ha);
		//return tag1&&tag2;
		 return tag1||tag2;
		 
	}

}
