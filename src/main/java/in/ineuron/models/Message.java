package in.ineuron.models;

import in.ineuron.constant.MessageType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@Inheritance(strategy = InheritanceType.JOINED)
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private MessageType messageType;

    @CreationTimestamp
    private LocalDateTime creationTime;

    @ManyToOne
    private User createdBy;

    @ManyToOne(fetch = FetchType.LAZY)
    private Chat chat;

    @ManyToMany
    private Set<User> deletedByUsers = new HashSet<>();

    @Override
    public String toString() {
        return "Message{" +
                "id=" + id +
                ", messageType=" + messageType +
                ", creationTime=" + creationTime +
                ", createdBy=" + createdBy +
//                ", chat-id=" + chat.getId() +
                ", deletedByUsers=" + deletedByUsers +
                '}';
    }
}
