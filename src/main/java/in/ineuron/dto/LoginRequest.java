package in.ineuron.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import jakarta.validation.constraints.*;

@Getter
@ToString
public class LoginRequest {

	@NotBlank(message = "Email is required, please enter email")
	@Email(regexp = "^[a-zA-Z0-9.!#$%&’*+/=?^_`{|}~-]+@[a-zA-Z0-9-]+(?:\\.[a-zA-Z0-9-]+)*$",
	message="invalid email!")
	private String email;


	@NotBlank(message = "Password should not be empty or null")
    @Pattern(regexp = "^(?!.*\\s).*$",
             message = "Invalid password")
	@Size(min = 8, message = "Password should have minimum 8 characters")
	private String password;

}
