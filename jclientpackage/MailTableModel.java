package jclientpackage;
import javax.swing.table.AbstractTableModel;

import java.util.ArrayList;
import java.util.List;

 public final class MailTableModel extends AbstractTableModel  {
	private static final long serialVersionUID = -8342074904913845902L;
	
	final static private int CHECKBOX_INDEX = 0;
	final static private int SUBJECT_INDEX = 1;
	final static private int FROM_INDEX = 3;
	final static private int DATE_INDEX = 4;
	
	final private static String[] columns = {" ", "Subject", "", "Sender", "Date"};
    private List<MailMessage> mailList;
    private MailMessage m;
    private JClient mc;
       
    public MailTableModel(JClient mc)    {
       mailList = new ArrayList<MailMessage>();
       this.mc = mc;
    }
    
        @Override
	    final public String getColumnName(int col)    {
	        return columns[col];
	    }
	    
	    final public int getColumnCount()    {
	        return columns.length;
	    }
	    
	    final public int getRowCount()  {
	        return mailList.size();
	    }
	    
	    @Override
	    public Class<?> getColumnClass(int column)  {
	    	return (getValueAt(0, column).getClass());
	    }
	    
	    @Override
	    final public void setValueAt(Object value, int row, int column)  {
	    	m = mailList.get(row);
	    	
	    	if(column == 0) {
	    		m.setCheckBoxValue((Boolean)value);
	    	}
	    }
	    
	    @Override 	
	    final public boolean isCellEditable(int row, int col) {
	    	return (col == 0);
	    }
	
	    final public Object getValueAt(int row, int col)  {
	       m = mailList.get(row);
	           switch(col)  {
	               case CHECKBOX_INDEX:
	            	   if((mc.getStatusHeaderBox()) && (m.getCheckBoxValue() == false)) {
	            		   mc.changeStatusHeaderBox(Boolean.FALSE);
	            		   mc.setStatusHeaderBox(Boolean.FALSE);
	            	   } else if((mc.getStatusHeaderBox().equals(Boolean.FALSE) && checkAllBoxesIsTrue()))  {
	            		   mc.changeStatusHeaderBox(Boolean.TRUE);
	            		   mc.setStatusHeaderBox(Boolean.TRUE);
	            	   }
	        	       return m.getCheckBoxValue();
	               case SUBJECT_INDEX:
	        	       return m.getSubject();
	               case FROM_INDEX:
	            	   return m.getFrom();
	               case DATE_INDEX:
	            	   return m.getDate();
	               default:
	            	   return new Object();  }  
	 	}
	    
	    private Boolean checkAllBoxesIsTrue() {
	    	for(int m = 0; m < mailList.size(); m++)  {
	    		if(mailList.get(m).getCheckBoxValue().booleanValue() == Boolean.FALSE) {
	    			return false;
	    		}
	    	}
	        return true;
	    }
	    
	    public final void setMailList(List<MailMessage> aMailList) {
	    	this.mailList = aMailList;
	    	fireTableDataChanged();
	    }
	    
	    public final List<MailMessage> getMailList() {
	    	return this.mailList;
	    }
	    
	    public final void addMail(MailMessage m)    {
	    	mailList.add(m);
	        fireTableRowsInserted(0, mailList.size() - 1);
	    }
	
	    public final void removeValueAt(int row)    {
	    	mailList.remove(row);
	    	fireTableRowsDeleted(row, row);
	    }
	
	    public final MailMessage getMail(int row)	{
				final MailMessage mm = mailList.get(row);
				return mm;
	    }
	
	    public final void removeAllMail()    {
	    	mailList.clear();
			fireTableRowsDeleted(0, mailList.size() - 1);
	    }
}