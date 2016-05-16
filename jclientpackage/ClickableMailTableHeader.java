package jclientpackage;
import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.Insets;
import java.awt.Component;

import java.util.Collections;
import java.util.List;
import java.util.ArrayList;

import javax.swing.table.TableColumn;
import javax.swing.table.JTableHeader;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JButton;
import javax.swing.table.TableCellRenderer;

 public final class ClickableMailTableHeader {
	private final TableColumn fColumn;
	private final JTableHeader fHeader;
	private final MailTableModel fMtm;
	private AddressBookTableModel fAbtm;
	private final JPanel fTableHeaderPanel;
	private List<MailMessage> fMailList;
	private List<AddressAccount> fAddressList = new ArrayList<AddressAccount>();
	
	public ClickableMailTableHeader(TableColumn aColumn, JTableHeader aHeader, MailTableModel aTableModel, List<MailMessage> aMailList) {
		this.fColumn = aColumn;
		this.fHeader = aHeader;
		this.fMtm = aTableModel;
		this.fMailList = aMailList;
		
		aHeader.setReorderingAllowed(false);
		fTableHeaderPanel = new JPanel(new BorderLayout());
		ButtonHeaderRenderer  renderer = new ButtonHeaderRenderer();
		fColumn.setHeaderRenderer(renderer);
		fHeader.addMouseListener(new HeaderListener(fHeader, renderer));
	}
	
	public ClickableMailTableHeader(TableColumn aColumn, JTableHeader aHeader, AddressBookTableModel aTableModel, List<AddressAccount> aAddressList) {
		this(aColumn, aHeader, null, new ArrayList<MailMessage>());
		this.fAbtm = aTableModel;
		this.fAddressList = aAddressList;
	}
	
	public final class HeaderListener extends MouseAdapter {
		final JTableHeader fTableHeaderListener;
		final ButtonHeaderRenderer fRenderer;
				
		HeaderListener(JTableHeader aHeaderListener, ButtonHeaderRenderer aRenderer) {
		    this.fTableHeaderListener = aHeaderListener;
		    this.fRenderer = aRenderer;
		}
		
			@Override
			public final void mouseClicked(MouseEvent me) {
				final int fColumnIndex = fHeader.columnAtPoint(me.getPoint());
							
				if(fMailList.size() > 0) {
					if((fColumnIndex != -1) && (fColumnIndex != 0)) {
						switch(fColumnIndex)  {
						   case 1:
							   setSortModeMail("Sort By Subject");
						       break;
						   case 2:
							   setSortModeMail("Sort By From");
							   break;
						   case 3:
							   setSortModeMail("Sort By Date");
							   break;
						   default:
							   break;
						}			
						Collections.sort(fMailList);
						fMtm.setMailList(fMailList);					
					}
			   } else if(fAddressList.size() > 0)  {  
					   if(fColumnIndex != -1)  {
						   switch(fColumnIndex)  {
						   case 0:
							   System.out.println("Here");
							   setSortModeAddress("Sort By First Name");
						       break;
						   case 1:
							   setSortModeAddress("Sort By Last Name");
							   break;
						   case 2:
							   setSortModeAddress("Sort By Middle Initial");
							   break;
						   case 3:
							   setSortModeAddress("Sort By Address");
						   default:
							   break;
						   }
				}
				   Collections.sort(fAddressList);
				   fAbtm.setAddressList(fAddressList);			   
			   }
			};
			
			@Override
	 	    public final void mousePressed(MouseEvent me)	    {
			    fRenderer.setPressedColumn(fHeader.getTable().convertColumnIndexToModel(fHeader.columnAtPoint(me.getPoint())));
			    fTableHeaderPanel.repaint();    	
		    }
		
		    @Override
		    public final void mouseReleased(MouseEvent me)	    {
			    fRenderer.setPressedColumn(-1);
			    fTableHeaderPanel.repaint();   
		    }	
		    
		    private final void setSortModeMail(String aSortModeText)  {
		    	for(int m = 0; m < fMailList.size(); m++) {
		    		MailMessage message = fMailList.get(m);
		    	    message.setSortMode(aSortModeText);
		    	}
		    }
		    
		    private final void setSortModeAddress(String aSortModeText) {
		    	for(int a = 0; a < fAddressList.size(); a++) {
		    		AddressAccount account = fAddressList.get(a);
		    		account.setSortMode(aSortModeText);
		    	}
		    }
	    
	}
	class ButtonHeaderRenderer extends JButton 
	                              implements TableCellRenderer  {
	
		private static final long serialVersionUID = 1210660439076176796L;
		int fPushedColumn;
		
		ButtonHeaderRenderer() {
			fPushedColumn = -1;
			setMargin(new Insets(0,0,0,0));			
		}
		
	    @Override
	    public final Component getTableCellRendererComponent(JTable aTable, Object aValue, boolean aIsSelected, boolean aHasFocus, int aRow, int aColumn)  {
            setText((aValue == null) ? "" : aValue.toString());
            boolean isPressed = (aTable.convertColumnIndexToModel(aColumn) == fPushedColumn);
            getModel().setPressed(isPressed);
            getModel().setArmed(isPressed);
            return this;
	    }	
	    
	    public void setPressedColumn(int col)  {
	    	fPushedColumn = col;
	    }
	}
	
	public final void setList (List<MailMessage> aMailList) {
    	fMailList = aMailList;
    } 
}
