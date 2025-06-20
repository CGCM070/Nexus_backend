package nexus_backend.service;

import nexus_backend.domain.User;
import nexus_backend.dto.AuthResponseDTO;
import nexus_backend.dto.RegisterRequestDTO;
import nexus_backend.dto.loginRequestDTO;
import nexus_backend.enums.ERol;
import nexus_backend.repository.UserRepository;
import nexus_backend.security.JwtService;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final UserRegistrationService userRegistrationService;

    public AuthenticationService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            UserRegistrationService userRegistrationService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.userRegistrationService = userRegistrationService;
    }


    @Transactional
    public AuthResponseDTO register(RegisterRequestDTO request) {
        // Verificar si el usuario ya existe
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email ya registrado");
        }

        // Crear usuario con roles
        Set<ERol> roles = new HashSet<>();
        roles.add(ERol.ROL_ADMIN);

        User newUser = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .fullName(request.getFullName())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .roles(roles)
                .createdAt(Timestamp.from(Instant.now()))
                .build();

        // Usar el servicio existente para el flujo de registro completo
        User savedUser = userRegistrationService.registerUser(newUser);

        // Generar token JWT
        String jwtToken = jwtService.generateToken(
                org.springframework.security.core.userdetails.User.builder()
                        .username(savedUser.getEmail())
                        .password(savedUser.getPasswordHash())
                        .authorities(savedUser.getRoles().stream()
                                .map(role -> new SimpleGrantedAuthority(role.name()))
                                .collect(Collectors.toList())).build()
        );

        return new AuthResponseDTO(
                savedUser.getId(),
                savedUser.getUsername(),
                savedUser.getEmail(),
                savedUser.getAvatarUrl(),
                jwtToken
        );
    }

    public AuthResponseDTO authenticate(loginRequestDTO request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Email incorrecto o no registrado"));

        // Verificar contraseña
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new RuntimeException("Credenciales inválidas");
        }

        // Generar token
        String jwtToken = jwtService.generateToken(
                org.springframework.security.core.userdetails.User.builder()
                        .username(user.getEmail())
                        .password(user.getPasswordHash())
                        .authorities(user.getRoles().stream()
                                .map(role -> new SimpleGrantedAuthority(role.name()))
                                .collect(Collectors.toList()))
                        .build()
        );

        return new AuthResponseDTO(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getAvatarUrl(),
                jwtToken
        );
    }
}