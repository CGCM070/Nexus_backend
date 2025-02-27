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
public class ChannelServiceTest {

    @Autowired
    private ChannelRepository channelRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ChannelService channelService;

    @Autowired
    private NoteService noteService;

    @Autowired
    private ServerRepository serverRepository;

    @PersistenceContext
    EntityManager entityManager;

    @Autowired
    private PlatformTransactionManager transactionManager;
    private TransactionTemplate transactionTemplate;

    @BeforeEach
    public void setUp() {
        transactionTemplate = new TransactionTemplate(transactionManager);
    }

    @Test
    @Order(1)
    void createNewChannelInServer() {
        transactionTemplate.executeWithoutResult(transactionStatus -> {
            // Buscar el servidor creado en el test anterior
            Server personalServer = serverRepository.findById(1L).orElseThrow(() -> new RuntimeException("Server not found"));

            // Crear un nuevo canal en el servidor
            Channel newChannel = Channel.builder()
                    .name("Nuevo Canal")
                    .description("Descripción del nuevo canal")
                    .createdAt(new Timestamp(System.currentTimeMillis()))
                    .updatedAt(new Timestamp(System.currentTimeMillis()))
                    .server(personalServer)
                    .build();
            channelService.createChannel(newChannel);

            // Verificar que el canal ha sido creado
            Channel createdChannel = channelRepository.findById(newChannel.getId()).orElseThrow(() -> new RuntimeException("Channel not found"));
//            assertNotNull(createdChannel);
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

            //si se quiere buscar un usuario existente
            // User newUser = userRepository.findById(2L).orElseThrow(() -> new RuntimeException("User not found"));

            // Obtener el canal existente
            Channel channel = channelRepository.findById(2L).orElseThrow(() -> new RuntimeException("Channel not found"));

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
    void deleteUserInChannel() {
        transactionTemplate.executeWithoutResult(transactionStatus -> {
            // Buscar el usuario creado en el test anterior
            User newUser = userRepository.findById(2L).orElseThrow(() -> new RuntimeException("User not found"));

            // Eliminar el usuario
            channelService.removeUserFromChannel(2L, newUser.getId());
            // Verificar que el usuario ha sido eliminado
            //assertThrows(EntityNotFoundException.class, () -> userRepository.findById(newUser.getId()));
        });
    }

    @Test
    @Order(5)
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
    @Order(6)
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


    @Test
    @Order(7)
    void deleteChannelInServer() {
        transactionTemplate.executeWithoutResult(transactionStatus -> {
            // Buscar el canal creado en el test anterior
            Channel existingChannel = channelRepository.findById(2L).orElseThrow(() -> new RuntimeException("Channel not found"));

            // Eliminar el canal
            channelService.deleteChannel(existingChannel.getId());

            // Verificar que el canal ha sido eliminado
            //assertThrows(EntityNotFoundException.class, () -> channelRepository.findById(existingChannel.getId()));
        });
    }

    @Test
    @Order(8)
    void updateChannelInServer() {
        transactionTemplate.executeWithoutResult(transactionStatus -> {
                    // Buscar el canal creado en el test anterior
                    Channel existingChannel = channelRepository.findById(1L).orElseThrow(() -> new RuntimeException("Channel not found"));

                    // Actualizar el canal
                    existingChannel.setName("Canal Actualizado");
                    existingChannel.setDescription("Descripción actualizada del canal");
                    channelService.updateChannel(existingChannel);

                    // Verificar que el canal ha sido actualizado
                    Channel updatedChannel = channelRepository.findById(existingChannel.getId()).orElseThrow(() -> new RuntimeException("Channel not found"));

                }
        );
    }
}

