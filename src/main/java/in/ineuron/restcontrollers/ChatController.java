package in.ineuron.restcontrollers;

import in.ineuron.annotation.ValidateUser;
import in.ineuron.dto.ChatResponse;
import in.ineuron.dto.GroupChatRequest;
import in.ineuron.models.Chat;
import in.ineuron.models.User;
import in.ineuron.services.ChatService;
import in.ineuron.services.TokenStorageService;
import in.ineuron.services.UserService;
import in.ineuron.utils.ChatUtils;
import in.ineuron.utils.UserUtils;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
@AllArgsConstructor
public class ChatController {

    private ChatService chatService;
    private UserService userService;
    private UserUtils userUtils;
    private ChatUtils chatUtils;
    private TokenStorageService tokenService;

    @ValidateUser
    @PostMapping("/single")
    public ResponseEntity<ChatResponse> createSingleChatHandler(@CookieValue("auth-token") String authToken, @RequestParam Long participantId) {

        User user = userService.fetchUserByAuthToken(authToken);
        Chat chat = chatService.createSingleChat(user.getId(), participantId);

        return ResponseEntity.ok(chatUtils.getChatResponse(chat));
    }

    @ValidateUser
    @GetMapping("/{chat-id}")
    public ResponseEntity<Chat> findChatByIdHandler(@PathVariable("chat-id") Long chatId) {

        Chat chat = chatService.findChatById(chatId);
        return ResponseEntity.ok(chat);
    }

    @ValidateUser
    @GetMapping("/all-chat")
    public ResponseEntity<List<ChatResponse>> findAllChatsByUserIdHandler(@CookieValue("auth-token") String authToken) {

        Long userId = tokenService.getUserIdFromToken(authToken);
        List<Chat> chats = chatService.findAllChatsByUserId(userId);

        return ResponseEntity.ok(chatUtils.getChatResponse(chats));
    }

    @ValidateUser
    @PostMapping("/create-group")
    public ResponseEntity<ChatResponse> createGroupHandler(@CookieValue("auth-token") String authToken,
                                                           @RequestBody GroupChatRequest chatReq) {

        Long userId = tokenService.getUserIdFromToken(authToken);
        Chat group = chatService.createGroup(chatReq, userId);

        return ResponseEntity.ok(chatUtils.getChatResponse(group));
    }
    @PutMapping("/{chat-id}/add-user/{user-to-add-id}")
    public ResponseEntity<Chat> addUserToGroupHandler(@CookieValue("auth-token") String authToken,
            @PathVariable("chat-id") Long chatId, @PathVariable("user-to-add-id") Long userToAddId) {

        Long reqUserId = tokenService.getUserIdFromToken(authToken);
        Chat chat = chatService.addUserToGroup(chatId,userToAddId,reqUserId);

        return ResponseEntity.ok(chat);
    }

    @PutMapping("/{chat-id}/remove-user/{user-to-remove-id}")
    public ResponseEntity<Chat> removeUserToGroupHandler(@CookieValue("auth-token") String authToken, @PathVariable("chat-id") Long chatId,
                                                      @PathVariable("user-to-remove-id") Long userToRemoveId) {

        Long reqUserId = tokenService.getUserIdFromToken(authToken);
        Chat chat = chatService.removeUserFromGroup(chatId,userToRemoveId,reqUserId);

        return ResponseEntity.ok(chat);
    }

    @PutMapping("/{chat-id}/rename-group/{new-name}")
    public ResponseEntity<Chat> renameGroupHandler(@CookieValue("auth-token") String authToken, @PathVariable("chat-id") Long chatId,
                                                      @PathVariable("new-name") String newGroupName) {

        Long reqUserId = tokenService.getUserIdFromToken(authToken);
        Chat chat = chatService.renameGroup(chatId,newGroupName, reqUserId);

        return ResponseEntity.ok(chat);
    }

    @DeleteMapping("/{chat-id}/delete")
    public ResponseEntity<Chat> deleteChat(@CookieValue("auth-token") String authToken, @PathVariable("chat-id") Long chatId) {

        Long userId = tokenService.getUserIdFromToken(authToken);
        Chat chat = chatService.deleteChat(chatId, userId);

        return ResponseEntity.ok(chat);
    }

}
