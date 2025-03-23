package nexus_backend.controller;

import lombok.extern.slf4j.Slf4j;
import nexus_backend.domain.Channel;
import nexus_backend.dto.UserDTO;
import nexus_backend.enums.EChannelRole;
import nexus_backend.service.ChannelService;
import org.springframework.security.access.prepost.PreAuthorize;
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

    // Ver canal: cualquier miembro invitado
    @PreAuthorize("@securityService.canAccessChannel(#id)")
    @GetMapping("/{id}")
    public Channel getChannelById(@PathVariable Long id) {
        log.info("Fetching channel with ID: {}", id);
        return channelService.getChannelById(id);
    }

    // Crear canal: solo owner del servidor
    @PreAuthorize("@securityService.canManageChannel(#channel.server.id)")
    @PostMapping("")
    public Channel createChannel(@RequestBody Channel channel) {
        log.info("Creating new channel");
        return channelService.createChannel(channel);
    }
/*
    // Invitar usuario: solo admin/owner del canal
    @PreAuthorize("@securityService.canManageChannel(#channelId)")
    @PostMapping("/{channelId}/user/{userId}")
    public void inviteUserToChannel(@PathVariable Long channelId, @PathVariable Long userId) {
        channelService.inviteUserToChannel(channelId, userId, EChannelRole.MEMBER);
    }
*/
    // Eliminar usuario: solo admin/owner del canal
    @PreAuthorize("@securityService.canManageChannel(#channelId)")
    @DeleteMapping("/{channelId}/remove/{userId}")
    public void removeUserFromChannel(@PathVariable Long channelId, @PathVariable Long userId) {
        log.info("Removing user with ID: {} from channel with ID: {}", userId, channelId);
        channelService.removeUserFromChannel(channelId, userId);
    }

    // Ver usuarios invitados: cualquier miembro
    @PreAuthorize("@securityService.canAccessChannel(#channelId)")
    @GetMapping("/{channelId}/invited-users")
    public Set<UserDTO> getChannelInvitedUsers(@PathVariable Long channelId) {
        return channelService.getChannelInvitedUsers(channelId);
    }

    // Eliminar canal: solo owner del servidor
    @PreAuthorize("@securityService.canManageChannel(#id)")
    @PutMapping("/{id}")
    public Channel updateChannel(@PathVariable Long id, @RequestBody Channel channel) {
        log.info("Updating channel with ID: {}", id);
        channel.setId(id);
        return channelService.updateChannel(channel.getId(), channel);
    }

    @PreAuthorize("@securityService.canManageChannel(#id)")
    @DeleteMapping("/{id}")
    public void deleteChannel(@PathVariable Long id) {
        log.info("Deleting channel with ID: {}", id);
        channelService.deleteChannel(id);
    }

    @GetMapping("")
    public List<Channel> getAllChannels() {
        log.info("Fetching all channels");
        return channelService.getAllChannels();
    }

}
