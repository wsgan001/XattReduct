package Test;

import java.awt.Color;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Iterator;

public class DrawPointsAndLines extends Frame {
	class Monitor5 extends MouseAdapter{ //MouseAdapter实现了MouseListener接口
		public void mousePressed(MouseEvent e){
			DrawPointsAndLines f=(DrawPointsAndLines)e.getSource();
			f.addPoint(new Point(e.getX(),e.getY()));
			f.repaint(); //让Frame强制经行重画
		}
	}
		public ArrayList points=null;
		DrawPointsAndLines(String s){
			super(s);
			points=new ArrayList();
			setLayout(null);
			setBounds(300,300,400,300);
			this.setBackground(new Color(204,204,255));
			setVisible(true);
			
			this.addMouseListener(new Monitor5());}
		public void paint(Graphics g){
			Iterator i=points.iterator();
			while(i.hasNext()){
				Point p=(Point)i.next();
				g.setColor(Color.blue);
				g.fillOval(p.x,p.y, 5, 5);
				}}
		public void addPoint(Point p){
			points.add(p);
			System.out.println("X:"+p.getX()+" Y:"+p.getY());
		}
		

	/*左键点击画，右键拖拽画线*/
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		new DrawPointsAndLines("Test1");
	}

}
