package Part1;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
 
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import myUtils.xFigure;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CombinedDomainXYPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.RectangleInsets;
import org.jfree.ui.RefineryUtilities;

import JGnuplot.JavaPlot;

import com.panayotis.gnuplot.plot.AbstractPlot;
import com.panayotis.gnuplot.plot.DataSetPlot;
import com.panayotis.gnuplot.style.NamedPlotColor;
import com.panayotis.gnuplot.style.PlotStyle;
import com.panayotis.gnuplot.style.Style;
import com.panayotis.gnuplot.terminal.PostscriptTerminal;
 

public class xFigurePart1 {
	 //将double[][]转化为XYDataset
    public static XYDataset createDataset(Map<String,double[][]> xydata){//[0][i] x [1][i] y

    	XYSeriesCollection data = new XYSeriesCollection();
    	 
    	Iterator iter = xydata.entrySet().iterator();
    	while(iter.hasNext()){
    		Map.Entry en = (Map.Entry)iter.next();
    		XYSeries xyseries = new XYSeries(en.getKey().toString()); 
    		double[][] val = (double[][])en.getValue(); 
    			for(int j=0;j<val[0].length;++j)
    			    xyseries.add(val[0][j], val[1][j]);
    		data.addSeries(xyseries);
    	 }
        return data;
    }
     
    //将XYDataset转化为JFreeChart
    public  static JFreeChart createChart(String[] titles, Map<String,double[][]> xydata) //Vector<double[][]> xydata  xydata(i)[0]-->x   xydata(i)[1]-->y  
    {        
    	XYDataset data = createDataset(xydata); 
        JFreeChart jfreechart = ChartFactory.createXYLineChart(titles[0],titles[2],titles[1], data, PlotOrientation.VERTICAL, true, true, false);
        jfreechart.setBackgroundPaint(Color.white);//设置chart背景颜色
 
        XYPlot xyplot = (XYPlot)jfreechart.getPlot();
        xyplot.setBackgroundPaint(Color.white); //设置plot背景颜色
          //xyplot.setBackgroundPaint(Color.white); //设置plot背景颜色
         xyplot.setAxisOffset(new RectangleInsets(5D, 5D, 5D, 5D));
         xyplot.setDomainGridlinePaint(Color.white);
         xyplot.setRangeGridlinePaint(Color.white);
         xyplot.setRangeGridlinesVisible(true);
         xyplot.setRangeGridlinePaint(Color.black);
         
         XYLineAndShapeRenderer render = (XYLineAndShapeRenderer)xyplot.getRenderer();
         /*render.setSeriesPaint(0, Color.RED); //设置线条颜色
         render.setSeriesPaint(1, Color.MAGENTA); //设置线条颜色*/
         render.setSeriesPaint(2, Color.ORANGE); //设置线条颜色
         render.setSeriesPaint(3, Color.DARK_GRAY); //设置线条颜色
         render.setSeriesPaint(4, Color.MAGENTA); //设置线条颜色
         render.setSeriesShapesVisible(0,true); 
         render.setSeriesShapesVisible(1,true); 
         render.setSeriesShapesVisible(2,true); 
         render.setSeriesShapesVisible(3,true); 
         render.setUseFillPaint(true);//空心*/
         
         render.setSeriesStroke(4, new BasicStroke(1.2F, 1, 1, 1F, new float[] {10F, 5F}, 0.0F));
         render.setSeriesShapesVisible(4, false);
        
         
      
         //jfreechart.getLegend().setVisible(true);
         //jfreechart.getLegend().setPosition(RectangleEdge.RIGHT);
      /*  xyplot.setDataset(data);
          xyplot.setRenderer((XYItemRenderer)lineandshaperenderer); 
        xyplot.setRangeGridlinesVisible(true);
        xyplot.setDomainAxis(new NumberAxis("Category"));
       xyplot.setRangeAxis(new NumberAxis("Value"));
        xyplot.setOrientation(PlotOrientation.VERTICAL);*/
        
        
        
        /*XYLineAndShapeRenderer a = (XYLineAndShapeRenderer)xyplot.getRenderer();
        
        a.setSeriesPaint(2, Color.DARK_GRAY); //设置线条颜色
        a.setShapesVisible(true); 
        a.setUseFillPaint(true);//空心*/
        
        //XYLineAndShapeRenderer xylineandshaperenderer = (XYLineAndShapeRenderer)xyplot.getRenderer();
       // xylineandshaperenderer.setShapesVisible(true);
        //xylineandshaperenderer.setShapesFilled(true);
 
        
        //NumberAxis numberaxis = (NumberAxis)xyplot.getRangeAxis();                 //设置y轴为整数
        //numberaxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());  

        
        //NumberAxis numberaxis = (NumberAxis)xyplot.getDomainAxis();                 //设置y轴为整数
        //numberaxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());  
        return  jfreechart;
    }
    
    
    
    
    public static void showFigure(String[] titles, Map<String,double[][]> xydata){
        JFreeChart jfreechart = xFigurePart1.createChart(titles, xydata);
        
        ChartPanel chartpanel = new ChartPanel(jfreechart);
        chartpanel.setPreferredSize(new Dimension(600,400)); //panel大小
   
    	
    	JFrame jp = new JFrame();
    	jp.add(chartpanel);
    	jp.setSize(600,400);
    	jp.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    	jp.setVisible(true);

    	RefineryUtilities.centerFrameOnScreen(jp);//开始在中心位置显示
    }
    public static void saveFigureAsEPS(String[] titles, String fpath, Map<String,double[][]> xydata) throws IOException{
    	 String path = "D:/Program Files/gnuplot/bin/gnuplot";
    	JavaPlot p = new JavaPlot(path);
    	 PostscriptTerminal epsf = new PostscriptTerminal(fpath+".eps");
    	
          epsf.setColor(true);
          epsf.set("linewidth" , "2");
          epsf.set("color", "solid" );
          epsf.set("\"Helvetica\"", "18");
          p.setTerminal(epsf);
         epsf.setColor(true);
         p.setTerminal(epsf);
         p.setTitle(titles[0] );
         
     	Iterator iter = xydata.entrySet().iterator();
     	int cnt = 4;
     	Vector<NamedPlotColor> c= new Vector<NamedPlotColor>();
     	c.add(NamedPlotColor.RED);
     	c.add(NamedPlotColor.BLUE);
     	c.add(NamedPlotColor.GOLDENROD);
     	c.add(NamedPlotColor.MAGENTA);
     	c.add(NamedPlotColor.BLACK);
     	int ccnt = 0;
     	double max = -1;
     	double min = 10;
     	int pointsize = 1;
     	int linewidth = 2;
    	while(iter.hasNext()){
    		Map.Entry en = (Map.Entry)iter.next();
    		XYSeries xyseries = new XYSeries(en.getKey().toString()); 
    		double[][] val = (double[][])en.getValue(); 
    		double[][] data = new double[val[0].length][2];
    		for(int i=0;i<val[0].length;++i){
    			data[i][0]=val[0][i];
    			data[i][1]=val[1][i];
    			if(val[1][i]>max)
    				max = val[1][i];
    			if(val[1][i]<min)
    				min = val[1][i];
    		}

            DataSetPlot line = new DataSetPlot(data);
            line.setTitle(en.getKey().toString());
            PlotStyle stl = new PlotStyle();
            stl.setStyle(Style.LINESPOINTS);
            //stl.setLineType(NamedPlotColor.GOLDENROD);
            stl.setPointSize(pointsize);
            stl.setLineWidth(linewidth); 
            stl.setPointType(cnt);
            stl.setLineType(c.elementAt(ccnt++));
            cnt = cnt+2;
            //45-正方形 67-圆 89上三角 1011下三角 1213菱形 1415五边形  

            line.setPlotStyle(stl);
            p.addPlot(line);
    	 }
    	  PlotStyle stlx = ((AbstractPlot) p.getPlots().get(4)).getPlotStyle();
          stlx.setStyle(Style.LINES);
          /*stlx.setLineType(0);
          stlx.setLineWidth(4);*/
    	
          
        p.getAxis("y").setLabel(titles[1]);
        p.getAxis("x").setLabel(titles[2]);
        double bound = max-min;
        if(bound!=0)
        	p.getAxis("y").setBoundaries(min-bound*0.7,max+bound*0.1 );
        //System.out.println(bound);
        //System.out.println(max+bound*0.1);
        //System.out.println(min-bound*0.3);
        //p.getAxis("y").setBoundaries(0 , 1);
        p.getPreInit().add("set key box");
        //p.getPreInit().add("set grid");
        p.getPreInit().add("set key right bottom");
        p.getPreInit().add("set title font \"Times-Roman,30\"");  
        p.getPreInit().add("set ylabel offset 2,0");
        p.plot();
    }
 
