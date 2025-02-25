package nexus_backend.service;

import jakarta.transaction.Transactional;
import nexus_backend.domain.Channel;
import nexus_backend.domain.Note;
import nexus_backend.domain.User;
import nexus_backend.exception.EntityNotFoundException;
import nexus_backend.repository.ChannelRepository;
import nexus_backend.repository.NoteRepository;
import nexus_backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NoteService {

    private final NoteRepository noteRepository;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;

    @Autowired
    public NoteService(NoteRepository noteRepository, UserRepository userRepository, ChannelRepository channelRepository) {
        this.noteRepository = noteRepository;
        this.userRepository = userRepository;
        this.channelRepository = channelRepository;
    }

    public List<Note> getAllNotes() {
        return noteRepository.findAll();
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

    @Transactional
    public Note createNote(Note note) {
        return noteRepository.save(note);
    }

    @Transactional
    public Note createNoteForUser(Long userId, Long channelId, Note note) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(userId, "User"));
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new EntityNotFoundException(channelId, "Channel"));
        note.setUser(user);
        note.setChannel(channel);
        return noteRepository.save(note);
    }


    public Note updateNoteById(Long noteId, Note note) {
        Note existingNote = noteRepository.findById(noteId)
                .orElseThrow(() -> new EntityNotFoundException(noteId , "Note"));
        existingNote.setTitle(note.getTitle());
        existingNote.setContent(note.getContent());
        return noteRepository.save(existingNote);
    }

    @Transactional
    public void deleteNote(Long noteId) {
        if (!noteRepository.existsById(noteId)) {
            throw new EntityNotFoundException(noteId , "Note");
        }
        noteRepository.deleteById(noteId);
    }

    public List<Note> getNotesByChannel(Long channelId) {
        return noteRepository.getAllByChannel_Id(channelId);
    }

}
