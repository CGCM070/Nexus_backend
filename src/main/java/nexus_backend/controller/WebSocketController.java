package nexus_backend.controller;


import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequiredArgsConstructor
public class WebSocketController {

    private final MessageService messageService;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;

    @MessageMapping("/channel/{channelId}/send")
    @Transactional
    public void sendChannelMessage(@DestinationVariable Long channelId, MessageDTO messageDTO) {


        User user = userRepository.findById(messageDTO.getUserId())
                .orElseThrow(() -> new EntityNotFoundException(messageDTO.getUserId(), "User"));

        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new EntityNotFoundException(channelId, "Channel"));

        log.info("Recibido mensaje para canal {}: {}", channelId, messageDTO);

        // Guardar mensaje en BD
        Message savedMessage = messageService.saveMessage(user, channel, messageDTO.getContent());

        // Enviar usando el nuevo metodo unificado
        messageService.sendMessageToChannel(savedMessage);
    }
}
