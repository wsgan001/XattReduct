package Part1;

 
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import javax.swing.JFrame;

import com.panayotis.gnuplot.plot.AbstractPlot;
import com.panayotis.gnuplot.plot.DataSetPlot;
import com.panayotis.gnuplot.style.NamedPlotColor;
import com.panayotis.gnuplot.style.PlotStyle;
import com.panayotis.gnuplot.style.Style;
import com.panayotis.gnuplot.swing.JPlot;
import com.panayotis.gnuplot.terminal.PostscriptTerminal;
import com.panayotis.iodebug.Debug;

import JGnuplot.JavaPlot;

public class RunGnuplot {

	  /**
     * @param args the command line arguments. First argument is the path of gnuplot application
	 * @throws IOException 
     */
    public static void main(String[] args) throws IOException {
        String path = "D:/Program Files/gnuplot/bin/gnuplot";
       // if (args.length > 0) {
        //    path = args[0];
       // }
        //JavaPlot p = new JavaPlot(path);
        //p.addPlot("sin(x)");
        //p.setTitle("Default Terminal Title");
        //p.plot();
        //simple3D();
         //defaultTerminal(path); 
        //simple();
       EPSTerminal(path);
         //JPlotTerminal(path);
        //SVGTerminal(path);
         //JPlotTerminal(path);
        //serialization(defaultTerminal(path));
        //file();

    }

    /* This is a very simple plot to demonstrate JavaPlot graphs */
    private static void simple() {
        JavaPlot p = new JavaPlot();
        p.addPlot("sin(x)");
        p.plot();
    }
    
    /* This is a very simple plot to demonstrate JavaPlot 3d graphs */
    private static void simple3D() {
        JavaPlot p = new JavaPlot(true);
        p.addPlot("sin(x)*y");
        p.plot();
    }
    
    /* This demo code uses default terminal. Use it as reference for other javaplot arguments  */
    private static JavaPlot defaultTerminal(String gnuplotpath) {
        JavaPlot p = new JavaPlot(gnuplotpath);
      //  JavaPlot.getDebugger().setLevel(Debug.VERBOSE);
        
        p.setTitle("eeeeeeeeeee");
        //p.getAxis("x").setLabel("X axis", "Arial", 20);
        //p.getAxis("y").setLabel("Y axis");

       // p.getAxis("x").setBoundaries(-30, 20);
      //  p.setKey(JavaPlot.Key.TOP_RIGHT);

        double[][] plot = {{1, 1.1}, {2, 2.2}, {3, 3.3}, {4, 4.3}};
        DataSetPlot s = new DataSetPlot(plot);
        p.addPlot(s);
       // p.addPlot("besj0(x)*0.12e1");
       // PlotStyle stl = ((AbstractPlot) p.getPlots().get(1)).getPlotStyle();
       // stl.setStyle(Style.POINTS);
       // stl.setLineType(NamedPlotColor.GOLDENROD);
      ///  stl.setPointType(5);
      //  stl.setPointSize(8);
      //  p.addPlot("sin(x)");

       /* p.newGraph();
        p.addPlot("sin(x)");

        p.newGraph3D();
        double[][] plot3d = {{1, 1.1,3}, {2, 2.2,3}, {3, 3.3,3.4}, {4, 4.3,5}};
        p.addPlot(plot3d);
        

        p.newGraph3D();
        p.addPlot("sin(x)*sin(y)");

        p.setMultiTitle("Global test title");
        StripeLayout lo = new StripeLayout();
        lo.setColumns(9999);
        p.getPage().setLayout(lo);*/
        p.plot();
        
      
        return p;
    }

