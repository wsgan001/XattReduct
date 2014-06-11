package Xreducer_gui;



import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.EventObject;
import java.util.Vector;

import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.UIManager;
//import javax.swing.table.AbstractTableModel;
//import javax.swing.table.DefaultTableModel;
import javax.swing.event.MouseInputListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import org.jfree.ui.RefineryUtilities;

import Xreducer_core.generateAlgMethod;
import Xreducer_gui.loadFileFrame.StateInfo;
import Xreducer_struct.globalValue;


 
public class loadFileFrame extends JPanel implements ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public JTable jt_datainfo = null;
	private JScrollPane jp = null;
	public Table_model model = null;
	public  JPopupMenu   popup   =  null;  
	private resultFrame rs = null;
	  
	/*
	 * @param args
	 */
	
	public loadFileFrame(){
		
		this.popup=new JPopupMenu();
	    

		JMenuItem setItem=new JMenuItem("设置");
		setItem.addActionListener(this);
		JMenuItem deleteItem=new JMenuItem("删除");
		deleteItem.addActionListener(this);
		JMenuItem  resItem=new JMenuItem("结果");
		resItem.addActionListener(this);
		

		this.popup.add(setItem);
		this.popup.addSeparator();
		this.popup.add(deleteItem);
		this.popup.addSeparator();
		this.popup.add(resItem);
		
        this.model = new Table_model(); 
        this.jt_datainfo = new JTable(model); 

        //this.jt_datainfo.setBackground(Color.white); 
       TableColumnModel tcm = this.jt_datainfo.getColumnModel(); 
        
      	  JComboBox c = new JComboBox();
       	 c.addItem("Taipei");
       	//c.addItem("ChiaYi");
       	//c.addItem("HsinChu");
       	this.rs = new resultFrame();

        //设置每项的宽度 
       tcm.getColumn(0).setCellRenderer(new CenterCellRenderer());
       tcm.getColumn(1).setCellRenderer(new CenterCellRenderer());
       tcm.getColumn(2).setCellRenderer(new CenterCellRenderer());
       tcm.getColumn(3).setCellRenderer(new CenterCellRenderer());
       tcm.getColumn(4).setCellRenderer(new CenterCellRenderer());

       tcm.getColumn(5).setCellRenderer(new ProgressRenderer());
       tcm.getColumn(5).setCellEditor(new ProgressEditor(new JTextField()));
       tcm.getColumn(7).setCellRenderer(new CenterCellRenderer());
       tcm.getColumn(8).setCellRenderer(new ButtonRenderer());
       tcm.getColumn(8).setCellEditor(new ButtonEditor(new JCheckBox(),this));
        //table.getColumn("JRadioButton").setCellRenderer(new RadioButtonRenderer());
        //table.getColumn("JRadioButton").setCellEditor(new RadioButtonEditor(new JCheckBox()));
        /* tcm.getColumn(1).setPreferredWidth(50); 
        tcm.getColumn(2).setPreferredWidth(50); 
        tcm.getColumn(3).setPreferredWidth(50); 
        tcm.getColumn(4).setPreferredWidth(50); */
		
       final MouseInputListener mouseInputListener = getMouseInputListener(this.jt_datainfo,this.popup);//添加鼠标右键选择行

       this.jt_datainfo.addMouseListener(mouseInputListener);
       this.jt_datainfo.addMouseMotionListener(mouseInputListener);
       this.jt_datainfo.addMouseListener(new newJTable_mouseAdapter(this));
		
		this.jp= new JScrollPane(this.jt_datainfo);
		this.jp.setPreferredSize(new Dimension(900, 300));
		//getRootPane().add(this.jp, BorderLayout.CENTER);
		this.add(this.jp); 


		

	}
	public void setProgress(int index,int v){
		this.model.setValueAt(v, index, 5);
	}
	public void setState(int index, StateInfo state){
		this.model.setValueAt(state, index, 7);
	}
	
	public void selectall(){
		this.model.selectall();
		this.jt_datainfo.updateUI(); 
	}
	public void cancelall(){
		this.model.cancalall(); 
	    this.jt_datainfo.updateUI();
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		 if(e.getActionCommand()=="结果")
		 {	  //MouseEvent me = (MouseEvent)e;
			  //int row = this.jt_datainfo.rowAtPoint(me.getPoint());
			  int row = this.jt_datainfo.getSelectedRow();
			  if(this.isoneFileDone(row)){
				  //rs.setFileres(row);
				  //System.out.println(row);
				  //rs.setVisible(true);
				  this.showResPanel(row);
			  }
		 }
		 if(e.getActionCommand()=="设置")
		 {	  
			 int row = this.jt_datainfo.getSelectedRow();
			 if(this.isoneFileDone(row)||this.isoneFileunDone(row)){
					algSettingFrame af = new algSettingFrame(globalValue.gcomf.onef.get(row),row);
					af.setVisible(true); 
			 }

		 }
		 if(e.getActionCommand()=="删除")
		 {
			 this.removeData();
		 }
	}
	//添加鼠标右键单击选择监听，并显示右键菜单
    private static MouseInputListener getMouseInputListener(final JTable jTable, final JPopupMenu popup){
       return new MouseInputListener() {
           public void mouseClicked(MouseEvent e) {
              processEvent(e);
           }
           public void mousePressed(MouseEvent e) {
              processEvent(e);
           }
           public void mouseReleased(MouseEvent e) {
              processEvent(e);
              if ((e.getModifiers() & MouseEvent.BUTTON3_MASK) != 0
                     && !e.isControlDown() && !e.isShiftDown()) {
            	  popup.show(jTable, e.getX(), e.getY());//右键菜单显示
              }
           }
           public void mouseEntered(MouseEvent e) {
              processEvent(e);
           }
           public void mouseExited(MouseEvent e) {
              processEvent(e);
           }
          public void mouseDragged(MouseEvent e) {
              processEvent(e);
           }
          public void mouseMoved(MouseEvent e) {
              processEvent(e);
           }
           private void processEvent(MouseEvent e) {
              if ((e.getModifiers() & MouseEvent.BUTTON3_MASK) != 0) {
                  int modifiers = e.getModifiers();
                  modifiers -= MouseEvent.BUTTON3_MASK;
                  modifiers |= MouseEvent.BUTTON1_MASK;
                  MouseEvent ne = new MouseEvent(e.getComponent(), e.getID(),
                         e.getWhen(), modifiers, e.getX(), e.getY(), e
                                .getClickCount(), false);
                  jTable.dispatchEvent(ne);
              }}};
    }
	public void addRow(int i, String fn, int ins,int att_Nominal, int att_Numeric,  int cla,
			int progress, boolean isselected, StateInfo state) {
		// TODO Auto-generated method stub
		String att = Integer.toString(att_Nominal)+"+"+Integer.toString(att_Numeric)+"="+Integer.toString(att_Nominal+att_Numeric);
		this.model.addRow(i, fn, ins, att, cla, progress, isselected, state);
		this.jt_datainfo.updateUI();
	}
	//public void removeData(Vector<File> f) {
	public void removeData() {
		// TODO Auto-generated method stub
    	if(this.jt_datainfo.getSelectedRow() != -1){
    		generateAlgMethod.deleteFile(this.jt_datainfo.getSelectedRow());
    		this.model.removeRow( this.jt_datainfo.getSelectedRow() ); 
    		//f.remove(this.jt_datainfo.getSelectedRow());
    	}
    		
    	this.jt_datainfo.updateUI();
	}
	//public void removeDatas(Vector<File> f) {
	public void removeDatas() {
		// TODO Auto-generated method stub
    	if(this.jt_datainfo.getSelectedRow() != -1)
    	{
    		generateAlgMethod.deleteFile(this.jt_datainfo.getSelectedRow(),this.jt_datainfo.getSelectedRowCount());
    		this.model.removeRows( this.jt_datainfo.getSelectedRow(),this.jt_datainfo.getSelectedRowCount() ); 
    	}
    		
    	/*int b = this.jt_datainfo.getSelectedRow();
    	int e = b + this.jt_datainfo.getSelectedRowCount();
    	for(int i=b; i<e ;++i){
    		f.remove(i);
    	}*/
    	this.jt_datainfo.updateUI(); 
	}
	public boolean isoneFileDone(int index){

		if((Integer)this.model.getValueAt(index, 5)==100){
			return true;
		}
		else{
			return false;
		}	
	}
	public boolean isoneFileunDone(int index){

		if((Integer)this.model.getValueAt(index, 5)==0){
			return true;
		}
		else{
			return false;
		}	
	}
	public void showResPanel(int index) {
		// TODO Auto-generated method stub
		if(this.rs.index!=index){
			this.rs.setFileres(index);
			}
			//System.out.println(index);
			this.rs.setVisible(true);		
	}
	public int getRowCount() { 
        return this.model.getRowCount();  
    } 
 
    public Object getValueAt(int row, int col) { 
       return this.model.getValueAt(row, col);
    } 
    public static enum StateInfo 
    {	
    	READY,COMPUTING,COMPLETE,NOEXITS;
    	public static String StateInfo2state(StateInfo i){
    		String str = "";
    		switch(i){
    		case READY: {str = "准备";break;}
    		case COMPUTING: {str = "正在进行";break;}
    		case COMPLETE: {str = "完成";break;}
    		case NOEXITS: {str = "文件不存在";break;}
    		default: break;
    		}
			return str;	
    	}
    	
    	public static StateInfo str2StateInfo(String str){
    		if(str == "准备") return StateInfo.READY;
    		else if(str == "正在进行") return StateInfo.COMPUTING;
    		else if(str == "完成") return StateInfo.COMPLETE;
    		else if(str == "文件不存在") return StateInfo.NOEXITS;
    		else
			return null;	
    	}
    	
    	public static StateInfo int2StateInfo(int is){
    		if(is == 0) return StateInfo.READY;
    		else if(is == 1) return StateInfo.COMPUTING;
    		else if(is == 2) return StateInfo.COMPLETE;
    		else if(is == 3) return StateInfo.NOEXITS;
    		else
			return null;	
    	}
    }
    
    
    
}




