package in.ineuron.models;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Data
public class Chat {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private String chatName;

    private String chatImage;

    private Boolean isGroup;

    @ManyToMany
    private Set<User> admins=new HashSet<>();

    @ManyToOne
    private User createdBy;

    @ManyToMany
    private Set<User> members=new HashSet<>();

    @OneToMany
    private List<Message> messages=new ArrayList<>();

    @Override
    public String toString() {
        return "Chat{" +
                "id=" + id +
                ", chatName='" + chatName + '\'' +
                ", chatImage='" + chatImage + '\'' +
                ", isGroup=" + isGroup +
                ", admins=" + admins +
                ", createdBy=" + createdBy +
                ", members=" + members +
                '}';
    }
}
