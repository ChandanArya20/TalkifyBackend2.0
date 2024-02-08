package in.ineuron.services.impl;

import in.ineuron.dto.MessageRequest;
import in.ineuron.exception.MessageNotFoundException;
import in.ineuron.exception.UserNotAuthorizedException;
import in.ineuron.models.Chat;
import in.ineuron.models.Message;
import in.ineuron.models.User;
import in.ineuron.repositories.ChatRepository;
import in.ineuron.repositories.MessageRepository;
import in.ineuron.services.ChatService;
import in.ineuron.services.MessageService;
import in.ineuron.services.UserService;
import in.ineuron.utils.MessageUtils;
import in.ineuron.utils.UserUtils;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@AllArgsConstructor
public class MessageServiceImpl implements MessageService {

    private MessageRepository msgRepo;
    private UserService userService;
    private ChatService chatService;
    private UserUtils userUtils;
    private MessageUtils messageUtils;
    private ChatRepository chatRepo;



    @Override
    public Message sendMessage(MessageRequest messReq, Long reqUserId) {
        User reqUser = userService.findUserById(reqUserId);
        Chat chat = chatService.findChatById(messReq.getChatId());

        Message msg = new Message();
        msg.setCreatedBy(reqUser);
        msg.setChat(chat);
        msg.setTextMessage(messReq.getContent());

        // Save the Message entity first
        Message savedMessage = msgRepo.save(msg);

        // Add the saved Message to the Chat's list of messages
        chat.getMessages().add(savedMessage);

        // Save the Chat entity
        Chat savedChat = chatRepo.save(chat);

        return savedMessage;
    }

    @Override
    public List<Message> getChatsMessage(Long chatId, Long reqUserId) {

        Chat chat = chatService.findChatById(chatId);
        User user = userService.findUserById(reqUserId);
        if(!chat.getMembers().contains(user)){
            throw new UserNotAuthorizedException("You are not authorized for this chat with id "+chatId);
        }else {
            return msgRepo.findByChat(chatService.findChatById(chatId));
        }
    }

    @Override
    public Message findMessageById(Long messageId) {
        return msgRepo.findById(messageId).orElseThrow(
                ()->new MessageNotFoundException("Message not found with id "+messageId)
        );
    }

    @Override
    public void deleteMessage(Long messageId, Long reqUserId) {
        Message msg = findMessageById(messageId);

        if(msg.getCreatedBy().getId().equals(messageId)){
            msgRepo.deleteById(messageId);
        }else {
            throw new UserNotAuthorizedException("You are not authorized for this message with id "+messageId);
        }
    }
}
