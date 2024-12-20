package in.ineuron.services.impl;

import in.ineuron.constant.ErrorConstant;
import in.ineuron.dto.GroupChatRequest;
import in.ineuron.exception.ChatException;
import in.ineuron.exception.UserException;
import in.ineuron.models.Chat;
import in.ineuron.models.Message;
import in.ineuron.models.User;
import in.ineuron.repositories.ChatRepository;
import in.ineuron.services.ChatService;
import in.ineuron.services.UserService;
import in.ineuron.utils.ChatUtils;
import in.ineuron.utils.TalkifyUtils;
import in.ineuron.utils.UserUtils;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ChatServiceImpl implements ChatService {

    private final ChatRepository chatRepo;
    private final UserService userService;

    @Override
    @Transactional
    public Chat createSingleChat(String participantId) {

        User reqUser = userService.getLoggedInUser();
        User participantUser = userService.findUserById(participantId);

        Optional<Chat> chatOptional = chatRepo.findSingleChatByUserIds(reqUser, participantUser);
        if (chatOptional.isPresent()) {
            Chat chat = chatOptional.get();

            // Check if user deleted chat previously
            if (chat.getDeletedByUsers().contains(reqUser)) {
                chat.getDeletedByUsers().remove(reqUser);
                return chatRepo.save(chat);
            }
            return chat;
        }

        // Create new chat for single conversation
        Chat newChat = new Chat();
        newChat.setCreatedBy(reqUser);
        newChat.getMembers().add(reqUser);
        newChat.getMembers().add(participantUser);
        newChat.setIsGroup(false);

        return chatRepo.save(newChat);  // Saving to database
    }

    @Override
    public Chat findChatById(String chatId) {
        return chatRepo.findById(chatId).orElseThrow(
                () -> new ChatException(
                        ErrorConstant.CHAT_NOT_FOUND_ERROR.getErrorCode(),
                        ErrorConstant.CHAT_NOT_FOUND_ERROR.getErrorMessage(),
                        HttpStatus.NOT_FOUND
                )
        );
    }

    @Override
    public List<Chat> findAllChats() {
        User user = userService.getLoggedInUser();
        List<Chat> chats = chatRepo.findNonDeletedChatsByUser(user);
        Collections.reverse(chats);

        // Filter and sort messages in each chat
        return chats.stream()
                .map(chat -> {
                    // Filter out deleted messages for the user
                    List<Message> messages = chat.getMessages()
                            .stream()
                            .filter(message -> !message.getDeletedByUsers().contains(user))
                            .sorted(Comparator.comparing(Message::getCreationTime)) // Sort by creationTime
                            .collect(Collectors.toList());

                    chat.setMessages(messages);
                    return chat;
                })
                .collect(Collectors.toList());
    }


    @Override
    public Chat createGroup(GroupChatRequest req) {
        Chat group = new Chat();
        User createdBy = userService.getLoggedInUser();

        group.setIsGroup(true);
        group.setChatName(req.getGroupName());
        group.setChatImage(req.getGroupImage());
        group.getAdmins().add(createdBy);
        group.setCreatedBy(createdBy);
        group.getMembers().add(createdBy);

        for (String memberId : req.getMembersId()) {
            User member = userService.findUserById(memberId);
            group.getMembers().add(member);
        }

        return chatRepo.save(group);
    }

    @Override
    public Chat addUserToGroup(String chatId, String userId) {
        Chat chat = findChatById(chatId);
        User reqUser = userService.getLoggedInUser();
        User user = userService.findUserById(userId);

        if (chat.getAdmins().contains(reqUser)) {
            chat.getAdmins().add(user);
            return chat;
        } else {
            throw new UserException(
                    ErrorConstant.USER_NOT_AUTHORIZED_ERROR.getErrorCode(),
                    ErrorConstant.USER_NOT_AUTHORIZED_ERROR.getErrorMessage()+" : Only admins are allowed to delete the group chat",
                    HttpStatus.UNAUTHORIZED
            );
        }
    }

    @Override
    public Chat renameGroup(String chatId, String newGroupName) {
        Chat chat = findChatById(chatId);
        User reqUser = userService.getLoggedInUser();

        if (chat.getMembers().contains(reqUser)) {
            chat.setChatName(newGroupName);
            return chatRepo.save(chat);
        } else {
            throw new UserException(
                    ErrorConstant.USER_NOT_AUTHORIZED_ERROR.getErrorCode(),
                    ErrorConstant.USER_NOT_AUTHORIZED_ERROR.getErrorMessage()+" : Requested user is not member of group",
                    HttpStatus.UNAUTHORIZED
            );
        }

    }

    @Override
    public Chat removeUserFromGroup(String chatId, String userId) {

        Chat chat = findChatById(chatId);
        // Find the user who requested the removal
        User reqUser = userService.getLoggedInUser();
        // Find the user to be removed from the group
        User user = userService.findUserById(userId);

        // Check if the requester is an admin of the group
        if (chat.getAdmins().contains(reqUser)) {
            chat.getAdmins().remove(user);
        } else if (chat.getMembers().contains(reqUser)) {
            // If the requester is not an admin but a member of the group
            if (userId.equals(reqUser.getId())) {
                chat.getMembers().remove(user);
            } else {
                throw new UserException(
                        ErrorConstant.USER_NOT_AUTHORIZED_ERROR.getErrorCode(),
                        ErrorConstant.USER_NOT_AUTHORIZED_ERROR.getErrorMessage()+" : You can't remove another user",
                        HttpStatus.UNAUTHORIZED
                );
            }
        } else {
            throw new UserException(
                    ErrorConstant.USER_NOT_AUTHORIZED_ERROR.getErrorCode(),
                    ErrorConstant.USER_NOT_AUTHORIZED_ERROR.getErrorMessage()+" : User not found in the chat",
                    HttpStatus.UNAUTHORIZED
            );
        }

        return chat;
    }

    @Override
    public Chat deleteChat(String chatId) {

        Chat chat = findChatById(chatId);
        // Find the user who requested the deletion
        User user = userService.getLoggedInUser();

        // Check if the chat is a group chat and if the user is an admin
        if (chat.getIsGroup() && !chat.getAdmins().contains(user)) {
            throw new UserException(
                    ErrorConstant.USER_NOT_AUTHORIZED_ERROR.getErrorCode(),
                    ErrorConstant.USER_NOT_AUTHORIZED_ERROR.getErrorMessage()+" : Only admins are allowed to delete the group chat",
                    HttpStatus.UNAUTHORIZED
            );
        }

        // Check if the user is a member of the chat
        if (!chat.getMembers().contains(user)) {
            throw new UserException(
                    ErrorConstant.USER_NOT_FOUND_ERROR.getErrorCode(),
                    ErrorConstant.USER_NOT_FOUND_ERROR.getErrorMessage(),
                    HttpStatus.NOT_FOUND
            );
        }

        // Delete messages associated with the chat
        for (Message message : chat.getMessages()) {
            message.getDeletedByUsers().add(user);
        }

        // Mark chat as deleted by the user
        chat.getDeletedByUsers().add(user);

        // Save the changes to the chat in the repository and return the updated chat
        return chatRepo.save(chat);
    }

}
