package nexus_backend.repository;

import nexus_backend.domain.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.sql.Timestamp;
import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findAllByChannelId(Long channelId);
    List<Task> findAllByAssignedToId(Long userId);
    List<Task> findAllByCreatorId(Long userId);
    List<Task> findAllByStatus(String status);
    List<Task> findAllByCreatedAtBetween(Timestamp start, Timestamp end);
    List<Task> findAllByUpdatedAtBetween(Timestamp start, Timestamp end);
}