/** 
* TableModel类，继承了AbstractTableModel 
*/ 
class Table_model extends AbstractTableModel { 
 
  //  private static final long serialVersionUID = -7495940408592595397L; 
 
    private Vector content = null; 
 

    private String[] title_name = { "序号", "数据集名称", "样本个数", "属性(Enum+Real)", "类别个数", "完成进度", "是否选择","状态", "查看结果"}; 
 
    public Table_model() { 
        content = new Vector(); 
    } 
 
    public Table_model(int count) { 
        content = new Vector(count); 
    } 
 
    public void addRow(Integer ID, String dataname, Integer ins, String att, Integer cla, Integer progress, boolean isselected, StateInfo state) { 
        Vector v = new Vector(8); 
        v.add(0, ID.toString()); 
        v.add(1, dataname); 
        v.add(2, ins); 
        v.add(3, att); 
        v.add(4, cla); 
        v.add(5, progress); 
        v.add(6, isselected); 


        
        v.add(loadFileFrame.StateInfo.StateInfo2state(state));
       /* switch(state){
        case 0:{ v.add(7, "准备"); break; }
        case 1:{ v.add(7, "正在进行"); break; }
        case 2:{ v.add(7, "完成"); break; }
        case 3:{ v.add(7, "文件不存在"); break; }
        default:break;
        }*/
 
        v.add(8, "Result");
        content.add(v); 
    } 
 
