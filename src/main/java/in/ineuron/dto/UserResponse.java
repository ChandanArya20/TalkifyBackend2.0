package in.ineuron.dto;

import lombok.Data;

@Data
public class UserResponse {
	
	private Long id;

	private String name;
	
	private String phone;

	private String userid;

	private String profileImage;

	private String about;
	
}
