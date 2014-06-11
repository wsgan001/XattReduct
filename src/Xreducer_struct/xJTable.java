package Xreducer_struct;

import java.awt.Component;
import java.util.Vector;

import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;



import Xreducer_gui.loadFileFrame;
import Xreducer_gui.loadFileFrame.StateInfo;

public class xJTable extends JTable {
	private xTablemodel xmodel = null;
	
	public xJTable(String[] title){
		this.xmodel = new xTablemodel(title);
		this.setModel(this.xmodel);
	}
	
	public void addData(Vector data){
		this.xmodel.addRow(data);
		this.updateUI();
	}
	public void deleteData(int row){
		this.xmodel.removeRow(row);
		this.updateUI();
	}
	public void setPreferredWidth(){
		int icols = this.getColumnCount();
		for(int i=0;i<icols;++i){
			int   with = getPreferredWidthForCloumn(i)+10; 
	            this.getColumnModel().getColumn(i).setPreferredWidth(with); 
		}
	}
	 private int getPreferredWidthForCloumn(int   icol){ 
	        TableColumnModel   tcl = this.getColumnModel(); 
	        TableColumn   col = tcl.getColumn(icol); 
	        int   c = col.getModelIndex(),width = 0,maxw = 0; 
	        for(int   r=0;r <this.getRowCount();++r){ 
	            TableCellRenderer   renderer   =   this.getCellRenderer(r,c); 
	            Component   comp   =   renderer.getTableCellRendererComponent(this,this.getValueAt(r,c),false,false,r,c); 
	            width   =   comp.getPreferredSize().width; 
	            maxw   =   width   >   maxw?width:maxw; 
	        }
	        return width;
	} 
}

/** 
* TableModel类，继承了AbstractTableModel 
*/ 
class xTablemodel extends AbstractTableModel { 
 
  //  private static final long serialVersionUID = -7495940408592595397L; 
 
    private Vector content = null; 
    private String[] title = null; 
 
    public xTablemodel(String[] title) { 
        this.content = new Vector(); 
        this.title = title;
    } 
    public void addRow(Vector instance) { 
        content.add(instance); 
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
        	 return false;
    } 

    //使修改的内容生效 
    public void setValueAt(Object value, int row, int col) { 
        ((Vector) content.get(row)).remove(col); 
        ((Vector) content.get(row)).add(col, value); 
        this.fireTableCellUpdated(row, col); 
    } 
 
    public String getColumnName(int col) { 
        return title[col]; 
    } 
 
    public int getColumnCount() { 
        return title.length; 
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
}