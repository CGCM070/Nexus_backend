package nexus_backend.dto;

import lombok.AllArgsConstructor;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;



@Data
@AllArgsConstructor
@NoArgsConstructor
public class MessageDTO {

    private Long id;
    private String content;
    private Long channelId;
    private Long userId;
    private String username;
    private String avatarUrl;
    private Timestamp createdAt;
    private Timestamp lastEditedAt;
    private boolean isEdited;
    private boolean isDeleted;

}
