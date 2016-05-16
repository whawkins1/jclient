package jclientpackage;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.internet.MimeBodyPart;

import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.SwingWorker;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public final class DownloadAllAttachmentListener implements ActionListener {
	final JDialog fParent;
	final List<Integer> fBodyPartIndices;
	final Message fMessage;
	JDialog fSplashSaveDialog;
	JProgressBar fSaveProgressBar;
	JLabel fSavingLabel;
	
	public DownloadAllAttachmentListener(JDialog aParent, Message aMessage, List<Integer> aBodyPartIndices) {
		fParent = aParent;
		fMessage = aMessage;		
		fBodyPartIndices = aBodyPartIndices;
	}
	
	@Override
	public void actionPerformed(ActionEvent ae) {
			final JFileChooser pathChooser = new JFileChooser();
			pathChooser.setCurrentDirectory(new File("."));
			pathChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			pathChooser.removeChoosableFileFilter(pathChooser.getFileFilter());
			pathChooser.setDialogTitle("Select Directory");
			pathChooser.setApproveButtonText("Save");
			pathChooser.setApproveButtonToolTipText("Save Attachment");
			final int returnVal = pathChooser.showOpenDialog(fParent);
	     
			if(returnVal == JFileChooser.APPROVE_OPTION) {
				final File directory = pathChooser.getCurrentDirectory();
				
				if(directory.canWrite()) {
					showSplashSaveDialog();
					SwingWorker<Void, Integer> workerSaving = new SwingWorker<Void, Integer>() { 
					     @Override
					      protected Void doInBackground() {
					     	  String dirPathFull = "";
					    	  MimeBodyPart bodyPart = null;
					    	  String fileName = "";
					    	  String checkDupFileName = "";
							String dirPath = directory.getAbsolutePath();
					        String currentDirName = pathChooser.getSelectedFile().getName();
					        dirPathFull = dirPath + File.separator + currentDirName + File.separator;
					        final File fullPathDirectory = new File(dirPathFull);
					        final File[] dirFiles = fullPathDirectory.listFiles();
					        Multipart multiPart = null;
					        try{
							     multiPart = (Multipart) fMessage.getContent();
					        } catch (IOException ioe) {
					        	JOptionPane.showMessageDialog(fParent, 
				                        "DataHandler Error", 
				                        "MESSAGE ERROR!", 
				                        JOptionPane.ERROR_MESSAGE);
					        } catch(MessagingException me) {
					        	JOptionPane.showMessageDialog(fParent, 
				                        "Extracting Content Failed", 
				                        "MESSAGE ERROR!", 
				                        JOptionPane.ERROR_MESSAGE);
					        }
							final int dirNumFiles = dirFiles.length;
							final int numBodyParts = fBodyPartIndices.size();
							final float progressChunkSize = (float)(100 / numBodyParts);
							int trackChunkSize = 0;
							for(int p = 0; p < numBodyParts; p++) {									
								int bodyPartIndex = fBodyPartIndices.get(p);
								try{
								    bodyPart = (MimeBodyPart)multiPart.getBodyPart(bodyPartIndex);
								}catch(MessagingException me) {
									JOptionPane.showMessageDialog(fParent, 
					                        "Failed Obtaining Message Part", 
					                        "MESSAGE ERROR!", 
					                        JOptionPane.ERROR_MESSAGE);
								} catch(IndexOutOfBoundsException iobe) {
									JOptionPane.showMessageDialog(fParent, 
					                        "Index Out of Range", 
					                        "INDEX ERROR!", 
					                        JOptionPane.ERROR_MESSAGE);
								}
								
								try {
											     fileName = bodyPart.getFileName();
											} catch (MessagingException me) {
												JOptionPane.showMessageDialog(fParent, 
									                    "Failed Obtaining Message Part FileName", 
									                    "MESSAGE ERROR!", 
									                    JOptionPane.ERROR_MESSAGE);
											}
								
								
									fSavingLabel.setText("Downloading " + fileName);
									fSavingLabel.repaint();
									final File checkFileOnDrive = new File(fullPathDirectory + File.separator + fileName);
										if((checkFileOnDrive.isFile()) && (dirNumFiles != 0)) {						
												for(int f = 0; f < dirNumFiles; f++) {
													final String dirFileName = dirFiles[f].getName();
													if(dirFileName.equalsIgnoreCase(fileName)) {												
														int response = JOptionPane.showConfirmDialog(null, 
																										"File Exists, Overwrite?", 
																										"OVERWRITE", 
																										JOptionPane.YES_NO_OPTION, 
																										JOptionPane.QUESTION_MESSAGE);
														 if(response == JOptionPane.NO_OPTION) {	
															    int numAdder = 1;														
																	for(int c = 0; c < dirNumFiles; c++) {
																		String numAdderParen = "(" + numAdder + ")";
																		final int lastDotIndex = fileName.lastIndexOf('.');
																		String subFirstPartFileName = fileName.substring(0, lastDotIndex);
																		String fileExt = fileName.substring(lastDotIndex);
																		checkDupFileName = subFirstPartFileName + numAdderParen + fileExt;
																		if(dirFiles[c].getName().equalsIgnoreCase(checkDupFileName)) {
																			numAdder++;
																		}// end if
																	}// end for
																	
																	JOptionPane.showMessageDialog(pathChooser, 
													   						"The File " + fileName + " Will be Renamed to " + checkDupFileName,
																			"RENAME", 
																			JOptionPane.INFORMATION_MESSAGE);
															        fileName = checkDupFileName;													
														 }	// end if
													 }//end if
												}// end for
									     }// end if
										  fileName = (dirPathFull + fileName);
										  try{
									        bodyPart.saveFile(new File(fileName));
										  } catch(MessagingException me) {
											  JOptionPane.showMessageDialog(fParent, 
													                        "Error Occured Downloading the Attachment", 
													                        "DOWNLOAD ERROR", 
													                        JOptionPane.ERROR_MESSAGE);
										  } catch(IOException ioe) {
											  JOptionPane.showMessageDialog(fParent, 
								                        "Error Occured Writing to the Drive", 
								                        "DOWNLOAD ERROR", 
								                        JOptionPane.ERROR_MESSAGE);
										  }
									        int setProgressChunk = (int)(trackChunkSize + progressChunkSize);
									        trackChunkSize = setProgressChunk;
									        setProgress(setProgressChunk);
							
								}  // end for
						          try {
									Thread.sleep(1000);
								} catch (InterruptedException e) {
									JOptionPane.showMessageDialog(fParent, 
					                        "Thread was Interrupted", 
					                        "THREAD ERROR", 
					                        JOptionPane.ERROR_MESSAGE);								
								} catch(IllegalArgumentException iae) {
									JOptionPane.showMessageDialog(fParent, 
					                        "Error Occured Downloading the Attachment", 
					                        "DOWNLOAD ERROR", 
					                        JOptionPane.ERROR_MESSAGE);
								}
							
					      return null;  
						  }  
					    				      
						  	@Override
							protected void done() {
						  		JOptionPane.showMessageDialog(fSplashSaveDialog, 
						  				                      "Downloads Complete", 
						  				                      "DOWNLOAD", 
						  				                      JOptionPane.INFORMATION_MESSAGE);
						  		
								fSplashSaveDialog.dispose();					  	
						  	}
					  	};
						
							workerSaving.addPropertyChangeListener(new PropertyChangeListener() {
		        				@Override
		        				public void propertyChange(PropertyChangeEvent pce) {
		        					if("progress".equals(pce.getPropertyName())) {
		        						fSaveProgressBar.setIndeterminate(false);
		        						fSaveProgressBar.setValue((Integer) pce.getNewValue());
		        					}
		        			}
		        		});
		        		workerSaving.execute();
				
					         	
			} else {
				JOptionPane.showMessageDialog(pathChooser, 
											  "Sufficient Permissions Needed in Writing to " + directory.getName(), 
											  "ACCESS DENIED", 
											  JOptionPane.ERROR_MESSAGE);
			}
		}
	}	
	
	private final void showSplashSaveDialog() {
		fSplashSaveDialog = new JDialog(fParent);
		final Dimension dialogSize = new Dimension(200, 30);
		fSplashSaveDialog.setSize(dialogSize);
		fSplashSaveDialog.setMaximumSize(dialogSize);
		fSplashSaveDialog.setMinimumSize(dialogSize);
		fSplashSaveDialog.setLayout(new BorderLayout());
		 fSavingLabel = new JLabel();
		 fSplashSaveDialog.add(fSavingLabel, BorderLayout.CENTER);
		 fSaveProgressBar = new JProgressBar(0, 100);
		 fSaveProgressBar.setValue(0);
		 fSplashSaveDialog.add(fSaveProgressBar, BorderLayout.SOUTH);
		 fSplashSaveDialog.setModal(false);
		 fSplashSaveDialog.setUndecorated(true);
		 fSplashSaveDialog.pack();
		 fSplashSaveDialog.setLocationRelativeTo(null);
		 fSplashSaveDialog.setVisible(true);
		 
	 }
}