package nexus_backend.repository;

import nexus_backend.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository  extends JpaRepository<User, Long> {

    // lo usamos en el test de UserRegistrationServiceTest
    //verifica que se existe un usuario con el email dado
    public Optional<User> findByEmail (String email);

    public Optional<User> findByUsername (String username);
}
