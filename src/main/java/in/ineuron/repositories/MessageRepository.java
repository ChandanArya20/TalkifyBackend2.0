package in.ineuron.repositories;

import in.ineuron.models.Chat;
import in.ineuron.models.Message;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {

    public List<Message> findByChat(Chat chat);
}
