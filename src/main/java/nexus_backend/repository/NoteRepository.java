package nexus_backend.repository;

import nexus_backend.domain.Note;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NoteRepository extends JpaRepository<Note, Long> {
    List<Note> findAllByUser_Id(Long id);
    List<Note>getNoteByChannel_Id(Long id);

}

