package in.ineuron.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserLoginResponse {

	private String jwtToken;
	private UserResponse user;
	
}
