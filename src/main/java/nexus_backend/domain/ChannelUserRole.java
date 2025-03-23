package nexus_backend.domain;


import jakarta.persistence.*;
import lombok.*;
import nexus_backend.enums.EChannelRole;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "channel_id"})
})
public class ChannelUserRole {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "channel_id")
    private Channel channel;

    @Enumerated(EnumType.STRING)
    private EChannelRole role;
}