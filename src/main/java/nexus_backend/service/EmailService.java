package nexus_backend.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendInvitationEmail(String toEmail, String inviterUsername,String channelName, String invitationToken) {

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Invitacion a canal de Nexus");
        message.setText("Hola,\n\n" +
                        inviterUsername + " te ha invitado a unirte al canal \"" + channelName + "\" en Nexus.\n\n" +
                        "Para aceptar la invitación, haz clic en el siguiente enlace:\n" +
                        "http://localhost:4200/invitation/accept?token=" + invitationToken + "\n\n" +
                        "¡Gracias por usar Nexus!");

        mailSender.send(message);
    }
}
