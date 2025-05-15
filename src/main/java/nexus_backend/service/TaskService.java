package nexus_backend.service;

import jakarta.transaction.Transactional;
import nexus_backend.domain.Channel;
import nexus_backend.domain.Task;
import nexus_backend.domain.User;
import nexus_backend.dto.TaskDTO;
import nexus_backend.exception.EntityNotFoundException;
import nexus_backend.repository.ChannelRepository;
import nexus_backend.repository.TaskRepository;
import nexus_backend.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;

    public TaskService(TaskRepository taskRepository, 
                      UserRepository userRepository, 
                      ChannelRepository channelRepository) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
        this.channelRepository = channelRepository;
    }

    public List<TaskDTO> getTasksByChannel(Long channelId) {
        return taskRepository.findAllByChannelId(channelId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public TaskDTO createTask(Long userId, Long channelId, Task task) {
        User creator = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(userId, "User"));
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new EntityNotFoundException(channelId, "Channel"));

        task.setCreator(creator);
        task.setChannel(channel);
        task.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        task.setUpdatedAt(new Timestamp(System.currentTimeMillis()));

        return convertToDTO(taskRepository.save(task));
    }

    @Transactional
    public TaskDTO updateTask(Long taskId, Task taskDetails) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new EntityNotFoundException(taskId, "Task"));

        task.setTitle(taskDetails.getTitle());
        task.setDescription(taskDetails.getDescription());
        task.setStatus(taskDetails.getStatus());
        task.setUpdatedAt(new Timestamp(System.currentTimeMillis()));

        return convertToDTO(taskRepository.save(task));
    }

    @Transactional
    public TaskDTO assignTask(Long taskId, Long userId) {
        // Validación de parámetros
        if (taskId == null) {
            throw new IllegalArgumentException("El ID de la tarea no puede ser nulo");
        }
        if (userId == null) {
            throw new IllegalArgumentException("El ID del usuario no puede ser nulo");
        }

        // Obtener la tarea
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new EntityNotFoundException(taskId, "Task"));

        // Obtener el usuario asignado
        User assignee = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(userId, "User"));

        // Validar que el usuario pertenece al canal
        Channel channel = task.getChannel();
        if (channel == null) {
            throw new IllegalStateException("La tarea no está asociada a ningún canal");
        }

        boolean isUserInChannel = channel.getInvitedUsers().stream()
                .anyMatch(user -> user.getId().equals(userId));

        if (!isUserInChannel) {
            throw new IllegalStateException("El usuario no pertenece a este canal");
        }

        // Asignar y guardar
        task.setAssignedTo(assignee);
        task.setUpdatedAt(new Timestamp(System.currentTimeMillis()));

        Task savedTask = taskRepository.save(task);
        return convertToDTO(savedTask);
    }

    @Transactional
    public void deleteTask(Long taskId) {
        if (!taskRepository.existsById(taskId)) {
            throw new EntityNotFoundException(taskId, "Task");
        }
        taskRepository.deleteById(taskId);
    }

    private TaskDTO convertToDTO(Task task) {
        TaskDTO dto = new TaskDTO(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getStatus(),
                task.getChannel().getId(),
                task.getCreator().getId(),
                task.getCreator().getUsername(),
                task.getCreator().getAvatarUrl(),
                task.getAssignedTo() != null ? task.getAssignedTo().getId() : null,
                task.getAssignedTo() != null ? task.getAssignedTo().getUsername() : null,
                task.getAssignedTo() != null ? task.getAssignedTo().getAvatarUrl() : null,
                task.getCreatedAt().toLocalDateTime(),
                task.getUpdatedAt().toLocalDateTime(),
                true  // Por defecto true, se puede modificar según la lógica de permisos
        );
        return dto;
    }
}