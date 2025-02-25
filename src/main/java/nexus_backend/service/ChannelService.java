package nexus_backend.service;

import nexus_backend.domain.Channel;
import nexus_backend.repository.ChannelRepository;
import org.springframework.stereotype.Service;

@Service
public class ChannelService {

    private final ChannelRepository channelRepository;

    public ChannelService(ChannelRepository channelRepository) {
        this.channelRepository = channelRepository;
    }

    public void createChannel(Channel channel) {
        channelRepository.save(channel);
    }
}
