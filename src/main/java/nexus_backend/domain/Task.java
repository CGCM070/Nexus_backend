package nexus_backend.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import nexus_backend.enums.TaskStatus;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)

@Entity
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @NotBlank
    @Column(length = 45)
    private String title;

    @NotBlank
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TaskStatus status = TaskStatus.PENDING;

    private Timestamp createdAt;
    private Timestamp updatedAt;

    @ManyToOne
    @JsonIgnore
    @ToString.Exclude
    private Channel channel;

    @ManyToOne
    @JsonIgnore
    @ToString.Exclude
    @JoinColumn(name = "creator_id")
    private User creator;

    @ManyToOne
    @JoinColumn(name = "assigned_to_id")
    @JsonIgnore
    @ToString.Exclude
    private User assignedTo;

}
