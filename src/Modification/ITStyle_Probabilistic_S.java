package Modification;

import Xreducer_fuzzy.ImplicatorTnormStyle;

public class ITStyle_Probabilistic_S extends ImplicatorTnormStyle{
	public double getfuzzyTnromValue(double y, double x){
		return x*y;
	}
	public double getfuzzyImplicatorValue(double y, double x){
		return 1-x+(x*y);
		//return Math.max(x, 1-y);
	}
	public String getInfor(){
		return "Probabilistic_S";
	}
	@Override
	public String getInformation() {
		// TODO Auto-generated method stub
		return "Probabilistic_S";
	}
}
