package in.ineuron.repositories;

import in.ineuron.models.Chat;
import in.ineuron.models.Message;
import in.ineuron.models.projection.MediaFileProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface MessageRepository extends JpaRepository<Message, String> {

    public List<Message> findByChat(Chat chat);

    @Query("SELECT mm.mediaData.id AS id, mm.mediaData.fileName AS fileName, mm.mediaData.fileType AS fileType, mm.mediaData.fileSize AS fileSize FROM MediaMessage mm WHERE mm.id = :messageId")
    Optional<MediaFileProjection> findMediaDataAttributesByMessageId(String messageId);

}
