package jclientpackage;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.internet.MimeBodyPart;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JFrame;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.KeyStroke;

import com.sun.glass.events.KeyEvent;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.layout.BorderPane;
import javafx.scene.web.WebView;
import javafx.scene.Scene;

public final class MailDisplay     
                            implements ActionListener{
	final private JDialog fDisplayMailDialog;	
	private String fSubject;
	private String fDate;
	private String fMessage;
	private String fSender;
	private final List<JPanel> attachPanelList;
	private final JButton replyButton;
	private final JButton replyAllButton;
	private final JButton forwardButton;
	private final JFXPanel javafxPanel;
	private final JClient fMc;
	private final Message fMessageImap;
	Message[] messageArr;
	 private WebView webComponent;
	
	public MailDisplay(JFrame aParent, MailMessage aMessage, Message aMessageImap, JClient aMc)    {
	      fDisplayMailDialog = new JDialog(aParent, "JMail Client", true);
		  this.fSubject = aMessage.getSubject();
		  this.fDate = aMessage.getDate();
		  this.fSender = aMessage.getFrom();
		  this.fMessage = aMessage.getContent();
		  this.fMessageImap = aMessageImap;
		  this.fMc = aMc;
	
		  final Font titleFont = new Font("Arial", Font.BOLD, 12);
		  
		  final JPanel subjectPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
	      final JLabel subjectLabel = new JLabel("Subject: ");
	      subjectLabel.setFont(titleFont);
	      final JLabel subjectInfo = new JLabel(fSubject);
	      
	      replyButton = createButton("Reply", "REPLY_TO_SENDER"); 
	      replyAllButton = createButton("Reply All", "REPLY_TO_ALL");
	      forwardButton = createButton("Forward", "FORWARD");
	      
	      final JPanel senderPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
	      final JLabel senderLabel = new JLabel("Sender: ");
	      senderLabel.setFont(titleFont);
	      final JLabel senderInfo = new JLabel(fSender);
	      senderInfo.setOpaque(true);

	      final JPanel datePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
	      final JLabel dateLabel = new JLabel("Date: ");
	      dateLabel.setFont(titleFont);
	      final JLabel dateInfo = new JLabel(fDate);
	      
	      subjectPanel.add(subjectLabel);
	      subjectPanel.add(subjectInfo);
	      subjectPanel.add(Box.createHorizontalStrut(310));
	      subjectPanel.add(replyButton);
	      subjectPanel.add(replyAllButton);
	      subjectPanel.add(forwardButton);
	      datePanel.add(dateLabel);
	      datePanel.add(dateInfo);
	      senderPanel.add(senderLabel);
	      senderPanel.add(senderInfo);
	      
	      javafxPanel = new JFXPanel();
	      Platform.runLater(new Runnable() {
	    	  @Override
	    	  public void run() {	    		  
	    		  BorderPane  borderPane = new BorderPane();
	    	      webComponent = new WebView();	    	      
	    	      webComponent.getEngine().loadContent(fMessage);	    	      
	    	      borderPane.setCenter(webComponent);
	    	      Scene scene = new Scene(borderPane, 730, 500);
	    	      javafxPanel.setScene(scene);
	    	  }
	      });
	      
	      final List<Integer> bodyPartIndices = aMessage.getAttachedFileList();
	      final int bodyPartIndicesSize = bodyPartIndices.size();
	      attachPanelList = new ArrayList<JPanel>();
	      final JPanel attachPanel = new JPanel();
	      
	      if(bodyPartIndicesSize != 0) {
	    	  attachPanel.setLayout(new BoxLayout(attachPanel, BoxLayout.Y_AXIS));
	    	  final JLabel attachTitle = new JLabel("Attachments: ");
	    	  final ImageIcon icon = new ImageIcon(getClass().getResource("/resources/downloadAll.png"));
	    	  final JButton buttonDownloadAll = new JButton(icon);
	    	  buttonDownloadAll.addActionListener(new DownloadAllAttachmentListener(fDisplayMailDialog, fMessageImap, bodyPartIndices));
	    	  buttonDownloadAll.setBorder(BorderFactory.createEmptyBorder());
	    	  buttonDownloadAll.setToolTipText("Download All Attachments");
	    	  buttonDownloadAll.setPreferredSize(new Dimension(20, 20));
	    	  buttonDownloadAll.setMaximumSize(buttonDownloadAll.getPreferredSize());
	    	  buttonDownloadAll.setMinimumSize(buttonDownloadAll.getPreferredSize());
	    	  final JPanel attachTitlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
	    	  attachTitlePanel.add(attachTitle);
	    	  attachTitlePanel.add(buttonDownloadAll);
	    	  attachPanel.add(attachTitlePanel);
	    	  for(int p = 0; p < bodyPartIndicesSize; p++) {
	    		  final JPanel createdPanel = createAttachPanel(bodyPartIndices.get(p), p);
	    		  attachPanelList.add(createdPanel);
	    		  attachPanel.add(createdPanel);
	    		  attachPanel.add(Box.createVerticalStrut(4));
	    	  }
	      }
	      
          final JPanel mainPanel = new JPanel();
          mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
          mainPanel.add(subjectPanel);
          mainPanel.add(senderPanel);
          mainPanel.add(datePanel);
          mainPanel.add(javafxPanel);
          mainPanel.add(attachPanel);
                    
          addEscapeListener(fDisplayMailDialog, fMc);
          fDisplayMailDialog.getContentPane().add(mainPanel);
          fDisplayMailDialog.setSize(new Dimension(730, 600));
          fDisplayMailDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
          fDisplayMailDialog.setLocationRelativeTo(aParent);
          fDisplayMailDialog.setVisible(true);
    }    
	
		private JPanel createAttachPanel(int aBodyPartIndex, final int aPanelIndex) {
			final JPanel attachFilePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
			try {	
					 final Multipart multiPart = (Multipart) fMessageImap.getContent();		    	 
		    		 final MimeBodyPart bodyPart = (MimeBodyPart)multiPart.getBodyPart(aBodyPartIndex);
		    		 
			    		 final String fileName = bodyPart.getFileName();
			    		 final JLabel labelAttach = new JLabel(fileName);
			    		 final ImageIcon icon = new ImageIcon(getClass().getResource("/resources/download.png"));
			    		 JButton buttonDownload = new JButton(icon);
			    		 buttonDownload.setToolTipText("Download Attachment");
			    		 buttonDownload.setBorder(BorderFactory.createEmptyBorder());
			    		 buttonDownload.addActionListener(new DownloadAttachmentIndivListener(fDisplayMailDialog, bodyPart, fileName));
			    		 buttonDownload.setPreferredSize(new Dimension(20, 20));
			    		 buttonDownload.setMaximumSize(buttonDownload.getPreferredSize());
			    		 buttonDownload.setMinimumSize(buttonDownload.getPreferredSize());
			    		
			    		 attachFilePanel.add(labelAttach);
			    		 attachFilePanel.add(buttonDownload);
		    		 
			} catch(MessagingException me) {
				me.printStackTrace();
			} catch(IOException ioe) {
				ioe.printStackTrace();
			}
			return attachFilePanel;
		}
	
		public final void actionPerformed(ActionEvent ae) {
			final String command = ae.getActionCommand();
			if(command.equals("REPLY_TO_SENDER")) {
				fMc.reply("REPLY_TO_SENDER");
			} else if(command.equals("REPLY_TO_ALL")) {
				fMc.reply("REPLY_TO_ALL");
			} else if(command.equals("FORWARD")) {
				fMc.forward();
			}
		}
		
		private static void addEscapeListener(JDialog aDialog, JClient aMc) {
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
		
		private final JButton createButton(String aTitle, String aActionCommand) {
			JButton b = new JButton(aTitle);
			b.addActionListener(this);
			b.setActionCommand(aActionCommand);
			
			return b;
		}
}