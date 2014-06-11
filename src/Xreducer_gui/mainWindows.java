package Xreducer_gui;


 

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTable;

import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

import weka.attributeSelection.ASEvaluation;
import weka.attributeSelection.ASSearch;
import weka.attributeSelection.CfsSubsetEval;
import weka.attributeSelection.ConsistencySubsetEval;
import weka.attributeSelection.GreedyStepwise;
import weka.attributeSelection.Ranker;
import weka.attributeSelection.ReliefFAttributeEval;
import weka.attributeSelection.WrapperSubsetEval;
import weka.classifiers.Classifier;
import weka.classifiers.bayes.NaiveBayes;

import Xreducer_core.Utils_XMLoperator;
import Xreducer_core.compareAlgorithmMethod;
import Xreducer_core.generateAlgMethod;
import Xreducer_gui.loadFileFrame.StateInfo;
import Xreducer_struct.comFiles;
import Xreducer_struct.globalValue;
import Xreducer_struct.oneAlgRecord;
import Xreducer_struct.oneAlgorithm;
import Xreducer_struct.oneAlgorithm.xCategory;
import Xreducer_struct.oneAlgorithm.xStyle;
import Xreducer_struct.oneDataRecord;
import Xreducer_struct.oneFile;
import Xreducer_struct.oneFile.xNSstyle;




public class mainWindows extends JFrame   implements ActionListener{

	
	private JButton jb_load = null;
	private JButton jb_start = null;
	private JButton jb_delete = null;	 
	private JButton jb_deletes = null;
	private JButton jb_selectall = null;
	private JButton jb_cancalall = null;
	private JButton jb_setting = null;
	
	private JButton jb_savemodel = null;
	private JButton jb_loadmodel = null;
	private JButton jb_readme = null;
	private Vector<File> datafiles =null;
	private JTabbedPane tabbedPane;
	private loadFileFrame ftable = null;
	private Integer ID = 0;
	
	private Vector<oneDataRecord> dataRecord = null;

	
	
