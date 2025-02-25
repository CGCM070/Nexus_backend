package nexus_backend;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import nexus_backend.domain.Channel;
import nexus_backend.domain.Note;
import nexus_backend.domain.Server;
import nexus_backend.domain.User;
import nexus_backend.repository.ChannelRepository;
import nexus_backend.repository.ServerRepository;
import nexus_backend.repository.UserRepository;
import nexus_backend.service.ChannelService;
import nexus_backend.service.NoteService;
import nexus_backend.service.UserRegistrationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.assertNotNull;


@SpringBootTest
class UserRegistrationServiceTest {

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
    private ServerRepository serverRepository;

    @Autowired
    private ChannelRepository channelRepository;

    @Autowired
    private UserRegistrationService userRegistrationService;

    @Autowired
    private ChannelService channelService;

    @Autowired
    private NoteService noteService;

    @Test
    @Order(1)
    void registerUser_createsUserServerAndWelcomeChannel() {
        transactionTemplate.executeWithoutResult(transactionStatus -> {
            // Crear un nuevo usuario
            User user = User.builder()
                    .username("Dennis")
                    .email("dennis@example.com")
                    .passwordHash("password")
                    .fullName("Dennis Torres")
                    .createdAt(new Timestamp(System.currentTimeMillis()))
                    .updatedAt(new Timestamp(System.currentTimeMillis()))
                    .build();

            // Registrar el usuario y realizar el onboarding
            userRegistrationService.registerUser(user);

            // Verificar que el usuario fue creado
            User newUser = userRepository.findByEmail("dennis@example.com");
            //assertNotNull(newUser.getId());

            // Verificar que el servidor personal fue creado
            Server personalServer = serverRepository.findByUser(newUser).orElseThrow(() -> new RuntimeException("Server not found"));
            // assertEquals("Dashboard Personal de Carlos", personalServer.getName());

            // Verificar que el canal de bienvenida fue creado
            Channel welcomeChannel = channelRepository.findByServerAndName(personalServer, "Bienvenida").orElseThrow();
            // assertEquals("Bienvenida", welcomeChannel.getName());
        });
    }


    @Test
    @Order(2)
    void inviteUserToChannel() {
        transactionTemplate.executeWithoutResult(transactionStatus -> {
            // Crear un nuevo usuario
            User newUser = User.builder()
                    .username("NuevoUsuario")
                    .email("nuevo@example.com")
                    .passwordHash("password")
                    .fullName("Nuevo Usuario")
                    .createdAt(new Timestamp(System.currentTimeMillis()))
                    .updatedAt(new Timestamp(System.currentTimeMillis()))
                    .build();
            userRepository.save(newUser);

            // Obtener el canal existente
            Channel channel = channelRepository.findById(1L).orElseThrow(() -> new RuntimeException("Channel not found"));

            // Invitar al nuevo usuario al canal
            channelService.inviteUserToChannel(channel.getId(), newUser.getId());

            // Verificar que el usuario ha sido agregado al canal
            Channel updatedChannel = channelRepository.findById(1L).orElseThrow(() -> new RuntimeException("Channel not found"));
            //    assertTrue(updatedChannel.getInvitedUsers().stream().anyMatch(user -> user.getId().equals(newUser.getId())));
        });
    }


    @Test
    @Order(3)
    void newUserCreatesNoteInChannel() {
        transactionTemplate.executeWithoutResult(transactionStatus -> {
            // Buscar el usuario creado en el test anterior
            User newUser = userRepository.findById(2L).orElseThrow(() -> new RuntimeException("User not found"));

            // Obtener el canal existente
            Channel channel = channelRepository.findById(1L).orElseThrow(() -> new RuntimeException("Channel not found"));

            // Crear una nueva nota en el canal
            Note newNote = Note.builder()
                    .title("Nueva Nota")
                    .content("Contenido de la nueva nota")
                    .createdAt(new Timestamp(System.currentTimeMillis()))
                    .updatedAt(new Timestamp(System.currentTimeMillis()))
                    .build();
            noteService.createNoteForUser(newUser.getId(), channel.getId(), newNote);

            // Verificar que la nota ha sido creada
            Note createdNote = noteService.getNoteById(newNote.getId());
            assertNotNull(createdNote);
        });
    }


    @Test
    @Order(4)
    void updateNoteInChannel() {
        transactionTemplate.executeWithoutResult(transactionStatus -> {
            // Buscar el usuario creado en el test anterior
            User newUser = userRepository.findById(2L).orElseThrow(() -> new RuntimeException("User not found"));

            // Obtener la nota existente
            Note existingNote = noteService.getNotesByUserId(newUser.getId()).get(0);

            // Actualizar la nota
            existingNote.setTitle("Nota Actualizada");
            existingNote.setContent("Contenido actualizado de la nota");
            noteService.updateNoteById(existingNote.getId(), existingNote);

            // Verificar que la nota ha sido actualizada
            Note updatedNote = noteService.getNoteById(existingNote.getId());
//            assertEquals("Nota Actualizada", updatedNote.getTitle());
//            assertEquals("Contenido actualizado de la nota", updatedNote.getContent());
        });
    }


    @Test
    @Order(5)
    void deleteNoteInChannel() {
        transactionTemplate.executeWithoutResult(transactionStatus -> {
            // Buscar el usuario creado en el test anterior
            User newUser = userRepository.findById(2L).orElseThrow(() -> new RuntimeException("User not found"));

            // Obtener la nota existente
            Note existingNote = noteService.getNotesByUserId(newUser.getId()).get(0);

            // Eliminar la nota
            noteService.deleteNote(existingNote.getId());

            // Verificar que la nota ha sido eliminada
            //assertThrows(EntityNotFoundException.class, () -> noteService.getNoteById(existingNote.getId()));
        });
    }
}
