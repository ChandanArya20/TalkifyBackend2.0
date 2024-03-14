package in.ineuron.repositories;

import in.ineuron.models.Chat;
import in.ineuron.models.Message;
import in.ineuron.models.projection.MediaFileProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface MessageRepository extends JpaRepository<Message, Long> {

    public List<Message> findByChat(Chat chat);

    @Query("SELECT mm.mediaData.id AS id, mm.mediaData.fileType AS fileType FROM MediaMessage mm WHERE mm.id = :messageId")
    Optional<MediaFileProjection> findMediaDataAttributesByMessageId(Long messageId);

}
