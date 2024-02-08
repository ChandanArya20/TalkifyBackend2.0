package in.ineuron.dto;

import in.ineuron.models.Chat;
import in.ineuron.models.User;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Data
public class MessageResponse {

    private Long id;

    private String textMessage;

    private LocalDateTime creationTime;

    private UserResponse createdBy;

    private ChatResponse chat;
}
