package jclientpackage;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;
import java.util.ArrayList;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.web.HTMLEditor;

import javax.mail.Address;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ImageIcon;

import com.sun.glass.events.KeyEvent;

public  class MailCompose  {
	 @SuppressWarnings("unused")
	private final String fSmtpServer;
	 @SuppressWarnings("unused")
	private final String fUserName;
	 @SuppressWarnings("unused")
	private final String fPassword;
	 private final JTextField fToTextField;
	 private final JTextField fSubjectTextField;
	 private String fHtmlMessage;
	 private String fMode;
	 private final JClient fMc;
	 private final JDialog fComposeDialog;
	 @SuppressWarnings("unused")
	private List<MailMessage> fSentList;
	 private List<String> fSendToList;
	 private String fSubjectDraft;
	 private String fContentDraft;
	 private final List<JPanel> fRemovePanelList = new ArrayList<JPanel>();
	 private MailMessage fReplyMessage;
	 private int fEditDraftRow;
	
	public MailCompose(JClient aMc, JFrame aParent, List<String> aSendToList, String aSubject, String aContent, int aRow, String aMode)  {
		fComposeDialog = new JDialog(aParent, "Compose");
		
		this.fMc = aMc;
		this.fUserName = fMc.getUserName();
		this.fPassword = fMc.getPassword();
		this.fSmtpServer = fMc.getSmtp();
		this.fSentList = fMc.getSentList();
		this.fSendToList = aSendToList;
		this.fSubjectDraft = aSubject;
		this.fContentDraft = aContent;
		this.fEditDraftRow = aRow;
		this.fReplyMessage = fMc.getMessageReply();
		this.fMode = aMode;
		
		fToTextField = new JTextField(30);		
		final JPanel toPanel = createPanel("To: ", fToTextField);
		
		final int sendToListSize = fSendToList.size();
		if(sendToListSize == 1) {
			fToTextField.setText(fSendToList.get(0));
		} else {
			StringBuilder sb = new StringBuilder();
			for(int a = 0; a < sendToListSize; a++) {
				sb.append(fSendToList.get(a));
				if(!(a == (sendToListSize - 1))) {
					sb.append(", ");
				}
			}
		}
		
	    fSubjectTextField = new JTextField(30);	    
	    final JPanel subjectPanel = createPanel("Subject: ", fSubjectTextField);
	  	    
	    final JPanel mainPanel = new JPanel();
	    mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
	    mainPanel.add(toPanel);
	    mainPanel.add(subjectPanel);
	    
	    final JFXPanel javafxPanel = new JFXPanel();
	    javafxPanel.setPreferredSize(new Dimension(650, 400));
	      
	      Platform.runLater(new Runnable() {
	    	  @Override
	    	  public void run() {	    		  
	    		  final BorderPane borderPane = new BorderPane();
	    	      
	    		  final HBox hBox = new HBox(10);
	    		  final VBox vBox = new VBox();
	    		  final HTMLEditor htmlEditor = new HTMLEditor();
	    		  htmlEditor.setPrefHeight(450);
	    		  htmlEditor.setPrefWidth(650);
	    	      final Scene scene = new Scene(new Group());
	    	      
	    	      if( (fMode.equals("REPLY_TO_SENDER")) || (fMode.equals("REPLY_TO_ALL"))) {
	    	    	  StringBuilder replyToBuilder = new StringBuilder();
	    	    	  
	    	    	  if(fMode.equals("REPLY_TO_ALL")) {
	    	    		final List<String> recipientsList = fReplyMessage.getRecipients();
	    	    		final int listSize =  recipientsList.size();
	    	    		for(int r = 0; r < listSize; r++) {
	    	    			replyToBuilder.append(recipientsList.get(r));
	    	    			if(!(r == (listSize - 1))) {
	    	    				replyToBuilder.append(", ");
	    	    			}
	    	    		}
	    	    	  }	else {
	    	    		  replyToBuilder.append(fReplyMessage.getReplyTo());  
	    	    	  }
	    	    	  
	    	    	  fToTextField.setText(replyToBuilder.toString());
	    	      	  fSubjectTextField.setText("RE: " + fReplyMessage.getSubject());
	    	    	  
	    	    	  htmlEditor.setHtmlText("<HTML><BODY><BR><BR><BR><BR>  <P>________________________________________________________________<BR></P>" + 
	    	    	  		                         fMc.getMessageReply().getContent() + "</HTML></BODY>");  
	    	      } else if (fMode.equals("ADDRESS_LIST")) {
	    	    	  final int sendListSize = fSendToList.size();
	    	    	  final StringBuilder buildToList = new StringBuilder();
	    	    	  
	    	    	  if(sendListSize == 1) {
	    	    		  buildToList.append(fSendToList.get(0));
	    	    	  } else {
	    	    		  for(int a = 0; a < sendListSize; a++) {
	    	    			  buildToList.append(fSendToList.get(a));
	    	    			  if(!(a == (sendListSize - 1))) {
	    	    				  buildToList.append(", ");
	    	    			  }
	    	    		  }
	    	    	  }
	    	    	  fToTextField.setText(buildToList.toString());
	    	      } else if (fMode.equals("OPEN_DRAFT")) {
	    	    	  final StringBuilder buildRecipient = new StringBuilder();

	    	    	  final List<String> recipientList = fSendToList;
	    	    	  final int recipientSize = recipientList.size();
	    	    	  
	    	    	  if(recipientSize == 1) {
	    	    		  buildRecipient.append(recipientList.get(0));
	    	    	  } else {
	    	    		  for(int d = 0; d < recipientList.size(); d++) {
	    	    			  buildRecipient.append(recipientList.get(d));
	    	    			  if(!(d == (recipientSize - 1))) {
	    	    				  buildRecipient.append(", ");
	    	    			  }
	    	    		  }
	    	    	  }
	    	    	  fToTextField.setText(buildRecipient.toString());
	    	    	  fSubjectTextField.setText(fSubjectDraft);
	    	    	  htmlEditor.setHtmlText(fContentDraft);	 
	    	      }
	    	      
	    	      Button sendButton = new Button("Send");
	    	      Button saveDraftButton = new Button("Save as Draft");
	    	      sendButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
						public void handle(MouseEvent ae) {
	    	    		  final String htmlText = htmlEditor.getHtmlText();
	    	    		
	    	    		  try {
		    	    			  fHtmlMessage = htmlText;
		    	    			  final String toText = fToTextField.getText();
		    	    			  final Address[] recipients = InternetAddress.parse(toText, false); 
		    	    			  if(toText.length() != 0) {
		    	    				  fMc.sendMessage(fSubjectTextField.getText(), fHtmlMessage, recipients);
	    	    			      }
		    	    			  fComposeDialog.dispose();
						 } catch (AddressException ex) {
							 ex.printStackTrace();
						 }  
	    	    	  }
	    	      });
		    	 
	    	          saveDraftButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
		    	    	public void handle(MouseEvent me) {
		    	    		 try {    
			    	    		fMc.saveDraft(fSubjectTextField.getText(), InternetAddress.parse(fToTextField.getText(), false), 
			    	    				      htmlEditor.getHtmlText(), fEditDraftRow);
			    	    		fComposeDialog.dispose();
		    	    		 } catch (AddressException ae) {
		   		    		  ae.printStackTrace();
		   		    	  }
		    	    	 }
		    	      });		    	  
	    	          
