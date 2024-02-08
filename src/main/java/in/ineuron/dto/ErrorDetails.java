package in.ineuron.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ErrorDetails {

    private String error;
    private String message;
    private LocalDateTime timeStamp;
}
