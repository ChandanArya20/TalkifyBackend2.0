package in.ineuron.utils;

import in.ineuron.dto.MessageResponse;
import in.ineuron.dto.UserResponse;
import in.ineuron.exception.MessageNotFoundException;
import in.ineuron.models.*;
import in.ineuron.models.projection.MediaFileProjection;
import in.ineuron.repositories.MessageRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Component
@AllArgsConstructor
public class MessageUtils {

    private final UserUtils userUtils;
    private final TalkifyUtils talkifyUtils;
    private final MessageRepository msgRepo;

    public MessageResponse getMessageResponse(Message message){

        UserResponse createdBy = userUtils.getUserResponse(message.getCreatedBy());
        LocalTime localTime = message.getCreationTime().toLocalTime();
        String formatTime = localTime.format(DateTimeFormatter.ofPattern("hh:mm a"));

        MessageResponse messageResponse = new MessageResponse();
        BeanUtils.copyProperties(message,messageResponse);

        messageResponse.setCreatedBy(createdBy);
        messageResponse.setCreationTime(formatTime);
//        messageResponse.setChatId(message.getChat().getId());

        if(message.getMessageType().equals(MessageType.TEXT)){
            messageResponse.setTextMessage(((TextMessage)message).getMessage());
            messageResponse.setMessageType(String.valueOf(MessageType.TEXT));
        } else {
            MediaFileProjection mediaMessageData = getMediaMessageDataById(message.getId());
            MediaMessage mediaMessage = (MediaMessage) message;

            messageResponse.setMediaURL(talkifyUtils.getBaseURL()+"/api/media/"+mediaMessageData.getId());
            messageResponse.setNoteMessage(mediaMessage.getNoteMessage());
            messageResponse.setMessageType(mediaMessageData.getFileType());
        }

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

    public MediaFileProjection getMediaMessageDataById(Long id) {
        return msgRepo.findMediaDataAttributesByMessageId(id).orElseThrow(
                ()-> new MessageNotFoundException("MediaMessage not found with id "+id));
    }

}
