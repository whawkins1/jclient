package jclientpackage;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.List;


public class CheckBoxListener implements ItemListener{
	final private MailTableModel fMtm;
	 final private JClient fMc;
	 private List<MailMessage> fMailList;
	 
	 public CheckBoxListener(MailTableModel aMtm, JClient aMc, List<MailMessage> aMailList)    {
		 this.fMtm = aMtm;
		 this.fMc = aMc;
		 this.fMailList = aMailList;
	 }
	 
	 public void itemStateChanged(ItemEvent e) {
		 boolean checked = (e.getStateChange() == ItemEvent.SELECTED);
		 if( !((checkIsFalseBox()) && (checked == false)))
			 for(int r = 0; r < fMtm.getRowCount(); r++) {
				fMtm.setValueAt(new Boolean(checked), r, 0);
				final MailMessage message = fMailList.get(r);
				message.setCheckBoxValue(true);
			 }		 
			 fMc.setStatusHeaderBox(checked);			 
	}
	 
	 private Boolean checkIsFalseBox() {
		 for(int m = 0; m < fMailList.size(); m++) {
			 if(fMailList.get(m).getCheckBoxValue().equals(false)) {
			     return true;
			 }
	     }
		 return false;
	 }
	 
	 public final void setList(List<MailMessage> aMailList) {
		 this.fMailList = aMailList;
	 }
}