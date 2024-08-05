package in.ineuron.services;

import in.ineuron.dto.GroupChatRequest;
import in.ineuron.models.Chat;

import java.util.List;

public interface ChatService {

    public Chat createSingleChat(Long participantId);
    public Chat findChatById(Long chatId);
    public List<Chat> findAllChats();
    public Chat createGroup(GroupChatRequest req);
    public Chat addUserToGroup(Long chatId, Long userId);
    public Chat renameGroup(Long chatId, String newChatName);
    public Chat removeUserFromGroup(Long chatId, Long userId);
    public Chat deleteChat(Long chatId);

}
