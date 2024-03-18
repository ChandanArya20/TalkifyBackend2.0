package in.ineuron.utils;

import in.ineuron.dto.ChatResponse;
import in.ineuron.dto.MessageResponse;
import in.ineuron.dto.UserResponse;
import in.ineuron.models.Chat;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@AllArgsConstructor
public class ChatUtils {

    private final UserUtils userUtils;
    private final MessageUtils messageUtils;

    // Method to generate ChatResponse object from Chat entity
    public ChatResponse getChatResponse(Chat chat){

        // Get UserResponse for createdBy
        UserResponse createdBy = userUtils.getUserResponse(chat.getCreatedBy());
        // Get UserResponse for admins
        List<UserResponse> admins = userUtils.getUserResponse(chat.getAdmins());
        // Get UserResponse for members
        List<UserResponse> users = userUtils.getUserResponse(chat.getMembers());
        // Get MessageResponse for messages
        List<MessageResponse> messageResponses = messageUtils.getMessageResponse(chat.getMessages(), chat.getId());
        // Create ChatResponse object and copy properties from Chat entity
        ChatResponse chatResponse = new ChatResponse();
        BeanUtils.copyProperties(chat,chatResponse);

        // Set createdBy, admins, members, and messages in ChatResponse
        chatResponse.setCreatedBy(createdBy);
        chatResponse.setAdmins(new HashSet<>(admins)); // Use HashSet to remove duplicates
        chatResponse.setMembers(new HashSet<>(users)); // Use HashSet to remove duplicates
        chatResponse.setMessages(messageResponses);

        return chatResponse;
    }

    // Method to generate list of ChatResponse objects from collection of Chat entities
    public List<ChatResponse> getChatResponse(Collection<Chat> chats){
        List<ChatResponse> chatResponses = new ArrayList<>();

        // Iterate through each Chat entity and generate ChatResponse
        for(Chat chat:chats){
            ChatResponse chatResponse = getChatResponse(chat);
            chatResponses.add(chatResponse);
        }
        return chatResponses;
    }
}
