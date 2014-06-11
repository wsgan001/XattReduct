package Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Vector;

import weka.core.Instances;

import myUtils.xFile;
import myUtils.xMath;
 
import Part1.finalClustererResult;
import Part1.oneClustererResult;
import Test.ConMatrix.LinkType;

public class ConMatrixMerit extends MethodConsensusFunction{

	public ConMatrixMerit(LinkType lt) {
		super(lt);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	@Override
	public finalClustererResult getFinalCluster(
			Vector<oneClustererResult> clusterers,double[][] labeledRx, int k) throws Exception {
		// TODO Auto-generated method stub
		double[][] labeledR = labeledRx.clone();
		int N = clusterers.elementAt(0).cluster.length;
		int[] res  = new int[N];
		int t = clusterers.size();
		
		double[][] matrix = new double[N][N];
		 
		for(int i=0;i<N;++i){
			for(int j=i;j<N;++j){
				double tmp=0;
				for(int q=0;q<t;++q){
					double  weg = clusterers.elementAt(q).f_weigth;
					if(clusterers.elementAt(q).cluster[i]==clusterers.elementAt(q).cluster[j])
						tmp+=weg;
						//tmp++;
				}
				matrix[i][j]=tmp;
				matrix[j][i]=tmp;
				
			}
		}
		
		int linecnt = 0;
		double max=-1;
		for(int i=0;i<N;++i){
			for(int j=i+1;j<N;++j){			 
				if(labeledR[i][j]==1||(labeledR[i][j]==0&&matrix[i][j]>0))
					 linecnt++;
				if(matrix[i][j]>max)
					max=matrix[i][j];
				labeledR[j][i]=labeledR[i][j];
				 
			}
		}
		
		
		String str = N+" "+linecnt+" "+"001\n";
		for(int i=0;i<N;++i){
			String tmpLine = "";
			for(int j=0;j<N;++j){
				if(i!=j){
					String strtmp = "";
					if(labeledR[i][j]==1){
						strtmp = (j+1)+" "+(int)(2*max)+" ";
						
						}
					else if(labeledR[i][j]==0&&matrix[i][j]>0){
						strtmp = (j+1)+" "+(int)(matrix[i][j]*100)+" ";
						
					}
					//String value = xMath.doubleFormat("0", matrix[i][j]);
						
					tmpLine = tmpLine + strtmp;
				}
			}
			//System.out.println(tmpLine);
			str =str+ tmpLine + "\n";
		}
		
		
	    xFile.writeNewFile("D:\\TETST\\", "graphis.txt", str);	
		String pf = "D:\\TETST\\graphis.txt";
		//System.out.println("C:\\Users\\Eric\\Desktop\\2012秋冬\\NO.4\\code\\metis-5.0.3\\programs\\Release\\gpmetis"+pf+" "+k);
	    try {
            // Windows 系统的计算器程序
            String cmd = "C:\\Users\\Eric\\Desktop\\2012秋冬\\NO.4\\code\\metis-5.0.3\\programs\\Release\\gpmetis.exe "+pf+" "+k;
            
            // 创建一个本机进程
            Process p = Runtime.getRuntime().exec(cmd);
            
            // 等待 Process 执行完毕再继续向下运行
           // p.waitFor();
            
        }catch (IOException ex){
            ex.printStackTrace();
        } 

        //System.out.println("transfer finish");
        
        File file=new File("D:\\TETST\\graphis.txt.part."+k);
        if(!file.exists()||file.isDirectory())throw new FileNotFoundException();
        BufferedReader br=new BufferedReader(new FileReader(file));
        String temp=null;
        StringBuffer sb=new StringBuffer();
        temp=br.readLine();
        int cnt = 0;
        while(temp!=null){
        	sb.append(temp+" ");
        	res[cnt++]=Integer.parseInt(temp);
        	temp=br.readLine();
        	
        } 
	//System.out.println(sb);
        
        

		finalClustererResult fc = new finalClustererResult();
		fc.cluster = res.clone();
		return fc;
	}

	@Override
	public finalClustererResult getFinalCluster(
			Vector<oneClustererResult> clusterers, int k) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public finalClustererResult getFinalCluster(
			Vector<oneClustererResult> clusterers, double[][] LRx, LinkType lt, Instances data,int k)
			throws Exception {
		// TODO Auto-generated method stub
		return getFinalCluster(clusterers,LRx,  k);
	}

}
