package Xreducer_gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.TextField;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Vector;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.table.AbstractTableModel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CombinedDomainXYPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.RectangleInsets;
import org.jfree.ui.RefineryUtilities;

import Xreducer_struct.globalValue;

public class resultFrame extends JDialog {

	public JTable jt = null;
	private JScrollPane jp = null;
	public resTable_model model = null;
	private JTabbedPane tabbedPane = null;
	private ChartPanel plot = null;
	private JScrollPane jp2 = null;
	private JTextArea rtext = null;
	
	
	public String[] algnames = null;
	public String[] redTimes = null;
	public String[] wlt = null;
	public String[] ACmean = null;
	public String[] ACstd = null;
	public String[] pval = null;
	public String[] rednum = null;
	public int index = -1;
	public int numcomAlg = -1;
	
	public resultFrame(){
		this.tabbedPane = new JTabbedPane();
		 this.model = new resTable_model(); 
	     this.jt = new JTable(model); 
	     this.jp= new JScrollPane(this.jt);
	     this.rtext=new JTextArea("",50,50);
	     this.rtext.setEditable(false);
	     
	     this.jp2 = new JScrollPane(this.rtext);
		this.tabbedPane.add("Ttest结果",this.jp);
		this.tabbedPane.add("Nibble结果",this.plot);
		this.tabbedPane.add("Latex结果",this.jp2);

		this.setTitle("结果");
		this.pack();
		this.setContentPane(tabbedPane);       	
		
		this.setSize(800, 500); 
       	RefineryUtilities.centerFrameOnScreen(this);//开始在中心位置显示
	}
	
	
	
	 //将double[][]转化为XYDataset
    private XYDataset createDataset(String key, double[] xdata, double[] ydata){

    	XYSeriesCollection xyseriescollection = new XYSeriesCollection();
    	int N = xdata.length;
    	XYSeries xyseries = new XYSeries(key);   	
    	for(int i=0;i<N;++i){
    			if(ydata[i]!=0)
    			 xyseries.add(xdata[i], ydata[i]);
    		}
    		xyseriescollection.addSeries(xyseries);
  
        return xyseriescollection;
    }
     
