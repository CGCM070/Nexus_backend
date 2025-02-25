package nexus_backend.domain;


import com.fasterxml.jackson.annotation.JsonIgnore;
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
public class Channel {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;


    private String name;

    @Column(length = 100)
    private String description;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    @ManyToOne
    @JsonIgnore
    private Server server;

    @OneToMany(mappedBy = "channel")
    @Builder.Default
    private Set<Message> messages = new HashSet<>();

    @OneToMany(mappedBy = "channel")
    @Builder.Default
    private Set<Note> notes = new HashSet<>();

    @OneToMany(mappedBy = "channel")
    @Builder.Default
    private Set<Task> tasks = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "channel_users",
            joinColumns = @JoinColumn(name = "channel_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> invitedUsers = new HashSet<>();

}
