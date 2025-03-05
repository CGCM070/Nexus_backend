package nexus_backend.repository;

import nexus_backend.domain.Server;
import nexus_backend.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ServerRepository  extends JpaRepository<Server, Long> {
    // lo usamos en el test de UserRegistrationServiceTest
    // verifica que se crea un servidor personal al registrar un usuario
    Optional<Server> findByUser(User user);

    Optional<Server> findByUserId(Long userId);

}
