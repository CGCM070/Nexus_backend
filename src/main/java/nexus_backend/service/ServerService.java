package nexus_backend.service;

import jakarta.transaction.Transactional;
import nexus_backend.domain.Server;
import nexus_backend.exception.EntityNotFoundException;
import nexus_backend.repository.ServerRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class ServerService {

    private final ServerRepository serverRepository;

    public ServerService(ServerRepository serverRepository) {
        this.serverRepository = serverRepository;
    }

    @Transactional
    public Server createServer(Server server) {
         return  serverRepository.save(server);
    }

    public List<Server> getAllServers() {
        return serverRepository.findAll();
    }

    public Server getServerById(Long id) {
        return serverRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(id, "Server"));
    }

    public Server getServerByUserId(Long userId) {
        return serverRepository.findByUserId(userId).orElseThrow(() -> new EntityNotFoundException(userId, "User not have a server"));
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
        serverRepository.delete(server);
    }
}
