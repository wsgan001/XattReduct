package Xreducer_fuzzy;

public class SStyle_Line extends SimilarityStyle{
	public double getSimilarityValue(double x, double y){
		//super.getSimilarityValue(x, y);
		if(Double.isNaN(x)||Double.isNaN(y))
			return 1;
		return 1-Math.abs((x-y)/(Vmax-Vmin));
	}
	public String getInfor(){
		return "Line";
	}
}
