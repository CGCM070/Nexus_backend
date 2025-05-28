package nexus_backend.repository;

import nexus_backend.domain.Task;
import nexus_backend.enums.TaskStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface TaskRepository extends JpaRepository<Task, Long> {

    @Query("SELECT t FROM Task t WHERE t.channel.id = :channelId " +
           "AND (:searchText IS NULL OR LOWER(t.title) LIKE LOWER(CONCAT('%', :searchText, '%'))) " +
           "AND (:status IS NULL OR t.status = :status)")
    Page<Task> searchTasks(
            @Param("channelId") Long channelId,
            @Param("searchText") String searchText,
            @Param("status") TaskStatus status,
            Pageable pageable);
}