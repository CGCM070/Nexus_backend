package nexus_backend.service;


import jakarta.transaction.Transactional;
import nexus_backend.domain.Channel;
import nexus_backend.domain.Note;
import nexus_backend.domain.Server;
import nexus_backend.domain.User;
import nexus_backend.repository.ChannelRepository;
import nexus_backend.repository.ServerRepository;
import nexus_backend.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;

@Service
public class UserRegistrationService {

    private final UserRepository userRepository;
    private final ServerRepository serverRepository;
    private final ChannelRepository channelRepository;


    public UserRegistrationService(UserRepository userRepository, ServerRepository serverRepository, ChannelRepository channelRepository) {
        this.userRepository = userRepository;
        this.serverRepository = serverRepository;
        this.channelRepository = channelRepository;
    }

    @Transactional
    public User registerUser(User newUser) {
        // 1. Registrar al nuevo usuario
        userRepository.save(newUser);

        // Crear servidor personal
        Server personalDashboard = Server.builder()
                .name("Dashboard Personal de " + newUser.getUsername())
                .description("Tu espacio personal en Nexus")
                .createdAt(new Timestamp(System.currentTimeMillis()))
                .user(newUser)
                .build();
        serverRepository.save(personalDashboard);


        // Crear canal de bienvenida
        Channel welcomeChannel = Channel.builder()
                .name("Bienvenida")
                .description("Canal de bienvenida a tu espacio personal")
                .server(personalDashboard)
                .createdAt(new Timestamp(System.currentTimeMillis()))
                .build();
        channelRepository.save(welcomeChannel);

        // Aui debemos crear el mensaje de bienvenida y asignarlo al canal de bienvenida
//        Message welcomeMessage = Message.builder()
//                .content("¡Bienvenido !  " + newUser.getUsername())
//                .user(newUser)
//                .channel(welcomeChannel)
//                .createdAt(new Timestamp(System.currentTimeMillis()))
//                .build();

        //Nota de bienvenida
        Note welcomeNote = Note.builder()
                .title("Bienvenido")
                .content("¡ Tamos Ready ? !")
                .user(newUser)
                .createdAt(new Timestamp(System.currentTimeMillis()))
                .build();


        // asignamos mensaje de bienvenida al canal de bienvenida
        // ( se puede hacer de esta manera o de la manera que se hizo en el test)
        // lo vamos a mejorar luego
        // de hecho no funciona asi, pq no tenemos ni el service para persistir el mensaje
        // ni el repository para hacerlo de manera directa ()

        //  welcomeChannel.getMessages().add(welcomeMessage);

        //Asignar la nota al canal de bienvenida
        welcomeNote.setChannel(welcomeChannel);
        //Guardar la nota del lado del usuario que posee cascada
        newUser.getNotes().add(welcomeNote);

        return newUser;
    }
}
