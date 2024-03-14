package in.ineuron.utils;

import in.ineuron.models.*;
import in.ineuron.models.projection.ChatProjection;
import in.ineuron.models.projection.MediaFileProjection;
import in.ineuron.models.projection.MessageProjection;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalTime;
import java.util.List;

@Component
public class TalkifyUtils {

    @Autowired
    private HttpServletRequest request;

    public String getBaseURL(){
        // Get the base URL dynamically from the current request
        return request.getRequestURL().toString().replace(request.getRequestURI(), "");
    }

    public MediaCategory getMediaCategory(MultipartFile file){

        String contentType = file.getContentType();
        String category = contentType.split("/")[0];

        return switch (category) {
            case "image" -> MediaCategory.IMAGE;
            case "video" -> MediaCategory.VIDEO;
            case "audio" -> MediaCategory.AUDIO;
            default -> MediaCategory.OTHER;
        };
    }

    public Chat getChat(ChatProjection chatProjection) {

        Chat chat = new Chat();

        chat.setId(chatProjection.getId());
        chat.setChatName(chatProjection.getChatName());
        chat.setChatImage(chatProjection.getChatImage());
        chat.setIsGroup(chatProjection.getIsGroup());
        chat.setAdmins(chatProjection.getAdmins());
        chat.setCreatedBy(chatProjection.getCreatedBy());
        chat.setMembers(chatProjection.getMembers());
        List<MessageProjection> messageProjections = chatProjection.getMessages();

        List<Message> messages = messageProjections.stream()
                .map(msgProj -> {
                    Message message = null;
                    
                    if (msgProj.getMessageType() == MessageType.TEXT) {
                        message= getTextMessage(msgProj);

                    } else {
                        message=getMediaMessage(msgProj);
                    }

                    return message;
                }).toList();

        chat.setMessages(messages);
        chat.setDeletedByUsers(chatProjection.getDeletedByUsers());

        return chat;
    }

    private static TextMessage getTextMessage(MessageProjection msgProj) {
        TextMessage textMessage = new TextMessage();

        textMessage.setId(msgProj.getId());
        textMessage.setMessageType(msgProj.getMessageType());
        textMessage.setMessage(msgProj.getMessage());
        textMessage.setCreationTime(msgProj.getCreationTime());
        textMessage.setCreatedBy(msgProj.getCreatedBy());
        textMessage.setCreatedBy(msgProj.getCreatedBy());
        textMessage.setDeletedByUsers(msgProj.getDeletedByUsers());
        return textMessage;
    }

    private static MediaMessage getMediaMessage(MessageProjection msgProj) {
        MediaMessage mediaMessage = new MediaMessage();

        mediaMessage.setId(msgProj.getId());
        mediaMessage.setMessageType(msgProj.getMessageType());
        mediaMessage.setCreationTime(msgProj.getCreationTime());
        mediaMessage.setCreatedBy(msgProj.getCreatedBy());
        mediaMessage.setCreatedBy(msgProj.getCreatedBy());
        mediaMessage.setDeletedByUsers(msgProj.getDeletedByUsers());
        mediaMessage.setMediaCategory(msgProj.getMediaCategory());
        mediaMessage.setNoteMessage(msgProj.getNoteMessage());
        return mediaMessage;
    }
}
