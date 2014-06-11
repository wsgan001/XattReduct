package myUtils;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.JFrame;


import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CombinedDomainXYPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.RectangleInsets;
import org.jfree.ui.RefineryUtilities;

public class xFigure {
	 //将double[][]转化为XYDataset
    private static XYDataset createDataset(Map<String,double[][]> xydata){//[0][i] x [1][i] y

    	XYSeriesCollection xyseriescollection = new XYSeriesCollection();
    	 
    	Iterator iter = xydata.entrySet().iterator();
    	
    	while(iter.hasNext()){
    		Map.Entry en = (Map.Entry)iter.next();
    		XYSeries xyseries = new XYSeries(en.getKey().toString()); 
    		double[][] val = (double[][])en.getValue(); 
    			for(int j=0;j<val[0].length;++j)
    			    xyseries.add(val[0][j], val[1][j]);
    		xyseriescollection.addSeries(xyseries);
    	 }
        return xyseriescollection;
    }
     
    //将XYDataset转化为JFreeChart
    private  static JFreeChart createChart(String[] titles, Map<String,double[][]> xydata) //Vector<double[][]> xydata  xydata(i)[0]-->x   xydata(i)[1]-->y  
    {        
    	XYDataset data = createDataset(xydata); 
        JFreeChart jfreechart = ChartFactory.createXYLineChart(titles[0],titles[2],titles[1], data, PlotOrientation.VERTICAL, true, true, false);
        jfreechart.setBackgroundPaint(Color.white);//设置chart背景颜色
        XYPlot xyplot = (XYPlot)jfreechart.getPlot();
       xyplot.setBackgroundPaint(Color.lightGray); //设置plot背景颜色
         //xyplot.setBackgroundPaint(Color.white); //设置plot背景颜色
        xyplot.setAxisOffset(new RectangleInsets(5D, 5D, 5D, 5D));
        xyplot.setDomainGridlinePaint(Color.white);
        xyplot.setRangeGridlinePaint(Color.white);
        
        XYLineAndShapeRenderer a = (XYLineAndShapeRenderer)xyplot.getRenderer();
        //a.setSeriesShapesVisible(0, true); //是否有点
        //a.setSeriesShapesVisible(1, false);
        //a.setSeriesShapesVisible(2, true);
        //a.setSeriesLinesVisible(2, false); //显示连线
        a.setSeriesPaint(2, Color.DARK_GRAY); //设置线条颜色
        a.setShapesVisible(true);
        
        //a.setSeriesShape(2, ShapeUtilities.createDiamond(4F));
        //a.setDrawOutlines(true);
        a.setUseFillPaint(true);//空心
        
        //XYLineAndShapeRenderer xylineandshaperenderer = (XYLineAndShapeRenderer)xyplot.getRenderer();
        //xylineandshaperenderer.setShapesVisible(true);
        //a.setShapesFilled(true);
 
        
        //NumberAxis numberaxis = (NumberAxis)xyplot.getRangeAxis();                 //设置y轴为整数
        //numberaxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());  

        
        //NumberAxis numberaxis = (NumberAxis)xyplot.getDomainAxis();                 //设置y轴为整数
        //numberaxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());  
        return jfreechart;
    }
    
    
    private  static JFreeChart createChart(String[] titles, Vector<Map<String,double[][]>> xydata) //Vector<double[][]> xydata  xydata(i)[0]-->x   xydata(i)[1]-->y  
    {       
    	String[] yKeys = new String[xydata.size()];
    	int yk = 0;
    	for(int k=1;k<titles.length-1;++k){
    		yKeys[yk++] = titles[k];
    	}
    	CombinedDomainXYPlot combineddomainxyplot = new CombinedDomainXYPlot(new NumberAxis("")); 
        combineddomainxyplot.setGap(10D);
    	for(int x=0;x<xydata.size();++x){
    		XYDataset data = createDataset(xydata.elementAt(x)); 
    		XYLineAndShapeRenderer sr = new XYLineAndShapeRenderer();
            sr.setShapesVisible(true); 
            sr.setUseFillPaint(true);//空心
           
            
            NumberAxis numberaxisY = new NumberAxis(yKeys[x]); 
            numberaxisY.setAutoRangeIncludesZero(false);


            XYPlot xyplots = new XYPlot(data, null, numberaxisY, sr);
            //xyplots.setRangeAxisLocation(AxisLocation.BOTTOM_OR_LEFT);
            
            xyplots.setBackgroundPaint(Color.lightGray); //设置plot背景颜色
            xyplots.setAxisOffset(new RectangleInsets(5D, 5D, 5D, 5D));
            xyplots.setDomainGridlinePaint(Color.white);
            xyplots.setRangeGridlinePaint(Color.white);
           
            combineddomainxyplot.add(xyplots,1);
    	}
    	
        NumberAxis numberaxisX = new NumberAxis(titles[titles.length-1]);
        //numberaxisX.setAutoRangeIncludesZero(false);
       // numberaxisX.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
       // combineddomainxyplot.setDomainAxis(numberaxisX);   // setDomainAxis 设置x轴  seRenderAxis 设置Y轴
    combineddomainxyplot.setOrientation(PlotOrientation.VERTICAL);
    JFreeChart jfreechart = new JFreeChart(titles[0], JFreeChart.DEFAULT_TITLE_FONT, combineddomainxyplot, true);
    jfreechart.setBackgroundPaint(Color.white);//设置chart背景颜色
    return jfreechart;
    }
    
    
    public static void showFigure(String[] titles, Map<String,double[][]> xydata){
        JFreeChart jfreechart = xFigure.createChart(titles, xydata);
        
        ChartPanel chartpanel = new ChartPanel(jfreechart);
        chartpanel.setPreferredSize(new Dimension(900,600)); //panel大小
   
    	
    	JFrame jp = new JFrame();
    	jp.add(chartpanel);
    	jp.setSize(900,600);
    	jp.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    	jp.setVisible(true);

    	RefineryUtilities.centerFrameOnScreen(jp);//开始在中心位置显示
    }
    
