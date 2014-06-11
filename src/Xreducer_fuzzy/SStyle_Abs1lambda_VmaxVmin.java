package Xreducer_fuzzy;

public class SStyle_Abs1lambda_VmaxVmin  extends SimilarityStyle{
	public double lambda = 4;
	public SStyle_Abs1lambda_VmaxVmin(double lambda) {
		this.lambda = lambda;
	}
	public double getSimilarityValue(double x, double y){
		//super.getSimilarityValue(x, y);
		if(Double.isNaN(x)||Double.isNaN(y))
			return 1;
		if(Vmax==Vmin)
			return 1.0;
		double abs = Math.abs((x-y)/(Vmax-Vmin));
		if(abs<=1.0/lambda)
			abs = 1-4.0*abs; 
		else abs = 0.0;
		return abs;
	}
	public String getInfor(){
		return "AbsVmaxVmin";
	}
}
