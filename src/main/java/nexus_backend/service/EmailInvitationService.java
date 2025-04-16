package nexus_backend.service;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import nexus_backend.domain.Channel;
import nexus_backend.domain.EmailInvitation;
import nexus_backend.domain.User;
import nexus_backend.enums.EChannelRole;
import nexus_backend.exception.EntityNotFoundException;
import nexus_backend.repository.ChannelRepository;
import nexus_backend.repository.EmailInvitationRepository;
import nexus_backend.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@Service
public class EmailInvitationService {

    private final EmailInvitationRepository emailInvitationRepository;
    private final EmailService emailService;
    private final UserRepository userRepository;
    private final ChannelService channelService;
    private final ChannelRepository channelRepository;


    @Transactional
    public void sendInvitationEmail(Long inviterId, Long channelId, String toEmail,EChannelRole role) {
        User inviter = userRepository.findById(inviterId)
                .orElseThrow(() -> new EntityNotFoundException(inviterId, "User"));

        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new EntityNotFoundException(channelId, "Channel"));



        //Veifivaciones
        if (channel.getServer().getUser().getId().equals(inviterId) &&
            channel.getInvitedUsers().contains(inviter)){
            throw new EntityNotFoundException(inviterId, "El usuario no tiene permisos para invitar a este canal");
        }

        if (emailInvitationRepository.existsByEmailAndChannelIdAndAcceptedIsFalse(toEmail, channelId)) {
            throw new RuntimeException("Ya existe una invitación pendiente para este usuario");
        }

        //Generar token
        String token = UUID.randomUUID().toString();

        // Crear invitación con rol
        EmailInvitation invitation = EmailInvitation.builder()
                .email(toEmail)
                .token(token)
                .user(inviter)
                .channel(channel)
                .createdAt(Timestamp.valueOf(LocalDateTime.now()))
                .expireAt(Timestamp.valueOf(LocalDateTime.now().plus(7, ChronoUnit.DAYS)))
                .accepted(false)
                .role(role) // Guardar el rol asignado
                .build();

        emailInvitationRepository.save(invitation);

        //Enviar email
        emailService.sendInvitationEmail(toEmail, inviter.getUsername(), channel.getName(), token);
    }


    @Transactional
    public void acceptInvitation(String token, Long userId) {
        EmailInvitation invitation = emailInvitationRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("No se encontró la invitación"));

        // Verificar que la invitación no haya expirado
        if (invitation.getExpireAt().before(Timestamp.valueOf(LocalDateTime.now()))) {
            throw new RuntimeException("La invitación ha expirado");
        }

        // Verificar qie no haya sido aceptada
        if (invitation.isAccepted()) {
            throw new RuntimeException("La invitación ya ha sido aceptada");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(userId, "User"));

        // verificar que el correo electronico condice

        if (!invitation.getEmail().equals(user.getEmail())) {
            throw new RuntimeException("El correo electronico no coincide con el de la invitación");
        }

        // Verificar que el usuario no este ya en el canal

        if (invitation.getChannel().getInvitedUsers().contains(user)) {
            throw new RuntimeException("El usuario ya esta en el canal");
        }

        // Añadir usuario al canal

        // Esto debería llamar a la versión del metodo que asigna un rol
        channelService.inviteUserToChannel(
                invitation.getChannel().getId(),
                userId,
                invitation.getRole()
        );

        invitation.setAccepted(true);
    }

    public List<EmailInvitation> getPendingInvitations(String email) {
        return emailInvitationRepository.findByEmailAndAcceptedIsFalse(email);
    }

    @Transactional
    public void cleanExpiredInvitations() {
        List<EmailInvitation> expiredInvitations =
                emailInvitationRepository.findByExpireAtBeforeAndAcceptedIsFalse(Timestamp.valueOf(LocalDateTime.now()));
        emailInvitationRepository.deleteAll(expiredInvitations);
    }
}
