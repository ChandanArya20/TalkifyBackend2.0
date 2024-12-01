package in.ineuron.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MessageResponse {

    private String id;

    private String messageType;

    private String textMessage;

    private String mediaURL;

    private String noteMessage;

    private String fileName;

    private Long fileSize;

    private LocalDateTime creationTime;

    private UserResponse createdBy;

    private String chatId;

}
