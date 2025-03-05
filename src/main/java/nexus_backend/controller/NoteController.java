package nexus_backend.controller;

import lombok.extern.slf4j.Slf4j;
import nexus_backend.domain.Note;
import nexus_backend.service.NoteService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/v1/api/notes")
public class NoteController {

    private final NoteService noteService;

    public NoteController(NoteService noteService) {
        this.noteService = noteService;
    }

    @GetMapping("")
    public List<Note> getAllNotes() {
        log.info("Fetching all notes");
        return noteService.getAllNotes();
    }

    @GetMapping("/{id}")
    public Note getNoteById(@PathVariable Long id) {
        log.info("Fetching note with ID: {}", id);
        return noteService.getNoteById(id);
    }

    @PostMapping("/user/{userId}/channel/{channelId}")
    public Note createNoteForUser(@PathVariable Long userId, @PathVariable Long channelId, @RequestBody Note note) {
        log.info("Creating note for user with ID: {} in channel with ID: {}", userId, channelId);
        return noteService.createNoteForUser(userId, channelId, note);
    }

    @PostMapping("")
    public Note createNote(@RequestBody Note note) {
        log.info("Creating new note");
        return noteService.createNote(note);
    }


    @DeleteMapping("/{id}")
    public void deleteNote(@PathVariable Long id) {
        log.info("Deleting note with ID: {}", id);
        noteService.deleteNote(id);
    }

    @GetMapping("/user/{userId}")
    public List<Note> getNotesByUserId(@PathVariable Long userId) {
        log.info("Fetching notes for user with ID: {}", userId);
        return noteService.getNotesByUserId(userId);
    }


    @PutMapping("/{noteId}")
    public Note updateNote(@PathVariable Long noteId, @RequestBody Note note) {
        log.info("Updating note with ID: {}", noteId);
        return noteService.updateNoteById(noteId, note);
    }


    @GetMapping("/channel/{channelId}")
    public List<Note> getNotesByChannel(@PathVariable Long channelId) {
        log.info("Fetching notes for channel with ID: {}", channelId);
        return noteService.getNotesByChannel(channelId);
    }
}
