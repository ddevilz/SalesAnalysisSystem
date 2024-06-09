package com.sas.SalesAnalysisSystem.controller;


import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sas.SalesAnalysisSystem.models.User;
import com.sas.SalesAnalysisSystem.repository.UserRepository;

@Controller
@RequestMapping(path="/Signup")

public class SignupController {
		
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
	
	
	@PostMapping(path="/adduser") 
	  public @ResponseBody HttpStatus addNewUser (@RequestParam String username,@RequestParam String nameString
	      , @RequestParam String email, @RequestParam String password ,@RequestParam String address,@RequestParam String mobileNumber) {
			
		User existingUser = userRepository.findByUsername(username);
		User existingEmailUser= userRepository.findByEmail(email);
	    if (existingUser != null  || existingEmailUser !=null) {
	    	return HttpStatus.NOT_ACCEPTABLE;
	    }
		
		String encodedPassword = bCryptPasswordEncoder.encode(password);
		
	    User n = new User(username, nameString ,email, encodedPassword, address, mobileNumber);
	    userRepository.save(n);
	    return HttpStatus.CREATED;
	  }
	
	@PutMapping(path="/update-user/{username}") 
	public @ResponseBody HttpStatus updateUser (@PathVariable String username,
		@RequestParam(required = false) String newUsername,
	    @RequestParam(required = false) String nameString,
	    @RequestParam(required = false) String email, 
	    @RequestParam(required = false) String password,
	    @RequestParam(required = false) String address, 
	    @RequestParam(required = false) String mobileNumber) {

	    User existingUser = userRepository.findByUsername(username);
//	    System.out.println(existingUser.toString());
	    if (existingUser==null) {
	        return HttpStatus.NOT_FOUND;  
	    }
	    
	    if (username != null) {
	        User existingUsernameUser = userRepository.findByUsername(newUsername);
	        if (existingUsernameUser != null && 								!existingUsernameUser.getUserid().equals(existingUser.getUserid())) {
	            return HttpStatus.NOT_ACCEPTABLE;  
	        }
	        existingUser.setUsername(newUsername);
	    }

	    if (nameString != null) {
	        existingUser.setNameString(nameString);
	    }

	    if (email != null) {
	        User existingEmailUser = userRepository.findByEmail(email);
	        if (existingEmailUser != null && 		!existingEmailUser.getUserid().equals(existingUser.getUserid())) {
	            return HttpStatus.NOT_ACCEPTABLE;  
	        }
	        existingUser.setEmail(email);
	    }

	    if (password != null) {
	        String currentPassword = existingUser.getPassword();
	        String encodedPassword = bCryptPasswordEncoder.encode(password);
	        if (password.equals(currentPassword)) {
	            return HttpStatus.NOT_ACCEPTABLE;   
	        }
	        existingUser.setPassword(encodedPassword);
	    }

	    if (address != null) {
	        existingUser.setAddress(address);
	    }

	    if (mobileNumber != null) {
	        existingUser.setMobileNumber(mobileNumber);
	    }
	    System.out.println(existingUser.toString());
	    userRepository.save(existingUser);
	    return HttpStatus.OK;
	}
	
	@GetMapping(path="/all")
	  public @ResponseBody Iterable<User> getAllUsers() {
	    return userRepository.findAll();
	  }

}
