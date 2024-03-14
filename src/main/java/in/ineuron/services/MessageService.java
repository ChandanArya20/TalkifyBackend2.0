package in.ineuron.services;

import in.ineuron.dto.MessageRequest;
import in.ineuron.models.Chat;
import in.ineuron.models.Message;
import in.ineuron.models.projection.MediaFileProjection;

import java.io.IOException;
import java.util.List;
import java.util.Set;

public interface MessageService {

    public Message sendMessage(MessageRequest messReq, Long reqUserId) throws IOException;

    public List<Message> getChatMessages(Long chatId, Long reqUserId);

    public Message findMessageById(Long messageId);

    public Message deleteMessage(Long messageId, Long reqUserId);

    public Chat deleteAllMessagesByChatId(Long chatId, Long reqUserId);

    public Chat deleteMessagesByIds(Long chatId, Set<Long> messageIds, Long reqUserId);

    public MediaFileProjection getMediaMessageDataById(Long id);
}
