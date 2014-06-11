package Xreducer_fuzzy;

public class ITStyle_KleeneDienes extends ImplicatorTnormStyle{
	public double getfuzzyTnromValue(double y, double x){
		return Math.min(x, y);
	}
	public double getfuzzyImplicatorValue(double y, double x){
		return Math.max(1-x, y);
		//return Math.max(x, 1-y);
	}
	public String getInfor(){
		return "KleeneDienes";
	}
	@Override
	public String getInformation() {
		// TODO Auto-generated method stub
		return "KleeneDienes";
	}
}
