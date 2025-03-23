package nexus_backend.domain;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)

@Entity
public class Note {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(length = 45)
    @NotBlank
    private String title;

    private String content;

    private Timestamp createdAt;

    private Timestamp updatedAt;

    @ManyToOne
    @JsonIgnore
    @ToString.Exclude
    private Channel channel;

    @ManyToOne
    @JsonIgnore
    @ToString.Exclude
    private User user;

}
