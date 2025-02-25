package nexus_backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class EntityNotFoundException extends ResponseStatusException {
    public EntityNotFoundException(Long id, String entityName) {
        super(HttpStatus.NOT_FOUND, "Not found Entity " + entityName + " with id: " + id);
    }
}