    //将XYDataset转化为JFreeChart
    private JFreeChart createChart(String title,String[] keys, double[][] xydata)
    {
    	int N = xydata.length;
    	int M = xydata[0].length;
    	XYDataset[] data = new XYDataset[N-1];
    	XYPlot[] xyplots = new XYPlot[N-1];
    	CombinedDomainXYPlot combineddomainxyplot = new CombinedDomainXYPlot(new NumberAxis("")); 
        combineddomainxyplot.setGap(10D);
        //combineddomainxyplot.setRangeAxis(N, axis)
        
        
        String[] yKeys = {"AC","newRed<->d","allRed<->newRed"};
    	for(int i=0;i<N-1;++i){
    		data[i] = createDataset(keys[i],xydata[N-1],xydata[i]);
    		XYLineAndShapeRenderer sr = new XYLineAndShapeRenderer();
            sr.setShapesVisible(true); 
            sr.setUseFillPaint(true);//空心
           
            
            NumberAxis numberaxisY = new NumberAxis(yKeys[i]); 
            numberaxisY.setAutoRangeIncludesZero(false);


            xyplots[i] = new XYPlot(data[i], null, numberaxisY, sr);
            //xyplots[i].setRangeAxisLocation(AxisLocation.BOTTOM_OR_LEFT);
            
            xyplots[i].setBackgroundPaint(Color.lightGray); //设置plot背景颜色
            xyplots[i].setAxisOffset(new RectangleInsets(5D, 5D, 5D, 5D));
            xyplots[i].setDomainGridlinePaint(Color.white);
            xyplots[i].setRangeGridlinePaint(Color.white);
           
            combineddomainxyplot.add(xyplots[i],1);
            
    	}            
            NumberAxis numberaxisX = new NumberAxis("Reduct Number");
            numberaxisX.setAutoRangeIncludesZero(false);
            numberaxisX.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
            combineddomainxyplot.setDomainAxis(numberaxisX);   // setDomainAxis 设置x轴  seRenderAxis 设置Y轴
        combineddomainxyplot.setOrientation(PlotOrientation.VERTICAL);
        JFreeChart jfreechart = new JFreeChart(title, JFreeChart.DEFAULT_TITLE_FONT, combineddomainxyplot, true);
        jfreechart.setBackgroundPaint(Color.white);//设置chart背景颜色
        return jfreechart;
    }
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		resultFrame t = new resultFrame();
		t.setVisible(true);
	}

	public void setFileres(int fileindex) {
		// TODO Auto-generated method stub
		
		this.index = fileindex;
		//this.algnames = globalValue.gcomf.onef.get(fileindex).algnames;
		this.numcomAlg = globalValue.gcomf.onef.get(fileindex).numcomAlg;
		this.algnames = new String[this.numcomAlg];
		for(int i=0;i<this.numcomAlg;++i){
			this.algnames[i]=globalValue.gcomf.onef.get(fileindex).algs.get(i).algname;
		}	
		int N = this.algnames.length;
		this.redTimes = new String[N];
		this.wlt = new String[N];
		this.ACmean = new String[N];
		this.ACstd = new String[N];
		this.pval = new String[N];
		this.rednum = new String[N];
		double[] rtemp = globalValue.gcomf.onef.get(fileindex).redTimes;
		for(int i=0;i<rtemp.length;++i){
			if(rtemp[i]<1){
				DecimalFormat df = new DecimalFormat("0.00");
				this.redTimes[i] = df.format(rtemp[i])+"s";
			}
			else{
				DecimalFormat df = new DecimalFormat("0.00");
				this.redTimes[i] = df.format(rtemp[i])+"s";
			}
			//this.redTimes[i] = Double.toString(rtemp[i])+"s";
		}
		double[][] ttemp = globalValue.gcomf.onef.get(fileindex).TtestRes;
		for(int i = 0;i<ttemp.length;++i){
			if(ttemp[i][2]==-1)
				this.wlt[i] = "Base";
			else if(ttemp[i][2]==1)
				this.wlt[i] = "Lose";
			else if(ttemp[i][2]==2)
				this.wlt[i] = "Win";
			else
				this.wlt[i] = "Tie";
			DecimalFormat df = new DecimalFormat("0.00");
			this.ACmean[i] = df.format(ttemp[i][0]*100);
			this.ACstd[i] = df.format(ttemp[i][1]*100);
			 
			if(Double.isNaN(ttemp[i][3])){
				this.pval[i] = "——";
			}
			else{
				this.pval[i] =  df.format(ttemp[i][3]);
			}
			rednum[i] = Integer.toString(globalValue.gcomf.onef.get(fileindex).algs.get(i).selectedAtt.length);
			
		}

		
      
        updateinfo();
	
        
        //更新latex结果
        String resstr = "数据名称："+globalValue.gcomf.onef.get(fileindex).filename+"\n";
        resstr += "约简结果\n";
        for(int i=0;i<N;++i){
        	resstr += algnames[i]+"\n";
        	resstr += Arrays.toString(globalValue.gcomf.onef.get(fileindex).selectAtts.get(i))+"\n";
        }
        resstr += "\n\n";
        for(int i=0;i<N;++i){
        	resstr += algnames[i]+"  ";
        }
        resstr += "\n约简个数和时间-latex格式\n";
        for(int i=0;i<N;++i){
        	resstr += this.rednum[i]+"&"+this.redTimes[i]+"&";
        }
        resstr += "\n约简时间-latex格式\n";
        for(int i=0;i<N;++i){
        	resstr += this.redTimes[i]+"&";
        }
        resstr += "\n约简个数-latex格式\n";
        for(int i=0;i<N;++i){
        	resstr += this.rednum[i]+"&";
        }
        
        resstr += "\n约简准确率-latex格式\n";
        resstr += this.ACmean[0]+"$\\pm$"+this.ACstd[0];
        resstr += "\n";
        for(int i=1;i<N;++i){
        	resstr += "&"+this.ACmean[i]+"$\\pm$"+this.ACstd[i];
        	String pc = "";
        	if (this.wlt[i] =="Win")
        		pc = "$^+$";
        	if (this.wlt[i] =="Lose")
        		pc = "$^-$";
        	resstr += "&"+ this.pval[i]+pc;
        	resstr += "\n";
        }
        


		this.tabbedPane.remove(1);

        JFreeChart jfreechart = createChart("Nibble Policy" ,globalValue.gcomf.onef.get(fileindex).keys, globalValue.gcomf.onef.get(fileindex).NSRes);
        //ChartPanel chartpanel = new ChartPanel(jfreechart);
        //chartpanel.setPreferredSize(new Dimension(900, 400)); //panel大小
        this.plot = new ChartPanel(jfreechart);
        this.tabbedPane.add("Nibble结果",this.plot);
        
        this.tabbedPane.remove(2);
	     this.jp2 = new JScrollPane(this.rtext);
        this.rtext.setText(resstr);

        this.tabbedPane.add("Latex结果", this.jp2);
	}
	
	public void updateinfo(){
		this.model.removeallRows();
		this.model.addData(this.algnames,this.ACmean,this.ACstd,this.wlt,this.pval,this.rednum ,this.redTimes);
		this.jt.updateUI(); 
	}
}

class resTable_model extends AbstractTableModel {
	private Vector<String[]> content = null; 
    private String[] title_name = { "Algorithm", "AC(%)", "Std(%)", "w/l/t", "pval","redNum", "Time"}; 
    public void addData(String[] algnames, String[] ACmean, String[] ACstd,	
    			   String[] wlt,String[] pval,String[] rednum,String[] redTimes){
    	int N = algnames.length;
    	int M = title_name.length;
    	for(int k=0;k<N;++k){
    		String[] temp = new String[M];
    		temp[0] = algnames[k];
    		temp[1] = ACmean[k];
    		temp[2] = ACstd[k];
    		temp[3] = wlt[k];
    		temp[4] = pval[k];
    		temp[5] = rednum[k];
    		temp[6] = redTimes[k];
    		this.content.add(temp);
    	}
    }

    
    public resTable_model() { 
        content = new Vector<String[]>(); 
    } 
	@Override
	public int getRowCount() {
		// TODO Auto-generated method stub
		return content.size(); 
	}

	@Override
	public int getColumnCount() {
		// TODO Auto-generated method stub
		 return title_name.length; 
	}
	public String getColumnName(int col) { 
	    return title_name[col]; 
	} 
	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		// TODO Auto-generated method stub
		return content.get(rowIndex)[columnIndex];   

	
	} 
    public void removeallRows() { 
       // for (int i = 0; i<content.size(); i++) {          
        //        content.remove(i); 
       // } 
    	content.clear();
    } 
}