    public static void showFigure(String[] titles, int[] dimension,  Map<String,double[][]> xydata){
        JFreeChart jfreechart = xFigure.createChart(titles, xydata);
        
        ChartPanel chartpanel = new ChartPanel(jfreechart);
        chartpanel.setPreferredSize(new Dimension(dimension[0],dimension[1])); //panel大小
   
    	
    	JFrame jp = new JFrame();
    	jp.add(chartpanel);
    	jp.setSize(dimension[0],dimension[1]);
    	jp.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    	jp.setVisible(true);

    	RefineryUtilities.centerFrameOnScreen(jp);//开始在中心位置显示
    }
    
    public static void showFigure(String[] titles, int[] dimension, Vector<Map<String,double[][]>> xydata){
        JFreeChart jfreechart = xFigure.createChart(titles, xydata);
        
        ChartPanel chartpanel = new ChartPanel(jfreechart);
        chartpanel.setPreferredSize(new Dimension(dimension[0],dimension[1])); //panel大小
   
    	
    	JFrame jp = new JFrame();
    	jp.add(chartpanel);
    	jp.setSize(dimension[0],dimension[1]);
    	jp.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    	jp.setVisible(true);

    	RefineryUtilities.centerFrameOnScreen(jp);//开始在中心位置显示
    }
    
    public static void showFigure(String[] titles, Vector<Map<String,double[][]>> xydata){
        JFreeChart jfreechart = xFigure.createChart(titles, xydata);
        
        ChartPanel chartpanel = new ChartPanel(jfreechart);
        chartpanel.setPreferredSize(new Dimension(900,600)); //panel大小
   
    	
    	JFrame jp = new JFrame();
    	jp.add(chartpanel);
    	jp.setSize(900,600);
    	jp.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    	jp.setVisible(true);

    	RefineryUtilities.centerFrameOnScreen(jp);//开始在中心位置显示
    }
    
    
    
    
    
    public static void saveFigure(String[] titles, String fpath, Map<String,double[][]> xydata){
    	String fn = fpath+titles[0]+".png";
        JFreeChart jfreechart = xFigure.createChart(titles, xydata);
        
        ChartPanel chartpanel = new ChartPanel(jfreechart);
        chartpanel.setPreferredSize(new Dimension(900,600)); //panel大小
   
        BufferedImage bi = jfreechart.createBufferedImage(900, 600, BufferedImage.TYPE_INT_ARGB, null);
        
        
        try{
        	ImageIO.write(bi, "png", new File(fn));
            bi.flush();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    public static void saveFigure(String[] titles, String fpath, int[] dimension, Map<String,double[][]> xydata){
    	String fn = fpath+titles[0]+".png";
        JFreeChart jfreechart = xFigure.createChart(titles, xydata);
        
        ChartPanel chartpanel = new ChartPanel(jfreechart);
        chartpanel.setPreferredSize(new Dimension(dimension[0],dimension[1])); //panel大小
   
        BufferedImage bi = jfreechart.createBufferedImage(dimension[0],dimension[1], BufferedImage.TYPE_INT_ARGB, null);
        
        
        try{
        	ImageIO.write(bi, "png", new File(fn));
            bi.flush();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    public static void saveFigure(String[] titles, String fpath, Vector<Map<String,double[][]>> xydata){
    	String fn = fpath+titles[0]+".png";
        JFreeChart jfreechart = xFigure.createChart(titles, xydata);
        
        ChartPanel chartpanel = new ChartPanel(jfreechart);
        chartpanel.setPreferredSize(new Dimension(900,600)); //panel大小
   
        BufferedImage bi = jfreechart.createBufferedImage(900, 600, BufferedImage.TYPE_INT_ARGB, null);
        
        
        try{
        	ImageIO.write(bi, "png", new File(fn));
            bi.flush();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    public static void saveFigure(String[] titles, String fpath, int[] dimension, Vector<Map<String,double[][]>> xydata){
    	String fn = fpath+titles[0]+".png";
        JFreeChart jfreechart = xFigure.createChart(titles, xydata);
        
        ChartPanel chartpanel = new ChartPanel(jfreechart);
        chartpanel.setPreferredSize(new Dimension(dimension[0],dimension[1])); //panel大小
   
        BufferedImage bi = jfreechart.createBufferedImage(dimension[0],dimension[1], BufferedImage.TYPE_INT_ARGB, null);
        
        
        try{
        	ImageIO.write(bi, "png", new File(fn));
            bi.flush();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    public static void main(String[] args) {
		// TODO Auto-generated method stub
    	String[] x = new String[4];
    	x[0]="xx"; //标题
    	x[1]="y1"; //y轴
    	x[2]="y2"; //y轴
    	x[3]="xx"; //y轴
    	String path = "C:/Users/Eric/Desktop/2012春/Modification.NO3/结果/1.with att/";
    	Map<String,double[][]> mxy = new HashMap<String,double[][]>();
    	double[][] xy1={{1,2,3,4},{1,1,5,8}};
    	double[][] xy2={{1,2,4},{1,1,50}};
    	mxy.put("x1", xy1);
    	mxy.put("x2", xy2);
    	
    	Vector<Map<String,double[][]>> allxy = new Vector<Map<String,double[][]>>();
    	allxy.add(mxy);
    	allxy.add(mxy);

    	//xFigure.showFigure(x, mxy);
    	//xFigure.saveFigure(x, path, mxy);
    	xFigure.showFigure(x, allxy);
	}

}
