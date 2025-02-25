package nexus_backend.service;

import jakarta.transaction.Transactional;
import nexus_backend.domain.Channel;
import nexus_backend.domain.Server;
import nexus_backend.domain.User;
import nexus_backend.repository.ChannelRepository;
import nexus_backend.repository.ServerRepository;
import nexus_backend.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;

@Service
public class ChannelService {

    private final ChannelRepository channelRepository;
    private final ServerRepository serverRepository;
    private final UserRepository userRepository;


    public ChannelService(ChannelRepository channelRepository, ServerRepository serverRepository, UserRepository userRepository) {
        this.channelRepository = channelRepository;
        this.serverRepository = serverRepository;
        this.userRepository = userRepository;
    }

    public void createChannel(Channel channel) {
        channelRepository.save(channel);
    }



    @Transactional
    public Channel createChannel(Long serverId, String name, String description) {
        Server server = serverRepository.findById(serverId).orElseThrow(() -> new RuntimeException("Server not found"));
        Channel channel = Channel.builder()
                .name(name)
                .description(description)
                .server(server)
                .createdAt(new Timestamp(System.currentTimeMillis()))
                .build();
        return channelRepository.save(channel);
    }



    @Transactional
    public void inviteUserToChannel(Long channelId, Long userId) {
        Channel channel = channelRepository.findById(channelId).orElseThrow(() -> new RuntimeException("Channel not found"));
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        channel.getInvitedUsers().add(user);
        channelRepository.save(channel);
    }
}
