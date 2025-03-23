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
        String username = auth.getName();

        // Verificar si tiene rol OWNER o ADMIN en el canal
        boolean hasRole = channelUserRoleRepository.findByChannelIdAndUserUsername(channelId, username)
                .map(role -> role.getRole() == EChannelRole.OWNER || role.getRole() == EChannelRole.ADMIN)
                .orElse(false);

        if (hasRole) {
            return true;
        }

        // Verificar si es propietario del servidor que contiene el canal
        return channelRepository.findById(channelId)
                .map(channel -> {
                    User serverOwner = channel.getServer().getUser();
                    return serverOwner != null && serverOwner.getUsername().equals(username);
                })
                .orElse(false);
    }
    public boolean canAccessChannel(Long channelId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        // Verificar si el usuario tiene un rol válido en el canal (ADMIN o OWNER)
        return channelUserRoleRepository.findByChannelIdAndUserUsername(channelId, username)
                .map(role -> role.getRole() == EChannelRole.OWNER || role.getRole() == EChannelRole.ADMIN || role.getRole() == EChannelRole.MEMBER)
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
        String username = auth.getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }
}