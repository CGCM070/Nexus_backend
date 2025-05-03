package nexus_backend.service;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import nexus_backend.domain.Channel;
import nexus_backend.domain.Server;
import nexus_backend.domain.User;
import nexus_backend.exception.EntityNotFoundException;
import nexus_backend.repository.ChannelRepository;
import nexus_backend.repository.ServerRepository;
import nexus_backend.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Service
public class UserService {

    private static final String DEFAULT_AVATAR_URL = "/assets/default-avatar.png";

    private final UserRepository userRepository;
    private final ServerRepository serverRepository;
    private final ChannelRepository channelRepository;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Transactional
    public User createUser(User user) {
        return userRepository.save(user);
    }

    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(id, "User"));
    }
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    public User updateUser(Long id, User user) {
        return userRepository.findById(id).map(u -> (id.equals(user.getId()) ? userRepository.save(user) : null))
                .orElseThrow(() -> new EntityNotFoundException(id, "User"));
    }

    @Transactional
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(id, "User"));

        // 1. Desconectar de los canales donde está invitado
        user.getInvitedChannels().forEach(channel ->
                channel.getInvitedUsers().remove(user));
        user.getInvitedChannels().clear();


        // 3. Desconectar tareas asignadas
        user.getAssignedTasks().forEach(task ->
                task.setAssignedTo(null));
        user.getAssignedTasks().clear();

        // 4. Manejar el servidor personal y sus canales
        if (user.getPersonalServer() != null) {
            Server server = user.getPersonalServer();

            // Primero eliminar todos los canales del servidor
            for (Channel channel : new HashSet<>(server.getChannels())) {
                // Limpiar las colecciones en el canal para evitar referencias circulares
                channel.getMessages().clear();
                channel.getNotes().clear();
                channel.getTasks().clear();
                channel.getInvitedUsers().clear();

                // Desconectar el canal del servidor
                channel.setServer(null);
            }

            // Limpiar la colección de canales del servidor
            server.getChannels().clear();

            // Desconectar el servidor del usuario
            user.setPersonalServer(null);
            server.setUser(null);

            // Eliminar el servidor
            serverRepository.delete(server);
        }

        // 5. Finalmente eliminar el usuario
        userRepository.delete(user);

    }

    public User updateAvatar(Long userId, String avatarUrl) {
        User user = findById(userId);

        user.setAvatarUrl(avatarUrl);
        user.setAvatarUrl(avatarUrl.trim().isEmpty() ? DEFAULT_AVATAR_URL : avatarUrl);
        return userRepository.save(user);
    }
}
