package in.ineuron.services.impl;

import in.ineuron.dto.MessageRequest;
import in.ineuron.exception.MessageNotFoundException;
import in.ineuron.exception.UserNotAuthorizedException;
import in.ineuron.models.*;
import in.ineuron.models.projection.MediaFileProjection;
import in.ineuron.repositories.ChatRepository;
import in.ineuron.repositories.MediaFileRepository;
import in.ineuron.repositories.MessageRepository;
import in.ineuron.services.ChatService;
import in.ineuron.services.MessageService;
import in.ineuron.services.UserService;
import in.ineuron.utils.MessageUtils;
import in.ineuron.utils.TalkifyUtils;
import in.ineuron.utils.UserUtils;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Set;

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
    MediaFileRepository mediaFileRepository;
    private TalkifyUtils talkifyUtils;

    @Override
    public Message sendTextMessage(MessageRequest messReq, Long reqUserId) throws IOException {
        User reqUser = userService.findUserById(reqUserId);
        Chat chat = chatService.findChatById(messReq.getChatId());
        Message message = null;

        TextMessage txtMsg = new TextMessage();
        txtMsg.setMessageType(MessageType.TEXT);
        txtMsg.setMessage(messReq.getTextMessage());
        txtMsg.setCreatedBy(reqUser);
        txtMsg.setChat(chat);

        message = txtMsg;

        // Save the Message entity first
        Message savedMessage = msgRepo.save(message);

        // Add the saved Message to the Chat's list of messages
        chat.getMessages().add(savedMessage);

        // Save the Chat entity
        Chat savedChat = chatRepo.save(chat);

        return savedMessage;
    }

    @Override
    public Message sendMediaMessage(MessageRequest messReq, Long reqUserId) throws IOException {
        User reqUser = userService.findUserById(reqUserId);
        Chat chat = chatService.findChatById(messReq.getChatId());
        Message message = null;

        MediaMessage mediaMsg = new MediaMessage();
        mediaMsg.setMessageType(MessageType.MEDIA);
        // Save the MediaFile entity first
        MediaFile mediaFile = new MediaFile();
        MultipartFile fileData = messReq.getMediaFile();
        mediaFile.setMediaContent(fileData.getBytes());
        mediaFile.setFileName(fileData.getOriginalFilename());
        mediaFile.setFileType(fileData.getContentType());
        mediaFile.setFileSize(fileData.getSize());

        mediaFile = mediaFileRepository.save(mediaFile);

        mediaMsg.setMediaData(mediaFile);
        mediaMsg.setMediaCategory(talkifyUtils.getMediaCategory(messReq.getMediaFile()));

        if (messReq.getNoteMessage() != null) {
            mediaMsg.setNoteMessage(messReq.getNoteMessage());
        }
        mediaMsg.setCreatedBy(reqUser);
        mediaMsg.setChat(chat);

        message = mediaMsg;

        // Save the Message entity first
        Message savedMessage = msgRepo.save(message);

        // Add the saved Message to the Chat's list of messages
        chat.getMessages().add(savedMessage);

        // Save the Chat entity
        Chat savedChat = chatRepo.save(chat);

        return savedMessage;
    }

    @Override
    public List<Message> getChatMessages(Long chatId, Long reqUserId) {

        Chat chat = chatService.findChatById(chatId);
        User user = userService.findUserById(reqUserId);

        if(!chat.getMembers().contains(user)){
            throw new UserNotAuthorizedException("You are not authorized for this chat with id "+chatId);
        }else {
            List<Message> messages = msgRepo.findByChat(chatService.findChatById(chatId));
            return messages.stream()
                    .filter(message -> !message.getDeletedByUsers().contains(user))
                    .toList();

        }
    }

    @Override
    public Message findMessageById(Long messageId) {
        return msgRepo.findById(messageId).orElseThrow(
                ()->new MessageNotFoundException("Message not found with id "+messageId)
        );
    }

    @Override
    public Message deleteMessage(Long messageId, Long reqUserId) {
        Message msg = findMessageById(messageId);
        User user = userService.findUserById(reqUserId);

        if(msg.getCreatedBy().getId().equals(messageId)){
            msg.getDeletedByUsers().add(user);
            return msgRepo.save(msg);
        }else {
            throw new UserNotAuthorizedException("You are not authorized for this message with id "+messageId);
        }
    }

    @Override
    public Chat deleteAllMessagesByChatId(Long chatId, Long reqUserId) {

        Chat chat = chatService.findChatById(chatId);
        User user = userService.findUserById(reqUserId);

        if(!chat.getMembers().contains(user)){
            throw new UserNotAuthorizedException("You are not authorized for this chat with id "+chatId);
        }else {
            List<Message> messages = msgRepo.findByChat(chatService.findChatById(chatId));

            // Delete messages associated with the chat
            for (Message message : messages) {
                message.getDeletedByUsers().add(user);
            }
            chat.setMessages(messages);
            Chat updatedChat = chatRepo.save(chat);

            // Filter out deleted messages from chat by reqUser
            List<Message> messagesList = updatedChat.getMessages()
                    .stream()
                    .filter(message -> !message.getDeletedByUsers().contains(user))
                    .toList();

            chat.setMessages(messagesList);

            return chat;
        }
    }

    @Override
    public Chat deleteMessagesByIds(Long chatId, Set<Long> messageIds, Long reqUserId) {
        Chat chat = chatService.findChatById(chatId);
        User user = userService.findUserById(reqUserId);

        if (!chat.getMembers().contains(user)) {
            throw new UserNotAuthorizedException("You are not authorized for this chat with id " + chatId);
        } else {
            List<Message> messagesToDelete = msgRepo.findAllById(messageIds);

            // Validate that the provided message IDs belong to the specified chat
            if (messagesToDelete.stream().anyMatch(message -> !message.getChat().getId().equals(chatId))) {
                throw new IllegalArgumentException("Invalid message IDs provided for chat with id " + chatId);
            }

            // Mark all messages as deleted by the user
            for (Message message : messagesToDelete) {
                message.getDeletedByUsers().add(user);
            }

            // Save the modified messages back to the database
            List<Message> updatedMessages = msgRepo.saveAll(messagesToDelete);

            // Exclude deleted messages from being added to the chat
            List<Message> nonDeletedMessages = chat.getMessages()
                    .stream()
                    .filter(message -> !message.getDeletedByUsers().contains(user))
                    .toList();

            chat.setMessages(nonDeletedMessages);

            return chat;
        }
    }

    @Override
    public MediaFileProjection getMediaMessageDataById(Long id) {
        return msgRepo.findMediaDataAttributesByMessageId(id).orElseThrow(
                ()-> new MessageNotFoundException("MediaMessage not found with id "+id));
    }


}
