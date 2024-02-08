package in.ineuron.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
public class GroupChatRequest {

    @NotBlank(message = "Group name must not be null or empty")
    @Size(min = 1, message = "Group name must not be empty")
    private String groupName;

    private String groupImage;

    @NotEmpty(message = "User IDs must not be empty")
    @Valid // Ensures validation is cascaded to elements in the list
    private List<Long> membersId;
}
