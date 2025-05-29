package nexus_backend.controller;

import lombok.extern.slf4j.Slf4j;
import nexus_backend.domain.Note;
import nexus_backend.domain.User;
import nexus_backend.dto.NoteDTO;
import nexus_backend.repository.UserRepository;
import nexus_backend.service.NoteService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/v1/api/notes")
public class NoteController {

    private final NoteService noteService;
    private final UserRepository userRepository;

    public NoteController(NoteService noteService, UserRepository userRepository) {
        this.noteService = noteService;
        this.userRepository = userRepository;
    }

    @GetMapping("")
    public Page<Note> getAllNotes(Pageable pageable) {
        log.info("Fetching all notes");
        return noteService.getAllNotes(pageable);
    }

    @GetMapping("/user/{userId}")
    public List<Note> getNotesByUserId(@PathVariable Long userId) {
        log.info("Fetching notes for user with ID: {}", userId);
        return noteService.getNotesByUserId(userId);
    }

    @PostMapping("")
    public Note createNote(@RequestBody Note note) {
        log.info("Creating new note");
        return noteService.createNote(note);
    }

    // Crear nota en canal: cualquier miembro
    @PreAuthorize("@securityService.canAccessChannel(#channelId)")
   // @PreAuthorize("@securityService.canManageChannel(#channelId)")
    @PostMapping("/user/{userId}/channel/{channelId}")
    public Note createNoteForUser(@PathVariable Long userId, @PathVariable Long channelId, @RequestBody Note note) {
        log.info("Creating note for user with ID: {} in channel with ID: {}", userId, channelId);
        return noteService.createNoteForUser(userId, channelId, note);
    }

    // Listar notas por canal: cualquier miembro
    @PreAuthorize("@securityService.canAccessChannel(#channelId)")
    @GetMapping("/channel/{channelId}")
    public Page<NoteDTO> getNotesByChannel(@PathVariable Long channelId, Pageable pageable) {
        Page<Note> notePage = noteService.getNotesByChannel(channelId, pageable);
        return notePage.map(noteService::convertToDTO);
    }

    @PreAuthorize("@securityService.canModifyResource(#id, 'note')")
    @DeleteMapping("/{id}")
    public void deleteNote(@PathVariable Long id) {
        noteService.deleteNote(id);
    }
    @GetMapping("/{id}")
    public Note getNoteById(@PathVariable Long id) {
        log.info("Fetching note with ID: {}", id);
        return noteService.getNoteById(id);
    }


    @PreAuthorize("@securityService.canModifyResource(#noteId, 'note')")
    @PutMapping("/{noteId}")
    public Note updateNote(@PathVariable Long noteId, @RequestBody Note note) {
        log.info("Updating note with ID: {}", noteId);
        return noteService.updateNoteById(noteId, note);
    }


    //------------------------------------------------

    @GetMapping("/user")
    public List<NoteDTO> getCurrentUserNotes() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        return noteService.getNotesByUserId(user.getId()).stream()
                .map(noteService::convertToDTO)
                .toList();
    }


}
