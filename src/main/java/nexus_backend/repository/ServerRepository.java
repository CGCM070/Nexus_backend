package nexus_backend.repository;

import nexus_backend.domain.Server;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ServerRepository  extends JpaRepository<Server, Long> {
}
