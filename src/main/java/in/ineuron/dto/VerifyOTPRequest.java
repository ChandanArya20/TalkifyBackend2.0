package in.ineuron.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;

@Data
public class VerifyOTPRequest {
    private String email;

    @JsonProperty("OTP")
    private String OTP;
}
