package in.ineuron.utils;

import in.ineuron.dto.ChatResponse;
import in.ineuron.dto.MessageResponse;
import in.ineuron.dto.UserResponse;
import in.ineuron.models.Chat;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class ChatUtils {

    private final UserUtils userUtils;
    private final MessageUtils messageUtils;

    public ChatUtils(UserUtils userUtils, MessageUtils messageUtils) {
        this.userUtils = userUtils;
        this.messageUtils = messageUtils;
    }

    public ChatResponse getChatResponse(Chat chat){

        UserResponse createdBy = userUtils.getUserResponse(chat.getCreatedBy());
        List<UserResponse> admins = userUtils.getUserResponse(chat.getAdmins());
        List<UserResponse> users = userUtils.getUserResponse(chat.getMembers());
        List<MessageResponse> messageResponses = messageUtils.getMessageResponse(chat.getMessages(), chat.getId());

        ChatResponse chatResponse = new ChatResponse();
        BeanUtils.copyProperties(chat,chatResponse);

        chatResponse.setCreatedBy(createdBy);
        chatResponse.setAdmins(new HashSet<>(admins));
        chatResponse.setMembers(new HashSet<>(users));
        chatResponse.setMessages(messageResponses);

        return chatResponse;
    }

    public List<ChatResponse> getChatResponse(Collection<Chat> chats){
        List<ChatResponse> chatResponses = new ArrayList<>();

        for(Chat chat:chats){

            ChatResponse chatResponse = getChatResponse(chat);
            chatResponses.add(chatResponse);
        }
        return chatResponses;
    }

}
