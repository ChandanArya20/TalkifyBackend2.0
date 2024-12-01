package in.ineuron.utils;

import in.ineuron.constant.ErrorConstant;
import in.ineuron.constant.MessageType;
import in.ineuron.dto.MessageResponse;
import in.ineuron.dto.UserResponse;
import in.ineuron.exception.MessageException;
import in.ineuron.models.MediaMessage;
import in.ineuron.models.Message;
import in.ineuron.models.TextMessage;
import in.ineuron.models.projection.MediaFileProjection;
import in.ineuron.repositories.MessageRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Component
@AllArgsConstructor
public class MessageUtils {

    private final UserUtils userUtils;
    private final TalkifyUtils talkifyUtils;
    private final MessageRepository msgRepo;

    // Method to generate MessageResponse object from Message entity and chatId
    public MessageResponse getMessageResponse(Message message, String chatId) {

        // Get UserResponse for createdBy
        UserResponse createdBy = userUtils.getUserResponse(message.getCreatedBy());

        // Create MessageResponse object and copy properties from Message entity
        MessageResponse messageResponse = new MessageResponse();
        BeanUtils.copyProperties(message, messageResponse);

        // Set createdBy, creation time, and chatId in MessageResponse
        messageResponse.setCreatedBy(createdBy);
        messageResponse.setChatId(chatId);

        // Set message specific properties based on message type
        if (message.getMessageType().equals(MessageType.TEXT)) {
            messageResponse.setTextMessage(((TextMessage) message).getMessage());
            messageResponse.setMessageType(String.valueOf(MessageType.TEXT));
        } else {
            // For media messages, fetch media message data and set relevant properties
            MediaFileProjection mediaMessageData = getMediaMessageDataById(message.getId());
            MediaMessage mediaMessage = (MediaMessage) message;

            messageResponse.setMessageType(mediaMessageData.getFileType());
            messageResponse.setFileName(mediaMessageData.getFileName());
            messageResponse.setFileSize(mediaMessageData.getFileSize());
            messageResponse.setMediaURL(talkifyUtils.getBaseURL() + "/api/media/" + mediaMessageData.getId()+"/stream");
            messageResponse.setNoteMessage(mediaMessage.getNoteMessage());
        }

        return messageResponse;
    }

    // Overloaded method to generate MessageResponse object without chatId
    public MessageResponse getMessageResponse(Message message) {

        // Get UserResponse for createdBy
        UserResponse createdBy = userUtils.getUserResponse(message.getCreatedBy());

        // Create MessageResponse object and copy properties from Message entity
        MessageResponse messageResponse = new MessageResponse();
        BeanUtils.copyProperties(message, messageResponse);

        // Set createdBy, creation time, and chatId in MessageResponse
        messageResponse.setCreatedBy(createdBy);
        messageResponse.setChatId(message.getChat().getId());

        // Set message specific properties based on message type
        if (message.getMessageType().equals(MessageType.TEXT)) {
            messageResponse.setTextMessage(((TextMessage) message).getMessage());
            messageResponse.setMessageType(String.valueOf(MessageType.TEXT));
        } else {
            // For media messages, fetch media message data and set relevant properties
            MediaFileProjection mediaMessageData = getMediaMessageDataById(message.getId());
            MediaMessage mediaMessage = (MediaMessage) message;

            messageResponse.setMessageType(mediaMessageData.getFileType());
            messageResponse.setFileName(mediaMessageData.getFileName());
            messageResponse.setMediaURL(talkifyUtils.getBaseURL() + "/api/media/" + mediaMessageData.getId()+"/stream");
            messageResponse.setNoteMessage(mediaMessage.getNoteMessage());
        }

        return messageResponse;
    }

    // Method to generate list of MessageResponse objects from collection of Message entities with chatId
    public List<MessageResponse> getMessageResponse(Collection<Message> messages, String chatId) {

        List<MessageResponse> messageResponses = new ArrayList<>();

        for (Message message : messages) {

            MessageResponse messageResponse = getMessageResponse(message, chatId);
            messageResponses.add(messageResponse);
        }
        return messageResponses;
    }

    // Overloaded method to generate list of MessageResponse objects without chatId
    public List<MessageResponse> getMessageResponse(Collection<Message> messages) {

        List<MessageResponse> messageResponses = new ArrayList<>();

        for (Message message : messages) {

            MessageResponse messageResponse = getMessageResponse(message);
            messageResponses.add(messageResponse);
        }
        return messageResponses;
    }

    // Method to fetch media message data by message ID
    public MediaFileProjection getMediaMessageDataById(String id) {
        return msgRepo.findMediaDataAttributesByMessageId(id).orElseThrow(
                () -> new MessageException(
                        ErrorConstant.MESSAGE_NOT_FOUND_ERROR.getErrorCode(),
                        ErrorConstant.MESSAGE_NOT_FOUND_ERROR.getErrorMessage() + " : with id " + id,
                        HttpStatus.NOT_FOUND
                ));
    }


}
