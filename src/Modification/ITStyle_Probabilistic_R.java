package Modification;

import Xreducer_fuzzy.ImplicatorTnormStyle;

public class ITStyle_Probabilistic_R extends ImplicatorTnormStyle{
	public double getfuzzyTnromValue(double y, double x){
		return x*y;
	}
	public double getfuzzyImplicatorValue(double y, double x){
		return x<=y?1:y/x;
		//return Math.max(x, 1-y);
	}
	public String getInfor(){
		return "Probabilistic_R";
	}
	@Override
	public String getInformation() {
		// TODO Auto-generated method stub
		return "Probabilistic_R";
	}

}
