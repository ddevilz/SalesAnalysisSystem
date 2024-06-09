package com.sas.SalesAnalysisSystem.models;


import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;

@Entity
public class PasswordResetToken {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(targetEntity = User.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "user_id")
    private User user;

    private String token;

    private LocalDateTime expirationTime;

    public PasswordResetToken() {
    }
    

	public User getUser() {
		return user;
	}


	public void setUser(User user) {
		this.user = user;
	}


	public String getToken() {
		return token;
	}


	public void setToken(String token) {
		this.token = token;
	} 
	
	public boolean isTokenExpired() {
	    return expirationTime.isBefore(LocalDateTime.now());
	}



	public LocalDateTime getExpirationTime() {
		return expirationTime;
	}


	public void setExpirationTime(LocalDateTime expirationTime) {
		this.expirationTime = expirationTime;
	}


	public PasswordResetToken(User user, String token, LocalDateTime expirationTime) {
		super();
		this.user = user;
		this.token = token;
		this.expirationTime = expirationTime;
	}


    

}
