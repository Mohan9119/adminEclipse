package com.example.admin;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
// signup containing 4 feilds all r storing in database
@Entity
public class AppUser {
	
	private String firstName;
	private String lastName;
	@Id
	private String userName;  // email Account
	private String password;
	private String activationKey;
	private Integer status; //  initial status will be 1 and changed to 2 after verification
	@OneToOne(cascade = CascadeType.ALL, mappedBy = "appUser")
	private Address address; 
	public String getActivationKey() {
		return activationKey;
	}
	public void setActivationKey(String activationKey) {
		this.activationKey = activationKey;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getUsername() {
		return userName;
	}
	public void setUsername(String username) {
		this.userName = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public Address getAddress() {
		return address;
	}
	public void setAddress(Address address) {
		this.address = address;
	}
	

}
