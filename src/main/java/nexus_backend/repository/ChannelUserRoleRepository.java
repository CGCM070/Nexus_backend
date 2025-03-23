package nexus_backend.repository;


import nexus_backend.domain.ChannelUserRole;
import nexus_backend.enums.EChannelRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ChannelUserRoleRepository extends JpaRepository<ChannelUserRole, Long> {
    Optional<ChannelUserRole> findByChannelIdAndUserUsername(Long channelId, String username);
    boolean existsByChannelIdAndUserUsername(Long channelId, String username);
    Optional<ChannelUserRole> findByChannelIdAndUserId(Long channelId, Long userId);
}