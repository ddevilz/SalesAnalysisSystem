package com.sas.SalesAnalysisSystem.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class User {
	
	public Integer getUserid() {
		return userid;
	}
	public void setUserid(Integer userid) {
		this.userid = userid;
	}
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Integer userid;
    private String username;
    
	private String nameString;
    private String email;
    private String password;
    private String address;
    private String mobileNumber;
	
	public User( String username, String nameString, String email, String password, String address,
			String mobileNumber) {
		this.username = username;
		this.nameString = nameString;
		this.email = email;
		this.password = password;
		this.address = address;
		this.mobileNumber = mobileNumber;
	}
	public String getNameString() {
		return nameString;
	}
	public void setNameString(String nameString) {
		this.nameString = nameString;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getMobileNumber() {
		return mobileNumber;
	}
	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public User() {
		
	}
	public void setName(String name) {
		this.username = name;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getUsername() {
		return this.username;
	}
	public String getEmail() {
		return this.email;
	}
	public String getPassword() {
		return this.password;
	}
	@Override
	public String toString() {
		return "User [userid=" + userid + ", username=" + username + ", nameString=" + nameString + ", email=" + email
				+ ", password=" + password + ", address=" + address + ", mobileNumber=" + mobileNumber + "]";
	}
	
}
