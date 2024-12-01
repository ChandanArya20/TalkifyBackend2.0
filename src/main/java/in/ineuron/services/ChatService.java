package in.ineuron.services;

import in.ineuron.dto.GroupChatRequest;
import in.ineuron.models.Chat;

import java.util.List;

public interface ChatService {

    public Chat createSingleChat(String participantId);
    public Chat findChatById(String chatId);
    public List<Chat> findAllChats();
    public Chat createGroup(GroupChatRequest req);
    public Chat addUserToGroup(String chatId, String userId);
    public Chat renameGroup(String chatId, String newChatName);
    public Chat removeUserFromGroup(String chatId, String userId);
    public Chat deleteChat(String chatId);

}
