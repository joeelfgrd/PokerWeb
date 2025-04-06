package edu.badpals.pokerweb.interfaces.handler;

import edu.badpals.pokerweb.domain.exceptions.PasswordIncorrectaException;
import edu.badpals.pokerweb.domain.exceptions.UsuarioBloqueadoException;
import edu.badpals.pokerweb.domain.exceptions.UsuarioNoEncontradoException;
import edu.badpals.pokerweb.domain.exceptions.UsuarioYaExisteException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class ValidationExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage()));
        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleErrorInesperado(RuntimeException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", "Internal Server Error");
        error.put("detalle", ex.getMessage());
        return ResponseEntity.status(500).body(error);
    }

    @ExceptionHandler(UsuarioNoEncontradoException.class)
    public ResponseEntity<Map<String, String>> handleUsuarioNoEncontrado(UsuarioNoEncontradoException ex) {
        return ResponseEntity.status(404).body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(UsuarioYaExisteException.class)
    public ResponseEntity<Map<String, String>> handleUsuarioYaExiste(UsuarioYaExisteException ex) {
        return ResponseEntity.status(409).body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(PasswordIncorrectaException.class)
    public ResponseEntity<Map<String, String>> handlePasswordIncorrecta(PasswordIncorrectaException ex) {
        return ResponseEntity.status(401).body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(UsuarioBloqueadoException.class)
    public ResponseEntity<Map<String, String>> handleUsuarioBloqueado(UsuarioBloqueadoException ex) {
        return ResponseEntity.status(403).body(Map.of("error", ex.getMessage()));
    }

}

