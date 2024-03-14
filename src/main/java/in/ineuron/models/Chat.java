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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
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

    @ManyToMany
    private Set<User> deletedByUsers = new HashSet<>();


}
