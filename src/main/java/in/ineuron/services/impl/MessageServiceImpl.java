package in.ineuron.services.impl;

import in.ineuron.constant.ErrorConstant;
import in.ineuron.constant.MessageType;
import in.ineuron.dto.MessageRequest;
import in.ineuron.exception.MessageException;
import in.ineuron.exception.UserException;
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
import org.springframework.http.HttpStatus;
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

    MediaFileRepository mediaFileRepository;
    private MessageRepository msgRepo;
    private UserService userService;
    private ChatService chatService;
    private ChatRepository chatRepo;
    private TalkifyUtils talkifyUtils;

    @Override
    public Message sendTextMessage(MessageRequest messReq) throws IOException {
        // Find the user who sent the message
        User reqUser = userService.getLoggedInUser();
        // Find the chat where the message is being sent
        Chat chat = chatService.findChatById(messReq.getChatId());
        Message message = null;

        // Create a text message entity
        TextMessage txtMsg = new TextMessage();
        txtMsg.setMessageType(MessageType.TEXT);
        txtMsg.setMessage(messReq.getTextMessage());
        txtMsg.setCreatedBy(reqUser);
        txtMsg.setChat(chat);
        message = txtMsg;

        Message savedMessage = msgRepo.save(message);
        chat.getMessages().add(savedMessage);
        Chat savedChat = chatRepo.save(chat);

        return savedMessage;
    }

    @Override
    public Message sendMediaMessage(MessageRequest messReq) throws IOException {
        // Find the user who sent the message
        User reqUser = userService.getLoggedInUser();
        // Find the chat where the message is being sent
        Chat chat = chatService.findChatById(messReq.getChatId());
        Message message = null;

        // Create a media message entity
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

        // Set media data and category for the media message
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
        chat.getMessages().add(savedMessage);
        Chat savedChat = chatRepo.save(chat);

        return savedMessage;
    }

    @Override
    public List<Message> getChatMessages(Long chatId) {

        Chat chat = chatService.findChatById(chatId);
        // Find the user who requested the chat messages
        User user = userService.getLoggedInUser();

        // Check if the user is a member of the chat
        if (!chat.getMembers().contains(user)) {
            throw new UserException(
                    ErrorConstant.USER_NOT_AUTHORIZED_ERROR.getErrorCode(),
                    ErrorConstant.USER_NOT_AUTHORIZED_ERROR.getErrorMessage() + " : You are not authorized for this chat with id " + chatId,
                    HttpStatus.UNAUTHORIZED
            );
        } else {
            List<Message> messages = msgRepo.findByChat(chatService.findChatById(chatId));

            // Filter out deleted messages for the requesting user
            return messages.stream()
                    .filter(message -> !message.getDeletedByUsers().contains(user))
                    .toList();
        }
    }


    @Override
    public Message findMessageById(Long messageId) {
        return msgRepo.findById(messageId).orElseThrow(
                () -> new MessageException(
                        ErrorConstant.MESSAGE_NOT_FOUND_ERROR.getErrorCode(),
                        ErrorConstant.MESSAGE_NOT_FOUND_ERROR.getErrorMessage() + " : with id " + messageId,
                        HttpStatus.NOT_FOUND
                ));
    }

    @Override
    public Message deleteMessage(Long messageId) {

        Message msg = findMessageById(messageId);
        // Find the user who requested the deletion
        User user = userService.getLoggedInUser();

        // Check if the user is the creator of the message
        if (msg.getCreatedBy().getId().equals(messageId)) {
            // If the user is authorized to delete the message, mark it as deleted
            msg.getDeletedByUsers().add(user);
            return msgRepo.save(msg);
        } else {
            throw new UserException(
                    ErrorConstant.USER_NOT_AUTHORIZED_ERROR.getErrorCode(),
                    ErrorConstant.USER_NOT_AUTHORIZED_ERROR.getErrorMessage() + " : You are not authorized for this message with id " + messageId,
                    HttpStatus.UNAUTHORIZED
            );
        }
    }

    @Override
    public Chat deleteAllMessagesByChatId(Long chatId) {

        Chat chat = chatService.findChatById(chatId);
        User user = userService.fetchUserByEmail(userService.getUsername());

        if (!chat.getMembers().contains(user)) {
            throw new UserException(
                    ErrorConstant.USER_NOT_AUTHORIZED_ERROR.getErrorCode(),
                    ErrorConstant.USER_NOT_AUTHORIZED_ERROR.getErrorMessage() + " : You are not authorized for this chat with id " + chatId,
                    HttpStatus.UNAUTHORIZED
            );
        } else {
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
    public Chat deleteMessagesByIds(Long chatId, Set<Long> messageIds) {
        Chat chat = chatService.findChatById(chatId);
        User user = userService.getLoggedInUser();

        if (!chat.getMembers().contains(user)) {
            throw new UserException(
                    ErrorConstant.USER_NOT_AUTHORIZED_ERROR.getErrorCode(),
                    ErrorConstant.USER_NOT_AUTHORIZED_ERROR.getErrorMessage() + " : You are not authorized for this chat with id " + chatId,
                    HttpStatus.UNAUTHORIZED
            );
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

}
