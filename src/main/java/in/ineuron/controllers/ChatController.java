package in.ineuron.controllers;

import in.ineuron.dto.ChatResponse;
import in.ineuron.dto.GroupChatRequest;
import in.ineuron.models.Chat;
import in.ineuron.services.ChatService;
import in.ineuron.utils.ChatUtils;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chats")
@AllArgsConstructor
public class ChatController {

    private ChatService chatService;
    private ChatUtils chatUtils;

    @PostMapping
    public ResponseEntity<ChatResponse> createSingleChat(@RequestParam String participantId) {
        Chat chat = chatService.createSingleChat(participantId);
        return ResponseEntity.ok(chatUtils.getChatResponse(chat));
    }

    @GetMapping("/{chat-id}")
    public ResponseEntity<Chat> findChatById(@PathVariable("chat-id") String chatId) {
        Chat chat = chatService.findChatById(chatId);
        return ResponseEntity.ok(chat);
    }

    @GetMapping
    public ResponseEntity<List<ChatResponse>> findAllChatsByUserId() {
        List<Chat> chats = chatService.findAllChats();
        return ResponseEntity.ok(chatUtils.getChatResponse(chats));
    }

    @PostMapping("/groups")
    public ResponseEntity<ChatResponse> createGroup(@RequestBody GroupChatRequest chatReq) {
        Chat group = chatService.createGroup(chatReq);
        return ResponseEntity.ok(chatUtils.getChatResponse(group));
    }

    @PutMapping("/{chat-id}/members/{user-to-add-id}")
    public ResponseEntity<Chat> addUserToGroup(@PathVariable("chat-id") String chatId,
                                               @PathVariable("user-to-add-id") String userToAddId) {
        Chat chat = chatService.addUserToGroup(chatId, userToAddId);
        return ResponseEntity.ok(chat);
    }

    @PutMapping("/{chat-id}/members/{user-to-remove-id}")
    public ResponseEntity<Chat> removeUserToGroup(@PathVariable("chat-id") String chatId,
                                                  @PathVariable("user-to-remove-id") String userToRemoveId) {
        Chat chat = chatService.removeUserFromGroup(chatId, userToRemoveId);
        return ResponseEntity.ok(chat);
    }

    @PutMapping("/{chat-id}/groups/{new-name}")
    public ResponseEntity<Chat> renameGroup(@PathVariable("chat-id") String chatId,
                                            @PathVariable("new-name") String newGroupName) {
        Chat chat = chatService.renameGroup(chatId, newGroupName);
        return ResponseEntity.ok(chat);
    }

    @DeleteMapping("/{chat-id}")
    public ResponseEntity<Chat> deleteChat(@PathVariable("chat-id") String chatId) {
        Chat chat = chatService.deleteChat(chatId);
        return ResponseEntity.ok(chat);
    }

}
