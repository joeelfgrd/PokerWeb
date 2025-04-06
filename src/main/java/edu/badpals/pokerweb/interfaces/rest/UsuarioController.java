package edu.badpals.pokerweb.interfaces.rest;

import edu.badpals.pokerweb.application.dtos.RegistroUsuarioDTO;
import edu.badpals.pokerweb.application.dtos.LoginDTO;
import edu.badpals.pokerweb.application.dtos.UsuarioLogueadoDTO;
import edu.badpals.pokerweb.application.service.UsuarioService;
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
        return ResponseEntity.ok(usuarioService.getUsuarioResponseByEmail(email));
    }

    @PostMapping("/registro")
    public ResponseEntity<UsuarioLogueadoDTO> registrar(@RequestBody RegistroUsuarioDTO dto) {
        return ResponseEntity.ok(usuarioService.registrar(dto));
    }

    @PostMapping("/login")
    public ResponseEntity<UsuarioLogueadoDTO> login(@RequestBody LoginDTO dto) {
        return ResponseEntity.ok(usuarioService.login(dto));
    }

}