package jclientpackage;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JButton;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.SwingWorker;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.sun.glass.events.KeyEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class SearchDialog implements ActionListener {
	private final JClient fMc;
	private List<MailMessage> fSelectedList;
	private Message[] fMessageArr;
	private final JTextField fForField;
	private final JRadioButton fSubjectRadio;
	private final JRadioButton fAddressRadio;
	private final JRadioButton fMessageBodyRadio;
	private final JButton fSearchButton;
	private final JButton fCancelButton;
	private final JDialog fDialog;
	private JDialog fSplashDialog;
	private final JFrame fParent;
	private JProgressBar fProgressBar;
	private final List<String> fStopWordList;
	List<MailMessage> fMessageResultsList;
	List<Message> fMessageResultsArr;

	public SearchDialog(JFrame aParent, JClient aMc) {
		this.fMc = aMc;
		this.fParent = aParent;
		fStopWordList = aMc.getStopWordList();
		fDialog = new JDialog(aParent); 
		final String[] selectStrings = { "InBox", "Read", "OutBox", "Spam", "Draft", "Trash", "Sent"};
		final JComboBox<String> comboSearchList = new JComboBox<String>(selectStrings);
		comboSearchList.addActionListener(this);
		comboSearchList.setSelectedItem("InBox");
		final JLabel forLabel = new JLabel("For:");
		fForField = new JTextField(30);
		final JPanel forPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		forPanel.add(forLabel);
		forPanel.add(fForField);
		final JLabel inLabel = new JLabel("In: ");
		final JPanel inPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		inPanel.add(inLabel);
		inPanel.add(comboSearchList);
		final JPanel forInPanel = new JPanel();
		forInPanel.setBorder(BorderFactory.createTitledBorder(""));
		forInPanel.setLayout(new BoxLayout(forInPanel, BoxLayout.Y_AXIS));
		forInPanel.add(forPanel);
		forInPanel.add(inPanel);
		final JPanel byPanel = new JPanel();
		byPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		byPanel.setBorder(BorderFactory.createTitledBorder("By"));
		fSubjectRadio = new JRadioButton("Subject");
		fSubjectRadio.setSelected(true);
		fAddressRadio = new JRadioButton("User Name");
		fMessageBodyRadio = new JRadioButton("Message Body");
		final ButtonGroup radioGroup = new ButtonGroup();
		radioGroup.add(fSubjectRadio);
		radioGroup.add(fAddressRadio);
		radioGroup.add(fMessageBodyRadio);
		byPanel.add(fSubjectRadio);
		byPanel.add(fMessageBodyRadio);
		byPanel.add(fAddressRadio);
		fSearchButton = new JButton("Search");
		fSearchButton.addActionListener(this);
		fSearchButton.setActionCommand("SEARCH");
		fCancelButton = new JButton("Exit");
		fCancelButton.addActionListener(this);
		fCancelButton.setActionCommand("CLOSE");
		final JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		buttonPanel.add(fSearchButton);
		buttonPanel.add(fCancelButton);
		final JPanel mainPanel = new JPanel();
       mainPanel.add(forInPanel);
        mainPanel.add(byPanel);
        mainPanel.add(buttonPanel);
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.PAGE_AXIS));
        addEscapeListener(fDialog);
        fDialog.getContentPane().add(mainPanel);
        fDialog.pack();
        fDialog.setLocationRelativeTo(fParent);
        fDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        Image image = null;     
        try{
             image = ImageIO.read(getClass().getResource("/resources/mail_message_new.png"));
        }catch(IOException ioe) {
        	JOptionPane.showMessageDialog(fParent, 
                                          "Unable to Access mail_message_new.png.", 
                                          "JMail Client",
                                          JOptionPane.ERROR_MESSAGE);    	 
        }
        fDialog.setIconImage(image);
        fDialog.setTitle("JMail Client");        
        fDialog.setVisible(true);
	}
	
	public final void actionPerformed(ActionEvent ae) {
			Object source = ae.getSource();
			if(source instanceof JComboBox) {
				@SuppressWarnings("unchecked")
				JComboBox<String> cb = (JComboBox<String>)source;
				final String modeName = (String)cb.getSelectedItem();

				switch(modeName) {
					case "InBox":
						fSelectedList = fMc.getUnseenList();
						fMessageArr = fMc.getUnseenMessageArr();
						break;
					case "Read":
						fSelectedList = fMc.getSeenList();
						fMessageArr = fMc.getSeenMessageArr();
						break;
					case "OutBox":
						fSelectedList = fMc.getOutBoxList();
						fMessageArr = fMc.getOutBoxMessageArr();
						break;
					case "Spam":
						fSelectedList = fMc.getSpamList();
						fMessageArr = fMc.getSpamMessages();
						break;
					case "Draft":
						fSelectedList = fMc.getDraftList();
						fMessageArr = fMc.getDraftMessageArr();
						break;
					case "Trash":
						fSelectedList = fMc.getTrashList();
						fMessageArr = fMc.getTrashMessages();
						break;
					case "Sent":
						fSelectedList = fMc.getSentList();
						fMessageArr = fMc.getSentMessageArr();
						break;
					default: 
						break;
			  }
		  } else if (source instanceof JButton)	{
			  final String command = ae.getActionCommand();
			  if(command.equals("SEARCH")) {
				  fSplashDialog = new JDialog(fParent); 
				  fSplashDialog.setLayout(new BorderLayout());
					 final JLabel titleWaiting = new JLabel("Searching Messages, Please Wait....");
					 fSplashDialog.add(titleWaiting, BorderLayout.CENTER);
					 fProgressBar = new JProgressBar(0, 100);
					 fProgressBar.setValue(0);
					 fSplashDialog.add(fProgressBar, BorderLayout.SOUTH);
					 fSplashDialog.setModal(false);
					 fSplashDialog.setUndecorated(true);
					 fSplashDialog.pack();
					 fSplashDialog.setLocationRelativeTo(fParent);
					 new MoveDialogFrameTogether(fParent, fSplashDialog);
					 fSplashDialog.setVisible(true);
					 fMessageResultsList = new ArrayList<MailMessage>();
					 fMessageResultsArr = new ArrayList<Message>();
  		    	SwingWorker<Void, Void> workerSearch = new SwingWorker<Void, Void>() {
                  protected Void doInBackground () {
					  
					  String messageText;				  
					  if(fSubjectRadio.isSelected()) {
						  for(int index = 0; index < fSelectedList.size(); index++) {
						  boolean foundSubjectMatch = false;  
						  final Message messageSearch = fMessageArr[index];
							      final MailMessage mailSubjectSearch = fSelectedList.get(index);
								  final String messageSubject = mailSubjectSearch.getSubject().trim();
								  foundSubjectMatch = searchTextFound(messageSubject);
								  if(foundSubjectMatch) {
									  fMessageResultsList.add(mailSubjectSearch);
									  fMessageResultsArr.add(messageSearch);
								  }						  
						  } 
					  } else if(fMessageBodyRadio.isSelected()) {
						  for(int indexOut = 0; indexOut < fSelectedList.size(); indexOut++) {
							  final MailMessage mailMessageSearch = fSelectedList.get(indexOut);
							  final Message messageSearch = fMessageArr[indexOut];
							  boolean foundTextMatch = false;
							  try{
								  final String contentType = messageSearch.getContentType().toLowerCase();
								  if(contentType.contains("text/html")) {
									  String messageHtml = mailMessageSearch.getContent();
									  final Document doc = Jsoup.parseBodyFragment(messageHtml);
									  final Element body = doc.body();
									  final String htmlBodyText = body.text();
									  foundTextMatch = searchTextFound(htmlBodyText);
								  } else if(contentType.contains("text/plain")) {
									  messageText = mailMessageSearch.getContent().toString().trim();
									  foundTextMatch = searchTextFound(messageText);
								  }						  
							  } catch(MessagingException me) {
								  me.printStackTrace();
							  }
							  if(foundTextMatch) {
								  fMessageResultsList.add(mailMessageSearch);
								  fMessageResultsArr.add(messageSearch);						  
							  }
						  }
						}else if(fAddressRadio.isSelected()) {
							  for(int index = 0; index < fSelectedList.size(); index++) {
								  boolean foundAddressMatch = false;
								  final Message messageSearch = fMessageArr[index];
									      final MailMessage mailMessageSearch = fSelectedList.get(index);
											  String messageAddressUnParse = mailMessageSearch.getFrom().trim();
											  final int lessIndex = messageAddressUnParse.indexOf('<');
											  final int atIndex = messageAddressUnParse.indexOf('@');
											  final String messageAddressParsed = messageAddressUnParse.substring(lessIndex + 1, atIndex);
											  foundAddressMatch = searchTextFound(messageAddressParsed);
											  if(foundAddressMatch) {
											       fMessageResultsList.add(mailMessageSearch);
											       fMessageResultsArr.add(messageSearch);
											  }						  
								  } 
				       }
					  
					  return null;
                  }
                  protected void done() {
                	  if(fMessageResultsList.size() > 0) {
						  final Message[] messageArrConv = fMessageResultsArr.toArray(new Message[0]);
						  fMc.setSearchList(fMessageResultsList);
						  fMc.setSearchArr(messageArrConv);
						  final JTree tree = fMc.getTree();
						  tree.setSelectionRow(8);
						  fDialog.dispose();
					  } else {
						  final int reply = JOptionPane.showConfirmDialog(fDialog, 
								                                          "No Matches, Search Again?", 
								                                          "Search Results",
								                                          JOptionPane.YES_NO_OPTION);
					       if(reply == JOptionPane.NO_OPTION) {
					    	   fDialog.dispose();
					       }
					  }
                	  fSplashDialog.dispose();
                  }
	  		    	};
		  		    	workerSearch.addPropertyChangeListener(new PropertyChangeListener() {
	        				@Override
	        				public void propertyChange(PropertyChangeEvent pce) {
	        					if("progress".equals(pce.getPropertyName())) {
	        						fProgressBar.setIndeterminate(false);
	        						fProgressBar.setValue((Integer) pce.getNewValue());
	        					}
	        			}
        		    });
	  		    	workerSearch.execute();
	  		    	
			  } else if(command.equals("CLOSE")) {
				 fDialog.dispose();
			 }			  
		  }
   }
	
	private final boolean searchTextFound(String aText) {
		  String searchTextArr[] = aText.split("\\s+");
		  final List<String> messageTextList = Arrays.asList(searchTextArr);
			  final String searchTerm = fForField.getText().trim();
			  final List<String> searchTermsList = editSearchText(searchTerm);
			  for(int indexIn = 0; indexIn < searchTermsList.size(); indexIn++) {
				  final String term = searchTermsList.get(indexIn);
			       for(int indexMess = 0; indexMess < messageTextList.size(); indexMess++) {
				        final String messageWord = messageTextList.get(indexMess);
					  if(term.equalsIgnoreCase(messageWord)) {
						  return true;
					  }
			       }
		      }
			  return false;
	}
	
	private final List<String> editSearchText(String aSearchText) {
		String searchTextArr[] = aSearchText.split("\\s+");
		List<String> searchTextList = Arrays.asList(searchTextArr);
		for(int indexOut = searchTextList.size() - 1; indexOut >= 0 ; indexOut--){	
		    final String searchWord = searchTextList.get(indexOut); 
			for(int indexIn = 0; indexIn < fStopWordList.size(); indexIn++) {
				final String stopWord = fStopWordList.get(indexIn);
				if(searchWord.equalsIgnoreCase(stopWord)){
					searchTextList.remove(indexOut);
				}
			}
		}
		return searchTextList;
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
}
	