package nexus_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChannelCreateDTO {
    private String name;
    private String description;
    private Long serverId;
}