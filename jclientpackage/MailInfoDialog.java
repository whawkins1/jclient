package jclientpackage;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.ImageIcon;
import javax.swing.KeyStroke;

import com.sun.glass.events.KeyEvent;

public final class MailInfoDialog {
	
	public MailInfoDialog(JFrame aParent) {
		final URL imagePath = getClass().getResource("/resources/mail_message_new.png");
		final ImageIcon infoIcon = new ImageIcon(imagePath);
		final JLabel imageInfoLabel = new JLabel();
		imageInfoLabel.setIcon(infoIcon);
		
		final JPanel infoLeftPanel = new JPanel();
		infoLeftPanel.add(imageInfoLabel);
		
		final JPanel infoRightPanel = new JPanel();
		infoRightPanel.setLayout(new BoxLayout(infoRightPanel, BoxLayout.Y_AXIS));
		infoRightPanel.add(new JLabel("Name: JClient"));
		infoRightPanel.add(Box.createVerticalStrut(4));
		infoRightPanel.add(new JLabel("Version: 1.0.0"));
		infoRightPanel.add(Box.createVerticalStrut(4));
		infoRightPanel.add(new JLabel("Copyright: 2016"));
		
		final JPanel infoMainPanel = new JPanel(new BorderLayout());
		infoMainPanel.add(infoLeftPanel, BorderLayout.LINE_START);
		infoMainPanel.add(infoRightPanel, BorderLayout.LINE_END);
		
		final JDialog infoDialog = new JDialog(aParent, "Information", true );
		infoDialog.add(infoMainPanel);
		Image image = null;
		try{
			image = ImageIO.read(imagePath);
		} catch (IOException ioe) {
			JOptionPane.showMessageDialog(aParent, 
                                 "Unable to Access mail_message_new.png.", 
                                 "JMail Client",
                                 JOptionPane.ERROR_MESSAGE);    	 
		}
		infoDialog.setIconImage(image);
		final Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		addEscapeListener(infoDialog);
		infoDialog.setSize(d.width/3, d.height/3);
		infoDialog.pack();
		infoDialog.setLocationRelativeTo(null);
		infoDialog.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		ImageIcon imgFrame = new ImageIcon(); 
		infoDialog.setIconImage(imgFrame.getImage());
		infoDialog.setTitle("JMail Client");
		infoDialog.setVisible(true);
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