    public void removeRow(int row) { 
        content.remove(row); 
    } 
 
    public void removeRows(int row, int count) { 
        for (int i = 0; i<count; i++) { 
            if (content.size() > row) { 
                content.remove(row); 
            } 
        } 
    } 
 
     
    //让表格中某些值可修改，但需要setValueAt(Object value, int row, int col)方法配合才能使修改生效 
    public boolean isCellEditable(int rowIndex, int columnIndex) { 
    	 boolean check = (Boolean)((Vector) content.get(rowIndex)).get(6);
    	 int progress = (Integer)((Vector) content.get(rowIndex)).get(5);
         if(columnIndex==6 || (columnIndex==8 && check && progress==100))
        	 return true;
         else
        	 return false; 
    } 
 
 
    //使修改的内容生效 
    public void setValueAt(Object value, int row, int col) { 
    	
    	if(col==7){  		
    		value = StateInfo.StateInfo2state((StateInfo)value);
    	}
        ((Vector) content.get(row)).remove(col); 
        ((Vector) content.get(row)).add(col, value); 
        this.fireTableCellUpdated(row, col); 
    } 
 
    public String getColumnName(int col) { 
        return title_name[col]; 
    } 
 
    public int getColumnCount() { 
        return title_name.length; 
    } 
 
    public int getRowCount() { 
        return content.size(); 
    } 
 
    public Object getValueAt(int row, int col) { 
        return ((Vector) content.get(row)).get(col); 
    } 
  
 
    //返回数据类型 
    public Class getColumnClass(int col) { 
        return getValueAt(0, col).getClass(); 
    } 
    public boolean[] getDate(){
    	boolean[] res = new boolean[content.size()];
    	for(int i=0;i<content.size();++i){
    			res[i]=(Boolean)((Vector) content.get(i)).get(6);
    	}
    	return res;
    }

	public void selectall() {
		// TODO Auto-generated method stub
		for(int i=0;i<content.size();++i){
			setValueAt(true,i,6);
		}
	}

	public void cancalall() {
		// TODO Auto-generated method stub
		for(int i=0;i<content.size();++i){
			setValueAt(false,i,6);
		}
	}
	

}


class ProgressEditor extends DefaultCellEditor {
	  private JProgressBar jpb;
	  private int    value;
	  public ProgressEditor(JTextField textField) {
		super(textField);
		// TODO Auto-generated constructor stub
		jpb = new JProgressBar();

	}
	  public Component getTableCellEditorComponent(JTable table, Object value,
              boolean isSelected, int row, int column) {
		  jpb.setStringPainted(true);
		  jpb.setValue(new Integer(value.toString()));
		  return jpb;
	  }

}

