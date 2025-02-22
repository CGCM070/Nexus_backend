package domain;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;

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

}
