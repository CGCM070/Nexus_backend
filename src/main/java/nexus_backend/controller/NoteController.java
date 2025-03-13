package nexus_backend.controller;

import lombok.extern.slf4j.Slf4j;
import nexus_backend.domain.Note;
import nexus_backend.service.NoteService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    @PostMapping("/user/{userId}/channel/{channelId}")
    public Note createNoteForUser(@PathVariable Long userId, @PathVariable Long channelId, @RequestBody Note note) {
        log.info("Creating note for user with ID: {} in channel with ID: {}", userId, channelId);
        return noteService.createNoteForUser(userId, channelId, note);
    }

    @GetMapping("/channel/{channelId}")
    public List<Note> getNotesByChannel(@PathVariable Long channelId) {
        log.info("Fetching notes for channel with ID: {}", channelId);
        return noteService.getNotesByChannel(channelId);
    }

    @DeleteMapping("/{id}")
    public void deleteNote(@PathVariable Long id) {
        log.info("Deleting note with ID: {}", id);
        noteService.deleteNote(id);
    }

    @GetMapping("/{id}")
    public Note getNoteById(@PathVariable Long id) {
        log.info("Fetching note with ID: {}", id);
        return noteService.getNoteById(id);
    }


    @PutMapping("/{noteId}")
    public Note updateNote(@PathVariable Long noteId, @RequestBody Note note) {
        log.info("Updating note with ID: {}", noteId);
        return noteService.updateNoteById(noteId, note);
    }



}
