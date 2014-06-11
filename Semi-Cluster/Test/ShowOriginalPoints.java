package Test;

import java.awt.Color;
import java.awt.Dimension;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Vector;

import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

import Part1.ClusterEnsemble;
import Part1.MCF_w_voting;
import Part1.MethodGenerateClustering;
 
import weka.clusterers.EM;
import weka.clusterers.SimpleKMeans;
import weka.core.Instances;
 

public class ShowOriginalPoints extends ApplicationFrame{
	
	
	
	
	public Vector<Vector<double[]>>  clusters = null; //double[1][]
	public Vector<double[][]> mustlink = null; //double[2][] 
	public Vector<double[][]> cannotlink = null; //double[2][] 
	public ShowOriginalPoints(String s, Vector<Vector<double[]>> clusters,Vector<double[][]> mustlink, Vector<double[][]>cannotlink) throws CloneNotSupportedException, IOException
    {
        super(s);
        this.clusters = clusters;
        this.mustlink = mustlink;
        this.cannotlink = cannotlink;

        XYDataset xydataset = createDataset();
        
        JFreeChart jfreechart = ChartFactory.createScatterPlot(s, "X", "Y", xydataset, PlotOrientation.VERTICAL, true, true, false);
        XYPlot xyplot = (XYPlot)jfreechart.getPlot();
        xyplot.setNoDataMessage("NO DATA");
        XYLineAndShapeRenderer xylineandshaperenderer = (XYLineAndShapeRenderer)xyplot.getRenderer();
        xylineandshaperenderer.setSeriesOutlinePaint(0, Color.black);
        xylineandshaperenderer.setUseOutlinePaint(true);
        NumberAxis numberaxis = (NumberAxis)xyplot.getDomainAxis();
        numberaxis.setAutoRangeIncludesZero(false);
        ChartPanel chartpanel = new ChartPanel(jfreechart);
        chartpanel.setPreferredSize(new Dimension(650, 500));
        chartpanel.setVerticalAxisTrace(true);
        chartpanel.setHorizontalAxisTrace(true);
        chartpanel.setDomainZoomable(true);
        chartpanel.setRangeZoomable(true);
        setContentPane(chartpanel);
        
        ChartPanel chartpanel2 = new ChartPanel(jfreechart);
        chartpanel2.setPreferredSize(new Dimension(600,400)); //panel大小
        
        
        String pfix = "jpg";
        String fpath = "C:\\Users\\Eric\\Desktop\\2012秋冬\\NO.4\\data\\restmp\\"+s;
    	String fn = fpath+"."+pfix;
        ChartUtilities.saveChartAsJPEG(new File(fn), jfreechart, 600, 400);
        
        /*JFreeChart jfreechart = createChart(xydataset);
        ChartPanel chartpanel = new ChartPanel(jfreechart);
        chartpanel.setPreferredSize(new Dimension(500, 300));
        setContentPane(chartpanel);*/
    }

    private static JFreeChart createChart(XYDataset xydataset)
    {
        JFreeChart jfreechart = ChartFactory.createXYLineChart("XYLineAndShapeRenderer Demo 1", "X", "Y", xydataset, PlotOrientation.VERTICAL, true, true, false);
        XYPlot xyplot = (XYPlot)jfreechart.getPlot();
        XYLineAndShapeRenderer xylineandshaperenderer = new XYLineAndShapeRenderer();
        //xylineandshaperenderer.setSeriesLinesVisible(0, true);
        //xylineandshaperenderer.setSeriesShapesVisible(0, false);
        //xylineandshaperenderer.setSeriesLinesVisible(1, false);
        //xylineandshaperenderer.setSeriesShapesVisible(1, true);
        xylineandshaperenderer.setToolTipGenerator(new StandardXYToolTipGenerator());
        xylineandshaperenderer.setDefaultEntityRadius(6);
        xyplot.setRenderer(xylineandshaperenderer);
        return jfreechart;
    }

    private  XYDataset createDataset() throws CloneNotSupportedException
    {
    	XYSeriesCollection xyseriescollection = new XYSeriesCollection();
    	/*for(int i=0;i<this.mustlink.size();++i){
    		XYSeries xyseries = new XYSeries("");
    		double p1x = this.mustlink.elementAt(i)[0][0];
    		double p1y = this.mustlink.elementAt(i)[0][1];
    		double p2x = this.mustlink.elementAt(i)[1][0];
    		double p2y = this.mustlink.elementAt(i)[1][1];
            xyseries.add(p1x, p1y);
            xyseries.add(p2x, p2y);
            xyseriescollection.addSeries(xyseries.clone());
    	}*/
    	
    	
    	for(int i=0;i<this.clusters.size();++i){
    		 XYSeries xyseries = new XYSeries(i+"x");
    		 for(int j=0;j<this.clusters.elementAt(i).size();++j){
    			 double px = this.clusters.elementAt(i).elementAt(j)[0];
    	     	 double py = this.clusters.elementAt(i).elementAt(j)[1];
    	     	xyseries.add(px,py);
    		 }
    		 xyseriescollection.addSeries((XYSeries)xyseries.clone());
    	}
        
        return xyseriescollection;
    }

