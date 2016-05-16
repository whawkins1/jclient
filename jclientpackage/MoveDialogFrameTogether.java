package jclientpackage;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.Window;

import javax.swing.JFrame;
import javax.swing.JDialog;

public final class MoveDialogFrameTogether extends ComponentAdapter {
	private Window frameWindow;
	private Window dialogWindow;
	public MoveDialogFrameTogether(JFrame aFrame, JDialog aDialog) {
		this.frameWindow = aFrame;
		this.dialogWindow= aDialog;
		frameWindow.addComponentListener(this);
		dialogWindow.addComponentListener(this);
	}
	
	@Override
	public final void componentMoved(ComponentEvent ce) {
		Window win = (Window)ce.getComponent();
		if(win == frameWindow) {
			dialogWindow.removeComponentListener(this);
			dialogWindow.setLocationRelativeTo(frameWindow);
			dialogWindow.addComponentListener(this);
		} else if(dialogWindow.isVisible()) {
			frameWindow.setLocationRelativeTo(dialogWindow);
		}
	}
}
