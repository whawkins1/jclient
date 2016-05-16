package jclientpackage;

import java.awt.Toolkit;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Image;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.InputMap;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JRootPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTable;
import javax.swing.JScrollPane;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import javax.swing.JButton;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.table.TableColumn;
import javax.swing.JOptionPane;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;
import javax.swing.JTree;
import javax.swing.border.Border;
import javax.swing.event.TreeSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.ImageIcon;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.prefs.Preferences;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.mail.Message;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;
import javax.mail.Address;
import javax.mail.Folder;
import javax.mail.MessagingException;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Multipart;
import javax.mail.BodyPart;
import javax.mail.Flags;
import javax.mail.search.FlagTerm;
import javax.mail.internet.MimeMessage;




//import com.sun.glass.events.KeyEvent;
import com.sun.mail.imap.protocol.FLAGS;
import com.sun.mail.smtp.SMTPTransport;
import com.sun.scenario.Settings;

public final class JClient 
                     implements ActionListener, TreeSelectionListener, ListSelectionListener  {   
	private final JFrame frame;
    private final JTable mailTable;
    private final MailTableModel mtm;
    private JButton writeButton;
    private JButton refreshButton;
    private JButton deleteButton;
    @SuppressWarnings("unused")
	private JButton exitButton;
   private JButton signinButton;
    private JButton addressBookButton;
    @SuppressWarnings("unused")
	private JButton infoButton;
    private JButton replyButton;
    private JButton replyAllButton;
    @SuppressWarnings("unused")
	private JButton forwardButton;
    private JButton fSearchButton;
	@SuppressWarnings("unused")
	private JButton fServerButton;
    private JDialog splashDialog;
    private JProgressBar progressBar;
    private JToolBar fToolBar;
    @SuppressWarnings("unused")
	private String reply;
    @SuppressWarnings("unused")
	private String from;
    private String fUserPassEntered;
    private String fUserNameEntered;
    private String fUserPop3ServerEntered;
    private String fUserSmtpServerEntered;
    private TableMouseListener fTml;
    @SuppressWarnings("unused")
	private Boolean loggedIn;
    private Boolean allChecked;
    private Boolean fPressingCTRL;
    private final CheckBoxListener fCbl;
    private final CheckBoxHeader cbh;
    private final List<ClickableMailTableHeader> fCmth = new ArrayList<ClickableMailTableHeader>();
    List<String> fStopWordList;
    private JTree tree;
    private JScrollPane fTreePane;
    private Store store;
	private Folder inBoxFolder;
	private Folder outBoxFolder;
	private Folder sentFolder;
	private Folder draftFolder;
	private Folder spamFolder;
	private Folder trashFolder;
	private boolean fSecuredConnection;
	private DefaultTreeModel fTreeModel;
	private final DefaultMutableTreeNode fTop;
	private Message[] unseenMessages;
	private Message[] seenMessages;
	private Message[] outBoxMessages;
	private Message[] sentMessages;
	private Message[] draftMessages;
	private Message[] spamMessages;
	private Message[] trashMessages;
	private Message[] fSearchMessages;
	private Message[] fCurrentList;
	private File[] fAttachedFiles;
	private String fReplySubject;
	private String fReplyTo;
	private String fNodePath;
	private MailMessage fReplyMessage;
	private Boolean fSuccessCreateMessages;
	private Session imapSession;
	private ButtonCellRenderer fBcr;
	private ButtonCellEditor fBce;
	private List<MailMessage> inBoxSeenList;
    private List<MailMessage> inBoxUnseenList;
    private List<MailMessage> outBoxList;
    private List<MailMessage> spamList;
    private List<MailMessage> fDraftList;
    private List<MailMessage> trashList;
    private List<MailMessage> sentList;
    private List<MailMessage> fSearchList;
    
    public JClient()    {
    	inBoxSeenList = new ArrayList<MailMessage>();
        inBoxUnseenList = new ArrayList<MailMessage>();
        outBoxList = new ArrayList<MailMessage>();
        spamList = new ArrayList<MailMessage>();
        fDraftList = new ArrayList<MailMessage>();
        trashList = new ArrayList<MailMessage>();
        sentList = new ArrayList<MailMessage>();
		fSearchList = new ArrayList<MailMessage>();

        
        mtm = new MailTableModel(this);
        mailTable = new JTable(mtm)  {
            private static final long serialVersionUID = 1003592960630989487L;

            @Override
	        public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
	           Component comp = super.prepareRenderer(renderer, row, column);
	              if(column != 2) {
		        	  final List<MailMessage> currentList = mtm.getMailList();
		               if(currentList.size() > 0){
			        	  final MailMessage message = currentList.get(row);
			        	  	if (message.checkRead()) {
			        	  		comp.setFont(new Font("Arial", Font.PLAIN, 12));
			        	  	} else {
			            		comp.setFont(new Font("Arial", Font.BOLD, 12));
			        	  	}		           
			          }
		          }    
		          return comp;
		          }            
        };  
        
        
        mailTable.setFocusable(false);  
        mailTable.setShowGrid(false);
        final JScrollPane mailScroller = new JScrollPane();
        mailScroller.getViewport().add(mailTable);
        final JPanel tablePanel = new JPanel();
        tablePanel.add(mailScroller);
        final TableColumnModel tcm = mailTable.getColumnModel();
        tcm.getColumn(0).setMinWidth(20);
        tcm.getColumn(0).setMaxWidth(20);
        tcm.getColumn(0).setPreferredWidth(20);
        tcm.getColumn(2).setMinWidth(20);
        tcm.getColumn(2).setMaxWidth(20);
        tcm.getColumn(2).setPreferredWidth(20);
        tcm.getColumn(2).setCellRenderer(new AttachIconRenderer());
        tcm.getColumn(3).setMinWidth(220);
        tcm.getColumn(3).setMaxWidth(220);
        tcm.getColumn(3).setPreferredWidth(140);
        tcm.getColumn(4).setMinWidth(140);
        tcm.getColumn(4).setMaxWidth(140);
        tcm.getColumn(4).setPreferredWidth(180);
        final TableColumn tc = tcm.getColumn(0);
        tc.setCellRenderer(mailTable.getDefaultRenderer(Boolean.class));
        fCbl = new CheckBoxListener(mtm, this, inBoxUnseenList);
        cbh = new CheckBoxHeader(fCbl, mtm);
        allChecked = false;
        tc.setHeaderRenderer(cbh);
        mailTable.getSelectionModel().addListSelectionListener(this);
        mailTable.setEnabled(false);
        
        final JPanel mailInfoPanel = new JPanel();
        mailInfoPanel.setLayout(new BoxLayout(mailInfoPanel, BoxLayout.Y_AXIS));
        
        fTop = new DefaultMutableTreeNode("Local Folders");
        makeNodes(fTop);
        tree = new JTree(fTop);
        tree.addTreeSelectionListener(this);
        fTreePane = new JScrollPane();
        tree.setEnabled(false);
        mailInfoPanel.add(fTreePane);
        
        final  JPopupMenu popup = new JPopupMenu();
        final JMenuItem menuHtml = new JMenuItem("HTML");
        menuHtml.addActionListener(this);
        menuHtml.setActionCommand("HTML");
        popup.add(menuHtml);
        
        fToolBar = new JToolBar();
        fToolBar.setRollover(true);
        addButtons();
        
        final JPanel mailControlPanel = new JPanel();
        mailControlPanel.setLayout(new BorderLayout());
        mailControlPanel.setPreferredSize(new Dimension(200, 48));
        mailControlPanel.setMaximumSize(new Dimension(200, 48));
        mailControlPanel.setMinimumSize(new Dimension(200, 48));
        mailControlPanel.add(fToolBar);
        
        final JSplitPane mailSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        mailSplitPane.setLeftComponent(mailInfoPanel);
        mailSplitPane.setRightComponent(mailScroller);
        mailSplitPane.setDividerLocation(110);
        
        frame = new JFrame();        
        frame.setLayout(new BorderLayout());
        fTml = new TableMouseListener(frame, mtm, mailTable, this, "", cbh);
        mailTable.addMouseListener(fTml);
        fPressingCTRL = false;
        Container cp = frame.getContentPane();
        cp.add(mailControlPanel, BorderLayout.PAGE_START);
        cp.add(mailSplitPane, BorderLayout.CENTER); 
        final Toolkit tk = Toolkit.getDefaultToolkit();
        final Dimension d = tk.getScreenSize();
        addEscapeListener(frame);
        frame.setSize(d.width/2, d.height/2);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        Image image = null;
        try {
        	image = ImageIO.read(getClass().getResource("/resources/mail_message_new.png"));
        } catch (IOException ioe) {
             createErrorMessage("The File mail_message_new.png Could Not be Accessed"); 
        }
		frame.setIconImage(image);
		frame.setTitle("JMail Client");
	            frame.setVisible(true);
             // Import Stop Words      
    	        fStopWordList = new ArrayList<String>();
    	        try {
    	        	final String path = getClass().getResource("/resources/stopwords.txt").toURI().getPath();
    	        	File stopWordFile = new File(path);
    				final FileReader fr = new FileReader(stopWordFile);
    				@SuppressWarnings("resource")
					final BufferedReader br = new BufferedReader(fr);
    				String stopWord = "";
    				while((stopWord = br.readLine()) != null) {
	    				stopWord = br.readLine();
	    				fStopWordList.add(stopWord);			
    				}
    			} catch (FileNotFoundException fne) {
    				fne.printStackTrace();
    			} catch (IOException ioe) {
    				ioe.printStackTrace();
    			} catch(URISyntaxException use) {
    				use.printStackTrace();
    			}
    }
    	    			     
			 private final void showSplashScreen(String aLabelText) {
				 splashDialog = new JDialog(frame); 
				 splashDialog.setLayout(new BorderLayout());
				 final JLabel titleWaiting = new JLabel(aLabelText);
				 splashDialog.add(titleWaiting, BorderLayout.CENTER);
				 progressBar = new JProgressBar(0, 100);
				 progressBar.setValue(0);
				 splashDialog.add(progressBar, BorderLayout.SOUTH);
				 splashDialog.setModal(false);
				 splashDialog.setUndecorated(true);
				 splashDialog.pack();
				 splashDialog.setLocationRelativeTo(frame);
				 new MoveDialogFrameTogether(frame, splashDialog);
				 splashDialog.setVisible(true);
			 }
		     
		     public void actionPerformed(ActionEvent e)     {
		        if(e.getActionCommand().equals("SIGN_IN"))        {		        	
		        	final MailLoginDialog mld = new MailLoginDialog(frame, this);
		        	final boolean loginSuccess = mld.showDialog();
		        	final JClient mc = this;
		        	fSuccessCreateMessages = false;
		        	
		        	if(loginSuccess) {
		        		showSplashScreen("Please Wait, Loading Messages....");
		        		SwingWorker<Void, Integer> workerLogin = new SwingWorker<Void, Integer>() {
							@Override
		        			protected Void doInBackground()  {
								 try {
									 setProgress(10);
									 inBoxFolder = getFolder("Inbox");
						    		 final Flags seen = new Flags(Flags.Flag.SEEN);
						    		 final FlagTerm flagTrue = new FlagTerm(seen, true);
						    		 final FlagTerm flagFalse = new FlagTerm(seen, false);
						    		 seenMessages = inBoxFolder.search(flagTrue);
						    		 inBoxSeenList = createMessages(seenMessages, true);
						    		 unseenMessages = inBoxFolder.search(flagFalse);
									 inBoxUnseenList = createMessages(unseenMessages, false );
						    		 setProgress(20);
						    		 outBoxFolder = getFolder("OUTBOX");
									 outBoxMessages = outBoxFolder.getMessages();
									 outBoxList = createMessages(outBoxMessages, true);
									 setProgress(40);
									 sentFolder = getFolder("Sent");
									 sentMessages = sentFolder.getMessages();
									 sentList = createMessages(sentMessages, true);
									 setProgress(60);
									 draftFolder = getFolder("Drafts");
									 draftMessages = draftFolder.getMessages();
									 fDraftList = createMessages(draftMessages, true);
									 setProgress(70);
									 spamFolder = getFolder("Spam");
									 spamMessages = spamFolder.getMessages();
									 setProgress(80);
									 trashFolder = getFolder("Trash");
									 trashMessages = trashFolder.getMessages();
									 trashList = createMessages(trashMessages, true);
									 setProgress(90);
									 fCurrentList = unseenMessages;
						    	     setProgress(100);
						    	     Thread.sleep(1000);
						    	} catch (MessagingException me) {
						    		createErrorMessage("Error Retrieving Folder Contents"); 
						    	} catch (InterruptedException ie) {
						    		createErrorMessage("Login Thread was Interrupted, Please try to Login Again.");
						    	} 
						    	return null;
							}		
							
							@Override
							protected void done() {
								splashDialog.dispose();
								if(fSuccessCreateMessages) {
					        		enableComponents(true);
					        		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
					        	}
								fTreeModel = new DefaultTreeModel(fTop);
					             tree.setModel(fTreeModel);
					             final JTableHeader th = mailTable.getTableHeader();
					             for(int c = 1; c < mailTable.getColumnCount(); c++) {
					     			final TableColumn tc = mailTable.getColumnModel().getColumn(c);
					     			
					     			final ClickableMailTableHeader cmth = new ClickableMailTableHeader(tc, th, mtm, inBoxUnseenList);
					     			fCmth.add(cmth);	
					     		}		        		
					            final TableColumnModel tcm =  th.getColumnModel();
					     		final TableColumn tcIcon = tcm.getColumn(2);
					            tcIcon.setHeaderRenderer(new IconRenderer());
					            fBcr = new ButtonCellRenderer(frame, mc); 
							    tree.setCellRenderer(fBcr);
					            fBce = new ButtonCellEditor(frame, mc);
						        tree.setCellEditor(fBce);
							    mtm.setMailList(inBoxUnseenList);
							    tree.setSelectionRow(1);
							    updateFolderTreeExt();
							    final ImageIcon icon = new ImageIcon(getClass().getResource("/resources/logout.png"));
							    signinButton.setIcon(icon);
							    signinButton.setText("Sign Out");
							    signinButton.setActionCommand("SIGN_OUT");
							}
		        		};
		        		
		        		workerLogin.addPropertyChangeListener(new PropertyChangeListener() {
		        				@Override
		        				public void propertyChange(PropertyChangeEvent pce) {
		        					if("progress".equals(pce.getPropertyName())) {
		        						progressBar.setIndeterminate(false);
		        						progressBar.setValue((Integer) pce.getNewValue());
		        					}
		        			}
		        		});
		        		workerLogin.execute();
		        	} 			        
		        } else if(e.getActionCommand().equals("WRITE"))   {	
		        		new MailCompose(this, frame, "HTML");  
		        } else if(e.getActionCommand().equals("REPLY"))   {
		        	    reply("REPLY");
		        } else if(e.getActionCommand().equals("REPLY_ALL")) {	
		        	    reply("REPLY_ALL");
		        } else if(e.getActionCommand().equals("FORWARD")) {
		        		forward();
		        } else if(e.getActionCommand().equals("DELETE") )  {
			        	    delete();
		        } else if(e.getActionCommand().equals("SEARCH_MAIL")) {
		        	       new SearchDialog(frame, this);
		        }  else if(e.getActionCommand().equals("REFRESH_MAIL") )  {		        	    
		        }  else if(e.getActionCommand().equals("ADDRESS_BOOK") )  {			
		        	  new AddressBookDialog(frame, fUserSmtpServerEntered, fUserNameEntered, fUserPassEntered, sentList, this);	
		        } else if(e.getActionCommand().equals("SERVER_SETTINGS")) {
		        	new ServerSettingsDialog(frame, this);
		        } else if(e.getActionCommand().equals("INFO")) {
		        		new MailInfoDialog(frame);
		        } else if(e.getActionCommand().equals("SIGN_OUT")) {
		        	enableComponents(false);
		        	signinButton.setText("Sign In");
		        	final ImageIcon icon = new ImageIcon(getClass().getResource("/resources/login.png"));
		        	signinButton.setIcon(icon);
		        	signinButton.setText("Sign In");
		        	signinButton.setActionCommand("SIGN_IN");
		        	prepareCloseConnection();
		        } else if(e.getActionCommand().equals("EXIT")) {
		        	exit();
		        }		        	
		}
		     private void enableComponents(boolean aEnable) {
			     mailTable.setEnabled(aEnable);		
	             tree.setEditable(aEnable);
	             if(!aEnable) {
	            	 tree.setModel(null);
		             fTreePane.setViewport(null);
	             } else {
	            	 fTreePane.setViewportView(tree);	 
	             }
	             addressBookButton.setEnabled(aEnable);
	     		 writeButton.setEnabled(aEnable);
	     	     addressBookButton.setEnabled(aEnable);
	     	     refreshButton.setEnabled(aEnable);
	     	     fSearchButton.setEnabled(aEnable);
	     	     tree.setEnabled(aEnable);
				 final String frameTitle = fSecuredConnection ? "JMail Client <SECURED>" : "JMail Client";
				 frame.setTitle(frameTitle);
				 frame.repaint();
		     }
		     
		     public final void updateFolderTreeExt() {
		    	 final int[] treeRows = tree.getSelectionRows();
		    	 DefaultMutableTreeNode node = null;
		    	 int rowSelected = -1;
		    	 if(treeRows.length > 0) {
			    	  rowSelected = treeRows[0];
			    	  if(rowSelected == 3) {
			    		  rowSelected = 1;
			    	  }
			    	  node = (DefaultMutableTreeNode)fTreeModel.getChild(fTop, rowSelected - 1);
		    	 }
			    	  int folderSize = 0;
			    	  String nodeNameExt = "";
			    	      if(rowSelected == 1) {
			    	    	  final int inBoxListSize = inBoxUnseenList.size();
				    		  for(int u = 0; u < inBoxListSize; u++) {
				    			  MailMessage message = inBoxUnseenList.get(u);
				    		      if(!message.checkRead()) {
				    		    	  folderSize++;
				    		      }
				    		  }
				    		  if(folderSize > 0) {
				    			  nodeNameExt = "InBox " + "(" + folderSize + ")";
				    		  } else {
				    			  nodeNameExt = "InBox";
				    		  }
				    	  } else if(rowSelected == 6) {
				    		  final int spamListSize = spamList.size(); 
				    		  folderSize = spamListSize;
				    		   if(folderSize > 0) {
				    			   nodeNameExt = "Spam " + "(" + folderSize + ")";
				    		   } else {
				    			   nodeNameExt = "Spam";
				    		   }
				    	  } 
				    	  if(nodeNameExt != "" && node != null) { 
			 				  fBcr.setLabel(rowSelected, nodeNameExt);
				    		  fTreeModel.nodeChanged(node);
			 			  }
		    }
		     private void setRead(List<MailMessage> aMessageList, Message[] aMessageArr) {
		    	 try{
		    		for(int index = 0; index < aMessageList.size(); index++ ) {
		    			final MailMessage message = aMessageList.get(index);
		    			if(message.checkRead() == false) {
		    			   aMessageArr[index].setFlag(Flags.Flag.SEEN, false);
		    			}
		    		}
		    		} catch(MessagingException me) {
		    			createErrorMessage("Error Setting Flag!");
		    		}
		    	}
		     
		     private final void prepareCloseConnection() {
		    	 if(inBoxFolder != null || outBoxFolder != null ||
		    		    	sentFolder != null || trashFolder != null  ||
		    		    	spamFolder != null )   {
		    		    	setRead(inBoxUnseenList, unseenMessages);
		    		    	setRead(spamList, spamMessages);
		    		    	setRead(trashList, trashMessages);
		    		    	SwingWorker<Void, Void> workerClose = new SwingWorker<Void, Void>() {
								protected Void doInBackground() throws Exception {
									inBoxFolder.close(true);
					        		outBoxFolder.close(true);
					        		sentFolder.close(true);
					        		draftFolder.close(true);
					        		trashFolder.close(true);
					        		spamFolder.close(true);			
					        		store.close();
					        		
					        		return null;
								}
							};
							workerClose.execute();
		    		    } 
		     }
		     
		     private final void exit() {
		    	 	 	frame.setVisible(false);
		    		    prepareCloseConnection();
		    		    frame.dispose();
		    		    System.exit(0);
		       	}
		     
		   @Override
			public final void valueChanged(ListSelectionEvent lse) {
				if(!(lse.getValueIsAdjusting())) {
					final boolean rowsDeleteAreSelected = (mailTable.getSelectedRowCount() > 0);
					deleteButton.setEnabled(rowsDeleteAreSelected);
					
					final boolean rowsOneSelected = (mailTable.getSelectedRowCount() == 1);
					replyButton.setEnabled(rowsOneSelected);
	                replyAllButton.setEnabled(rowsOneSelected);
			}
		}     
		     
		 private final List<MailMessage> createMessages(Message aMessages[], boolean aIsRead) {
				final List<MailMessage> messageListCreate = new ArrayList<MailMessage>();
				try {
				 for(int m = 0; m < aMessages.length; m++)  {
				     final Message messageServer = aMessages[m];
					 final Calendar todayCal = Calendar.getInstance();
					 String messageDate = messageServer.getSentDate().toString();
				     final int messageLastIndex = messageDate.length();
				     final String messageYear = messageDate.substring(messageLastIndex - 4, messageLastIndex).trim();
				     final String messageParse = messageDate.substring(0, 10).trim();
					 
				    if((todayCal.get(Calendar.YEAR)) == (Integer.parseInt(messageYear))){
			    			 messageDate = messageParse;
			    	} else {
			    			 messageDate = messageParse + messageYear;			    			 
			    	}	 
				    
				    final Object msgContent = messageServer.getContent();
				    String content = "";
				    final MailMessage message = new MailMessage(false);
				    
				    if(msgContent instanceof Multipart) {
				    	final Multipart multipart = (Multipart) msgContent;
				    	
				    	for(int j = 0; j <  multipart.getCount(); j++) {
				    		final BodyPart bodyPart = multipart.getBodyPart(j);
				    		final String disposition = bodyPart.getDisposition();
                                if(disposition != null && (disposition.equalsIgnoreCase("ATTACHMENT"))) {
				    	        	message.setAttachedBodyPartIndex(j);
				    	        } else {
				    	            content = getText(bodyPart);	
				    	        }		  
				    	}
				    } else {
				    	content = messageServer.getContent().toString();
				    }
				    
				    message.setRead(aIsRead);
							                                                                       
					 message.setFrom(InternetAddress.toString(aMessages[m].getFrom()));
					 message.setSubject(aMessages[m].getSubject());
					 message.setDate(messageDate);
					 message.setMessageContent(content);
					 message.setReplyTo(InternetAddress.toString(aMessages[m].getReplyTo()));
					 Address[] replyToAddress = aMessages[m].getRecipients(Message.RecipientType.TO);
					 List<String> recipientsList = new ArrayList<String>();
			    	   for(int r = 0; r < replyToAddress.length; r++) {
			    		   recipientsList.add(replyToAddress[r].toString());
			    	   }
			    	 message.setRecipients(recipientsList);  
					 message.setRead(aIsRead);
					 messageListCreate.add(message);
			    }
			     	         
				 fSuccessCreateMessages = true; 
			 } catch (MessagingException e) {
					e.printStackTrace();
			 } catch (IOException ioe) {
				 ioe.printStackTrace();
			 }	
			 return messageListCreate;	
		}
		 
		public final void saveDraft(String aSubject, Address[] aRecipients, String aContent, int aRow) {
			try {
				if(aRow == -1) {
					final MailMessage draftMessageNew = new MailMessage(Boolean.FALSE);
					final List<String> draftNewRecipientsList = new ArrayList<String>();
			    	   for(int d = 0; d < aRecipients.length; d++) {
			    		   draftNewRecipientsList.add(aRecipients[d].toString());
			    	}
			    	      
			    	draftMessageNew.setRecipients(draftNewRecipientsList);
			    	draftMessageNew.setMessageContent(aContent);
			    	draftMessageNew.setSubject(aSubject);
			    	draftMessageNew.setFrom(fUserNameEntered);
					final DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
					final String draftDate = df.format(new Date());
					draftMessageNew.setDate(draftDate);
					fDraftList.add(draftMessageNew);
				} else {
					final MailMessage draftMessageEdit = fDraftList.get(aRow);
					final List<String> draftEditRecipientsList = new ArrayList<String>();
			    	   for(int d = 0; d < aRecipients.length; d++) {
			    		   draftEditRecipientsList.add(aRecipients[d].toString());
			    	}			    	   
			    	draftMessageEdit.setRecipients(draftEditRecipientsList); 
			    	draftMessageEdit.setMessageContent(aContent);
			    	draftMessageEdit.setSubject(aSubject);
			    	
			    	draftMessages[aRow].setFlag(Flags.Flag.DELETED, true);			    	
				}
					final Message draftMessageAppend = new MimeMessage(imapSession);
					
					draftMessageAppend.setRecipients(Message.RecipientType.TO, aRecipients);
					draftMessageAppend.setContent(aContent, "text/html; charset=utf-8");
					draftMessageAppend.setSubject(aSubject);
					draftMessageAppend.setFrom(new InternetAddress(fUserNameEntered));
					draftMessageAppend.setSentDate(new Date());
					draftMessageAppend.setFlag(Flags.Flag.DRAFT, true);
					
					final Message[] addDraftMessage = new Message[1];
					addDraftMessage[0] = draftMessageAppend;
					
					SwingWorker<Void, Void> workerAppend = new SwingWorker<Void, Void>() {
						protected Void doInBackground() throws Exception {
							draftFolder.appendMessages(addDraftMessage);
							return null;
						}
					};
				  workerAppend.execute();
			} catch(MessagingException me) {
				me.printStackTrace();
			}
		}
		
		public final String getNodePath() {
			return fNodePath;
		}
		
		public final List<MailMessage> getDraftList() {
			return fDraftList;
		}
		
		     
		public final void valueChanged(TreeSelectionEvent tse) {
				final int[] rowSelection = tree.getSelectionRows();
			if(rowSelection.length > 0) {	
				final int folderRow = rowSelection[0];
				try{						
						if(folderRow == 3) {
						    List<Message> tempUnseenMessageList = new LinkedList<Message>(Arrays.asList(unseenMessages));
							List<Message> tempSeenMessageList = new LinkedList<Message>(Arrays.asList(seenMessages));
								for(int index = inBoxUnseenList.size() - 1; index >= 0 ; index--) {
									final MailMessage message = inBoxUnseenList.get(index);
									if(message.checkRead()) {
										final Message seenMessage = tempUnseenMessageList.get(index);
										tempSeenMessageList.add(seenMessage);
										tempUnseenMessageList.remove(index);
										   seenMessage.setFlag(Flags.Flag.SEEN, true);
										   message.setCheckBoxValue(false);
										   inBoxSeenList.add(message);
										   inBoxUnseenList.remove(message);
									}
								}
								unseenMessages = tempUnseenMessageList.toArray(new Message[0]);
							    seenMessages = tempSeenMessageList.toArray(new Message[0]);
							    setTableList(inBoxSeenList, seenMessages);
								fTml.enableMenuItem("MARK_AS_UNREAD", true);
								fTml.enableMenuItem("MARK_AS_READ", false);
								fTml.enableMenuItem("SPAM", true);
								fTml.enableMenuItem("MOVE", false);
						    }
				}catch(MessagingException me) {
					createErrorMessage("Error Setting Flag, Please Try Again."); 
				}
				
				if(folderRow == 1) {
					setTableList(inBoxUnseenList, unseenMessages);
					fTml.enableMenuItem("MARK_AS_UNREAD", true);
					fTml.enableMenuItem("MARK_AS_READ", true);
					fTml.enableMenuItem("SPAM", true);
					fTml.enableMenuItem("MOVE", false);
				} else if (folderRow == 2)	{
					setTableList(outBoxList, outBoxMessages);
					fTml.disableAllMenuItem();
				} else if (folderRow == 4) {
					setTableList(fDraftList, draftMessages);
					fTml.disableAllMenuItem();
				} else if (folderRow == 5)  {				
					setTableList(sentList, sentMessages);
					fTml.disableAllMenuItem();
				} else if (folderRow == 6) {	
					setTableList(spamList, spamMessages);
					fTml.enableMenuItem("SPAM", false);
					fTml.enableMenuItem("MOVE", true);
					fTml.enableMenuItem("MOVE_SPAM", false);
				} else if (folderRow == 7) {
					setTableList(trashList, trashMessages);
					fTml.enableMenuItem("MOVE", true);
					fTml.enableMenuItem("MARK_AS_UNREAD", false);
					fTml.enableMenuItem("MARK_AS_READ", false);
				}else if (folderRow == 8) {
					setTableList(fSearchList, fSearchMessages);
					fTml.enableMenuItem("MOVE", true);
					fTml.enableMenuItem("MARK_AS_UNREAD", false);
					fTml.enableMenuItem("MARK_AS_READ", false);
				}
			}	
		}
		
		public final void setTableList(List<MailMessage> aModelList, Message[] aCurrentList) {
			mtm.setMailList(aModelList);
			fCbl.setList(aModelList);
			fCurrentList = aCurrentList;
			fCmth.get(0).setList(aModelList);
		}
		
		public final Message[] getCurrentArr() {
			return fCurrentList;
		}
		
		public final Boolean getStatusHeaderBox()  {
			return allChecked;
		}
		
		public final  void setStatusHeaderBox(Boolean aUpdateHeader) {
			this.allChecked = aUpdateHeader;
			deleteButton.setEnabled(aUpdateHeader);
		}
		
		public final void changeStatusHeaderBox(Boolean status) {
			cbh.setSelected(status);			
			frame.repaint();
		}
		
		private final void makeNodes(DefaultMutableTreeNode aTop)  {
			DefaultMutableTreeNode mailCategory = null;
			
			mailCategory = new DefaultMutableTreeNode("InBox");
			aTop.add(mailCategory);
			
			mailCategory = new DefaultMutableTreeNode("OutBox");
			aTop.add(mailCategory);
			
			mailCategory = new DefaultMutableTreeNode("Read");
			aTop.add(mailCategory);
			
			mailCategory = new DefaultMutableTreeNode("Drafts");
			aTop.add(mailCategory);
			
			mailCategory = new DefaultMutableTreeNode("Sent");
			aTop.add(mailCategory);
			
			mailCategory = new DefaultMutableTreeNode("Spam");
			aTop.add(mailCategory);
			
			mailCategory = new DefaultMutableTreeNode("Trash");
			aTop.add(mailCategory);
			
			mailCategory = new DefaultMutableTreeNode("Search");
			aTop.add(mailCategory);
		}
		
		private final String getText(Part bodyPart) {
			@SuppressWarnings("unused")
			boolean textIsHtml = false;
			
			try { 
	        	if(bodyPart.isMimeType("text/*")) {
	        		String s  = (String) bodyPart.getContent();
	        	    textIsHtml = bodyPart.isMimeType("text/html");
	        	    return s;
	        	} 
	        	
	           if(bodyPart.isMimeType("Multipart/alternative")) {
	        		Multipart mp = (Multipart) bodyPart.getContent();
	        		String text = null;
	        		
	        		for(int i = 0; i < mp.getCount(); i++)  {
	        			Part bp = mp.getBodyPart(i);
	        			if(bp.isMimeType("text/plain"))  {
	        				if(text == null) {
	        					text = getText(bp);
	        					continue;
	        				} else if (bp.isMimeType("text/html")) {
	        					String s = getText(bp);
	        				    if(s != null) {
	        				    	return s;
	        				    }
	        				}
	        			}
	        		}
	        		return text;
	        	} else if(bodyPart.isMimeType("multipart/*")) {
	        		Multipart mp = (Multipart) bodyPart.getContent();
	        		for(int i = 0;  i < mp.getCount(); i++) {
	        			String s = getText(mp.getBodyPart(i));
	        			if(s != null) {
	        				return s;
	        			}
	        		}
	        	}
	        } catch(MessagingException me) {
				me.printStackTrace();
			} catch(IOException ioe) {
				ioe.printStackTrace();
			}
		  return null;
	}
		
		public final void reply(String aActionReply) {
		   if(mailTable.getSelectedRowCount() == 1) {
     	     	fReplyMessage = mtm.getMail(mailTable.getSelectedRow());
     	     	@SuppressWarnings("unused")
				final MailCompose mc = new MailCompose(this, frame, fReplyMessage.getRecipients(), "", "", -1, aActionReply); 
		   } else {
			   createErrorMessage("Only One Message Can be Replied to, Please try again.");
		   } 		
		}
		
		public final void delete() {
			showSplashScreen("Deleting Messages, Please Wait...");
			SwingWorker<Void, Integer> workerDeleting = new SwingWorker<Void, Integer>() {
	        	  @Override
	        	  protected Void doInBackground() { 
					    try {
		    	    		 final List<MailMessage> messageListDelete = mtm.getMailList();
				        		final List<Integer> deleteIndicesList = new ArrayList<Integer>();
				        		
				        		for(int index = 0; index < messageListDelete.size(); index++) {
					        		final MailMessage messageDelete = messageListDelete.get(index);
				        			if(messageDelete.getCheckBoxValue()) {
					        			deleteIndicesList.add(index);
					        		}
				        		}
				        		int progressChunkInit = 0;
				        		int progressChunk = progressChunkInit + 20;
				        		setProgress(progressChunk);
				        		final int deleteIndicesSize = deleteIndicesList.size();
				        		Message[] deleteMessageArr = new Message[deleteIndicesSize];
				        		
				        		for(int index = 0; index < deleteIndicesSize; index++) {
				        			final int deleteIndex = deleteIndicesList.get(index);
				        			deleteMessageArr[index] = fCurrentList[deleteIndex];
				        		}
				        		
				        		final int progressChunkInc = (Integer)(80 / deleteIndicesSize);
				        		final int[] folderSelectedArr = tree.getSelectionRows();
				        		final int folderSelectedIndex = folderSelectedArr[0];
				        		Message messageDel;
				        			  if(folderSelectedIndex < 7 && folderSelectedIndex != 6) {
					        			  trashFolder.appendMessages(deleteMessageArr);	  
					        			  for(int index = deleteIndicesSize - 1; index >= 0; index--) {
					    	    			   final int deleteIndex = deleteIndicesList.get(index);
					    	    			   messageDel = fCurrentList[deleteIndex];
					    	    			   messageDel.setFlag(Flags.Flag.DELETED, true);
					    	    			   final MailMessage message = messageListDelete.get(deleteIndex);
					    	    			   trashList.add(message);
					    			 		   message.setCheckBoxValue(false);
					    	    			   mtm.removeValueAt(deleteIndex);
					    	    			   progressChunk = progressChunk + progressChunkInc;
					    	    			   setProgress(progressChunk);
					    	    		   }
						        	   } else {   	 
						        		   final int reply = JOptionPane.showConfirmDialog(frame,
						        				                                           "Permanently Delete Message?",
						        				                                           "JMail", 
						        				                                           JOptionPane.YES_NO_OPTION);
						        		   if(reply ==JOptionPane.YES_OPTION) {
								    		 	 for(int index = deleteIndicesSize - 1; index >= 0; index--) {
							    			 		 	final int deleteIndex = deleteIndicesList.get(index);
							    			 		 	messageDel = fCurrentList[deleteIndex];
							    			 		 	messageDel.setFlag(FLAGS.Flag.DELETED, true);
							    			 		 	mtm.removeValueAt(deleteIndex);
							    			 		 	 progressChunk = progressChunk + progressChunkInc;
								    	    			   setProgress(progressChunk);
							    			      }  
						        		   }	 
					    			  }
		   	    	    } catch (MessagingException me) {
		   	    	    		 createErrorMessage("An Error Occured Deleting Message(s), Please Try Again?");
			    	    }
	        	   return null;
	        	 }
	        	  
	        	 @Override
		         protected void done() {
	        		 splashDialog.dispose();
	        		 changeStatusHeaderBox(false);
	        		 updateFolderTreeExt();
	        		 JOptionPane.showMessageDialog(	frame, 
	        				 						"All Messages Deleted.",
                                                    "JMail", 
                                                    JOptionPane.INFORMATION_MESSAGE);
		         }
	         };
	         
	         workerDeleting.addPropertyChangeListener(new PropertyChangeListener() {
 				@Override
 				public void propertyChange(PropertyChangeEvent pce) {
 					if("progress".equals(pce.getPropertyName())) {
 						progressBar.setIndeterminate(false);
 						progressBar.setValue((Integer) pce.getNewValue());
 					}
 			   }
 		     });
	         
	         workerDeleting.execute();
		}
		
		public final void forward() {
			final List<MailMessage> messageForwardList = mtm.getMailList();
			final int forwardListSize = messageForwardList.size();
			int countChecked = 0;
			int checkedIndex = 0;
			
			for(int c = 0; c < forwardListSize; c++) {
				if(messageForwardList.get(c).getCheckBoxValue() == true) {
					checkedIndex = c;
					countChecked++;
				}
			}
			
			if( countChecked == 1) {
				JTextField forwardAddressText = new JTextField(40);
				JPanel forwardPanel = new JPanel(new BorderLayout());
				MailMessage message = messageForwardList.get(checkedIndex);
				
				forwardPanel.add(new JLabel("Enter Addresses To Forward( Delimiter ',' )"), BorderLayout.NORTH);
				forwardPanel.add(forwardAddressText, BorderLayout.CENTER);
				
				int okOrCancel = JOptionPane.showConfirmDialog(SwingUtilities.getWindowAncestor(frame), 
						 															forwardPanel,
						 															 "Enter Addresses",
						 															 JOptionPane.OK_CANCEL_OPTION);
				if(okOrCancel == JOptionPane.OK_OPTION) {
					final String addressText = forwardAddressText.getText();
					if(addressText.length() != 0) {
						final List<String> forwardList = Arrays.asList(addressText.split(","));
						int position = 0;
						final Address[] forwardAddresses = new Address[20];	
						
						try {
							for(int f = 0; f < forwardList.size(); f++)   {
								final InternetAddress addressCheck = new InternetAddress(forwardList.get(f)); 
								if(validateAddress(addressCheck)) {
									forwardAddresses[position] = addressCheck;
								    position++;
								} else {
									createErrorMessage("The Email Address " + addressCheck.toString() + " is not Valid");
								}
							}							
							sendMessage("FWD: " + message.getSubject(), message.getContent(), forwardAddresses);
							
						} catch (AddressException ae) {
							ae.printStackTrace();
						}
					}
				}
			} else if(countChecked > 1) {
				createErrorMessage("Only One Message Can be Forwarded, Please try again."); 
			} else if(countChecked == 0) {
				createErrorMessage("Please Select a Message to Forward."); 
			}
		}
		
		public final void markSpam() {
			final List<Integer> setSpamList = new ArrayList<Integer>();
			final List<MailMessage> messageList = mtm.getMailList();
			for(int index = 0; index < messageList.size(); index++) {
				final MailMessage messageChecked = messageList.get(index);
				if(messageChecked.getCheckBoxValue()) {
					setSpamList.add(index);
				}
			}
			final int spamListSize = setSpamList.size();
				if(spamListSize > 0) {
					final Message[] spamMessages = new Message[spamListSize];
					final List<Message> messageConvArr = new LinkedList<Message>(Arrays.asList(fCurrentList));					
					for(int k = 0; k < spamListSize; k++) {
						final int markSpamIndex = setSpamList.get(k);
						spamMessages[k] = messageConvArr.get(markSpamIndex);
						final MailMessage mailMessage = messageList.get(markSpamIndex); 
						spamList.add(mailMessage);
						mailMessage.setCheckBoxValue(false);
						mtm.removeValueAt(markSpamIndex);
					}
					fCurrentList = messageConvArr.toArray(new Message[0]);						
						checkAndSetHeaderBox();
						SwingWorker<Void, Void> workerAppend = new SwingWorker<Void, Void>() {
							protected Void doInBackground()  {
								try {
									spamFolder.appendMessages(spamMessages);
								} catch(MessagingException me) {
									createErrorMessage("Error, Could Not Move Message(s) to Spam Folder"); 
								}
								return null;
							}
						};
					  workerAppend.execute();
				} 
		}
		
		private final void checkAndSetHeaderBox() {
			final List<MailMessage> currentList = mtm.getMailList();
 			boolean uncheckedBox = false;
				for(int c = 0; c < currentList.size(); c++) {
					if(currentList.get(c).getCheckBoxValue() == false) {
						uncheckedBox = true;
					}
				}	
			if(getStatusHeaderBox() && uncheckedBox) {
				setStatusHeaderBox(false);
				changeStatusHeaderBox(false);
			}
		}
		
		public final void setFlagRead(boolean aFlagRead) {
			List<Integer> setFlagList = new ArrayList<Integer>();
			
			final List<MailMessage> messageList = mtm.getMailList();
			for(int index = 0; index < messageList.size(); index++) {
					final MailMessage messageChecked = messageList.get(index);
					if(messageChecked.getCheckBoxValue()) {
						setFlagList.add(index);
					}
				}
				if(setFlagList.size() == 0) {
					final int row = mailTable.getSelectedRow();
					setFlagList.add(row);
				}
						
			final int setFlagSize = setFlagList.size();
			if(setFlagSize > 0) {
				try {
					   if(fNodePath.startsWith("[Local Folders, InBox")){   
						   		for(int index = 0; index < setFlagSize; index++) {
									final int indexNum = setFlagList.get(index);
									final MailMessage message = mtm.getMail(indexNum);
									final Message serverMessage = unseenMessages[indexNum];
										if(!aFlagRead) {
									      message.setRead(false);
									      serverMessage.setFlag(Flags.Flag.SEEN, false);									      
									} else {
									     message.setRead(true);
									     serverMessage.setFlag(Flags.Flag.SEEN, true);
									}
								}
						}
					   else if((fNodePath.equals("[Local Folders, Read]")) && (!aFlagRead)) {
						   List<Message> tempUnseenMessageList = new LinkedList<Message>(Arrays.asList(unseenMessages));
						   List<Message> tempSeenMessageList = new LinkedList<Message>(Arrays.asList(seenMessages));
						   for(int f = setFlagSize - 1; f >= 0; f--) {
					    	   final int index = setFlagList.get(f);
					    	   final Message seenMessage = tempSeenMessageList.get(index);
							   seenMessage.setFlag(Flags.Flag.SEEN, false);
							   tempUnseenMessageList.add(seenMessage);
							   tempSeenMessageList.remove(index);
							   final MailMessage mailLocalAddOrRemove = inBoxSeenList.get(index);
							   mailLocalAddOrRemove.setCheckBoxValue(false);
							   mailLocalAddOrRemove.setRead(false);
							   inBoxUnseenList.add(mailLocalAddOrRemove);
							   mtm.removeValueAt(index);
						   }
						   unseenMessages = tempUnseenMessageList.toArray(new Message[0]);
					       seenMessages = tempSeenMessageList.toArray(new Message[0]);
					       if(inBoxSeenList.size() == 0) {
					    	   changeStatusHeaderBox(false);
					       }
					   }
					   
					   updateFolderTreeExt();		
				} catch(MessagingException me) {
					createErrorMessage("Error Setting Flag!");
				} catch(ArrayStoreException ase){
					ase.printStackTrace();
				} catch(NullPointerException npe) {
					npe.printStackTrace();
				}
			} 		
		}
		
		public final void sendMessage(String aSubject, String aContent, Address[] aRecipients) {
			 final Properties props = new Properties();
			 props.put("mail.smtp.host", fUserSmtpServerEntered);
			 props.put("mail.smtp.auth", "true");
			 props.put("mail.smtp.from", fUserNameEntered);
			 props.put("mail.smtp.starttls.enable", "true");
			 props.put("mail.smtp.port", 25);
			 final Session session = Session.getInstance(props, null);
			 final String protocolText = fSecuredConnection ? "smtps" : "smtp";
			 
			 try {						 
				 SMTPTransport transport = (SMTPTransport)session.getTransport(protocolText);
				 transport.connect(protocolText + ".gmx.com", fUserNameEntered, fUserPassEntered);
				 	 
					 Message message = new MimeMessage(session);
					 message.setFrom(new InternetAddress(fUserNameEntered));
					 message.setRecipients(Message.RecipientType.TO, aRecipients);
					 message.setSubject(aSubject);
					 MimeBodyPart htmlPart = new MimeBodyPart();
					 htmlPart.setContent(aContent, "text/html; charset=utf-8");
					 MimeMultipart multiPart = new MimeMultipart("alternative");
					 multiPart.addBodyPart(htmlPart);
					 final int attachedFilesSize = fAttachedFiles.length;
					 if(attachedFilesSize != 0) {	 
						 for(int p = 0; p < attachedFilesSize; p++) {
							 MimeBodyPart attachPart = new MimeBodyPart();
							 attachPart.attachFile(fAttachedFiles[p]);
							 multiPart.addBodyPart(attachPart);
						 }
					 }	
					 message.setContent(multiPart);					 					 
					 transport.sendMessage(message, aRecipients);
						 
					 final int serverReturnCode = transport.getLastReturnCode();
					 transport.close();
					 	if(serverReturnCode == 250) {
						    	 JOptionPane.showMessageDialog(frame, "Message Sent!");
						    	 String sentDate = "";
							     DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
								 sentDate = df.format(new Date());
								 MailMessage sentMessage = new MailMessage(Boolean.FALSE);
								 sentMessage.setSubject(aSubject);
								 sentMessage.setDate(sentDate);
								 sentMessage.setMessageContent(aContent);
								 sentList.add(sentMessage);	 
						     } else {
						    	 JOptionPane.showMessageDialog(frame, 
						    			                       "Error Sending Message", 
						    			                       "Send Error", 
						    			                       JOptionPane.ERROR_MESSAGE);
						     }					 
			 } catch (MessagingException me) {
				 throw new RuntimeException(me);
			 } catch (NullPointerException npe) {
		    	 npe.printStackTrace();
		     } catch (IllegalArgumentException iae) {
		    	 iae.printStackTrace();
		     } catch(IllegalStateException ise) {
		    	 ise.printStackTrace();
		     } catch(IOException ioe) {
		    	 ioe.printStackTrace();
		     }
		}
		
		public final boolean validateAddress(InternetAddress aAddress) {
			boolean result = true;
			
			try {
				aAddress.validate();
			} catch (AddressException ae) {
				result = false;
			}
			
			return result;
		}
			

		public final void setUserName(String aUserName) {
			this.fUserNameEntered = aUserName;
		}
		
		public final void setPass(String aPass) {
			this.fUserPassEntered = aPass;
		}
		
		public final void setAttachedFiles(File[] aAttachedFiles) {
			this.fAttachedFiles = aAttachedFiles;
		}
		
		public final void setImap(String aImap) {
			this.fUserPop3ServerEntered = aImap;
		}
		
		public final String getImapSeverAddress() {
			return this.fUserPop3ServerEntered;
		}
		
		public final void setTrashList(List<MailMessage> aTrashList) {
			this.trashList = aTrashList;
		}
		
		public final void setSmtp(String aSmtp) {
			this.fUserSmtpServerEntered = aSmtp;
		}
		
		public final void setSearchList(List<MailMessage> aMessageList) {
			this.fSearchList = aMessageList;
		}
		
		public final int getSearchListSize() {
			return fSearchList.size();
		}
		
		public final void setSearchArr(Message[] aMessageArr) {
			this.fSearchMessages = aMessageArr;
		}
		
		public final void setSecuredConnection(boolean secured) {
			this.fSecuredConnection = secured;
		}
		
		public final DefaultTreeModel getTreeModel() {
			return this.fTreeModel;
		}
		
		private final Folder getFolder(String aName) {
			Folder f = null;
			try {	
			    f = store.getFolder(aName);
				f.open(Folder.READ_WRITE);
			} catch (MessagingException me) {
				me.printStackTrace();
			}
			return f;
		}
		
		public final String getSmtp() {
			return this.fUserSmtpServerEntered;
		}
		
		public final String getUserName() {
			return this.fUserNameEntered;
		}
		
		public final String getPassword() {
			return this.fUserPassEntered;
		}
		
		public final List<MailMessage> getSentList() {
			return this.sentList;
		}
		
		public final MailMessage getMessageReply() {
			return this.fReplyMessage;
		}
		
		public final String getReplyTo() {
			return this.fReplyTo;
		}
		
		public final String getSubjectReply() {
			return this.fReplySubject;
		}
		
		public final boolean getCTRLKeyState() {
			return this.fPressingCTRL;
		}
		
		public final void setImapSession(Session aSession){
			this.imapSession = aSession;
		}
		
		public final void setStore(Store aStore){
			this.store = aStore;
		}
		
		public final List<MailMessage> getUnseenList() {
			return this.inBoxUnseenList;
		}
		
		public final Message[] getUnseenMessageArr() {
			return this.unseenMessages;
		}
		
		public final List<MailMessage> getSeenList() {
			return this.inBoxSeenList;
		}
		
		public final Message[] getSeenMessageArr() {
			return this.seenMessages;
		}
		
		public final Message[] getTrashMessages(){
			return this.trashMessages;
		}
		
		public final List<MailMessage> getSpamList() {
			return this.spamList;
		}
		
		public final Message[] getSpamMessages() {
			return this.spamMessages;
		}
		
		public final Message[] getSentMessageArr() {
			return this.sentMessages;
		}
		
		public final List<MailMessage> getOutBoxList() {
			return this.outBoxList;
		}
		
		public final Message[] getDraftMessageArr() {
			return this.draftMessages;
		}
		
		public final Message[] getOutBoxMessageArr() {
			return this.outBoxMessages;
		}

		public final List<MailMessage> getTrashList() {
			return this.trashList;
		}
		
		public final MailTableModel getTableModel() {
			return this.mtm;
		}
		
		public final Folder getInBoxFolder() {
			return this.inBoxFolder;
		}
		
		public final Folder getSpamFolder() {
			return this.spamFolder;
		}
		
		public final List<String> getStopWordList() {
			return this.fStopWordList;
		}
		
		public final JTree getTree() {
			return this.tree;
		}
		
		
		private final void addButtons() {			
			signinButton = createButton("Sign In", "SIGN_IN", "Sign In To Account", getClass().getResource("/resources/login.png"), true);
			writeButton = createButton("Write", "WRITE", "Compose Email", getClass().getResource("/resources/mail_compose.png"), false);
			deleteButton = createButton("Delete", "DELETE", "Delete Message(s)", getClass().getResource("/resources/mail_delete.png"), false);
			addressBookButton = createButton("Address Book", "ADDRESS_BOOK", "Open Address Book", getClass().getResource("/resources/address_book.png"), false);
			replyButton = createButton("Reply", "REPLY", "Reply To Message", getClass().getResource("/resources/mailreply.png"), false);
			replyAllButton = createButton("Reply All", "REPLY_ALL", "Reply To All Recipients", getClass().getResource("/resources/mailreplyall.png"), false);
			forwardButton = createButton("Forward", "FORWARD", "Forward Message", getClass().getResource("/resources/forward.png"), false);
            refreshButton = createButton("Refesh", "REFRESH_MAIL", "Check For New Messages", getClass().getResource("/resources/refresh.png"), false);
	        fSearchButton = createButton("Search", "SEARCH_MAIL", "Search Messages", getClass().getResource("/resources/search.png"), false);
	        fServerButton = createButton("Server", "SERVER_SETTINGS", "Mail Server Settings", getClass().getResource("/resources/mail_server.png"), true); 
	        infoButton = createButton("Info", "INFO", "Information Dialog", getClass().getResource("/resources/info.png"), true);
	        exitButton = createButton("Exit", "EXIT", "Exit Client", getClass().getResource("/resources/exit.png"), true);
		}
		
		private final JButton createButton(String aTitle, String ac, String aToolTipText, URL pathIcon, boolean enable)  {
	        final ImageIcon icon = new ImageIcon(pathIcon);
			JButton b = new JButton(aTitle, icon);
			b.setVerticalTextPosition(SwingConstants.BOTTOM);
			b.setHorizontalTextPosition(SwingConstants.CENTER);
			b.setFocusPainted(false);
			final Border border = BorderFactory.createRaisedBevelBorder();
			b.setBorder(border);
			b.addActionListener(this);
			b.setActionCommand(ac);
			final Dimension d = new Dimension(74, 46); 
			b.setPreferredSize(d);
			b.setMinimumSize(d);
			b.setMaximumSize(d);
			b.setToolTipText(aToolTipText);
			fToolBar.add(b);
			b.setEnabled(enable);
			
	    	return b;
		}
		
		private void addEscapeListener(final JFrame frame) {
	    	ActionListener escListener = new ActionListener() {
	    		
	    		@Override
	    		public void actionPerformed(ActionEvent ae) {
	    			exit();
	    		}
	    	};
	    	
	    	frame.getRootPane().registerKeyboardAction(escListener, 
	    												KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
	    												JComponent.WHEN_IN_FOCUSED_WINDOW);
	    }

		final class AttachIconRenderer extends DefaultTableCellRenderer {
			private static final long serialVersionUID = 7028482446083451151L;
			JLabel attachIconLabel;
		
			@Override
			public Component getTableCellRendererComponent(JTable table,
															Object value,
															boolean isSelected,
															boolean hasFocus,
															int row,
															int column)
			{
				final List<MailMessage> messageList = mtm.getMailList();
				final MailMessage message = messageList.get(row);
				final List<Integer> attachList = message.getAttachedFileList();
				
				if(attachList.size() > 0) {
					final ImageIcon icon = new ImageIcon(getClass().getResource("/resources/paperclipreal.png"));
					attachIconLabel = new JLabel();
					attachIconLabel.setIcon(icon);
				} else {
					attachIconLabel = new JLabel();
				}
				return attachIconLabel;
			}
		}
		
		final class IconRenderer extends DefaultTableCellRenderer {
			private static final long serialVersionUID = 4793867298199964932L;
			final ImageIcon icon = new ImageIcon(getClass().getResource("/resources/paperclipreal.png"));
			final private JLabel iconLabel = new JLabel(icon);
			
			@Override
			public Component getTableCellRendererComponent(JTable table,
														   Object value,
														   	boolean isSelected,
														   	boolean hasFocus,
														   	int row,
														   	int column) 	{
				return iconLabel;
			}
		}
		
		 private final void createErrorMessage(String aMessage) {
			  JOptionPane.showMessageDialog(frame, 
	                                        aMessage, 
	                                        "Client Error!",
	                                        JOptionPane.ERROR_MESSAGE);
		  }
 
  public static void main(String[] args)        {
	  Preferences pref = Preferences.userNodeForPackage(Settings.class);
	  final boolean showDialog = pref.getBoolean("settings.developerConsole", true);
	  final JDialog infoDialog = new JDialog();
	  try	    {
	      UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
	      InputMap im = (InputMap)UIManager.get("Button.focusInputMap");
	      im.put(KeyStroke.getKeyStroke("ENTER"), "pressed");
	      im.put(KeyStroke.getKeyStroke("released ENTER"), "released");
	      UIManager.put("Button.defaultButtonFollowsFocus", Boolean.TRUE);
	      SwingUtilities.updateComponentTreeUI(infoDialog);
	    }	catch(UnsupportedLookAndFeelException ulfe)	 {
	    	ulfe.printStackTrace();
	    	System.out.println("Unsupported Look And Feel");		    	
	    }	catch(IllegalAccessException iae)	{
	    	iae.printStackTrace();
	    	System.out.println("Unsupported Look And Feel");
	    }	catch(InstantiationException ie)	{
	    	ie.printStackTrace();
	    	System.out.println("Unsupported Look And Feel");
	    }   catch(ClassNotFoundException cnfe)	{
	    	cnfe.printStackTrace();
	    	System.out.println("Unsupported Look And Feel");
	    }        
	  if(showDialog) {
		  infoDialog.setLayout(new BorderLayout());
		  final  JLabel labelInfo = new JLabel();
		  final String webAddressJavaFX = "http://www.oracle.com/technetwork/java/javase/downloads/index.html";
		  labelInfo.setText("<html><body>This Client is Only Compatible With GMX Mail Service." +
		  		            "To Register go to www.gmx.com Also, to view HTML Messages you must <br> download JavaFX at " +
		  		            "<a href=\"\">"+ webAddressJavaFX +"</a> </body></html>");
		  final Cursor handCursor = new Cursor(Cursor.HAND_CURSOR);
		  labelInfo.setCursor(handCursor);
		  labelInfo.addMouseListener(new MouseAdapter() {
			  @Override
			  public void mouseClicked(MouseEvent me) {
				  final Desktop desktop = Desktop.getDesktop();
				  try {
					  final URI uri = new URI(webAddressJavaFX);
					  desktop.browse(uri);
				  } catch(URISyntaxException | IOException ioe) {
					  JOptionPane.showMessageDialog(null, 
	                                               "Error Accessing Website.", 
	                                               "JMail Client",
	                                               JOptionPane.ERROR_MESSAGE);
				  }
			 }	  
		  });
		  labelInfo.setHorizontalTextPosition(SwingConstants.CENTER);
		  final JButton okButton = new JButton("OK");
		   okButton.addActionListener(new ActionListener () {
			  public void actionPerformed(ActionEvent ae) {
				  infoDialog.dispose();
			  }
		  });
		   okButton.requestFocusInWindow();
		   final JPanel labelPanel = new JPanel();
		   labelPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
		   labelPanel.add(labelInfo);
		   final JPanel buttonCheckPanel = new JPanel();
		   buttonCheckPanel.setLayout(new FlowLayout());
		   JCheckBox checkBoxShowAgain = null;
		   checkBoxShowAgain = new JCheckBox();
		   checkBoxShowAgain.setText("Don't Show Again");
		   checkBoxShowAgain.setSelected(false);
		   buttonCheckPanel.add(checkBoxShowAgain);	      
		   buttonCheckPanel.add(okButton);
		   infoDialog.add(labelInfo, BorderLayout.NORTH);
		   infoDialog.add(buttonCheckPanel, BorderLayout.SOUTH);
		   infoDialog.setModal(true);
		   infoDialog.setTitle("JMail Client");
		   final JRootPane rp = infoDialog.getRootPane();
		   rp.setDefaultButton(okButton);
		   infoDialog.pack();
		   infoDialog.setLocationRelativeTo(null);
		   infoDialog.setVisible(true);
		   pref.putBoolean("settings.developerConsole", !checkBoxShowAgain.isSelected());
	  }
	  
	final Runnable thread = (new Runnable()			{
			public void run()				{
				new JClient();		
			}				
		});		
		SwingUtilities.invokeLater(thread);
   }		
}