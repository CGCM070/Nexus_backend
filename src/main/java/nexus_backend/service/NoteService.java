package nexus_backend.service;

import jakarta.transaction.Transactional;
import nexus_backend.domain.Channel;
import nexus_backend.domain.Note;
import nexus_backend.domain.User;
import nexus_backend.dto.NoteDTO;
import nexus_backend.exception.EntityNotFoundException;
import nexus_backend.repository.ChannelRepository;
import nexus_backend.repository.NoteRepository;
import nexus_backend.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;

@Service
public class NoteService {

    private final NoteRepository noteRepository;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;


    public NoteService(NoteRepository noteRepository, UserRepository userRepository, ChannelRepository channelRepository) {
        this.noteRepository = noteRepository;
        this.userRepository = userRepository;
        this.channelRepository = channelRepository;
    }

    public Page<Note> getAllNotes(Pageable pageable) {
        return noteRepository.findAll(pageable);
    }

    public Note getNoteById(Long id) {
        return noteRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(id , "Note"));
    }

    public List<Note> getNotesByUserId(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException(userId , "User");
        }
        return noteRepository.findAllByUser_Id(userId);
    }

    //no tiene sentido sin un canal asociado
    @Transactional
    public Note createNote(Note note) {
        Timestamp now = new Timestamp(System.currentTimeMillis());
        if (note.getCreatedAt() == null) {
            note.setCreatedAt(now);
        }
        if (note.getUpdatedAt() == null) {
            note.setUpdatedAt(now);
        }
        return noteRepository.save(note);
    }

    @Transactional
    public Note createNoteForUser(Long userId, Long channelId, Note note) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(userId, "User"));
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new EntityNotFoundException(channelId, "Channel"));

        // Establecer los timestamps
        Timestamp now = new Timestamp(System.currentTimeMillis());
        note.setCreatedAt(now);
        note.setUpdatedAt(now);

        note.setUser(user);
        note.setChannel(channel);
        return noteRepository.save(note);
    }


    public Note updateNoteById(Long noteId, Note note) {
        Note existingNote = noteRepository.findById(noteId)
                .orElseThrow(() -> new EntityNotFoundException(noteId , "Note"));
        existingNote.setTitle(note.getTitle());
        existingNote.setContent(note.getContent());
        existingNote.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        return noteRepository.save(existingNote);
    }

    @Transactional
    public void deleteNote(Long noteId) {
        if (!noteRepository.existsById(noteId)) {
            throw new EntityNotFoundException(noteId , "Note");
        }
        noteRepository.deleteById(noteId);
    }

    public Page<Note>  getNotesByChannel(Long channelId, Pageable pageable) {
        return noteRepository.getAllByChannel_Id(channelId, pageable);
    }

    @Transactional
    public Note createWelcomeNoteForUser(User user, Channel channel) {
        Note welcomeNote = Note.builder()
                .title("Bienvenido")
                .content("¡ Tamos Ready ? !")
                .createdAt(new Timestamp(System.currentTimeMillis()))
                .build();

        return createNoteForUser(user.getId(), channel.getId(), welcomeNote);
    }


    public NoteDTO convertToDTO(Note note) {
        return new NoteDTO(
                note.getId(),
                note.getTitle(),
                note.getContent(),
                note.getChannel().getId(),
                note.getUser().getId(),
                note.getUser().getUsername(),
                note.getUser().getAvatarUrl(),
                note.getCreatedAt() != null ? note.getCreatedAt().toLocalDateTime() : null,
                note.getUpdatedAt() != null ? note.getUpdatedAt().toLocalDateTime() : null
        );
    }
}
