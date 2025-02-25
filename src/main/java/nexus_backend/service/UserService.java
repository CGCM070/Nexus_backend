package nexus_backend.service;

import jakarta.transaction.Transactional;
import nexus_backend.domain.User;
import nexus_backend.exception.EntityNotFoundException;
import nexus_backend.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Transactional
    public User createUser(User user) {
        return userRepository.save(user);
    }

    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(id, "User"));
    }

    public User updateUser(Long id, User user) {
        return userRepository.findById(id).map(u -> (id.equals(user.getId()) ? userRepository.save(user) : null))
                .orElseThrow(() -> new EntityNotFoundException(id, "User"));
    }

    public void deleteUser(Long id) {
        userRepository.findById(id).map(u -> {
            userRepository.delete(u);
            return u;
        }).orElseThrow(() -> new EntityNotFoundException(id, "User"));
    }
}
