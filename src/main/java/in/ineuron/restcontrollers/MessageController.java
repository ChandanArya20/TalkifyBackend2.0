package in.ineuron.restcontrollers;

import com.fasterxml.jackson.databind.ObjectMapper;
import in.ineuron.annotation.ValidateUser;
import in.ineuron.dto.ChatMessageRequest;
import in.ineuron.dto.ChatResponse;
import in.ineuron.dto.MessageRequest;
import in.ineuron.dto.MessageResponse;
import in.ineuron.models.Chat;
import in.ineuron.models.Message;
import in.ineuron.services.MessageService;
import in.ineuron.services.TokenStorageService;
import in.ineuron.services.UserService;
import in.ineuron.utils.ChatUtils;
import in.ineuron.utils.MessageUtils;
import in.ineuron.utils.UserUtils;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Set;

@Controller
@RequestMapping("/api/message")
@AllArgsConstructor
public class MessageController {

    private MessageService messageService;
    private UserService userService;
    private UserUtils userUtils;
    private TokenStorageService tokenService;
    private MessageUtils messageUtils;
    private SimpMessagingTemplate messagingTemplate;
    private ChatUtils chatUtils;
    private ObjectMapper mapper;

    //handles both text and media message to send
    @PostMapping("/send")
    @ValidateUser
    public ResponseEntity<MessageResponse> sendMessageRestAPI(
            @CookieValue("auth-token") String authToken, @RequestParam String msgRequest, @RequestParam(required = false) MultipartFile mediaFile ) throws IOException {

        Long reqUserId = tokenService.getUserIdFromToken(authToken);

        // Convert the JSON string to MessageRequest
        MessageRequest msgRequestData = mapper.readValue(msgRequest, MessageRequest.class);
        Message message=null;

        if(mediaFile!=null){
            msgRequestData.setMediaFile(mediaFile);
            message = messageService.sendMediaMessage(msgRequestData, reqUserId);

        }else {
            message = messageService.sendTextMessage(msgRequestData, reqUserId);
        }

        MessageResponse messageResponse = messageUtils.getMessageResponse(message);
//      Send the message to the specific user
        messagingTemplate.convertAndSend("/topic/message"+messageResponse.getChatId(), messageResponse);

        return ResponseEntity.ok(messageResponse);
    }

    // Websocket handler to accept text message
    @MessageMapping("/message/send")
    public void sendMessageWebsocket(
            @Payload MessageRequest msgRequest ) throws IOException {

        Message message = messageService.sendTextMessage(msgRequest, msgRequest.getReqUserId());

        MessageResponse messageResponse = messageUtils.getMessageResponse(message);
        //Send the message to the specific user
        messagingTemplate.convertAndSend("/topic/message"+messageResponse.getChatId(), messageResponse);
    }

    // Websocket handler that returns all messages of a chat by chat id
    @MessageMapping("/chat/messages")
    public void getChatMessages(@Payload ChatMessageRequest req) {

        List<Message> messages = messageService.getChatMessages(req.getChatId(), req.getReqUserId());
        List<MessageResponse> messageResponses = messageUtils.getMessageResponse(messages);
        //Send the message to the specific user
        messagingTemplate.convertAndSend("/topic/messages"+req.getChatId(), messageResponses);
    }

    @GetMapping("/{chat-id}")
    @ValidateUser
    public ResponseEntity<List<MessageResponse>> getChatMessagesHandler(@CookieValue("auth-token") String authToken, @PathVariable("chat-id") Long chatId) {

        Long reqUser = tokenService.getUserIdFromToken(authToken);
        List<Message> messages = messageService.getChatMessages(chatId,reqUser);
        return ResponseEntity.ok(messageUtils.getMessageResponse(messages));
    }

    @DeleteMapping("/delete-all/{chat-id}")
    @ValidateUser
    public ResponseEntity<ChatResponse> deleteAllMessages(@CookieValue("auth-token") String authToken, @PathVariable("chat-id") Long chatId) {

        Long reqUser = tokenService.getUserIdFromToken(authToken);
        Chat chat = messageService.deleteAllMessagesByChatId(chatId, reqUser);
        return ResponseEntity.ok(chatUtils.getChatResponse(chat));
    }

    @DeleteMapping("/delete/{chat-id}")
    @ValidateUser
    public ResponseEntity<ChatResponse> deleteMessages(@CookieValue("auth-token") String authToken, @PathVariable("chat-id") Long chatId, @RequestBody  Set<Long> messageIds) {

        Long reqUser = tokenService.getUserIdFromToken(authToken);
        Chat chat = messageService.deleteMessagesByIds(chatId, messageIds, reqUser);
        return ResponseEntity.ok(chatUtils.getChatResponse(chat));
    }

    @DeleteMapping("/{message-id}")
    @ValidateUser
    public ResponseEntity<Message> deleteMessageHandler(@CookieValue("auth-token") String authToken, @PathVariable("message-id") Long messageId) {

        Long reqUser = tokenService.getUserIdFromToken(authToken);
        Message message = messageService.deleteMessage(messageId, reqUser);
        return ResponseEntity.ok(message);
    }



}