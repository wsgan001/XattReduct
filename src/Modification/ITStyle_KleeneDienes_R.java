package Modification;

import Xreducer_fuzzy.ImplicatorTnormStyle;

public class ITStyle_KleeneDienes_R extends ImplicatorTnormStyle{
	public double getfuzzyTnromValue(double y, double x){
		return Math.min(x, y);
	}
	public double getfuzzyImplicatorValue(double y, double x){
		return x<=y?1:y;
		//return Math.max(x, 1-y);
	}
	public String getInfor(){
		return "KleeneDienes_R";
	}
	@Override
	public String getInformation() {
		// TODO Auto-generated method stub
		return "KleeneDienes_R";
	}

}
