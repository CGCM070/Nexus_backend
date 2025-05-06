package nexus_backend.repository;


import nexus_backend.domain.ChannelUserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ChannelUserRoleRepository extends JpaRepository<ChannelUserRole, Long> {
    Optional<ChannelUserRole> findByChannelIdAndUserUsername(Long channelId, String username);
    boolean existsByChannelIdAndUserUsername(Long channelId, String username);
    Optional<ChannelUserRole> findByChannelIdAndUserId(Long channelId, Long userId);
    Optional<ChannelUserRole> findByChannelIdAndUserEmail(Long channelId, String email);
     void deleteByUserId(Long userId);
    @Modifying
    @Query("DELETE FROM ChannelUserRole cur WHERE cur.channel.id = :channelId")
    void deleteByChannelId(@Param("channelId") Long channelId);

    Optional<ChannelUserRole> findByUserUsernameAndChannelId(String username, Long id);
}