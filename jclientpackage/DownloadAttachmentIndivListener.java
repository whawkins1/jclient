package jclientpackage;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionListener;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;

import javax.mail.MessagingException;
import javax.mail.internet.MimeBodyPart;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

public final class DownloadAttachmentIndivListener implements ActionListener{
	private MimeBodyPart fBodyPart;
	private String fFileName;
	final private JDialog fParent;
	private JDialog fDownloadingDialog;
		
	public  DownloadAttachmentIndivListener (JDialog aParent, MimeBodyPart aBodyPart, String aFileName) {
		fBodyPart = aBodyPart;
		fFileName = aFileName;
		fParent = aParent;
	}
		
	@Override
	public void actionPerformed(ActionEvent ae) {
				final JFileChooser pathChooser = new JFileChooser();
				pathChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				pathChooser.setCurrentDirectory(new File("."));
				pathChooser.setSelectedFile(new File(fFileName));
				pathChooser.setDialogTitle("Select Directory");
				pathChooser.setApproveButtonText("Save");
				pathChooser.setApproveButtonToolTipText("Save Attachment");
				String extension = "";
				final int fileNameLen = fFileName.length();
				//Parse Out File Name
				if((fFileName.contains(".")) && (fFileName.indexOf(".") == fileNameLen - 4)) {
					final int i = fFileName.lastIndexOf('.');
					extension = (fFileName.substring(i)).toLowerCase();
				}
				final int returnVal = pathChooser.showOpenDialog(fParent);
				if(returnVal == JFileChooser.APPROVE_OPTION) {
					final File directory = pathChooser.getCurrentDirectory();
					if(directory.canWrite()) {
						final File saveFile = pathChooser.getSelectedFile();
						String fileName = saveFile.getName();
			            String dirPath = directory.getAbsolutePath();
						if(saveFile.exists()) {
							int returnValue = JOptionPane.YES_NO_OPTION;
							JOptionPane.showConfirmDialog(pathChooser, "File Exists, Overwrite?", "OVERWRITE", returnValue);
							if(returnValue == JOptionPane.YES_OPTION) {
								final File[] dirFiles = directory.listFiles();
								String checkFileName = "";								
								boolean noDuplicate = true;
								final int dirNumFiles = dirFiles.length;
								for(int f = 0; f < dirNumFiles & noDuplicate; f++) {
									if(dirFiles[f].getName().equalsIgnoreCase(fileName)) {
										if(f != 0) {
											int numAdder = 2;
											boolean notFound = true;
											while(notFound) {
												checkFileName = fileName + "(" + numAdder + ")";
												for(int c = 0; c < dirNumFiles; c++) {
													if(dirFiles[c].getName().equalsIgnoreCase(checkFileName)) {
														notFound = false;
													}
												}
												numAdder++;	
											}
										} else {
											checkFileName = fileName + "(1)";
										}
										noDuplicate = false;
										fileName = checkFileName;
									}
								}
								JOptionPane.showMessageDialog(pathChooser, 
										                      "The File " + fFileName + " Will be Renamed to " + fileName,
															  "RENAME", 
															  JOptionPane.INFORMATION_MESSAGE);	
								}	
						}
						if(!(fileName.toLowerCase().endsWith(extension))) {
							fileName = fileName + extension;
						}
						fFileName = (dirPath + File.separator + fileName);
						createDownloadDialog(fFileName);
						SwingWorker<Void, Integer> workerSaving = new SwingWorker<Void, Integer>() {
							protected Void doInBackground() {
								try {
								fBodyPart.saveFile(new File(fFileName));
								} catch(MessagingException me) {
									me.printStackTrace();
								} catch(IOException ioe) {
									ioe.printStackTrace();
								}
								return null;
							}
							@Override
							protected void done() {
								fDownloadingDialog.dispose();
								JOptionPane.showMessageDialog(pathChooser, 
										                      "Download Complete!", 
										                      "ATTACHMENT", 
										                      JOptionPane.INFORMATION_MESSAGE);
							}
						};
						workerSaving.execute();						
					} else {
						JOptionPane.showMessageDialog(pathChooser, 
								  "Sufficient Permissions Needed in Writing to " + directory.getName(), 
								  "ACCESS DENIED", 
								  JOptionPane.ERROR_MESSAGE);
					}
				}
	}
	
	private final void createDownloadDialog(String aFileName) {
		fDownloadingDialog = new JDialog(fParent);
		final Dimension dialogSize = new Dimension(200, 30);
		fDownloadingDialog.setSize(dialogSize);
		fDownloadingDialog.setMaximumSize(dialogSize);
		fDownloadingDialog.setMinimumSize(dialogSize);
		fDownloadingDialog.setLayout(new BorderLayout());
		final JLabel tempTextLabel = new JLabel("Downloading " + aFileName);
		fDownloadingDialog.add(tempTextLabel, BorderLayout.PAGE_START);
		final JLabel tempImageLabel = new JLabel(new ImageIcon(getClass().getResource("/images/waitingIcon.gif")));
		fDownloadingDialog.add(tempImageLabel);
		fDownloadingDialog.setModal(false);
		fDownloadingDialog.setUndecorated(true);
		fDownloadingDialog.pack();
		fDownloadingDialog.setLocationRelativeTo(null);
		fDownloadingDialog.setVisible(true);
	}
}