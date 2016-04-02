package com.i10n.fleet.util;

import java.util.LinkedList;

import com.i10n.db.entity.Address;


/**
 * @author HEMANT
 *
 */
public class AddressUtils {
	
	private static AddressUtils instance;
		
	public static AddressUtils getInstance () {			
		if (instance == null) {
			instance = new AddressUtils ();
		}		
		return instance;
	}
	
	//Used to store the idle point computed  from moduleupdateHandler
	LinkedList<Address> addressList=new LinkedList<Address>();

	/**
	 * @return the idlePointList
	 */
	public LinkedList<Address> getAddressList() {
		return addressList;
	}

	/**
	 * @param addressLists the idlePointList to set
	 */
	public void setAddressList(LinkedList<Address> addressLists) {
		synchronized (addressList) {
			addressList = addressLists;
		}
		
	}
	
	
	public LinkedList<Address> getAddressLists(){
		synchronized (addressList) {
			return addressList;	
		}
	}

}
