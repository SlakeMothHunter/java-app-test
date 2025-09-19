package security;

import java.io.IOException;

import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;

import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import models.AuthResponseDTO;
import service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler{
	
	private final AuthService authService;
	
	public OAuth2SuccessHandler(@Lazy AuthService authService) {
		super();
		this.authService = authService;
	}

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {
		
		System.out.println("OAuth2 Authentication Done Succesfully");
		
		OAuth2AuthenticationToken token = (OAuth2AuthenticationToken) authentication;
		AuthResponseDTO authResponse = authService.handleOAuth2(token);
		
		 response.setContentType("application/json");
	     response.setCharacterEncoding("UTF-8");
	     
	     new ObjectMapper().writeValue(response.getWriter(), authResponse);

	     
		
	}

}
