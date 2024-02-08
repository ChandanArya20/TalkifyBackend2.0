package in.ineuron.utils;

import in.ineuron.dto.ChatResponse;
import in.ineuron.dto.MessageResponse;
import in.ineuron.dto.UserResponse;
import in.ineuron.models.Chat;
import in.ineuron.models.Message;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

@Component
public class MessageUtils {

    private final UserUtils userUtils;
    private final ChatUtils chatUtils;

    public MessageUtils(UserUtils userUtils, ChatUtils chatUtils) {
        this.userUtils = userUtils;
        this.chatUtils = chatUtils;
    }

    public MessageResponse getMessageResponse(Message message){

        UserResponse createdBy = userUtils.getUserResponse(message.getCreatedBy());
        ChatResponse chatResponse = chatUtils.getChatResponse(message.getChat());

        MessageResponse messageResponse = new MessageResponse();
        BeanUtils.copyProperties(message,messageResponse);

        messageResponse.setCreatedBy(createdBy);
        messageResponse.setChat(chatResponse);

        return messageResponse;
    }

    public List<MessageResponse> getMessageResponse(Collection<Message> messages){

        List<MessageResponse> messageResponses = new ArrayList<>();

        for(Message message:messages){

            UserResponse createdBy = userUtils.getUserResponse(message.getCreatedBy());
            ChatResponse chatResponse = chatUtils.getChatResponse(message.getChat());

            MessageResponse messageResponse = new MessageResponse();
            BeanUtils.copyProperties(message,messageResponse);

            messageResponse.setCreatedBy(createdBy);
            messageResponse.setChat(chatResponse);

            messageResponses.add(messageResponse);
        }
        return messageResponses;
    }

}