    /* This demo code creates a EPS file on home directory */
    private static JavaPlot EPSTerminal(String gnuplotpath) throws IOException {
        JavaPlot p = new JavaPlot(gnuplotpath);

          PostscriptTerminal epsf = new PostscriptTerminal(System.getProperty("user.home") +
                 System.getProperty("file.separator") + "output.eps");
          epsf.setColor(true);
          epsf.set("linewidth" , "2");
          epsf.set("color", "solid" );
          epsf.set("\"Helvetica\"", "20");
          p.setTerminal(epsf);
        // p.getPreInit().add(" set terminal epslatex standalone color solid ");
        //p.getPreInit().add("cd 'C:\\Users\\Eric\\Desktop\\test\\gnuplot'");
        // p.getPreInit().add("set output 'temple.tex'");
        
      //  p.setTitle("Postscript Terminal Title");
       // p.addPlot("sin (x)");
       // p.addPlot("sin(x)*cos(x)");
        //p.newGraph();
        //p.addPlot("cos(x)");
        //p.setTitle("Trigonometric functions -1");
       // p.setMultiTitle("Trigonometric functions");
        
         double[][] plot = {{1, 1.1}, {2, 2.2}, {3, 3.3}, {4, 4.3}};
         DataSetPlot s = new DataSetPlot(plot);
         s.setTitle("line1");
         p.addPlot(s);
        
        double[][] plot2 = {{1, 1.6}, {2, 1.2}, {3, 8.3}, {4, 2.3}};
        DataSetPlot s2 = new DataSetPlot(plot2);
      s2.setTitle("lines");
     
      double[][] plot3 = {{3, 1.1}, {1, 6.2}, {2, 2.3}};
      DataSetPlot s3 = new DataSetPlot(plot3);
      s.setTitle("line3");
      p.addPlot(s3);
      
      double[][] plot4 = {{0, 1.1}, {4,1.2},  };
      DataSetPlot s4 = new DataSetPlot(plot4);
      s.setTitle("line4");
      p.addPlot(s4);
      
      p.getAxis("y").setLabel("Y axis");
      p.getAxis("x").setLabel("$\\alpha$");
  
          // PlotStyle stl = new PlotStyle();
        // stl.setStyle(Style.LINESPOINTS);
        // stl.setLineType(NamedPlotColor.GOLDENROD);
       // stl.setPointType(4);
        //45-正方形 67-圆 89上三角 1011下三角 1213菱形 1415五边形  
       //   stl.setPointSize(1);
       //   stl.setLineWidth(3);
 
          
       //s2.setPlotStyle(stl);
     // p.getPreInit().add("set key box");
     // p.getPreInit().add("set grid");
          p.addPlot(s2);
          
          PlotStyle stlx = ((AbstractPlot) p.getPlots().get(1)).getPlotStyle();
         stlx.setStyle(Style.LINESPOINTS);
         stlx.setPointType(4);
         stlx.setLineWidth(2);
         stlx.setPointSize(2);
         stlx.setLineType(NamedPlotColor.GOLDENROD);
         
          PlotStyle stlx2 = ((AbstractPlot) p.getPlots().get(0)).getPlotStyle();
          stlx2.setStyle(Style.LINESPOINTS);
          stlx2.setPointType(6);
          stlx2.setLineWidth(2);
          stlx2.setPointSize(2);
          stlx2.setLineType(NamedPlotColor.RED);
          
          PlotStyle stlx3 = ((AbstractPlot) p.getPlots().get(2)).getPlotStyle();
          stlx3.setStyle(Style.LINESPOINTS);
          stlx3.setPointType(8);
          stlx3.setLineWidth(2);
          stlx3.setPointSize(2);
          stlx3.setLineType(NamedPlotColor.BLUE);
          
          PlotStyle stlx4 = ((AbstractPlot) p.getPlots().get(3)).getPlotStyle();
          stlx4.setStyle(Style.LINESPOINTS);
          stlx4.setPointType(10);
          stlx4.setLineWidth(2);
          stlx4.setPointSize(2);
          stlx4.setLineType(NamedPlotColor.MAGENTA);
        
        p.plot();
        
        return p;
    }

