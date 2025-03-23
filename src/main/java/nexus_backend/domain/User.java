package nexus_backend.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import nexus_backend.enums.ERol;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashSet;

import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)

@Entity

public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;


    @Column(length = 45)
    private String username;

    @Column(nullable = false, unique = true)
    private String email;

    @NotBlank
    private String passwordHash;

    @Column(length = 100)
    private String fullName;

    private String avatarUrl;

    private Timestamp createdAt;
    private Timestamp updatedAt;

    // Corrigiendo la relación bidireccional, usando cascade pero sin orphanRemoval
    @OneToOne(mappedBy = "user", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private Server personalServer;



    // Relación faltante con los canales donde el usuario está invitado
    @ManyToMany(mappedBy = "invitedUsers")
    @Builder.Default
    @JsonIgnore
    @ToString.Exclude
    private Set<Channel> invitedChannels = new HashSet<>();

    // Mantener orphanRemoval solo para las entidades que son "propiedad" exclusiva del usuario
    @OneToMany(mappedBy = "user",cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @JsonIgnore
    @ToString.Exclude
    private List<Note> notes = new ArrayList<>();

    @OneToMany(mappedBy = "creator",cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @JsonIgnore
    @ToString.Exclude
    private Set<Task> createdTasks = new HashSet<>();

    // Las tareas asignadas no deberían eliminarse al eliminar un usuario
    @OneToMany(mappedBy = "assignedTo", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @Builder.Default
    @JsonIgnore
    @ToString.Exclude
    private Set<Task> assignedTasks = new HashSet<>();

    @OneToMany(mappedBy = "user",cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @JsonIgnore
    @ToString.Exclude
    private Set<Message> messages = new HashSet<>();


    @ElementCollection(targetClass = ERol.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Set<ERol> roles = new HashSet<>();
}
