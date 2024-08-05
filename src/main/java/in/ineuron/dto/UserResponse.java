package in.ineuron.dto;

import in.ineuron.models.Role;
import jakarta.persistence.OneToMany;
import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
public class UserResponse {
	
	private Long id;

	private String name;
	
	private String phone;

	private String userid;

	private String profileImage;

	private String about;

//	boolean active;

//	private List<String> roles;
	
}
