package nexus_backend.controller;

import lombok.extern.slf4j.Slf4j;
import nexus_backend.domain.Task;
import nexus_backend.dto.TaskDTO;
import nexus_backend.service.TaskService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/v1/api/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PreAuthorize("@securityService.canAccessChannel(#channelId)")
    @GetMapping("/channel/{channelId}")
    public List<TaskDTO> getTasksByChannel(@PathVariable Long channelId) {
        return taskService.getTasksByChannel(channelId);
    }

    @PreAuthorize("@securityService.canAccessChannel(#channelId)")
    @PostMapping("/channel/{channelId}/user/{userId}")
    public TaskDTO createTask(@PathVariable Long channelId, 
                            @PathVariable Long userId,
                            @RequestBody Task task) {
        return taskService.createTask(userId, channelId, task);
    }

    @PreAuthorize("@securityService.canModifyResource(#taskId, 'task')")
    @PutMapping("/{taskId}")
    public TaskDTO updateTask(@PathVariable Long taskId, @RequestBody Task task) {
        return taskService.updateTask(taskId, task);
    }

    @PreAuthorize("@securityService.canModifyResource(#taskId, 'task')")
    @DeleteMapping("/{taskId}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long taskId) {
        taskService.deleteTask(taskId);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("@securityService.canAccessChannel(#channelId)")
    @PutMapping("/{taskId}/assign/{userId}")
    public TaskDTO assignTask(@PathVariable Long taskId, 
                            @PathVariable Long userId) {
        return taskService.assignTask(taskId, userId);
    }
}