package edu.badpals.pokerweb.infraestructure.controller;

import edu.badpals.pokerweb.infraestructure.dtos.RegistroUsuarioDTO;
import edu.badpals.pokerweb.infraestructure.dtos.LoginDTO;
import edu.badpals.pokerweb.infraestructure.dtos.UsuarioLogueadoDTO;
import edu.badpals.pokerweb.application.service.UsuarioService;
import edu.badpals.pokerweb.application.service.UsuarioService.LoginStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @Operation(summary = "Registro de usuario", description = "Registra un nuevo usuario en la plataforma")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario registrado correctamente"),
            @ApiResponse(responseCode = "400", description = "Error en el registro")
    })
    @PostMapping("/registro")
    public ResponseEntity<UsuarioLogueadoDTO> registrar(@RequestBody RegistroUsuarioDTO dto) {
        try {
            UsuarioLogueadoDTO usuarioRegistrado = usuarioService.registrar(dto);
            return ResponseEntity.ok(usuarioRegistrado);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "Login de usuario", description = "Autentica a un usuario con email y contraseña")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login exitoso"),
            @ApiResponse(responseCode = "401", description = "Contraseña incorrecta"),
            @ApiResponse(responseCode = "403", description = "Usuario bloqueado"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PostMapping("/login")
    public ResponseEntity<UsuarioLogueadoDTO> login(@RequestBody LoginDTO dto) {
        LoginStatus status = usuarioService.login(dto);

        if (status == LoginStatus.LOGIN_OK) {
            UsuarioLogueadoDTO usuario = usuarioService.getUsuarioResponseByEmail(dto.getEmail());
            return ResponseEntity.ok(usuario);
        } else if (status == LoginStatus.USER_NOT_FOUND) {
            return ResponseEntity.status(404).body(null);
        } else if (status == LoginStatus.ERROR_PASSWORD) {
            return ResponseEntity.status(401).body(null);
        } else if (status == LoginStatus.USER_BLOCKED) {
            return ResponseEntity.status(403).body(null);
        } else {
            return ResponseEntity.status(500).build();
        }
    }
}
