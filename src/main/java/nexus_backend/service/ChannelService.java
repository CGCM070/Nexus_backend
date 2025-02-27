package nexus_backend.service;

import jakarta.transaction.Transactional;
import nexus_backend.domain.Channel;
import nexus_backend.domain.User;
import nexus_backend.exception.EntityNotFoundException;
import nexus_backend.repository.ChannelRepository;
import nexus_backend.repository.ServerRepository;
import nexus_backend.repository.UserRepository;
import org.springframework.stereotype.Service;



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

    @Transactional
    public void createChannel(Channel channel) {
        channelRepository.save(channel);
    }


    @Transactional
    public void inviteUserToChannel(Long channelId, Long userId) {
        Channel channel = channelRepository.findById(channelId).orElseThrow(() -> new EntityNotFoundException(channelId, " Channel not found"));
        User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException(userId, "User not found"));
        channel.getInvitedUsers().add(user);
    //    channelRepository.save(channel); channel es dueño de la relación por lo que no es necesario guardar
    }

    @Transactional
    public void removeUserFromChannel(Long channelId, Long userId) {
        Channel channel = channelRepository.findById(channelId).
                orElseThrow(() -> new EntityNotFoundException(channelId, "Channel not found"));
        User user = userRepository.findById(userId).
                orElseThrow(() -> new EntityNotFoundException(userId, "User not found"));
        channel.getInvitedUsers().remove(user);
     //   channelRepository.save(channel); channel es dueño de la relación por lo que no es necesario guardar
    }

    @Transactional
    public void updateChannel(Channel channel) {
        channelRepository.save(channel);
    }

    @Transactional
    public void deleteChannel(Long channelId) {
        Channel channel = channelRepository.findById(channelId).
                orElseThrow(() -> new EntityNotFoundException(channelId, "Channel not found"));
        channelRepository.delete(channel);
    }
}