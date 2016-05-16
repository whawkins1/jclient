package jclientpackage;
import javax.swing.JCheckBox;
import javax.swing.JTable;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;

import java.awt.Component;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;

import java.awt.event.MouseListener;

public final class CheckBoxHeader extends JCheckBox
                               implements TableCellRenderer, MouseListener{
   
   private static final long serialVersionUID = 2794381556172867130L;
   private final CheckBoxHeader rendererComponent;
   private int column;
   private boolean mousePressed = false;
   final private MailTableModel fMtm;
   
   CheckBoxHeader(ItemListener itemListener, MailTableModel aMtm)  {
	   rendererComponent = this;
	   rendererComponent.addItemListener(itemListener);
       this.fMtm = aMtm; 
   }
   
       @Override
	   final public Component getTableCellRendererComponent(JTable table, Object value,
			                                          boolean isSelected, boolean hasFocus,
			                                          int row, int column) {
		   if(table.isEnabled()) {
			   JTableHeader header = table.getTableHeader();
			       if(header.isEnabled())  {
			          header.addMouseListener(rendererComponent);
			       }
		   }
		   setColumn(column);
	       return rendererComponent;
	   }
	   
	   final private void setColumn(int column) {
		   this.column = column;
	   }
	   
	   final public int getColumn() {
		   return column;
	   }
	   
	   final private void handleClickEvent(MouseEvent e) {
		   if(mousePressed) {
			   mousePressed = false;
			   final JTableHeader header =  (JTableHeader)(e.getSource());
			   final JTable tableView = header.getTable();
			   final TableColumnModel columnModel = tableView.getColumnModel();
			   final int viewColumn = columnModel.getColumnIndexAtX(e.getX());
			   final int column = tableView.convertColumnIndexToModel(viewColumn);
			   
			   if( (viewColumn == this.column) && (e.getClickCount() == 1) && (column != -1)) {
				   doClick();
			   }
		   }
	   }
	   
	   @Override
	   final public void mouseClicked(MouseEvent e)  {
		   handleClickEvent(e);
		   fMtm.fireTableDataChanged();
		   ((JTableHeader)e.getSource()).repaint();
	   }
	   
	   @Override
	   final public void mousePressed(MouseEvent e)  {
		   mousePressed = true;
	   }
	   
	   final public void mouseReleased(MouseEvent e) {
		   
	   }
	   
	   final public void mouseEntered(MouseEvent e)  {
		   
	   }
	   
	   final public void mouseExited(MouseEvent e)  {
		   
	   }
}
