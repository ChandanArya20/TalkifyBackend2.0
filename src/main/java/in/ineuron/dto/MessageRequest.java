package in.ineuron.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class MessageRequest {

    @NotNull(message = "User ID must not be null")
    private Long userId;

    @NotNull(message = "Chat ID must not be null")
    private Long chatId;

    @NotNull(message = "Content must not be null")
    @Size(min = 1, message = "Content must not be empty")
    private String content;
}

