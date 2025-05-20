package nexus_backend.repository;

import nexus_backend.domain.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository  extends JpaRepository<Message, Long> {

    List<Message> findByChannelIdOrderByCreatedAtAsc(Long channelId);
    List<Message>findByUserId(Long userId);


    @Modifying
    @Query("DELETE FROM Message m WHERE m.channel.id = :channelId")
    void deleteByChannelId(@Param("channelId") Long channelId);

}
