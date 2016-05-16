package jclientpackage;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.IOException;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.imageio.ImageIO;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Store;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.JDialog;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.KeyStroke;

import com.sun.glass.events.KeyEvent;

public final class MailLoginDialog  
                       implements ActionListener, ItemListener{
	private final List<JPanel> fPanelList;
    private final JTextField fUserField;
    private final JPasswordField fPassField;
    private final JButton fLoginButton;
    private final JDialog fLoginDialog;
    private final JCheckBox fCbSecured;
    @SuppressWarnings("unused")
	private final JButton fCancelButton;
    private final JClient fMc;
    private boolean fSecuredConnection;    
    private boolean fLoginSuccess;
    private final JFrame fParent;
    
    public MailLoginDialog(JFrame aParent, JClient aMc) {
    	this.fMc = aMc;
    	this.fParent = aParent;
    	fLoginDialog = new JDialog(fParent, "Login", true);
    	fPanelList = new ArrayList<JPanel>(3);
    	fUserField = new JTextField(22);
        fPassField = new JPasswordField(20);
       
        final JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        fLoginButton = createButton("Login", "LOGIN", buttonPanel);
        buttonPanel.add(Box.createHorizontalStrut(4));
        fCancelButton = createButton("Cancel", "CANCEL", buttonPanel);
        @SuppressWarnings("unused")
		final JPanel userPanel = createPanel("E-mail:", fUserField, fPanelList);
        @SuppressWarnings("unused")
		final JPanel passPanel = createPanel("Password:", fPassField, fPanelList);
        fCbSecured = new JCheckBox("Secured", false);
        fCbSecured.addItemListener(this);
        fCbSecured.setActionCommand("SECURED");
        final JPanel cbPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        cbPanel.add(fCbSecured);
        fPanelList.add(cbPanel);
        fPanelList.add(buttonPanel);
                        
        final JPanel loginPanel = new JPanel();
    	loginPanel.setLayout(new BoxLayout(loginPanel, BoxLayout.Y_AXIS));
    	 
    	 for(int p = 0; p < fPanelList.size(); p++)  {
    		 loginPanel.add(fPanelList.get(p));
             loginPanel.add(Box.createVerticalStrut(2));
    	 }
    	 
    	 Image image = null;
    	 try{
 			image = ImageIO.read(getClass().getResource("/resources/mail_message_new.png"));
 		} catch (IOException ioe) {
 			JOptionPane.showMessageDialog(aParent, 
                                  "Unable to Access mail_message_new.png.", 
                                  "JMail Client",
                                  JOptionPane.ERROR_MESSAGE);    	 
 		}
    	 fLoginDialog.setIconImage(image);
    	 fLoginDialog.getRootPane().setDefaultButton(fLoginButton);
    	 fLoginDialog.getContentPane().add(loginPanel);
    	 fLoginDialog.pack();
    	 addEscapeListener(fLoginDialog);
    	 fLoginDialog.setLocationRelativeTo(null);
    	 fLoginDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    	 fLoginDialog.setVisible(true);
    }	
	 
	    public final void actionPerformed(ActionEvent ae) {
	    	final String command = ae.getActionCommand();
	    	Store store = null;
			 if(command.equals("LOGIN"))  {
		    	 final  String emailEntered = fUserField.getText().trim();
					 if(emailEntered.contains("@"))  {
						
						 final String pass = new String(fPassField.getPassword());
						 String imapProtocol = "imap";
						 String port = "143";
						 if(fSecuredConnection) {
							 imapProtocol = "imaps";
						     port = "993";
						 }
						 fLoginSuccess = false;
					 try {
						     final Properties properties = System.getProperties();
							 properties.setProperty("mail.store.protocol", imapProtocol);
					    	 properties.setProperty("mail.imap.port", port);
					    	 properties.setProperty("mail.imap.connectiontimeout", "5000");
					    	 properties.setProperty("mail.imap.timeout", "5000");
					    	 final Session imapSession = Session.getDefaultInstance(properties, null);
							 store = imapSession.getStore(imapProtocol);
				    	     final String imapServerAddress = fMc.getImapSeverAddress();
				    	     store.connect(imapServerAddress, emailEntered, pass);
				    		 fLoginSuccess = true;
					    } catch (NoSuchProviderException e) {
					    	JOptionPane.showMessageDialog(fParent, 
                                                          "No Such Email Provider.", 
                                                          "JMail Client",
                                                          JOptionPane.ERROR_MESSAGE);
						} catch (MessagingException e) {
							JOptionPane.showMessageDialog(fParent, 
		                                                  "Email, Password, or Server Address Invalid.", 
                                                          "JMail Client",
                                                          JOptionPane.ERROR_MESSAGE);
						}
				    	 if(fLoginSuccess) {
				    		 fMc.setSecuredConnection(fSecuredConnection);
				    		 fMc.setStore(store);
				    		 fMc.setUserName(emailEntered);
				    		 fMc.setPass(pass);
						      fLoginDialog.dispose();
				    	 } else {
				    		 fUserField.setText("");
				    		 fPassField.setText("");
				    		 fCbSecured.setSelected(false);
				    	 }
				    } else {
						 JOptionPane.showMessageDialog(fLoginDialog,
								 					   "The Entered Username must have the Format jdoe@gmx.com", 
								 					   "Username Error", 
								 					   JOptionPane.ERROR_MESSAGE);
			         }
			 } else if(command.equals("CANCEL")) {
				 fLoginDialog.dispose();				 
			 }
	     }
	    
	    public final void itemStateChanged(ItemEvent ie) {
	    	if(fCbSecured.isSelected()) {
	    		fSecuredConnection = true;
	    	} 
	    }
	    
	     public final boolean showDialog() {
	    	 return fLoginSuccess;
	     }
		 	 
	     private static void addEscapeListener(final JDialog dialog) {
		    	ActionListener escListener = new ActionListener() {
		    		@Override
		    		public void actionPerformed(ActionEvent ae) {
		    			dialog.dispose();
		    		}
		    	};
		    	
		    	dialog.getRootPane().registerKeyboardAction(escListener, 
		    												KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
		    												JComponent.WHEN_IN_FOCUSED_WINDOW);
		    }
	     
	     private final JPanel createPanel(String aTitle, JTextField aPanelTextField, List<JPanel> aPanelList)  {
	    	 JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));
	    	 p.add(new JLabel(aTitle));
	    	 p.add(Box.createHorizontalStrut(2));
	    	 p.add(aPanelTextField);
	    	 aPanelList.add(p);
	    	 return p;
	     }
		 
		 private final JButton createButton(String aTitle, String aActionCommand, JPanel aButtonPanel)  {
			 JButton b = new JButton(aTitle);
			 b.addActionListener(this);
			 b.setActionCommand(aActionCommand);
			 aButtonPanel.add(b);
			 return b;
		 }
}
