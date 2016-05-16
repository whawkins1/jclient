package jclientpackage;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.FlowLayout;
import java.awt.Dimension;
import java.awt.Image;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.JOptionPane;

import com.sun.glass.events.KeyEvent;

import java.util.List;

public final class AddAddressInfo  implements ActionListener  {
	private final AddressBookTableModel fAbtm;  
    private final JTextField fFirstNameText;
    private final JTextField fAddressText;
    private final JTextField fMiddleInitialText;
    private final JTextField fLastNameText;
    @SuppressWarnings("unused")
	private final JButton fOKButton;
    @SuppressWarnings("unused")
	private final JButton fCancelButton;
    private final JDialog fParent;
    private final JDialog fAddressDialog;
    private final String fMode;
    private Boolean fSuccessAdd;
    private  int fEditAccountNum;
    private final List<AddressAccount> fAddressList;
	
	public AddAddressInfo(JDialog aParent, AddressBookTableModel aAbtm, List<AddressAccount> aAddressList, String aMode) {
		this.fParent = aParent;
		fAddressDialog = new JDialog(fParent, "Add Address", true);
		this.fAbtm = aAbtm;
		this.fMode = aMode;
		this.fAddressList = aAddressList;
		
		fSuccessAdd = false;
		
		final JLabel firstNameLabel = new JLabel("First Name:");
		final JLabel middleInitialLabel = new JLabel("Middle Initial:");
		final JLabel lastNameLabel = new JLabel("Last Name:");
		final JLabel addressLabel = new JLabel("Address:");
				
		fFirstNameText = new JTextField(21);
		fMiddleInitialText = new JTextField(20);
		fLastNameText = new JTextField(21);
		fAddressText = new JTextField(22);
		
		if(aMode.equalsIgnoreCase("EDIT")) {
			fFirstNameText.setText(fAddressList.get(fEditAccountNum).getFirstName());
			fMiddleInitialText.setText(fAddressList.get(fEditAccountNum).getMiddleInitial());
			fLastNameText.setText(fAddressList.get(fEditAccountNum).getLastName());
			fAddressText.setText(fAddressList.get(fEditAccountNum).getAddress());
		}
	    		
		final JPanel firstNamePanel = createPanelInfo(firstNameLabel, fFirstNameText);
		final JPanel middleInitialPanel = createPanelInfo(middleInitialLabel, fMiddleInitialText);
		final JPanel lastNamePanel = createPanelInfo(lastNameLabel, fLastNameText);
		final JPanel addressPanel = createPanelInfo(addressLabel, fAddressText);		
	    
		final JPanel buttonPanel = new JPanel(new FlowLayout());
		
	    fOKButton = createButton("OK", "OK", buttonPanel);
	    fCancelButton = createButton("Cancel", "CANCEL", buttonPanel);
	
	    final JPanel mainPanel = new JPanel();
	    mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
	    mainPanel.add(firstNamePanel);
	    mainPanel.add(middleInitialPanel);
	    mainPanel.add(lastNamePanel);
	    mainPanel.add(addressPanel);
	    mainPanel.add(buttonPanel);
	    
	    InputMap im = (InputMap)UIManager.get("Button.focusInputMap");
	    im.put(KeyStroke.getKeyStroke("ENTER"), "pressed");
	    im.put(KeyStroke.getKeyStroke("released ENTER"), "released");
	    UIManager.put("Button.defaultButtonFollowsFocus", Boolean.TRUE);
	    
	    fAddressDialog.getContentPane().add(mainPanel);
	    
	    fAddressDialog.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	    Image image = null;
	    try {
	         image = ImageIO.read(getClass().getResource("/resources/address_book.png"));	
	    } catch (IOException ioe) {
	    	JOptionPane.showMessageDialog(fParent, 
                    "Unable to Access address_book.png", 
                    "JMail Client",
                    JOptionPane.ERROR_MESSAGE);    	 
	    }
        addEscapeListener(fAddressDialog);
        fAddressDialog.setIconImage(image);
        fAddressDialog.setTitle(fMode + " Address");
        fAddressDialog.pack();
        fAddressDialog.setLocationRelativeTo(fParent);
        fAddressDialog.setVisible(true);
	}
	
	public AddAddressInfo(JDialog aParent, AddressBookTableModel aAbtm, List<AddressAccount> aAddressList, int aEditAccountNum, String aMode) {
		this(aParent, aAbtm, aAddressList, aMode);
		this.fEditAccountNum = aEditAccountNum;
		
	}
		public void actionPerformed(ActionEvent ae) {
			if(ae.getActionCommand().equals("OK")) {
				final String firstNameEntered = fFirstNameText.getText().trim();
				final String lastNameEntered = fLastNameText.getText().trim();
				final String middleNameEntered = fMiddleInitialText.getText().trim();
				final String addressEntered = fAddressText.getText().trim();
				
				if(firstNameEntered.equals("") || lastNameEntered.equals("") 
					|| middleNameEntered.equals("") || addressEntered.equals("")) {
					
					JOptionPane.showMessageDialog(fAddressDialog,	"All Fields Must Be Completed", "Add Error", JOptionPane.ERROR_MESSAGE);
				} else if (!( addressEntered.contains("@"))){
					JOptionPane.showMessageDialog(fAddressDialog,	
							                     "Address Field Must Contain '@'(i.e. johndoe@test.com)", 
							                     "Add Error",
							                     JOptionPane.ERROR_MESSAGE);
				} else {
					if(fMode.toUpperCase().equals("ADD")) {
						final AddressAccount aAdd = new AddressAccount(firstNameEntered, lastNameEntered, middleNameEntered, addressEntered);
						fAbtm.addAddress(aAdd);						
					} else if (fMode.toUpperCase().equals("EDIT")) {					
						final AddressAccount aEdit = fAddressList.get(fEditAccountNum);
						aEdit.setFirstName(firstNameEntered);
						aEdit.setLastName(lastNameEntered);
						aEdit.setMiddleInitial(middleNameEntered);
							if(addressEntered.contains("@")) {
								aEdit.setAddress(addressEntered);
								fAbtm.fireTableDataChanged();
							} else {
								JOptionPane.showMessageDialog(fAddressDialog, "The Address Field must contain the character '@'", "Error Edit", JOptionPane.ERROR_MESSAGE);
							    return;
							}
					}
					fSuccessAdd = true;
					fAddressDialog.dispose();
				}
			} else if(ae.getActionCommand().equals("CANCEL")) {
				fAddressDialog.dispose();	
			}			
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
		
		public final boolean showDialog() {
			return fSuccessAdd;
		}
		
		private final JButton createButton(String aTitle, String aActionCommand, JPanel aButtonPanel) {
			JButton b = new JButton(aTitle);
			b.setActionCommand(aActionCommand);
			b.addActionListener(this);
			final Dimension d = new Dimension(65, 20);
			b.setPreferredSize(d);
			b.setMaximumSize(d);
			b.setMinimumSize(d);
			b.setEnabled(true);
			aButtonPanel.add(Box.createHorizontalStrut(4));
			aButtonPanel.add(b);
			
			return b;
		}
		
		private final JPanel createPanelInfo(JLabel aLabel, JTextField aText) {
			JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));
			p.add(aLabel);
			p.add(Box.createHorizontalStrut(4));
			p.add(aText);
			
			return p;
		}
}
