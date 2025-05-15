package nexus_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nexus_backend.enums.TaskStatus;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskDTO {
    private Long id;
    private String title;
    private String description;
    private TaskStatus status;
    private Long channelId;
    private Long creatorId;
    private String creatorUsername;
    private String creatorAvatarUrl;
    private Long assignedToId;
    private String assignedToUsername;
    private String assignedToAvatarUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean canEdit;
}