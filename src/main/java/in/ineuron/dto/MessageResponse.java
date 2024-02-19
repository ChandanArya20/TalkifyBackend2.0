package in.ineuron.dto;

import in.ineuron.models.Chat;
import in.ineuron.models.User;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;

@Data
public class MessageResponse {

    private Long id;

    private String textMessage;

    private String creationTime;

    private UserResponse createdBy;

    private Long chatId;

}
