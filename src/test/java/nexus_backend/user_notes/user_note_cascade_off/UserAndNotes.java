package nexus_backend.user_notes.user_note_cascade_off;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import nexus_backend.domain.Note;
import nexus_backend.domain.User;
import nexus_backend.repository.NoteRepository;
import nexus_backend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.sql.Timestamp;


@SpringBootTest
class UserAndNotes {


    @PersistenceContext
    EntityManager entityManager;
    @Autowired
    private PlatformTransactionManager transactionManager;
    private TransactionTemplate transactionTemplate;

    @BeforeEach
    public void setUp() {
        transactionTemplate = new TransactionTemplate(transactionManager);
    }

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NoteRepository noteRepository;


    @Test
    void CreateUserWithNote_WhitOutCascade() {

        // Configuracion
        // @OneToMany(mappedBy = "user")
        // private Set<Note> notes = new HashSet<>();

        transactionTemplate.executeWithoutResult(transactionStatus -> {
            User user = User.builder()
                    .username("Pepe")
                    .email("Pepe@example.com")
                    .passwordHash("password")
                    .fullName("Pepe Paco")
                    .createdAt(new Timestamp(System.currentTimeMillis()))
                    .updatedAt(new Timestamp(System.currentTimeMillis()))
                    .build();


            // Crear y guardar una nota asociada al usuario
            Note note = Note.builder()
                    .title("Nota de Pepe 2")
                    .content("Contenido de la nota de Pepe 2")
                    .createdAt(new Timestamp(System.currentTimeMillis()))
                    .updatedAt(new Timestamp(System.currentTimeMillis()))
                    .user(user)
                    .build();


            noteRepository.save(note);
            userRepository.save(user);

        });
    }




    @Test
    void UnlinkUserNote_WhitOut_OrphanRemoveAndCascade() {
        // Configuracion
        // @OneToMany(mappedBy = "user")
        // private Set<Note> notes = new HashSet<>();

        transactionTemplate.executeWithoutResult(transactionStatus -> {

            //busco al usuario
            User user = userRepository.findById(11L).orElseThrow();

            // Eliminar la relación entre el usuario y la nota (sin eliminar la nota)
            Note noteUnlink = user.getNotes().iterator().next();
            // mantener la consistencia de la relación bidireccional entre el usuario y la nota
            // quitamos de la nota al usuario y de la lista de notas del usuario a la nota
            noteUnlink.setUser(null);
            user.getNotes().add(null);

            //  Guardar los cambios
            userRepository.save(user);
            noteRepository.save(noteUnlink);
        });
    }



}
