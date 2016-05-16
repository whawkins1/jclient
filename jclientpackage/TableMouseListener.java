package jclientpackage;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.mail.Message;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JPopupMenu;
import javax.swing.JMenuItem;
import javax.swing.JDialog;
import javax.swing.JSeparator;

import java.util.List;
import java.util.ArrayList;

public final class TableMouseListener extends MouseAdapter  
                                             implements ActionListener {
	
	final private JFrame fParent;
	final private MailTableModel fMtm;
	@SuppressWarnings("unused")
	private AddressBookTableModel fAbtm;
	final private JTable fTable;
	private String fUserSmtpServerEntered;
	private String fUserNameEntered;
	private String fUserPassEntered;
	private final JClient fMc;
	private final String fMode;
	@SuppressWarnings("unused")
	private List<MailMessage> fSentList;
	private final JPopupMenu fPopupMenuMain;
	private final JMenu fSubMenuMove;
	@SuppressWarnings("unused")	
	private final JMenuItem fOpenItem;
	@SuppressWarnings("unused")
	private final JMenuItem fDeleteItem;
	@SuppressWarnings("unused")
	private final JMenuItem fReplyItem;
	@SuppressWarnings("unused")
	private final JMenuItem fReplyAllItem;
	@SuppressWarnings("unused")
	private final JMenuItem fForwardItem;
	private final JMenuItem fSetReadItem;
	private final JMenuItem fSetUnreadItem;
	private final JMenuItem fSetSpamItem;
	@SuppressWarnings("unused")
	private final JMenuItem fMoveToInbox;
	@SuppressWarnings("unused")
	private final JMenuItem fMoveToRead;
	private final JMenuItem fMoveToSpam;
	private final CheckBoxHeader fCbh;
	private List<AddressAccount> fAccountList;
	@SuppressWarnings("unused")
	private AddressBookDialog fAddressBook;
	private JDialog fDialog;
	
	
	public TableMouseListener(JFrame aParent, MailTableModel aMTM, JTable aTable, JClient aMc, String aMode, CheckBoxHeader aCbh)  {
		this.fParent = aParent;
		this.fMtm = aMTM;
		this.fTable = aTable;
		this.fMc = aMc;
		this.fMode = aMode;
		this.fCbh = aCbh;
		
		final JSeparator firstSeparator = new JSeparator();
		fPopupMenuMain = new JPopupMenu();
		fOpenItem = createMenuItem("Open", "OPEN_MESSAGE", fPopupMenuMain, "/resources/mailopen.png");
		fPopupMenuMain.add(firstSeparator);
		fDeleteItem = createMenuItem("Delete", "DELETE_MESSAGE", fPopupMenuMain, "/resources/mail_delete.png");
		fSetReadItem = createMenuItem("Mark as Read", "MARK_AS_READ", fPopupMenuMain, "/resources/markread.png");
		fSetUnreadItem = createMenuItem("Mark as Unread", "MARK_AS_UNREAD", fPopupMenuMain, "/resources/markunread.png");
		fSetSpamItem = createMenuItem("This is Spam", "THIS_IS_SPAM", fPopupMenuMain, "/resources/mailspam.png");
		final JSeparator secondSeparator = new JSeparator();
		fPopupMenuMain.add(secondSeparator);
		fReplyItem = createMenuItem("Reply To Sender", "REPLY_TO_SENDER", fPopupMenuMain, "/resources/mailreply.png");
		fReplyAllItem = createMenuItem("Reply To All", "REPLY_TO_ALL", fPopupMenuMain, "/resources/mailreplyall.png");
		fForwardItem = createMenuItem("Forward", "FORWARD", fPopupMenuMain, "/resources/forward.png");		
		final JSeparator thirdSeparator = new JSeparator();
		fPopupMenuMain.add(thirdSeparator);
		fSubMenuMove = new JMenu("Move");
		fMoveToInbox = createMenuItem("Inbox", "MOVE_INBOX", fSubMenuMove, "/resources/forward.png");
		fMoveToRead = createMenuItem("Read", "MOVE_READ", fSubMenuMove, "/resources/forward.png");
		fMoveToSpam = createMenuItem("Spam", "MOVE_SPAM", fSubMenuMove, "/resources/forward.png");
		fPopupMenuMain.add(fSubMenuMove);
	}
	
	public TableMouseListener(JFrame aParent, AddressBookTableModel aAbtm, JTable aTable, String aUserSmtpServerEntered, String aUserNameEntered, 
			                  String aUserPassEntered, List<MailMessage> aSentList, JClient aMc, AddressBookDialog aAddressBook, JDialog aAddressBookDialog, List<AddressAccount> aAccountList,  String aMode) {
		this(aParent, new MailTableModel(aMc), aTable, aMc, aMode, null);
	    this.fAbtm = aAbtm;	
	    this.fUserSmtpServerEntered = aUserSmtpServerEntered;
	    this.fUserNameEntered = aUserNameEntered;
	    this.fUserPassEntered = aUserPassEntered;
        this.fSentList = aSentList;
        this.fAddressBook = aAddressBook;
        this.fDialog = aAddressBookDialog;
        this.fAccountList = aAccountList;
	}
	
		public final void actionPerformed(ActionEvent ae) {
			final String ac = ae.getActionCommand();
			if(ac.equals("OPEN_MESSAGE")) {
				final int rowOpenIndex = fTable.getSelectedRow();
				if(fTable.getSelectedRowCount() == 1) {
					open(rowOpenIndex);
				} else {
					JOptionPane.showMessageDialog(fDialog, 
                                                  "Only a Single Message Can be Opened, Please Try Again.", 
                                                  "ERROR!",
                                                  JOptionPane.ERROR_MESSAGE);
				}
			} else if (ac.equals("DELETE_MESSAGE")) {
				fMc.delete();
			} else if (ac.equals("REPLY_TO_SENDER")) {
				fMc.reply("REPLY_TO_SENDER");
			} else if (ac.equals("REPLY_TO_ALL")) {
				fMc.reply("REPLY_TO_ALL");
			} else if (ac.equals("FORWARD")) {
				fMc.forward();
			} else if(ac.equals("MARK_AS_SPAM") || (ac.equals("MOVE_SPAM"))) {
				fMc.markSpam();
			} else if(ac.equals("MARK_AS_READ") || (ac.equals("MOVE_READ"))) {
				fMc.setFlagRead(true);
			} else if(ac.equals("MARK_AS_UNREAD") || (ac.equals("MOVE_INBOX")) ) {
				fMc.setFlagRead(false);
			}
		}		
	
		@Override
		public final void mouseClicked(MouseEvent me) {
			final JTable source = (JTable) me.getSource();
			final int row = source.rowAtPoint(me.getPoint());
			final String nodePath = fMc.getNodePath();
			final boolean tableEnabled = fTable.isEnabled();
			
			if(tableEnabled) {
				if(row == -1)  {
					fTable.clearSelection();
				} else if(me.isControlDown()) {
					List<MailMessage> tableList = fMtm.getMailList();
					if(tableList.size() > 0) {
						final MailMessage message = tableList.get(row);
						final Boolean checked = message.getCheckBoxValue();
						final boolean value = (checked) ? false : true;
						message.setCheckBoxValue(value);						
						fTable.repaint();
					}
				} else if(me.getClickCount() == 2) {
				   if((nodePath != null) && (fMc.getNodePath().trim().equals("[Local Folders, Drafts]"))) {
							 final MailMessage draftMessage = fMc.getDraftList().get(row);
							 new MailCompose(fMc, fParent, "OPEN_DRAFT", draftMessage, row);
				   } else if(fMode.equals("COMPOSE") ) {  
				        	if((fUserSmtpServerEntered != "") || (fUserNameEntered != "") || (fUserPassEntered != "")) {
				        		 	 final List<String> sendToList = new ArrayList<String>();
				        		 	 sendToList.add(fAccountList.get(row).getAddress());
									 @SuppressWarnings("unused")
									final MailCompose mc = new MailCompose(fMc, fParent, sendToList, "", "", -1, "ADDRESS_SELECT");
		        			} else {
		        				JOptionPane.showMessageDialog(fParent,
		        						                      "You Must be Logged into the Mail Server To Send Messages", 
		        						                      "Connection Error!", 
		        						                      JOptionPane.ERROR_MESSAGE);
		        			}
					} else {
					    open(row);
					}
				}
			}
	   }
		
		@Override
		public final void mouseReleased(MouseEvent me) {
			if(me.isPopupTrigger() && fTable.isEnabled()) {
				final JTable source = (JTable) me.getSource();
				final int row = source.rowAtPoint(me.getPoint());
				final int column = source.columnAtPoint(me.getPoint());

					if(!source.isRowSelected(row)) {
						source.changeSelection(row, column, false, false);
					}
				         final List<MailMessage> messageList = fMtm.getMailList();
				         final int messageSize = messageList.size();
				         final MailMessage messageChecked = messageList.get(row);
				        if(fCbh.isSelected()) {
							fCbh.setSelected(false);
						} else if(messageSize > 0 && (!messageChecked.getCheckBoxValue())) {
							for(int index = 0; index < messageSize; index++) {
								final MailMessage message = messageList.get(index);
								message.setCheckBoxValue(false);	
			    			}
							messageChecked.setCheckBoxValue(true);
				       }
					   fPopupMenuMain.show(me.getComponent(), me.getX(), me.getY());
			}
		}
		
		public final void enableMenuItem(String aDisableItem, boolean aItemEnabled) {
			if(aDisableItem.equals("SPAM")) {
				fSetSpamItem.setEnabled(aItemEnabled);
			} else if(aDisableItem.equals("MARK_AS_UNREAD")) {
				fSetUnreadItem.setEnabled(aItemEnabled);
			} else if(aDisableItem.equals("MARK_AS_READ")) {
				fSetReadItem.setEnabled(aItemEnabled);
			} else if(aDisableItem.equals("MOVE")){
				fSubMenuMove.setEnabled(aItemEnabled);
		    } else if(aDisableItem.equals("MOVE_SPAM")){
		    	fMoveToSpam.setEnabled(false);
		    }
		}
		
		public final void disableAllMenuItem() {
			fSetSpamItem.setEnabled(false);
			fSetUnreadItem.setEnabled(false);
			fSetReadItem.setEnabled(false);
		}
		
		private final JMenuItem createMenuItem(String aTitle, String aActionCommand, JComponent aPopup,
				                               String aIconPath) {
			ImageIcon icon = null;
			try {
			     final Image image = ImageIO.read(getClass().getResource(aIconPath));
			     final Image scaledImage = image.getScaledInstance(20, 20, Image.SCALE_SMOOTH);
			     icon = new ImageIcon(scaledImage);
			} catch(IOException ioe) {
				JOptionPane.showMessageDialog(fParent,
	                                          "Error Accessing " + aTitle + " Icon", 
	                                          "JMail Client", 
	                                          JOptionPane.ERROR_MESSAGE);
			}
			JMenuItem m = new JMenuItem(aTitle, icon);
			m.addActionListener(this);
			m.setActionCommand(aActionCommand);
			
			aPopup.add(m);
			return m;			
		}
		
		private final void open(int aRow) {
				final MailMessage openedMessage = fMtm.getMail(aRow);
				final Message[] messageArr = fMc.getCurrentArr();
				Message message = messageArr[aRow];
				final List<MailMessage> mailList = fMtm.getMailList();
				for(int index = 0; index < mailList.size(); index++) {
					final MailMessage messageCheckValue = mailList.get(index);
					final Boolean messageChecked = messageCheckValue.getCheckBoxValue();
					if(messageChecked) {
						messageCheckValue.setCheckBoxValue(false);
					}
				}
    			fMc.setFlagRead(true);
				new MailDisplay(fParent, openedMessage, message, fMc);
		}
}