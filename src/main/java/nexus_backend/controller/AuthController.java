package nexus_backend.controller;

import lombok.RequiredArgsConstructor;
import nexus_backend.dto.AuthResponseDTO;
import nexus_backend.dto.RegisterRequestDTO;
import nexus_backend.dto.loginRequestDTO;
import nexus_backend.service.AuthenticationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/api/auth")
@CrossOrigin(origins = "http://localhost:4200")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponseDTO> register(@RequestBody RegisterRequestDTO request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@RequestBody loginRequestDTO request) {
        return ResponseEntity.ok(authService.authenticate(request));
    }
}