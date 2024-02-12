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

    public MessageUtils(UserUtils userUtils) {
        this.userUtils = userUtils;
    }

    public MessageResponse getMessageResponse(Message message){

        UserResponse createdBy = userUtils.getUserResponse(message.getCreatedBy());

        MessageResponse messageResponse = new MessageResponse();
        BeanUtils.copyProperties(message,messageResponse);

        messageResponse.setCreatedBy(createdBy);
        messageResponse.setChatId(message.getChat().getId());

        return messageResponse;
    }

    public List<MessageResponse> getMessageResponse(Collection<Message> messages){

        List<MessageResponse> messageResponses = new ArrayList<>();

        for(Message message:messages){

            MessageResponse messageResponse = getMessageResponse(message);
            messageResponses.add(messageResponse);
        }
        return messageResponses;
    }

}
