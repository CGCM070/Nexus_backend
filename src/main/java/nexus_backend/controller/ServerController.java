package nexus_backend.controller;

import lombok.extern.slf4j.Slf4j;
import nexus_backend.domain.Server;
import nexus_backend.repository.ServerRepository;
import nexus_backend.service.ServerService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/v1/api/server")
public class ServerController {

    private final ServerService serverService;
    private final ServerRepository serverRepository;

    public ServerController(ServerService serverService, ServerRepository serverRepository) {
        this.serverService = serverService;
        this.serverRepository = serverRepository;
    }

    @GetMapping("")
    public List<Server> getAllServers() {
        log.info("Fetching all servers");
        return serverService.getAllServers();
    }

    @GetMapping("/{id}")
    public Server getServerById(@PathVariable Long id) {
        log.info("Fetching server with ID: {}", id);
        return serverService.getServerById(id);
    }

    @GetMapping("/user/{userId}")
    public Server getServerByUserId(@PathVariable Long userId) {
        log.info("Fetching server for user with ID: {}", userId);
        return serverService.getServerByUserId(userId);
    }

    @PostMapping("")
    public Server createServer(@RequestBody Server server) {
        log.info("Creating new server");
        return serverService.createServer(server);
    }

    @PostMapping("/{serverId}/user/{userId}")
    public Server assignServerToUser(@PathVariable Long serverId, @PathVariable Long userId) {
        return  serverService.assignServerToUser(serverId, userId);
    }


    @PutMapping("/{id}")
    public Server updateServer(@PathVariable Long id, @RequestBody Server serverDetails) {
        log.info("Updating server with ID: {}", id);
        return serverService.updateServer(id, serverDetails);
    }

    @PreAuthorize("@securityService.isServerOwner(#id)")
    @DeleteMapping("/{id}")
    public void deleteServer(@PathVariable Long id) {
        serverService.deleteServer(id);
    }


    public boolean isServerOwner(Long serverId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        return serverRepository.findById(serverId)
                .map(server -> server.getUser().getUsername().equals(username))
                .orElse(false);
    }
}
