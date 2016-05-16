package jclientpackage;
public final class AddressAccount
                             implements Comparable<AddressAccount>  {
	private String fFirstName;
	private String fLastName;
	private String fMiddleInitial;
	private String fAddress;
	private String fCurrentSortMode;
	
	public AddressAccount(String aFirstName, String aLastName, String aMiddleInitial, String aAddress){
		this.fFirstName = aFirstName;
		this.fLastName = aLastName;
		this.fMiddleInitial = aMiddleInitial;
		this.fAddress = aAddress;
	}
	
		public final void setFirstName(String aFirstName) {
			this.fFirstName = aFirstName;
		}
		
		public final void setLastName(String aLastName) {
			this.fLastName = aLastName;
		}
		
		public final void setMiddleInitial(String aMiddleInitial) {
			this.fMiddleInitial = aMiddleInitial;
		}
		
		public final void setAddress(String aAddress) {
			this.fAddress = aAddress;
		}
		
		public final void setSortMode(String aSortMode) {
			this.fCurrentSortMode = aSortMode;
		}
	
	    public final String getFirstName() {
			return this.fFirstName;
		}
		
		public final String getLastName() {
			return this.fLastName;
		}
	
		public final String getMiddleInitial() {
			return this.fMiddleInitial;
		}
	
		public final String getAddress() {
			return this.fAddress;
		}
	
        @Override
        public final int compareTo(AddressAccount aCompareAccount) {
        	int calculatedComparison = 0;
        	
        	switch(fCurrentSortMode)  {
        	case "Sort By First Name":
        		calculatedComparison = getFirstName().compareToIgnoreCase(aCompareAccount.getFirstName());
        		break;
        	case "Sort By Last Name":
        		calculatedComparison = getLastName().compareToIgnoreCase(aCompareAccount.getLastName());
        		break;
        	case "Sort By Middle Initial":
        		calculatedComparison = getMiddleInitial().compareToIgnoreCase(aCompareAccount.getMiddleInitial());
        		break;
        	case "Sort By Address":
        		calculatedComparison = getAddress().compareToIgnoreCase(aCompareAccount.getAddress());
        		break;
        	}
        	
        	return calculatedComparison;
        }		
}
