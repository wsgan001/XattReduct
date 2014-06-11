package featureselection;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Random;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import featureselection.SemiroughFS.evaluate_struct;

import weka.attributeSelection.AttributeSelection;
import weka.attributeSelection.CfsSubsetEval;
import weka.attributeSelection.GreedyStepwise;
import weka.clusterers.SimpleKMeans;
import weka.core.Instances;

public class SemiroughFS_heuristic {

	static int sumofdismatforlabel;
	static int sumofdismatforunlabel;
	
	static int[][] selectlabeldismat;
	static int[][] selectunlabeldismat;
	static double energy=1;
	static boolean unlabel_fill=true;
	static boolean ratioimprove=true;
	
	
	static XSSFWorkbook book;
	static XSSFSheet sheet;
	static XSSFRow row;
	static int method=1;
//	XSSFCol col;
	
	public static void computefulldismatvalue(Instances ldataset,Instances udataset){
//		fulldismatforlabel=new int[ldataset.numInstances()][ldataset.numInstances()];
		sumofdismatforlabel=0;
		sumofdismatforunlabel=0;
		int atrnum=ldataset.numAttributes()-1;
		for(int i=0;i<ldataset.numInstances();i++){
			for(int j=i+1;j<ldataset.numInstances();j++){
				for(int k=0;k<atrnum;k++){
					if(ldataset.instance(i).value(k)!=ldataset.instance(j).value(k) 
							&&ldataset.instance(i).classValue()!=ldataset.instance(j).classValue()){
//						fulldismatforlabel[i][k]=1;
//						fulldismatforlabel[k][i]=1;
						sumofdismatforlabel++;
						break;
					}
				}
			}
		}
		for(int i=0;i<udataset.numInstances();i++){
			for(int j=i+1;j<udataset.numInstances();j++){
				for(int k=0;k<atrnum;k++){
					if(udataset.instance(i).value(k)!=udataset.instance(j).value(k)){
						if(!unlabel_fill || udataset.instance(i).classValue()
								!=udataset.instance(j).classValue()){
							sumofdismatforunlabel++;
//							fulldismatforunlabel[i][k]=1;
//							fulldismatforunlabel[k][i]=1;
						}						
						break;
					}
				}
			}
		}
	}
	
	public static int getattimporve(Instances dataset,int newatt,boolean islabeled){
		int improve=0;
		for(int i=0;i<dataset.numInstances();i++){
			for(int j=i+1;j<dataset.numInstances();j++){
				if(dataset.instance(i).value(newatt)!=dataset.instance(j).value(newatt)){
					if(islabeled && selectlabeldismat[i][j]==0 && 
							dataset.instance(i).classValue()!=dataset.instance(j).classValue()){
						improve++;
					}else if(!islabeled && selectunlabeldismat[i][j]==0){
						if(!unlabel_fill || dataset.instance(i).classValue()
								!=dataset.instance(j).classValue()){
							improve++;
						}
					}
					
				}
			}
		}
		return improve;
	}
	
	public static int getdismatcount(Instances dataset,boolean[] att,boolean islabeled){
		int count=0;
		int atrnum=dataset.numAttributes()-1;
		for(int i=0;i<dataset.numInstances();i++){
			for(int j=i+1;j<dataset.numInstances();j++){
				for(int k=0;k<att.length;k++){
					if(att[k]&&dataset.instance(i).value(k)!=dataset.instance(j).value(k)){
						if(islabeled&&dataset.instance(i).classValue()!=dataset.instance(j).classValue()){
							count++;
							break;
						}else if(!islabeled){
							if(!unlabel_fill || dataset.instance(i).classValue()
									!=dataset.instance(j).classValue()){
								count++;
								break;
							}
						}
					}
				}
			}
		}
		return count;
	}
							
	
	public static void addnewatttodismat(Instances dataset,int newatt,boolean islabeled){
		for(int i=0;i<dataset.numInstances();i++){
			for(int j=i+1;j<dataset.numInstances();j++){
				if(dataset.instance(i).value(newatt)!=dataset.instance(j).value(newatt)){
					if(islabeled && 
							dataset.instance(i).classValue()!=dataset.instance(j).classValue()){
						selectlabeldismat[i][j]=1;
					}else if(!islabeled){
						if(!unlabel_fill || dataset.instance(i).classValue()
								!=dataset.instance(j).classValue()){
							selectunlabeldismat[i][j]=1;
						}
					}
					
				}
			}
		}
		
	}
		
