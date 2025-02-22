package nexus_backend.domain;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;
import java.util.HashSet;

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

    @Column(length = 75)
    private String email;

    private String passwordHash;

    @Column(length = 100)
    private String fullName;

    private String avatarUrl;

    private Timestamp createdAt;
    private Timestamp updatedAt;


    @OneToMany(mappedBy = "user")
    @Builder.Default
    private Set<Message> messages = new HashSet<>();


    @OneToMany(mappedBy = "inviter")
    @Builder.Default
    private Set<Invitation> invitations = new HashSet<>();

    @OneToMany(mappedBy = "user")
    @Builder.Default
    private Set<Note> notes = new HashSet<>();

    @OneToMany(mappedBy = "creator")
    private Set<Task> createdTasks;

    @OneToMany(mappedBy = "assignedTo")
    private Set<Task> assignedTasks;

    @ManyToMany(mappedBy = "users")
    private Set<Server> servers;


}
