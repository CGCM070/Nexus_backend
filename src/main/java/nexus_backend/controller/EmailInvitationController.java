package nexus_backend.controller;

import lombok.AllArgsConstructor;
import nexus_backend.domain.EmailInvitation;
import nexus_backend.service.EmailInvitationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/v1/api/email-invitations")
@AllArgsConstructor
public class EmailInvitationController {

    private final EmailInvitationService emailInvitationService;

    @PostMapping("/send")
    public ResponseEntity<Map<String, String>> sendInvitationEmail(
            @RequestParam Long inviterId,
            @RequestParam Long channelId,
            @RequestParam String email) {
        emailInvitationService.sendInvitationEmail(inviterId, channelId, email);
        return ResponseEntity.ok(Map.of("message", "Invitación enviada"));
    }

    @PostMapping("/accept")
    public ResponseEntity<Map<String, String>> acceptInvitation(
            @RequestParam String token,
            @RequestParam Long userId) {
        emailInvitationService.acceptInvitation(token, userId);
        return ResponseEntity.ok(Map.of("message", "Invitación aceptada"));
    }

    @GetMapping("/pending")
    public ResponseEntity<List<EmailInvitation>> getPendingInvitations(
            @RequestParam String email) {

        return ResponseEntity.ok(emailInvitationService.getPendingInvitations(email));
    }


}
