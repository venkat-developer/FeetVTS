/**
 * 
 */
package com.i10n.db.entity;

import com.i10n.db.entity.IEntity.IEntity;
import com.i10n.db.entity.primarykey.LongPrimaryKey;

/**
 * @author sreekanth
 *
 */
public class User implements IEntity<User>{
	
	public enum UserRole {
		ADMIN_USER(0),NORMAL_USER(1);
		
		private int val;
		
		UserRole(int value){
			this.val = value;
		}
		
		public int getValue(){
			return val;
		}		
		
		public static UserRole getUserRole(int value){
			UserRole role = NORMAL_USER;
			if(value == ADMIN_USER.getValue()){
				role = ADMIN_USER;
			}else if (value == NORMAL_USER.getValue()){
				role = NORMAL_USER;
			}
			return role;
		}
		
	}

	public enum UserStatus {
		USER_ACTIVE(0),USER_DELETED(1);
		
		private int val;
		
		UserStatus(int value){
			this.val = value;
		}
		public int getValue(){
			return val;
		}
		
		public static UserStatus getUserStatus(int value){
			UserStatus userStatus = USER_ACTIVE;
			if(value == USER_ACTIVE.getValue()){
				userStatus = USER_ACTIVE;
			}else if (value == USER_DELETED.getValue()){
				userStatus = USER_DELETED;
			}
			return userStatus;
		}
		
	}
	
	private LongPrimaryKey id;
	private String firstname;
	private String lastname;
	private String login;
	private String password;
	private Long groupId;
	private UserRole role;
	private UserStatus userStatus;
	private Long ownerId;
	public static final int TNCSC_GROUP_ID=3;
	
	private int offroadCount;
	private int noGPRSCount;

	public User(Long id, String login, String password, UserRole role,
			UserStatus userstatus,Long groupId, Long owner_id,String firstname,String lastname, int offroadCount, int noGPRSCount) {
		super();
		this.id = new LongPrimaryKey(id);
		this.login = login;
		this.password = password;
		this.role = role;
		userStatus = userstatus;
		this.groupId = groupId;
		this.ownerId = owner_id;
		this.firstname=firstname;
		this.lastname=lastname;
		this.offroadCount = offroadCount;
		this.noGPRSCount = noGPRSCount;
	}

	public User(String login, String password, Long ownerId, String firstname, String Lastname, Long groupId) {
		super();
		this.firstname=firstname;
		this.lastname=Lastname;
		this.login = login;
		this.password = password;
		this.groupId = groupId;
		this.role = UserRole.NORMAL_USER;
		this.userStatus = UserStatus.USER_ACTIVE;
		this.ownerId = ownerId;
	}
	public String getFirstname() {
		return firstname;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public String getLastname() {
		return lastname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	public User(Long id,String login, String password, Long ownerId) {
		super();
		this.id=new LongPrimaryKey(id);
		this.login = login;
		this.password = password;
		this.role = UserRole.NORMAL_USER;
		this.userStatus = UserStatus.USER_ACTIVE;
		this.ownerId = ownerId;
	}
	
	public Long getGroupId(){
		return groupId;
	}
	
	public void setGroupId(Long groupId){
		this.groupId = groupId;
	}
	
	public String getLogin() {
		return login;
	}
	public void setLogin(String login) {
		this.login = login;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public UserRole getRole() {
		return role;
	}
	public void setRole(UserRole role) {
		this.role = role;
	}
	public UserStatus getUserStatus() {
		return userStatus;
	}
	public void setUserStatus(UserStatus status) {
		userStatus = status;
	}

	public void setId(LongPrimaryKey id) {
		this.id = id;
	}
	
	public long getId() {
		return this.id.getId();
	}
	
    @Override
    public boolean equalsEntity(User object) {
        return false;
    }

	public void setOwnerId(Long ownerId) {
		this.ownerId = ownerId;
	}

	public Long getOwnerId() {
		return ownerId;
	}
	
	public int getOffroadCount() {
		return offroadCount;
	}

	public void setOffroadCount(int offroadCount) {
		this.offroadCount = offroadCount;
	}

	public int getNoGPRSCount() {
		return noGPRSCount;
	}

	public void setNoGPRSCount(int noGPRSCount) {
		this.noGPRSCount = noGPRSCount;
	}

	@Override
	public String toString(){
		StringBuilder strBuilder = new StringBuilder();
		strBuilder.append(getId()).append("-").append(getLogin()).append("-").append(getPassword()).append("-");
		strBuilder.append(getRole()).append("-").append(getUserStatus());
		return strBuilder.toString();
	}
}
