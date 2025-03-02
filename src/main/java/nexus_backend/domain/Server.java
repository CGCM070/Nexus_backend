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
public class Server {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;


    @Column(length = 45)
    private String name;

    @Column(length = 45)
    private String description;

    private Timestamp createdAt;

    private Timestamp updatedAt;

    @OneToMany(mappedBy = "server", cascade = CascadeType.ALL)
    @Builder.Default
    private Set<Invitation> invitations = new HashSet<>();

    @OneToMany(mappedBy = "server")
    @Builder.Default
    private Set<Channel> channels = new HashSet<>();

    @OneToOne
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;
}
