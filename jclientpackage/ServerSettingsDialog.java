package jclientpackage;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.KeyStroke;

import com.sun.glass.events.KeyEvent;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public final class ServerSettingsDialog implements ActionListener {
	private final JClient fMc;
	private final JDialog fDialog;
    private final JTextField fIMAPField;
    private final JTextField fSMTPField;
    private final JButton fSaveButton;
	
	public ServerSettingsDialog(JFrame aParent, JClient aMc) {
		this.fMc = aMc;
		fDialog = new JDialog(aParent, "Server Settings", true);
		 fIMAPField = new JTextField(18);
	        fIMAPField.setText("imap.gmx.com");
	        fIMAPField.requestFocus();
	        fSMTPField = new JTextField(18);
	        fSMTPField.setText("mail.gmx.com");
	        fSaveButton = new JButton("Save");
	        fSaveButton.addActionListener(this);
	        fSaveButton.setActionCommand("SAVE_SETTINGS");
	        final Dimension dimButton = new Dimension(20, 8);
	        fSaveButton.setSize(dimButton);
	        fSaveButton.setMaximumSize(dimButton);
	        fSaveButton.setMinimumSize(dimButton);
	        final JPanel imapPanel = createPanel("IMAP Server: ", fIMAPField);
	        final JPanel smtpPanel = createPanel("SMTP Server:", fSMTPField);
	        final JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
	        buttonPanel.add(fSaveButton);
	        final JPanel mainPanel = new JPanel();
	        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
	        mainPanel.add(imapPanel);
	        mainPanel.add(mainPanel.add(Box.createVerticalStrut(2)));
	        mainPanel.add(smtpPanel);
	        mainPanel.add(buttonPanel);
	        fDialog.add(mainPanel);
	     
	        fDialog.pack();
	        addEscapeListener(fDialog);
	        fDialog.setLocationRelativeTo(null);
	        fDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
	        fDialog.setVisible(true);
	}
	
	public final void actionPerformed(ActionEvent ae) {
		if(ae.getActionCommand().equals("SAVE_SETTINGS")) {
			final String imapAddress = fIMAPField.getText().trim();
			final String smtpAddress = fSMTPField.getText().trim();
			if((imapAddress.equals("")) || (smtpAddress.equals(""))) {
				int answer = JOptionPane.showConfirmDialog(fDialog,
	                    "The Address Fields are not Complete, Continue?", 
                        "WARNING",
                        JOptionPane.OK_CANCEL_OPTION);
				if(answer == JOptionPane.CANCEL_OPTION) {
					return;
				}
			}
			fMc.setImap(imapAddress);
			fMc.setSmtp(smtpAddress);
			fDialog.dispose();	
		}
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
	
	private final JPanel createPanel(String aTitle, JComponent aTextField) {
		 JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));
    	 p.add(new JLabel(aTitle));
    	 p.add(Box.createHorizontalStrut(2));
    	 p.add(aTextField);
    	 
    	 return p;
	}
}