package jclientpackage;
import java.util.ArrayList;
import java.util.List;

public final class MailMessage implements Comparable<MailMessage> {
	public Boolean fCheckBoxValue;
	private String fFrom;
	private String fSubject;
	private String fDate;
	private String fUid;
	private Boolean fRead;
	private String fContent;
	private String fSortMode;
	private String fReplyTo;
	private List<String> fRecipients;	
	private List<Integer> fAttachedBodyPartIndex = new ArrayList<Integer>();
	
    public MailMessage(Boolean checkBoxValue)    {
		this.fCheckBoxValue = checkBoxValue;
    }
    
    	public final void setAttachedBodyPartIndex(int aIndex) {
    		fAttachedBodyPartIndex.add(aIndex);
    	}
    	
    	public final Integer getAttachedBodyPartIndex(int aIndex) {
    		return fAttachedBodyPartIndex.get(aIndex);
    	}
    	
    	public final List<Integer> getAttachedFileList() {
    		return fAttachedBodyPartIndex;
    	}
    	
        public final void setSortMode(String aSortModeText) {
	    	this.fSortMode = aSortModeText;
	    }
	    
	    public final void  setFrom(String aFrom) {
	    	this.fFrom = aFrom;
	    }
	    
	    public final void setRecipients(List<String> aRecipients)  {
	    	this.fRecipients = aRecipients;
	    }
	    
	    public final List<String> getRecipients() {
	    	return this.fRecipients;
	    }
	    
	    public final String getFrom() {
	    	return this.fFrom;
	    }
	    
	    public final String getSubject() {
	    	return this.fSubject;
	    }
	    
	    public final void setMessageContent(String aContent) {
	    	this.fContent = aContent;
	    }
	
	    public final void setDate(String aDate) {
	    	this.fDate = aDate;
	    }
	    
	    public final String getDate() {
	    	return this.fDate;
	    }
	    
	    public final String getContent() {
	    	return this.fContent;
	    }	    
	    
	    public final void setMessage(String aMessage) {
	    	this.fContent = aMessage;
	    }
	    
	    public final void setSubject(String aSubject) {
	    	this.fSubject = aSubject;
	    }
	    
	    public final String getUid() {
	    	return this.fUid;
	    }
	    
	    public final void setUid(String aUid) {
	    	this.fUid = aUid;
	    }
	    
	    public final boolean checkRead() {
	    	return this.fRead;
	    }
	    
	    public final void setRead(boolean aRead) {
	    	this.fRead = aRead;
	    }
	    
	    public final void setReplyTo(String aReplyTo) {
	    	this.fReplyTo = aReplyTo;
	    }
	    
	    public final String getReplyTo() {
	    	return this.fReplyTo;
	    }
	    
	    public final Boolean getCheckBoxValue() {
	        return this.fCheckBoxValue;
	    }
	    
	    public final void setCheckBoxValue(Boolean aCheckBoxValue)  {
	    	this.fCheckBoxValue = aCheckBoxValue;
	    }
	    
	    @Override
	    public final int compareTo(MailMessage aCompareMail) {
	    	int calculatedMailComparison = 0;
	    	
	    	switch(fSortMode) {
	    	case "Sort By Subject":
	    		calculatedMailComparison = getSubject().compareToIgnoreCase((aCompareMail.getSubject())); 
	    	   break;
	    	case "Sort By From":
	    		calculatedMailComparison = getFrom().compareToIgnoreCase((aCompareMail.getFrom()));
	    	   break;
	    	case "Sort By Date":
	    		calculatedMailComparison = getDate().compareToIgnoreCase((aCompareMail.getDate()));
	    		break;
	        default:
	        	calculatedMailComparison = 0;
	        	break;            	
	    }
	    	return calculatedMailComparison;
	    }
}