	public mainWindows(String title) throws IOException, Exception {
		super(title);
		
		String rfp = globalValue.dataRecordpath;
		this.dataRecord = Utils_XMLoperator.parserXml(rfp, 0);
		generateAlgMethod.setGlobalcomf(this.dataRecord);
		
		
		// TODO Auto-generated constructor stub
		this.tabbedPane = new JTabbedPane();	
		datafiles = new Vector<File>();
		//JPanel p0= new JPanel();
		this.jb_load = new JButton("����");
		this.jb_load.addActionListener(this);
		this.jb_start = new JButton("��ʼ");
		this.jb_start.addActionListener(this);
		this.jb_delete = new JButton("ɾ��");
		this.jb_delete.addActionListener(this);
		this.jb_deletes = new JButton("ɾ������");
		this.jb_deletes.addActionListener(this);
		this.jb_selectall = new JButton("ȫѡ");
		this.jb_selectall.addActionListener(this);
		this.jb_cancalall = new JButton("��ѡ");
		this.jb_cancalall.addActionListener(this);
		this.jb_setting = new JButton("����");
		this.jb_setting.addActionListener(this);
		this.jb_savemodel = new JButton("����ģʽ");
		this.jb_savemodel.addActionListener(this);
		this.jb_loadmodel = new JButton("����ģʽ");
		this.jb_loadmodel.addActionListener(this);
		this.jb_readme = new JButton("˵��");
		this.jb_readme.addActionListener(this);
		
		
		JPanel lf0 = new JPanel();
		lf0.setLayout(new BorderLayout());
		ftable = new loadFileFrame();
		lf0.add(ftable,BorderLayout.WEST);
		JPanel lf1 = new JPanel();
		lf1.setLayout(new GridLayout(7,1));
		lf1.add(jb_load);
		lf1.add(jb_start);
		lf1.add(jb_setting);
		lf1.add(jb_delete);
		lf1.add(jb_deletes);
		lf1.add(jb_selectall);
		lf1.add(jb_cancalall);
		
		JPanel lf2 = new JPanel();
		lf2.setLayout(new GridLayout(3,1));
		lf2.add(jb_savemodel);
		lf2.add(jb_loadmodel);
		lf2.add(jb_readme);
		
		JPanel lf3 = new JPanel();
		lf3.setLayout(new BorderLayout());
		lf3.add(lf1,BorderLayout.NORTH);
		lf3.add(lf2,BorderLayout.SOUTH);
		
		lf0.add(lf3,BorderLayout.EAST);
		tabbedPane.add("�����ļ�",lf0);
		
		this.InitFile(this.ftable);
		
		
		
		this.pack();
		this.setContentPane(tabbedPane);
        this.setSize(800, 600);   
        //this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //�رոöԻ��� ����ر�
        this.setDefaultCloseOperation( JFrame.DO_NOTHING_ON_CLOSE);
		this.pack(); //�޴˾䣬��ʼ�Ի�����С��
        RefineryUtilities.centerFrameOnScreen(this);//��ʼ������λ����ʾ
       
        
        
         addWindowListener(new WindowAdapter(){
  		   @Override
  		   public void windowClosing(WindowEvent e) {
  		     int   option=   JOptionPane.showConfirmDialog( 
  		    		mainWindows.this, "ȷ���˳�ϵͳ? ", "��ʾ ",JOptionPane.YES_NO_CANCEL_OPTION); 
  		      if(option==JOptionPane.YES_OPTION)
  		         if(e.getWindow()   ==  mainWindows.this) 
  		         { 
  		        	 //���������ļ�

  		        	 getFilerecords(); 	 
						try {
							saveFilerecords();
						} catch (Exception e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
  		        	 System.exit(0); 
  		         } 
  		         else 
  		         { 
  		        	 return; 
  		         
  		         } 
  		   }
  		  }); 
 
	}
	protected void saveFilerecords() throws Exception {
		// TODO Auto-generated method stub
		String rfp = globalValue.dataRecordpath;
		Utils_XMLoperator.saveXml(rfp, 0, this.dataRecord);

	}
	protected void getFilerecords() {
		// TODO Auto-generated method stub
	
		//generateAlgMethod.Init(this.getFiles(), this.ftable);
		 boolean[] isSelect = this.ftable.model.getDate();
		 this.dataRecord = new Vector<oneDataRecord>(isSelect.length);
		 for(int i=0;i<isSelect.length;++i){
			 oneDataRecord temp = new oneDataRecord();
			 int id = Integer.parseInt(this.ftable.getValueAt(i, 0).toString());
			 temp.filepath = globalValue.gcomf.onef.get(i).filepath.getAbsolutePath();
			 temp.filename = globalValue.gcomf.onef.get(i).filename;
			 temp.ins = globalValue.gcomf.onef.get(i).ins;
			 temp.att = globalValue.gcomf.onef.get(i).att;
			 temp.cla = globalValue.gcomf.onef.get(i).cla;
			 temp.ischoose = isSelect[i];
			 String state = this.ftable.getValueAt(i, 7).toString();
			 
			 temp.state =  (StateInfo.str2StateInfo(state)).ordinal();
			 temp.progress = (Integer)this.ftable.getValueAt(i, 5);
			 
			 oneFile aftemp = globalValue.gcomf.onef.get(i);
			 temp.baseindex = aftemp.baseindex;
			 temp.signifcantlevel = aftemp.signiifcantlevel; 
			 temp.numReduce = aftemp.numRun;
			 temp.numRun = aftemp.numRun;
			 temp.trainclname = aftemp.clname;
			 temp.numFold = aftemp.numFold;
			 temp.numcomAlg = aftemp.numcomAlg;
			 temp.NSindex = aftemp.NSindex;
			 temp.NSstyle = aftemp.NSstyle;
			 temp.NSmaxIndex = aftemp.NSmaxIndex;
			 temp.NSclname = aftemp.NSclname;
				//public double[][] NSRes = null;
			 temp.randomI = aftemp.randomI;
			 
			 Vector<oneAlgRecord> oneAlgs = new Vector<oneAlgRecord>();
			 
			 for(int j=0;j<temp.numcomAlg;++j){
				 oneAlgRecord oneAlg = new oneAlgRecord();
				 oneAlg.alg_category = aftemp.algs.get(j).category;
				 oneAlg.alg_style = aftemp.algs.get(j).style;
				 oneAlg.alg_ID = aftemp.algs.get(j).ID;
				 oneAlg.alg_alpha = aftemp.algs.get(j).alpha;
				 oneAlg.alg_algname = aftemp.algs.get(j).algname;
				 oneAlg.alg_clname = aftemp.algs.get(j).clname;
				 oneAlg.alg_flag = aftemp.algs.get(j).flag;
				 oneAlg.alg_eval = aftemp.algs.get(j).evalname;
				 oneAlg.alg_numFold = aftemp.algs.get(j).numFold;
				 oneAlg.alg_numReduce = aftemp.algs.get(j).numReduce;
				 oneAlg.alg_numRun = aftemp.algs.get(j).numRun;
				 oneAlg.alg_search = aftemp.algs.get(j).searchname;
				 oneAlgs.add(oneAlg);
			 }
			 temp.algs = oneAlgs;
			 this.dataRecord.add(temp);
		}
		 
	}
	public void showWindow(){

        this.setVisible(true);
	}
	
	private void InitFile(loadFileFrame ft) throws IOException, Exception {
		// TODO Auto-generated method stub
		if(this.dataRecord!=null){
			for(int i=0;i<this.dataRecord.size();++i){
				oneDataRecord temp = this.dataRecord.get(i);
				File f = new File(temp.filepath);
				this.datafiles.add(f);
				
				Vector v = checkDataInfo.getDataInfo(0, f);
		    	this.datafiles.add(f);
		    	String fn = (String)v.get(2);
		    	int ins = (Integer)v.get(3);
		    	int attNomianl = (Integer)v.get(4);
		    	int attNumical = (Integer)v.get(5);
		    	int cla = (Integer)v.get(6);
				this.ftable.addRow( ID+1, temp.filename, temp.ins, attNomianl,attNumical, temp.cla, temp.progress, temp.ischoose, StateInfo.int2StateInfo(temp.state)); 
		        ID++;
			}     
		}
	}
	/**
	 * @param args
	 * @throws Exception 
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException, Exception {
		// TODO Auto-generated method stub
		mainWindows mw = new mainWindows("X-Reducer-beta");
		mw.showWindow();
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		 if(e.getActionCommand()=="����")
		 {
			 try {
				addData();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		 }
		 if(e.getActionCommand()=="ɾ��")
		 {
			 this.ftable.removeData();    //ɾ��ָ������ 
			 }
		 if(e.getActionCommand()=="ɾ������")
		 {
			 this.ftable.removeDatas();    //ɾ������ 
		 }
		 if(e.getActionCommand()=="��ʼ")
		 {
			 //boolean[] res = this.model.getDate();
			 //System.out.println(Arrays.toString(res));
			 //this.model.setValueAt(100, 0, 5);
			 //Init();
		        Runner r  = new Runner(globalValue.gcomf,this.ftable);
		        Thread t = new Thread(r);
		        t.start();

		 }
		 if(e.getActionCommand()=="ȫѡ")
		 {
	    	this.ftable.selectall();
		 }
		 if(e.getActionCommand()=="��ѡ")
		 {
		    this.ftable.cancelall();
		 }
		 if(e.getActionCommand()=="ȫ������")
		 {
			 algSettingFrame af = new algSettingFrame(globalValue.gcomf.onef.get(0),-1);
			 af.setVisible(true);
		 }
		 if(e.getActionCommand()=="����ģʽ")
		 {
 
		 }
		 if(e.getActionCommand()=="����ģʽ")
		 {
 
		 }
		 if(e.getActionCommand()=="˵��")
		 {
			 String str = "X-reducer beta 1.0\n Ŀ�꣺��ɻ�����ܣ���ʾ�������";
			 JOptionPane.showMessageDialog(this ,str);
		 }
	}

    //Ĭ�����Ϊ�������� 
    private void addData() throws IOException, Exception {
    	
    	
    	NotepadFile choosefile = new NotepadFile(this);
    	choosefile.openFile(null);
    	
    	File f = choosefile.getFile();
    	if(f!=null){
	    	Vector v = checkDataInfo.getDataInfo(ID, f);
	    	this.datafiles.add(f);
	    	String fn = (String)v.get(2);
	    	int ins = (Integer)v.get(3);
	    	int attNomianl = (Integer)v.get(4);
	    	int attNumical = (Integer)v.get(5);
	    	int cla = (Integer)v.get(6);
	    	this.ftable.addRow( ID+1, fn, ins, attNomianl,attNumical, cla, new Integer(0), true, StateInfo.READY); 
	        ID++;
        }
    	else
    		return;
    	generateAlgMethod.setDefaultAlgs(f);
    } 


	private File[] getFiles(){
		 boolean[] res = this.ftable.model.getDate();

			 File[] fns = new File[res.length];
			 for(int i=0;i<res.length;++i){
				 if(res[i]){
					 fns[i]=(File)this.datafiles.get(i);
				 }
			 
				 else{
					 fns[i]=null;
			 }}
			 return fns;
 
		 //System.out.println(Arrays.toString(res));
	} 
	
}

class Runner implements Runnable{ //ʵ��������ӿڣ�jdk��֪���������һ���߳� 


	//private File[] t = null;
	private loadFileFrame ftable = null;
	private comFiles cf = null;
	public Runner(comFiles cf,loadFileFrame ftable){
		this.cf = cf;
		this.ftable=ftable;
	}
    public void run(){ 
		 compareAlgorithmMethod cm = new compareAlgorithmMethod(cf,this.ftable);
		 try {
			cm.StartAlg();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
         
    }
}