    /* This demo code creates a EPS file on home directory */
    private static JavaPlot EPSTerminal2(String gnuplotpath) throws IOException {
        JavaPlot p = new JavaPlot(gnuplotpath);

          PostscriptTerminal epsf = new PostscriptTerminal(System.getProperty("user.home") +
                 System.getProperty("file.separator") + "output.eps");
          epsf.setColor(true);
          epsf.set("linewidth" , "2");
          epsf.set("color", "solid" );
          epsf.set("\"Helvetica\"", "20");
          p.setTerminal(epsf);
        // p.getPreInit().add(" set terminal epslatex standalone color solid ");
        //p.getPreInit().add("cd 'C:\\Users\\Eric\\Desktop\\test\\gnuplot'");
        // p.getPreInit().add("set output 'temple.tex'");
        
      //  p.setTitle("Postscript Terminal Title");
       // p.addPlot("sin (x)");
       // p.addPlot("sin(x)*cos(x)");
        //p.newGraph();
        //p.addPlot("cos(x)");
        //p.setTitle("Trigonometric functions -1");
       // p.setMultiTitle("Trigonometric functions");
        
         double[][] plot = {{1, 1.1}, {2, 2.2}, {3, 3.3}, {4, 4.3}};
         DataSetPlot s = new DataSetPlot(plot);
         s.setTitle("line1");
         p.addPlot(s);
        
        double[][] plot2 = {{1, 1.6}, {2, 1.2}, {3, 8.3}, {4, 2.3}};
        DataSetPlot s2 = new DataSetPlot(plot2);
      s2.setTitle("lines");
     
      double[][] plot3 = {{3, 1.1}, {1, 6.2}, {2, 2.3}};
      DataSetPlot s3 = new DataSetPlot(plot3);
      s.setTitle("line3");
      p.addPlot(s3);
      
      double[][] plot4 = {{0, 1.1}, {4,1.2},  };
      DataSetPlot s4 = new DataSetPlot(plot4);
      s.setTitle("line4");
      p.addPlot(s4);
      
      p.getAxis("y").setLabel("Y axis");
      p.getAxis("x").setLabel("$\\alpha$");
  
          // PlotStyle stl = new PlotStyle();
        // stl.setStyle(Style.LINESPOINTS);
        // stl.setLineType(NamedPlotColor.GOLDENROD);
       // stl.setPointType(4);
        //45-正方形 67-圆 89上三角 1011下三角 1213菱形 1415五边形  
       //   stl.setPointSize(1);
       //   stl.setLineWidth(3);
 
          
       //s2.setPlotStyle(stl);
     // p.getPreInit().add("set key box");
     // p.getPreInit().add("set grid");
          p.addPlot(s2);
          
          PlotStyle stlx = ((AbstractPlot) p.getPlots().get(1)).getPlotStyle();
         stlx.setStyle(Style.LINESPOINTS);
         stlx.setPointType(4);
         stlx.setLineWidth(2);
         stlx.setPointSize(2);
         stlx.setLineType(NamedPlotColor.GOLDENROD);
         
          PlotStyle stlx2 = ((AbstractPlot) p.getPlots().get(0)).getPlotStyle();
          stlx2.setStyle(Style.LINESPOINTS);
          stlx2.setPointType(6);
          stlx2.setLineWidth(2);
          stlx2.setPointSize(2);
          stlx2.setLineType(NamedPlotColor.RED);
          
          PlotStyle stlx3 = ((AbstractPlot) p.getPlots().get(2)).getPlotStyle();
          stlx3.setStyle(Style.LINESPOINTS);
          stlx3.setPointType(8);
          stlx3.setLineWidth(2);
          stlx3.setPointSize(2);
          stlx3.setLineType(NamedPlotColor.BLUE);
          
          PlotStyle stlx4 = ((AbstractPlot) p.getPlots().get(3)).getPlotStyle();
          stlx4.setStyle(Style.LINESPOINTS);
          stlx4.setPointType(10);
          stlx4.setLineWidth(2);
          stlx4.setPointSize(2);
          stlx4.setLineType(NamedPlotColor.MAGENTA);
        
        p.plot();
        
        return p;
    }


    /* This demo code displays plot on screen using SVG commands (only b&w) */
    private static JavaPlot SVGTerminal(String gnuplotpath) {
        JavaPlot p = new JavaPlot();
        JavaPlot.getDebugger().setLevel(Debug.VERBOSE);

        com.panayotis.gnuplot.terminal.SVGTerminal svg = new com.panayotis.gnuplot.terminal.SVGTerminal();
        p.setTerminal(svg);

        p.setTitle("SVG Terminal Title");
        p.addPlot("x+3");
        p.plot();

        try {
            JFrame f = new JFrame();
            f.getContentPane().add(svg.getPanel());
            f.pack();
            f.setLocationRelativeTo(null);
            f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            f.setVisible(true);
        } catch (ClassNotFoundException ex) {
            System.err.println("Error: Library SVGSalamander not properly installed?");
        }
        
        return p;
    }

 
    

}
