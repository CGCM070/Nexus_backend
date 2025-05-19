package nexus_backend.service;


import nexus_backend.domain.ChannelUserRole;
import nexus_backend.domain.Message;
import nexus_backend.domain.User;
import nexus_backend.enums.EChannelRole;
import nexus_backend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class SecurityService {

    @Autowired
    private ChannelRepository channelRepository;

    @Autowired
    private ChannelUserRoleRepository channelUserRoleRepository;

    @Autowired
    private NoteRepository noteRepository;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaskRepository taskRepository;

    // Para verificar si el usuario puede administrar un canal (OWNER o ADMIN)
    public boolean canManageChannel(Long channelId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();

        // Usar email en lugar de username
        boolean hasRole = channelUserRoleRepository.findByChannelIdAndUserEmail(channelId, email)
                .map(role -> role.getRole() == EChannelRole.OWNER || role.getRole() == EChannelRole.ADMIN)
                .orElse(false);

        if (hasRole) return true;

        // También actualizar esta verificación
        return channelRepository.findById(channelId)
                .map(channel -> {
                    User serverOwner = channel.getServer().getUser();
                    return serverOwner != null && serverOwner.getEmail().equals(email);
                })
                .orElse(false);
    }
    public boolean canAccessChannel(Long channelId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();

        // Verificar rol en el canal
        boolean hasRole = channelUserRoleRepository.findByChannelIdAndUserEmail(channelId, email)
                .map(role -> role.getRole() == EChannelRole.OWNER ||
                             role.getRole() == EChannelRole.ADMIN ||
                             role.getRole() == EChannelRole.MEMBER)
                .orElse(false);

        if (hasRole) return true;

        // Verificar si es propietario del servidor
        return channelRepository.findById(channelId)
                .map(channel -> {
                    User serverOwner = channel.getServer().getUser();
                    return serverOwner != null && serverOwner.getEmail().equals(email);
                })
                .orElse(false);
    }

    // Para verificar si puede modificar recursos específicos
    public boolean canModifyResource(Long resourceId, String resourceType) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = getUserFromAuth(auth);

        switch (resourceType.toLowerCase()) {
            case "note":
                return noteRepository.findById(resourceId)
                        .map(note -> {
                            // El creador puede modificar
                            if (note.getUser().getId().equals(currentUser.getId())) {
                                return true;
                            }
                            // OWNER o ADMIN del canal pueden modificar
                            return channelUserRoleRepository
                                           .findByChannelIdAndUserEmail(note.getChannel().getId(), currentUser.getEmail())
                                           .map(role -> role.getRole() == EChannelRole.OWNER || role.getRole() == EChannelRole.ADMIN)
                                           .orElse(false)
                                   // O es el dueño del servidor
                                   || isChannelOwner(note.getChannel().getId(), currentUser.getEmail());
                        })
                        .orElse(false);
            case "message":
                // El creador del mensaje puede modificarlo
                return messageRepository.findById(resourceId)
                        .map(message -> message.getUser().getId().equals(currentUser.getId()))
                        .orElse(false);
            case "task":
                return taskRepository.findById(resourceId)
                        .map(task -> {
                            if (task.getCreator().getId().equals(currentUser.getId())) {
                                return true;
                            }
                            // ADMIN u OWNER en la tabla de roles
                            boolean hasRole = channelUserRoleRepository
                                    .findByChannelIdAndUserEmail(task.getChannel().getId(), currentUser.getEmail())
                                    .map(role -> role.getRole() == EChannelRole.OWNER ||
                                                 role.getRole() == EChannelRole.ADMIN)
                                    .orElse(false);
                            if (hasRole) return true;
                            // O es el dueño del canal (dueño del servidor)
                            return isChannelOwner(task.getChannel().getId(), currentUser.getEmail());
                        })
                        .orElse(false);
            default:
                return false;
        }
    }

    // Nuevo método auxiliar
    private boolean isChannelOwner(Long channelId, String email) {
        return channelRepository.findById(channelId)
                .map(channel -> {
                    User serverOwner = channel.getServer().getUser();
                    return serverOwner != null && serverOwner.getEmail().equals(email);
                })
                .orElse(false);
    }
    public boolean canModifyMessage(Long messageId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Optional<Message> messageOpt = messageRepository.findById(messageId);

        if (messageOpt.isEmpty() || auth == null) {
            return false;
        }

        Message message = messageOpt.get();
        String email = auth.getName(); // Usando email en lugar de username

        // Verificar si es el autor del mensaje
        if (message.getUser().getEmail().equals(email)) {
            return true;
        }

        // Verificar roles de administrador en el canal
        Optional<ChannelUserRole> roleOpt = channelUserRoleRepository
                .findByChannelIdAndUserEmail(message.getChannel().getId(), email);

        return roleOpt.isPresent() &&
               (roleOpt.get().getRole() == EChannelRole.ADMIN ||
                roleOpt.get().getRole() == EChannelRole.OWNER);
    }

    private User getUserFromAuth(Authentication auth) {
        String email = auth.getName(); // auth.getName() devuelve el email
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }
}