package nexus_backend.util;


import jakarta.transaction.Transactional;
import nexus_backend.domain.Note;
import nexus_backend.domain.User;
import nexus_backend.repository.NoteRepository;
import nexus_backend.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Timestamp;

@Component
public class DataLoader implements CommandLineRunner {


    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NoteRepository noteRepository;

    private static final Logger logger = LoggerFactory.getLogger(DataLoader.class);



    @Override
    public void run(String... args) throws Exception {
        logger.info("Starting DataLogger...");
        createUsersAndNotes();
        logger.info("DataLogger finished.");
    }

    @Transactional
    void createUsersAndNotes() {

            // Crear usuario 1 con 2 notas
            User user1 = User.builder()
                    .username("Usuario1")
                    .email("user1@example.com")
                    .passwordHash("password1")
                    .fullName("Nombre Usuario1")
                    .createdAt(new Timestamp(System.currentTimeMillis()))
                    .updatedAt(new Timestamp(System.currentTimeMillis()))
                    .build();

            Note note1 = Note.builder()
                    .title("Nota1 de Usuario1")
                    .content("Contenido de nota1 de Usuario1")
                    .createdAt(new Timestamp(System.currentTimeMillis()))
                    .updatedAt(new Timestamp(System.currentTimeMillis()))
                    .user(user1)
                    .build();

            Note note2 = Note.builder()
                    .title("Nota2 de Usuario1")
                    .content("Contenido de nota2 de Usuario1")
                    .createdAt(new Timestamp(System.currentTimeMillis()))
                    .updatedAt(new Timestamp(System.currentTimeMillis()))
                    .user(user1)
                    .build();

            user1.getNotes().add(note1);
            user1.getNotes().add(note2);
            userRepository.save(user1);
            logger.info("Created user Usuario1 with 2 notes.");

            // Crear usuario 2 con 2 notas
            User user2 = User.builder()
                    .username("Usuario2")
                    .email("user2@example.com")
                    .passwordHash("password2")
                    .fullName("Nombre Usuario2")
                    .createdAt(new Timestamp(System.currentTimeMillis()))
                    .updatedAt(new Timestamp(System.currentTimeMillis()))
                    .build();

            Note note3 = Note.builder()
                    .title("Nota1 de Usuario2")
                    .content("Contenido de nota1 de Usuario2")
                    .createdAt(new Timestamp(System.currentTimeMillis()))
                    .updatedAt(new Timestamp(System.currentTimeMillis()))
                    .user(user2)
                    .build();

            Note note4 = Note.builder()
                    .title("Nota2 de Usuario2")
                    .content("Contenido de nota2 de Usuario2")
                    .createdAt(new Timestamp(System.currentTimeMillis()))
                    .updatedAt(new Timestamp(System.currentTimeMillis()))
                    .user(user2)
                    .build();

            user2.getNotes().add(note3);
            user2.getNotes().add(note4);
            userRepository.save(user2);
            logger.info("Created user Usuario2 with 2 notes.");

            // Crear usuario 3 con 1 nota
            User user3 = User.builder()
                    .username("Usuario3")
                    .email("user3@example.com")
                    .passwordHash("password3")
                    .fullName("Nombre Usuario3")
                    .createdAt(new Timestamp(System.currentTimeMillis()))
                    .updatedAt(new Timestamp(System.currentTimeMillis()))
                    .build();

            Note note5 = Note.builder()
                    .title("Nota de Usuario3")
                    .content("Contenido de nota de Usuario3")
                    .createdAt(new Timestamp(System.currentTimeMillis()))
                    .updatedAt(new Timestamp(System.currentTimeMillis()))
                    .user(user3)
                    .build();

            user3.getNotes().add(note5);
            userRepository.save(user3);
            logger.info("Created user Usuario3 with 1 note.");

            // Crear usuario 4 sin nota
            User user4 = User.builder()
                    .username("Usuario4")
                    .email("user4@example.com")
                    .passwordHash("password4")
                    .fullName("Nombre Usuario4")
                    .createdAt(new Timestamp(System.currentTimeMillis()))
                    .updatedAt(new Timestamp(System.currentTimeMillis()))
                    .build();

            userRepository.save(user4);
            logger.info("Created user Usuario4 with no notes.");

    }
}
