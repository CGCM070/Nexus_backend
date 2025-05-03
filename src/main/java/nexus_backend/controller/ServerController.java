package nexus_backend.controller;

import lombok.extern.slf4j.Slf4j;
import nexus_backend.domain.Channel;
import nexus_backend.domain.Server;
import nexus_backend.domain.User;
import nexus_backend.exception.EntityNotFoundException;
import nexus_backend.repository.ServerRepository;
import nexus_backend.repository.UserRepository;
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
    private final UserRepository userRepository;

    public ServerController(ServerService serverService,
                            ServerRepository serverRepository
    , UserRepository userRepository) {
        this.serverService = serverService;
        this.userRepository = userRepository;
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
        return serverService.assignServerToUser(serverId, userId);
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


    //Refactorizar luego
    @GetMapping("/user")
    public Server getUserServer() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();

        // Obtener usuario por correo electrónico en lugar de por nombre de usuario
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException(0L, "Usuario no encontrado"));

        return serverService.getServerByUserId(user.getId());
    }

    @GetMapping("/channels/invited")
    public List<Channel> getInvitedChannels() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException(0L, "Usuario no encontrado"));

        return serverService.getInvitedChannels(user.getId());
    }
}