    public static void saveFigure(String[] titles, String fpath, Map<String,double[][]> xydata) throws IOException{
    	
    	titles[0]="";
        JFreeChart jfreechart = xFigurePart1.createChart(titles, xydata);
        
        ChartPanel chartpanel = new ChartPanel(jfreechart);
        chartpanel.setPreferredSize(new Dimension(600,400)); //panel大小
        
        
        String pfix = "jpg";
    	String fn = fpath+"."+pfix;
        ChartUtilities.saveChartAsJPEG(new File(fn), jfreechart, 600, 400);
    
        /*String pfix = "png";
    	String fn = fpath+"."+pfix;
        ChartUtilities.saveChartAsPNG(new File(fn), jfreechart, 600, 400);*/
    
        
        /*String pfix = "png";
    	String fn = fpath+"."+pfix;
        BufferedImage bi = jfreechart.createBufferedImage(600,400, BufferedImage.TYPE_INT_ARGB, null);
        try{
        	ImageIO.write(bi, pfix, new File(fn));
            bi.flush();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }*/
        
        
        
    }
  
    public static void main(String[] args) throws Exception {
    	Map<String,double[][]> xydata = new LinkedHashMap<String,double[][]> ();
    	double[][] data1 = {{2,5},{4.5,6.5}};
    	double[][] data2 = {{1,4},{3,6.5}};
    	double[][] data3 = {{2,5},{4.1,6.2}};
    	double[][] data4 = {{1,4},{1.8,4.7}};
    	double[][] data5 = {{1,8},{1.5,2.7}};
    	xydata.put("xx1", data1);
    	xydata.put("xx2", data2);
    	xydata.put("xx3", data3);
    	xydata.put("xx4", data4);
    	xydata.put("xx5", data5);
    	String[] t = {"TT","Y","X"};
    	showFigure(t,xydata);
    }
}
