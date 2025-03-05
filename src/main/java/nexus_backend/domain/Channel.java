package nexus_backend.domain;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;
import nexus_backend.dto.UserDTO;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

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
    @ToString.Exclude
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
    @JsonIgnore
    @ToString.Exclude
    private Set<User> invitedUsers = new HashSet<>();


    /**
     *  Este metodo retorna un Set de UserDTOs
     *  para evitar una serialización infinit
     *  de los objetos User dentro de  Channel
     */
    @JsonProperty("invitedUsers")
    public Set<UserDTO> getInvitedUsersDTO() {
        return invitedUsers.stream()
                .map(user -> new UserDTO(
                        user.getId(),
                        user.getUsername(),
                        user.getEmail(),
                        user.getAvatarUrl(),
                        user.getFullName()
                ))
                .collect(Collectors.toSet());
    }

}
