package Xreducer_struct;

public class ResNode {
	public double elvMean = 0;
	public double elvStd = 0;
	public double pval = 0;
	public String ltw = ""; //l->"-"; t->" "; w->"+"
	private int ltwindex = -2; // base=0;
	public ResNode() {
		// TODO Auto-generated constructor stub
	}
	public ResNode(double mean, double std, double pval, int ltw){
		this.elvMean = mean;
		this.elvStd = std;
		this.pval=pval;
		this.setWLT(ltw);
	}
	public void setWLT(int intt) {
		// TODO Auto-generated method stub
		this.ltwindex = intt;
		switch(intt){
		
		case 0: {ltw="\\Large\\circ";break;}   //B
		case 1: {ltw="\\,+";break;}  // W
		case 2: {ltw="\\,\\circ";break;}//L
		case 3: {ltw="\\,-";break;}//T
		
		
//		case 0: {ltw="\\Large\\circ";break;}   //B
//		case 1: {ltw="\\Large\\textcolor{red}{\\checkmark}";break;}  // W
//		case 2: {ltw="\\,\\mathbf{\\times}";break;}//L
//		case 3: {ltw="\\,-";break;}//T
		
		/*case 0: {ltw="\\circ";break;}   //B
		case 1: {ltw="\\textcolor{red}{\\checkmark}";break;}  // W
		case 2: {ltw="\\mathbf{\\times}";break;}//L
		case 3: {ltw="-";break;}//T
		default:break;*/
		}
	}
	public int getWLT(){
		return this.ltwindex;
	}


}