    public  JPanel createDemoPanel() throws CloneNotSupportedException
    {
        JFreeChart jfreechart = createChart(createDataset());
        return new ChartPanel(jfreechart);
    }

    public static void main(String args[]) throws Exception
    {
    	
    	
    	String dataname = "half-rings";
    	//String dataname = "Aggregation";
    	//String dataname = "Compound";
    	
    	//String dataname = "Pathbased";
    	//String dataname = "Spiral";
    	//String dataname = "D31";
    	//String dataname = "R15";
    	//String dataname = "Flame";
    	
    	
    	String path = "C:\\Users\\Eric\\Desktop\\2012秋冬\\NO.4\\data\\"+dataname+".arff";
    	Instances data = new Instances(new FileReader(path));
		data.setClassIndex(data.numAttributes()-1);
		
		Vector<Vector<double[]>> p = new Vector<Vector<double[]>>();
		for(int i=0;i<data.numClasses();++i){
			Vector<double[]> pt = new Vector<double[]>();
			for(int j=0;j<data.numInstances();++j){                                    
				if(data.instance(j).classValue()==i){
					double[] pp = new double[2];
					pp[0]=data.instance(j).value(0);
					pp[1] = data.instance(j).value(1);
					pt.add(pp);
				}
			}        
			p.add(pt);
		}
    	
		Instances dataforcluster = new Instances(new FileReader(path));
		  dataforcluster.deleteAttributeAt(dataforcluster.numAttributes()-1);
		  dataforcluster = dataforcluster;
		
		  SimpleKMeans km = new SimpleKMeans(); 
			km.setMaxIterations(100);
			km.setNumClusters(data.numClasses());
			km.setDontReplaceMissingValues(true);
			km.setSeed(2);
			km.setPreserveInstancesOrder(true);
			km.buildClusterer(dataforcluster);
		
			int[] res = km.getAssignments();
			
			
			Vector<Vector<double[]>> pkmeans = new Vector<Vector<double[]>>();
			for(int i=0;i<data.numClasses();++i){
				Vector<double[]> pt = new Vector<double[]>();
				for(int j=0;j<data.numInstances();++j){                                    
					if(res[j]==i){
						double[] pp = new double[2];
						pp[0]=data.instance(j).value(0);
						pp[1] = data.instance(j).value(1);
						pt.add(pp);
					}
				}        
				pkmeans.add(pt);
			}
			
			int rnd = 2;
			double per = 0.1;
			int ensize = 30;
			int type =0;//0 全部属性
			 boolean[] labeled =ClusterEnsemble.getRandomLabeled(per,data.numInstances(),rnd);
			  MethodGenerateClustering mc = new MethodGenerateClustering(data,dataforcluster,labeled,ensize,rnd++,type,0.5);
			       ClusterEnsemble ce =new ClusterEnsemble(mc,new MCF_w_voting(),true);
			
			//int[] res2 = ce.fr.cluster;
			       //int[] res2 ={0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 1, 0, 0, 1, 1, 0, 1, 1, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 1, 0, 0, 1, 0, 1, 0, 1, 1, 0, 0, 0, 0, 1, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1};

					
					EM em = new EM();
					em.setMaxIterations(100);
					em.setNumClusters(7);
					//em.setSeed(Rnd.nextInt());
					em.buildClusterer(dataforcluster);
					int[] res2 = new int[dataforcluster.numInstances()];
					for(int i=0;i<dataforcluster.numInstances();++i){
						res2[i]=em.clusterInstance(dataforcluster.instance(i));
					}
			       
			       
			
			Vector<Vector<double[]>> pmyMethod = new Vector<Vector<double[]>>();
			for(int i=0;i<data.numClasses();++i){
				Vector<double[]> pt = new Vector<double[]>();
				for(int j=0;j<data.numInstances();++j){                                    
					if(res2[j]==i){
						double[] pp = new double[2];
						pp[0]=data.instance(j).value(0);
						pp[1] = data.instance(j).value(1);
						pt.add(pp);
					}
				}        
				pmyMethod.add(pt);
			}
			

    	
    	ShowOriginalPoints xylineandshaperendererdemo1 = new ShowOriginalPoints(dataname,pmyMethod,null,null);
		//	ShowOriginalPoints xylineandshaperendererdemo1 = new ShowOriginalPoints(dataname,pkmeans,null,null);	
        xylineandshaperendererdemo1.pack();
        RefineryUtilities.centerFrameOnScreen(xylineandshaperendererdemo1);
        xylineandshaperendererdemo1.setVisible(true);
    }
}
