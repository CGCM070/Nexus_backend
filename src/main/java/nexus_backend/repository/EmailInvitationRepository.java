package nexus_backend.repository;

import nexus_backend.domain.EmailInvitation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

public interface EmailInvitationRepository extends JpaRepository<EmailInvitation, Long> {
    Optional<EmailInvitation> findByToken(String token);

    boolean existsByEmailAndChannelIdAndAcceptedIsFalse(String email, Long channelId);

    List<EmailInvitation> findByEmailAndAcceptedIsFalse(String email);

    List<EmailInvitation> findByExpireAtBeforeAndAcceptedIsFalse(Timestamp timestamp);

    @Modifying
    @Query("DELETE FROM EmailInvitation ei WHERE ei.channel.id = :channelId")
    void deleteByChannelId(@Param("channelId") Long channelId);

}
