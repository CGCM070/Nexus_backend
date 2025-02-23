package nexus_backend.user_notes.user_note_cascade_all;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import nexus_backend.domain.Note;
import nexus_backend.domain.User;
import nexus_backend.repository.NoteRepository;
import nexus_backend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.sql.Timestamp;
import java.util.Set;


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
    @Order(1)
    void CreateUserWithNote_Whit_CascadeAll() {
        // Configuracion
        // @OneToMany(mappedBy = "user", cascade = ALL)
        // private Set<Note> notes = new HashSet<>();

        transactionTemplate.executeWithoutResult(transactionStatus -> {
            User marta = User.builder()
                    .username("Marta")
                    .email("marta@example.com2")
                    .passwordHash("password")
                    .fullName("Marta Martinez")
                    .createdAt(new Timestamp(System.currentTimeMillis()))
                    .updatedAt(new Timestamp(System.currentTimeMillis()))
                    .build();


            // Crear y guardar una nota asociada al usuario
            Note note = Note.builder()
                    .title("Nota1 de Marta")
                    .content("Comprar manzanas")
                    .createdAt(new Timestamp(System.currentTimeMillis()))
                    .updatedAt(new Timestamp(System.currentTimeMillis()))
                    .user(marta)
                    .build();

            Note note2 = Note.builder()
                    .title("Nota2 de Marta")
                    .content("Ir de viaje")
                    .createdAt(new Timestamp(System.currentTimeMillis()))
                    .updatedAt(new Timestamp(System.currentTimeMillis()))
                    .user(marta)
                    .build();

            marta.getNotes().add(note);
            marta.getNotes().add(note2);
            userRepository.save(marta);


            User paco = User.builder()
                    .username("Paco")
                    .email("paco@example.com")
                    .passwordHash("password")
                    .fullName("Paco Perez")
                    .createdAt(new Timestamp(System.currentTimeMillis()))
                    .updatedAt(new Timestamp(System.currentTimeMillis()))
                    .build();

            Note note3 = Note.builder()
                    .title("Nota de Paco")
                    .content("Comprar pan")
                    .createdAt(new Timestamp(System.currentTimeMillis()))
                    .updatedAt(new Timestamp(System.currentTimeMillis()))
                    .user(paco)
                    .build();

            paco.getNotes().add(note3);
            userRepository.save(paco);

            User juan = User.builder()
                    .username("Juan")
                    .email("juan@example.com")
                    .passwordHash("password")
                    .fullName("Juan Jimenez")
                    .createdAt(new Timestamp(System.currentTimeMillis()))
                    .updatedAt(new Timestamp(System.currentTimeMillis()))
                    .build();

            // Juan no tiene notas
            userRepository.save(juan);

            // una nota sin usuario
            Note note4 = Note.builder()
                    .title("Nota sin usuario")
                    .content("Nota sin usuario")
                    .createdAt(new Timestamp(System.currentTimeMillis()))
                    .updatedAt(new Timestamp(System.currentTimeMillis()))
                    .build();

            noteRepository.save(note4);


        });
    }


    @Test
    @Order(2)
    void UnlinkUserNote_Whit_OrphanRemoveAndCascade() {
        // Configuracion
        // @OneToMany(mappedBy = "user", cascade = ALL, orphanRemoval = true)
        // private Set<Note> notes = new HashSet<>();

        transactionTemplate.executeWithoutResult(transactionStatus -> {

            //busco al usuario
            User paco = userRepository.findById(2L).orElseThrow();

            Note noteUnlink = paco.getNotes().iterator().next();
            // con el orphanRemoval = true al setear a null el usuario de la nota
            // se elimina la nota
            noteUnlink.setUser(null);

            //  Guardar los cambios
            userRepository.save(paco);

        });
    }


    @Test
    @Order(3)
    void DeleteUserNote_WhitOrphanRemoveAndCascadeAll() {
        // Configuracion
        // @OneToMany(mappedBy = "user", cascade = ALL, orphanRemoval = true)
        // private Set<Note> notes = new HashSet<>();

        transactionTemplate.executeWithoutResult(transactionStatus -> {

            User user = userRepository.findById(1L).orElseThrow();
            Note noteEliminar = user.getNotes().iterator().next();

            // ya no seteamos a null el usuario de la nota
            // sino que eliminamos la nota de la lista de notas del usuario
            user.getNotes().remove(noteEliminar);

            userRepository.save(user);
        });
    }


    @Test
    @Order(4)
    void DeleteSpecificNote_WithOrphanRemoveAndCascadeAll() {
        // Configuracion
        // @OneToMany(mappedBy = "user", cascade = ALL, orphanRemoval = true)
        // private Set<Note> notes = new HashSet<>();

        transactionTemplate.executeWithoutResult(transactionStatus -> {

            User marta = userRepository.findById(1L).orElseThrow();


            //  Simular que el frontend nos envía el ID de la nota a eliminar
            Long noteIdToDelete = marta.getNotes().iterator().next().getId();

            Note noteToRemove = marta.getNotes().stream()
                    .filter(note -> note.getId().equals(noteIdToDelete))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Nota no encontrada"));

            //  Eliminar la nota de la colección de notas del usuario
            marta.getNotes().remove(noteToRemove);


            userRepository.save(marta);
        });
    }


    @Test
    @Order(5)
    void UpdatedNoteToUser (){
        // en base de datos tenemos una nota sin usuario el noteId es 4
        // y un usuario sin notas el userId es 3 juan

        transactionTemplate.executeWithoutResult(transactionStatus -> {

            User juan = userRepository.findById(3L).orElseThrow();
            Note note = noteRepository.findById(4L).orElseThrow();
            // En note tenemos el id del usuario
            note.setUser(juan);

            // guardamos la nota
            noteRepository.save(note);
        });
    };


    @Test
    @Order(6)
    void UpdateUserNote (){

        transactionTemplate.executeWithoutResult(transactionStatus -> {

            User juan = userRepository.findById(3L).orElseThrow();
            Note noteModificar = juan.getNotes().iterator().next();
            // Modificamos la nota
            noteModificar.setTitle("Nota modificada");
            noteModificar.setContent("Contenido modificado");

            // guardamos la nota del lado del cascade all y se propaga a la nota
            userRepository.save(juan);
        });
    };
}


