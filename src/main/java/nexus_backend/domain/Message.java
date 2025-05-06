package nexus_backend.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.sql.Timestamp;
import java.time.LocalDateTime;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)

@Entity
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @NotBlank
    private String content;

    private LocalDateTime createdAt;
    private LocalDateTime lastEditedAt;

    private boolean isEdited;
    private boolean isDeleted;
    @ManyToOne
    @JsonIgnore
    @ToString.Exclude
    private User user;

    @ManyToOne
    @JsonIgnore
    @ToString.Exclude
    private Channel channel;



}
