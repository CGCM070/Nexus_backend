package nexus_backend.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import nexus_backend.domain.Note;
import nexus_backend.domain.User;
import nexus_backend.exception.NoteNotFoundException;
import nexus_backend.exception.UserNotFoundException;
import nexus_backend.repository.NoteRepository;
import nexus_backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NoteRepository noteRepository;


    public List<User> getAllUsers() {
        return this.userRepository.findAll();
    }

    public List<Note> getAllUserNotes(Long id) {
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException(id);
        }
        return this.noteRepository.findAllByUser_Id(id);
    }


    @Transactional
    public User createUser(User user) {
        userRepository.save(user);
        return user;
    }

    public User findById(Long id) {
        return this.userRepository.findById(id).orElseThrow(() ->
                new UserNotFoundException(id));
    }

    public Note createNoteForUser(Long id, Note note) {
        User user = this.userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        //asociamos la nota al usuario
        note.setUser(user);
        return this.noteRepository.save(note);
    }


    @Transactional
    public Note getNoteById(Long userId, Long noteId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        return noteRepository.findById(noteId)
                .orElseThrow(() -> new NoteNotFoundException(noteId));
    }

    @Transactional
    public void deleteNoteForUser(Long noteId) {
        // Buscar la nota
        Note note = noteRepository.findById(noteId)
                .orElseThrow(() -> new NoteNotFoundException(noteId));

        // Obtener el usuario asociado a la nota
        User user = note.getUser();

        if (user != null) {
            // Desvinculamos la nota del usuario. Esto provoca la eliminación por "orphanRemoval".
            user.getNotes().remove(note);

            // Guardamos al usuario, lo que eliminará la nota de la base de datos debido a "orphanRemoval = true".
            userRepository.save(user);
        } else {
            throw new IllegalStateException("La nota no está asociada a un usuario");
        }
    }



}
