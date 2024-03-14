package in.ineuron.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private MessageType messageType;

    @CreationTimestamp
    private LocalDateTime creationTime;

    @ManyToOne
    private User createdBy;

    @ManyToOne
    @JsonIgnore
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
