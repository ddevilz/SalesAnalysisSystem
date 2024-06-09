
package com.sas.SalesAnalysisSystem.controller;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;

import com.sas.SalesAnalysisSystem.repository.PasswordResetTokenRepository;
import com.sas.SalesAnalysisSystem.repository.UserRepository;
import com.sas.SalesAnalysisSystem.models.PasswordResetToken;
import com.sas.SalesAnalysisSystem.models.User;

import jakarta.mail.internet.MimeMessage;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
 
@RequestMapping("/auth/password")
@Controller
public class PasswordResetTokenController {
	
	 	@Autowired
	    private UserRepository userRepository;
	    
	    @Autowired
	    private PasswordResetTokenRepository passwordResetTokenRepository;
	    
	    @Autowired
	    private JavaMailSender javaMailSender;
	    
		@Autowired
	    private BCryptPasswordEncoder bCryptPasswordEncoder;
	    
	    @PostMapping("/forgotPassword")
	    @Transactional(rollbackFor = Exception.class)
	    public ResponseEntity<String> forgotPassword(@RequestParam String email) {
	        User user = userRepository.findByEmail(email);
	        if (user == null) {
	            return ResponseEntity.notFound().build();
	        }
	        String token = UUID.randomUUID().toString();
	        LocalDateTime expirationTime = LocalDateTime.now().plusMinutes(10);
	        PasswordResetToken passwordResetToken = new PasswordResetToken(user, token, expirationTime);
	        passwordResetTokenRepository.save(passwordResetToken);
	        try {
	            sendEmail(email, token);
	            return ResponseEntity.ok("Password reset email sent successfully");
	        } catch (Exception e) {
	            Logger logger = LoggerFactory.getLogger(PasswordResetTokenController.class);
	            logger.error("Error sending email for password reset", e);
	            throw new RuntimeException("Failed to send password reset email");
	        }
	    }

	    public void sendEmail(String email, String token) {
	    	try {
	    		MimeMessage message=javaMailSender.createMimeMessage();
		    	MimeMessageHelper helper= new MimeMessageHelper(message,true);
		    	helper.setTo(email);
		    	helper.setSubject("Password Reset Mail from Celltone");
		    	helper.setText("Dear User, \n\n" + "Please click on the following link to reset your password:  https://lms-celltone.vercel.app/reset-password"
		    			+ "?token="+token);
		    	javaMailSender.send(message);
				
			} catch (Exception e) {
				 e.printStackTrace();
			}
	    }
	    
	    @GetMapping("/resetPassword")
	    public ResponseEntity<String> resetPassword(@RequestParam String token, @RequestParam String newPassword) {
	        Optional<PasswordResetToken> optionalToken = passwordResetTokenRepository.findByToken(token);
	        if (optionalToken.isPresent()) {
	            PasswordResetToken passwordResetToken = optionalToken.get();
	            if (passwordResetToken.isTokenExpired()) {
	                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Reset token has expired");
	            }
	            User user = passwordResetToken.getUser();
	    		String encodedPassword = bCryptPasswordEncoder.encode(newPassword);
	            user.setPassword(encodedPassword);
	            userRepository.save(user);
	            passwordResetTokenRepository.delete(passwordResetToken);
	            return ResponseEntity.ok("Password reset successfully");
	        } else {
	            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid reset token");
	        }
	    }
}
