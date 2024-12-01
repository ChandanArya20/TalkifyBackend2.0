package in.ineuron.dto;

import in.ineuron.models.Role;
import jakarta.persistence.OneToMany;
import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
public class UserResponse {
	
	private String id;

	private String name;
	
	private String phone;

	private String email;

	private String userid;

	private String profileImage;

	private String about;

//	boolean active;

//	private List<String> roles;
	
}
