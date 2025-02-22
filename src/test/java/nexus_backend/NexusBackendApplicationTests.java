package nexus_backend;

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
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class NexusBackendApplicationTests {


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
    void saveUserWithNote_WhitOutCascade() {


        // Configuracion
        // @OneToMany(mappedBy = "user")
        // private Set<Note> notes = new HashSet<>();

        transactionTemplate.executeWithoutResult(transactionStatus -> {
            User user = User.builder()
                    .username("testuser")
                    .email("testuser@example.com")
                    .passwordHash("password")
                    .fullName("Test User")
                    .createdAt(new Timestamp(System.currentTimeMillis()))
                    .updatedAt(new Timestamp(System.currentTimeMillis()))
                    .build();


            // Crear y guardar una nota asociada al usuario
            Note note = Note.builder()
                    .title("Sample Title")
                    .content("Sample Content")
                    .createdAt(new Timestamp(System.currentTimeMillis()))
                    .updatedAt(new Timestamp(System.currentTimeMillis()))
                    .user(user)
                    .build();






            note.setUser(user);
            user.getNotes().add(note);

            noteRepository.save(note);
            userRepository.save(user);




        });
    }


    @Test
    void saveUserWithNote_Whit_CascadeAll() {


        // Configuracion
        // @OneToMany(mappedBy = "user", cascade = ALL)
        // private Set<Note> notes = new HashSet<>();

        transactionTemplate.executeWithoutResult(transactionStatus -> {
            User user = User.builder()
                    .username("testuser2")
                    .email("testuser@example.com2")
                    .passwordHash("password2")
                    .fullName("Test User2")
                    .createdAt(new Timestamp(System.currentTimeMillis()))
                    .updatedAt(new Timestamp(System.currentTimeMillis()))
                    .build();


            // Crear y guardar una nota asociada al usuario
            Note note = Note.builder()
                    .title("Sample Title2")
                    .content("Sample Content2")
                    .createdAt(new Timestamp(System.currentTimeMillis()))
                    .updatedAt(new Timestamp(System.currentTimeMillis()))
                    .user(user)
                    .build();


            user.getNotes().add(note);
            userRepository.save(user);


        });
    }



    @Test
    void UnlinkUserNote_WhitOrphanRemove() {
        // Configuracion
        // @OneToMany(mappedBy = "user", cascade = ALL, orphanRemoval = true)
        // private Set<Note> notes = new HashSet<>();

        transactionTemplate.executeWithoutResult(transactionStatus -> {

        User user = userRepository.findById(7L).orElseThrow();
        Note noteEliminar = user.getNotes().iterator().next();
        user.getNotes().remove(noteEliminar);

        userRepository.save(user);

        });


    }


    @Test
    void DeleteSpecificNote_WithOrphanRemove() {
        // Configuracion
        // @OneToMany(mappedBy = "user", cascade = ALL, orphanRemoval = true)
        // private Set<Note> notes = new HashSet<>();

        transactionTemplate.executeWithoutResult(transactionStatus -> {

            User user = userRepository.findById(9L).orElseThrow();


            //  Simular que el frontend nos envía el ID de la nota a eliminar
            Long noteIdToDelete = user.getNotes().iterator().next().getId();

            Note noteToRemove = user.getNotes().stream()
                    .filter(note -> note.getId().equals(noteIdToDelete))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Nota no encontrada"));

            //  Eliminar la nota de la colección de notas del usuario
            user.getNotes().remove(noteToRemove);


            userRepository.save(user);
        });


    }

}
