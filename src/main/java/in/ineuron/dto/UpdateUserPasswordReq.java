package in.ineuron.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class UpdateUserPasswordReq {

    @NotBlank(message = "Email is required, please enter email")
    @Email(regexp = "^[a-zA-Z0-9.!#$%&’*+/=?^_`{|}~-]+@[a-zA-Z0-9-]+(?:\\.[a-zA-Z0-9-]+)*$",
            message="invalid email!")
    private String email;

    @NotBlank(message = "Password should not be empty or null")
    @Pattern(regexp = "^(?!.*\\s).*$",
            message = "Invalid password")
    private String newPassword;
}
