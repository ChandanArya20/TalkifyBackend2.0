package in.ineuron.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class TextMessage extends Message {

    @Column(nullable = false)
    private String message;

    @Override
    public String toString() {
        return "TextMessage{" +
                "id='" + getId() + '\'' +
                ", messageType=" + getMessageType() +
                ", message='" + message + '\'' +
                '}';
    }
}
