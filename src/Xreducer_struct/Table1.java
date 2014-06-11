package Xreducer_struct;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.*;
/**
 * @author hardneedl
 */
class Table1 extends JFrame {
    private static final Dimension FRAME_SIZE = new Dimension(400, 300);
    private static final String TITLE = "FRAME TITLE";
    public String getTitle() {return TITLE;}
    public Dimension getSize() {return FRAME_SIZE;}

    private JTable table;
    Table1() {
        init();
        doLay();
        handleEvents();
        pack();
        setVisible(true);
    }

    private void init() {
        table = new JTable(new _TableModel());
        TableColumnModel colm =table.getColumnModel();
        Enumeration<TableColumn> enu = colm.getColumns();
        while (enu.hasMoreElements()) {
            TableColumn col = enu.nextElement();
            col.setCellRenderer(new TableCellRenderer(){
                private JLabel l = new JLabel(){
                    public boolean isOpaque() {return true;}
                };
                public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                    l.setText(value.toString());
                    l.setForeground(hasFocus ? Color.RED : Color.black);
                    l.setBackground(hasFocus ? Color.YELLOW :Color.white);

                    return l;
                }
            });
        }
    }

    private void doLay() {
        Container container = getContentPane();
        container.add(new JScrollPane(table), BorderLayout.CENTER);
    }

    private void handleEvents() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private class _TableModel extends AbstractTableModel {
        public int getRowCount() { return 3;}
        public int getColumnCount() {return 4;}
        public Object getValueAt(int rowIndex, int columnIndex) {return "dddddd";}
    }
    public static void main(String[] args) {
        new Table1();
    }
}