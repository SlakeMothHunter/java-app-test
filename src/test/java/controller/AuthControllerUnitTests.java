package controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.validation.BindingResult;
import static org.mockito.BDDMockito.given;


import controllers.AuthController;
import models.AuthResponseDTO;
import models.UserDTO;
import security.JwtUtil;
import service.AuthService;

@SpringBootTest(classes = main.CrudAppApplication.class) //Load the Spring Boot application
@AutoConfigureMockMvc
public class AuthControllerUnitTests {
	
	private  BindingResult bindingResult;
	private  AuthService authService;
	private UserDetailsManager userDetailsManager;
	private PasswordEncoder passEncoder;
	private JwtUtil jwtUtil;
	private AuthController authController;
	
	@BeforeEach
	void setup() {
		 bindingResult = mock(BindingResult.class);
	     authService = mock(AuthService.class); 
		 userDetailsManager = mock(UserDetailsManager.class);
		 passEncoder = mock(PasswordEncoder.class);
		 jwtUtil = mock(JwtUtil.class);
		
		authController = new AuthController(authService, userDetailsManager, passEncoder, jwtUtil);
	}
	
	
	
	@Test
	void shouldLoginValidCredentials() throws Exception {
		UserDTO user = new UserDTO("valid-username", "valid-password");
		AuthResponseDTO response = new AuthResponseDTO("fake-token", "fake-refresh-token");
		 
		given(authService.login(any(UserDetails.class))).willReturn(response);
		ResponseEntity<?> result = authController.login(user, bindingResult); //the authController.login function is what is being tested
		
		assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);  // check 200 OK
	    assertThat(result.getBody()).isInstanceOf(AuthResponseDTO.class);  // check body type

	    AuthResponseDTO body = (AuthResponseDTO) result.getBody();
	    assertThat(body.getToken()).isEqualTo("fake-token");
	    assertThat(body.getRefreshToken()).isEqualTo("fake-refresh-token");
		
	}
	
	@Test
	void shouldFailIfValidationErrors() throws Exception{
	    UserDTO user = new UserDTO("", ""); // invalid data
	    
	    when(bindingResult.hasErrors()).thenReturn(true); //We are using the mock bindingResult to return true. If we don't we get an Illegal Argument exception 

	    ResponseEntity<?> result = authController.login(user, bindingResult); //again we are testing this, but this time with empty username and password

	    assertThat(result.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
	}
	
	@Test
	void shouldRegisterNewUser() {
	    UserDTO user = new UserDTO("newuser", "password123");

	    when(userDetailsManager.userExists("newuser")).thenReturn(false);
	    when(passEncoder.encode("password123")).thenReturn("encoded-pass");

	    ResponseEntity<?> result = authController.signup(user, bindingResult);

	    assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
	    assertThat(result.getBody())
	            .isEqualTo("User: newuser registered succesfully. Please login using the /login endpoint");
	    verify(userDetailsManager).createUser(any(UserDetails.class));
	
	
	}
	
	@Test
	void shouldFailIfUserAlreadyExists() {
	    UserDTO user = new UserDTO("existinguser", "password123");

	    when(userDetailsManager.userExists("existinguser")).thenReturn(true);

	    ResponseEntity<?> result = authController.signup(user, bindingResult);

	    assertThat(result.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
	    assertThat(result.getBody()).isEqualTo(user);
	}

}
