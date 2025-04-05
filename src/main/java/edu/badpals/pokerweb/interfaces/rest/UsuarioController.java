package edu.badpals.pokerweb.interfaces.rest;

import edu.badpals.pokerweb.application.dtos.RegistroUsuarioDTO;
import edu.badpals.pokerweb.application.dtos.LoginDTO;
import edu.badpals.pokerweb.application.dtos.UsuarioLogueadoDTO;
import edu.badpals.pokerweb.application.service.UsuarioService;
import edu.badpals.pokerweb.application.service.UsuarioService.LoginStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @PostMapping("/registro")
    public ResponseEntity<UsuarioLogueadoDTO> registrar(@RequestBody RegistroUsuarioDTO dto) {
        try {
            UsuarioLogueadoDTO usuarioRegistrado = usuarioService.registrar(dto);
            return ResponseEntity.ok(usuarioRegistrado);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

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