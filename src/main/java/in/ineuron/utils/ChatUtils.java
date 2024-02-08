package in.ineuron.utils;

import in.ineuron.dto.ChatResponse;
import in.ineuron.dto.UserResponse;
import in.ineuron.models.Chat;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class ChatUtils {

    private final UserUtils userUtils;

    public ChatUtils(UserUtils userUtils) {
        this.userUtils = userUtils;
    }

    public ChatResponse getChatResponse(Chat chat){

        UserResponse createdBy = userUtils.getUserResponse(chat.getCreatedBy());
        List<UserResponse> admins = userUtils.getUserResponse(chat.getAdmins());
        List<UserResponse> users = userUtils.getUserResponse(chat.getMembers());

        ChatResponse chatResponse = new ChatResponse();
        BeanUtils.copyProperties(chat,chatResponse);

        chatResponse.setCreatedBy(createdBy);
        chatResponse.setAdmins(new HashSet<>(admins));
        chatResponse.setMembers(new HashSet<>(users));

        return chatResponse;
    }

    public List<ChatResponse> getChatResponse(Collection<Chat> chats){
        List<ChatResponse> chatResponses = new ArrayList<>();

        for(Chat chat:chats){

            UserResponse createdBy = userUtils.getUserResponse(chat.getCreatedBy());
            List<UserResponse> admins = userUtils.getUserResponse(chat.getAdmins());
            List<UserResponse> users = userUtils.getUserResponse(chat.getMembers());

            ChatResponse chatResponse = new ChatResponse();
            BeanUtils.copyProperties(chat,chatResponse);

            chatResponse.setCreatedBy(createdBy);
            chatResponse.setAdmins(new HashSet<>(admins));
            chatResponse.setMembers(new HashSet<>(users));

            chatResponses.add(chatResponse);
        }
        return chatResponses;
    }

}
