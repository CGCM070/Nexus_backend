package nexus_backend.controller;

import lombok.extern.slf4j.Slf4j;
import nexus_backend.domain.Channel;
import nexus_backend.dto.UserDTO;
import nexus_backend.service.ChannelService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@Slf4j
@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/v1/api/channels")

public class ChannelController {

    private final ChannelService channelService;

    public ChannelController(ChannelService channelService) {
        this.channelService = channelService;
    }

    @GetMapping("")
    public List<Channel> getAllChannels() {
        log.info("Fetching all channels");
        return channelService.getAllChannels();
    }

    @GetMapping("/{id}")
    public Channel getChannelById( @PathVariable Long id) {
        log.info("Fetching channel with ID: {}", id);
        return channelService.getChannelById(id);
    }

    @PostMapping("")
    public Channel createChannel(@RequestBody Channel channel) {
        log.info("Creating new channel");
        return channelService.createChannel(channel);
    }

    @PutMapping("/{id}")
    public Channel updateChannel(@PathVariable Long id, @RequestBody Channel channel) {
        log.info("Updating channel with ID: {}", id);
        channel.setId(id);
        return channelService.updateChannel(channel.getId(), channel);
    }

    @DeleteMapping("/{id}")
    public void deleteChannel(@PathVariable Long id) {
        log.info("Deleting channel with ID: {}", id);
        channelService.deleteChannel(id);
    }

    @PostMapping("/{channelId}/user/{userId}")
    public void inviteUserToChannel(@PathVariable Long channelId, @PathVariable Long userId) {
        channelService.inviteUserToChannel(channelId, userId);
    }

    @DeleteMapping("/{channelId}/remove/{userId}")
    public void removeUserFromChannel(@PathVariable Long channelId, @PathVariable Long userId) {
        log.info("Removing user with ID: {} from channel with ID: {}", userId, channelId);
        channelService.removeUserFromChannel(channelId, userId);
    }


    @GetMapping("/{channelId}/invited-users")
    public Set<UserDTO> getChannelInvitedUsers(@PathVariable Long channelId) {
        return channelService.getChannelInvitedUsers(channelId);
    }
}
