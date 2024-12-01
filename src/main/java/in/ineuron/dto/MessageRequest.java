package in.ineuron.dto;

import jakarta.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@ToString
public class MessageRequest {

    @NotNull(message = "User ID must not be null")
    private String reqUserId;

    @NotNull(message = "Chat ID must not be null")
    private String chatId;

    private String textMessage;

    private  MultipartFile mediaFile;

    private String noteMessage;

}

