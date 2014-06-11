package Part1;

public class oneClustererResult {
	public int[] cluster = null;
	public double semi_weigth = 0;
	public double nmi_weigth = 0;
	public double squarederror_weigth = 0;
	public double f_weigth = 0;
	public oneClustererResult(int n){
		cluster = new int[n];
	}
	public oneClustererResult(){
	}
}
