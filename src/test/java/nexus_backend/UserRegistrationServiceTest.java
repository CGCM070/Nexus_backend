package nexus_backend;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import nexus_backend.domain.Channel;
import nexus_backend.domain.Server;
import nexus_backend.domain.User;
import nexus_backend.repository.ChannelRepository;
import nexus_backend.repository.ServerRepository;
import nexus_backend.repository.UserRepository;
import nexus_backend.service.UserRegistrationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.assertEquals;
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

//            User user2 = User.builder()
//                    .username("User2")
//                    .email("user@example.com")
//                    .passwordHash("password")
//                    .fullName("User2 Torres")
//                    .createdAt(new Timestamp(System.currentTimeMillis()))
//                    .updatedAt(new Timestamp(System.currentTimeMillis()))
//                    .build();

            // Registrar el usuario y realizar el onboarding
            userRegistrationService.registerUser(user);
         //   userRegistrationService.registerUser(user2);

            // Verificar que el usuario fue creado
            User newUser = userRepository.findByEmail("dennis@example.com");
            assertNotNull(newUser.getId());

            // Verificar que el servidor personal fue creado
            Server personalServer = serverRepository.findByUser(newUser).orElseThrow(() -> new RuntimeException("Server not found"));
             assertEquals("Dashboard Personal de Dennis", personalServer.getName());

            // Verificar que el canal de bienvenida fue creado
            Channel welcomeChannel = channelRepository.findByServerAndName(personalServer, "Bienvenido").orElseThrow();
             assertEquals("Bienvenido", welcomeChannel.getName());
        });
    }

}
