package nexus_backend;


import nexus_backend.domain.Server;
import nexus_backend.domain.User;
import nexus_backend.repository.ServerRepository;
import nexus_backend.repository.UserRepository;
import nexus_backend.service.ServerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.annotation.Order;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class ServerTest {

    @Autowired
    private ServerRepository serverRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ServerService serverService;

    @Autowired
    private PlatformTransactionManager transactionManager;
    private TransactionTemplate transactionTemplate;

    @BeforeEach
    public void setUp() {
        transactionTemplate = new TransactionTemplate(transactionManager);
    }


    @Test
    @Order(1)
    void createServer() {
        transactionTemplate.executeWithoutResult(transactionStatus -> {
            // Crear un nuevo usuario
            User user = User.builder()
                    .username("TestUser")
                    .email("testuser@example.com")
                    .passwordHash("password")
                    .fullName("Test User")
                    .createdAt(new Timestamp(System.currentTimeMillis()))
                    .updatedAt(new Timestamp(System.currentTimeMillis()))
                    .build();
            userRepository.save(user);

            // Crear un nuevo servidor
            Server server = Server.builder()
                    .name("Test Server")
                    .description("Test Server Description")
                    .createdAt(new Timestamp(System.currentTimeMillis()))
                    .updatedAt(new Timestamp(System.currentTimeMillis()))
                    .user(user)
                    .build();
            serverService.createServer(server);

            // Verificar que el servidor ha sido creado
            Server createdServer = serverRepository.findById(server.getId()).orElseThrow(() -> new RuntimeException("Server not found"));
            assertNotNull(createdServer);
        });
    }

    @Test
    @Order(2)
    void getServerById(){
        transactionTemplate.executeWithoutResult(transactionStatus -> {
            Server server = serverRepository.findById(1L).orElseThrow(() -> new RuntimeException("Server not found"));

            Server foundServer = serverRepository.findById(server.getId()).orElseThrow(() -> new RuntimeException("Server not found"));

            assertEquals(server, foundServer);
        });
    }

    @Test
    @Order(3)
    void getServerByUser(){
        transactionTemplate.executeWithoutResult(transactionStatus -> {
            User user = userRepository.findById(1L).orElseThrow(() -> new RuntimeException("User not found"));

            Server server = serverRepository.findByUser(user).orElseThrow(() -> new RuntimeException("Server not found"));

            assertNotNull(server);

        });
    }

    @Test
    @Order(4)
    void updateServer(){
        transactionTemplate.executeWithoutResult(transactionStatus -> {
            Server server = serverRepository.findById(1L).orElseThrow(() -> new RuntimeException("Server not found"));

            server.setName("Updated Server Name");
            server.setDescription("Updated Server Description");
            server.setUpdatedAt(new Timestamp(System.currentTimeMillis()));

            serverRepository.save(server);

            Server updatedServer = serverRepository.findById(server.getId()).orElseThrow(() -> new RuntimeException("Server not found"));

            assertEquals(server, updatedServer);
        });
    }

    @Test
    @Order(5)
    void deleteServer(){
        transactionTemplate.executeWithoutResult(transactionStatus -> {
            Server server = serverRepository.findById(1L).orElseThrow(() -> new RuntimeException("Server not found"));
            serverRepository.delete(server);


        });
    }

    @Test
    @Order(6)
    void serverForUser() {
        transactionTemplate.executeWithoutResult(transactionStatus -> {
            Server server = serverRepository.findById(2L).orElseThrow(() -> new RuntimeException("Server not found"));
            User user = userRepository.findById(2L).orElseThrow(() -> new RuntimeException("User not found"));
            user.setPersonalServer(server);
            server.setUser(user);
            userRepository.save(user);
            serverRepository.save(server);
        });
    }
}
