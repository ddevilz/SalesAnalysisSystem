package com.sas.SalesAnalysisSystem.service;

import org.springframework.stereotype.Service;
 import com.sas.SalesAnalysisSystem.models.User;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class UserService {
	private List<User> store = new ArrayList<>();
	public UserService() { 
    }
	
	public void addUser(String username, String name, String email, String address, String mobileNumber) {
        User newUser = new User(UUID.randomUUID().toString(), username, name, email, address, mobileNumber);
        this.store.add(newUser);
    }
	
	public List<User> getUsers(){
		return this.store;
	}

}