	public static evaluate_struct semirough_heuristic(Instances dataset,
			Instances ldataset, Instances udataset, double fileno) throws Exception{
		evaluate_struct evaluatevalue;
//		Vector<evaluate_struct> evaluatevalueset = new Vector<evaluate_struct>();
		int atrnum=ldataset.numAttributes()-1;
		int curselectsumforlabel=0;
		int curselectsumforunlabel=0;
		int selectedattnum=0;
		computefulldismatvalue(ldataset,udataset);
		selectlabeldismat=new int[ldataset.numInstances()][ldataset.numInstances()];
		selectunlabeldismat=new int[udataset.numInstances()][udataset.numInstances()];
		for(int i=0;i<selectlabeldismat.length;i++)
			for(int j=0;j<selectlabeldismat[0].length;j++)
				selectlabeldismat[i][j]=0;
		for(int i=0;i<selectunlabeldismat.length;i++)
			for(int j=0;j<selectunlabeldismat[0].length;j++)
				selectunlabeldismat[i][j]=0;

		boolean[] curselectatt=new boolean[atrnum];
		Arrays.fill(curselectatt, false);		
		while( true ){
			if( curselectsumforlabel == sumofdismatforlabel
					&&  ((double)curselectsumforunlabel) - (energy* ((double)sumofdismatforunlabel)) > -1e-5 )
			{
					break;			
			}
			double maximsigl=-1;double maxsigu=-1;int sel=-1;
			int maximprovel=-1;int maximproveu=-1;
			for(int i=0;i<atrnum;i++){
				if(curselectatt[i]==true)
					continue;
				int improvel=getattimporve(ldataset,i,true);
				int improveu=getattimporve(udataset,i,false);
				double sigforl,sigforu;
				if(ratioimprove){
					if(sumofdismatforlabel>0){
						sigforl=((double)(curselectsumforlabel+improvel)) / ((double)sumofdismatforlabel);
					}else{
						sigforl=0;
					}
					if(sumofdismatforunlabel>0){
						sigforu=((double)(curselectsumforunlabel+improveu)) / ((double)sumofdismatforunlabel);
					}else{
						sigforu=0;
					}
				}else{
					sigforl=improvel;
					sigforu=improveu;
				}
				boolean flag=false;
				if(method==1){
					flag=(sigforl+ sigforu) > (maximsigl + maxsigu);
				}else{
					if(curselectsumforlabel < sumofdismatforlabel)
						flag=improvel > maximprovel;
					else
						flag=improveu > maximproveu;
				}
				if(flag){
	//			if(  (improvel+improveu) > (maximprovel + maximproveu) ){
					maximsigl=sigforl;
					maxsigu=sigforu;
					sel=i;
					maximprovel=improvel;
					maximproveu=improveu;
				}
			}
			addnewatttodismat(ldataset,sel,true);
			addnewatttodismat(udataset,sel,false);
			curselectsumforlabel+=maximprovel;
			curselectsumforunlabel+=maximproveu;
			curselectatt[sel]=true;
			selectedattnum++;
		}
		
		for (int i = 0; i < curselectatt.length; i++) {
			if (curselectatt[i]) {
				curselectatt[i] = false;
				int labelcount = getdismatcount(ldataset,curselectatt,true);
				int unlabelcount = getdismatcount(udataset,curselectatt,false);
				if(labelcount==sumofdismatforlabel
						&&  ((double)unlabelcount) - (energy* ((double)sumofdismatforunlabel)) > -1e-5 ){
					selectedattnum--;
				} else {
					curselectatt[i] = true;
				}
			}
		}
		int[] finalatt=new int[selectedattnum]; int pos=0;
		for(int i=0;i<curselectatt.length;i++){
			if(curselectatt[i]){
				finalatt[pos]=i;
				pos++;
			}
		}
		System.out.printf("select att: "+ selectedattnum+":");
		for(int i=0;i<finalatt.length;i++){
			System.out.printf( " " + (finalatt[i]+1));			
		}
		System.out.printf("\n");
		evaluatevalue = SemiroughFS.myevevaluate(dataset, finalatt, true);
		return evaluatevalue;
	}
	
