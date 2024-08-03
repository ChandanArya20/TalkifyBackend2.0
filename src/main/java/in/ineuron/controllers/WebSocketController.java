package in.ineuron.controllers;

import in.ineuron.dto.ChatMessageRequest;
import in.ineuron.dto.MessageRequest;
import in.ineuron.dto.MessageResponse;
import in.ineuron.models.Message;
import in.ineuron.services.MessageService;
import in.ineuron.services.WebSocketMessagingService;
import in.ineuron.utils.MessageUtils;
import lombok.AllArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.util.List;

@Controller
@AllArgsConstructor
public class WebSocketController {

    private final MessageService messageService;
    private final MessageUtils messageUtils;
    private final WebSocketMessagingService webSocketMessagingService;

    // WebSocket handler to accept text message
    @MessageMapping("/messages/send")
    public void sendMessageWebSocket(@Payload MessageRequest msgRequest) throws IOException {
        Message message = messageService.sendTextMessage(msgRequest, msgRequest.getReqUserId());
        MessageResponse messageResponse = messageUtils.getMessageResponse(message);
        webSocketMessagingService.sendMessageToUser(messageResponse.getChatId(), messageResponse);
    }

    // WebSocket handler that returns all messages of a chat by chat id
    @MessageMapping("/messages/chats")
    public void getChatMessages(@Payload ChatMessageRequest req) {
        List<Message> messages = messageService.getChatMessages(req.getChatId(), req.getReqUserId());
        List<MessageResponse> messageResponses = messageUtils.getMessageResponse(messages);
        webSocketMessagingService.sendChatMessages(req.getChatId(), messageResponses);
    }
}
