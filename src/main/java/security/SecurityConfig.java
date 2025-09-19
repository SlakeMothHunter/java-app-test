package security;

import static org.springframework.security.config.Customizer.withDefaults;

import java.util.logging.Logger;

import javax.sql.DataSource;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;


@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
	
	private final DataSource dataSource;
	private Logger logger =  Logger.getLogger(SecurityConfig.class.getName());
	
	
	private final OAuth2SuccessHandler oAuthSuccessHandler;

	
	public SecurityConfig(DataSource dataSource, OAuth2SuccessHandler oAuthSuccessHandler) {
		super();
		this.dataSource = dataSource;
		this.oAuthSuccessHandler = oAuthSuccessHandler;
	}


	@Bean
	SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http, JwtFilter jwtFilter) throws Exception {
		http.csrf(csrf -> csrf.disable());
		http.sessionManagement(sessionConfig -> sessionConfig.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
		http.authorizeHttpRequests((requests) -> requests
				.requestMatchers("/auth/signup", "/auth/login", "/auth/signup/", "/auth/login/", "/auth/refresh", "/auth/refresh/").permitAll()
				.anyRequest().authenticated());
		http.httpBasic(withDefaults());
		http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
		http.oauth2Login(oAuth2 -> oAuth2.failureHandler((request, response, exception) -> {logger.info("OAuth2 Error");}).successHandler(oAuthSuccessHandler));
		return http.build();
	}

	
	@Bean
	PasswordEncoder passEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	@Bean
	UserDetailsManager jdbcUserDetailsManager() {
		return new JdbcUserDetailsManager(dataSource);
	}
	
	
	//This is what has the authenticate method.
	@Bean
	AuthenticationManager authManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
		
	}
	
	
	

}
