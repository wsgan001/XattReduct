package FCBFandRS;

import Xreducer_core.Utils;
import weka.core.Instances;

public class Fullset extends FSmethod {

	public Fullset(Instances data) {
		super(data);
		this.algname = "FullSetÈ«¼¯";
		this.m_selectAtt = getSelectedAtt();
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean[] getReduceAtt() {
		// TODO Auto-generated method stub
		boolean[] newB = Utils.Instances2FullBoolean(m_data);
		this.m_useTime = 0;
		this.m_numRed = Utils.booleanSelectedNum(newB);
		return newB;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
