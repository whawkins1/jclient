package jclientpackage;
import java.util.List;
import java.util.ArrayList;

import javax.swing.table.AbstractTableModel;


public final class AddressBookTableModel extends AbstractTableModel{
	private static final long serialVersionUID = -7499767764548052684L;
	private static final int LAST_NAME_INDEX = 0;
	private static final int FIRST_NAME_INDEX = 1;
	private static final int MIDDLE_INITIAL_INDEX = 2;
	private static final int ADDRESS_INDEX = 3;
	
	private static final String[] fColumns = {"Last", "First", "MI", "Address"};
	private List<AddressAccount> fAddressList; 
	
	public AddressBookTableModel() {
		fAddressList = new ArrayList<AddressAccount>();		
	}
	
		@Override
		public final String getColumnName(int aCol) {
			return fColumns[aCol];
		}
		
		@Override
		public final int getColumnCount() {
			return fColumns.length;
		}
		
		@Override
		public final int getRowCount() {
			return fAddressList.size();
		}
		
		@Override
		public final Class<?> getColumnClass(int aColumn) {
			return (getValueAt(0, aColumn).getClass());
		}
		
		@Override
		public final boolean isCellEditable(int aRow, int aCol) {
			return false;
		}
		
		@Override
		public final void setValueAt(Object aValue, int aRow, int aColumn) {
			
		}
		
		public final void setAddressList(List<AddressAccount> aAddressList) {
			this.fAddressList = aAddressList;
			fireTableRowsUpdated(0, fAddressList.size() - 1);
		}
						
		public final void addAddress(AddressAccount aAccount) {
			fAddressList.add(aAccount);
			fireTableRowsInserted(fAddressList.size() - 1, fAddressList.size() - 1);;
		}
		
		public final void removeAddress(int aRemoveIndex) {
			fAddressList.remove(aRemoveIndex);
			fireTableRowsDeleted(aRemoveIndex, aRemoveIndex);
		}
		
		public final void removeMultiAddress(int[] aRemoveIndexes) {
			for(int r = aRemoveIndexes.length - 1; r >= 0; r--) {
				fAddressList.remove(aRemoveIndexes[r]);
			}			
			fireTableRowsDeleted(aRemoveIndexes[0], aRemoveIndexes[aRemoveIndexes.length - 1]);
		}
		
		public final List<AddressAccount> getAddressList() {
			return this.fAddressList;
		}
		
		public final Object getValueAt(int aRow, int aCol) {
			AddressAccount aa = fAddressList.get(aRow);
			
			switch(aCol) {
			case LAST_NAME_INDEX:
				return aa.getLastName();
			case FIRST_NAME_INDEX:
				return aa.getFirstName();
			case MIDDLE_INITIAL_INDEX:
				return aa.getMiddleInitial();
			case ADDRESS_INDEX:
				return aa.getAddress();
				default:
					return new Object();
			}
		}
		
		
}