	    	      vBox.getChildren().addAll(htmlEditor);
	    	      hBox.setAlignment(Pos.CENTER);
	    	      hBox.getChildren().addAll(sendButton);      
	    	      hBox.getChildren().addAll(saveDraftButton);
	              borderPane.setCenter(vBox);
	              borderPane.setBottom(hBox);
	    	      
	              scene.setRoot(borderPane);
	    	      javafxPanel.setScene(scene);
	    	  }
	      });
	      mainPanel.add(javafxPanel);
	      
	      
		final JPanel attachHeadingPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
	      final ImageIcon icon = new ImageIcon(getClass().getResource("/resources/paperclipreal.png"));
	      final JButton attachButton = new JButton(icon);
	      attachButton.setToolTipText("Add Attachment");
	      attachButton.addActionListener(new ActionListener() {
	    	 @Override
	    	 public void actionPerformed(ActionEvent ae) {
	    	 final JFileChooser chooserAttach = new JFileChooser();
	    	 chooserAttach.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
	    	 chooserAttach.setMultiSelectionEnabled(true);
		     chooserAttach.setCurrentDirectory(new File("C:\\"));	  
		     chooserAttach.setDialogTitle("Add Attachment");
		     final int returnVal = chooserAttach.showDialog(fComposeDialog, "Add");
		     if(returnVal == JFileChooser.APPROVE_OPTION)   {
		    	 	  final File[] attachedFiles = chooserAttach.getSelectedFiles();
		    	 	  final int attachFileLength = attachedFiles.length;
		    	 	  if(attachFileLength != 0) {
		    	 		  final int removePanelListSize = fRemovePanelList.size();
		    	 		  if(removePanelListSize == 0)  {
		    	 			  final JLabel attachMainLabel = new JLabel("Attachments:");
		    	 			  attachMainLabel.setFont(new Font("Arial", Font.BOLD, 14));
		    	 			  attachHeadingPanel.add(attachMainLabel);
		    	 			  mainPanel.add(attachHeadingPanel);
		    	 		  }
			    	 	for	(int f = 0; f < attachFileLength; f++) {
		    	 		  	final String fileName = attachedFiles[f].getName();
			    	 		final JPanel removePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
			    	 		removePanel.setPreferredSize(new Dimension(350, 25));
			    	 		final JLabel removeLabel = new JLabel(fileName);
			    	 		removeLabel.setToolTipText(attachedFiles[f].getAbsolutePath());
			    	 		removePanel.add(removeLabel);
			    	 		  final ImageIcon icon = new ImageIcon(getClass().getResource("/resources/remove.png"));
			    	 		  final JButton removeButton = new JButton(icon);
			    	 		  removeButton.setPreferredSize(new Dimension(20, 20));
					   	      removeButton.setBorder(BorderFactory.createEmptyBorder());
			    	 		  removeButton.setToolTipText("Remove Attachment");
					   	      removeButton.addActionListener(new ActionListener() {
					   	    	  @Override
					   	    	  public void actionPerformed(ActionEvent ae) {
					   	    		  if(fRemovePanelList.size() != 0) {
					   	    			  fRemovePanelList.remove(removePanel);
					   	    			  mainPanel.remove(removePanel);
					   	    			  if(removePanelListSize == 0) {
					   	    				  mainPanel.remove(attachHeadingPanel);
					   	    			  }
					   	    			  fComposeDialog.revalidate();
					   	    			  fComposeDialog.pack();
					   	    			  fComposeDialog.repaint();
					   	    		  }
					   	    	  }
					   	      });
					   	      removePanel.add(removeButton);
					   	      fRemovePanelList.add(removePanel);
					   	      mainPanel.add(removePanel);
					   	      fMc.setAttachedFiles(attachedFiles);
					   	      fComposeDialog.revalidate();
					   	      fComposeDialog.pack();
					   	      fComposeDialog.repaint();
			    	 	}    		    	  
		    	 	  }
		         }	      
		     }
	      });
	      toPanel.add(Box.createHorizontalStrut(280));
	      toPanel.add(attachButton);
	      
	    fComposeDialog.getContentPane().add(mainPanel);	    
	    
	    addEscapeListener(fComposeDialog);
        fComposeDialog.setSize(new Dimension(650, 450));
        fComposeDialog.setLocationRelativeTo(aParent);
        fComposeDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        fComposeDialog.pack();
        fComposeDialog.setVisible(true);
	}
	
	public MailCompose(JClient aMc, JFrame aParent, String aMode) {
		this(aMc, aParent, new ArrayList<String>(), "", "", -1, aMode);
	}
	
	public MailCompose(JClient aMc, JFrame aParent, String aMode, MailMessage aDraftMessage, int aEditDraftRow) {
		this(aMc, aParent, aDraftMessage.getRecipients(), aDraftMessage.getSubject(), aDraftMessage.getContent(), aEditDraftRow, aMode);		
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
		
		private final JPanel createPanel(String aLabelText, JTextField aTextField) {
			final  JLabel label = new JLabel(aLabelText);
			final  JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
			panel.add(label);
			panel.add(Box.createHorizontalStrut(2));
			panel.add(aTextField);
					
			return panel;
		}
}
