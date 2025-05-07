package nexus_backend.service;

import jakarta.transaction.Transactional;
import nexus_backend.domain.Channel;
import nexus_backend.domain.ChannelUserRole;
import nexus_backend.domain.Server;
import nexus_backend.domain.User;
import nexus_backend.dto.ChannelCreateDTO;
import nexus_backend.dto.UserDTO;
import nexus_backend.enums.EChannelRole;
import nexus_backend.exception.EntityNotFoundException;
import nexus_backend.repository.*;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
import java.util.Set;


@Service
public class ChannelService {

    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;
    private final MessageService messageService;
    private final ChannelUserRoleRepository channelUserRoleRepository;
    private final MessageRepository messageRepository;
    private final NoteRepository noteRepository;
    private final EmailInvitationRepository emailInvitationRepository;
    private SimpMessagingTemplate messagingTemplate;


    public ChannelService(ChannelRepository channelRepository, UserRepository userRepository,
                          MessageService messageService,
                          ChannelUserRoleRepository channelUserRoleRepository,
                          MessageRepository messageRepository, NoteRepository noteRepository,
                          EmailInvitationRepository emailInvitationRepository,
                          SimpMessagingTemplate messagingTemplate) {

        this.channelRepository = channelRepository;
        this.userRepository = userRepository;
        this.messageService = messageService;
        this.channelUserRoleRepository = channelUserRoleRepository;
        this.messageRepository = messageRepository;
        this.noteRepository = noteRepository;
        this.emailInvitationRepository = emailInvitationRepository;
        this.messagingTemplate = messagingTemplate;

    }

    public List<Channel> getAllChannels() {
        return channelRepository.findAll();
    }

    public Channel getChannelById(Long id) {
        return channelRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(id, "Channel"));
    }

    @Transactional
    public Channel createChannel(Channel channel) {
        Channel savedChannel = channelRepository.save(channel);
        // Crear cola para el canal
        messageService.createChannelQueue(savedChannel.getId());
        return savedChannel;
    }


    // Añadir metodo para invitar con rol específico
    @Transactional
    public void inviteUserToChannel(Long channelId, Long userId, EChannelRole role) {
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new EntityNotFoundException(channelId, "Channel"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(userId, "User"));

        // Añadir usuario a invitados
        channel.getInvitedUsers().add(user);
        channelRepository.save(channel);

        // Crear relación con rol
        ChannelUserRole channelUserRole = ChannelUserRole.builder()
                .channel(channel)
                .user(user)
                .role(role)
                .build();

        channelUserRoleRepository.save(channelUserRole);
    }

    //prodriamos prescindir de esta solo se usa en el test
    @Transactional
    public void inviteUserToChannel(Long channelId, Long userId) {
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new EntityNotFoundException(channelId, "Channel"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(userId, "User"));
        channel.getInvitedUsers().add(user);
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
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new EntityNotFoundException(channelId, "Channel not found"));

        // 1. Eliminar mensajes
        messageRepository.deleteByChannelId(channelId);

        // 2. Eliminar notas
        noteRepository.deleteByChannelId(channelId);

        // 3. Eliminar roles de usuarios
        channelUserRoleRepository.deleteByChannelId(channelId);

        // 4. Eliminar invitaciones pendientes por email
        emailInvitationRepository.deleteByChannelId(channelId);

        // 5. Eliminar la cola de mensajes
        messageService.deleteChannelQueue(channelId);

        // 6. Eliminar el canal
        channelRepository.delete(channel);
        messagingTemplate.convertAndSend("/topic/channel-deleted/" + channelId, "deleted");
    }

    //Devuelve los usuarios invitados a un canal en formato DTO
    //Se usa en el controlador para devolver los usuarios invitados a un canal
    //evitando la serialización de los objetos User
    @Transactional
    public Set<UserDTO> getChannelInvitedUsers(Long channelId) {
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new EntityNotFoundException(channelId, "Channel"));
        return channel.getInvitedUsersDTO();
    }


    @Transactional
    public Channel createDefaultChannelForServer(Server server) {

        Channel welcomeChannel = Channel.builder()
                .name("Bienvenido")
                .description("Canal de bienvenida a tu espacio personal")
                .server(server)
                .createdAt(new Timestamp(System.currentTimeMillis()))
                .build();
        return createChannel(welcomeChannel);
    }

    @Transactional
    public Channel createChannelFromDTO(ChannelCreateDTO channelDTO) {
        Channel channel = Channel.builder()
                .name(channelDTO.getName())
                .description(channelDTO.getDescription())
                .server(Server.builder().id(channelDTO.getServerId()).build())
                .createdAt(new Timestamp(System.currentTimeMillis()))
                .updatedAt(new Timestamp(System.currentTimeMillis()))
                .build();

        return createChannel(channel);
    }


    @Transactional
    public List<Channel> getChannelsByServerId(Long serverId) {
        return channelRepository.findByServerId(serverId);
    }
}