package in.ineuron.services;

import in.ineuron.dto.MessageRequest;
import in.ineuron.models.Chat;
import in.ineuron.models.Message;

import java.util.List;
import java.util.Set;

public interface MessageService {

    public Message sendMessage(MessageRequest messReq, Long reqUserId);

    public List<Message> getChatMessages(Long chatId, Long reqUserId);

    public Message findMessageById(Long messageId);

    public Message deleteMessage(Long messageId, Long reqUserId);

    public Chat deleteAllMessagesByChatId(Long chatId, Long reqUserId);

    public Chat deleteMessagesByIds(Long chatId, Set<Long> messageIds, Long reqUserId);
}
