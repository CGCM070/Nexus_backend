package nexus_backend.service;

import jakarta.transaction.Transactional;
import nexus_backend.domain.Note;
import nexus_backend.domain.User;
import nexus_backend.exception.EntityNotFoundException;
import nexus_backend.repository.NoteRepository;
import nexus_backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NoteService {

    private final NoteRepository noteRepository;
    private final UserRepository userRepository;

    @Autowired
    public NoteService(NoteRepository noteRepository, UserRepository userRepository) {
        this.noteRepository = noteRepository;
        this.userRepository = userRepository;
    }

    public List<Note> getAllNotes() {
        return noteRepository.findAll();
    }

    public Note getNoteById(Long id) {
        return noteRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(id , "Note"));
    }

    public List<Note> getNotesByUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException(userId , "User");
        }
        return noteRepository.findAllByUser_Id(userId);
    }

    @Transactional
    public Note createNote(Note note) {
        return noteRepository.save(note);
    }


    public Note createNoteForUser(Long userId, Note note) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(userId , "User"));
        note.setUser(user);
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
        return noteRepository.getNoteByChannel_Id(channelId);
    }

}
