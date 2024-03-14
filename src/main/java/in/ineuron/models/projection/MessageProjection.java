package in.ineuron.models.projection;

import in.ineuron.models.Chat;
import in.ineuron.models.MediaCategory;
import in.ineuron.models.MessageType;
import in.ineuron.models.User;

import java.time.LocalDateTime;
import java.util.Set;

public interface MessageProjection {
    Long getId();

    MessageType getMessageType();

    LocalDateTime getCreationTime();

    User getCreatedBy();

//    ChatProjection getChat();

    Set<User> getDeletedByUsers();

    // Specific fields for TextMessage
    String getMessage();

    // Specific fields for MediaMessage
    MediaFileProjection getMediaData();

    MediaCategory getMediaCategory();

    String getNoteMessage();
}
