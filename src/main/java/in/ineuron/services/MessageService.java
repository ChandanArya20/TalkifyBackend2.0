package in.ineuron.services;

import in.ineuron.dto.MessageRequest;
import in.ineuron.models.Chat;
import in.ineuron.models.Message;
import in.ineuron.models.projection.MediaFileProjection;

import java.io.IOException;
import java.util.List;
import java.util.Set;

public interface MessageService {

//    public Message sendMessage(MessageRequest messReq, Long reqUserId) throws IOException;

    Message sendTextMessage(MessageRequest messReq) throws IOException;

    Message sendTextMessage(MessageRequest messReq, Long reqUserId) throws IOException;

    Message sendMediaMessage(MessageRequest messReq) throws IOException;

    public List<Message> getChatMessages(Long chatId);

    public Message findMessageById(Long messageId);

    public Message deleteMessage(Long messageId);

    public Chat deleteAllMessagesByChatId(Long chatId);

    public Chat deleteMessagesByIds(Long chatId, Set<Long> messageIds);

}
