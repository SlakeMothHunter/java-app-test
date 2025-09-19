package models;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class UserDTO {
	@NotBlank(message = "Username cannot be blank")
	@Size(min = 3, max = 100, message = "minimum size must be 6, maximum 100")
	private String username;
	
	@NotBlank(message = "Password cannot be blank")
	@Size(min = 6, max = 30, message = "minimum size must be 6, maximum 100")
	private String password;
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public UserDTO(
			@NotBlank(message = "Username cannot be blank") @Size(min = 3, max = 100, message = "minimum size must be 6, maximum 100") String username,
			@NotBlank(message = "Password cannot be blank") @Size(min = 6, max = 30, message = "minimum size must be 6, maximum 100") String password) {
		super();
		this.username = username;
		this.password = password;
	}
	
	
}