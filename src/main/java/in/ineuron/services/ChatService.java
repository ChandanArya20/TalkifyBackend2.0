package in.ineuron.services;

import in.ineuron.dto.GroupChatRequest;
import in.ineuron.models.Chat;

import java.util.List;

public interface ChatService {

    public Chat createSingleChat(Long reqUserId, Long participantId);
    public Chat findChatById(Long chatId);
    public List<Chat> findAllChatsByUserId(Long userId);
    public Chat createGroup(GroupChatRequest req, Long reqUserId);
    public Chat addUserToGroup(Long chatId, Long userId, Long reqUserId);
    public Chat renameGroup(Long chatId, String newChatName, Long reqUserId);
    public Chat removeUserFromGroup(Long chatId, Long userId, Long reqUserId);
    public Chat deleteChat(Long chatId, Long userId);

}