	public static evaluate_struct wekatest(Instances alldataset,Instances ldataset) throws Exception{
		evaluate_struct evaluatevalue;
		AttributeSelection attsel = new AttributeSelection();
		CfsSubsetEval eval = new CfsSubsetEval();
//		ConsistencySubsetEval eval = new ConsistencySubsetEval();
		GreedyStepwise search = new GreedyStepwise();
//		search.setSearchBackwards(true);
		attsel.setEvaluator(eval);
		attsel.setSearch(search);
		attsel.SelectAttributes(ldataset);
		
		int[] tempatt =attsel.selectedAttributes();
		int[] fsatt=new int[tempatt.length-1];
		for(int i=0;i<fsatt.length;i++){
			fsatt[i]=tempatt[i];
		}
		
		evaluatevalue = SemiroughFS.myevevaluate(alldataset, fsatt, false);
		
		System.out.printf("select att: "+ fsatt.length+":");
		for(int i=0;i<fsatt.length;i++){
			System.out.printf( " " + (fsatt[i]+1));			
		}
		System.out.printf("\n");
		return evaluatevalue;
	}

	
	public static void writetobook(int curline, int curcol,
			evaluate_struct evaluatevalue) {
		book.getSheetAt(0).getRow(curline).createCell(curcol)
				.setCellValue(evaluatevalue.NNpct);
		book.getSheetAt(1).getRow(curline).createCell(curcol)
				.setCellValue(evaluatevalue.RBFpct);
		book.getSheetAt(2).getRow(curline).createCell(curcol)
		.setCellValue(evaluatevalue.Cartpct);
		book.getSheetAt(3).getRow(curline).createCell(curcol)
		.setCellValue(evaluatevalue.RandomForestpct);		
		book.getSheetAt(4).getRow(curline).createCell(curcol)
				.setCellValue(evaluatevalue.pos);
		book.getSheetAt(5).getRow(curline).createCell(curcol)
				.setCellValue(evaluatevalue.entropy);
		book.getSheetAt(6).getRow(curline).createCell(curcol)
				.setCellValue(evaluatevalue.appac);
		book.getSheetAt(7).getRow(curline).createCell(curcol)
				.setCellValue(evaluatevalue.length);
	}
	
	public static void outheader(BufferedWriter output,String dataname,String unlablraio) throws IOException{
		output.write("\\begin{table}[htbp]\n");
		output.write("\\begin{center}\n");
		output.write("\\caption{Experimental result for " + dataname + " with " +unlablraio+"\\% percents of unlabled data}\n");
		output.write("\\begin{tabular}{ c c c c c c }\n");
		output.write("\\hline\n");
		output.write(" & $|Pos_{A}(D)|$ & $H(D|A)$ & $Appaccurcy_{A}(U/D)$ & Classify accuracy & Length \\\\ \\hline  \n");
		
	}
	
	public static void outfooter(BufferedWriter output) throws IOException{
		output.write("\\hline\n");
		output.write("\\end{tabular}\n");
		output.write("\\end{center}\n");
		output.write("\\end{table}\n");		
	}
	
	public static void outline(BufferedWriter output,String method,evaluate_struct es) throws IOException{
		DecimalFormat df=new DecimalFormat("0.000");
		DecimalFormat df_p=new DecimalFormat("0");
		output.write(method + " & "+ df_p.format(es.pos) +" & " + df.format(es.entropy) +" & " + 
		df.format(es.appac)+ " & " + df.format(es.NNpct) + " & "+ df_p.format(es.length)+"  \\\\ \n");
	}
	
