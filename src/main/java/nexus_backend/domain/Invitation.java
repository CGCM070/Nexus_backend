package nexus_backend.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import nexus_backend.enums.Status;
import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)

@Entity
public class Invitation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    private Status status;

    private Timestamp createdAt;

    private Timestamp expireAt;

//    @ManyToOne
//    @JsonIgnore
//    private User inviter;

    @ManyToOne
    @JoinColumn(name = "invited_user_id")
    @JsonIgnore
    private User invitedUser;

    @ManyToOne
    @JsonIgnore
    private Server server;

}
