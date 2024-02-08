package in.ineuron.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Data
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private String textMessage;

    @CreationTimestamp
    private LocalDateTime creationTime;

    @ManyToOne
    private User createdBy;

    @ManyToOne
    @JsonIgnore
    private Chat chat;

    @Override
    public String toString() {
        return "Message{" +
                "id=" + id +
                ", textMessage='" + textMessage + '\'' +
                ", creationTime=" + creationTime +
                ", createdBy=" + createdBy +
                '}';
    }
}
