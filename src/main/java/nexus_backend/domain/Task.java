package nexus_backend.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

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

    @Column(length = 45)
    private String title;

    private String description;

    private String status;

    private Timestamp createdAt;

    private Timestamp updatedAt;


    @ManyToOne
    @JsonIgnore
    private Channel channel;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "creator_id")
    private User creator;

    @ManyToOne
    @JoinColumn(name = "assigned_to_id")
    @JsonIgnore
    private User assignedTo;

}
