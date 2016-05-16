package jclientpackage;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.net.URL;

import java.util.List;

import javax.swing.JPanel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.JButton;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTree;

public final class ButtonCellRenderer extends DefaultTreeCellRenderer 
                                                   implements TreeCellRenderer{
	private static final long serialVersionUID = 1L;
	final Color lightBlue = new Color(204, 255, 255);
	private JPanel fTrashPanel;
	private JPanel fRefreshPanel;
	private JPanel fSpamPanel;
	private JPanel fDefaultPanel;
	private JLabel fDefaultLabel;
	private JLabel fRefreshLabel;
	private JLabel fSpamLabel;
	private JButton fTrashButton;
	private JButton fSpamButton;
	private JButton fRefreshButton;
	private JClient fMc;
	
	public ButtonCellRenderer(JFrame aParent, JClient aMc) {
		this.fMc = aMc;
		
		fRefreshButton = createButton(getClass().getResource("/resources/refreshSmall.png"), "Check New Mail");
		fTrashButton = createButton(getClass().getResource("/resources/emptyTrash.png"), "Empty Spam");
		fSpamButton = createButton(getClass().getResource("/resources/emptyTrash.png"), "Empty Spam");
		fRefreshLabel = new JLabel("Inbox");
		fRefreshPanel = createPanel(fRefreshButton, fRefreshLabel);
		fSpamLabel = new JLabel("Spam");
		fSpamPanel = createPanel(fSpamButton, fSpamLabel);
		final JLabel trashLabel = new JLabel("Trash");
		fTrashPanel = createPanel(fTrashButton, trashLabel);
		fDefaultPanel = new JPanel(new BorderLayout());
		fDefaultPanel.setOpaque(true);
		fDefaultLabel = new JLabel();
		fDefaultPanel.add(fDefaultLabel, BorderLayout.WEST);
	}
	
	@Override
	public Component  getTreeCellRendererComponent(JTree tree, Object value, boolean selected,
			                                      boolean expanded, boolean leaf, int row, 
			                                      boolean hasFocus){  
		Color highlightColor;
		if(selected) {
			highlightColor = lightBlue;
		} else {
			highlightColor = Color.white;
		}
		List<MailMessage> messageList;
		if(row == 1) {
			fRefreshPanel.setBackground(highlightColor);
			fRefreshPanel.grabFocus();
			return fRefreshPanel;
		}
		else if(row == 7) {
			messageList = fMc.getTrashList();
			if(messageList.size() > 0) {
				fTrashButton.setEnabled(true);
				fTrashButton.setVisible(true);
			} else {
				fTrashButton.setEnabled(false);
				fTrashButton.setVisible(false);
			}
			fTrashPanel.setBackground(highlightColor);
			fTrashPanel.grabFocus();
			return fTrashPanel;
		}  else if(row == 6) {
			messageList = fMc.getSpamList();
			if(messageList.size() > 0) {
				fSpamButton.setEnabled(true);
				fSpamButton.setVisible(true);
			} else {
				fSpamButton.setEnabled(false);
				fSpamButton.setVisible(false);
			}
			fSpamPanel.setBackground(highlightColor);
			return fSpamPanel;
		} else {
			final DefaultMutableTreeNode node = (DefaultMutableTreeNode)value;
			final String nodeText = node.getUserObject().toString();
			fDefaultLabel.setText(nodeText);
			fDefaultPanel.setBackground(highlightColor);
			fDefaultPanel.grabFocus();
		}
		return fDefaultPanel;
	}
	
	public void setLabel(int aRow, String aLabelText) {
		if(aRow == 1) {
			fRefreshLabel.setText(aLabelText);
		} else if(aRow == 6) {
			fSpamLabel.setText(aLabelText);
		}
	}
	
	
	
	private JPanel createPanel(JButton aButton, JLabel aLabel) {
		final JPanel p = new JPanel(); 
		p.setLayout(new BorderLayout());
		p.setOpaque(true);		
		p.setFocusable(true);
		p.setRequestFocusEnabled(true);
		p.add(aLabel, BorderLayout.WEST);
		p.add(aButton, BorderLayout.EAST);
		
		return p;
	}
	
	private JButton createButton(URL aURLPath, String aToolTipText) {
		final JButton b = new JButton();
		b.setToolTipText(aToolTipText);
		b.setOpaque(false);
		b.setContentAreaFilled(false);
		final Dimension d = new Dimension(18, 18);
		b.setPreferredSize(d);
		b.setMaximumSize(d);
		b.setMinimumSize(d);
		final ImageIcon icon = new ImageIcon(aURLPath);
		b.setIcon(icon);
				
		return b;
	}
	
}
