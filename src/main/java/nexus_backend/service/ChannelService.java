package nexus_backend.service;

import jakarta.transaction.Transactional;
import nexus_backend.domain.Channel;
import nexus_backend.domain.User;
import nexus_backend.exception.EntityNotFoundException;
import nexus_backend.repository.ChannelRepository;
import nexus_backend.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class ChannelService {

    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;


    public ChannelService(ChannelRepository channelRepository, UserRepository userRepository) {
        this.channelRepository = channelRepository;
        this.userRepository = userRepository;
    }

    public List<Channel> getAllChannels() {
        return channelRepository.findAll();
    }

    public Channel getChannelById(Long id) {
        return channelRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(id , "Channel"));
    }

    @Transactional
    public Channel createChannel(Channel channel) {
        return   channelRepository.save(channel);
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
    public Channel updateChannel(Long id, Channel updatedChannel) {
        Channel existingChannel = channelRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(id, "Channel"));

        //nombre y descripción
        if (updatedChannel.getName() != null) {
            existingChannel.setName(updatedChannel.getName());
        }
        if (updatedChannel.getDescription() != null) {
            existingChannel.setDescription(updatedChannel.getDescription());
        }
        return channelRepository.save(existingChannel);

    }
    @Transactional
    public void deleteChannel(Long channelId) {
        Channel channel = channelRepository.findById(channelId).
                orElseThrow(() -> new EntityNotFoundException(channelId, "Channel not found"));
        channelRepository.delete(channel);
    }
}