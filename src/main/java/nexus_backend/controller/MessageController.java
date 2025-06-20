package nexus_backend.controller;

import lombok.RequiredArgsConstructor;
import nexus_backend.domain.Channel;
import nexus_backend.domain.Message;
import nexus_backend.domain.User;
import nexus_backend.dto.MessageDTO;
import nexus_backend.exception.EntityNotFoundException;
import nexus_backend.repository.ChannelRepository;
import nexus_backend.repository.UserRepository;
import nexus_backend.service.MessageService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/v1/api/messages")
@RequiredArgsConstructor
public class MessageController {


    private final MessageService messageService;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;

    @PreAuthorize("@securityService.canAccessChannel(#channelId)")
    @GetMapping("/channel/{channelId}")
    public List<MessageDTO> getChannelMessages(@PathVariable Long channelId) {
        return messageService.getMessagesByChannelId(channelId).stream()
                .map(messageService::convertToDTO)
                .collect(Collectors.toList());
    }

    @PreAuthorize("@securityService.canAccessChannel(#channelId)")
    @PostMapping("/channel/{channelId}")
    public MessageDTO sendMessage(
            @PathVariable Long channelId,
            @RequestParam Long userId,
            @RequestBody String content) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(userId, "User"));

        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new EntityNotFoundException(channelId, "Channel"));

        // Guardar mensaje en BD
        Message savedMessage = messageService.saveMessage(user, channel, content);

        // Enviar usando el nuevo método unificado
        messageService.sendMessageToChannel(savedMessage);

        // Convertir a DTO para la respuesta
        return messageService.convertToDTO(savedMessage);
    }

    @PreAuthorize("@securityService.canModifyMessage(#messageId)")
    @PutMapping("/{messageId}")
    public MessageDTO editMessage(
            @PathVariable Long messageId,
            @RequestBody String newContent) {
        Message editedMessage = messageService.editMessage(messageId, newContent);
        return messageService.convertToDTO(editedMessage);
    }

    @PreAuthorize("@securityService.canModifyMessage(#messageId)")
    @DeleteMapping("/{messageId}")
    public MessageDTO deleteMessage(@PathVariable Long messageId) {
        Message deletedMessage = messageService.softDeleteMessage(messageId);
        return messageService.convertToDTO(deletedMessage);
    }
}
