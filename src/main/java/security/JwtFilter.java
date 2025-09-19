package security;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtFilter extends OncePerRequestFilter{
	
	
	private final JwtUtil jwtUtil;
	private final UserDetailsManager userDetailsManager;
	
	public JwtFilter(JwtUtil jwtUtil, UserDetailsManager userDetailsManager) {
		super();
		this.jwtUtil = jwtUtil;
		this.userDetailsManager = userDetailsManager;
	}



	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		
		final String requestHeader = request.getHeader("Authorization");
		if (requestHeader == null || !requestHeader.startsWith("Bearer ")) {
			filterChain.doFilter(request, response);
			return;
		}
		
		String token = requestHeader.substring(7);
		if ("".equals(token)) {
			filterChain.doFilter(request, response);
			return;
		}
		String username = jwtUtil.getUsernameFromToken(token);
		
		if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
			try {
				UserDetails userDetails = userDetailsManager.loadUserByUsername(username);
				if (jwtUtil.validateAccessToken(token, userDetails)){
					UsernamePasswordAuthenticationToken authToken =new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
					
					SecurityContextHolder.getContext().setAuthentication(authToken);
				}
			}
			catch (UsernameNotFoundException e) {
				throw new RuntimeException("Username given in token is invalid");
			}
		}
		filterChain.doFilter(request, response);
	}

}
