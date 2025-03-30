package nexus_backend;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import nexus_backend.domain.Channel;
import nexus_backend.domain.Server;
import nexus_backend.domain.User;
import nexus_backend.enums.ERol;
import nexus_backend.exception.EntityNotFoundException;
import nexus_backend.repository.ChannelRepository;
import nexus_backend.repository.ServerRepository;
import nexus_backend.repository.UserRepository;
import nexus_backend.service.UserRegistrationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

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
    private PasswordEncoder passwordEncoder;

    @Test
    @Order(1)
    void registerUser_createsUserServerAndWelcomeChannel() {
        transactionTemplate.executeWithoutResult(transactionStatus -> {
            // Crear roles para los usuarios
            Set<ERol> adminRoles = new HashSet<>();
            adminRoles.add(ERol.ROL_ADMIN);
            adminRoles.add(ERol.ROL_USER);



            // Crear un usuario con rol de administrador
            User user = User.builder()
                    .username("Dennis")
                    .email("dennis@example.com")
                    .passwordHash(passwordEncoder.encode("password"))
                    .fullName("Dennis Torres")
                    .roles(adminRoles)
                    .createdAt(new Timestamp(System.currentTimeMillis()))
                    .updatedAt(new Timestamp(System.currentTimeMillis()))
                    .build();

            // Crear un usuario con rol estándar
            User user2 = User.builder()
                    .username("Cesar")
                    .email("cesar.gabriel.martinezs7@gmail.com")
                    .passwordHash(passwordEncoder.encode("password"))
                    .fullName("cesar castillo")
                    .roles(adminRoles)
                    .createdAt(new Timestamp(System.currentTimeMillis()))
                    .updatedAt(new Timestamp(System.currentTimeMillis()))
                    .build();

            // Registrar los usuarios
            userRegistrationService.registerUser(user);
            userRegistrationService.registerUser(user2);

            // Verificar que el usuario administrador fue creado correctamente
            User adminUser = userRepository.findByEmail("dennis@example.com").orElseThrow(
                    () -> new EntityNotFoundException(0L, "Usuario administrador no existe")
            );
            assertNotNull(adminUser.getId());
            assertTrue(adminUser.getRoles().contains(ERol.ROL_ADMIN));
            assertTrue(adminUser.getRoles().contains(ERol.ROL_USER));
            assertEquals(2, adminUser.getRoles().size());

            // Verificar que el usuario estándar fue creado correctamente
            User normalUser = userRepository.findByEmail("cesar.gabriel.martinezs7@gmail.com").orElseThrow(
                    () -> new EntityNotFoundException(0L, "Usuario estándar no existe")
            );
            assertNotNull(normalUser.getId());
            assertTrue(normalUser.getRoles().contains(ERol.ROL_USER));
            assertEquals(2, normalUser.getRoles().size());

            // Verificar servidor personal
            Server personalServer = serverRepository.findByUser(adminUser).orElseThrow(() ->
                    new RuntimeException("Server not found"));
            assertEquals("Dashboard Personal de Dennis", personalServer.getName());

            // Verificar canal de bienvenida
            Channel welcomeChannel = channelRepository.findByServerAndName(personalServer, "Bienvenido").orElseThrow();
            assertEquals("Bienvenido", welcomeChannel.getName());


        });
    }
}