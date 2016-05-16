package jclientpackage;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.util.Arrays;
import java.util.EventObject;
import java.util.LinkedList;
import java.util.List;

import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.search.FlagTerm;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.AbstractCellEditor;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeCellEditor;
import javax.swing.JFrame;

public final class ButtonCellEditor extends AbstractCellEditor
                                                 implements TreeCellEditor, ActionListener {
 
	private static final long serialVersionUID = 1L;
final Color lightBlue = new Color(204, 255, 255);
   private JFrame fParent;
   private JClient fMc;
   private Object fValue;
   private JButton fRefreshButton;
   private JButton fTrashButton;
   private JButton fSpamButton;
   private JLabel fDefaultLabel;
   private JLabel fRefreshLabel;
   private JLabel fSpamLabel;
   private JPanel fTrashPanel;
   private JPanel fSpamPanel;
   private JPanel fRefreshPanel;
   private JPanel fDefaultPanel;
   
	public ButtonCellEditor(JFrame aParent, JClient aMc) {
		this.fParent = aParent;
		this.fMc = aMc;
		fRefreshButton = createButton("/resources/refreshSmall.png", "REFRESH_MAIL", "Check New Mail");
		fRefreshLabel = new JLabel("InBox");
		fRefreshPanel = createPanel(fRefreshButton, fRefreshLabel);
		fTrashButton = createButton("/resources/emptyTrash.png", "EMPTY_TRASH", "Empty Trash");
		final JLabel trashLabel = new JLabel("Trash");
		fTrashPanel = createPanel(fTrashButton, trashLabel);
		fSpamButton = createButton("/resources/emptyTrash.png", "EMPTY_SPAM", "Empty Spam");
		fSpamLabel = new JLabel("Spam");
		fSpamPanel = createPanel(fSpamButton, fSpamLabel);
		fDefaultPanel = new JPanel(new BorderLayout());
		fDefaultPanel.setOpaque(true);
		fDefaultLabel = new JLabel();
		fDefaultPanel.add(fDefaultLabel, BorderLayout.WEST);
		
	}

	 @Override
	 public Component getTreeCellEditorComponent(JTree tree, Object value, boolean selected,
			                                      boolean expanded, boolean leaf, int row) {
	 
		 	this.fValue = value;
		 	final List<MailMessage> messageList;
		    Color highlightColor;
			if(selected) {
				highlightColor = lightBlue;
			} else {
				highlightColor = Color.white;
			}
		    if(row == 1) {
		    	fRefreshPanel.setBackground(highlightColor);
		    	fRefreshPanel.requestFocusInWindow();
		    	return fRefreshPanel;
		    }else if(row == 7) {
		    	messageList = fMc.getTrashList();
		 		if(messageList.size() > 0) {
		 			fTrashButton.setVisible(true);
		 			fTrashButton.setEnabled(true);
		 		} else {
		 			fTrashButton.setVisible(false);
		 			fTrashButton.setEnabled(false);
		 		}
		 		fTrashPanel.setBackground(highlightColor);
		 		fTrashPanel.grabFocus();
		 		return fTrashPanel;
			} else if (row == 6)  {
				messageList = fMc.getSpamList();
				if(messageList.size() > 0) {
					fSpamButton.setVisible(true);
					fSpamButton.setEnabled(true);
				} else {
					fSpamButton.setEnabled(false);	
				    fSpamButton.setVisible(false);
				}
				fSpamPanel.setBackground(highlightColor);
				fSpamPanel.grabFocus();
				return fSpamPanel;
			} else {
				final DefaultMutableTreeNode node = (DefaultMutableTreeNode)value;
				final String nodeText = node.getUserObject().toString();
				fDefaultLabel.setText(nodeText);
				fDefaultPanel.setBackground(highlightColor);
				fDefaultPanel.grabFocus();
			}		 	
		    return fDefaultPanel;
	 }
	 
	 @Override
		public Object getCellEditorValue() {
			return this.fValue;		
	 }
	 
	 private void deleteAllMessages(List<MailMessage> aMessageList, Message[] aMessageArr) {
		 final int option = JOptionPane.showConfirmDialog(fParent, 
                 "Delete all Messages in this Folder" + '\n'
                 + "This is Permanent!",
                 "JMail Client",
                 JOptionPane.YES_NO_OPTION,
                 JOptionPane.WARNING_MESSAGE); 
		 if(option == JOptionPane.YES_OPTION) { 
			 List<Message> messageArrConv = new LinkedList<Message>(Arrays.asList(aMessageArr));
		   	  messageArrConv.clear();
		   	  aMessageArr = messageArrConv.toArray(new Message[0]);
		   	  aMessageList.clear();
		 }
	 }
	 
     @Override
	 public void actionPerformed(ActionEvent ae) {
		 final String command = ae.getActionCommand();
		 if(command.equals("EMPTY_TRASH")) {
			  final List<MailMessage> trashMessageList = fMc.getTrashList();
              final Message[] trashMessageArr = fMc.getTrashMessages();
              deleteAllMessages(trashMessageList, trashMessageArr);
		 } else if (command.equals("EMPTY_SPAM")) {
			  final List<MailMessage> spamMessageList = fMc.getSpamList();
          	  final Message[] spamMessageArr = fMc.getSpamMessages();
          	  deleteAllMessages(spamMessageList, spamMessageArr);
		 } else if(command.equals("REFRESH_MAIL")){
			 try {
				 final Folder folder = fMc.getInBoxFolder();
				 
     	    	if(folder.hasNewMessages()) {
     	    		List<MailMessage> inBoxUnseenList = fMc.getUnseenList();
     	    		addRecentMessages(folder, inBoxUnseenList);
     	    		final MailTableModel tableModel = fMc.getTableModel();
     	    		tableModel.setMailList(inBoxUnseenList);
     	    	}
	        	
	        	} catch(MessagingException me) {
	        		JOptionPane.showMessageDialog(fParent,
		                                          "Error Checking Folder For New Messages!", 
		                                          "JMail Client",
		                                          JOptionPane.ERROR_MESSAGE);
	        	}
		 }
		 stopCellEditing();
	 }
     
     private final void addRecentMessages(Folder aFolder, List<MailMessage> recentMessageList) {
			try {
 	        final Message recentMessages[] = aFolder.search(new FlagTerm(new Flags(Flags.Flag.RECENT), true));
 	            
				for(int index = 0; index < recentMessages.length; index++) {
  	    		     final MailMessage recentMessage = new MailMessage(false);
  	    		     recentMessageList.add(recentMessage);
	    	    }
				if(recentMessageList.size() > 0) {
					fMc.updateFolderTreeExt();
				}
			} catch(MessagingException me) {
				JOptionPane.showMessageDialog(fParent,
						                      aFolder.getName() + " Could Not Be Searched", 
						                      "JMail Client",
						                      JOptionPane.ERROR_MESSAGE);
			}
	}
     
     public void setLabel(int aRow, String aLabelText) {
 		if(aRow == 1) {
 			fRefreshLabel.setText(aLabelText);
 		} else if(aRow == 6) {
 			fSpamLabel.setText(aLabelText);
 		}
 	}
     
     public boolean isCellEditable(EventObject aEvent) {
    	 if(aEvent instanceof MouseEvent) {
    		 return ((MouseEvent)aEvent).getClickCount() >= 1;
    	 }
    	 return true;
     }
	 
	 private JPanel createPanel(JButton aButton, JLabel aLabel) {
		 	final JPanel p = new JPanel(); 
		 	p.setOpaque(true);
		 	p.setFocusable(true);
		 	p.requestFocus(true);
		 	p.setLayout(new BorderLayout());
			p.add(aLabel, BorderLayout.WEST);
			p.add(aButton, BorderLayout.EAST);
			
			return p;
		}
	 
	 private JButton createButton(String aImagePath, String aCommand, String aToolTipText) {
			final JButton b = new JButton();
			b.addActionListener(this);
			b.setActionCommand(aCommand);
			b.setToolTipText(aToolTipText);
			b.setOpaque(false);
			b.setContentAreaFilled(false);
			final Dimension d = new Dimension(18, 18);
			b.setPreferredSize(d);
			b.setMaximumSize(d);
			b.setMinimumSize(d);
			b.setIcon(new ImageIcon(getClass().getResource(aImagePath)));
			return b;
		}
}
