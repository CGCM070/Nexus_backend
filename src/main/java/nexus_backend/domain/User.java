package nexus_backend.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

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

    private String passwordHash;

    @Column(length = 100)
    private String fullName;

    private String avatarUrl;

    private Timestamp createdAt;
    private Timestamp updatedAt;


    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private Server personalServer;

    @OneToMany(mappedBy = "invitedUser")
    @Builder.Default
    @JsonIgnore
    private Set<Invitation> invitations = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @JsonIgnore
    private List<Note> notes = new ArrayList<>();

    @OneToMany(mappedBy = "creator")
    @Builder.Default
    @JsonIgnore
    private Set<Task> createdTasks = new HashSet<>();

    @OneToMany(mappedBy = "assignedTo")
    @Builder.Default
    @JsonIgnore
    private Set<Task> assignedTasks = new HashSet<>();

    @OneToMany(mappedBy = "user")
    @Builder.Default
    @JsonIgnore
    private Set<Message> messages = new HashSet<>();

}
