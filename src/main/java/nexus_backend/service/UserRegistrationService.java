package nexus_backend.service;


import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import nexus_backend.domain.Channel;
import nexus_backend.domain.ChannelUserRole;
import nexus_backend.domain.Server;
import nexus_backend.domain.User;
import nexus_backend.enums.EChannelRole;
import nexus_backend.repository.ChannelUserRoleRepository;
import org.springframework.stereotype.Service;


@AllArgsConstructor
@Service
public class UserRegistrationService {

    private final UserService userService;
    private final ServerService serverService;
    private final ChannelService channelService;
    private final NoteService noteService;
    private final ChannelUserRoleRepository channelUserRoleRepository;


    @Transactional
    public User registerUser(User newUser) {
        User savedUser = userService.createUser(newUser);
        Server personalServer = serverService.createPersonalServerForUser(savedUser);
        Channel welcomeChannel = channelService.createDefaultChannelForServer(personalServer);

        // Asignar rol OWNER al usuario en su canal
        ChannelUserRole ownerRole = ChannelUserRole.builder()
                .user(savedUser)
                .channel(welcomeChannel)
                .role(EChannelRole.OWNER)
                .build();
        channelUserRoleRepository.save(ownerRole);

        noteService.createWelcomeNoteForUser(savedUser, welcomeChannel);
        return savedUser;
    }
}