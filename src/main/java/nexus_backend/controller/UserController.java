package nexus_backend.controller;

import lombok.extern.slf4j.Slf4j;
import nexus_backend.domain.Note;
import nexus_backend.domain.User;
import nexus_backend.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/v1/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }


    @GetMapping("")
    public List<User> getAllUsers() {
        return this.userService.getAllUsers();
    }


    @GetMapping("/{id}/notes")
    public List<Note> getAllUserNotes(@PathVariable Long id) {
        log.info("User notes whit  id : {}", id);
        return this.userService.getAllUserNotes(id);
    }


    @PostMapping("")
    public User createUser(@RequestBody User user) {
        log.info("Create new user ");
        return this.userService.createUser(user);
    }


    @GetMapping("/{id}")
    public User getUserById(@PathVariable Long id) {
        log.info("Search user whit  id : {}", id);
        return this.userService.findById(id);
    }

    @PostMapping("/{id}/notes")
    public Note createNoteForUser(@PathVariable Long id, @RequestBody Note note) {
        log.info("Create note for user whit id : {}", id);
        return this.userService.createNoteForUser(id, note);
    }

    @GetMapping("/{userId}/notes/{noteId}")
    public Note getNoteById(@PathVariable Long userId, @PathVariable Long noteId) {
        log.info("Get note whit id: {}  user id: {}", noteId, userId);
        return this.userService.getNoteById(userId, noteId);
    }


    @DeleteMapping("/notes/{noteId}")
    public void deleteNoteForUser(@PathVariable Long noteId) {
        log.info("Delete note whit id: {}", noteId);
        this.userService.deleteNoteForUser(noteId);
    }
}
