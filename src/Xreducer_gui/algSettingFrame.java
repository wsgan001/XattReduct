package Xreducer_gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

import org.jfree.ui.RefineryUtilities;


import Xreducer_core.generateAlgMethod;
import Xreducer_struct.globalValue;
import Xreducer_struct.oneFile;
import Xreducer_struct.xJTable;

public class algSettingFrame   extends JDialog implements ActionListener {

	private panel_left pl = null;
	private panel_right pr = null;
	private oneFile onefile = null;
	private int fileindex = -1;

	public algSettingFrame(oneFile onefile, int row){
		this.fileindex = row;//row -1 全局
		this.onefile = onefile;
		this.pl = new panel_left(this.onefile);
		this.pr = new panel_right(this.pl.getTable(),this.fileindex);
		//this.pl.setSize(200, 200);
		
		this.add(pl,BorderLayout.WEST);
		this.add(pr);
		this.pack();
		this.setSize(800, 355);
		this.setTitle("设置");
		RefineryUtilities.centerFrameOnScreen(this);
		//this.setVisible(true);
		
	}
	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//algSettingFrame t = new algSettingFrame(null);
	}
}
class panel_left extends JPanel{
	private xJTable jt_alginfo = null;
	private JScrollPane jp = null;
	public panel_left(oneFile onef){
		String[] title = {"序号","算法列表"};
		this.jt_alginfo = new xJTable(title);
		addData(onef);
		this.jt_alginfo.setPreferredWidth(); //设置最佳宽度
		//this.jt_alginfo.setSize(300, 300);
		//this.jt_alginfo.setPreferredSize(new Dimension(300, 300));
		this.jp= new JScrollPane(this.jt_alginfo);
		//this.jp.setSize(300, 300);
		this.jp.setPreferredSize(new Dimension(180, 300));
		this.add(this.jp); 
		//this.pl.setPreferredSize(new Dimension(200, 200));
		this.setSize(300,300);
	}
	public xJTable getTable(){
		return this.jt_alginfo;
	}
	public void addData(oneFile onef){

		for(int i=0;i<onef.numcomAlg;++i)
		{
			int id = onef.algs.get(i).ID;
			String algname = onef.algs.get(i).algname;
			Vector onecontent = new Vector();
			onecontent.add(id);
			onecontent.add(algname);
			this.jt_alginfo.addData(onecontent);
		}
		
	}
	
}
class panel_right extends JTabbedPane  implements ActionListener{
	//private JTabbedPane tabbedPane= null;
	private JButton jb_add = null;
	private JButton jb_delete = null;
	private xJTable jt_alginfo = null;
	private int fileindex = -1;
	private JComboBox petList = null;
	public panel_right(xJTable jt,int fileindex){
		this.fileindex = fileindex;
		jt_alginfo = jt;
		JPanel jp1 = new JPanel();
		

		String[] petStrings = { "全集", "CfsSubsetEval算法", "Consistency算法", "ReliefF算法", "Wrapper算法", 
				"条件熵","正域","FCBF[SU](0)算法","FCBF[SU](log)算法","FCBF[CAIR](0)算法"	
		};
		//Create the combo box, select item at index 4.
		//Indices start at 0, so 4 specifies the pig.
		this.petList = new JComboBox(petStrings);
		this.petList.setSelectedIndex(-1);
		this.petList.addActionListener(this);

		this.jb_add = new JButton("添加算法");
		this.jb_add.addActionListener(this);
		this.jb_delete = new JButton("删除");
		this.jb_delete.addActionListener(this);
		JPanel lf1 = new JPanel();
		lf1.setLayout(new GridLayout(7,1));
		lf1.add(petList);
		lf1.add(jb_add);
		lf1.add(jb_delete);
		
		
		
		jp1.add(lf1,BorderLayout.WEST);
		JPanel lf2 = new JPanel();
		jp1.add(lf2,BorderLayout.EAST);
		//this.tabbedPane = new JTabbedPane();
		//this.tabbedPane.add("设置算法",jp1);

		
		//this.tabbedPane.setSize(300,900);
		//this.add(this.tabbedPane);
		this.add("设置算法",jp1);
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		 if(e.getActionCommand()=="删除")
		 {
			 deleteAlg();
		 }
		 if(e.getActionCommand()=="添加算法")
		 {
			 System.out.println(this.petList.getSelectedItem());
		 }
	}
	private void deleteAlg() {
		// TODO Auto-generated method stub
    	if(this.jt_alginfo.getSelectedRow() != -1){
    		generateAlgMethod.deleteAlg(this.fileindex,this.jt_alginfo.getSelectedRow());
    		this.jt_alginfo.deleteData(this.jt_alginfo.getSelectedRow()); 
    		//f.remove(this.jt_datainfo.getSelectedRow());
    	}
    		
    	this.jt_alginfo.updateUI();
	}
}