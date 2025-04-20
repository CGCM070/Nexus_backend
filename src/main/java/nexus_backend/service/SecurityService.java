package nexus_backend.service;


import nexus_backend.domain.User;
import nexus_backend.enums.EChannelRole;
import nexus_backend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

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

        if (resourceType.equals("note")) {
            return noteRepository.findById(resourceId)
                    .map(note -> note.getUser().getId().equals(currentUser.getId()))
                    .orElse(false);
        } else if (resourceType.equals("message")) {
            return messageRepository.findById(resourceId)
                    .map(message -> message.getUser().getId().equals(currentUser.getId()))
                    .orElse(false);
        }

        return false;
    }

    private User getUserFromAuth(Authentication auth) {
        String email = auth.getName(); // auth.getName() devuelve el email
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }
}