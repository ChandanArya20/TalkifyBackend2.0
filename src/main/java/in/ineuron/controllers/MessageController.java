package in.ineuron.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import in.ineuron.annotation.ValidateUser;
import in.ineuron.constant.Constant;
import in.ineuron.dto.ChatResponse;
import in.ineuron.dto.MessageRequest;
import in.ineuron.dto.MessageResponse;
import in.ineuron.models.Chat;
import in.ineuron.models.Message;
import in.ineuron.services.MessageService;
import in.ineuron.services.TokenStorageService;
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
    private final TokenStorageService tokenService;
    private final MessageUtils messageUtils;
    private final ChatUtils chatUtils;
    private final ObjectMapper mapper;
    private final WebSocketMessagingService webSocketMessagingService;

    // Handles both text and media message to send
    @PostMapping
    @ValidateUser
    public ResponseEntity<MessageResponse> sendMessage(
            @CookieValue(Constant.AUTH_TOKEN) String authToken, @RequestParam String msgRequest, @RequestParam(required = false) MultipartFile mediaFile ) throws IOException {

        Long reqUserId = tokenService.getUserIdFromToken(authToken);

        // Convert the JSON string to MessageRequest
        MessageRequest msgRequestData = mapper.readValue(msgRequest, MessageRequest.class);
        Message message = null;

        if (mediaFile != null) {
            msgRequestData.setMediaFile(mediaFile);
            message = messageService.sendMediaMessage(msgRequestData, reqUserId);
        } else {
            message = messageService.sendTextMessage(msgRequestData, reqUserId);
        }

        MessageResponse messageResponse = messageUtils.getMessageResponse(message);
        // Send the message to the specific user
        webSocketMessagingService.sendMessageToUser(messageResponse.getChatId(), messageResponse);

        return ResponseEntity.ok(messageResponse);
    }

    @GetMapping("/chats/{chat-id}")
    @ValidateUser
    public ResponseEntity<List<MessageResponse>> getChatMessages(@CookieValue(Constant.AUTH_TOKEN) String authToken, @PathVariable("chat-id") Long chatId) {
        Long reqUser = tokenService.getUserIdFromToken(authToken);
        List<Message> messages = messageService.getChatMessages(chatId, reqUser);
        return ResponseEntity.ok(messageUtils.getMessageResponse(messages));
    }

    @DeleteMapping("/all/chats/{chat-id}")
    @ValidateUser
    public ResponseEntity<ChatResponse> deleteAllMessages(@CookieValue(Constant.AUTH_TOKEN) String authToken, @PathVariable("chat-id") Long chatId) {
        Long reqUser = tokenService.getUserIdFromToken(authToken);
        Chat chat = messageService.deleteAllMessagesByChatId(chatId, reqUser);
        return ResponseEntity.ok(chatUtils.getChatResponse(chat));
    }

    @DeleteMapping("/chats/{chat-id}")
    @ValidateUser
    public ResponseEntity<ChatResponse> deleteMessagesByChatIds(@CookieValue(Constant.AUTH_TOKEN) String authToken, @PathVariable("chat-id") Long chatId, @RequestBody Set<Long> messageIds) {
        Long reqUser = tokenService.getUserIdFromToken(authToken);
        Chat chat = messageService.deleteMessagesByIds(chatId, messageIds, reqUser);
        return ResponseEntity.ok(chatUtils.getChatResponse(chat));
    }

    @DeleteMapping("/{message-id}")
    @ValidateUser
    public ResponseEntity<Message> deleteMessage(@CookieValue(Constant.AUTH_TOKEN) String authToken, @PathVariable("message-id") Long messageId) {
        Long reqUser = tokenService.getUserIdFromToken(authToken);
        Message message = messageService.deleteMessage(messageId, reqUser);
        return ResponseEntity.ok(message);
    }
}
