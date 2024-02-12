package in.ineuron.dto;

import in.ineuron.models.Message;
import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
public class ChatResponse {

    private Long id;

    private String chatName;

    private String chatImage;

    private Boolean isGroup;

    private Set<UserResponse> admins=new HashSet<>();

    private UserResponse createdBy;

    private Set<UserResponse> members=new HashSet<>();

    private List<MessageResponse> messages=new ArrayList<>();


}
