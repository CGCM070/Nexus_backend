package nexus_backend.domain;

import jakarta.persistence.*;
import lombok.*;
import nexus_backend.enums.ERol;

import java.sql.Timestamp;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)

@Entity
public class Rol {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Enumerated(EnumType.STRING)
    private ERol rol;

    private Timestamp createdAt;
    private Timestamp updatedAt;
}
