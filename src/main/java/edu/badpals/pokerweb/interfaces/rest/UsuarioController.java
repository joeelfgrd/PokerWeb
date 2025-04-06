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

    @GetMapping("/usuario")
    public ResponseEntity<UsuarioLogueadoDTO> getUsuario(@RequestParam String email) {
        UsuarioLogueadoDTO usuario = usuarioService.getUsuarioResponseByEmail(email);
        return ResponseEntity.ok(usuario);
    }

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
        UsuarioLogueadoDTO usuario = usuarioService.login(dto);
        return ResponseEntity.ok(usuario);
    }

}