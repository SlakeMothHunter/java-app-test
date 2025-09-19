package service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;

import security.JwtUtil;
import security.RandomPassGen;
import models.AuthResponseDTO;

@Service
public class AuthService {
	
	private final AuthenticationManager authManager;
	private final JwtUtil jwtUtil;
	private final UserDetailsManager userDetailsManager;
	
	public AuthService(AuthenticationManager authManager, JwtUtil jwtUtil, UserDetailsManager userDetailsManager) {
		super();
		this.authManager = authManager;
		this.jwtUtil = jwtUtil;
		this.userDetailsManager = userDetailsManager;
	}
	
	
	public AuthResponseDTO login(UserDetails request){
		
		try {
			Authentication auth = authManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
		
			UserDetails userDetails  = (UserDetails) auth.getPrincipal();
			String token = jwtUtil.generateToken(userDetails);
			String refreshToken = jwtUtil.generateRefreshToken(userDetails);
			
			return new AuthResponseDTO(token, refreshToken);
		}
		catch (BadCredentialsException e) {throw new RuntimeException("Invalid credentials");}
	}


	public AuthResponseDTO handleOAuth2(OAuth2AuthenticationToken token) {
		OAuth2User oAuthUser = token.getPrincipal();
		String email_username = oAuthUser.getAttribute("email");
		System.out.println(email_username);
	    if (!userDetailsManager.userExists(email_username)) {
	    	String randomPassword = RandomPassGen.generateRandomPassword(30);

	        UserDetails newUser = User.withUsername(email_username)
	                .password(randomPassword)  // dummy password (noop = no encoding)
	                .roles("USER")
	                .build();

	        userDetailsManager.createUser(newUser);
	        System.out.println("New OAuth2 user added: " + email_username);
	    }
	    
	    UserDetails user = userDetailsManager.loadUserByUsername(email_username);
	    
		String accessToken = jwtUtil.generateToken(user);
		String refreshToken = jwtUtil.generateRefreshToken(user);
		
		System.out.println("accessToken: " + accessToken);
		System.out.println("refreshToken: " + refreshToken); 
		
		return new AuthResponseDTO(accessToken, refreshToken);
	}
}
