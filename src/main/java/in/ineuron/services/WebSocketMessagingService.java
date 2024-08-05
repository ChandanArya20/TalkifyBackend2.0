package in.ineuron.services;

import in.ineuron.constant.Constant;
import in.ineuron.dto.MessageResponse;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import lombok.AllArgsConstructor;

import java.util.List;

@Service
@AllArgsConstructor
public class WebSocketMessagingService {

    private final SimpMessagingTemplate messagingTemplate;

    public void sendMessageToUser(Long chatId, MessageResponse messageResponse) {
        messagingTemplate.convertAndSend(Constant.MESSAGE_TOPIC_PREFIX + chatId, messageResponse);
    }

    public void sendChatMessages(Long chatId, List<MessageResponse> messageResponses) {
        messagingTemplate.convertAndSend(Constant.MESSAGES_TOPIC_PREFIX + chatId, messageResponses);
    }
}
