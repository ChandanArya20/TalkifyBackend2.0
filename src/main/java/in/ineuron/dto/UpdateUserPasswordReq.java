package in.ineuron.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class UpdateUserPasswordReq {

    @NotBlank(message = "Password should not be empty or null")
    @Pattern(regexp = "^(?!.*\\s).*$",
            message = "Invalid password")
    private String newPassword;
}
