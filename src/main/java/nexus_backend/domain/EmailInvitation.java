package nexus_backend.domain;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)

@Entity
public class EmailInvitation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    private String email;

    @Column(unique = true)
    private String token;

    private Timestamp createdAt;

    private Timestamp expireAt;

    private boolean accepted;


    @ManyToOne
    private User user;

    @ManyToOne
    private Channel channel;
}
