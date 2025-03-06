package nexus_backend.service;


import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import nexus_backend.domain.Channel;
import nexus_backend.domain.Server;
import nexus_backend.domain.User;
import org.springframework.stereotype.Service;


@AllArgsConstructor
@Service
public class UserRegistrationService {

    private final UserService userService;
    private final ServerService serverService;
    private final ChannelService channelService;
    private final NoteService noteService;


    @Transactional
    public User registerUser(User newUser) {
        User savedUser = userService.createUser(newUser);
        Server personalServer = serverService.createPersonalServerForUser(savedUser);
        Channel welcomeChannel = channelService.createDefaultChannelForServer(personalServer);
        noteService.createWelcomeNoteForUser(savedUser, welcomeChannel);
        return savedUser;
    }
}