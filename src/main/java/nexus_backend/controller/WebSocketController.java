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
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;



@Controller
@RequiredArgsConstructor
public class WebSocketController {

    private final MessageService messageService;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/channel/{channelId}/send")
    public void sendChannelMessage(@DestinationVariable Long channelId, MessageDTO messageDTO) {
        User user = userRepository.findById(messageDTO.getUserId())
                .orElseThrow(() -> new EntityNotFoundException(messageDTO.getUserId(), "User"));

        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new EntityNotFoundException(channelId, "Channel"));

        // Guardar mensaje en BD
        Message savedMessage = messageService.saveMessage(user, channel, messageDTO.getContent());

        // Enviar usando el nuevo metodo unificado
        messageService.sendMessageToChannel(savedMessage);
    }
}
