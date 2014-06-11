package Xreducer_fuzzy;

public class SStyle_Abs1lambda_Vmax extends SimilarityStyle{
	public double lambda = 4;
	public SStyle_Abs1lambda_Vmax(double lambda) {
		this.lambda = lambda;
	}
	public double getSimilarityValue(double x, double y){
		//super.getSimilarityValue(x, y);
		if(Double.isNaN(x)||Double.isNaN(y))
		return 1;
		if(Vmax==Vmin)
			return 1;
		if(Vmax==0)
			Vmax=Vmin;
		double abs = Math.abs((x-y)/Vmax);
		if(abs<=1.0/lambda)
			abs = 1-4.0*abs; 
		else abs = 0.0;
		return abs;
	}	
	public String getInfor(){
		return "AbsVmax";
	}
}
