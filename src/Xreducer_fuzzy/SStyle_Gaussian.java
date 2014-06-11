package Xreducer_fuzzy;

public class SStyle_Gaussian extends SimilarityStyle{
	public double getSimilarityValue(double x, double y){
		//super.getSimilarityValue(x, y);
		if(Double.isNaN(x)||Double.isNaN(y))
			return 1;
		return Math.exp(-(x-y)*(x-y)/Vstdvar*Vstdvar);
	}
	public String getInfor(){
		return "Gaussian";
	}
}
