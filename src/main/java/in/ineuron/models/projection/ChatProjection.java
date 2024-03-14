package in.ineuron.models.projection;

import in.ineuron.models.Chat;
import in.ineuron.models.MediaCategory;
import in.ineuron.models.MessageType;
import in.ineuron.models.User;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Set;

public interface ChatProjection {
    Long getId();
    String getChatName();
    String getChatImage();
    Boolean getIsGroup();
    Set<User> getAdmins();
    User getCreatedBy();
    Set<User> getMembers();
    List<MessageProjection> getMessages();
    Set<User> getDeletedByUsers();
}

