package in.ineuron.restcontrollers;

import in.ineuron.annotation.ValidateUser;
import in.ineuron.dto.MessageRequest;
import in.ineuron.dto.MessageResponse;
import in.ineuron.models.Message;
import in.ineuron.services.MessageService;
import in.ineuron.services.TokenStorageService;
import in.ineuron.services.UserService;
import in.ineuron.utils.MessageUtils;
import in.ineuron.utils.UserUtils;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/api/message")
@ValidateUser
@AllArgsConstructor
public class MessageController {

    private MessageService messageService;
    private UserService userService;
    private UserUtils userUtils;
    private TokenStorageService tokenService;
    private MessageUtils messageUtils;
    private SimpMessagingTemplate messagingTemplate;

    @PostMapping("/send")
    @ValidateUser
    public ResponseEntity<MessageResponse> sendMessageHandler(
            @CookieValue("auth-token") String authToken,
            @RequestBody MessageRequest msgReq ) {

        Long reqUser = tokenService.getUserIdFromToken(authToken);
        Message message = messageService.sendMessage(msgReq, reqUser);

        MessageResponse messageResponse = messageUtils.getMessageResponse(message);

        // Send the message to the specific user
        messagingTemplate.convertAndSend("/topic/message"+msgReq.getChatId(), messageResponse);

        return ResponseEntity.ok(messageResponse);
    }

    @GetMapping("/chat/{chat-id}")
    @ValidateUser
    public ResponseEntity<List<MessageResponse>> getChatsMessageHandler(@CookieValue("auth-token") String authToken, @PathVariable("chat-id") Long chatId) {

        Long reqUser = tokenService.getUserIdFromToken(authToken);
        List<Message> messages = messageService.getChatsMessage(chatId,reqUser);
        return ResponseEntity.ok(messageUtils.getMessageResponse(messages));
    }

    @DeleteMapping("/{message-id}")
    @ValidateUser
    public ResponseEntity<String> deleteMessageHandler(@CookieValue("auth-token") String authToken, @PathVariable("message-id") Long messageId) {

        Long reqUser = tokenService.getUserIdFromToken(authToken);
        messageService.deleteMessage(messageId,reqUser);
        return ResponseEntity.ok("Message deleted successfully...");
    }



}