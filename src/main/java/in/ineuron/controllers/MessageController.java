package in.ineuron.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import in.ineuron.dto.ChatResponse;
import in.ineuron.dto.MessageRequest;
import in.ineuron.dto.MessageResponse;
import in.ineuron.models.Chat;
import in.ineuron.models.Message;
import in.ineuron.services.MessageService;
import in.ineuron.services.WebSocketMessagingService;
import in.ineuron.utils.ChatUtils;
import in.ineuron.utils.MessageUtils;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Set;

@Controller
@RequestMapping("/api/messages")
@AllArgsConstructor
public class MessageController {

    private final MessageService messageService;
    private final MessageUtils messageUtils;
    private final ChatUtils chatUtils;
    private final ObjectMapper mapper;
    private final WebSocketMessagingService webSocketMessagingService;

    // Handles both text and media message to send
    @PostMapping
    public ResponseEntity<MessageResponse> sendMessage(
            @RequestParam String msgRequest, @RequestParam(required = false) MultipartFile mediaFile) throws IOException {

        // Convert the JSON string to MessageRequest
        MessageRequest msgRequestData = mapper.readValue(msgRequest, MessageRequest.class);
        Message message = null;

        if (mediaFile != null) {
            msgRequestData.setMediaFile(mediaFile);
            message = messageService.sendMediaMessage(msgRequestData);
        } else {
            message = messageService.sendTextMessage(msgRequestData);
        }

        MessageResponse messageResponse = messageUtils.getMessageResponse(message);
        // Send the message to the specific user
        webSocketMessagingService.sendMessageToUser(messageResponse.getChatId(), messageResponse);
        return ResponseEntity.ok(messageResponse);
    }

    @GetMapping("/chats/{chat-id}")
    public ResponseEntity<List<MessageResponse>> getChatMessages(@PathVariable("chat-id") String chatId) {
        List<Message> messages = messageService.getChatMessages(chatId);
        return ResponseEntity.ok(messageUtils.getMessageResponse(messages));
    }

    @DeleteMapping("/all/chats/{chat-id}")
    public ResponseEntity<ChatResponse> deleteAllMessages(@PathVariable("chat-id") String chatId) {
        Chat chat = messageService.deleteAllMessagesByChatId(chatId);
        return ResponseEntity.ok(chatUtils.getChatResponse(chat));
    }

    @DeleteMapping("/chats/{chat-id}")
    public ResponseEntity<ChatResponse> deleteMessagesByChatIds(@PathVariable("chat-id") String chatId, @RequestBody Set<String> messageIds) {
        Chat chat = messageService.deleteMessagesByIds(chatId, messageIds);
        return ResponseEntity.ok(chatUtils.getChatResponse(chat));
    }

    @DeleteMapping("/{message-id}")
    public ResponseEntity<Message> deleteMessage(@PathVariable("message-id") String messageId) {
        Message message = messageService.deleteMessage(messageId);
        return ResponseEntity.ok(message);
    }
}
