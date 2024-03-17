package in.ineuron.dto;

import in.ineuron.models.MessageType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
    private Long reqUserId;

    @NotNull(message = "Chat ID must not be null")
    private Long chatId;

    private String textMessage;

    private  MultipartFile mediaFile;

    private String noteMessage;

}

