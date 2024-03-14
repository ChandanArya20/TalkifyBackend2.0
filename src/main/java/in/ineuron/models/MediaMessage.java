package in.ineuron.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
public class MediaMessage extends Message {

    @OneToOne(fetch = FetchType.LAZY)
    private MediaFile mediaData;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MediaCategory mediaCategory; //like audio, video, image or other

    private String noteMessage;

    @Override
    public String toString() {
        return "MediaMessage{" +
                "id='" + getId() +
                ", messageType=" + getMessageType() +
                ", messageCategory=" + getMediaCategory() +
                ", mediaFile=" + getMediaData() +
                ", noteMessage=" + getNoteMessage() +
                '}';
    }

}

