package nexus_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class MessageDTO {

    private Long id;
    private String content;
    private Long channelId;
    private Long userId;
    private String username;
    private LocalDateTime timestamp;


}
