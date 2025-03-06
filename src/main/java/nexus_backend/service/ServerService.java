package nexus_backend.service;

import jakarta.transaction.Transactional;
import nexus_backend.domain.Server;
import nexus_backend.domain.User;
import nexus_backend.exception.EntityNotFoundException;
import nexus_backend.repository.ServerRepository;
import nexus_backend.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;

@Service
public class ServerService {

    private final ServerRepository serverRepository;
    private final UserRepository userRepository;

    public ServerService(ServerRepository serverRepository, UserRepository userRepository) {
        this.serverRepository = serverRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public Server createServer(Server server) {
        return serverRepository.save(server);
    }

    public List<Server> getAllServers() {
        return serverRepository.findAll();
    }

    public Server getServerById(Long id) {
        return serverRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(id, "Server"));
    }

    @Transactional
    public Server assignServerToUser(Long userId, Long serverId) {
        Server server = serverRepository.findById(serverId).orElseThrow(() -> new EntityNotFoundException(serverId, "Server"));
        User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException(userId, "User"));
        server.setUser(user);
        user.setPersonalServer(server);
        userRepository.save(user);
        return serverRepository.save(server);
    }

    public Server getServerByUserId(Long userId) {
        return serverRepository.findByUserId(userId).orElseThrow(() -> new EntityNotFoundException(userId, "{ User not have a server }"));
    }

    @Transactional
    public Server updateServer(Long id, Server serverDetails) {
        Server server = serverRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(id, "Server"));
        server.setName(serverDetails.getName());
        server.setDescription(serverDetails.getDescription());
        return serverRepository.save(server);
    }

    @Transactional
    public void deleteServer(Long id) {
        Server server = serverRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(id, "Server"));
        User user = server.getUser();
        user.setPersonalServer(null);
        server.setUser(null);
        serverRepository.delete(server);
    }

    // Crear servidor personal
    @Transactional
    public Server createPersonalServerForUser(User user) {
        // Crear servidor personal
        Server personalDashboard = Server.builder()
                .name("Dashboard Personal de " + user.getUsername())
                .description("Tu espacio personal en Nexus")
                .createdAt(new Timestamp(System.currentTimeMillis()))
                .build();

        // Guardar y asignar al usuario
        Server savedServer = createServer(personalDashboard);
        assignServerToUser(user.getId(), savedServer.getId());

        return savedServer;
    }

}
