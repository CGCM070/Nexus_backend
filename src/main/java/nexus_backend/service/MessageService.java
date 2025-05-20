package nexus_backend.service;


import lombok.RequiredArgsConstructor;
import nexus_backend.domain.Channel;
import nexus_backend.domain.Message;
import nexus_backend.domain.User;
import nexus_backend.dto.MessageDTO;
import nexus_backend.exception.EntityNotFoundException;
import nexus_backend.repository.MessageRepository;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;
    private final AmqpAdmin amqpAdmin;
    private  final RabbitTemplate rabbitTemplate;
    private final SimpMessagingTemplate webSocketTemplate;


    @Value("${nexus.rabbitmq.exchange}")
    private String exchangeName;

    /**
     * Crea una cola para un canal específico
     */
    public Queue createChannelQueue(Long channelId) {
        String queueName = "nexus.channel." + channelId;
        Queue queue = new Queue(queueName, true);
        amqpAdmin.declareQueue(queue);

        Binding binding = BindingBuilder.bind(queue)
                .to(new TopicExchange(exchangeName))
                .with("nexus.channel." + channelId + ".#");
        amqpAdmin.declareBinding(binding);

        return queue;
    }

    /**
     * Elimina la cola de un canal
     */
    public void deleteChannelQueue(Long channelId) {
        String queueName = "nexus.channel." + channelId;
        amqpAdmin.deleteQueue(queueName);
    }

    /**
     * Obtiene mensajes por canal
     */
    public List<Message> getMessagesByChannelId(Long channelId) {
        return messageRepository.findByChannelIdOrderByCreatedAtAsc(channelId);
    }

    /**
     * Guarda un mensaje en la base de datos
     */
    @Transactional
    public Message saveMessage(User user, Channel channel, String content) {

        // Verificar que el usuario está invitado al canal o es el propietario del servidor
        boolean isServerOwner = channel.getServer().getUser() != null &&
                                channel.getServer().getUser().getId().equals(user.getId());
        boolean isInvited = channel.getInvitedUsers().contains(user);

        if (!isServerOwner && !isInvited) {
            throw new RuntimeException("El usuario no tiene permiso para enviar mensajes a este canal");
        }

        Message message = Message.builder()
                .content(content)
                .createdAt(Timestamp.valueOf(LocalDateTime.now()))
                .user(user)
                .channel(channel)
                .build();

        return messageRepository.save(message);
    }

    /**
     * Envía un mensaje a un canal específico
     */
    @Transactional
    public void sendMessageToChannel(Message message) {
        // Convertir entidad a DTO internamente
        MessageDTO messageDTO = convertToDTO(message);

        // Enrutar mensaje a través de RabbitMQ
        String routingKey = "nexus.channel." + message.getChannel().getId() + ".message";
        rabbitTemplate.convertAndSend(exchangeName, routingKey, messageDTO);

        // Enviar inmediatamente por WebSocket
        webSocketTemplate.convertAndSend("/topic/channel/" + message.getChannel().getId(), messageDTO);
    }


    @Transactional
    public Message editMessage(Long messageId, String newContent) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new EntityNotFoundException(messageId, "Message"));

        message.setContent(newContent);
        message.setEdited(true);
        message.setLastEditedAt(Timestamp.valueOf(LocalDateTime.now()));

        Message savedMessage = messageRepository.save(message);

        // Enviar actualización por WebSocket y RabbitMQ
        MessageDTO messageDTO = convertToDTO(savedMessage);
        String routingKey = "nexus.channel." + savedMessage.getChannel().getId() + ".update";
        rabbitTemplate.convertAndSend(exchangeName, routingKey, messageDTO);
        webSocketTemplate.convertAndSend("/topic/channel/" + savedMessage.getChannel().getId(), messageDTO);

        return savedMessage;
    }

    @Transactional
    public Message softDeleteMessage(Long messageId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new EntityNotFoundException(messageId, "Message"));

        if (message.isDeleted()) {
            throw new IllegalStateException("El mensaje ya ha sido eliminado");
        }

        // Solo marcar como eliminado sin modificar el contenido
        message.setDeleted(true);
        message.setEdited(false);
        message.setLastEditedAt(null);

        Message savedMessage = messageRepository.save(message);

        // Al enviar la actualización, ahí sí enviamos el texto de eliminado
        MessageDTO messageDTO = convertToDTO(savedMessage);
        messageDTO.setContent("Este mensaje fue eliminado");

        // Enviar actualización con el texto modificado solo para la visualización
        String routingKey = "nexus.channel." + savedMessage.getChannel().getId() + ".update";
        rabbitTemplate.convertAndSend(exchangeName, routingKey, messageDTO);
        webSocketTemplate.convertAndSend("/topic/channel/" + savedMessage.getChannel().getId(), messageDTO);

        return savedMessage;
    }

    /**
     * Convierte una entidad Message a MessageDTO
     */
    public MessageDTO convertToDTO(Message message) {
        MessageDTO dto = new MessageDTO();
        dto.setId(message.getId());
        dto.setContent(message.isDeleted() ? "Este mensaje fue eliminado" : message.getContent());
        dto.setChannelId(message.getChannel().getId());
        dto.setUserId(message.getUser().getId());
        dto.setUsername(message.getUser().getUsername());
        dto.setAvatarUrl(message.getUser().getAvatarUrl());
        dto.setCreatedAt(message.getCreatedAt());
        dto.setLastEditedAt(message.getLastEditedAt());
        dto.setEdited(message.isEdited());
        dto.setDeleted(message.isDeleted());
        return dto;
    }
}


