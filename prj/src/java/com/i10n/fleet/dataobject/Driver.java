/**
 * 
 */
package com.i10n.fleet.dataobject;

/**
 * @author HEMANT
 *
 */
public class Driver {
	private long id;       
	private String firstname;
	private String lastname;
	private long licenseno;
	private String photo;    
	private long ownerid;  
	private long groupid;  
	private boolean deleted;
	/**
	 * @return the id
	 */
	public long getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(long id) {
		this.id = id;
	}
	/**
	 * @return the firstname
	 */
	public String getFirstname() {
		return firstname;
	}
	/**
	 * @param firstname the firstname to set
	 */
	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}
	/**
	 * @return the lastname
	 */
	public String getLastname() {
		return lastname;
	}
	/**
	 * @param lastname the lastname to set
	 */
	public void setLastname(String lastname) {
		this.lastname = lastname;
	}
	/**
	 * @return the licenseno
	 */
	public long getLicenseno() {
		return licenseno;
	}
	/**
	 * @param licenseno the licenseno to set
	 */
	public void setLicenseno(long licenseno) {
		this.licenseno = licenseno;
	}
	/**
	 * @return the photo
	 */
	public String getPhoto() {
		return photo;
	}
	/**
	 * @param photo the photo to set
	 */
	public void setPhoto(String photo) {
		this.photo = photo;
	}
	/**
	 * @return the ownerid
	 */
	public long getOwnerid() {
		return ownerid;
	}
	/**
	 * @param ownerid the ownerid to set
	 */
	public void setOwnerid(long ownerid) {
		this.ownerid = ownerid;
	}
	/**
	 * @return the groupid
	 */
	public long getGroupid() {
		return groupid;
	}
	/**
	 * @param groupid the groupid to set
	 */
	public void setGroupid(long groupid) {
		this.groupid = groupid;
	}
	/**
	 * @return the deleted
	 */
	public boolean isDeleted() {
		return deleted;
	}
	/**
	 * @param deleted the deleted to set
	 */
	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}  

}
