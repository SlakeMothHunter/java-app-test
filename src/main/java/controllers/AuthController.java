package controllers;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import models.UserDTO;
import security.JwtUtil;
import service.AuthService;

@RestController
@RequestMapping("/auth")
public class AuthController {
	
	private final AuthService authService;
	private final UserDetailsManager userDetailsManager;
	private final PasswordEncoder passEncoder;
	private final JwtUtil jwtUtil;

	public AuthController(AuthService authService, UserDetailsManager userDetailsManager, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
		super();
		this.authService = authService;
		this.userDetailsManager = userDetailsManager;
		this.passEncoder = passwordEncoder;
		this.jwtUtil = jwtUtil;
	}
	
	@PostMapping({"/login", "/login/"})
	public ResponseEntity<?> login(@Valid @RequestBody UserDTO request, BindingResult bindingResult){
		
		if (bindingResult.hasErrors()) {
			Map<String, String> errorMap = new HashMap<>();
			bindingResult.getFieldErrors().forEach(error -> errorMap.put(error.getField(), error.getDefaultMessage()));
			return ResponseEntity.badRequest().body(errorMap);
		}
		
		UserDetails user = User.withUsername(request.getUsername()).password(request.getPassword()).build();
		return ResponseEntity.ok(authService.login(user));
		
	}

	@PostMapping({"/signup", "/signup/"})
	public ResponseEntity<?> signup(@Valid @RequestBody UserDTO request, BindingResult bindingResult){
		
		if (bindingResult.hasErrors()) {
			Map<String, String> errorMap = new HashMap<>();
			bindingResult.getFieldErrors().forEach(error -> errorMap.put(error.getField(), error.getDefaultMessage()));
			return ResponseEntity.badRequest().body(errorMap);
		}
		
		//First check if user exists
		if (userDetailsManager.userExists(request.getUsername())) {
			return ResponseEntity.badRequest().body(request);
		}
		
		if (request.getUsername() == null || request.getUsername().trim().isEmpty()) {
			throw new IllegalArgumentException("Username cannot be empty");
		}
		
		if (request.getPassword() == null  || request.getPassword().trim().isEmpty()){
			throw new IllegalArgumentException("Password cannot be empty");
			
		}
		
		
		//Encoding the plain text password string
		String encodedPassword = passEncoder.encode(request.getPassword());
		
		//Generating the UserDetails object. We will pass this to the createUser method of userDetailsManager object.
		//And since our userDetailsManager object is really a JdbcUserDetailsManager object, it will handle everything
		UserDetails user = User.withUsername(request.getUsername()).password(encodedPassword).roles("USER").build();
		
		//create user (and write to db)
		userDetailsManager.createUser(user);
		
		return ResponseEntity.ok("User: " + user.getUsername() + " registered succesfully. Please login using the /login endpoint");
		
	}
	
	@PostMapping({"/refresh", "/refresh/"})
	public ResponseEntity<?> refresh(@RequestHeader("Refresh") String authRefreshHeader){
		if (authRefreshHeader == null || !authRefreshHeader.startsWith("Bearer ")) {
			return ResponseEntity.badRequest().body("Invalid or malformed refresh token");
		}
		
		String refreshToken = authRefreshHeader.substring(7);
		
		if(!jwtUtil.validateRefreshToken(refreshToken)) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired refresh token");
		}
		
		try {
			String username = jwtUtil.getUsernameFromToken(refreshToken);
	        UserDetails user = userDetailsManager.loadUserByUsername(username);
	       
	        String newAccessToken = jwtUtil.generateToken(user);

	        return ResponseEntity.ok(newAccessToken);
		}
		catch (UsernameNotFoundException e) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found in the database");
		}
	}
}