	public static void main(String[] args) throws Exception {
		
		unlabel_fill=true;
		ratioimprove=true;
		energy=1;
		
		String[] datastr = {"nursery"};//,"hepatitis","cylinder-bands","colic","hypothyroid","credit-a","credit-g"}; //"vote", "zoo","soybean","mushroom","tic-tac-toe", "mushroom","kr-vs-kp" 
		//"autos",
		//String[] datastr = {"test"};//test
		for (int dataset_iter = 0; dataset_iter < datastr.length; dataset_iter++) {
			String dataname = datastr[dataset_iter];
			File f = null;

			f = new File("E:\\CODE\\eclipse\\SVN_lib\\XattReduct\\mydata\\"
					+ dataname + ".arff");


			Instances dataset = new Instances(new FileReader(f));
			
			for(int i=0;i<dataset.numAttributes();i++){
				if(!dataset.attribute(i).isNominal())
					dataset.deleteAttributeAt(i);
			}
			
			for (int i = 0; i < dataset.numAttributes(); i++)
				dataset.deleteWithMissing(i);
			System.out.printf(dataset.numInstances() + " "+dataset.numAttributes()+"\n");
			
			
			Random random = new Random(0);
			dataset.randomize(random);			
			
			dataset.setClassIndex(dataset.numAttributes() - 1);
			
			String[] evalstr = { "1NNpct", "RBFpct","Cartpct" ,"RandomForestpct" , "Pos", "entropy", "appac", "Length" };
			String[] methodstr = { "Semi-rough-D","Semi-rough-P", "Dismat based","Unlabel rough","Entropy based","Pos based"};
			book = new XSSFWorkbook();
			for(int evaiter=0;evaiter<evalstr.length;evaiter++){
				sheet=book.createSheet(evalstr[evaiter]);
				row=sheet.createRow(0);
				row.createCell(0).setCellValue("Unlabel raito");
				for(int i=0;i<methodstr.length;i++){
					row.createCell(i+1).setCellValue(methodstr[i]);
				}
			}
			int curline = 0;

			double[] test_unlabel_ratio = new double[26];
			for(int i=0;i<test_unlabel_ratio.length;i++)
				test_unlabel_ratio[i]=((double)(50+2*i))/((double)100);
			
//			{ 0, 0.1, 0.2,0.3, 0.4, 0.5, 0.6, 0.7,0.8,0.9, 1 };
//			double[] test_unlabel_ratio = {0.6};
			for (int i = 0; i < test_unlabel_ratio.length; i++) {				
				double unlabel_ratio = test_unlabel_ratio[i];
				DecimalFormat outdf=new DecimalFormat("00");
				BufferedWriter output = null;
				File outfile = new File("ans\\" + dataname + outdf.format(unlabel_ratio*100)+"%.txt");
				outfile.createNewFile();
				output = new BufferedWriter(new FileWriter(outfile));
				outheader(output,dataname,outdf.format(unlabel_ratio*100));
				
				
				curline++;
				DecimalFormat df_ratio=new DecimalFormat("00");
				for(int evaiter=0;evaiter<evalstr.length;evaiter++){
					book.getSheetAt(evaiter).createRow(curline).createCell(0).setCellValue(df_ratio.format(unlabel_ratio*100) + "%");
				}
				
				int ulabelnum = (int) (dataset.numInstances() * unlabel_ratio);
				int labelnum = dataset.numInstances() - ulabelnum;
				Instances ldataset = null, udataset = null;
				ldataset = new Instances(dataset, labelnum);
				udataset = new Instances(dataset, ulabelnum);
				for (int j = 0; j < dataset.numInstances(); j++) {
					if (j < labelnum) {
						ldataset.add(dataset.instance(j));
					} else {
						udataset.add(dataset.instance(j));
					}
				}
				ldataset.setClassIndex(ldataset.numAttributes() - 1);
				udataset.setClassIndex(udataset.numAttributes()-1 );
				Instances nulldataset=new Instances(dataset,0);
//				Instances nullUdataset=new Instances(udataset,0);			
				if (udataset.numInstances() > 0 && unlabel_fill) {
					Instances tmpu=new Instances(udataset);
					tmpu.setClassIndex(-1);
					tmpu.deleteAttributeAt(tmpu.numAttributes()-1);
					SimpleKMeans cluster=new SimpleKMeans();
//					.kmode = Kmode.RunWithDataset(udataset, dataset
//							.classAttribute().numValues());
					cluster.setNumClusters(dataset.classAttribute().numValues());
					cluster.buildClusterer(tmpu);
//					int[] cluster = kmode.getfinalcluster();
					for (int j = 0; j < tmpu.numInstances(); j++) {
						int tmpcv=cluster.clusterInstance(tmpu.instance(j));
						udataset.instance(j).setClassValue(tmpcv);
					}
				}
				evaluate_struct evaluatevalue=null;
				DecimalFormat df=new DecimalFormat("0.0000");
				int curcol=1;
				System.out.printf("Semi-rough set-D\n"); 
				evaluatevalue=semirough_heuristic(dataset, ldataset, udataset, unlabel_ratio);
				System.out.printf("1NN:" + df.format(evaluatevalue.NNpct)+ 
						" RBFpct:"+df.format(evaluatevalue.RBFpct)+" Pos:"+evaluatevalue.pos+
						" entropy:"+df.format(evaluatevalue.entropy)+ " Appac:"+df.format(evaluatevalue.appac)+"\n");				
				writetobook(curline,curcol,evaluatevalue);
				outline(output,methodstr[0],evaluatevalue);
				
				curcol++;
				System.out.printf("Semi-rough set-P \n"); 
				evaluatevalue=Semiroughpos.semirough_heuristic(dataset, ldataset, udataset, unlabel_ratio);
				System.out.printf("1NN:" + df.format(evaluatevalue.NNpct)+ 
						" RBFpct:"+df.format(evaluatevalue.RBFpct)+" Pos:"+evaluatevalue.pos+
						" entropy:"+df.format(evaluatevalue.entropy)+ " Appac:"+df.format(evaluatevalue.appac)+"\n");				
				writetobook(curline,curcol,evaluatevalue);
				outline(output,methodstr[1],evaluatevalue);
				
				curcol++;
				if(ldataset.numInstances()>0){
					System.out.printf("Dismat based\n");
					evaluatevalue=semirough_heuristic(dataset, ldataset, nulldataset, unlabel_ratio);
					System.out.printf("1NN:" + df.format(evaluatevalue.NNpct)+ 
							" RBFpct:"+df.format(evaluatevalue.RBFpct)+" Pos:"+evaluatevalue.pos+
						" entropy:"+df.format(evaluatevalue.entropy)+ " Appac:"+df.format(evaluatevalue.appac)+"\n");
					writetobook(curline,curcol,evaluatevalue);
					outline(output,methodstr[2],evaluatevalue);
				}
				
				curcol++;
				if(udataset.numInstances()>0){
					System.out.printf("Unlabel rough set\n");
					evaluatevalue=semirough_heuristic(dataset, nulldataset, udataset, unlabel_ratio);
					System.out.printf("1NN:" + df.format(evaluatevalue.NNpct)+ 
							" RBFpct:"+df.format(evaluatevalue.RBFpct)+" Pos:"+evaluatevalue.pos+
						" entropy:"+df.format(evaluatevalue.entropy)+ " Appac:"+df.format(evaluatevalue.appac)+"\n");
					writetobook(curline,curcol,evaluatevalue);
					outline(output,methodstr[3],evaluatevalue);
				}
				

				curcol++;
				if (ldataset.numInstances() > 0) {
					System.out.printf("Entropy :\n");
					evaluatevalue = RSFS.entropybasedreduct(dataset,ldataset);
					System.out.printf("1NN:" + df.format(evaluatevalue.NNpct)+ 
							" RBFpct:"+df.format(evaluatevalue.RBFpct)+" Pos:"+evaluatevalue.pos+
						" entropy:"+df.format(evaluatevalue.entropy)+ " Appac:"+df.format(evaluatevalue.appac)+"\n");
					writetobook(curline,curcol,evaluatevalue);
					outline(output,methodstr[4],evaluatevalue);
				}
				
				curcol++;
				if (ldataset.numInstances() > 0) {
					System.out.printf("Entropy :\n");
					evaluatevalue = RSFS.posbasedreduct(dataset,ldataset);
					System.out.printf("1NN:" + df.format(evaluatevalue.NNpct)+ 
							" RBFpct:"+df.format(evaluatevalue.RBFpct)+" Pos:"+evaluatevalue.pos+
						" entropy:"+df.format(evaluatevalue.entropy)+ " Appac:"+df.format(evaluatevalue.appac)+"\n");
					writetobook(curline,curcol,evaluatevalue);
					outline(output,methodstr[5],evaluatevalue);
				}
				outfooter(output);
				output.close();
			}
			FileOutputStream fileOut;
			fileOut = new FileOutputStream("ans\\SemiRSFSh_"+dataname+".xlsx");
			book.write(fileOut);
			fileOut.close();
			System.out.println("写入成功，运行结束！");
		}
		return;
	}
}
