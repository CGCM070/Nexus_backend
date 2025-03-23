package nexus_backend.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import nexus_backend.enums.EChannelRole;

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

    @NotBlank
    private String email;

    @Column(unique = true)
    private String token;

    private Timestamp createdAt;

    private Timestamp expireAt;

    private boolean accepted;
    @Enumerated(EnumType.STRING)
    private EChannelRole role;

    @ManyToOne
    @ToString.Exclude
    @JsonIgnore
    private User user;

    @ManyToOne
    @ToString.Exclude
    @JsonIgnore
    private Channel channel;
}
