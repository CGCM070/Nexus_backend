package nexus_backend.exception;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(Long id) {
        super("User not found whit id : " + id );
    }
}
