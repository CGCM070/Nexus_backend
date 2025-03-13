package nexus_backend.repository;

import nexus_backend.domain.Channel;
import nexus_backend.domain.Server;
import nexus_backend.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ChannelRepository extends JpaRepository<Channel, Long> {
   // hecho para el test de UserRegistrationServiceTest , podemos prescindir de este método
    Optional<Channel> findByServerAndName(Server server, String name);

    // Retorna todos los canales donde el usuario es miembro
    List<Channel> findAllByInvitedUsersContaining(User user);
}
