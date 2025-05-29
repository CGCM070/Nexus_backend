package nexus_backend.repository;

import nexus_backend.domain.Note;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NoteRepository extends JpaRepository<Note, Long> {
    List<Note> findAllByUser_Id(Long id);

    Page<Note> getAllByChannel_Id(Long id, Pageable pageable);

    @Modifying
    @Query("DELETE FROM Note n WHERE n.channel.id = :channelId")
    void deleteByChannelId(@Param("channelId") Long channelId);

}

