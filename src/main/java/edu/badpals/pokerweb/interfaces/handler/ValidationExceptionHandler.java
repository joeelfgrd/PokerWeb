package edu.badpals.pokerweb.interfaces.handler;

import edu.badpals.pokerweb.application.dtos.ResponseErrorDTO;
import edu.badpals.pokerweb.domain.exceptions.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@RestControllerAdvice
public class ValidationExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseErrorDTO> handleValidationErrors(MethodArgumentNotValidException ex) {
        String errores = ex.getBindingResult().getFieldErrors().stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .collect(Collectors.joining("; "));
        return ResponseEntity.badRequest().body(new ResponseErrorDTO("VALIDATION_ERROR", errores, 400));
    }

    @ExceptionHandler(UsuarioNoEncontradoException.class)
    public ResponseEntity<ResponseErrorDTO> handleUsuarioNoEncontrado(UsuarioNoEncontradoException ex) {
        return ResponseEntity.status(404).body(new ResponseErrorDTO("NOT_FOUND", ex.getMessage(), 404));
    }

    @ExceptionHandler(UsuarioYaExisteException.class)
    public ResponseEntity<ResponseErrorDTO> handleUsuarioYaExiste(UsuarioYaExisteException ex) {
        return ResponseEntity.status(409).body(new ResponseErrorDTO("CONFLICT", ex.getMessage(), 409));
    }

    @ExceptionHandler(PasswordIncorrectaException.class)
    public ResponseEntity<ResponseErrorDTO> handlePasswordIncorrecta(PasswordIncorrectaException ex) {
        return ResponseEntity.status(401).body(new ResponseErrorDTO("UNAUTHORIZED", ex.getMessage(), 401));
    }

    @ExceptionHandler(UsuarioBloqueadoException.class)
    public ResponseEntity<ResponseErrorDTO> handleUsuarioBloqueado(UsuarioBloqueadoException ex) {
        return ResponseEntity.status(403).body(new ResponseErrorDTO("FORBIDDEN", ex.getMessage(), 403));
    }

    @ExceptionHandler(PartidaNoEncontradaException.class)
    public ResponseEntity<ResponseErrorDTO> handlePartidaNoEncontrada(PartidaNoEncontradaException ex) {
        return ResponseEntity.status(404).body(new ResponseErrorDTO("NOT_FOUND", ex.getMessage(), 404));
    }

    @ExceptionHandler(PartidaNoActivaException.class)
    public ResponseEntity<ResponseErrorDTO> handlePartidaNoActiva(PartidaNoActivaException ex) {
        return ResponseEntity.status(400).body(new ResponseErrorDTO("BAD_REQUEST", ex.getMessage(), 400));
    }

    @ExceptionHandler(PartidaSinJugadoresException.class)
    public ResponseEntity<ResponseErrorDTO> handlePartidaSinJugadores(PartidaSinJugadoresException ex) {
        return ResponseEntity.status(400).body(new ResponseErrorDTO("BAD_REQUEST", ex.getMessage(), 400));
    }

    @ExceptionHandler(PartidaFinalizadaException.class)
    public ResponseEntity<ResponseErrorDTO> handlePartidaFinalizada(PartidaFinalizadaException ex) {
        return ResponseEntity.status(400).body(new ResponseErrorDTO("BAD_REQUEST", ex.getMessage(), 400));
    }

    @ExceptionHandler(JugadorYaUnidoException.class)
    public ResponseEntity<ResponseErrorDTO> handleJugadorYaUnido(JugadorYaUnidoException ex) {
        return ResponseEntity.status(400).body(new ResponseErrorDTO("BAD_REQUEST", ex.getMessage(), 400));
    }

    @ExceptionHandler(MaximoJugadoresException.class)
    public ResponseEntity<ResponseErrorDTO> handleMaximoJugadores(MaximoJugadoresException ex) {
        return ResponseEntity.status(400).body(new ResponseErrorDTO("BAD_REQUEST", ex.getMessage(), 400));
    }

    @ExceptionHandler(FaseIncorrectaException.class)
    public ResponseEntity<ResponseErrorDTO> handleFaseIncorrecta(FaseIncorrectaException ex) {
        return ResponseEntity.status(400).body(new ResponseErrorDTO("BAD_REQUEST", ex.getMessage(), 400));
    }

    @ExceptionHandler(GanadorIndeterminadoException.class)
    public ResponseEntity<ResponseErrorDTO> handleGanadorIndeterminado(GanadorIndeterminadoException ex) {
        return ResponseEntity.status(400).body(new ResponseErrorDTO("BAD_REQUEST", ex.getMessage(), 400));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ResponseErrorDTO> handleErrorInesperado(RuntimeException ex) {
        return ResponseEntity.status(500).body(new ResponseErrorDTO("INTERNAL_ERROR", ex.getMessage(), 500));
    }
}
