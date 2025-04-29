package nexus_backend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Value("${app.frontend-url}")
    private String frontendUrl;

    public EmailService(JavaMailSender mailSender, TemplateEngine templateEngine) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
    }

    public void sendInvitationEmail(String toEmail, String inviterUsername,
                                    String channelName, String invitationToken) {
        try {
            Context context = new Context();
            context.setVariable("inviterUsername", inviterUsername);
            context.setVariable("channelName", channelName);
            context.setVariable("invitationToken", invitationToken);
            context.setVariable("invitationUrl",
                    frontendUrl + "/invitation/accept?token=" + invitationToken);

            String process = templateEngine.process("email/invitation", context);

            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setTo(toEmail);
            helper.setSubject("Invitación a canal de Nexus");
            helper.setText(process, true);

            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            throw new RuntimeException("Error al enviar el email", e);
        }
    }
}