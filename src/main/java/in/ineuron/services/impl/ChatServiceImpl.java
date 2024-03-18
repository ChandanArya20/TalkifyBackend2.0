package in.ineuron.services.impl;

import in.ineuron.dto.GroupChatRequest;
import in.ineuron.exception.ChatNotFoundException;
import in.ineuron.exception.UserNotAuthorizedException;
import in.ineuron.exception.UserNotFoundException;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@AllArgsConstructor
public class ChatServiceImpl implements ChatService {

    private final ChatRepository chatRepo;
    private final UserService userService;
    private final UserUtils userUtils;
    private final ChatUtils chatUtils;
    private final TalkifyUtils talkifyUtils;

    @Override
    public Chat createSingleChat(Long reqUserId, Long participantId) {

        User reqUser = userService.findUserById(reqUserId);
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
    public Chat findChatById(Long chatId) {
        return chatRepo.findById(chatId).orElseThrow(
                () -> new ChatNotFoundException("Chat not found with id " + chatId)
        );
    }

    @Override
    public List<Chat> findAllChatsByUserId(Long userId) {

        User user = userService.findUserById(userId);
        List<Chat> chats = chatRepo.findNonDeletedChatsByUser(user);
        Collections.reverse(chats);

        // filter deleted messages data from every chat
        return chats.stream()
                .map(chat -> {

                    List<Message> messages = chat.getMessages()
                            .stream()
                            .filter(message -> !message.getDeletedByUsers().contains(user))
                            .collect(Collectors.toList());
                    chat.setMessages(messages);
                    return chat;
                })
                .collect(Collectors.toList());
    }

    @Override
    public Chat createGroup(GroupChatRequest req, Long reqUserId) {
        Chat group = new Chat();
        User createdBy = userService.findUserById(reqUserId);

        group.setIsGroup(true);
        group.setChatName(req.getGroupName());
        group.setChatImage(req.getGroupImage());
        group.getAdmins().add(createdBy);
        group.setCreatedBy(createdBy);
        group.getMembers().add(createdBy);

        for (Long memberId : req.getMembersId()) {
            User member = userService.findUserById(memberId);
            group.getMembers().add(member);
        }

        return chatRepo.save(group);
    }

    @Override
    public Chat addUserToGroup(Long chatId, Long userId, Long reqUserId) {

        Chat chat = findChatById(chatId);
        User reqUser = userService.findUserById(reqUserId);
        User user = userService.findUserById(userId);

        if (chat.getAdmins().contains(reqUser)) {
            chat.getAdmins().add(user);
            return chat;
        } else {
            throw new UserNotFoundException("Only admins are allowed to delete the group chat");
        }
    }

    @Override
    public Chat renameGroup(Long chatId, String newGroupName, Long reqUserId) {
        Chat chat = findChatById(chatId);
        User reqUser = userService.findUserById(reqUserId);

        if (chat.getMembers().contains(reqUser)) {
            chat.setChatName(newGroupName);
            return chatRepo.save(chat);
        } else {
            throw new UserNotAuthorizedException("Requested user is not member of group");
        }

    }

    @Override
    public Chat removeUserFromGroup(Long chatId, Long userId, Long reqUserId) {

        Chat chat = findChatById(chatId);
        // Find the user who requested the removal
        User reqUser = userService.findUserById(reqUserId);
        // Find the user to be removed from the group
        User user = userService.findUserById(userId);

        // Check if the requester is an admin of the group
        if (chat.getAdmins().contains(reqUser)) {
            chat.getAdmins().remove(user);
        } else if (chat.getMembers().contains(reqUser)) {
            // If the requester is not an admin but a member of the group
            if (userId.equals(reqUserId)) {
                chat.getMembers().remove(user);
            } else {
                throw new UserNotAuthorizedException("You can't remove another user");
            }
        } else {
            throw new UserNotAuthorizedException("User not found in the chat");
        }

        return chat;
    }

    @Override
    public Chat deleteChat(Long chatId, Long userId) {

        Chat chat = findChatById(chatId);
        // Find the user who requested the deletion
        User user = userService.findUserById(userId);

        // Check if the chat is a group chat and if the user is an admin
        if (chat.getIsGroup() && !chat.getAdmins().contains(user)) {
            throw new UserNotAuthorizedException("Only admins are allowed to delete the group chat");
        }

        // Check if the user is a member of the chat
        if (!chat.getMembers().contains(user)) {
            throw new UserNotFoundException("User not found in the chat");
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
