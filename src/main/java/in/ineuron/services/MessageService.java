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

    Message sendTextMessage(MessageRequest messReq, String reqUserId) throws IOException;

    Message sendMediaMessage(MessageRequest messReq) throws IOException;

    public List<Message> getChatMessages(String chatId);

    public Message findMessageById(String messageId);

    public Message deleteMessage(String messageId);

    public Chat deleteAllMessagesByChatId(String chatId);

    public Chat deleteMessagesByIds(String chatId, Set<String> messageIds);

}
