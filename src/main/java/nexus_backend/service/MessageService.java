package nexus_backend.service;


import lombok.RequiredArgsConstructor;
import nexus_backend.domain.Channel;
import nexus_backend.domain.Message;
import nexus_backend.domain.User;
import nexus_backend.dto.MessageDTO;
import nexus_backend.repository.MessageRepository;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
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
        return messageRepository.findByChannelIdOrderByCreatedAtDesc(channelId);
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
                .createdAt(new Timestamp(System.currentTimeMillis()))
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


    /**
     * Convierte una entidad Message a MessageDTO
     */
    public MessageDTO convertToDTO(Message message) {
        return new MessageDTO(
                message.getId(),
                message.getContent(),
                message.getChannel().getId(),
                message.getUser().getId(),
                message.getUser().getUsername(),
                message.getCreatedAt().toLocalDateTime()
        );
    }
}

