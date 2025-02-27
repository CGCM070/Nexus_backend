package nexus_backend.repository;

import nexus_backend.domain.Channel;
import nexus_backend.domain.Server;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ChannelRepository extends JpaRepository<Channel, Long> {
   // hecho para el test de UserRegistrationServiceTest , podemos prescindir de este método
    Optional<Channel> findByServerAndName(Server server, String name);

    //
}
