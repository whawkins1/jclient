package jclientpackage;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.BorderLayout;
import java.io.IOException;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.Box;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JFrame;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.sun.glass.events.KeyEvent;

public final class AddressBookDialog 
                                   implements ActionListener, ListSelectionListener {
	 private final JTable fAddressTable;
	 private final AddressBookTableModel fTableModel;
	 private JButton fOkButton;
	 @SuppressWarnings("unused")
	private JButton fAddButton;
	 private JButton fEditButton;
	 @SuppressWarnings("unused")
	private JButton fCancelButton;
	 private JButton fRemoveButton;
	 private final String fUserSmtpServerEntered;
	 private final String fUserNameEntered;
	 @SuppressWarnings("unused")
	private final String fUserPassEntered;
	 @SuppressWarnings("unused")
	private final List<MailMessage> fSentList;
	 private final JDialog fAddressBookDialog;
	 private final JFrame fParent;
	 private final List<AddressAccount> fAddressList;
	 private final TableMouseListener fTml;
	 private final JClient fMc;
	 
	public AddressBookDialog(JFrame aParent, String aUserSmtpServerEntered, String aUserNameEntered, String aUserPassEntered, List<MailMessage> aSentList, JClient aMc) {
		this.fParent = aParent;
		this.fUserSmtpServerEntered = aUserSmtpServerEntered;
		this.fUserNameEntered = aUserNameEntered;
		this.fUserPassEntered = aUserPassEntered;
		this.fSentList = aSentList;
		this.fMc = aMc;
		
		fAddressBookDialog = new JDialog(fParent, "Address Book", true);
		final JPanel mainPanel = new JPanel(new BorderLayout());
	    
		fTableModel = new AddressBookTableModel();
	    fAddressTable = new JTable(fTableModel);    
	    fAddressTable.getSelectionModel().addListSelectionListener(this);	   
	    fAddressTable.setFocusable(false);
	    fAddressTable.setShowGrid(false);
	    fAddressTable.getColumnModel().getColumn(2).setMinWidth(1);
	    fAddressTable.getColumnModel().getColumn(2).setPreferredWidth(1);
	    	
	    final JScrollPane scrollPane = new JScrollPane();
	    scrollPane.getViewport().add(fAddressTable, null);
	    scrollPane.getViewport().setBackground(fAddressTable.getBackground());	    
	    mainPanel.add(scrollPane, BorderLayout.CENTER);
	    
	    fAddressList = fTableModel.getAddressList();
	    fTml = new TableMouseListener(fParent, fTableModel, fAddressTable, fUserSmtpServerEntered, fUserNameEntered, aUserPassEntered, aSentList, fMc, this, fAddressBookDialog, fAddressList, "COMPOSE");
	    fAddressTable.addMouseListener(fTml);
	    for(int c = 0; c < fAddressTable.getColumnCount(); c++) {
    		new ClickableMailTableHeader(fAddressTable.getColumnModel().getColumn(c),
    				                     fAddressTable.getTableHeader(), 
    		                             fTableModel, 
    		                             fAddressList);  		                            
    	}
	 
	    final JPanel buttonPanel = new JPanel(new FlowLayout());
	    fOkButton = createButton("Ok", "OK", buttonPanel, false);
	    fAddButton = createButton("Add", "ADD", buttonPanel, true);
	    fRemoveButton = createButton("Remove", "REMOVE", buttonPanel, false);
	    fEditButton = createButton("Edit", "EDIT", buttonPanel, false);
	    fCancelButton = createButton("Cancel", "CANCEL", buttonPanel, true);
	    mainPanel.add(buttonPanel, BorderLayout.PAGE_END);
	    
	    InputMap im = (InputMap)UIManager.get("Button.focusInputMap");
	    im.put(KeyStroke.getKeyStroke("ENTER"), "pressed");
	    im.put(KeyStroke.getKeyStroke("released ENTER"), "released");
		UIManager.put("Button.defaultButtonFollowsFocus", Boolean.TRUE);
	    
	    fAddressBookDialog.getContentPane().add(mainPanel);
	    	    
	    fAddressBookDialog.setSize(420, 600);
	    addEscapeListener(fAddressBookDialog);
        fAddressBookDialog.setLocationRelativeTo(fParent);
        fAddressBookDialog.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        Image image = null;
        try {
        	image = ImageIO.read(getClass().getResource("/resources/address_book.png"));	
        } catch (IOException ioe) {
        	JOptionPane.showMessageDialog(fParent, 
                    "Unable to Access address_book.png.", 
                    "JMail Client",
                    JOptionPane.ERROR_MESSAGE);    	 
        }
        
        fAddressBookDialog.setIconImage(image);
        fAddressBookDialog.setTitle("Address Book");
        fAddressBookDialog.setVisible(true);
	}
	
		
		public void actionPerformed(ActionEvent ae) {
			if(ae.getActionCommand().equals("OK")) {
				final int tableSelectCount = fAddressTable.getSelectedRowCount();
				
				if(tableSelectCount >= 1) {					
					 	 //final int rowsSelected = fAddressTable.getSelectedRowCount();
						 final List<String> composeAddressList = new ArrayList<String>();
						 //if( rowsSelected == 1) {	
						 	//composeAddressList.add(fAddressList.get(fAddressTable.getSelectedRow()).getAddress());
						  //if (rowsSelected >= 1) {
							 final int[] selectedRows = fAddressTable.getSelectedRows();
							 for(int a = 0; a < tableSelectCount; a++) {
								 composeAddressList.add(fAddressList.get(selectedRows[a]).getAddress());
						 }
						 @SuppressWarnings("unused")	 
						 final MailCompose mc = new MailCompose(fMc, fParent, composeAddressList, "", "", -1, "ADDRESS_LIST");
					 					         
				} else {//if(tableSelectCount == 0) {
					JOptionPane.showMessageDialog(fParent, "An Address(s) Must be Selected To Write A Message", "Address Error!", JOptionPane.ERROR_MESSAGE);
				}//} else if(tableSelectCount > 1) {
					//JOptionPane.showMessageDialog(fParent, "Only One Message Can be Written to an Address.", "Address Error!", JOptionPane.ERROR_MESSAGE);
				//}				
				
			} else if(ae.getActionCommand().equals("ADD")) {
				final AddAddressInfo addDialog = new AddAddressInfo(fAddressBookDialog, fTableModel, fAddressList, "Add");				
			    final boolean successAdd = addDialog.showDialog();
			        if(successAdd) {
			        	fOkButton.setEnabled(true);
			        }			    
			} else if(ae.getActionCommand().equals("REMOVE")) {
				final int removeIndex = fAddressTable.getSelectedRow();
				final int removeRowCount = fAddressTable.getSelectedRowCount();
				
				if(removeIndex != -1) {
					if( removeRowCount > 1) {
						final int[] selectedRows = fAddressTable.getSelectedRows();
						Arrays.sort(selectedRows);
						fTableModel.removeMultiAddress(selectedRows);
					} else {
						fTableModel.removeAddress(removeIndex);
					}					
				}
			} else if(ae.getActionCommand().equals("EDIT")) {	
				if(fAddressTable.getSelectedRowCount() == 1 ) {
					new AddAddressInfo(fAddressBookDialog, fTableModel, fAddressList, fAddressTable.getSelectedRow(), "Edit");
				} else {
					JOptionPane.showMessageDialog(fAddressBookDialog, "Only One Account can be Selected for Edit", "Edit Error", JOptionPane.ERROR_MESSAGE);
				}
			} else if(ae.getActionCommand().equals("CANCEL")) {
				fAddressBookDialog.dispose();
			}
		}
		
		private static void addEscapeListener(final JDialog aDialog) {
	    	ActionListener escListener = new ActionListener() {
	    		
	    		@Override
	    		public void actionPerformed(ActionEvent ae) {
	    			aDialog.dispose();
	    		}
	    	};
	    	
	    	aDialog.getRootPane().registerKeyboardAction(escListener, 
	    												KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
	    												JComponent.WHEN_IN_FOCUSED_WINDOW);
	    }
		
		@Override
		public final void valueChanged(ListSelectionEvent lse) {
				if(!(lse.getValueIsAdjusting())) {
					final boolean rowsAreSelected = (fAddressTable.getSelectedRowCount() > 0);
					fEditButton.setEnabled(rowsAreSelected);
					fRemoveButton.setEnabled(rowsAreSelected);
				}
				
			}
		
	    private final JButton createButton(String aTitle, String aActionCommand, JPanel aButtonPanel, Boolean aEnable) {
	    	JButton b = new JButton(aTitle);
	    	b.addActionListener(this);
	    	b.setActionCommand(aActionCommand);
	    	b.setEnabled(aEnable);
	    	aButtonPanel.add(b);
	    	aButtonPanel.add(Box.createHorizontalStrut(4));
	    	
	    	return b;    	
	    }
}
