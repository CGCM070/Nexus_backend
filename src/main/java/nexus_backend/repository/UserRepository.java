package nexus_backend.repository;

import nexus_backend.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository  extends JpaRepository<User, Long> {

    // lo usamos en el test de UserRegistrationServiceTest
    //verifica que se existe un usuario con el email dado
    public User findByEmail (String email);
}
