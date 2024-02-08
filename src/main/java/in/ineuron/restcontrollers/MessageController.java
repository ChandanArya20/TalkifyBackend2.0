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
import org.springframework.http.ResponseEntity;
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

    @PostMapping("/send")
    @ValidateUser
    public ResponseEntity<MessageResponse> sendMessageHandler(@CookieValue("auth-token") String authToken, @RequestBody MessageRequest msgReq) {

        Long reqUser = tokenService.getUserIdFromToken(authToken);
        Message message = messageService.sendMessage(msgReq,reqUser);
        return ResponseEntity.ok(messageUtils.getMessageResponse(message));
    }

    @GetMapping("/chat/{chat-id}")
    @ValidateUser
    public ResponseEntity<List<Message>> getChatsMessageHandler(@CookieValue("auth-token") String authToken, @PathVariable("chat-id") Long chatId) {

        Long reqUser = tokenService.getUserIdFromToken(authToken);
        List<Message> message = messageService.getChatsMessage(chatId,reqUser);
        return ResponseEntity.ok(message);
    }

    @DeleteMapping("/{message-id}")
    @ValidateUser
    public ResponseEntity<String> deleteMessageHandler(@CookieValue("auth-token") String authToken, @PathVariable("message-id") Long messageId) {

        Long reqUser = tokenService.getUserIdFromToken(authToken);
        messageService.deleteMessage(messageId,reqUser);
        return ResponseEntity.ok("Message deleted successfully...");
    }



}