class ProgressRenderer extends JProgressBar implements TableCellRenderer {
	@Override
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
		// TODO Auto-generated method stub
		setStringPainted(true);
		setValue(new Integer(value.toString()));
		return this;
	}
	  

}

class ButtonEditor extends DefaultCellEditor {
	  protected JButton button;
	  private String    label;
	  private boolean   isPushed;
	  private loadFileFrame lf = null;
	  public ButtonEditor(JCheckBox checkBox,loadFileFrame lf) {
	    super(checkBox);
	    //rs = new resultFrame();
    	//rs.setSize(800, 400); 
	    this.lf = lf;
	    button = new JButton();
	    button.setOpaque(true);
	    button.addActionListener(new ActionListener() {
	    	
	      public void actionPerformed(ActionEvent e) {
	    	  
	        fireEditingStopped();
	      }
	    });
  
    	
	  }

	  public Component getTableCellEditorComponent(JTable table, Object value,
	                   boolean isSelected, int row, int column) {
	    if (isSelected) {
	      button.setForeground(table.getSelectionForeground());
	      button.setBackground(table.getSelectionBackground());
	    } else{
	      button.setForeground(table.getForeground());
	      button.setBackground(table.getBackground());
	    }
	    label = (value ==null) ? "" : value.toString();
	    button.setText( label );
	    isPushed = false;
     
 
	    return button;
	  }

	/*   public Object getCellEditorValue() {
		   resultFrame rs = new resultFrame();
	    if (isPushed)  {
	      // 
	      // 
	      // JOptionPane.showMessageDialog(button ,label + ": Ouch!");
	       // System.out.println(label + ": Ouch!");
	    	
	    	rs.setSize(600, 600);   
	    	RefineryUtilities.centerFrameOnScreen(rs);//开始在中心位置显示
	  
	    	rs.setVisible(true);
	    	
	    	//问题，不是foucs对话框
 
	    }
	    isPushed = false;
	    //return new String( "新值" ) ;
	    return rs;
	    
	  } */
	  
	  public boolean isCellEditable(EventObject e){
		  if(e instanceof MouseEvent){
			  MouseEvent me = (MouseEvent)e;
			  JTable table = (JTable)e.getSource();
			  int row = table.rowAtPoint(me.getPoint());
			  this.lf.showResPanel(row);
			  //rs.setFileres(row);
			  //System.out.println(row);
			  //rs.setVisible(true);
		  }
		  
		  return false;
	  }
	  
	  public boolean stopCellEditing() {
	    isPushed = false;
	    return super.stopCellEditing();
	  }

	  protected void fireEditingStopped() {
	    super.fireEditingStopped();
	  }
	}
class ButtonRenderer extends JButton implements TableCellRenderer {

	  public ButtonRenderer() {
	    setOpaque(true);
	  }
	  
	  public Component getTableCellRendererComponent(JTable table, Object value,
	                   boolean isSelected, boolean hasFocus, int row, int column) {
	    if (isSelected) {
	      setForeground(table.getSelectionForeground());
	      setBackground(table.getSelectionBackground());
	    } else{
	      setForeground(table.getForeground());
	      setBackground(UIManager.getColor("Button.background"));
	    }
	    setText( (value ==null) ? "" : value.toString() );
	    return this;
	  }
	}
class CenterCellRenderer extends DefaultTableCellRenderer{
	public CenterCellRenderer(){
		setHorizontalAlignment(JLabel.CENTER);
	}
}


class newJTable_mouseAdapter extends java.awt.event.MouseAdapter { 
	 private loadFileFrame lf = null;
 
	 public newJTable_mouseAdapter(loadFileFrame lf){
		 this.lf = lf;
	 }

  public void mouseClicked(MouseEvent e) { 
	  if (e.getButton() == MouseEvent.BUTTON1) {// 单击鼠标左键
		     if (e.getClickCount() == 2) {
		      //int row = this.lfgetSelectedRow();;// 列数
		     // for (int i = 0; i < colummCount; i++)
		       //System.out.print(tb.getModel().getValueAt(tb.getSelectedRow(), i).toString()+ "   ");
		      //System.out.println();
		    	 MouseEvent me = (MouseEvent)e;
				  JTable table = (JTable)e.getSource();
				  int row = table.rowAtPoint(me.getPoint());
				  if(this.lf.isoneFileDone(row)){
					  this.lf.showResPanel(row);
				  }
				  
		     }
		    }
  } 
} 