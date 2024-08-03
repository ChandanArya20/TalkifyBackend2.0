package in.ineuron.dto;


import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class UserRequest {

	@NotBlank(message = "Name should not be empty or null")
	@Size(min=3, message = "Name should be greater than 2")
	private String name;

	private String phone;

	@NotBlank(message = "Email should not be empty or null")
	@Email(regexp = "^[a-zA-Z0-9.!#$%&’*+/=?^_`{|}~-]+@[a-zA-Z0-9-]+(?:\\.[a-zA-Z0-9-]+)*$",
	message="invalid email!")
	private String email;

	@NotBlank(message = "User id should not be empty or null")
	@Size(min = 3, message = "Userid should be minimum 3 characters")
	private String userid;

	private String profileImage;

	@NotBlank(message = "Password should not be empty or null")
    @Pattern(regexp = "^(?!.*\\s).*$",
             message = "Space is not allowed")
	@Size(min = 8, message = "Password should have minimum 8 characters")
	private String password;

	@AssertTrue(message = "Invalid phone format")
	public boolean isPhoneValid() {
		if (phone == null || phone.isEmpty()) {
			phone=null;
			return true;
		}

		// Perform the validation only if phone is not null and not empty
		return phone.matches("^[6-9][0-9]*$") && phone.length() == 10;
	}

}
