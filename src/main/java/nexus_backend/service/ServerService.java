package nexus_backend.service;

import nexus_backend.domain.Server;
import nexus_backend.repository.ServerRepository;
import org.springframework.stereotype.Service;

@Service
public class ServerService {

    private final ServerRepository serverRepository;

    public ServerService(ServerRepository serverRepository) {
        this.serverRepository = serverRepository;
    }

    public void createServer(Server server) {
        serverRepository.save(server);
    }
}
