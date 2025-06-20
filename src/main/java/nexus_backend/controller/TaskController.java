package nexus_backend.controller;

import lombok.extern.slf4j.Slf4j;
import nexus_backend.domain.Task;
import nexus_backend.dto.TaskDTO;
import nexus_backend.enums.TaskStatus;
import nexus_backend.service.TaskService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


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
    @GetMapping("/channel/{channelId}/search")
    public Page<TaskDTO> searchTasksByChannel(
            @PathVariable Long channelId,
            @RequestParam(required = false) String searchText,
            @RequestParam(required = false) TaskStatus status,
            Pageable pageable) {
        return taskService.searchTasksByChannel(channelId, searchText, status, pageable);
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

    @PreAuthorize("@securityService.canModifyResource(#taskId, 'task')")
    @PutMapping("/{taskId}/assign/{userId}")
    public TaskDTO assignTask(@PathVariable Long taskId, @PathVariable Long userId) {
        try {
            return taskService.assignTask(taskId, userId);
        } catch (Exception e) {
            log.error("Error al asignar la tarea: {}", e.getMessage());
            throw e;
        }
    }

    @PreAuthorize("@securityService.canModifyResource(#taskId, 'task')")
    @PutMapping("/{taskId}/unassign")
    public TaskDTO unassignTask(@PathVariable Long taskId) {
        return taskService.unassignTask(taskId);
    }
}