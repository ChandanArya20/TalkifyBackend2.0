package in.ineuron.dto;

import lombok.Data;

@Data
public class ChatMessageRequest {

    private String reqUserId;
    private String chatId;
}
