package nexus_backend.repository;

import nexus_backend.domain.Note;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NoteRepository extends JpaRepository<Note, Long> {
    List<Note> findAllByUser_Id(Long id);

    List<Note>getAllByChannel_Id(Long id);

    Note deleteNoteByChannel_IdAndUser_Id(Long channelId, Long userId);

    @Modifying
    @Query("DELETE FROM Note n WHERE n.channel.id = :channelId")
    void deleteByChannelId(@Param("channelId") Long channelId);

}

