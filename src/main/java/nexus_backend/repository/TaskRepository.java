package nexus_backend.repository;

import nexus_backend.domain.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


public interface TaskRepository extends JpaRepository<Task, Long> {
    Page<Task> findAllByChannel_Id(Long channelId, Pageable pageable);
}
